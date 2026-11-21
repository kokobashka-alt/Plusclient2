package wtf.onetap.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import wtf.onetap.util.ChatUtil;

public class BlockEspCommand implements Command {
   private static final List<String> STRING_LIST = List.of("add", "remove", "list", "clear");
   private static final List<Block> BLOCK_LIST = new CopyOnWriteArrayList<>();
   private static final List<EntityType<?>> ENTITY_TYPE_LIST = new CopyOnWriteArrayList<>();

   @Override
   public String getText2() {
      return "blockesp";
   }

   @Override
   public String getText3() {
      return "Подсвечивает добавленные блоки и сущности через .blockesp";
   }

   @Override
   public String getUsage() {
      return "add <блок|сущность>|remove <блок|сущность>|list|clear";
   }

   public static List<Block> getBlocks() {
      return BLOCK_LIST;
   }

   public static List<EntityType<?>> getEntityTypes() {
      return ENTITY_TYPE_LIST;
   }

   @Override
   public void showHelp(CommandHelp commandHelp) {
      if (commandHelp.getText2() == null) {
         this.printUsage();
      } else {
         switch (commandHelp.getText2().toLowerCase(Locale.ROOT)) {
            case "add":
               this.m_9(commandHelp);
               break;
            case "remove":
               this.m_3_void(commandHelp);
               break;
            case "list":
               this.m_2_void();
               break;
            case "clear":
               ChatUtil.log("§7Количество удалённых элементов: " + (BLOCK_LIST.size() + ENTITY_TYPE_LIST.size()));
               BLOCK_LIST.clear();
               ENTITY_TYPE_LIST.clear();
               break;
            default:
               this.printUsage();
         }
      }
   }

   private void m_9(CommandHelp commandHelp) {
      var stringList = commandHelp.getStringList();
      if (stringList.isEmpty()) {
         ChatUtil.log("§cИспользование: §7.blockesp add <блок|сущность>");
      } else {
         String text = (String)stringList.get(0);
         Block block = resolveBlock(text);
         if (block != Blocks.AIR) {
            if (!BLOCK_LIST.contains(block)) {
               BLOCK_LIST.add(block);
            }

            ChatUtil.log("§aБлок §c" + getBlockId(block) + " §aуспешно добавлен");
         } else {
            EntityType entityType = parseEntityType(text);
            if (entityType == null) {
               ChatUtil.log("§cБлок или сущность §7" + text + " §cне найдены");
            } else {
               if (!ENTITY_TYPE_LIST.contains(entityType)) {
                  ENTITY_TYPE_LIST.add(entityType);
               }

               ChatUtil.log("§aСущность §c" + getEntityName(entityType) + " §aуспешно добавлена");
            }
         }
      }
   }

   private void m_3_void(CommandHelp commandHelp) {
      var stringList = commandHelp.getStringList();
      if (stringList.isEmpty()) {
         ChatUtil.log("§cИспользование: §7.blockesp remove <блок|сущность>");
      } else {
         String text = (String)stringList.get(0);
         if (BLOCK_LIST.remove(resolveBlock(text))) {
            ChatUtil.log("§aБлок §c" + text + " §aуспешно удалён");
         } else if (ENTITY_TYPE_LIST.remove(parseEntityType(text))) {
            ChatUtil.log("§aСущность §c" + text + " §aуспешно удалена");
         } else {
            ChatUtil.log("§cЭлемент с именем §7" + text + " §cотсутствует");
         }
      }
   }

   private void m_2_void() {
      if (BLOCK_LIST.isEmpty() && ENTITY_TYPE_LIST.isEmpty()) {
         ChatUtil.log("§7Список не содержит элементов");
      } else {
         ChatUtil.log("§7Список всех элементов (" + (BLOCK_LIST.size() + ENTITY_TYPE_LIST.size()) + "):");

         for (Block block : BLOCK_LIST) {
            ChatUtil.log("§7— §c" + getBlockId(block));
         }

         for (EntityType entityType : ENTITY_TYPE_LIST) {
            ChatUtil.log("§7— §c" + getEntityName(entityType));
         }
      }
   }

   private static Block resolveBlock(String text) {
      Block block = parseBlock(text);
      return block != Blocks.AIR ? block : m_8_Block(text);
   }

   private static Block parseBlock(String text) {
      try {
         Identifier identifier = Identifier.parse(text);
         return BuiltInRegistries.BLOCK.getValue(identifier);
      } catch (Exception e) {
         return Blocks.AIR;
      }
   }

