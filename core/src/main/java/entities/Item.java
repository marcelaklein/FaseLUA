package entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import managers.AssetManager;

public class Item extends Entidade {

    private final Sprite sprite;
    private final String tipo;
    private boolean coletado = false;

    public Item(float x, float y, String tipo, AssetManager assets) {
        // Passamos 0 provisoriamente, pois vamos definir as dimensões reais no switch
        super(x, y, 0, 0);
        this.tipo = tipo;

        // OTIMIZAÇÃO: Centralizamos os tamanhos aqui para evitar bugs onde
        // a imagem do item fica de um tamanho e a hitbox de colisão fica de outro.
        switch (tipo) {
            case "oxigenio":
                width = 32; height = 48;
                sprite = new Sprite(assets.oxigenioTexture);
                break;
            case "comida":
                width = 40; height = 40;
                sprite = new Sprite(assets.comidaTexture);
                break;
            case "abrigo":
                width = 320; height = 226;
                sprite = new Sprite(assets.abrigoTexture);
                break;
            default:
                width = 32; height = 32;
                sprite = new Sprite(assets.oxigenioTexture);
                break;
        }

        // Atualizamos o sprite e os limites de colisão (bounds da Entidade)
        sprite.setSize(width, height);
        this.bounds.set(x, y, width, height);

        // OTIMIZAÇÃO: Definimos a posição do sprite apenas UMA vez na criação,
        // em vez de repetir 60 vezes por segundo no método render.
        sprite.setPosition(x, y);
    }

    @Override
    public void update(float delta) {
        // Itens estáticos não precisam de update.
        // Se no futuro a comida cair (gravidade), você atualiza o sprite.setPosition aqui.
    }

    @Override
    public void render(SpriteBatch batch) {
        // Renderiza apenas se não foi coletado e a entidade ainda estiver ativa
        if (!coletado && ativo) {
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        // Correto: A textura é gerenciada e descartada pelo AssetManager
    }

    public String getTipo() { return tipo; }

    public boolean isColetado() { return coletado; }

    public void coletar() {
        this.coletado = true;
        this.setAtivo(false); // Sincroniza com a classe base para desativar física/colisões
    }
}
