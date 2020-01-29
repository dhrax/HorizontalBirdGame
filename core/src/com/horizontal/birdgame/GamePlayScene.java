package com.horizontal.birdgame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureArray;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

import sun.rmi.runtime.Log;

import static com.horizontal.birdgame.PickUp.FUEL;
import static com.horizontal.birdgame.PickUp.SHIELD;
import static com.horizontal.birdgame.PickUp.STAR;

public class GamePlayScene extends ScreenAdapter {


	SpriteBatch batch; //util para dibujar la misma textura en diferentes posiciones (mejora de rendimiento)

	OrthographicCamera camera; //OrthographicCamera: sirve como ventana del juego, es muy util para juegos 2D donde
	//se necesite una proyeccion Ortografica, es decir, donde no haya punto de fuga, por lo
	//que todos los elementos se encuentran en la misma escala, da igual donde nos encontremos.
	//Tambien realiza todas las operaciones para la vista automaticamente.


	TextureAtlas atlas, meteorAtlas;

	AssetManager manager;

	TextureRegion background, terrainBelow, terrainAbove, tap, pillarUp, pillarDown;
	float terrainOffset;
	Animation pajaro;
	float pajaroAnimTime;
	Vector2 planeVelocity = new Vector2();
	Vector2 planePosition= new Vector2();
	Vector2 planeDefaultPosition= new Vector2();
	Vector2 gravity= new Vector2();
	private static final Vector2 damping = new Vector2(0.99f,0.99f); //funciona friccion para reducir la velocidad del pajaro

	Texture gameOver;

	Begin juego;

	//TODO descomentar para el control tactil
	/*
	Vector3 tocadoPantalla = new Vector3();
	Vector2 tmpVector = new Vector2(); //indicador para mostrar donde se ha realizado la ultima pulsacion
	private static final int TOUCH_IMPULSE=500;
	TextureRegion tapIndicator;
	float tapDrawTime;
	private static final float TAP_DRAW_TIME_MAX=1.0f;
	*/


	//TODO descomentar para el control por giroscopio
	int orientation;
	float accelX;
	//float accelY;

	GameState gameState;

	Vector2 scrollVelocity = new Vector2(); //velocidad a la que va por defecto el avion
	Array<Vector2> pillars = new Array<>(); //array para almacenar las rocas pegadas al techo/suelo
	//Objetos, X representa la posicion del objeto, Y representa si mira hacia arriba o hacia abajo (1: arriba, -1: abajo)
	Vector2 lastPillarPosition=new Vector2(); //almacena el último pilar
	float deltaPosition;

	//objetos para las colisiones entre pilares y pajaro
	Rectangle planeRect=new Rectangle();
	Rectangle terrAboveRect =new Rectangle();
	Rectangle terrBelowRect =new Rectangle();
	Polygon obstacleTri = new Polygon();
	float[] arrVertices = new float[6];
	Circle obstacleMeteoro = new Circle();
	Rectangle pickUpRect = new Rectangle();

	//TODO descomentar para pintar los poígonos de las colisiones
	private ShapeRenderer shapeRenderer;

	//TODO pulir colisiones con los meteoros
	//Objetos para los meteoros
	Array<TextureAtlas.AtlasRegion> meteorTextures = new Array<>();
	TextureRegion selectedMeteorTexture;
	boolean meteorInScene;
	private static final int METEOR_SPEED=60;
	Vector2 meteorPosition= new Vector2();
	Vector2 meteorVelocity= new Vector2();
	float nextMeteorIn; //contador para sacar el siguiente meteoro


	Music backgroundMusic;
	Sound colisionSound, spawnMeteorSound;

	PickUp pickUp;
	Vector3 pickupTiming = new Vector3();
	ArrayList<PickUp> pickupsInScene = new ArrayList<>();


