package managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;

public class AssetManager implements Disposable {
    public BitmapFont font;
    public Texture oxigenioTexture;
    public Texture comidaTexture;
    public Texture abrigoTexture;

    // --- TEXTURA DO FUNDO ADICIONADA AQUI ---
    public Texture backgroundTexture;

    public AssetManager() {
        font = new BitmapFont();

        // Carrega as texturas dos itens
        oxigenioTexture = new Texture(Gdx.files.internal("textures/oxigenio.png"));
        comidaTexture = new Texture(Gdx.files.internal("textures/comida.png"));
        abrigoTexture = new Texture(Gdx.files.internal("textures/abrigo.png"));

        // --- CARREGAMENTO DO FUNDO ADICIONADO AQUI ---
        backgroundTexture = new Texture(Gdx.files.internal("textures/lua_background.png"));
    }

    @Override
    public void dispose() {
        if (font != null) font.dispose();
        if (oxigenioTexture != null) oxigenioTexture.dispose();
        if (comidaTexture != null) comidaTexture.dispose();
        if (abrigoTexture != null) abrigoTexture.dispose();

        // --- DESCARTE DO FUNDO ADICIONADO AQUI ---
        if (backgroundTexture != null) backgroundTexture.dispose();
    }
}
