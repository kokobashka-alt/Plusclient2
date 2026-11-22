package wtf.onetap.combat.rotation.holyworld;

import net.fabricmc.loader.api.FabricLoader;
import wtf.onetap.Onetap;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Owns everything the HolyWorld rotation needs: the recorded dataset, the live model, the recorder and the
 * background trainer. Training runs on its own daemon thread on a copy of the model and publishes the result by
 * swapping a volatile reference, so the render and tick threads never wait for it.
 */
public final class HolyWorldBrain implements MinecraftAccess {
    public static final HolyWorldBrain INSTANCE = new HolyWorldBrain();

    public static final int HIDDEN = 24;
    public static final int MIN_TRAINING_TICKS = 400;

    private final Dataset dataset = new Dataset();
    private final HolyWorldRecorder recorder = new HolyWorldRecorder(this);
    private final AtomicBoolean stopTraining = new AtomicBoolean();
    private volatile GruNet live;
    private volatile boolean recording;
    private volatile boolean training;
    private volatile String trainingStatus = "не запущено";
    private boolean started;

    private HolyWorldBrain() {
    }

    /** Registers the recorder and loads saved files; safe to call more than once. */
    public synchronized void init() {
        if (started) {
            return;
        }
        started = true;
        Onetap.EVENT_BUS.register(recorder);
        File dir = directory();
        try {
            if (new File(dir, "dataset.bin").isFile()) {
                try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(new File(dir, "dataset.bin").toPath())))) {
                    dataset.read(in);
                }
            }
        } catch (IOException e) {
            dataset.clear();
        }
        try {
            if (new File(dir, "model.bin").isFile()) {
                try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(new File(dir, "model.bin").toPath())))) {
                    live = GruNet.read(in);
                }
            }
        } catch (IOException e) {
            live = null;
        }
    }

    public File directory() {
        File dir = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("holyworld").toFile();
        dir.mkdirs();
        return dir;
    }

    public Dataset dataset() {
        return dataset;
    }

    /** The trained model, or null if none has been trained or loaded yet. */
    public GruNet model() {
        return live;
    }

    public boolean isRecording() {
        return recording;
    }

    public boolean isTraining() {
        return training;
    }

    public void setRecording(boolean on) {
        if (!on && recording) {
            recorder.flush();
        }
        recording = on;
    }

    public void tell(String text) {
        MINECRAFT.execute(() -> ChatUtil.log("§d[HolyWorld] §7" + text));
    }

    // ------------------------------------------------------------------------------------------ training

    /** Starts training in the background; returns an explanation if it cannot start, otherwise null. */
    public synchronized String startTraining(int epochs) {
        if (training) {
            return "Обучение уже идёт: " + trainingStatus;
        }
        recorder.flush();
        List<Dataset.Episode> episodes = dataset.snapshot();
        int ticks = dataset.totalTicks();
        if (ticks < MIN_TRAINING_TICKS) {
            return "Мало данных: " + ticks + " тиков, нужно минимум " + MIN_TRAINING_TICKS
                    + " (около 20 секунд ведения цели). Включи запись: .holyworld record on";
        }

        GruNet base = live;
        GruNet work;
        if (base != null && base.hidden == HIDDEN) {
            work = base.copy();
        } else {
            work = new GruNet(HIDDEN);
            work.randomize(new Random(System.nanoTime()));
        }
        GruTrainer.Config cfg = new GruTrainer.Config();
        cfg.epochs = epochs;
        cfg.seed = System.nanoTime();
        stopTraining.set(false);
        training = true;
        trainingStatus = "старт";

        Thread thread = new Thread(() -> runTraining(work, episodes, cfg, ticks), "holyworld-trainer");
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
        return null;
    }

    private void runTraining(GruNet work, List<Dataset.Episode> episodes, GruTrainer.Config cfg, int ticks) {
        try {
            tell("Обучение: " + episodes.size() + " эпизодов, " + ticks + " тиков, GRU " + HIDDEN + " нейронов, " + cfg.epochs + " эпох.");
            GruTrainer.Result result = GruTrainer.train(work, episodes, cfg, (epoch, total, trainLoss, valLoss, bestLoss) -> {
                trainingStatus = String.format("эпоха %d/%d, ошибка %.5f", epoch, total, Double.isNaN(valLoss) ? trainLoss : valLoss);
                if (epoch == 1 || epoch % 10 == 0 || epoch == total) {
                    tell(trainingStatus);
                }
            }, stopTraining);

            if (result.epochsRun == 0) {
                tell("Обучение остановлено до первой эпохи, модель не изменена.");
                return;
            }
            live = work;
            save();
            double ratio = result.baselineLoss > 0 ? result.bestLoss / result.baselineLoss : Double.NaN;
            String quality;
            if (Double.isNaN(ratio)) {
                quality = "качество не оценено";
            } else if (ratio < 0.5) {
                quality = String.format("модель объясняет большую часть движений (ошибка %.0f%% от базовой)", ratio * 100);
            } else if (ratio < 0.85) {
                quality = String.format("модель слабо лучше среднего (ошибка %.0f%% от базовой), запиши больше данных", ratio * 100);
            } else {
                quality = String.format("модель почти не лучше среднего (ошибка %.0f%% от базовой), данных мало или ведение хаотичное", ratio * 100);
            }
            tell((result.stopped ? "Остановлено, сохранена лучшая модель. " : "Готово. ")
                    + (result.usedValidation ? "Оценка на отложенных эпизодах: " : "Оценка на обучающих данных (мало эпизодов для отложенной выборки): ")
                    + quality + ".");
        } catch (RuntimeException e) {
            tell("Ошибка обучения: " + e);
        } finally {
            trainingStatus = "не запущено";
            training = false;
        }
    }

    public void stopTraining() {
        stopTraining.set(true);
    }

    // ------------------------------------------------------------------------------------------ files and state

    public synchronized String save() {
        File dir = directory();
        try {
            GruNet model = live;
            if (model != null) {
                writeAtomically(new File(dir, "model.bin").toPath(), out -> model.write(out));
            }
            writeAtomically(new File(dir, "dataset.bin").toPath(), dataset::write);
            return null;
        } catch (IOException e) {
            return "Не удалось сохранить: " + e.getMessage();
        }
    }

    public synchronized String load() {
        File dir = directory();
        File modelFile = new File(dir, "model.bin");
        File datasetFile = new File(dir, "dataset.bin");
        if (!modelFile.isFile() && !datasetFile.isFile()) {
            return "В папке " + dir.getAbsolutePath() + " нет сохранённых файлов.";
        }
        try {
            if (modelFile.isFile()) {
                try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(modelFile.toPath())))) {
                    live = GruNet.read(in);
                }
            }
            if (datasetFile.isFile()) {
                try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(datasetFile.toPath())))) {
                    dataset.read(in);
                }
            }
            return null;
        } catch (IOException e) {
            return "Не удалось загрузить: " + e.getMessage();
        }
    }

    public void resetModel() {
        live = null;
        File file = new File(directory(), "model.bin");
        file.delete();
    }

    public void clearData() {
        recorder.flush();
        dataset.clear();
        new File(directory(), "dataset.bin").delete();
    }

    public String status() {
        GruNet model = live;
        StringBuilder sb = new StringBuilder();
        sb.append("Запись: ").append(recording ? "включена" : "выключена").append(" | эпизодов ").append(dataset.episodeCount())
                .append(", тиков ").append(dataset.totalTicks()).append(" (~").append(dataset.totalTicks() / 20).append(" с)");
        sb.append("\nМодель: ");
        if (model == null) {
            sb.append("нет (в режиме HolyWorld будет простой запасной контроллер)");
        } else {
            sb.append("GRU ").append(model.hidden).append(", шагов обучения ").append(model.trainedSteps)
                    .append(String.format(", шум %.2f°/%.2f°", Features.outputToDelta(model.noiseStd[0]), Features.outputToDelta(model.noiseStd[1])));
        }
        sb.append("\nОбучение: ").append(training ? trainingStatus : "не запущено");
        return sb.toString();
    }

    private interface Writer {
        void write(DataOutputStream out) throws IOException;
    }

    private static void writeAtomically(Path target, Writer writer) throws IOException {
        Path temp = Files.createTempFile(target.getParent(), target.getFileName().toString(), ".tmp");
        try {
            try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(temp)))) {
                writer.write(out);
            }
            try {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }
}