	public GamePlayScene (Begin begin) {

		juego = begin;
		batch = juego.batch;
		camera = juego.camera;
		manager = juego.manager;
		atlas = juego.atlas;
		meteorAtlas = juego.meteorAtlas;


		Gdx.app.setLogLevel(Application.LOG_DEBUG); //para poder poner logs


		//background = new Texture("background.png"); //no hace falta ya que se ha creado un texture Atlas
		background = atlas.findRegion("background");
		//terrainBelow=new TextureRegion(new Texture("groundGrass.png")); //no hace falta ya que se ha creado un texture Atlas
		terrainBelow = atlas.findRegion("groundGrass");
		tap = atlas.findRegion("tap");

		terrainAbove=new TextureRegion(terrainBelow);
		terrainAbove.flip(true, true); //convierte terrainAbove en terrainBelow dado la vuelta

		pajaro = new Animation(0.2f, atlas.findRegion("1"),
				atlas.findRegion("2"),
				atlas.findRegion("3"),
				atlas.findRegion("4")); //crea una animacion rotando los frames cada X tiempo
		pajaro.setPlayMode(Animation.PlayMode.LOOP); //ya que la animacion es un bucle
		pajaroAnimTime=0;

		//TODO descomentar para el control tactil
		//tapIndicator = atlas.findRegion("tapTick");

		//TODO descomentar para el control por giroscopio
		orientation = Gdx.input.getRotation();

		gameState = GameState.INIT;
		gameOver=new Texture("UI/textGameOver.png");

		pillarUp=atlas.findRegion("rockGrass");
		pillarDown = atlas.findRegion("rockGrassDown");

		shapeRenderer = new ShapeRenderer();

		//Texturas de los meteoros
		meteorTextures.add(meteorAtlas.findRegion("meteorBrown_med1"));
		meteorTextures.add(meteorAtlas.findRegion("meteorBrown_med3"));
		meteorTextures.add(meteorAtlas.findRegion("meteorBrown_small1"));
		meteorTextures.add(meteorAtlas.findRegion("meteorBrown_small2"));
		meteorTextures.add(meteorAtlas.findRegion("meteorBrown_tiny1"));
		meteorTextures.add(meteorAtlas.findRegion("meteorBrown_tiny2"));

		//Se pone musica de fondo
		backgroundMusic = manager.get("Music/background.ogg", Music.class);
		backgroundMusic.setLooping(true);
		backgroundMusic.play();

		colisionSound = manager.get("Music/colision.wav", Sound.class);



		resetScene();
	}

	public void render (float delta) {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		updateScene(); //logica del juego
		drawScene(); //dibuja lo que hay en la pantalla
	}

	@Override
	public void dispose () { //destruimos para liberar memoria

		backgroundMusic.dispose();
		colisionSound.dispose();
		spawnMeteorSound.dispose();
		pillars.clear();
		meteorTextures.clear();
	}

	public void resetScene(){ //reseteamos la pantalla (cuando muramos)
		pillars.clear();
		terrainOffset=0;
		pajaroAnimTime=0;
		planeVelocity.set(400, 0);
		gravity.set(0, -4);
		planeDefaultPosition.set(50, 190);
		planePosition.set(planeDefaultPosition.x, planeDefaultPosition.y);
		scrollVelocity.set(4, 0);
		obstacleTri.setPosition(0, 0);
		arrVertices = new float[]{0,0,0,0,0,0};
		obstacleTri.setVertices(arrVertices);
		meteorInScene=false;
		nextMeteorIn=(float)Math.random()*5; //s easigna un valor aleatorio para que cada vez que se comience, salga un meteoro distinto
		obstacleMeteoro.set(0,0 ,0);
		pickupTiming.set((float)(0.5+Math.random()*0.5), (float)(0.5+Math.random()*0.5), (float)(0.5+Math.random()*0.5));

	}

