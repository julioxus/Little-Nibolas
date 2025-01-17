package com.codamasters.screens;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.codamasters.LittleNibolas;
import com.codamasters.LNHelpers.AnimatedSprite;
import com.codamasters.LNHelpers.AssetsLoaderRome;
import com.codamasters.LNHelpers.InputHandlerRome;
import com.codamasters.gameobjects.Escudo;
import com.codamasters.gameobjects.Horse;
import com.codamasters.gameobjects.Lanza;
import com.codamasters.gameobjects.Plataforma;
import com.codamasters.gameobjects.Soldado;
import com.badlogic.gdx.physics.box2d.ContactListener;




public class ScreenRome implements Screen{
	
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private SpriteBatch batch, batch2;
	private OrthographicCamera camera, camera2;

	private final float TIMESTEP = 1 / 60f;
	private final int VELOCITYITERATIONS = 8, POSITIONITERATIONS = 3;
	
	private Array<Body> tmpBodies = new Array<Body>();
	private Horse myHorse;
	private Body ground;
	private Fixture fixtureGround;
	private Array<Lanza> lanzas = new Array<Lanza>();
	private Array<Soldado> soldados = new Array<Soldado>();
    private float time=0.0f;
    private float timePlatform =0.0f;
	private Random rand;
	private int minX = 4;
	private int maxX = 8;
	private int minPlatX = -4;
	private int maxPlatX = 4;
	private int minTiempoPlataforma = 120;
	private int maxTiempoPlataforma = 240;
	private float minY=10;
	private float maxY=15;
	private float posY;
	private int posX;
	private Plataforma plataforma;
	private boolean direc, win;
	private Soldado sold;
	private static Preferences prefs;
	private static int score=0;
	private int tiempoPlataforma;
	private float screenWidth;
	private float screenHeight;
	private float gameWidth;
	private float gameHeight;
	private int midPointY;
	private Escudo escudo;
	private boolean recogido;
	private float tiempoEscudo;
	private boolean primerEscudo;
	private int minEscudoX = -9;
	private int maxEscudoX = 9;
	private int minTiempoEscudo = 200;
	private int maxTiempoEscudo = 400;
	private int tiempoAparicionEscudo;
	private float tiempoTexto = 0;

	
	private LittleNibolas game;
	
	public ScreenRome(LittleNibolas game){
		this.game = game;
	}
	

