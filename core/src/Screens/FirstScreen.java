package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import pong.game.PongGame;

public class FirstScreen implements Screen {
    private static final int WORLD_WIDTH  = PongGame.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = PongGame.WORLD_HEIGHT;
    private Game game;
    private OrthographicCamera camera;
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font, font_big;

    public FirstScreen(PongGame _game) {
        game = _game;
    }
    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        camera.update();
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        final Sound f_sharp_5 = Gdx.audio.newSound(Gdx.files.internal("data/pongblip_f_sharp_5.mp3")); // звук выбора
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/04b_24.ttf")); // установка шрифта
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 60;
        font = generator.generateFont(parameter);
        parameter.size = 80;
        font_big = generator.generateFont(parameter);
        generator.dispose();
        Label.LabelStyle label_style = new Label.LabelStyle();
        label_style.font = font;
        label_style.fontColor = Color.WHITE;
        Label.LabelStyle label_big_style = new Label.LabelStyle();
        label_big_style.font = font_big;
        label_big_style.fontColor = Color.WHITE;

        Label label_pong = new Label("NOHG", label_big_style);

        Label label_1_player = new Label("PLAY TO:", label_style);

        Label label_1_win = new Label("3 WINS", label_style);
        label_1_win.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) { // кнопка
                f_sharp_5.play();
                game.setScreen(new GameScreen((PongGame) game, 3));
                dispose();
            }
        });

        Label label_3_wins = new Label("5 WINS", label_style);
        label_3_wins.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                f_sharp_5.play();
                game.setScreen(new GameScreen((PongGame) game, 5));
                dispose();
            }
        });

        Label label_5_wins = new Label("10 WINS", label_style);
        label_5_wins.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                f_sharp_5.play();
                game.setScreen(new GameScreen((PongGame) game, 10));
                dispose();
            }
        });

        Label label_exit = new Label("EXIT", label_style);
        label_exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                f_sharp_5.play();
                Gdx.app.exit();
            }
        });

        final Table table = new Table(); // таблица для отображения всех надписей
        table.setPosition(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);
        table.add(label_pong).expandX().center().pad(WORLD_HEIGHT / 32);
        table.row();
        table.add(label_1_player).expandX().center().pad(WORLD_HEIGHT / 32);
        table.row();
        table.add(label_1_win).expandX().center().pad(WORLD_HEIGHT / 32);
        table.row();
        table.add(label_3_wins).expandX().center().pad(WORLD_HEIGHT / 32);
        table.row();
        table.add(label_5_wins).expandX().center().pad(WORLD_HEIGHT / 32);
        table.row();
        table.add(label_exit).expandX().center();

        stage = new Stage();
        stage.getViewport().setCamera(camera);
        Gdx.input.setInputProcessor(stage);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        camera.update();
        stage.getViewport().setCamera(camera);
        stage.getViewport().update(width,height, false);
        stage.getCamera().update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        font.dispose();
        font_big.dispose();
    }
}