	private void updateScene(){
		float deltaTime = Gdx.graphics.getDeltaTime(); //tiempo entre el frame actual y el fram anterior
		//esto permite hacer animaciones sin depender de la velocidad del dispositivo
		/*
		 For example, in a device giving 60 fps, the deltaTime value will be 1/60, that is, 0.0167. On a slower device with 40 fps,
		 it will be 1/40, that is, 0.025. For every frame, this value multiplied by 200 is 3.34 and 5 respectively for fast and
		 slow devices. So, for 1 second, it will be 60 x 3.34 and 40 x 5 for these devices respectively,
		 which is approximately 200 (the original value) in both cases. So, the movement in 1 second is 200 on both devices
		 */
		if(gameState == GameState.GAME_OVER)
		{
			backgroundMusic.stop();
			if(Gdx.input.justTouched()){
				gameState = GameState.INIT;
				resetScene();
			}
			return;
		}
		if(gameState == GameState.INIT)
		{
			backgroundMusic.play();
			if(Gdx.input.justTouched())
				gameState = GameState.ACTION;
			return;
		}



		/**
		 * Esto permite desplazar el pajaro dependiendo de la posicion donde se pulse
		 */
		/*if(Gdx.input.justTouched())
		{
			tocadoPantalla.set(Gdx.input.getX(),Gdx.input.getY(),0);
			camera.unproject(tocadoPantalla);

			//necesitamos estas dos líneas para realizar el cáculo entre la posicion del pajaro y donde se ha pulsado
			tmpVector.set(planePosition.x,planePosition.y);
			tmpVector.sub( tocadoPantalla.x, tocadoPantalla.y).nor(); //sustrae al vector sus parametros y .nor() lo normaliza
			//se normaliza para saber la direccion del vector

			planeVelocity.mulAdd(tmpVector,
					//Aqui, nos aseguramos de que se restringe el valor que se añade, para que no salga de los límites
					TOUCH_IMPULSE- MathUtils.clamp(Vector2.dst(tocadoPantalla.x,
							tocadoPantalla.y, planePosition.x, planePosition.y), 0,
							TOUCH_IMPULSE));
			tapDrawTime=TAP_DRAW_TIME_MAX;
		}
		tapDrawTime-=deltaTime;
		*/

		//recoge los valores del acelerometro
		//TODO descomentar para el control por giroscopio
		accelX = Gdx.input.getAccelerometerX();
		//lo comento porque no hacen falta ya que tenemos una velocidad predeterminada
		//accelY = Gdx.input.getAccelerometerY();
		//planePosition.x+=accelY;
		planePosition.y-=accelX;

		pajaroAnimTime+=deltaTime;
		planeVelocity.scl(damping); //reducimos velocidad
		//planeVelocity.add(gravity); //añadimos la gravedad a la trayectoria
		planeVelocity.add(scrollVelocity);

		planePosition.mulAdd(planeVelocity, deltaTime); //multiplica escalarmente
		terrainOffset-=planePosition.x-planeDefaultPosition.x; //valor utilizado para hacer que es terreno se mueva hacia la izquierda

		//recorre los pilares para ver si hay que añadir uno,
		//también los muve por la pantalla, al igual que el terreno
		deltaPosition=planePosition.x-planeDefaultPosition.x;
		planeRect.set(planePosition.x, planePosition.y+20, 115, 50);


		if(lastPillarPosition.x<600 || pillars.size==0)
			addPillar();

		//se recorre array al revés para que el último elemento sea el referenciado

		for( int i=pillars.size-1; i>=0; i--){
			pillars.get(i).x-=deltaPosition;
			if(pillars.get(i).x+pillarUp.getRegionWidth()<-10)
				pillars.removeValue(pillars.get(i), false);

			if(pillars.get(i).y==1){
				obstacleTri.setPosition(pillars.get(i).x, 0);
				arrVertices= new float[] {pillars.get(i).x, 0f, pillars.get(i).x+pillarUp.getRegionWidth(), 0f, pillars.get(i).x+pillarUp.getRegionWidth()*0.6f, pillarUp.getRegionHeight()};
			}else{
				obstacleTri.setPosition(pillars.get(i).x, 480-50); //por alguna razón, tengo que restar tamaño a la
				// posicion y inicial, ya que si no, la colision no llega hasta la punta del pilar
				arrVertices = new float[] {pillars.get(i).x, 480f, pillars.get(i).x+pillarUp.getRegionWidth(), 480f, pillars.get(i).x+pillarUp.getRegionWidth()*0.6f, 480-pillarUp.getRegionHeight()};

			}
		}
		Gdx.app.debug("Ancho", String.valueOf(Gdx.graphics.getWidth()));
		Gdx.app.debug("Alto", String.valueOf(Gdx.graphics.getHeight()));
		obstacleTri.setVertices(arrVertices);

		//vamos moviendo el meteoro hacia la izquierda
		if(meteorInScene) {
			meteorPosition.mulAdd(meteorVelocity, deltaTime);
			meteorPosition.x-=deltaPosition;
			if(meteorPosition.x<-10)  {
				meteorInScene=false;
			}
		}
		nextMeteorIn-=deltaTime;
		//tiempo para el siguiente meteoro
		if(nextMeteorIn<=0) {
			launchMeteor();
		}

		checkAndCreatePickup(deltaTime);

		for (PickUp p: pickupsInScene){
			p.pickupPosition.x-=10;
		}


		if(isCollision(obstacleTri, planeRect, arrVertices)){
			if(gameState != GameState.GAME_OVER)
			{
				gameState = GameState.GAME_OVER;
				colisionSound.play();
				return;
			}
		}


		if(meteorInScene) {
			obstacleMeteoro.set(meteorPosition.x+selectedMeteorTexture.getRegionWidth()/2f, meteorPosition.y+selectedMeteorTexture.getRegionWidth()/2f, selectedMeteorTexture.getRegionWidth()/2);

			if(Intersector.overlaps(obstacleMeteoro, planeRect)){
				if(gameState != GameState.GAME_OVER){
					gameState = GameState.GAME_OVER;
					colisionSound.play();
					return;
				}
			}
		}

		planePosition.x=planeDefaultPosition.x; // se resetea para que el terreno se dibuje bien (ya que el terreno depende de tu posicion)

		//comprobamos si hay que resetear el terreno para no ver el final
		if(terrainOffset*-1>terrainBelow.getRegionWidth())
			terrainOffset=0;
		if(terrainOffset>0)
			terrainOffset=-terrainBelow.getRegionWidth();


		if(gameState == GameState.INIT || gameState == GameState.GAME_OVER)
			return;


		//TODO sacar fuera del bucle, ya que va a ser constante
		terrBelowRect.set(0, 0, terrainBelow.getRegionWidth(), terrainBelow.getRegionHeight());
		terrAboveRect.set(0, 480-terrainAbove.getRegionHeight(), terrainAbove.getRegionWidth(), terrainAbove.getRegionHeight());

		if(planeRect.overlaps(terrAboveRect) || planeRect.overlaps(terrBelowRect)){
			if(gameState != GameState.GAME_OVER)
			{
				gameState = GameState.GAME_OVER;
				colisionSound.play();
				return;
			}
		}

	}

