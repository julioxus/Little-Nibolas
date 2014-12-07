package com.codamasters.screens;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.codamasters.LNHelpers.AnimatedSprite;
import com.codamasters.LNHelpers.InputHandler;
import com.codamasters.gameobjects.Horse;
import com.codamasters.gameobjects.Lanza;
import com.badlogic.gdx.physics.box2d.ContactListener;



public class PantallaActual implements Screen{
	
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private SpriteBatch batch;
	private OrthographicCamera camera;

	private final float TIMESTEP = 1 / 60f;
	private final int VELOCITYITERATIONS = 8, POSITIONITERATIONS = 3;
	
	private Array<Body> tmpBodies = new Array<Body>();
	private Horse myHorse;
	private Body ground;
	private Fixture fixtureGround;
	private Array<Lanza> lanzas = new Array<Lanza>();
	boolean muerto;
    private BitmapFont font;
    private Animation animation;
    private AnimatedSprite animatedSprite;
    private float time;
	private Random rand;
	private int minX = 4;
	private int maxX = 8;
	private int posX;
	private TextureRegion flecha;


	@Override
	public void render(float delta) {
		
		if(!muerto){
			
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATIONS);
			
			camera.position.x = myHorse.getBody().getPosition().x;
			camera.update();
		
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			animatedSprite.setBounds(myHorse.getBody().getPosition().x-myHorse.WIDTH/2, myHorse.getBody().getPosition().y-myHorse.HEIGHT/4, myHorse.WIDTH, myHorse.HEIGHT/2);
			animatedSprite.setKeepSize(true);
			animatedSprite.draw(batch);
			for (Lanza lanza : lanzas) {
				lanza.getAnimatedSprite().setBounds(lanza.getBody().getPosition().x-lanza.WIDTH/2, lanza.getBody().getPosition().y-lanza.HEIGHT/4, lanza.WIDTH, lanza.HEIGHT/2);
				lanza.getAnimatedSprite().setKeepSize(true);
				lanza.getAnimatedSprite().setOriginCenter();
				Gdx.app.log("La flecha va rotando ", lanza.getBody().getAngle()+"");
				lanza.getAnimatedSprite().setRotation((float)(lanza.getBody().getAngle()*180/Math.PI));
				lanza.getAnimatedSprite().draw(batch);
			}
			world.getBodies(tmpBodies);
			for(Body body : tmpBodies)
				if(body.getUserData() instanceof Sprite) {
					Sprite sprite = (Sprite) body.getUserData();
					sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
					sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
					sprite.draw(batch);
				}
			batch.end();
			
			myHorse.update();
			
			time+=delta;
			if(time==5*delta){
				time=0;
				rand=new Random();
				posX= minX + rand.nextInt(maxX - minX + 1);
				lanzas.add(new Lanza(world, this, posX, 10f, 1f, 0.5f));
			}


			for (Lanza lanza : lanzas) {
				if(lanza.getBody().getPosition().x < camera.position.x-camera.viewportWidth/2){
					lanza.destroy();
					lanza = new Lanza(world, this, camera.position.x+camera.viewportWidth/2, 10f, 2f, 1f);					
				}
			}
			
			debugRenderer.render(world, camera.combined);
		}
		else{
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
	        batch.begin();
	        font.setScale(0.08f);
	        font.draw(batch, " MUERTO", camera.position.x-2,camera.position.y+1);
	        batch.end();
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
	        				if(lanza.EsMortal())
	        					muerto=true;
	        			}
	        			
