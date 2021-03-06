package GameObject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Rectangle {
    public float width;
    public float height;
    private final ShapeRenderer shapeRenderer;
    private BodyDef bodyDef;
    private FixtureDef fixtureDef;
    public Body body;

    public Rectangle(World world, ShapeRenderer shapeRenderer, float width, float height) {
        this.width = width;
        this.height = height;
        this.shapeRenderer = shapeRenderer;
        createBody(world);
    }

    private void createBody(World _world) {
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.bullet = true;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2, new Vector2(width/2, height/2), 0f);
        fixtureDef  = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 1f;
        body = _world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    private Matrix4 transform = new Matrix4();
    public void draw() {
        Vector2 pos = body.getWorldCenter();
        float angle = body.getAngle();
        transform.setToTranslation(pos.x, pos.y, 0);
        transform.rotate(0, 0, 1, (float) Math.toDegrees(angle));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.identity();
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.setTransformMatrix(transform);
        shapeRenderer.rect(0, 0, width, height);
        shapeRenderer.end();
    }
}