	private void drawScene(){
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		//Blending es dibujar pixeles translucidos cuando se dibuja una textura sobre otra
		batch.disableBlending(); //primero lo desabilitamos para dibujar el fondo
		batch.draw(background, 0, 0);
		batch.enableBlending(); //lo habilitamos para dibujar el terreno sobre el fondo

		//pinto los pilares
		for(Vector2 vec: pillars) {
			if (vec.y == 1)
				batch.draw(pillarUp, vec.x, 0);
			else
				batch.draw(pillarDown, vec.x, 480 - pillarDown.getRegionHeight());
		}

		for(PickUp p: pickupsInScene){
			batch.draw(p.pickupTexture, p.pickupPosition.x, p.pickupPosition.y);
		}

		//dibujamos el terreno
		batch.draw(terrainBelow, terrainOffset, 0);
		batch.draw(terrainBelow, terrainOffset + terrainBelow. getRegionWidth(), 0);//se dibujan dos veces para dar la impresion de scroll infinito
		batch.draw(terrainAbove, terrainOffset, 480 - terrainAbove. getRegionHeight());
		batch.draw(terrainAbove, terrainOffset + terrainAbove. getRegionWidth(), 480 - terrainAbove.getRegionHeight());
		batch.draw((TextureRegion) pajaro.getKeyFrame(pajaroAnimTime), planePosition.x, planePosition.y);

		/* //TODO descomentar para el control tactil
		if(tapDrawTime>0)
		{
			batch.draw(tapIndicator, tocadoPantalla.x-60f, //60 es la mitad del ancho y 50 la mitad del altop
					tocadoPantalla.y-50f);
		}
		*/

		//Se pinta el meteoro aleatorio
		if(meteorInScene) {
			batch.draw(selectedMeteorTexture, meteorPosition.x, meteorPosition.y);
		}


		if(gameState == GameState.INIT)
			batch.draw(tap, planePosition.x+300, planePosition.y-50);

		if(gameState == GameState.GAME_OVER)
			batch.draw(gameOver, 400-206, 240-80);
		batch.end();
		/*
		Gdx.gl.glLineWidth(5f);

		shapeRenderer.setAutoShapeType(true);
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin();
		shapeRenderer.setColor(Color.BLACK);
		//shapeRenderer.triangle(arrVertices[0], arrVertices[1], arrVertices[2], arrVertices[3], arrVertices[4], arrVertices[5]);
		shapeRenderer.rect(terrBelowRect.x, terrBelowRect.y, terrBelowRect.width, terrBelowRect.height);
		shapeRenderer.end();

		shapeRenderer.setAutoShapeType(true);
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin();
		shapeRenderer.setColor(Color.BLACK);
		//shapeRenderer.triangle(arrVertices[0], arrVertices[1], arrVertices[2], arrVertices[3], arrVertices[4], arrVertices[5]);
		shapeRenderer.rect(terrAboveRect.x, terrAboveRect.y, terrAboveRect.width, terrAboveRect.height);
		shapeRenderer.end();

		shapeRenderer.setAutoShapeType(true);
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.BLACK);
		//shapeRenderer.triangle(arrVertices[0], arrVertices[1], arrVertices[2], arrVertices[3], arrVertices[4], arrVertices[5]);
		shapeRenderer.polygon(arrVertices);
		shapeRenderer.end();

		shapeRenderer.setAutoShapeType(true);
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin();
		shapeRenderer.setColor(Color.BLACK);
		//shapeRenderer.triangle(arrVertices[0], arrVertices[1], arrVertices[2], arrVertices[3], arrVertices[4], arrVertices[5]);
		shapeRenderer.rect(planeRect.x, planeRect.y, planeRect.width, planeRect.height);
		shapeRenderer.end();

		if(meteorInScene) {
			shapeRenderer.setAutoShapeType(true);
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin();
			shapeRenderer.setColor(Color.BLACK);
			//shapeRenderer.triangle(arrVertices[0], arrVertices[1], arrVertices[2], arrVertices[3], arrVertices[4], arrVertices[5]);
			shapeRenderer.circle(obstacleMeteoro.x, obstacleMeteoro.y, obstacleMeteoro.radius);
			shapeRenderer.end();
		}
		*/


	}


