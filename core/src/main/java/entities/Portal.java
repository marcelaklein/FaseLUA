package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import physics.PhysicsWorld;

public class Portal extends Entidade {

    private final Sprite sprite;
    private final String targetPhase;
    private Body body;

    // Construtor completo com dimensões personalizáveis
    public Portal(float x, float y, float width, float height, Texture texture, String targetPhase, PhysicsWorld physicsWorld) {
        super(x, y, width, height);
        this.targetPhase = targetPhase;

        this.sprite = new Sprite(texture);
        this.sprite.setSize(width, height);
        this.sprite.setPosition(x, y);

        if (physicsWorld != null) {
            this.body = physicsWorld.createSensorBody(
                x + width / 2f,
                y + height / 2f,
                width,
                height,
                "PORTAL"
            );
        }
    }

    // Construtor com tamanho padrão (96x96)
    public Portal(float x, float y, Texture texture, String targetPhase, PhysicsWorld physicsWorld) {
        this(x, y, 96f, 96f, texture, targetPhase, physicsWorld);
    }

    @Override
    public void update(float delta) {
        // Reservado para futuras animações de Rotação/Efeitos do Portal
    }

    @Override
    public void render(SpriteBatch batch) {
        if (ativo) {
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        // Textura gerenciada externamente pelo AssetManager
    }

    public String getTargetPhase() {
        return targetPhase;
    }

    public Body getBody() {
        return body;
    }
}
