package events;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class EventBus {

    public interface EventListener {
        void onEvent(GameEvent event);
    }

    private static EventBus instance;
    private final ObjectMap<EventType, Array<EventListener>> listeners;

    private EventBus() {
        listeners = new ObjectMap<>();
    }

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public void subscribe(EventType type, EventListener listener) {
        if (type == null || listener == null) return;

        Array<EventListener> list = listeners.get(type);
        if (list == null) {
            list = new Array<>(false, 8);
            listeners.put(type, list);
        }
        if (!list.contains(listener, true)) {
            list.add(listener);
        }
    }

    public void unsubscribe(EventType type, EventListener listener) {
        if (type == null || listener == null) return;

        Array<EventListener> list = listeners.get(type);
        if (list != null) {
            list.removeValue(listener, true);
        }
    }

    public void publish(GameEvent event) {
        if (event == null) return;

        Array<EventListener> list = listeners.get(event.getType());
        if (list != null) {
            // OTIMIZAÇÃO: Percorrer de trás para frente elimina 'new Array<>(list)',
            // reduz uso do GC e lida com remoções de ouvintes com segurança.
            for (int i = list.size - 1; i >= 0; i--) {
                list.get(i).onEvent(event);
            }
        }
    }

    public void publish(EventType type) {
        publish(new GameEvent(type));
    }

    public void publish(EventType type, Object data) {
        publish(new GameEvent(type, data));
    }

    public void clear() {
        listeners.clear();
    }
}