	enum GameState
	{
		INIT, ACTION, GAME_OVER
	}


	private void addPillar()
	{
		Gdx.app.debug("Pinta", "Pilar "+pillars.size);
		Vector2 pillarPosition=new Vector2();

		if(pillars.size==0)
		{
			pillarPosition.x=(float) (800 + Math.random()*600);
		}
		else
		{
			pillarPosition.x=lastPillarPosition.x+ 600;
		}
		if(MathUtils.randomBoolean())
		{
			pillarPosition.y=1;
		}
		else{
			pillarPosition.y=-1;//upside down
		}
		lastPillarPosition=pillarPosition;
		pillars.add(pillarPosition);
	}

	//comprueba las colisiones entre polígonos y rectangulos
	private boolean isCollision(Polygon p, Rectangle r, float[] arrVertices) {
		Polygon rPoly = new Polygon(arrVertices);
		rPoly.setPosition(r.x, r.y);

		return (Intersector.overlapConvexPolygons(rPoly, p));
	}


	//funcion que genera los meteoros
	private void launchMeteor() {
		nextMeteorIn=1.5f+(float)Math.random()*5;
		//si hay un meteoro en la pantalla, no pintamos otro
		if(meteorInScene)  {
			return;
		}
		meteorInScene=true;
		int id= (int)(Math.random()*meteorTextures.size);
		selectedMeteorTexture=meteorTextures.get(id); //escogemos un meteoro aleatorio
		meteorPosition.x=810; //ponemos el meteoro inicialmente justo a la derecha de la pantalla
		meteorPosition.y=(float) (80+Math.random()*320);
		Vector2 destination=new Vector2(); //creamos un vector que apunte a la parte izquierda de la pantalla
		destination.x=-10;
		destination.y=(float) (80+Math.random()*320);
		destination.sub(meteorPosition).nor(); //normalizamos el vector para darle direccion
		meteorVelocity.mulAdd(destination, METEOR_SPEED); //añadimos velocidad al meteoro
	}


