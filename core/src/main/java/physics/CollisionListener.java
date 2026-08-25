package physics;

import com.badlogic.gdx.physics.box2d.*;
import events.EventBus;
import events.EventType;

public class CollisionListener implements ContactListener {

    // Defina seus tipos como Enum para garantir performance máxima
    public enum EntityType {
        ASTRONAUTA,
        OBSTACLE,
        WALL,
        PORTAL
    }

    @Override
    public void beginContact(Contact contact) {
        Object dataA = contact.getFixtureA().getBody().getUserData();
        Object dataB = contact.getFixtureB().getBody().getUserData();

        if (!(dataA instanceof EntityType) || !(dataB instanceof EntityType)) {
            return;
        }

        EntityType typeA = (EntityType) dataA;
        EntityType typeB = (EntityType) dataB;

        // Descobre qual dos dois é o astronauta e pega o objeto oposto
        EntityType other = null;
        if (typeA == EntityType.ASTRONAUTA) {
            other = typeB;
        } else if (typeB == EntityType.ASTRONAUTA) {
            other = typeA;
        }

        // Se nenhum for o astronauta, encerra rápido
        if (other == null) return;

        // Processa as colisões adicionando novos casos facilmente no switch
        switch (other) {
            case OBSTACLE:
                EventBus.getInstance().publish(EventType.PLAYER_COLLIDED_OBSTACLE);
                break;

            case WALL:
                EventBus.getInstance().publish(EventType.PLAYER_COLLIDED_WALL);
                break;

            case PORTAL:
                EventBus.getInstance().publish(EventType.PORTAL_ENTERED);
                break;

            // Adicione os outros tipos que faltam aqui
            default:
                break;
        }
    }

    @Override public void endContact(Contact contact) {}
    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
