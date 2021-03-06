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

class FinishScreen implements Screen { // экран окончания игры
    private static final int WORLD_WIDTH  = PongGame.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = PongGame.WORLD_HEIGHT;
    private boolean isPlayerWins;
    private int scoreToWins;
    private Game game;
    private OrthographicCamera camera;
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font, font_big;

    FinishScreen(PongGame game, boolean is_player_wins, int score_to_wins) {
        this.game = game;
        this.isPlayerWins = is_player_wins;
        this.scoreToWins  = score_to_wins;
    }

    @Override
    public void show() {
        final int score_to_wins = this.scoreToWins;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        camera.update();
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        // Звуки
        final Sound f_sharp_5 = Gdx.audio.newSound(Gdx.files.internal("data/pongblip_f_sharp_5.mp3"));

        // шрифт
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/04b_24.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 60;
        font = generator.generateFont(parameter);

        parameter.size = 70;
        font_big = generator.generateFont(parameter);
        generator.dispose();

        Label.LabelStyle label_style = new Label.LabelStyle();
        label_style.font = font;
        label_style.fontColor = Color.WHITE;

        Label.LabelStyle label_big_style = new Label.LabelStyle();
        label_big_style.font = font_big;
        label_big_style.fontColor = Color.WHITE;

        Label label_player_wins = new Label("PLAYER WIN", label_big_style);

        Label label_device_wins = new Label("YOU LOSE", label_big_style);

        Label label_play_again = new Label("PLAY AGAIN", label_style);
        label_play_again.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                f_sharp_5.play();
                game.setScreen(new GameScreen((PongGame) game, score_to_wins));
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

        final Table table = new Table();
        table.setPosition(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);
        if (isPlayerWins) {
            table.add(label_player_wins).expandX().center().pad(WORLD_HEIGHT / 32);
        } else {
            table.add(label_device_wins).expandX().center().pad(WORLD_HEIGHT / 32);
        }
        table.row();
        table.add(label_play_again).expandX().center().pad(WORLD_HEIGHT / 32);
        table.row();
        table.add(label_exit).expandX().center();
        stage = new Stage();
        stage.getViewport().setCamera(camera);
        Gdx.input.setInputProcessor(stage);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1000);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);;
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height, false);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        font.dispose();
    }
}
