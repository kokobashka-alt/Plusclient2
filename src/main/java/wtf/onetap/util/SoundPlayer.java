package wtf.onetap.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl.Type;
import javax.sound.sampled.FloatControl;

public class SoundPlayer {
   private SoundPlayer() {
   }

   public static void play(String text) {
      m_7(text, 1.0F);
   }

   public static void m_7(String text, float floatValue) {
      if (!(floatValue <= 0.0F)) {
         try {
            InputStream inputStream = SoundPlayer.class.getResourceAsStream("/assets/onetap/sounds/" + text + ".wav");
            if (inputStream == null) {
               return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            if (floatValue < 1.0F && clip.isControlSupported(Type.MASTER_GAIN)) {
               FloatControl floatControl = (FloatControl)clip.getControl(Type.MASTER_GAIN);
               floatControl.setValue(Math.max(floatControl.getMinimum(), floatControl.getMaximum() + (float)(20.0 * Math.log10(floatValue))));
            }

            clip.addLineListener(event -> {
               if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                  clip.close();
               }
            });
            clip.start();
         } catch (Exception e) {
         }
      }
   }
}
