package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.faseLUA.FaseLUA;
import managers.AssetManager;
import save.SaveManager;

public class MenuScreen implements Screen, Disposable {
    private final FaseLUA game;
    private final SpriteBatch batch;
    private final AssetManager assets;
    private final SaveManager saveManager;

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private static final float WORLD_WIDTH = 1280f;
    private static final float WORLD_HEIGHT = 720f;

    private final float btnWidth = 320f;
    private final float btnHeight = 70f;
    private final float btnX = (WORLD_WIDTH - 320f) / 2f;
    private final float btnNovoY = 380f;
    private final float btnContinuarY = 280f;
    private final float btnSairY = 180f;

    private final Vector3 touchVector = new Vector3();

    public MenuScreen(FaseLUA game, SpriteBatch batch, AssetManager assets) {
        this.game = game;
        this.batch = batch;
        this.assets = assets;
        this.saveManager = new SaveManager();
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        shapeRenderer = new ShapeRenderer();

        // Checagem de segurança para garantir que a fonte não seja nula
        if (assets != null && assets.font != null) {
            font = assets.font;
        } else {
            font = new BitmapFont();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.20f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        // --- CÓDIGO DO FUNDO ADICIONADO AQUI ---
        // Desenha a textura de fundo antes dos botões para que fique por trás
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (assets.backgroundTexture != null) {
            batch.draw(assets.backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        }
        batch.end();
        // ----------------------------------------

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.2f, 0.45f, 0.75f, 1f);
        shapeRenderer.rect(btnX, btnNovoY, btnWidth, btnHeight);

        if (saveManager.hasSave()) {
            shapeRenderer.setColor(0.2f, 0.65f, 0.35f, 1f);
        } else {
            shapeRenderer.setColor(0.3f, 0.3f, 0.32f, 1f);
        }
        shapeRenderer.rect(btnX, btnContinuarY, btnWidth, btnHeight);

        shapeRenderer.setColor(0.6f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(btnX, btnSairY, btnWidth, btnHeight);
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        font.getData().setScale(2.8f);
        font.setColor(1f, 0.9f, 0.6f, 1f);
        font.draw(batch, "ECHOES na Lua", 380, 580);

        font.getData().setScale(1.8f);
        font.setColor(0.9f, 0.85f, 0.7f, 1f);
        font.draw(batch, "Survival RPG", 520, 520);

        font.getData().setScale(1.2f);
        font.setColor(0.8f, 0.75f, 0.6f, 1f);
        font.draw(batch, "Base Orion - Missão de Sobrevivência", 400, 490);

        font.getData().setScale(1.5f);
        font.setColor(1f, 1f, 1f, 1f);
        font.draw(batch, "NOVO JOGO", btnX + 70, btnNovoY + 45);

        if (saveManager.hasSave()) {
            font.draw(batch, "CONTINUAR", btnX + 70, btnContinuarY + 45);
            font.getData().setScale(0.9f);
            font.setColor(0.7f, 0.95f, 0.7f, 1f);
            font.draw(batch, "Último save: " + saveManager.getLastSaveTime(), btnX + 30, btnContinuarY - 15);
        } else {
            font.setColor(0.5f, 0.5f, 0.5f, 1f);
            font.draw(batch, "CONTINUAR", btnX + 70, btnContinuarY + 45);
        }

        font.getData().setScale(1.5f);
        font.setColor(1f, 1f, 1f, 1f);
        font.draw(batch, "SAIR", btnX + 115, btnSairY + 45);

        font.getData().setScale(0.95f);
        font.setColor(0.6f, 0.65f, 0.7f, 1f);
        font.draw(batch, "Durante o jogo: F5 = Salvar | Abrigo = Auto-save", 340, 60);

        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);
        batch.end();

        if (Gdx.input.justTouched()) {
            touchVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchVector);

            float worldX = touchVector.x;
            float worldY = touchVector.y;

            if (isInside(worldX, worldY, btnX, btnNovoY, btnWidth, btnHeight)) {
                game.deveCarregarSave = false;
                game.setScreen(new GameScreen(game, batch, assets));
            } else if (saveManager.hasSave() && isInside(worldX, worldY, btnX, btnContinuarY, btnWidth, btnHeight)) {
                game.deveCarregarSave = true;
                game.setScreen(new GameScreen(game, batch, assets));
            } else if (isInside(worldX, worldY, btnX, btnSairY, btnWidth, btnHeight)) {
                Gdx.app.exit();
            }
        }
    }

    private boolean isInside(float mx, float my, float x, float y, float w, float h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
