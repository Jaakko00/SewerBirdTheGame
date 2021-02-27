package fi.tamk.tiko;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;

	public static final boolean DEBUG_PHYSICS = false;
	public static final float WORLD_WIDTH = 8.0f;
	public static final float WORLD_HEIGHT = 4.8f;
	public static Texture birdTexture;
	public static Texture birdTextureFlapped;
	public static Texture backgroundTexture;
	public static Texture pipeTexture;
	public static Texture pipe1;
	public static Texture pipe2;
	public static Texture pipe3;
	public static Texture pipe4;
	public static Texture heartThree;
	public static Texture heartTwo;
	public static Texture heartOne;
	public static Texture heartZero;
	public static Texture gameoverScreen;
	public static Texture instructionsTexture;
	public static Texture logoTexture;
	public static Texture bgpipesTexture;
	public static Texture filterTexture;
	public static Texture heartTexture;
	private Sound wingSound;
	private Sound hitSound;
	private Sound scoreSound;
	private Music backgroundMusic;
	public float speed = -2f;

	//test

	public BitmapFont font;

	public Preferences prefs;

	public Instructions instructions1;
	public Logo logo;
	public Feathers feathers;
	public Filter mistFilter;
	public Hearts hearts;

	//Stores the players score, which is calculated by amount of pipes jumped over
	public double score = 0;
	public int highscore = 0;

	//If !started, the bird will hover stationary, and the map won't move
	public boolean started = false;

	//Used for idle movement before the game has started
	float idleMovementY = 2.3f;
	float idleMovementX = 2.0f;
	boolean goUpY = true;
	boolean goUpX = true;

	//Used to store the position of death for the particle effects to spawn into
	float positionOfDeathX = 0;
	float positionOfDeathY = 0;

	//Stores whether the bird is alive or not, changing this back to true will start moving the map again
	public boolean alive = true;

	//Used to count lives, if all lost, the game will start at idle
	public int lives = 3;
	public boolean lostLife = false;


	private OrthographicCamera camera;
	private Box2DDebugRenderer debugRenderer;

	private World world;
	private Body birdBody;


	Array<Body> bodies = new Array<Body>();
	Array<Background> backgrounds = new Array<Background>();
	Array<BGPipes> backgroundPipes = new Array<>();


	public enum GameObjectType {
		BIRD,
		PIPE
	}

	class GameObjectInfo {
		public Texture texture;
		public int textureID;
		public float radius;
		public float width;
		public float height;
		public boolean hasSpawnedPipe;
		public boolean flipped;
		public boolean jumpedOver;
		public boolean hasTexture;

		GameObjectType type;

		public GameObjectInfo(Texture t, float r, GameObjectType got, float w, float h, boolean flippedOrNah, boolean hasSpawnedPipeOrNah) {
			texture = t;
			textureID = 1;
			radius = r;
			type = got;
			width = w;
			height = h;
			hasSpawnedPipe = hasSpawnedPipeOrNah; //Every pipe spawns another when passed over a certain line, one pipe can spawn only one pipe
			flipped = flippedOrNah; //Used on pipes, whether the texture should be flipped or not
			jumpedOver = false; //Used on pipes, turns true when player has jumped over it, so you can't get duplicate points from pipes
			hasTexture = false;
		}
	}

	GameObjectInfo birdGameObject;


	@Override
	public void create () {
		batch = new SpriteBatch();

		//Textures
		birdTexture = new Texture("green1.png");
		birdTextureFlapped = new Texture("green2.png");
		pipeTexture = new Texture("pipe.png");
		pipe1 = new Texture("pipe.png");
		pipe2 = new Texture("pipe2.png");
		pipe3 = new Texture("pipe3.png");
		pipe4 = new Texture("pipe4.png");

		heartThree = new Texture("3lives.png");
		heartTwo = new Texture("2lives.png");
		heartOne = new Texture("1live.png");
		heartZero = new Texture("0lives.png");

		gameoverScreen = new Texture("gameover.png");
		instructionsTexture = new Texture("instructions.png");
		logoTexture = new Texture("logo.png");
		backgroundTexture = new Texture("background.png");
		bgpipesTexture = new Texture("backgroundPipes.png");
		filterTexture = new Texture("filter.png");
		heartTexture = new Texture("3lives.png");

		font = new BitmapFont(Gdx.files.internal("*.fnt"), false);
		font.setUseIntegerPositions(false);

		prefs = Gdx.app.getPreferences("SewerbirdPref");

		instructions1 = new Instructions();
		instructions1.setX(0);
		instructions1.setY(0);

		logo = new Logo();
		logo.setX(0);
		logo.setY(0);

		mistFilter = new Filter();
		mistFilter.setX(0);
		mistFilter.setY(0);

		hearts = new Hearts();
		hearts.setX(-2.8f);
		hearts.setY(0);

		feathers = new Feathers();

		//Sounds
		wingSound = Gdx.audio.newSound(Gdx.files.internal("wing.mp3"));
		hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.mp3"));
		scoreSound = Gdx.audio.newSound(Gdx.files.internal("score.mp3"));
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(0.1f);
		backgroundMusic.play();

		backgrounds.add(new Background(), new Background());
		backgrounds.get(0).setX(0);
		backgrounds.get(1).setX(WORLD_WIDTH);

		backgroundPipes.add(new BGPipes(), new BGPipes());
		backgroundPipes.get(0).setX(0);
		backgroundPipes.get(1).setX(WORLD_WIDTH);

		/*
		Time of day, currently not used
		Date date = new Date();
		Calendar calendarG = new GregorianCalendar();
		calendarG.setTime(date);
		int minutes = calendarG.get(Calendar.MINUTE);
		int hours = calendarG.get(Calendar.HOUR_OF_DAY);
		 */



		birdGameObject = new GameObjectInfo(birdTexture, 0.3f, GameObjectType.BIRD, 0.6f, 0.6f, false, false);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

		debugRenderer = new Box2DDebugRenderer();

		world = new World(new Vector2(0, -9.8f), true);

		birdBody = createBody(2f, WORLD_HEIGHT / 2, true, 0.4f, 0);
		birdBody.setUserData(birdGameObject);
		birdBody.setFixedRotation(true);

		createGround();

		pipeObst();

		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				// Game objects collide with each other

				// Let's get user data from both of the objects
				// We do not know the order:
				Object userData1 = contact.getFixtureA().getBody().getUserData();
				Object userData2 = contact.getFixtureB().getBody().getUserData();

				if(userData1 != null) {
					GameObjectInfo data1 = (GameObjectInfo) userData1;
					if(data1.type == GameObjectType.BIRD) {
						if(alive == true) {
							hitSound.play();
						}
						birdBody.setFixedRotation(false);
						alive = false;
					}
				}
				if(userData2 != null) {
					GameObjectInfo data2 = (GameObjectInfo) userData2;
					if(data2.type == GameObjectType.BIRD) {
						if(alive) {
							hitSound.play();
						}
						birdBody.setFixedRotation(false);
						alive = false;
					}
				}
			}

			@Override
			public void endContact(Contact contact) {
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {

			}

		});
	}

	public static Texture birdTexture() {
		//Texture green = new Texture("green1.png");
		Texture test = birdTexture;
		return test;
	}

	/**
	 * Randomises the pipe texture
	 * @return texture of the pipe
	 */
	public static Texture pipeTexture() {
		Texture p1 = pipe1;
		Texture p2 = pipe2;
		Texture p3 = pipe3;
		Texture p4 = pipe4;

		float color = MathUtils.random(0f, 4f);
		if(color < 1) {
			return p3;
		} else if(color < 2 && color >= 1) {
			return p2;
		} else if(color < 2.5 && color >= 2) {
			return p4;
		} else {
			return p1;
		}
	}

	/**
	 * Creates a body
	 * @param x position
	 * @param y position
	 * @param dynOrNah is it dynamic or kinematic
	 * @param width
	 * @param height
	 * @return
	 */
	private Body createBody(float x, float y, boolean dynOrNah, float width, float height) {
		Body playerBody;
		if(dynOrNah) {
			playerBody = world.createBody(getDefinitionOfBody(x, y));
			playerBody.createFixture(getFixtureDefinition(width / 2));
		} else {
			playerBody = world.createBody(getDefinitionOfBodyKin(x, y));
			playerBody.createFixture(getFixtureDefinitionKin(width, height));
		}
		return playerBody;
	}

	private BodyDef getDefinitionOfBody(float x, float y) {
		// Body Definition
		BodyDef myBodyDef = new BodyDef();

		// It's a body that moves
		myBodyDef.type = BodyDef.BodyType.DynamicBody;

		// Initial position is centered up
		// This position is the CENTER of the shape!
		myBodyDef.position.set(x, y);

		return myBodyDef;
	}
	private BodyDef getDefinitionOfBodyKin(float x, float y) {
		// Body Definition
		BodyDef myBodyDef = new BodyDef();

		// It's a body that moves
		myBodyDef.type = BodyDef.BodyType.KinematicBody;

		// Initial position is centered up
		// This position is the CENTER of the shape!
		myBodyDef.position.set(x, y);

		return myBodyDef;
	}

	private FixtureDef getFixtureDefinition(float radius) {
		FixtureDef playerFixtureDef = new FixtureDef();

		// Mass per square meter (kg^m2)
		playerFixtureDef.density = 1;

		// How bouncy object? Very bouncy [0,1]
		playerFixtureDef.restitution = 0.5f;

		// How slipper object? [0,1]
		playerFixtureDef.friction = 0.5f;

		// Create circle shape.
		CircleShape circleshape = new CircleShape();
		circleshape.setRadius(radius);

		// Add the shape to the fixture
		playerFixtureDef.shape = circleshape;

		return playerFixtureDef;
	}
	private FixtureDef getFixtureDefinitionKin(float width, float height) {
		FixtureDef playerFixtureDef = new FixtureDef();

		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(width / 2, height / 2);

		// Add the shape to the fixture
		playerFixtureDef.shape = polygonShape;

		return playerFixtureDef;
	}

	private void clearScreen(float r, float g, float b) {
		Gdx.gl.glClearColor(r, g, b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void render () {
		batch.setProjectionMatrix(camera.combined);
		clearScreen(0,0,0);

		world.getBodies(bodies);

		batch.begin();


		//Moves the background, background pipes, logo, and instructions
		moveBackground();

		//Draws the ground and the bird
		drawAllBodies();

		//Tests if the player is alive and whether the game has started or not and functions accordingly
		aliveFunctions();

		batch.draw(filterTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);


		batch.end();

		if(DEBUG_PHYSICS) {
			debugRenderer.render(world, camera.combined);
		}

		//if(score < 8) {
			spawnPipe();
		//}

		animateBird();

		clearBodies();

		countScore();

		drawScore();

		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			saveAndQuit();
		}

		doPhysicsStep(Gdx.graphics.getDeltaTime());
	}

	private void saveAndQuit() {
		if(score > prefs.getInteger("highscore")) {
			prefs.putInteger("highscore", (int)score);
		}
		prefs.flush();
		Gdx.app.exit();
		System.exit(0);
	}

	private void drawScore() {
		batch.begin();
		if(score < 10) {
			font.getData().setScale(.02f, .03f);
			font.draw(batch, Integer.toString((int)score), 3.73f, 5.1f);
		} else {
			font.getData().setScale(.02f, .03f);
			font.draw(batch, Integer.toString((int)score), 3.55f, 5.1f);
		}
		batch.end();
		batch.begin();
		if(prefs.getInteger("highscore") != 0) {
			font.getData().setScale(.008f, .01f);
			font.draw(batch, "Highscore: " + prefs.getInteger("highscore"), 5.2f, 4.7f);
		}
		batch.end();
	}

	private void aliveFunctions() {

		if(alive) {
			positionOfDeathX = birdBody.getPosition().x;
			positionOfDeathY = birdBody.getPosition().y;
		}
		if(!alive) {
			feathers.hit();
			feathers.draw(batch, positionOfDeathX - 409 / 400f / 2, positionOfDeathY - 409 / 400f / 2);
			batch.draw(gameoverScreen, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
			if(!lostLife) {
				lives--;
			}
			lostLife = true;
		}
		if(started) {
			if(alive && checkIfInsideScreen()) {
				checkUserInput();
			}
		} else {
			restartGame(false);
			if(Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.justTouched()) {
				birdBody.setLinearVelocity(0,0);
				birdBody.applyLinearImpulse(new Vector2(0, 0.5f), birdBody.getWorldCenter(), true);
				wingSound.play(0.1f);
				started = true;
			}


			//USED FOR MOVING THE BIRD UP AND DOWN BEFORE THE GAME HAS STARTED
			if(goUpY) {
				idleMovementY += 0.005;
			} else {
				idleMovementY -= 0.005;
			}
			if(idleMovementY >= 2.5) {
				goUpY = false;
			}
			if(idleMovementY <= 2.3) {
				goUpY = true;
			}

			if(goUpX) {
				idleMovementX += 0.005;
			} else {
				idleMovementX -= 0.005;
			}
			if(idleMovementX >= 2.1) {
				goUpX = false;
			}
			if(idleMovementX <= 1.9) {
				goUpX = true;
			}
			birdBody.setTransform(idleMovementX, idleMovementY , 0);
		}
		if((Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.justTouched())&& started && !alive && lives > 0) {
			restartGame(true);
		} else if ((Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.justTouched()) && !alive && lives <= 0) {
			restartGame(true);
			started = false;
			restartGame(false);
			lives = 3;
		}
		batch.draw(setHeartTexture(), hearts.getX(), 3.9f, WORLD_WIDTH, WORLD_HEIGHT); //GOTTA FIX

	}

	public Texture setHeartTexture() {
		Texture three = heartThree;
		Texture two = heartTwo;
		Texture one = heartOne;
		Texture zero = heartZero;
		if(lives == 3) {
			hearts.setTexture(three);
			return three;
		} else if(lives == 2) {
			hearts.setTexture(two);
			return two;
		} else if(lives == 1) {
			hearts.setTexture(one);
			return one;
		} else {
			hearts.setTexture(zero);
			return zero;
		}
	}

	/**
	 * Flaps the birds wings when jumped
	 */
	private void animateBird() {
		Texture green2 = birdTextureFlapped;
		Texture green = birdTexture;

		if ((Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.justTouched()) && alive) {
			birdGameObject.texture = green2;
		} else {
			birdGameObject.texture = green;
		}
	}


	/**
	 * Moves the background and the logo and instructions
	 */
	private void moveBackground() {
		//This used to have for-loop to check all background images, but that caused a bug, that would separate the two pictures when lagging
		if(alive && started) {
			backgrounds.get(0).translateX(speed / 100 );
			backgrounds.get(1).translateX(speed / 100 );
			backgroundPipes.get(0).translateX(speed / 80);
			backgroundPipes.get(1).translateX(speed / 80);
			instructions1.translateX(speed / 60);
			logo.translateX(speed / 60);
		}
		if(backgrounds.get(0).getX() + WORLD_WIDTH <= 0) {
			backgrounds.get(0).setX(WORLD_WIDTH);
		}
		if(backgrounds.get(1).getX() + WORLD_WIDTH <= 0) {
			backgrounds.get(1).setX(WORLD_WIDTH);
		}
		if(backgroundPipes.get(0).getX() + WORLD_WIDTH <= 0) {
			backgroundPipes.get(0).setX(WORLD_WIDTH);
		}
		if(backgroundPipes.get(1).getX() + WORLD_WIDTH <= 0) {
			backgroundPipes.get(1).setX(WORLD_WIDTH);
		}
		batch.draw(backgroundTexture, backgrounds.get(0).getX(), backgrounds.get(0).getY(), WORLD_WIDTH * 1.01f, WORLD_HEIGHT);
		batch.draw(backgroundTexture, backgrounds.get(1).getX(), backgrounds.get(1).getY(), WORLD_WIDTH * 1.01f, WORLD_HEIGHT);
		batch.draw(bgpipesTexture, backgroundPipes.get(0).getX(), backgroundPipes.get(0).getY(), WORLD_WIDTH * 1.01f, WORLD_HEIGHT);
		batch.draw(bgpipesTexture, backgroundPipes.get(1).getX(), backgroundPipes.get(1).getY(), WORLD_WIDTH * 1.01f, WORLD_HEIGHT);
		batch.draw(instructionsTexture, instructions1.getX(),instructions1.getY() ,WORLD_WIDTH, WORLD_HEIGHT);
		batch.draw(logoTexture, logo.getX(),logo.getY() , WORLD_WIDTH, WORLD_HEIGHT);
	}

	/**
	 * Counts how many pipes the player has jumped over, this can be used with addition to spawnPipes() to make levels with varying amounts of pipes
	 */
	private void countScore() {
		for (Body body : bodies) {
			if (body.getUserData() != null) {
				GameObjectInfo info = (GameObjectInfo) body.getUserData();
				if(info.type != GameObjectType.BIRD) {
					if(body.getPosition().x < birdBody.getPosition().x && !info.jumpedOver && alive) {
						info.jumpedOver = true;
						scoreSound.play(0.05f);
						score += 0.5;
					}
				}
			}
		}
	}


	/**
	 * Restarts the game. Boolean restartAll determines will the game start over after death, or will it just reset constantly during idle period
	 * @param restartAll
	 */
	private void restartGame(boolean restartAll) {
		Array<Body> bodiesToBeDestroyed = new Array<Body>();
		// Iterate all pipes
		for (Body body : bodies) {
			// If it's not ground
			if(body.getUserData() != null) {
				GameObjectInfo info = (GameObjectInfo) body.getUserData();
				// If it's pipe, then mark it to be removed.
				if (info.type == GameObjectType.PIPE) {
					world.destroyBody(body);
				}
			}
		}
		alive = true;
		birdBody.setLinearVelocity(0, 0);
		pipeObst();
		speed = -2f;
		if (restartAll) {
			instructions1.setX(0);
			logo.setX(0);
			birdBody.setTransform(2f, WORLD_HEIGHT / 2, 0);
			birdBody.applyLinearImpulse(new Vector2(0, 0.5f), birdBody.getWorldCenter(), true);
			birdGameObject.texture = birdTexture();
			System.out.println("Score: " + (int) score);
			feathers.reset();
			lostLife = false;
			System.out.println(lives);
		}
		if(lives <= 0) {
			if(score > prefs.getInteger("highscore")) {
				prefs.putInteger("highscore", (int)score);
			}
			score = 0;

		}
	}

	private void spawnPipe() {
		for(Body body : bodies) {
			if(body.getUserData() != null) {
				GameObjectInfo data = (GameObjectInfo) body.getUserData();
				if(data.type == GameObjectType.PIPE && body.getPosition().x < WORLD_WIDTH - 2f && !data.hasSpawnedPipe) {
					pipeObst();
					data.hasSpawnedPipe = true;
				}
				if(data.type == GameObjectType.PIPE && !alive) {
					body.setLinearVelocity(0,0);
				} else if (data.type == GameObjectType.PIPE) {
					body.setLinearVelocity(speed, 0);
					speed -= 0.0001f;
				}
				if(!data.hasTexture && data.type == GameObjectType.PIPE) {
					data.texture = pipeTexture();
					data.hasTexture = true;
				}
			}
		}
	}

	private boolean checkIfInsideScreen() {
		return (birdBody.getPosition().y < WORLD_HEIGHT);
	}

	/**
	 * clears all pipes that are out of the screen
	 */
	public void clearBodies() {
		Array<Body> bodiesToBeDestroyed = new Array<Body>();

		// Iterate all pipes
		for (Body body : bodies) {
			// If it's not ground
			if(body.getUserData() != null) {
				GameObjectInfo info = (GameObjectInfo) body.getUserData();
				// If it's pipe, then mark it to be removed.
				if (info.type == GameObjectType.PIPE) {
					float yPos = body.getPosition().y;
					if (yPos < -1 * info.radius * 2) {
						bodiesToBeDestroyed.add(body);
					}

				}
			}
		}
		// Destroy needed bodies
		for (Body body : bodiesToBeDestroyed) {
			world.destroyBody(body);
		}
	}

	/**
	 * checks user input
	 */
	private void checkUserInput() {
		// KEY INPUT FOR DESKTOP
		if(Gdx.input.isKeyJustPressed(Input.Keys.UP)|| Gdx.input.justTouched()) {
			birdBody.setLinearVelocity(0,0);
			birdBody.applyLinearImpulse(new Vector2(0, 0.5f), birdBody.getWorldCenter(), true);
			wingSound.play(0.1f);
		}

	}

	/**
	 * creates the pipe obstacles
	 */
	private void pipeObst() {
		GameObjectInfo pipeObstInfo = new GameObjectInfo(pipeTexture, 1f, GameObjectType.PIPE, pipeTexture.getWidth() / 200f, pipeTexture.getHeight() / 100f, false, false);

		// Create body is my own method
		Body pipeBody = createBody(WORLD_WIDTH + 1f,
				MathUtils.random(-2f, 0.1f),
				false,
				pipeObstInfo.width,
				pipeObstInfo.height);

		pipeBody.setBullet(true);
		pipeBody.setLinearVelocity(-3f, 0);

		pipeBody.setUserData(pipeObstInfo);

		//Generates Top pipe
		GameObjectInfo pipeObstInfoTop = new GameObjectInfo(pipeTexture, 1f, GameObjectType.PIPE, pipeTexture.getWidth() / 200f, pipeTexture.getHeight() / 100f, true, true);
		// Create body is my own method
		Body pipeBodyTop = createBody(WORLD_WIDTH + 1f,
				pipeBody.getPosition().y + 1.5f + pipeObstInfo.height,
				false,
				pipeObstInfo.width,
				pipeObstInfo.height);

		pipeBodyTop.setBullet(true);
		pipeBodyTop.setLinearVelocity(-3f, 0);

		pipeBodyTop.setUserData(pipeObstInfoTop);
	}

	/**
	 * Draws all bodies in the world
	 */
	private void drawAllBodies() {
		// Draw all bodies
		for (Body body : bodies) {

			// Draw all bodies with user data (ground is not drawn)
			if(body.getUserData() != null) {

				// Get user data, has texture, type (pipe, or tennisball) and
				// radius
				GameObjectInfo info = (GameObjectInfo) body.getUserData();
				if(info.type == GameObjectType.BIRD && alive) {
					body.setTransform(body.getPosition().x, body.getPosition().y, body.getLinearVelocity().y / 12);
				}
				batch.draw(info.texture,
						body.getPosition().x - info.width / 2,
						body.getPosition().y - info.height / 2,
						info.radius,                   // originX
						info.radius,                   // originY
						info.width,               // width
						info.height,               // height
						1f,                          // scaleX
						1f,                          // scaleY
						body.getTransform().getRotation() * MathUtils.radiansToDegrees,
						0,                             // Start drawing from x = 0
						0,                             // Start drawing from y = 0
						info.texture.getWidth(),       // End drawing x
						info.texture.getHeight(),      // End drawing y
						false,                         // flipX
						info.flipped);                        // flipY

			}
		}

	}

	private BodyDef getGroundBodyDef() {
		// Body Definition
		BodyDef myBodyDef = new BodyDef();

		// This body won't move
		myBodyDef.type = BodyDef.BodyType.StaticBody;

		// Initial position is centered up
		// This position is the CENTER of the shape!
		myBodyDef.position.set(WORLD_WIDTH / 2, 0.0f);

		return myBodyDef;
	}

	private PolygonShape getGroundShape() {
		// Create shape
		PolygonShape groundBox = new PolygonShape();

		// Real width and height is 2 X this!
		groundBox.setAsBox( WORLD_WIDTH/2 , 0.0f);

		return groundBox;
	}

	public void createGround() {
		Body groundBody = world.createBody(getGroundBodyDef());

		// Add shape to fixture, 0.0f is density.
		// Using method createFixture(Shape, density) no need
		// to create FixtureDef object as on createPlayer!
		groundBody.createFixture(getGroundShape(), 0.0f);
	}


	private double accumulator = 0;
	private float TIME_STEP = 1 / 60f;

	private void doPhysicsStep(float deltaTime) {
		float frameTime = deltaTime;

		// If it took ages (over 4 fps, then use 4 fps)
		// Avoid of "spiral of death"
		if(deltaTime > 1 / 4f) {
			frameTime = 1 / 4f;
		}
		accumulator += frameTime;
		while (accumulator >= TIME_STEP) {
			// It's fixed time step!
			world.step(TIME_STEP, 6, 2);
			accumulator -= TIME_STEP;
		}
	}
	
	@Override
	public void dispose () {
		birdTexture.dispose();
		hitSound.dispose();
		scoreSound.dispose();
		wingSound.dispose();
		backgroundTexture.dispose();
		instructionsTexture.dispose();
		logoTexture.dispose();
		pipeTexture.dispose();
		pipe1.dispose();
		pipe2.dispose();
		pipe3.dispose();
		pipe4.dispose();
		heartOne.dispose();
		heartTwo.dispose();
		heartThree.dispose();
		heartZero.dispose();
		bgpipesTexture.dispose();
		world.dispose();

	}
}