	private void checkAndCreatePickup(float delta)
	{
		Gdx.app.debug("CREA PICKUP", "Si");
		pickupTiming.sub(delta);
		if(pickupTiming.x<=0)
		{
			pickupTiming.x=(float)(0.5+Math.random()*0.5);
			if(addPickup(STAR))
				pickupTiming.x=1+(float)Math.random()*2;
		}
		if(pickupTiming.y<=0)
		{
			pickupTiming.y=(float)(0.5+Math.random()*0.5);
			if(addPickup(FUEL))
				pickupTiming.y=3+(float)Math.random()*2;
		}
		if(pickupTiming.z<=0)
		{
			pickupTiming.z=(float)(0.5+Math.random()*0.5);
			if(addPickup(SHIELD))
				pickupTiming.z=10+(float)Math.random()*3;
		}
	}

	private boolean addPickup(int pickupType)
	{
		Vector2 randomPosition=new Vector2();
		randomPosition.x=820;
		randomPosition.y=(float) (80+Math.random()*320);
		/*for(Vector2 vec: pillars)
		{
			if(vec.y==1)
			{
				pickUpRect.set(vec.x , 0, pillarUp.getRegionWidth(),
						pillarUp.getRegionHeight());
			}
			else
			{
				pickUpRect.set(vec.x , 480-pillarDown.getRegionHeight(),
						pillarUp.getRegionWidth(), pillarUp.getRegionHeight());
			}

		}*/
		Gdx.app.debug("ANADE PICKUP", "Si");
		pickUp=new PickUp(pickupType);
		pickUp.pickupPosition.set(randomPosition);
		pickupsInScene.add(pickUp);
		return true;
	}

	private void pickIt(PickUp pickup)
	{
		//pickup.pickupSound.play();
		switch(pickup.type){
			case STAR:
				//starCount+=pickup.pickupValue;
				break;
			case SHIELD:
				//shieldCount=pickup.pickupValue;
				break;
			case FUEL:
				//fuelCount=pickup.pickupValue;
				break;
		}
		pickupsInScene.remove(pickup);
	}
}
