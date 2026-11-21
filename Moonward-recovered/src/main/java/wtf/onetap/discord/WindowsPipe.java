package wtf.onetap.discord;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Tlhelp32.PROCESSENTRY32;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.TOKEN_ELEVATION;
import com.sun.jna.ptr.IntByReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WindowsPipe implements DiscordIpc.CallbackType {
   private final HANDLE hANDLE;
   private final String text;

   private WindowsPipe(HANDLE hANDLE2, String text2) {
      this.hANDLE = hANDLE2;
      this.text = text2;
   }

   public static WindowsPipe m_1_C6u() throws wtf.onetap.discord.DiscordIpc.StateHolder2 {
      Kernel32 kernel32 = Kernel32.INSTANCE;
      int value = -1;
      int value2 = -1;
      ArrayList list = new ArrayList();

      for (int i = 0; i < 10; i++) {
         String text2 = "\\\\.\\pipe\\discord-ipc-" + i;
         HANDLE hANDLE2 = kernel32.CreateFile(text2, (int)-8853979523050373120L, 0, null, 3, 0, null);
         if (!isInvalid(hANDLE2)) {
            return new WindowsPipe(hANDLE2, text2);
         }

         int value3 = Native.getLastError();
         switch (value3) {
            case 2:
               break;
            case 5:
               if (value < 0) {
                  value = i;
               }
               break;
            case 231:
               if (value2 < 0) {
                  value2 = i;
               }
               break;
            default:
               list.add(text2 + ": ошибка Win32 " + value3);
         }
      }

      if (value >= 0) {
         throw new DiscordIpc.StateHolder2(m_8_String("\\\\.\\pipe\\discord-ipc-" + value));
      } else if (value2 >= 0) {
         throw new DiscordIpc.StateHolder2("Discord занят: все экземпляры \\\\.\\pipe\\discord-ipc-" + value2 + " заняты другими программами (ERROR_PIPE_BUSY), повторю позже");
      } else if (!list.isEmpty()) {
         throw new DiscordIpc.StateHolder2("не удалось открыть пайп Discord: " + String.join("; ", list));
      } else {
         throw new DiscordIpc.StateHolder2("Discord не запущен: нет пайпа \\\\.\\pipe\\discord-ipc-0..9");
      }
   }

   @Override
   public int read(byte[] byteArray) throws java.io.IOException {
      IntByReference intByReference = new IntByReference();
      if (!Kernel32.INSTANCE.PeekNamedPipe(this.hANDLE, null, 0, null, intByReference, null)) {
         throw new IOException("пайп закрыт (ошибка Win32 " + Native.getLastError() + ")");
      } else {
         int value = Math.min(intByReference.getValue(), byteArray.length);
         if (value <= 0) {
            return 0;
         } else {
            IntByReference intByReference2 = new IntByReference();
            if (!Kernel32.INSTANCE.ReadFile(this.hANDLE, byteArray, value, intByReference2, null)) {
               throw new IOException("чтение из пайпа (ошибка Win32 " + Native.getLastError() + ")");
            } else {
               return intByReference2.getValue();
            }
         }
      }
   }

   @Override
   public void write(byte[] byteArray) throws java.io.IOException {
      IntByReference intByReference = new IntByReference();
      if (!Kernel32.INSTANCE.WriteFile(this.hANDLE, byteArray, byteArray.length, intByReference, null) || intByReference.getValue() != byteArray.length) {
         throw new IOException("запись в пайп (ошибка Win32 " + Native.getLastError() + ")");
      }
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   public void close() {
      Kernel32.INSTANCE.CloseHandle(this.hANDLE);
   }

   private static String m_8_String(String text2) {
      DiscordPresence.m_1_String();
      String text3 = "Windows запретил игре доступ к " + text2 + " (ERROR_ACCESS_DENIED). ";
      ArrayList list = new ArrayList();
      ArrayList list2 = new ArrayList();
      ArrayList list3 = new ArrayList();

      Boolean bool;
      try {
         bool = Advapi32Util.isCurrentProcessElevated();

         for (WindowsPipe.DataRecord c3 : m_0_List()) {
            Boolean bool2 = m_5_Boolean(c3.f_3l);
            ArrayList list4 = bool2 == null ? list3 : (bool2 ? list : list2);
            if (!list4.contains(c3.f_7t)) {
               list4.add(c3.f_7t);
            }
         }
      } catch (Throwable e) {
         return text3 + "Проверить права не удалось (" + e + "). Скорее всего Discord запущен от имени администратора: перезапусти его обычным способом.";
      }

      String text4 = bool ? "игра запущена от администратора" : "игра запущена без прав администратора";
      if (!bool && (!list.isEmpty() || !list3.isEmpty()) && list2.isEmpty()) {
         ArrayList list5 = new ArrayList(list);
         list5.addAll(list3);
         return text3
            + "Discord запущен от имени администратора ("
            + String.join(", ", list5)
            + "), а "
            + text4
            + ". Закрой Discord полностью (в трее тоже) и запусти его обычным способом, без «Запуск от имени администратора» и без галочки в свойствах ярлыка/совместимости.";
      } else {
         return list.isEmpty() && list3.isEmpty() && list2.isEmpty()
            ? text3 + "Процесс Discord не найден: пайп держит другая программа, запущенная от администратора или другим пользователем; " + text4 + "."
            : text3
               + text4
               + "; Discord: "
               + m_9(list, list2, list3)
               + ". Возможно, Discord запущен от другого пользователя Windows или его пайп держит другая программа.";
      }
   }

   private static String m_9(List<String> stringList, List<String> stringList2, List<String> stringList3) {
      ArrayList list = new ArrayList();
      if (!stringList.isEmpty()) {
         list.add("от администратора " + stringList);
      }

      if (!stringList2.isEmpty()) {
         list.add("обычный " + stringList2);
      }

      if (!stringList3.isEmpty()) {
         list.add("права не видны " + stringList3);
      }

      return String.join(", ", list);
   }

   private static List<WindowsPipe.DataRecord> m_0_List() {
      ArrayList list = new ArrayList();
      Kernel32 kernel32 = Kernel32.INSTANCE;
      HANDLE hANDLE2 = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new DWORD(0L));
      if (isInvalid(hANDLE2)) {
         return list;
      }

      try {
         PROCESSENTRY32 pROCESSENTRY32 = new PROCESSENTRY32();

         for (boolean flag = kernel32.Process32First(hANDLE2, pROCESSENTRY32); flag; flag = kernel32.Process32Next(hANDLE2, pROCESSENTRY32)) {
            String text2 = Native.toString(pROCESSENTRY32.szExeFile);
            String text3 = text2.toLowerCase(Locale.ROOT);
            if (text3.startsWith("discord") || text3.equals("vesktop.exe") || text3.equals("legcord.exe")) {
               list.add(new WindowsPipe.DataRecord(pROCESSENTRY32.th32ProcessID.intValue(), text2));
            }
         }
      } finally {
         kernel32.CloseHandle(hANDLE2);
      }

      return list;
   }

   private static Boolean m_5_Boolean(int value) {
      Kernel32 kernel32 = Kernel32.INSTANCE;
      HANDLE hANDLE2 = kernel32.OpenProcess(4096, false, value);
      if (hANDLE2 == null) {
         return null;
      }

      try {
         HANDLEByReference hANDLEByReference = new HANDLEByReference();
         if (!Advapi32.INSTANCE.OpenProcessToken(hANDLE2, 8, hANDLEByReference)) {
            return Native.getLastError() == 5 ? Boolean.TRUE : null;
         }

         try {
            TOKEN_ELEVATION tOKEN_ELEVATION = new TOKEN_ELEVATION();
            IntByReference intByReference = new IntByReference();
            return !Advapi32.INSTANCE.GetTokenInformation(hANDLEByReference.getValue(), 20, tOKEN_ELEVATION, tOKEN_ELEVATION.size(), intByReference) ? null : tOKEN_ELEVATION.TokenIsElevated != 0;
         } finally {
            kernel32.CloseHandle(hANDLEByReference.getValue());
         }
      } finally {
         kernel32.CloseHandle(hANDLE2);
      }
   }

   private static boolean isInvalid(HANDLE hANDLE2) {
      return hANDLE2 == null || WinBase.INVALID_HANDLE_VALUE.equals(hANDLE2);
   }

   private record DataRecord(int f_3l, String f_7t) {
      public int m_3l() {
         return this.f_3l;
      }

      public String m_7t() {
         return this.f_7t;
      }
   }
}