   private static Block m_8_Block(String text) {
      String text2 = text.toLowerCase(Locale.ROOT).trim().replace(' ', '_');

      for (Block block : BuiltInRegistries.BLOCK) {
         if (block != Blocks.AIR && getBlockId(block).toLowerCase(Locale.ROOT).equals(text2)) {
            return block;
         }
      }

      return Blocks.AIR;
   }

   private static EntityType<?> parseEntityType(String text) {
      Identifier identifier = Identifier.tryParse(text);
      if (identifier != null) {
         EntityType entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(identifier).orElse(null);
         if (entityType != null) {
            return entityType;
         }
      }

      String text2 = text.toLowerCase(Locale.ROOT).trim().replace(' ', '_');

      for (EntityType entityType2 : BuiltInRegistries.ENTITY_TYPE) {
         if (getEntityName(entityType2).toLowerCase(Locale.ROOT).equals(text2)) {
            return entityType2;
         }
      }

      return null;
   }

   private static String getBlockName(Block block) {
      return block.getName().getString();
   }

   private static String getBlockId(Block block) {
      return getBlockName(block).replace(' ', '_');
   }

   private static String getEntityName(EntityType<?> entityType) {
      return entityType.getDescription().getString().replace(' ', '_');
   }

   private static String getBlockPath(Block block) {
      return BuiltInRegistries.BLOCK.getKey(block).getPath();
   }

   public static List<String> complete(String[] stringArray, int value, String text) {
      if (value == 1) {
         return filterByPrefix(STRING_LIST, text);
      }

      if (stringArray.length < 2) {
         return List.of();
      }

      String text2 = stringArray[1].toLowerCase(Locale.ROOT);
      if (value == 2 && text2.equals("add")) {
         return m_6_List(text);
      }

      if (value == 2 && text2.equals("remove")) {
         ArrayList list = new ArrayList();

         for (Block block : BLOCK_LIST) {
            m_4(list, getBlockId(block), getBlockPath(block), text);
         }

         for (EntityType entityType : ENTITY_TYPE_LIST) {
            m_4(list, getEntityName(entityType), BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath(), text);
         }

         return list;
      } else {
         return List.of();
      }
   }

   private static void m_4(List<String> stringList, String text, String text2, String text3) {
      String text4 = text3.toLowerCase(Locale.ROOT).replace(' ', '_');
      if (text.toLowerCase(Locale.ROOT).startsWith(text4)) {
         stringList.add(text);
      } else if (text2.startsWith(text4)) {
         stringList.add(text2);
      }
   }

   private static List<String> m_6_List(String text) {
      String text2 = text.toLowerCase(Locale.ROOT).replace(' ', '_');
      ArrayList list = new ArrayList();

      for (Block block : BuiltInRegistries.BLOCK) {
         if (block != Blocks.AIR) {
            String text3 = getBlockId(block);
            if (text3.toLowerCase(Locale.ROOT).startsWith(text2)) {
               list.add(text3);
               if (list.size() >= 20) {
                  break;
               }
            }
         }
      }

      for (EntityType entityType : BuiltInRegistries.ENTITY_TYPE) {
         if (list.size() >= 20) {
            break;
         }

         String text4 = getEntityName(entityType);
         if (text4.toLowerCase(Locale.ROOT).startsWith(text2)) {
            list.add(text4);
         }
      }

      if (list.isEmpty()) {
         for (Block block2 : BuiltInRegistries.BLOCK) {
            if (block2 != Blocks.AIR) {
               String text5 = getBlockPath(block2);
               if (text5.startsWith(text2)) {
                  list.add(text5);
                  if (list.size() >= 20) {
                     break;
                  }
               }
            }
         }

         for (EntityType entityType2 : BuiltInRegistries.ENTITY_TYPE) {
            if (list.size() >= 20) {
               break;
            }

            String text6 = BuiltInRegistries.ENTITY_TYPE.getKey(entityType2).getPath();
            if (text6.startsWith(text2)) {
               list.add(text6);
            }
         }
      }

      return list;
   }

   private static List<String> filterByPrefix(Iterable<String> stringList, String text) {
      String text2 = text.toLowerCase(Locale.ROOT);
      ArrayList list = new ArrayList();

      for (String text3 : stringList) {
         if (text3.toLowerCase(Locale.ROOT).startsWith(text2)) {
            list.add(text3);
         }
      }

      return list;
   }

   private void printUsage() {
      ChatUtil.log("§cИспользование: §7.blockesp <add|remove|list|clear>");
   }
}
