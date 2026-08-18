package entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entidade {

    // OTIMIZAÇÃO: Usar 'final' previne que as subclasses usem 'new Vector2()' ou 'new Rectangle()',
    // forçando-as a usar '.set(x,y)', o que evita alocação de memória e travamentos pelo Garbage Collector.
    protected final Vector2 position;
    protected final Vector2 velocity;
    protected final Rectangle bounds;

    protected float width, height;
    protected boolean ativo = true;

    public Entidade(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2();
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(x, y, width, height);
    }

    public abstract void update(float delta);
    public abstract void render(SpriteBatch batch);
    public abstract void dispose();

    // MELHORIA: Atualiza a posição e os limites (hitbox) simultaneamente para evitar dessincronização.
    public void setPosition(float x, float y) {
        this.position.set(x, y);
        this.bounds.setPosition(x, y);
    }

    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public Rectangle getBounds() { return bounds; }

    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
