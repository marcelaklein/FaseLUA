package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import io.faseLUA.FaseLUA;
import managers.AssetManager;

public class GameOverScreen implements Screen {
    private final FaseLUA game;
    private final SpriteBatch batch;
    private final AssetManager assets;
    private final String motivo;
    private OrthographicCamera camera;
    private BitmapFont font;
    private Rectangle botaoTentarNovamente;
    private Rectangle botaoMenu;

    public GameOverScreen(FaseLUA game, SpriteBatch batch, AssetManager assets, String motivo) {
        this.game = game;
        this.batch = batch;
        this.assets = assets;
        this.motivo = motivo;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        font = assets.font;

        botaoTentarNovamente = new Rectangle(420, 300, 440, 70);
        botaoMenu = new Rectangle(420, 200, 440, 70);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        font.getData().setScale(2.8f);
        font.setColor(1f, 0.2f, 0.2f, 1f);
        font.draw(batch, "FIM DE JOGO", 450, 580);

        font.getData().setScale(1.4f);
        font.setColor(0.9f, 0.8f, 0.8f, 1f);
        font.draw(batch, motivo, 380, 480);

        font.getData().setScale(1.7f);
        font.setColor(1f, 0.9f, 0.4f, 1f);
        font.draw(batch, "[ TENTAR NOVAMENTE ]", 440, 340);

        font.setColor(0.8f, 0.8f, 0.8f, 1f);
        font.draw(batch, "[ MENU PRINCIPAL ]", 460, 240);

        font.getData().setScale(1.0f);
        batch.end();

        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);

            if (botaoTentarNovamente.contains(touch.x, touch.y)) {
                game.setScreen(new GameScreen(game, batch, assets));
            } else if (botaoMenu.contains(touch.x, touch.y)) {
                game.setScreen(new MenuScreen(game, batch, assets));
            }
        }
    }

    @Override public void resize(int width, int height) { camera.setToOrtho(false, 1280, 720); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
