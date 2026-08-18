package input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import events.EventBus;
import events.EventType;

public class GameInputProcessor implements InputProcessor {

    private final Vector2 direction = new Vector2();
    private boolean up, down, left, right;
    private boolean pausePressed = false;

    public Vector2 getDirection() {
        direction.set(0, 0);

        if (up) direction.y += 1f;
        if (down) direction.y -= 1f;
        if (left) direction.x -= 1f;
        if (right) direction.x += 1f;

        // OTIMIZAÇÃO: Normaliza apenas se o vetor tiver magnitude (evita cálculo desnecessário em (0,0))
        if (direction.x != 0 || direction.y != 0) {
            direction.nor();
        }

        return direction;
    }

    public boolean isMoving() {
        return up || down || left || right;
    }

    public boolean isPauseJustPressed() {
        boolean wasPressed = pausePressed;
        pausePressed = false;
        return wasPressed;
    }

    /**
     * Reseta o estado dos botões. Deve ser chamado ao perder o foco ou mudar de tela
     * para evitar que o personagem continue andando sozinho ("tecla presa").
     */
    public void reset() {
        up = false;
        down = false;
        left = false;
        right = false;
        pausePressed = false;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.W:
            case Input.Keys.UP:
                up = true;
                return true;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                down = true;
                return true;
            case Input.Keys.A:
            case Input.Keys.LEFT:
                left = true;
                return true;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                right = true;
                return true;
            case Input.Keys.ESCAPE:
                pausePressed = true;
                return true;
            case Input.Keys.SPACE:
                EventBus.getInstance().publish(EventType.PLAYER_MOVED, "SPACE");
                return true;
            default:
                return false; // Permite que outros processadores de entrada usem a tecla
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.W:
            case Input.Keys.UP:
                up = false;
                return true;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                down = false;
                return true;
            case Input.Keys.A:
            case Input.Keys.LEFT:
                left = false;
                return true;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                right = false;
                return true;
            default:
                return false;
        }
    }

    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}
