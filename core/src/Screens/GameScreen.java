package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import GameObject.Ball;
import GameObject.Paddle;
import GameObject.PaddleEnemy;
import GameObject.Rectangle;
import pong.game.PongGame;

public class GameScreen implements Screen {
    private final Game game;
    private static final int WORLD_WIDTH  = PongGame.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = PongGame.WORLD_HEIGHT;

    // Левая граница, размеры и позиция
    private static final float GROUND_LEFT_SIZE_X = WORLD_WIDTH / 32f;
    private static final float GROUND_LEFT_SIZE_Y = WORLD_HEIGHT;
    private static final float GROUND_LEFT_POSITION_X = -GROUND_LEFT_SIZE_X;
    private static final float GROUND_LEFT_POSITION_Y = 0;

    // Правая граница, размеры и позиция
    private static final float GROUND_RIGHT_SIZE_X = WORLD_WIDTH / 32f;
    private static final float GROUND_RIGHT_SIZE_Y = WORLD_HEIGHT;
    private static final float GROUND_RIGHT_POSITION_X = WORLD_WIDTH;
    private static final float GROUND_RIGHT_POSITION_Y = 0;

    // Шар
    private static final float BALL_RADIUS = WORLD_HEIGHT / 120f;
    private static final float BALL_POSITION_X = WORLD_WIDTH  / 2f;
    private static final float BALL_POSITION_Y = WORLD_HEIGHT / 2f;
    private static final float BALL_SPEED_X = WORLD_WIDTH  / 4f;
    private static final float BALL_SPEED_Y = WORLD_HEIGHT / 4f;
    private static final float BALL_SPEED_START = WORLD_HEIGHT / 3f;  // Начальная скорость шарика
    private static final float BALL_SPEED_MAX = WORLD_HEIGHT * 3f; // Максимальная скорость шарика
    private static final float BALL_SPEED_INCREMENT = WORLD_HEIGHT / 20f; // Величина увеличения скорости шарика поле 5-го удара
    private float ballSpeed; // Текущая скорость шарика

    // ракетка
    private static final float PADDLE_SIZE_X = WORLD_WIDTH  / 10f;
    private static final float PADDLE_SIZE_Y = WORLD_HEIGHT / 64f;
    private static final float PADDLE_POSITION_X = WORLD_WIDTH / 2f - PADDLE_SIZE_X / 2f; // Середина экрана
    private static final float PADDLE_POSITION_Y = WORLD_HEIGHT / 12f; // Нижняя ракетка

    // ракетка соперника
    private static final float PADDLE_ENEMY_SIZE_X = WORLD_WIDTH / 10f;
    private static final float PADDLE_ENEMY_SIZE_Y = WORLD_HEIGHT / 64f;
    private static final float PADDLE_ENEMY_POSITION_X = WORLD_WIDTH / 2f - PADDLE_ENEMY_SIZE_X / 2f; // Середина экрана
    private static final float PADDLE_ENEMY_POSITION_Y = WORLD_HEIGHT - PADDLE_ENEMY_SIZE_Y - WORLD_WIDTH / 8f; // Верхняя ракетка

    // центральная линия
    private static final float CENTER_LINE_POSITION_X = GROUND_LEFT_SIZE_X - WORLD_WIDTH / 64f;
    private static final float CENTER_LINE_POSITION_Y = WORLD_HEIGHT/2f - WORLD_HEIGHT / 256f;
    private static final float CENTER_LINE_SIZE_X = WORLD_WIDTH/32f;
    private static final float CENTER_LINE_SIZE_Y = WORLD_HEIGHT/64f;
    private static final float CENTER_LINE_STEP_X = WORLD_WIDTH/16f;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private World world;
    private Rectangle groundTop;
    private Rectangle groundBottom;
    private Ball ball;
    private Paddle paddle;
    private PaddleEnemy paddleEnemy;
    private ShapeRenderer shapeRenderer;
    private FPSLogger fpsLogger;

    // Сосотояние игры
    private enum State { RUN  }
    private State state = State.RUN;
    private int paddleScore = 0;
    private int paddleEnemyScore = 0;
    private int paddleContact = 0;     // касаение с ракеткой, через 5 касаний увеливается скорость
    private boolean isPaddleGoal = false; // чей гол - нужно для определения направления старта мячика
    private boolean gameActive = false;
    private int scoreToWins;
    private Sound f_sharp_3;

