package events;

import com.badlogic.gdx.utils.Pool.Poolable;

public class GameEvent implements Poolable {

    private EventType type;
    private Object data;

    // Construtor padrão necessário para o Pool da LibGDX
    public GameEvent() {
        this(null, null);
    }

    public GameEvent(EventType type) {
        this(type, null);
    }

    public GameEvent(EventType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public EventType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    // CORREÇÃO: Nomenclatura corrigida de getdata para getData (camelCase)
    @SuppressWarnings("unchecked")
    public <T> T getData(Class<T> clazz) {
        if (data != null && clazz.isInstance(data)) {
            return (T) data;
        }
        return null;
    }

    // Configura o evento ao reutilizar um objeto do Pool
    public void set(EventType type, Object data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public void reset() {
        this.type = null;
        this.data = null;
    }
}
