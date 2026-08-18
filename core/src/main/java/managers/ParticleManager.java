package managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public class ParticleManager implements Disposable {

    private final ObjectMap<String, ParticleEffectPool> pools;
    private final Array<ParticleEffect> baseEffects;
    private final Array<ParticleEffectPool.PooledEffect> activeEffects;

    public ParticleManager() {
        pools = new ObjectMap<>();
        baseEffects = new Array<>();
        activeEffects = new Array<>();
    }

    public void loadEffect(String name, String effectPath, String imagesDir) {
        ParticleEffect effect = new ParticleEffect();
        effect.load(Gdx.files.internal(effectPath), Gdx.files.internal(imagesDir));

        // Guarda a referência base para fazer o dispose() correto no final
        baseEffects.add(effect);

        ParticleEffectPool pool = new ParticleEffectPool(effect, 8, 30);
        pools.put(name, pool);
    }

    public void play(String name, float x, float y) {
        play(name, x, y, 1.0f);
    }

    public void play(String name, float x, float y, float scale) {
        ParticleEffectPool pool = pools.get(name);
        if (pool == null) {
            Gdx.app.error("ParticleManager", "Efeito não encontrado: " + name);
            return;
        }

        ParticleEffectPool.PooledEffect effect = pool.obtain();
        effect.setPosition(x, y);
        if (scale != 1.0f) {
            effect.scaleEffect(scale);
        }
        effect.start();
        activeEffects.add(effect);
    }

    public void update(float delta) {
        for (int i = activeEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = activeEffects.get(i);
            effect.update(delta);

            if (effect.isComplete()) {
                effect.free();
                activeEffects.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch) {
        // OTIMIZAÇÃO: Loop indexado evita alocação de iteradores durante o render
        for (int i = 0; i < activeEffects.size; i++) {
            activeEffects.get(i).draw(batch);
        }
    }

    @Override
    public void dispose() {
        for (int i = 0; i < activeEffects.size; i++) {
            activeEffects.get(i).free();
        }
        activeEffects.clear();

        for (ParticleEffectPool pool : pools.values()) {
            pool.clear();
        }
        pools.clear();

        // OTIMIZAÇÃO: Libera texturas e recursos das partículas base
        for (int i = 0; i < baseEffects.size; i++) {
            baseEffects.get(i).dispose();
        }
        baseEffects.clear();
    }
}
