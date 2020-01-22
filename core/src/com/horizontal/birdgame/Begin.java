package com.horizontal.birdgame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import sun.rmi.runtime.Log;

public class Begin extends ApplicationAdapter {
	SpriteBatch batch; //util para dibujar la misma textura en diferentes posiciones (mejora de rendimiento)
	Texture img;
	FPSLogger fpsLogger;
	OrthographicCamera camera; //OrthographicCamera: sirve como ventana del juego, es muy util para juegos 2D donde
								//se necesite una proyeccion Ortografica, es decir, donde no haya punto de fuga, por lo
								//que todos los elementos se encuentran en la misma escala, da igual donde nos encontremos.
								//Tambien realiza todas las operaciones para la vista automaticamente.
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

	//TODO poner cuando se quiera hacer responsive
	//Viewport viewport; //se utiliza para hacer responsive la palicacion en diferentes resoluciones y tamaños de pantalla

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
	float accelY;

	GameState gameState;

	Vector2 scrollVelocity = new Vector2(); //velocidad a la que va por defecto el avion
	Array<Vector2> pillars = new Array<>(); //array para almacenar las rocas pegadas al techo/suelo
	//Objetos, X representa la posicion del objeto, Y representa si mira hacia arriba o hacia abajo (1: arriba, -1: abajo)
	Vector2 lastPillarPosition=new Vector2(); //almacena el último pilar
	float deltaPosition;

	//rectangulos para coprobar el overlap entre el pajaro y los pilares
	Rectangle planeRect=new Rectangle();
	Rectangle obstacleRect=new Rectangle();

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG); //para poder poner logs
		batch = new SpriteBatch();
		fpsLogger = new FPSLogger();
		//img = new Texture("badlogic.jpg");
		camera = new OrthographicCamera();
		//se puede crear una camara responsive:
		/*
			float w = Gdx.graphics.getWidth();
			float h = Gdx.graphics.getHeight();
			cam = new OrthographicCamera(30, 30 * (h / w));
		 */
		camera.setToOrtho(false, 800, 480); //no hace falta esta linea, porque al implementar eñ VIewPort, la de abajo la sustituye
		/* Utilizado para mantener el tamaño de pantalla en distintos dispositivos
		camera.position.set(400,240,0);
		viewport = new FitViewport(800, 480, camera);
		*/

		//Texturas empaquetadas
		TextureAtlas atlas = new
				TextureAtlas(Gdx.files.internal("HorizontalBirdgame.pack"));
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

		resetScene();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		fpsLogger.log(); //muestra en el logcat/Debug los FPS a los que va la aplicacion. TAG: fps
		updateScene(); //logica del juego
		drawScene(); //dibuja lo que hay en la pantalla
	}
	
	@Override
	public void dispose () { //destruimos para liberar memoria
		batch.dispose();
		img.dispose();
	}

	public void resetScene(){ //reseteamos la pantalla (cuando muramos)
		terrainOffset=0;
		pajaroAnimTime=0;
		planeVelocity.set(400, 0);
		gravity.set(0, -4);
		planeDefaultPosition.set(50, 190);
		planePosition.set(planeDefaultPosition.x, planeDefaultPosition.y);
		scrollVelocity.set(4, 0);
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

			if(gameState == GameState.INIT)
			{
				if(Gdx.input.justTouched()){
					gameState = GameState.ACTION;
				}
				return;
			}
			if(gameState == GameState.GAME_OVER)
			{
				if(Gdx.input.justTouched()){
					gameState = GameState.INIT;
					resetScene();

				}
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
		accelY = Gdx.input.getAccelerometerY();
		planePosition.x+=accelY;
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
		planeRect.set(planePosition.x, planePosition.y, 120, 100);
		//TODO solucionar colisiones con pilares
		for(Vector2 vec: pillars)
		{
			vec.x-=deltaPosition;
			if(vec.x+pillarUp.getRegionWidth()<-10)
			{
				pillars.removeValue(vec, false);
			}
			if(vec.y==1)
			{
				obstacleRect.set(vec.x, 0, pillarUp.getRegionWidth(),
						pillarUp.getRegionHeight());
			}else
			{
				obstacleRect.set(vec.x,
						480-pillarDown.getRegionHeight(),
						pillarUp.getRegionWidth(), pillarUp.getRegionHeight());
			}
		}
		if(lastPillarPosition.x<400)
			addPillar();
		if(planeRect.overlaps(obstacleRect))
		{
			if(gameState != GameState.GAME_OVER)
			{
				gameState = GameState.GAME_OVER;
			}
		}
		planePosition.x=planeDefaultPosition.x; // se resetea para que el terreno se dibuje bien (ya que el terreno depende de tu posicion)

		//comprobamos si hay que resetear el terreno para no ver el final
		if(terrainOffset*-1>terrainBelow.getRegionWidth())
			terrainOffset=0;
		if(terrainOffset>0)
			terrainOffset=-terrainBelow.getRegionWidth();



		if(gameState == GameState.INIT || gameState == GameState.GAME_OVER)
		{
			return;
		}

		if(planePosition.y < terrainBelow.getRegionHeight() -20||
				planePosition.y + 73 > 480 - terrainBelow.getRegionHeight())
		{
			if(gameState != GameState.GAME_OVER)
			{
				gameState = GameState.GAME_OVER;
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
			if (vec.y == 1) {
				batch.draw(pillarUp, vec.x, 0);
			} else {
				batch.draw(pillarDown, vec.x,
						480 - pillarDown.getRegionHeight());
			}
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
		if(gameState == GameState.INIT)
		{
			batch.draw(tap, planePosition.x+300, planePosition.y-50);
		}

		if(gameState == GameState.GAME_OVER)
		{
			batch.draw(gameOver, 400-206, 240-80);
		}
		batch.end();
	}


	//Sirve para que, al cambiar el tamaño de la ventana, el view port lo recoja y se actualice
	/*
	@Override
	public void resize (int width, int height)
	{
		viewport.update(width, height);
	}*/

	enum GameState
	{
		INIT, ACTION, GAME_OVER
	}


	private void addPillar()
	{
		Vector2 pillarPosition=new Vector2();

		if(pillars.size==0)
		{
			pillarPosition.x=(float) (800 + Math.random()*600);
		}
		else
		{
			pillarPosition.x=lastPillarPosition.x+(float) (600 +
					Math.random()*600);
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
}
