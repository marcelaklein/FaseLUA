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

public class VictoryScreen implements Screen {
    private final FaseLUA game;
    private final SpriteBatch batch;
    private final AssetManager assets;
    private OrthographicCamera camera;
    private BitmapFont font;
    private Rectangle botaoReiniciar;
    private Rectangle botaoMenu;
    private float tempoSobrevivido;

    public VictoryScreen(FaseLUA game, SpriteBatch batch, AssetManager assets, float tempoSobrevivido) {
        this.game = game;
        this.batch = batch;
        this.assets = assets;
        this.tempoSobrevivido = tempoSobrevivido;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        font = assets.font;

        botaoReiniciar = new Rectangle(420, 300, 440, 70);
        botaoMenu = new Rectangle(420, 200, 440, 70);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.15f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        font.getData().setScale(2.8f);
        font.setColor(0.3f, 1f, 0.4f, 1f);
        font.draw(batch, "MISSAO CUMPRIDA!", 380, 580);

        font.getData().setScale(1.6f);
        font.setColor(0.9f, 1f, 0.7f, 1f);
        font.draw(batch, "Voce sobreviveu " + (int)tempoSobrevivido + " segundos na Lua!", 340, 500);

        font.getData().setScale(1.3f);
        font.setColor(0.8f, 0.9f, 0.7f, 1f);
        font.draw(batch, "Base Orion - Echoes na Lua", 450, 430);

        font.getData().setScale(1.7f);
        font.setColor(0.3f, 0.9f, 0.4f, 1f);
        font.draw(batch, "[ NOVA MISSAO ]", 490, 340);

        font.setColor(0.9f, 0.8f, 0.3f, 1f);
        font.draw(batch, "[ VOLTAR AO MENU ]", 470, 240);

        font.getData().setScale(1.0f);
        font.setColor(0.6f, 0.7f, 0.6f, 1f);
        font.draw(batch, "Professor: Ricardo Marcel | Orion ITAO School", 400, 80);

        batch.end();

        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);

            if (botaoReiniciar.contains(touch.x, touch.y)) {
                game.setScreen(new GameScreen(game, batch, assets));
            } else if (botaoMenu.contains(touch.x, touch.y)) {
                game.setScreen(new MenuScreen(game, batch, assets));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, 1280, 720);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