	        			if((lanza.getFixture() == fixtureA && fixtureGround==fixtureB ) || (lanza.getFixture() == fixtureB && fixtureGround==fixtureA )){
	        				lanza.getBody().setAngularVelocity(0);
	        				lanza.setEsMortal(false);
	        			}
	        			
	        		}
	        		
	        		if((myHorse.getFixture() == fixtureA && fixtureGround==fixtureB ) || (myHorse.getFixture() == fixtureB && fixtureGround==fixtureA )){
	        			myHorse.setNumSaltos(0);
	        		}
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
		
		float screenWidth = Gdx.graphics.getWidth();
		float screenHeight = Gdx.graphics.getHeight();
		float gameWidth = 203;
		float gameHeight = screenHeight / (screenWidth / gameWidth);
        font = new BitmapFont();
        font.setColor(Color.RED);
        time = 0;
		
		world = new World(new Vector2(0, -9.81f), true);
		debugRenderer = new Box2DDebugRenderer();
		batch = new SpriteBatch();
		TextureRegion[] sprites = TextureRegion.split(new Texture("bunny.png"), 32, 32)[0];
		flecha = new TextureRegion(new Texture("flecha.png"));
		//TextureRegion[] sprites = TextureRegion.split( new Texture("nico_sheet.png"), 632, 1368)[0];
		/*Texture tNibolas = new Texture("nico_sheet.png");
		int distancia = 1756;
		TextureRegion nibolas1 = new TextureRegion(tNibolas, 596, 232, 632, 1368);
		  //nibolas1.flip(false, true);
		  
		  TextureRegion nibolas2 = new TextureRegion(tNibolas, 596+distancia, 232, 632, 1368);
		  //nibolas2.flip(false, true);
		  
		 TextureRegion nibolas3 = new TextureRegion(tNibolas, 596+distancia*2, 232, 632, 1368);
		  //nibolas3.flip(false, true);
		  
		 TextureRegion nibolas4 = new TextureRegion(tNibolas, 596+distancia*3, 232, 632, 1368);
		  //nibolas4.flip(false, true);
		  
		 TextureRegion nibolas5 = new TextureRegion(tNibolas, 596+distancia*4, 232, 632, 1368);
		  //nibolas5.flip(false, true);
		  
		 TextureRegion nibolas6 = new TextureRegion(tNibolas, 596+distancia*5, 232, 632, 1368);
		
		 Array<TextureRegion> sprites = new Array<TextureRegion>();
		 sprites.add(nibolas1);
		 sprites.add(nibolas2);
		 sprites.add(nibolas3);
		 sprites.add(nibolas4);
		 sprites.add(nibolas5);
		 sprites.add(nibolas6);
		 */
		 
		animation = new Animation(1/12f, sprites);
		animation.setPlayMode(Animation.PlayMode.LOOP);
		animatedSprite = new AnimatedSprite(animation);
		
		Animation animFlecha = new Animation(1/2f, flecha);
		AnimatedSprite animSpriteFlecha = new AnimatedSprite(animFlecha);
		
		Lanza lan = new Lanza(world, this, 10f, 5f, 2f, 1f);
		lan.setAnimatedSprite(animSpriteFlecha);
		
		Lanza lan2 = new Lanza(world, this, 8f, 6f, 2f, 1f);
		lan2.setAnimatedSprite(animSpriteFlecha);

		lanzas.add(lan);
		lanzas.add(lan2);
		
        createCollisionListener();

		camera = new OrthographicCamera(gameWidth/10, gameHeight/10);
		
		myHorse = new Horse(world, this, 0, 1.5f, 1.5f, 1.5f);
				
		BodyDef bodyDef = new BodyDef();
		FixtureDef fixtureDef = new FixtureDef();
		
		
		// GROUND
		// body definition
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(0, 0);

		// ground shape
		ChainShape groundShape = new ChainShape();
		
		groundShape.createChain(new Vector2[] {new Vector2(-50, 0), new Vector2(50,0)});

		// fixture definition
		fixtureDef.shape = groundShape;
		fixtureDef.friction = .5f;
		fixtureDef.restitution = 0;
		fixtureDef.density = 2.5f;

		ground = world.createBody(bodyDef);
		fixtureGround = ground.createFixture(fixtureDef);
		
		
		groundShape.dispose();
		
		Gdx.input.setInputProcessor(new InputHandler(this,gameWidth/10,gameHeight/10));
		
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
		world.dispose();
		debugRenderer.dispose();
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
		muerto=false;
		lanzas.clear();
		show();
	}
	
	public boolean isMuerto(){
		return muerto;
	}

	public AnimatedSprite getSprite(){
		return animatedSprite;
	}
	
}
