package GameObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class Paddle extends Rectangle {
    public static boolean isMobile = false;
    public Paddle(World _world, ShapeRenderer _shapeRenderer, float _width, float _height) {
        super(_world, _shapeRenderer, _width, _height);
    }

    public void processMovement(float move_velocity, float min_x, float max_x, float screen_width, float world_width) {
        float body_x = body.getPosition().x;
        float body_y = body.getPosition().y;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) { // управление кнопками
            body.setLinearVelocity(-move_velocity, 0f);
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            body.setLinearVelocity(move_velocity, 0f);
        } else {
            body.setLinearVelocity(0f, 0f);
        }

        if (Gdx.input.isTouched() & isMobile) { // управление сенсором
            float touch_x = Gdx.input.getX()/(screen_width/world_width);
            if (touch_x - width/2f >= min_x && touch_x + width/2f <= max_x) {
                body.setTransform(touch_x - width/2f, body_y, 0f);
            }
        }

        if (body_x < min_x) { // ограничения
            body.setTransform(min_x, body_y, 0f);
        } else if (body_x + width > max_x) {
            body.setTransform(max_x - width, body_y, 0f);

        }
    }
}
