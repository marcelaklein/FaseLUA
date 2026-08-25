package physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;

public class PhysicsWorld implements Disposable {

    public enum GravityPreset {
        LUA(-1.62f),
        MARTE(-3.71f);

        private final float value;
        GravityPreset(float value) { this.value = value; }
        public float getValue() { return value; }
    }

    public static final float PPM = 32f;
    private static final float TIME_STEP = 1f / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    private final World world;
    private final Vector2 gravityVector = new Vector2(0, GravityPreset.MARTE.getValue());
    private float accumulator = 0f;

    public PhysicsWorld() {
        world = new World(gravityVector, true);
        world.setContactListener(new CollisionListener());
    }

    public void update(float delta) {
        // Limita o tempo máximo por frame para evitar "spiral of death" em travamentos
        float frameTime = Math.min(delta, 0.25f);
        accumulator += frameTime;

        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }

    public void setGravity(float gravity) {
        this.gravityVector.y = gravity;
        world.setGravity(this.gravityVector);
    }

    public float getCurrentGravity() {
        return gravityVector.y;
    }

    public void setPhaseGravity(GravityPreset preset) {
        setGravity(preset.getValue());
    }

    // --- Métodos de Criação de Corpos ---

    public Body createDynamicBody(float x, float y, float width, float height, Object userData) {
        return buildBody(BodyDef.BodyType.DynamicBody, x, y, width, height, userData, 1.0f, 0.4f, 0.1f, false, true);
    }

    public Body createStaticBody(float x, float y, float width, float height, Object userData) {
        return buildBody(BodyDef.BodyType.StaticBody, x, y, width, height, userData, 0f, 0.6f, 0f, false, false);
    }

    public Body createSensorBody(float x, float y, float width, float height, Object userData) {
        return buildBody(BodyDef.BodyType.StaticBody, x, y, width, height, userData, 0f, 0f, 0f, true, false);
    }

    private Body buildBody(BodyDef.BodyType type, float x, float y, float width, float height,
                           Object userData, float density, float friction, float restitution,
                           boolean isSensor, boolean fixedRotation) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(x / PPM, y / PPM);
        bodyDef.fixedRotation = fixedRotation;

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((width / 2f) / PPM, (height / 2f) / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.isSensor = isSensor;

        body.createFixture(fixtureDef);
        body.setUserData(userData);

        shape.dispose();
        return body;
    }

    public void destroyBody(Body body) {
        if (body != null && !world.isLocked()) {
            world.destroyBody(body);
        }
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void dispose() {
        if (world != null) {
            world.dispose();
        }
    }
}
