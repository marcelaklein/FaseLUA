package managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;

public class AssetManager implements Disposable {

    // Texturas públicas para acesso direto pelas entidades
    public Texture astronautaTexture;
    public Texture oxigenioTexture;
    public Texture comidaTexture;
    public Texture abrigoTexture;
    public Texture backgroundTexture;

    // Fonte
    public BitmapFont font;

    public void load() {
        // OTIMIZAÇÃO: Carregamento com aplicação de filtros encapsulada
        astronautaTexture = loadTexture("textures/astronauta.png");
        oxigenioTexture = loadTexture("textures/oxigenio.png");
        comidaTexture = loadTexture("textures/comida.png");
        abrigoTexture = loadTexture("textures/abrigo.png");
        backgroundTexture = loadTexture("textures/marte_background.png");

        // Configuração da fonte com filtro de textura para escalonamento suave
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.setColor(1f, 1f, 1f, 1f);
        font.getData().setScale(1.0f);
    }

    /**
     * Auxiliar para carregar textura e aplicar filtro de qualidade sem repetir código.
     */
    private Texture loadTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }

    @Override
    public void dispose() {
        // Desalocação segura de memória
        if (astronautaTexture != null) astronautaTexture.dispose();
        if (oxigenioTexture != null) oxigenioTexture.dispose();
        if (comidaTexture != null) comidaTexture.dispose();
        if (abrigoTexture != null) abrigoTexture.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (font != null) font.dispose();
    }
}
