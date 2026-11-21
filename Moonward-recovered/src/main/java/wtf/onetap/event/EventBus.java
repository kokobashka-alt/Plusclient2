package wtf.onetap.event;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.gui.ChatInputHelper;
import wtf.onetap.module.Module;
import wtf.onetap.module.misc.UnHook;
import wtf.onetap.util.LatencyStats;

public class EventBus {
   private static final Comparator<EventBus.Listener> PRIORITY_ORDER = Comparator.<EventBus.Listener>comparingInt(listener -> listener.priority.ordinal()).reversed();
   private final Map<Class<? extends Event>, List<EventBus.Listener>> listeners;
   private final Lookup lookup;
   private static String[] textArray;

   public EventBus() {
      this.listeners = new ConcurrentHashMap<>();
      this.lookup = MethodHandles.lookup();
   }

   public void register(Object object) {
      Event.m_8_C3_sArr();

      for (Method method : object.getClass().getDeclaredMethods()) {
         EventTarget eventTarget = method.getAnnotation(EventTarget.class);
         if (eventTarget != null) {
            Class[] classArray = method.getParameterTypes();
            if (classArray.length == 1 && Event.class.isAssignableFrom(classArray[0])) {
               method.setAccessible(true);

               try {
                  MethodHandle methodHandle = this.lookup.unreflect(method).bindTo(object);
                  EventBus.Listener listener = new EventBus.Listener(classArray[0], methodHandle, eventTarget.priority(), object);
                  this.listeners.computeIfAbsent(listener.eventType, clazz -> new CopyOnWriteArrayList<>()).add(listener);
                  ((CopyOnWriteArrayList)this.listeners.get(listener.eventType)).sort(PRIORITY_ORDER);
               } catch (IllegalAccessException illegalAccessException) {
                  throw new IllegalStateException("Cannot bind @EventTarget method " + method, illegalAccessException);
               }
            }
         }
      }
   }

   public void unregister(Object object) {
      for (List list : this.listeners.values()) {
         list.removeIf(object2 -> ((wtf.onetap.event.EventBus.Listener)object2).owner == object);
      }
   }

   public void unregister(Event event) {
      Event.m_8_C3_sArr();
      var listenerList = this.listeners.get(event.getClass());
      if (listenerList != null) {
         UnHook unHook = Onetap.MODULE_MANAGER.get(UnHook.class);

         for (EventBus.Listener listener : listenerList) {
            if (unHook == null || !unHook.isFlag() || event instanceof KeyPressEvent && listener.owner instanceof ChatInputHelper) {
               if (event instanceof Cancellable cancellable && cancellable.isCancelled()) {
                  return;
               }

               long longValue = listener.stats != null ? System.nanoTime() : 0L;

               try {
                  listener.handle.invoke((Event)event);
               } catch (Throwable e) {
                  throw new RuntimeException("EventTarget handler threw for " + event.getClass(), e);
               }

               if (listener.stats != null) {
                  listener.stats.recordSince(longValue);
               }
            }
         }
      }
   }

   private static LatencyStats.StateHolder m_5(Object object, Class<? extends Event> clazz) {
      if (object instanceof Module module) {
         String text = clazz == TickEvent.class
            ? "update"
            : (clazz == ConnectedEvent.class ? "tick" : (clazz == PreMotionEvent.class ? "pre-motion" : (clazz == PostMotionEvent.class ? "post-motion" : (clazz == MovementInputEvent.class ? "input" : null))));
         return text == null ? null : LatencyStats.getOrCreate(module.getName() + " " + text);
      } else {
         return null;
      }
   }

   static {
      m_8(null);
   }

   public static void m_8(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_7_StringArr() {
      return textArray;
   }

   private static final class Listener {
      public final Class<? extends Event> eventType;
      public final MethodHandle handle;
      public final EventPriority priority;
      public final Object owner;
      public final LatencyStats.StateHolder stats;

      public Listener(Class<? extends Event> clazz, MethodHandle methodHandle, EventPriority eventPriority, Object object) {
         Event.m_8_C3_sArr();
         this.eventType = clazz;
         this.handle = methodHandle;
         this.priority = eventPriority;
         this.owner = object;
         this.stats = EventBus.m_5(object, clazz);
      }
   }
}
