package io.faseLUA;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import managers.AssetManager;
import screens.MenuScreen;

public class FaseLUA extends Game {
    public SpriteBatch batch;
    public AssetManager assets;
    public boolean deveCarregarSave = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = new AssetManager();
        this.setScreen(new MenuScreen(this, batch, assets));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
    }
}
