package wtf.onetap.command;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import wtf.onetap.combat.rotation.holyworld.HolyWorldBrain;
import wtf.onetap.util.ChatUtil;

/** Controls the in-game training of the HolyWorld neural rotation: record, train, save, load. */
public class HolyWorldCommand implements Command {
    private static final int DEFAULT_EPOCHS = 40;
    private static final int MIN_EPOCHS = 5;
    private static final int MAX_EPOCHS = 400;

    public HolyWorldCommand() {
        HolyWorldBrain.INSTANCE.init();
    }

    @Override
    public String getText2() {
        return "holyworld";
    }

    @Override
    public String getText3() {
        return "Нейро-ротация HolyWorld: запись и обучение прямо в игре";
    }

    @Override
    public String getUsage() {
        return "record on|off|train [эпохи]|stop|status|save|load|reset|clear|open";
    }

    @Override
    public void showHelp(CommandHelp help) {
        String sub = help.getText2();
        if (sub == null) {
            status();
            return;
        }
        List<String> args = help.getStringList();
        switch (sub.toLowerCase()) {
            case "help" -> help();
            case "status" -> status();
            case "record" -> record(args);
            case "train" -> train(args);
            case "stop" -> stop();
            case "save" -> say(orDone(HolyWorldBrain.INSTANCE.save(), "Сохранено: " + HolyWorldBrain.INSTANCE.directory().getAbsolutePath()));
            case "load" -> say(orDone(HolyWorldBrain.INSTANCE.load(), "Загружено."));
            case "reset" -> reset();
            case "clear" -> {
                HolyWorldBrain.INSTANCE.clearData();
                say("Записанные данные удалены.");
            }
            case "open" -> open();
            default -> help();
        }
    }

    private static void say(String text) {
        ChatUtil.log("§d[HolyWorld] §7" + text);
    }

    private static String orDone(String problem, String done) {
        return problem == null ? done : problem;
    }

    private void help() {
        say(".holyworld record on|off — записывать, как ты ведёшь цель мышью (KillAura выключена)");
        say(".holyworld train [эпохи] — обучить GRU на записи в фоне (по умолчанию " + DEFAULT_EPOCHS + ")");
        say(".holyworld stop — остановить обучение, сохранив лучшую модель");
        say(".holyworld status | save | load | reset | clear | open");
        say("Потом в KillAura выбери ротацию «HolyWorld».");
    }

    private void status() {
        for (String line : HolyWorldBrain.INSTANCE.status().split("\n")) {
            say(line);
        }
    }

    private void record(List<String> args) {
        if (args.isEmpty()) {
            say("Использование: .holyworld record on|off");
            return;
        }
        boolean on = args.get(0).equalsIgnoreCase("on");
        if (!on && !args.get(0).equalsIgnoreCase("off")) {
            say("Использование: .holyworld record on|off");
            return;
        }
        HolyWorldBrain.INSTANCE.setRecording(on);
        if (on) {
            say("Запись включена. Води мышью по живой цели (игрок или моб до 7 блоков), KillAura должна быть выключена.");
        } else {
            say("Запись выключена. Эпизодов: " + HolyWorldBrain.INSTANCE.dataset().episodeCount()
                    + ", тиков: " + HolyWorldBrain.INSTANCE.dataset().totalTicks());
        }
    }

    private void train(List<String> args) {
        int epochs = DEFAULT_EPOCHS;
        if (!args.isEmpty()) {
            try {
                epochs = Integer.parseInt(args.get(0));
            } catch (NumberFormatException e) {
                say("Число эпох должно быть целым: " + MIN_EPOCHS + "–" + MAX_EPOCHS);
                return;
            }
            if (epochs < MIN_EPOCHS || epochs > MAX_EPOCHS) {
                say("Число эпох: " + MIN_EPOCHS + "–" + MAX_EPOCHS);
                return;
            }
        }
        String problem = HolyWorldBrain.INSTANCE.startTraining(epochs);
        if (problem != null) {
            say(problem);
        }
    }

    private void stop() {
        if (!HolyWorldBrain.INSTANCE.isTraining()) {
            say("Обучение не запущено.");
            return;
        }
        HolyWorldBrain.INSTANCE.stopTraining();
        say("Остановка запрошена, лучшая модель будет сохранена после текущей эпохи.");
    }

    private void reset() {
        if (HolyWorldBrain.INSTANCE.isTraining()) {
            say("Сначала останови обучение: .holyworld stop");
            return;
        }
        HolyWorldBrain.INSTANCE.resetModel();
        say("Модель сброшена, записанные данные оставлены.");
    }

    private void open() {
        File dir = HolyWorldBrain.INSTANCE.directory();
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(dir);
            }
            say("Папка: " + dir.getAbsolutePath());
        } catch (IOException e) {
            say("Не удалось открыть папку: " + dir.getAbsolutePath());
        }
    }
}