    GameScreen(PongGame game, int score_to_wins) {
        this.game = game;
        this.scoreToWins = score_to_wins;
        fpsLogger = new FPSLogger();
        f_sharp_3 = Gdx.audio.newSound(Gdx.files.internal("data/pongblip_f_sharp_3.mp3"));
    }

    @Override
    public void show() {
        this.gameActive = false;

        // звуки
        final Sound f_sharp_5 = Gdx.audio.newSound(Gdx.files.internal("data/pongblip_f_sharp_5.mp3"));
        final Sound f_sharp_4 = Gdx.audio.newSound(Gdx.files.internal("data/pongblip_f_sharp_4.mp3"));

        // шрифтик
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/04b_24.ttf"));
        final FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = WORLD_HEIGHT / 10;
        font = generator.generateFont(parameter);
        generator.dispose();

        // создание камеры
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

        camera.update();
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        world = new World(new Vector2(0, 0), true);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        createObjects(); // создание объектов

        world.setContactListener(new ContactListener() { // столкновения
            @Override
            public void beginContact(Contact contact) {
               // столкновение с ракеткой противника
                if ((contact.getFixtureA().getBody() == ball.body   && contact.getFixtureB().getBody() == paddle.body) ||
                    (contact.getFixtureA().getBody() == paddle.body && contact.getFixtureB().getBody() == ball.body)) {
                    f_sharp_5.play();

                    // Вычисление точки удара шарика об ракетку
                    float contact_pos_x = contact.getWorldManifold().getPoints()[0].x - paddle.body.getPosition().x - paddle.width/2f;
                    float angle = 90 - contact_pos_x*60 / (PADDLE_SIZE_X/2f); // Вычисление угла отражения, максимальный угол = 60 гр
                    Vector2 velocity = ball.body.getLinearVelocity();
                    velocity = velocity.nor().scl(ballSpeed);
                    velocity.setAngle(angle);
                    ball.body.setLinearVelocity(velocity);
                    paddleContact += 1;
                }

               // столкновение с ракеткой противника
                if ((contact.getFixtureA().getBody() == ball.body && contact.getFixtureB().getBody() == paddleEnemy.body) ||
                    (contact.getFixtureA().getBody() == paddleEnemy.body && contact.getFixtureB().getBody() == ball.body)) {
                    f_sharp_5.play();
                    float contact_pos_x = contact.getWorldManifold().getPoints()[0].x - paddleEnemy.body.getPosition().x - paddleEnemy.width/2f;
                    float angle = 90 - contact_pos_x * 60 / (PADDLE_ENEMY_SIZE_X / 2f);
                    Vector2 speed = ball.body.getLinearVelocity();
                    speed = speed.nor().scl(ballSpeed);
                    speed.setAngle(angle);
                    ball.body.setLinearVelocity(speed);
                    paddleContact += 1;
                }

                // Столкновение со стеной
                if ((contact.getFixtureA().getBody() == ball.body && contact.getFixtureB().getBody() == groundTop.body) ||
                    (contact.getFixtureA().getBody() == groundTop.body && contact.getFixtureB().getBody() == ball.body)) {
                    f_sharp_4.play();
                }

                // Столкновение со стеной
                if ((contact.getFixtureA().getBody() == ball.body && contact.getFixtureB().getBody() == groundBottom.body) ||
                    (contact.getFixtureA().getBody() == groundBottom.body && contact.getFixtureB().getBody() == ball.body)) {
                    f_sharp_4.play();
                }
            }

            @Override
            public void endContact(Contact contact) { }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) { }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) { }
        });
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        if (!gameActive) { // старт игры
            ballSpeed = BALL_SPEED_START;
            ball.body.setTransform(BALL_POSITION_X, BALL_POSITION_Y, 0f);
            Vector2 speed = new Vector2(BALL_SPEED_X, BALL_SPEED_Y);
            speed = speed.nor().scl(ballSpeed);
            ball.body.setLinearVelocity(ball.body.getLinearVelocity().nor().scl(ballSpeed));
            if (isPaddleGoal) {
                speed.y = -speed.y;
            }
            ball.pushBall(speed);
            this.gameActive = true;
        }

        if (this.gameActive) { // процесс игры
            world.step(1 / 60f, 3, 3);
            paddle.processMovement(50f, 0f, GROUND_RIGHT_POSITION_X, Gdx.graphics.getWidth(), WORLD_WIDTH);
            paddleEnemy.processMovement(120f, 0f, GROUND_RIGHT_POSITION_X, WORLD_WIDTH);
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            groundTop.draw();
            groundBottom.draw();
            paddle.draw();
            paddleEnemy.draw();
            ball.draw();
            batch.end();

            int check_win = checkWin(); // проверка выигрыша, окончание раунда
            if (check_win != 0) {
                if (check_win == 1) {
                    paddleEnemyScore += 1;
                    isPaddleGoal = false; // компьютер забил гол
                } else {
                    paddleScore += 1;
                    isPaddleGoal = true; // игрок забил гол
                }
                f_sharp_3.play();
                paddleContact = 0; // обнулить касания
                gameActive = false;
            }

            if (paddleScore >= this.scoreToWins) { // проверка окончания игры
                game.setScreen(new FinishScreen((PongGame)game, false, this.scoreToWins));
                dispose();
                return;
            } else if (paddleEnemyScore >= this.scoreToWins) {
                game.setScreen(new FinishScreen((PongGame)game, true, this.scoreToWins));
                dispose();
                return;
            }

            if ((paddleContact+1) % 4 == 0) { // увеличение скорости при 5 ударе об ракетку
                if (ballSpeed < BALL_SPEED_MAX) {
                    ballSpeed += BALL_SPEED_INCREMENT;
                }
                ball.body.setLinearVelocity(ball.body.getLinearVelocity().nor().scl(ballSpeed));
                paddleContact++;
            }
            drawScore(paddleScore, paddleEnemyScore);
            drawCenterLine();
        }
    }

    private void createObjects() { // создание игрового мира
        // Верхняя граница
        groundTop = new Rectangle(world, shapeRenderer, GROUND_RIGHT_SIZE_X, GROUND_RIGHT_SIZE_Y);
        groundTop.body.setTransform(GROUND_RIGHT_POSITION_X, GROUND_RIGHT_POSITION_Y, 0f);

        // Нижняя ганица
        groundBottom = new Rectangle(world, shapeRenderer, GROUND_LEFT_SIZE_X, GROUND_LEFT_SIZE_Y);
        groundBottom.body.setTransform(GROUND_LEFT_POSITION_X, GROUND_LEFT_POSITION_Y, 0f);

        // Шар
        ball = new Ball(world, shapeRenderer, BALL_RADIUS);

        // Ракетка игрока
        paddle = new Paddle(world, shapeRenderer, PADDLE_SIZE_X, PADDLE_SIZE_Y);
        paddle.body.setTransform(PADDLE_POSITION_X, PADDLE_POSITION_Y, 0f);

        // Ракетка соперника
        paddleEnemy = new PaddleEnemy(world, shapeRenderer, PADDLE_ENEMY_SIZE_X, PADDLE_ENEMY_SIZE_Y, ball);
        paddleEnemy.body.setTransform(PADDLE_ENEMY_POSITION_X, PADDLE_ENEMY_POSITION_Y, 0f);
    }

    private int checkWin() { // проверка выигрыша
        float ball_y = ball.body.getPosition().y;
        if (ball_y < 0) {
            return 2;
        } else if (ball_y > WORLD_HEIGHT) {
            return 1;
        } else {
            return 0;
        }
    }

    private void drawScore(int score_1, int score_2) {
        batch.begin();
        font.draw(batch, Integer.toString(score_1), WORLD_WIDTH - WORLD_WIDTH/10f, WORLD_HEIGHT/2f + WORLD_HEIGHT/10f); // Соперник
        font.draw(batch, Integer.toString(score_2), WORLD_WIDTH - WORLD_WIDTH/10f, WORLD_HEIGHT/2f - WORLD_HEIGHT/32f); // Игрок
        batch.end();
    }

    private void drawCenterLine() {
        batch.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        float pos_x = CENTER_LINE_POSITION_X;
        for (int i = 0; i < 16; i++) {
            shapeRenderer.rect(pos_x, CENTER_LINE_POSITION_Y, CENTER_LINE_SIZE_X, CENTER_LINE_SIZE_Y);
            pos_x += CENTER_LINE_STEP_X;
        }
        shapeRenderer.end();
        batch.end();
    }


    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        world.dispose();
        batch.dispose();
        font.dispose();
    }
}