	@Override
	public void render(float delta) {
		
		if(myHorse.getVidas()>0){
			
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATIONS);
			
			//camera.position.x = myHorse.getBody().getPosition().x;
			camera.update();
			camera2.update();
		
			batch.setProjectionMatrix(camera.combined);
			batch2.setProjectionMatrix(camera2.combined);
			
			
		
			batch.begin();
			
			
			batch.draw(AssetsLoaderRome.background, camera.position.x-camera.viewportWidth/2, camera.position.y-camera.viewportHeight/2, camera.viewportWidth, camera.viewportHeight);
			AssetsLoaderRome.animatedSprite.setBounds(myHorse.getBody().getPosition().x-myHorse.WIDTH/2, myHorse.getBody().getPosition().y-myHorse.HEIGHT/2, myHorse.WIDTH, myHorse.HEIGHT);
			//animatedSprite.setBounds(myHorse.getBody().getPosition().x, myHorse.getBody().getPosition().y,myHorse.WIDTH*1.4f, myHorse.HEIGHT);
			AssetsLoaderRome.animatedSprite.setKeepSize(true);
			AssetsLoaderRome.animatedSprite.draw(batch);
			
			if(myHorse.getVidas()==2){
				escudo.destroy();
				//escudo = new Escudo(world, this, camera.position.x-3*camera.viewportWidth/7, camera.position.y+3*camera.viewportHeight/7, 1f, 1f);
				escudo = new Escudo(world, this, camera.position.x, -10, 1f, 1f);
				escudo.setAnimatedSprite(AssetsLoaderRome.animSpriteEscudo);
				escudo.getAnimatedSprite().setBounds(camera.position.x-3*camera.viewportWidth/7, camera.position.y+3*camera.viewportHeight/8, escudo.HEIGHT/2, escudo.WIDTH);
				escudo.getAnimatedSprite().setKeepSize(true);
				escudo.getAnimatedSprite().draw(batch);	
			}
			else{
				escudo.getAnimatedSprite().setBounds(escudo.getBody().getPosition().x - escudo.WIDTH/2, escudo.getBody().getPosition().y-escudo.HEIGHT/4, escudo.HEIGHT/2, escudo.WIDTH);
				escudo.getAnimatedSprite().setKeepSize(true);
				escudo.getAnimatedSprite().draw(batch);	
			}
			
			
			for (Lanza lanza : lanzas) {
				lanza.getAnimatedSprite().setBounds(lanza.getBody().getPosition().x-lanza.WIDTH/2, lanza.getBody().getPosition().y-lanza.HEIGHT/4, lanza.WIDTH, lanza.HEIGHT/2);
				lanza.getAnimatedSprite().setKeepSize(true);
				lanza.getAnimatedSprite().setOriginCenter();
				lanza.getAnimatedSprite().setRotation((float)(lanza.getBody().getAngle()*180/Math.PI));
				lanza.getAnimatedSprite().draw(batch);
			}
			plataforma.getAnimatedSprite().setBounds(plataforma.getBody().getPosition().x-plataforma.WIDTH/2, plataforma.getBody().getPosition().y-plataforma.HEIGHT/20, plataforma.WIDTH, plataforma.HEIGHT/12);
			plataforma.getAnimatedSprite().setKeepSize(true);
			plataforma.getAnimatedSprite().draw(batch);
			

				
			world.getBodies(tmpBodies);
			for(Body body : tmpBodies)
				if(body.getUserData() instanceof Sprite) {
					Sprite sprite = (Sprite) body.getUserData();
					sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
					sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
					sprite.draw(batch);
				}
			
			/*
			sold.getAnimatedSprite().setBounds(sold.getBody().getPosition().x-sold.WIDTH/2, sold.getBody().getPosition().y-sold.HEIGHT/4, sold.WIDTH, sold.HEIGHT/2);
			sold.getAnimatedSprite().setKeepSize(true);
			sold.getAnimatedSprite().draw(batch);
			*/
			
			batch.end();

		    String scoreText = score + "";
			
		    batch2.begin();
			AssetsLoaderRome.font.setScale(0.25f);
			AssetsLoaderRome.font.draw(batch2, "" + score, camera.position.x-scoreText.length()/2,camera.position.y+camera.viewportHeight*4);
		    batch2.end();
			
			myHorse.update();
			
			
			time+=delta;
			if(time>60*5*delta && lanzas.size < 10){
				time=0;
				posX= minX + rand.nextInt(maxX - minX + 1);
				posY= minY + rand.nextFloat()*maxX;
				Lanza lan = new Lanza(world, this, posX+camera.position.x+camera.viewportWidth/2+posX, posY, 1f, 0.5f);
				lan.setAnimatedSprite(AssetsLoaderRome.animSpriteFlecha);
				lanzas.add(lan);
			}			
			for (Lanza lanza : lanzas) {
				if( ( lanza.getBody().getPosition().x < camera.position.x-camera.viewportWidth/2) || (lanza.getBody().getLinearVelocity().y == 0)){
					lanza.destroy();
					posX= minX + rand.nextInt(maxX - minX + 1);
					posY= minY + rand.nextFloat()*maxX;
					lanza = new Lanza(world, this, camera.position.x+camera.viewportWidth/2+posX, posY, 1f, 0.5f);
					AssetsLoaderRome.arrow.play();
					addScore();
					}
			}
			
			timePlatform+=delta;
			
			
			if(timePlatform>tiempoPlataforma*delta){
				timePlatform=0;
				tiempoPlataforma = minTiempoPlataforma + rand.nextInt(maxTiempoPlataforma - minTiempoPlataforma + 1);
				plataforma.destroy();
				
				posX= minPlatX + rand.nextInt(maxPlatX - minPlatX + 1);
				plataforma = new Plataforma(world, this, posX, -3f, 3f, 1f);
				plataforma.setAnimatedSprite(AssetsLoaderRome.animSpritePlataforma);
			}
			
			
			tiempoEscudo+=delta;
			
			if(tiempoEscudo>tiempoAparicionEscudo*delta && myHorse.getVidas()==1){
				tiempoEscudo=0;
				primerEscudo=true;
				escudo.destroy();
				//escudo = new Escudo(world, this, camera.position.x-3*camera.viewportWidth/7, camera.position.y+3*camera.viewportHeight/7, 1f, 1f);
				int posEscudoX= minEscudoX + rand.nextInt(maxEscudoX - minEscudoX + 1);
				while( (posEscudoX > myHorse.getBody().getPosition().x && posEscudoX < myHorse.getBody().getPosition().x+2 ) ||
				    (posEscudoX < myHorse.getBody().getPosition().x && posEscudoX > myHorse.getBody().getPosition().x-2 )	){
					posEscudoX= minEscudoX + rand.nextInt(maxEscudoX - minEscudoX + 1);
				}
				
				escudo = new Escudo(world, this, posEscudoX, -4.5f, 1f, 1f);
				escudo.setAnimatedSprite(AssetsLoaderRome.animSpriteEscudo);
			}
			
			if(win && tiempoTexto < 2){
				tiempoTexto+=delta;
				batch2.begin();
				AssetsLoaderRome.font.setScale(0.25f);
			    AssetsLoaderRome.font.draw(batch2, "SELFIE CONSEGUIDA", -80,0);
			    batch2.end();
			}
			
			if(score==150 && !win){
				game.actionResolver.unlockAchievement(LittleNibolas.ACHIEVEMENT2);
				AssetsLoaderRome.win.play();
				win = true;
			}
			
			if(score==500){
				game.actionResolver.unlockAchievement(LittleNibolas.ACHIEVEMENT5);
			}
		
    		//for (Soldado sold : soldados) {
    			    			
    			// direccion --> TRUE : VOY A LA IZQUIERDA
    			// direccion --> FALSE: VOY A LA DERECHA
    			/*
    			sold.update();


				if( ( ( sold.getBody().getPosition().x < camera.position.x-camera.viewportWidth/2-5) && sold.getDireccion() )
						|| ( sold.getBody().getPosition().x > camera.position.x+camera.viewportWidth/2+5) && !sold.getDireccion()){
					Gdx.app.log("HE LLEGADO AL LIMITE", "");
					sold.destroy();
					//soldados.removeIndex(soldados.size-1);
					direc = rand.nextBoolean();
					*/
					//if(direc)
					//	Gdx.app.log("VIENE DE DERECHA!!!","");
					//else
					//	Gdx.app.log("VIENE DE IZQUIERDA!!!","");
					
					/*
					if(direc)
						 sold = new Soldado(world, this, camera.position.x+camera.viewportWidth/2+4, -6.6f, 0.8f, 0.8f, direc);
					else
						 sold = new Soldado(world, this, camera.position.x-camera.viewportWidth/2-4, -6.6f, 0.8f, 0.8f, direc);		
					**/
					//soldados.add(sold);
	    		//}
				

				
    		//}

			
			//Gdx.app.log("NUMERO DE FLECHAS", lanzas.size+"");
			
			//debugRenderer.render(world, camera.combined);
			//if(score>10){
				//music_R.stop();
				//((Game) Gdx.app.getApplicationListener()).setScreen((new FinSegundoNivel1()));
			//}
		}
		else{
			
			/*
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
	        batch.begin();
	        font.setScale(0.08f);
	        font.draw(batch, " MUERTO", camera.position.x-2,camera.position.y+1);
	        batch.end();
	        */
						
		AssetsLoaderRome.setScore(score);
		AssetsLoaderRome.music_R.stop();
		AssetsLoaderRome.reloadNibolas();
		
		if(win)
			((Game) Gdx.app.getApplicationListener()).setScreen((new CongratsRome(game)));
		else
			((Game) Gdx.app.getApplicationListener()).setScreen((new GameOverRome(game)));

		}
		
	}
	
	 private void createCollisionListener() {
	        world.setContactListener(new ContactListener() {
	        	
	            @Override
	            public void preSolve(Contact contact, Manifold oldManifold) {
	            }

	            @Override
	            public void postSolve(Contact contact, ContactImpulse impulse) {
	            }


	            @Override
	            public void beginContact(Contact contact) {
	                Fixture fixtureA = contact.getFixtureA();
	                Fixture fixtureB = contact.getFixtureB();
	                
	        		for (Lanza lanza : lanzas) {
	        			
	        			if(( lanza.getFixture() == fixtureA && myHorse.getFixture()==fixtureB ) || ( lanza.getFixture() == fixtureB && myHorse.getFixture()==fixtureA ) ){
	        				if(lanza.EsMortal()){
	        					AssetsLoaderRome.impact.play();
	        					myHorse.setVidas(myHorse.getVidas()-lanza.DANIO);
	        					recogido=false;
	        					tiempoEscudo=0;

	        				}
	        				lanza.getBody().setAngularVelocity(0);
	        			}
	        			
	        			if((lanza.getFixture() == fixtureA && fixtureGround==fixtureB ) || (lanza.getFixture() == fixtureB && fixtureGround==fixtureA )){
	        				lanza.getBody().setAngularVelocity(0);
	        				lanza.setEsMortal(false);
	        			}
	        			
	        			if((lanza.getFixture() == fixtureA && plataforma.getFixture()==fixtureB ) || (lanza.getFixture() == fixtureB && plataforma.getFixture()==fixtureA )){
	        				lanza.getBody().setAngularVelocity(0);
	        			}
	        			
	        			
	        			
	        			if(!recogido){
		        			if((lanza.getFixture() == fixtureA && escudo.getFixture()==fixtureB ) || (lanza.getFixture() == fixtureB && escudo.getFixture()==fixtureA )){
		        				lanza.getBody().setAngularVelocity(0);
		        			}
	        			}
	        			
	        			
	        			/*
	        			
	        			if((lanza.getFixture() == fixtureA && sold.getFixture()==fixtureB ) || (lanza.getFixture() == fixtureB && sold.getFixture()==fixtureA )){
	        				lanza.getBody().setAngularVelocity(0);
	        				lanza.setEsMortal(false);
	        			}
	        			*/
	        			

	        		}
	        		
	        		if((myHorse.getFixture() == fixtureA && fixtureGround==fixtureB ) || (myHorse.getFixture() == fixtureB && fixtureGround==fixtureA )){
	        			myHorse.setNumSaltos(0);
	        		}
	        		
	        		
        			if(!recogido){
		        		if((myHorse.getFixture() == fixtureA && escudo.getFixture()==fixtureB ) || (myHorse.getFixture() == fixtureB && escudo.getFixture()==fixtureA )){
		        			recogido=true;
		        			myHorse.setVidas(myHorse.getVidas()+escudo.VIDA);
		        			AssetsLoaderRome.shield.play();
		        		}
        			}
        			
	        
	        		//for (Soldado sold : soldados) {
	        		
	        		//	if(( sold.getFixture() == fixtureA && myHorse.getFixture()==fixtureB ) || ( sold.getFixture() == fixtureB && myHorse.getFixture()==fixtureA ) ){
	        		//			muerto=true;
	        		//	}
	        		//}

	        		
	            }

	            @Override
	            public void endContact(Contact contact) {
	                Fixture fixtureA = contact.getFixtureA();
	                Fixture fixtureB = contact.getFixtureB();
	            }

	        });
	    }

	 
	@Override
	public void resize(int width, int height) {
		//camera.viewportWidth = width / 25;
		//camera.viewportHeight = height / 25;
		
	}

	@Override
	public void show() {
		
		screenWidth = 980;
		screenHeight = 720;
		gameWidth = 203;
		gameHeight = screenHeight / (screenWidth / gameWidth);
        midPointY = (int) (gameHeight / 2);

        time = 0;
        score = 0;
        tiempoEscudo=0;
        win = false;
		
        AssetsLoaderRome.music_R.play();
		AssetsLoaderRome.reloadNibolas(); // AAAAAAAAAHHHHHHHH !!!!!!!!!!!!!!!
		
		world = new World(new Vector2(0, -4.9f), true);
		debugRenderer = new Box2DDebugRenderer();
		batch = new SpriteBatch();
		batch2 = new SpriteBatch();
	

		rand=new Random();
		posX= minX + rand.nextInt(maxX - minX + 1);
		posY= minY + rand.nextFloat()*maxX;
        tiempoAparicionEscudo = minTiempoEscudo + rand.nextInt(maxTiempoEscudo - minTiempoEscudo + 1);

		Lanza lan = new Lanza(world, this, posX, posY, 1f, 0.5f);
		lan.setAnimatedSprite(AssetsLoaderRome.animSpriteFlecha);

		lanzas.add(lan);
		
		camera = new OrthographicCamera(gameWidth/10, gameHeight/10);
		camera2 = new OrthographicCamera(gameWidth, gameHeight);
		
		myHorse = new Horse(world, this, 0, -5.95f, 1f, 2f);
				
		BodyDef bodyDef = new BodyDef();
		FixtureDef fixtureDef = new FixtureDef();
	
		plataforma = new Plataforma(world, this, 5, -3f, 3f, 1f);
		plataforma.setAnimatedSprite(AssetsLoaderRome.animSpritePlataforma);
		tiempoPlataforma = minTiempoPlataforma + rand.nextInt(maxTiempoPlataforma - minTiempoPlataforma + 1);
		
		escudo = new Escudo(world, this, -4, -10f, 1f, 1f);
		escudo.setAnimatedSprite(AssetsLoaderRome.animSpriteEscudo);
		recogido=false;
		primerEscudo=false;

		//Soldado sold;
		/*
		direc= rand.nextBoolean();
		if(direc)
			 sold = new Soldado(world, this, 10, -6.6f, 0.8f, 0.8f, direc);
		else
			 sold = new Soldado(world, this, -10, -6.6f, 0.8f, 0.8f, direc);
		
		sold.setAnimatedSprite(animatedSprite);

			*/
		//soldados.add(sold);
		
		// GROUND
		// body definition
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(0, 0);

		// ground shape
		ChainShape groundShape = new ChainShape();
		
		groundShape.createChain(new Vector2[] {new Vector2(-50, -6), new Vector2(50,-6)});

		// fixture definition
		fixtureDef.shape = groundShape;
		fixtureDef.friction = .5f;
		fixtureDef.restitution = 0;
		fixtureDef.density = 2.5f;

		ground = world.createBody(bodyDef);
		fixtureGround = ground.createFixture(fixtureDef);
		
		Plataforma izquierda = new Plataforma(world, this, camera.position.x-camera.viewportWidth/2-0.5f, -6f, 1f, 1f);
		Plataforma derecha = new Plataforma(world, this, camera.position.x+camera.viewportWidth/2+0.5f, -6f, 1f, 1f);

        createCollisionListener();

		
		groundShape.dispose();
		
		
		Gdx.input.setInputProcessor(new InputHandlerRome(this,gameWidth/10,gameHeight/10));
		
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

	public Horse getHorse(){
		return myHorse;
	}
	
	public void setCamera(OrthographicCamera camera){
		this.camera = camera;
	}
	
	public OrthographicCamera getCamera(){
		return camera;
	}
	
	public World getWorld(){
		return world;
	}
	
	public void restart(){
		myHorse.setVidas(1);
		lanzas.clear();
		show();
	}
	
	public AnimatedSprite getSprite(){
		return AssetsLoaderRome.animatedSprite;
	}
	
	   
	
	public void addScore(){
		score+=1;
	}
	
	@Override
	public void dispose() {
		world.dispose();
		debugRenderer.dispose();
	}
	
}
