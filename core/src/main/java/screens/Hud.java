package screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import entities.Astronauta;
import managers.AssetManager;

public class Hud implements Disposable {
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;

    public Hud(AssetManager assets) {
        this.font = assets.font;
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render(SpriteBatch batch, Astronauta astronauta, float cameraX, float cameraY) {
        float barX = cameraX - 300;
        float barY = cameraY + 220;
        float barWidth = 200;
        float barHeight = 18f;

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        float oxiPercent = Math.max(0f, Math.min(1f, astronauta.getOxigenio() / 100f));
        if (oxiPercent > 0.5f) shapeRenderer.setColor(0.2f, 0.8f, 1f, 1f);
        else if (oxiPercent > 0.3f) shapeRenderer.setColor(1f, 0.8f, 0.2f, 1f);
        else shapeRenderer.setColor(1f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(barX, barY, barWidth * oxiPercent, barHeight);

        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(barX, barY - 35, barWidth, barHeight);

        float enerPercent = Math.max(0f, Math.min(1f, astronauta.getEnergia() / 100f));
        if (enerPercent > 0.5f) shapeRenderer.setColor(0.3f, 0.9f, 0.3f, 1f);
        else if (enerPercent > 0.3f) shapeRenderer.setColor(1f, 0.7f, 0.2f, 1f);
        else shapeRenderer.setColor(1f, 0.4f, 0.1f, 1f);
        shapeRenderer.rect(barX, barY - 35, barWidth * enerPercent, barHeight);

        shapeRenderer.end();

        batch.begin();
        float hudX = cameraX - 300;
        float hudY = cameraY + 300;

        font.getData().setScale(1.3f);
        font.setColor(Color.WHITE);
        font.draw(batch, "ECHOES na Lua", hudX, hudY + 30);
        font.draw(batch, "Base Orion", hudX, hudY + 5);

        font.getData().setScale(1.1f);
        font.draw(batch, "O2: " + (int) astronauta.getOxigenio() + "%", hudX + 230, barY + 15);
        font.draw(batch, "Energia: " + (int) astronauta.getEnergia() + "%", hudX + 230, barY - 20);

        font.setColor(0.9f, 0.9f, 0.6f, 1f);
        font.draw(batch, "Tempo: " + (int) astronauta.getTempoVivo() + "s", hudX, barY - 70);

        if (astronauta.getOxigenio() < 30) {
            font.setColor(Color.RED);
            font.draw(batch, "OXIGENIO CRITICO!", hudX, barY - 100);
        }
        if (astronauta.getEnergia() < 30) {
            font.setColor(Color.ORANGE);
            font.draw(batch, "ENERGIA BAIXA!", hudX, barY - 125);
        }

        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);

        batch.end();
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
