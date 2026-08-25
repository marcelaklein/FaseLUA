package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.faseLUA.FaseLUA;

import entities.Astronauta;
import entities.Item;
import entities.Obstacle;
import entities.Portal;
import events.EventBus;
import events.EventType;
import events.GameEvent;
import input.GameInputProcessor;
import managers.AssetManager;
import managers.ParticleManager;
import physics.PhysicsWorld;
import physics.PhysicsWorld.GravityPreset;
import save.GameSaveData;
import save.SaveManager;

public class GameScreen implements Screen, EventBus.EventListener {
    private final FaseLUA game;
    private final SpriteBatch batch;
    private final AssetManager assets;

    private Astronauta astronauta;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Portal portal;
    private PhysicsWorld physicsWorld;
    private GameInputProcessor inputProcessor;
    private EventBus eventBus;
    private SaveManager saveManager;
    private Array<Item> itens;
    private Array<Obstacle> obstacles;
    private Hud hud;
    private ParticleManager particleManager;

    private String currentPhase = "LUA";
    private float obstacleCooldown = 0f;
    private float poeiraTimer = 0f;
    private float alertaTimer = 0f;
    private Item abrigo = null;
    private boolean pausado = false;
    private final float TEMPO_VITORIA = 60f;
    private float saveFeedbackTimer = 0f;

    private Texture obstacleTex;
    private Texture portalTex;

    public GameScreen(FaseLUA game, SpriteBatch batch, AssetManager assets) {
        this.game = game;
        this.batch = batch;
        this.assets = assets;
    }

    @Override
    public void show() {
        physicsWorld = new PhysicsWorld();
        physicsWorld.setPhaseGravity(GravityPreset.valueOf(currentPhase));

        inputProcessor = new GameInputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);

        eventBus = EventBus.getInstance();
        eventBus.subscribe(EventType.PLAYER_COLLIDED_OBSTACLE, this);
        eventBus.subscribe(EventType.PLAYER_COLLIDED_WALL, this);
        eventBus.subscribe(EventType.PORTAL_ENTERED, this);
        eventBus.subscribe(EventType.PLAYER_DIED, this);

        saveManager = new SaveManager();

