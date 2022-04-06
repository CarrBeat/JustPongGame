package GameObject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;


public class Ball {
    private float radius;
    private ShapeRenderer shapeRenderer;
    private BodyDef bodyDef;
    private FixtureDef fixtureDef;
    public Body body;

    public Ball(World world, ShapeRenderer _shapeRenderer, float _radius) {
        radius = _radius;
        shapeRenderer = _shapeRenderer;
        createBody(world);
    }

    private void createBody(World world) {
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.bullet = true;
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 1f;
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void pushBall(Vector2 velocity) { // запуск шарика
        body.setLinearVelocity(velocity);
    }

    public void draw() {
        Vector2 pos = body.getWorldCenter();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.identity();
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(pos.x - radius, pos.y - radius, 2 * radius, 2 * radius);
        shapeRenderer.end();
    }
}
