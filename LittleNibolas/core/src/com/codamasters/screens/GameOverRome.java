package com.codamasters.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.codamasters.LittleNibolas;
import com.codamasters.LNHelpers.AssetLoaderSpace;
import com.codamasters.LNHelpers.AssetsLoaderRome;
import com.codamasters.tween.ActorAccessor;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameOverRome implements Screen {

	private Stage stage;
	private Skin skin;
	private Table table;
	private TweenManager tweenManager;
	private Label puntos, lHighscore;
	private Sprite splash;
	private SpriteBatch batch;
	private LittleNibolas game;
	
	public GameOverRome(LittleNibolas game){
		this.game = game;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
	

		tweenManager.update(delta);
	}

	@Override
	public void resize(int width, int height) {
	    stage.getViewport().update(width, height, false);

		table.invalidateHierarchy();
	}

	@Override
	public void show() {
		stage = new Stage(new FitViewport(1280,720));

		//Gdx.graphics.setDisplayMode((int) (Gdx.graphics.getHeight() / 1.5f), Gdx.graphics.getHeight(), false);
		Gdx.input.setInputProcessor(stage);

		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/atlas.pack"));

		table = new Table(skin);
		table.setFillParent(true);

		// creating heading
		Label heading = new Label("Game Over", skin, "big");
		heading.setFontScale(3);
		
		
		int score = AssetsLoaderRome.getScore();
		int highscore = AssetsLoaderRome.getHighScore();
		
		puntos = new Label("Puntuacion obtenida:" + score,skin);
		puntos.setFontScale(1);
		if(score > highscore){
			lHighscore = new Label("Nuevo record !!!",skin);
			lHighscore.setFontScale(1);
			AssetsLoaderRome.setHighScore(score);
			game.actionResolver.submitScore(LittleNibolas.LEADERBOARD_NV2, score);
		}else{
			lHighscore = new Label("Puntuacion maxima:" + highscore,skin);
			lHighscore.setFontScale(1);
		}
		
		// creating buttons
		TextButton buttonPlay = new TextButton("Reintentar", skin,"big");
		buttonPlay.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.addAction(sequence(moveTo(0, -stage.getHeight(), .5f), run(new Runnable() {

					@Override
					public void run() {
						((Game) Gdx.app.getApplicationListener()).setScreen(new ScreenRome(game));
					}
				})));
			}
		});
		buttonPlay.pad(10, 60, 10, 60);

		TextButton buttonSettings = new TextButton("Volver al menu", skin,"big");
		buttonSettings.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.addAction(sequence(moveTo(0, -stage.getHeight(), .5f), run(new Runnable() {
					
					@Override
					public void run() {
						AssetLoaderSpace.music_menu.play();
						//AssetLoaderSpace.estrellado.stop();
						
						((Game) Gdx.app.getApplicationListener()).setScreen(new LevelMenu(game));
					}
				})));
			}
		});
		buttonSettings.pad(10);

		// putting stuff together
		table.add(heading).spaceBottom(50).row();
		table.add(puntos).spaceBottom(10).row();
		table.add(lHighscore).spaceBottom(20).row();
		table.add(buttonPlay).spaceBottom(15).row();
		table.add(buttonSettings).spaceBottom(15).row();

		stage.addActor(table);

		// creating animations
		tweenManager = new TweenManager();
		Tween.registerAccessor(Actor.class, new ActorAccessor());

		// heading color animation
		Timeline.createSequence().beginSequence()
				.push(Tween.to(heading, ActorAccessor.RGB, .5f).target(1, 0, 0))

				.end().repeat(Tween.INFINITY, 0).start(tweenManager);

		// heading and buttons fade-in
		Timeline.createSequence().beginSequence()
				.push(Tween.set(buttonPlay, ActorAccessor.ALPHA).target(0))
				.push(Tween.set(buttonSettings, ActorAccessor.ALPHA).target(0))
				.push(Tween.from(heading, ActorAccessor.ALPHA, .25f).target(0))
				.push(Tween.to(buttonPlay, ActorAccessor.ALPHA, .25f).target(1))
				.push(Tween.to(buttonSettings, ActorAccessor.ALPHA, .25f).target(1))
				.end().start(tweenManager);

		// table fade-in
		Tween.from(table, ActorAccessor.ALPHA, .75f).target(0).start(tweenManager);
		Tween.from(table, ActorAccessor.Y, .75f).target(Gdx.graphics.getHeight() / 8).start(tweenManager);

		tweenManager.update(Gdx.graphics.getDeltaTime());
		
		game.intentos++;
		if(game.intentos > 1){
			game.actionResolver.showOrLoadInterstital();
			game.intentos = 0;
		}
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();

	}

}