        astronauta = new Astronauta(200, 200, assets, physicsWorld);
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);

        if (game.deveCarregarSave) {
            carregarJogo();
            game.deveCarregarSave = false;
        }

        itens = new Array<>();
        itens.add(new Item(400, 300, "oxigenio", assets));
        itens.add(new Item(800, 600, "oxigenio", assets));
        itens.add(new Item(600, 400, "comida", assets));
        itens.add(new Item(1000, 700, "comida", assets));
        itens.add(new Item(300, 500, "comida", assets));
        itens.add(new Item(700, 200, "oxigenio", assets));
        itens.add(new Item(900, 350, "abrigo", assets));

        obstacles = new Array<>();
        obstacleTex = new Texture(Gdx.files.internal("textures/obstacle.png"));

        obstacles.add(new Obstacle(300, 120, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(420, 180, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(600, 100, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(600, 280, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(780, 160, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(780, 340, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(980, 120, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(980, 300, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(1150, 200, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(1150, 380, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(1250, 120, obstacleTex, physicsWorld));
        obstacles.add(new Obstacle(1250, 450, obstacleTex, physicsWorld));

        portalTex = new Texture(Gdx.files.internal("textures/portal.png"));
        portal = new Portal(1700, 320, portalTex, "MARTE", physicsWorld);

        for (Item item : itens) {
            if ("abrigo".equals(item.getTipo())) {
                abrigo = item;
                break;
            }
        }

        hud = new Hud(assets);
        pausado = false;

        particleManager = new ParticleManager();
        particleManager.loadEffect("poeira", "particles/poeira.p", "particles/");
        particleManager.loadEffect("faisca", "particles/faisca.p", "particles/");
        particleManager.loadEffect("alerta", "particles/alerta_oxigenio.p", "particles/");
        particleManager.loadEffect("coleta", "particles/coleta.p", "particles/");
        particleManager.loadEffect("explosao", "particles/explosao.p", "particles/");
        particleManager.loadEffect("rastro", "particles/rastro.p", "particles/");

        physicsWorld.createStaticBody(640, 15, 1600, 30, "WALL");
        physicsWorld.createStaticBody(-20, 360, 40, 800, "WALL");
        physicsWorld.createStaticBody(1620, 360, 40, 800, "WALL");
    }

    @Override
    public void render(float delta) {
        if (inputProcessor.isPauseJustPressed()) {
            pausado = !pausado;
        }

        if (pausado) {
            renderPauseScreen();
            return;
        }

        if (obstacleCooldown > 0f) obstacleCooldown -= delta;
        if (saveFeedbackTimer > 0f) saveFeedbackTimer -= delta;

        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            salvarJogo();
        }

        physicsWorld.update(delta);

        Vector2 dir = inputProcessor.getDirection();
        astronauta.move(dir.x, dir.y, delta);
        astronauta.update(delta);
        particleManager.update(delta);

        if (astronauta.isMorto()) {
            particleManager.play("explosao", astronauta.getPosition().x + 16, astronauta.getPosition().y + 20, 1.5f);
            String motivo = astronauta.getOxigenio() <= 0 ? "Você morreu por falta de Oxigênio!" : "Você morreu por falta de Energia!";
            game.setScreen(new GameOverScreen(game, batch, assets, motivo));
            return;
        }

        if (astronauta.getTempoVivo() >= TEMPO_VITORIA) {
            game.setScreen(new VictoryScreen(game, batch, assets, astronauta.getTempoVivo()));
            return;
        }

        camera.position.x += (astronauta.getPosition().x - camera.position.x) * 0.1f;
        camera.position.y += (astronauta.getPosition().y - camera.position.y) * 0.1f;
        camera.update();

        if ("LUA".equalsIgnoreCase(currentPhase)) {
            Gdx.gl.glClearColor(0.15f, 0.15f, 0.20f, 1f);
        } else {
            Gdx.gl.glClearColor(0.65f, 0.35f, 0.2f, 1f);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Item item : itens) item.render(batch);
        for (Obstacle obs : obstacles) obs.render(batch);
        if (portal != null) portal.render(batch);

        astronauta.render(batch);
        particleManager.render(batch);

        boolean dentroDoAbrigo = false;
        for (Item item : itens) {
            if (item.isColetado()) continue;
            if (astronauta.getBounds().overlaps(item.getBounds())) {
                particleManager.play("coleta", item.getPosition().x + 16, item.getPosition().y + 16, 1.1f);

                if ("oxigenio".equals(item.getTipo())) {
                    astronauta.oxigenioRecuperada(30);
                    item.coletar();
                } else if ("comida".equals(item.getTipo())) {
                    astronauta.energiaRecuperada(40);
                    item.coletar();
                } else if ("abrigo".equals(item.getTipo())) {
                    dentroDoAbrigo = true;
                    astronauta.oxigenioRecuperada(100);
                }
            }
        }
        astronauta.setProtegido(dentroDoAbrigo);
        batch.end();

        updateParticles(delta);

        hud.render(batch, astronauta, camera.position.x, camera.position.y);

        if (saveFeedbackTimer > 0f) {
            batch.begin();
            assets.font.getData().setScale(1.3f);
            assets.font.setColor(0.3f, 1f, 0.5f, 1f);
            assets.font.draw(batch, "PROGRESSO SALVO / CARREGADO!", camera.position.x - 140, camera.position.y + 280);
            assets.font.getData().setScale(1.0f);
            batch.end();
        }
    }

    private void renderPauseScreen() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        assets.font.getData().setScale(2.5f);
        assets.font.setColor(1f, 1f, 0.6f, 1f);
        assets.font.draw(batch, "PAUSADO", 540, 400);

        assets.font.getData().setScale(1.3f);
        assets.font.setColor(0.8f, 0.8f, 0.7f, 1f);
        assets.font.draw(batch, "Pressione ESC para continuar", 470, 330);
        assets.font.getData().setScale(1.0f);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.L) && saveManager.hasSave()) {
            carregarJogo();
            pausado = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.setScreen(new MenuScreen(game, batch, assets));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            salvarJogo();
        }
    }

    private void updateParticles(float delta) {
        if (astronauta.isMoving()) {
            poeiraTimer += delta;
            if (poeiraTimer >= 0.08f) {
                particleManager.play("poeira", astronauta.getPosition().x + 16, astronauta.getPosition().y + 4);
                poeiraTimer = 0f;
            }
        } else {
            poeiraTimer = 0f;
        }

        if (abrigo != null) {
            particleManager.play("faisca", abrigo.getPosition().x + 80, abrigo.getPosition().y + 140, 0.7f);
        }

        if (astronauta.getOxigenio() < 30f) {
            alertaTimer += delta;
            if (alertaTimer >= 0.25f) {
                particleManager.play("alerta", astronauta.getPosition().x + 16, astronauta.getPosition().y + 40, 0.9f);
                alertaTimer = 0f;
            }
        } else {
            alertaTimer = 0f;
        }

        if (astronauta.isMoving() && astronauta.getEnergia() < 40f) {
            particleManager.play("rastro", astronauta.getPosition().x + 16, astronauta.getPosition().y + 10, 0.6f);
        }
    }

    private void salvarJogo() {
        if (astronauta == null) return;
        GameSaveData data = astronauta.toSaveData(currentPhase);
        saveManager.save(data);
        saveFeedbackTimer = 2.0f;
    }

    private void carregarJogo() {
        if (!saveManager.hasSave()) return;
        GameSaveData data = saveManager.load();
        if (data != null) {
            astronauta.fromSaveData(data);
            currentPhase = data.fase != null ? data.fase : "LUA";
            physicsWorld.setPhaseGravity(GravityPreset.valueOf(currentPhase));
            saveFeedbackTimer = 2.0f;
        }
    }

    @Override
    public void onEvent(GameEvent event) {
        switch (event.getType()) {
            case PLAYER_COLLIDED_OBSTACLE:
                if (obstacleCooldown <= 0f) {
                    astronauta.oxigenioRecuperada(-12f);
                    obstacleCooldown = 1.0f;
                }
                break;
            case PORTAL_ENTERED:
                if (portal != null) {
                    changePhase(portal.getTargetPhase());
                }
                break;
            case PLAYER_DIED:
                game.setScreen(new GameOverScreen(game, batch, assets, "Você morreu!"));
                break;
            default:
                break;
        }
    }

    private void changePhase(String newPhase) {
        currentPhase = newPhase;
        physicsWorld.setPhaseGravity(GravityPreset.valueOf(newPhase));
        eventBus.publish(EventType.PHASE_CHANGED, newPhase);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (physicsWorld != null) physicsWorld.dispose();
        if (hud != null) hud.dispose();
        if (particleManager != null) particleManager.dispose();
        if (obstacleTex != null) obstacleTex.dispose();
        if (portalTex != null) portalTex.dispose();

        if (itens != null) {
            for (Item item : itens) {
                if (item != null) item.dispose();
            }
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
