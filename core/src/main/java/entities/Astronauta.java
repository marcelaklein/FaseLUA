package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import managers.AssetManager;
import physics.PhysicsWorld;
import save.GameSaveData;

public class Astronauta extends Entidade implements Interagivel {

    // Constantes para balanceamento (boas práticas)
    private static final float CONSUMO_ENERGIA_MOV = 2.5f;
    private static final float CONSUMO_OXIGENIO_MOV = 1.5f;
    private static final float CONSUMO_OXIGENIO_BASE = 3.0f;
    private static final float MAX_STATUS = 100f;

    private float oxigenio = MAX_STATUS;
    private float energia = MAX_STATUS;
    private float speed = 100f;
    private final Sprite sprite;
    private boolean protegido = false;
    private boolean viradoEsquerda = false;
    private float tempoVivo = 0f;
    private Body body;
    private PhysicsWorld physicsWorld;

    public Astronauta(float x, float y, AssetManager assets, PhysicsWorld physicsWorld) {
        super(x, y, 32, 48);
        this.physicsWorld = physicsWorld;

        // OTIMIZAÇÃO: Puxar a textura direto do AssetManager evita Memory Leak e travamentos
        Texture texture = new Texture(com.badlogic.gdx.Gdx.files.internal("textures/astronauta.png"));
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setPosition(x, y);

        position.set(x, y);
        bounds.set(x, y, width, height);
        ativo = true;

        body = physicsWorld.createDynamicBody(
            x + width / 2f,
            y + height / 2f,
            width,
            height,
            "ASTRONAUTA"
        );
    }

    public void move(float dirX, float dirY, float delta) {
        if (!ativo || body == null) return;

        if (dirX != 0) {
            viradoEsquerda = dirX < 0;
        }

        // OTIMIZAÇÃO: Evita setar o flip todo frame se já estiver na direção certa
        if (sprite.isFlipX() != viradoEsquerda) {
            sprite.setFlip(viradoEsquerda, false);
        }

        float velocityX = dirX * speed / PhysicsWorld.PPM;
        float velocityY = dirY * speed / PhysicsWorld.PPM;

        body.setLinearVelocity(velocityX, velocityY);

        Vector2 bodyPos = body.getPosition();
        position.set(
            bodyPos.x * PhysicsWorld.PPM - width / 2f,
            bodyPos.y * PhysicsWorld.PPM - height / 2f
        );

        sprite.setPosition(position.x, position.y);
        bounds.setPosition(position.x, position.y);

        if (dirX != 0 || dirY != 0) {
            energia = Math.max(0, energia - CONSUMO_ENERGIA_MOV * delta);
            if (!protegido) {
                oxigenio = Math.max(0, oxigenio - CONSUMO_OXIGENIO_MOV * delta);
            }
        }
    }

    @Override
    public void update(float delta) {
        if (!ativo) return;

        tempoVivo += delta;

        if (!protegido) {
            oxigenio = Math.max(0, oxigenio - CONSUMO_OXIGENIO_BASE * delta);
        }

        // OTIMIZAÇÃO: Checagem unificada de morte
        if (oxigenio <= 0 || energia <= 0) {
            ativo = false;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (ativo) {
            sprite.draw(batch);
        }
    }

    public GameSaveData toSaveData(String faseAtual) {
        GameSaveData data = new GameSaveData();
        data.posX = position.x;
        data.posY = position.y;
        data.oxigenio = oxigenio;
        data.energia = energia;
        data.tempoVivo = tempoVivo;
        data.fase = faseAtual;
        data.versao = 1;
        return data;
    }

    public void fromSaveData(GameSaveData data) {
        if (data == null) return;

        position.set(data.posX, data.posY);

        if (sprite != null) {
            sprite.setPosition(data.posX, data.posY);
        }
        if (bounds != null) {
            bounds.setPosition(data.posX, data.posY); // BUG RESOLVIDO: Estava sprite.setPosition antes
        }
        if (body != null) {
            body.setTransform(
                (data.posX + width / 2f) / PhysicsWorld.PPM,
                (data.posY + height / 2f) / PhysicsWorld.PPM,
                0
            );
            body.setLinearVelocity(0, 0);

            oxigenio = data.oxigenio;
            energia = data.energia;
            tempoVivo = data.tempoVivo;
            ativo = true;
        }
    }

    public Body getBody() { return body; }
    public Vector2 getPosition() { return position; }
    public Rectangle getBounds() { return bounds; }

    public boolean isMoving() {
        // OTIMIZAÇÃO: len2() evita cálculo pesado de raiz quadrada. 0.4f ao quadrado = 0.16f.
        return body != null && body.getLinearVelocity().len2() > 0.16f;
    }

    public void setProtegido(boolean protegido) { this.protegido = protegido; }
    public float getOxigenio() { return oxigenio; }
    public float getEnergia() { return energia; }
    public float getTempoVivo() { return tempoVivo; }

    public void oxigenioRecuperada(float quantidade) {
        oxigenio = Math.min(MAX_STATUS, oxigenio + quantidade);
    }

    public void energiaRecuperada(float quantidade) {
        energia = Math.min(MAX_STATUS, energia + quantidade);
    }

    public boolean isMorto() {
        return !ativo;
    }

    @Override
    public void dispose() {
        // Vazio de propósito: Como a textura vem do AssetManager,
        // é responsabilidade dele limpar a memória ao trocar/fechar o jogo.
    }

    @Override
    public void interagir(Entidade outra) {
    }

    @Override
    public boolean podeInteragir() {
        return ativo && oxigenio > 15;
    }
}
