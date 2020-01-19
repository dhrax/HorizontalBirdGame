package com.horizontal.birdgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Begin extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	FPSLogger fpsLogger;
	OrthographicCamera camera; //OrthographicCamera: sirve como ventana del juego, es muy util para juegos 2D donde
								//se necesite una proyeccion Ortografica, es decir, donde no haya punto de fuga, por lo
								//que todos los elementos se encuentran en la misma escala, da igual donde nos encontremos.
								//Tambien realiza todas las operaciones para la vista automaticamente.
	Texture background;
	TextureRegion terrainBelow, terrainAbove;
	float terrainOffset;

	@Override
	public void create () {
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
		camera.setToOrtho(false, 800, 480);
		background = new Texture("background.png");

		terrainBelow=new TextureRegion(new Texture("groundGrass.png"));
		terrainAbove=new TextureRegion(terrainBelow);
		terrainAbove.flip(true, true); //convierte terrainAbove en terrainBelow dado la vuelta
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
	public void dispose () {
		batch.dispose();
		img.dispose();
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
		terrainOffset-=200*deltaTime; //valor utilizado para hacer que es terreno se mueva hacia la izquierda
	}

	private void drawScene(){
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		//Blending es dibujar pixeles translucidos cuando se dibuja una textura sobre otra
		batch.disableBlending(); //primero lo desabilitamos para dibujar el fondo
		batch.draw(background, 0, 0);
		batch.enableBlending(); //lo habilitamos para dibujar el terreno sobre el fondo
		//dibujamos el terreno
		batch.draw(terrainBelow, terrainOffset, 0);
		batch.draw(terrainBelow, terrainOffset + terrainBelow. getRegionWidth(), 0);//se dibujan dos veces para dar la impresion de scroll infinito
		batch.draw(terrainAbove, terrainOffset, 480 - terrainAbove. getRegionHeight());
		batch.draw(terrainAbove, terrainOffset + terrainAbove. getRegionWidth(), 480 - terrainAbove.getRegionHeight());
		batch.end();
	}
}
