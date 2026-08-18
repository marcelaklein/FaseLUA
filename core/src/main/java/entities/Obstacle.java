package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import physics.PhysicsWorld;

public class Obstacle extends Entidade {

    private final Sprite sprite;
    private Body body;

    // Construtor completo: permite criar obstáculos de qualquer tamanho
    public Obstacle(float x, float y, float width, float height, Texture texture, PhysicsWorld physicsWorld) {
        super(x, y, width, height);

        this.sprite = new Sprite(texture);
        this.sprite.setSize(width, height);
        this.sprite.setPosition(x, y);

        if (physicsWorld != null) {
            this.body = physicsWorld.createStaticBody(
                x + width / 2f,
                y + height / 2f,
                width,
                height,
                "OBSTACLE"
            );
        }
    }

    // Construtor conveniente com tamanho padrão (64x64)
    public Obstacle(float x, float y, Texture texture, PhysicsWorld physicsWorld) {
        this(x, y, 64f, 64f, texture, physicsWorld);
    }

    @Override
    public void update(float delta) {
        // Obstáculos estáticos não exigem lógica de atualização por frame
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

    public Body getBody() {
        return body;
    }
}
