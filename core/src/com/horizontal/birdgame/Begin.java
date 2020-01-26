package com.horizontal.birdgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Begin extends Game {

    public static final int screenWidth=800;
    public static final int screenHeight=480;

    SpriteBatch batch; //util para dibujar la misma textura en diferentes posiciones (mejora de rendimiento)

    FPSLogger fpsLogger;
    OrthographicCamera camera; //OrthographicCamera: sirve como ventana del juego, es muy util para juegos 2D donde
    //se necesite una proyeccion Ortografica, es decir, donde no haya punto de fuga, por lo
    //que todos los elementos se encuentran en la misma escala, da igual donde nos encontremos.
    //Tambien realiza todas las operaciones para la vista automaticamente.

    //TODO poner cuando se quiera hacer responsive
    //Viewport viewport; //se utiliza para hacer responsive la palicacion en diferentes resoluciones y tamaños de pantalla

    TextureAtlas atlas, meteorAtlas;


    public Begin(){

        fpsLogger = new FPSLogger();

        camera = new OrthographicCamera();
        //se puede crear una camara responsive:
		/*
			float w = Gdx.graphics.getWidth();
			float h = Gdx.graphics.getHeight();
			cam = new OrthographicCamera(30, 30 * (h / w));
		 */
        camera.setToOrtho(false, screenWidth, screenHeight); //no hace falta esta linea, porque al implementar eñ VIewPort, la de abajo la sustituye
        /* Utilizado para mantener el tamaño de pantalla en distintos dispositivos
		camera.position.set(400,240,0);
		viewport = new FitViewport(800, 480, camera);
		*/
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        //Texturas empaquetadas
        atlas = new TextureAtlas(Gdx.files.internal("HorizontalBirdgame.pack"));
        meteorAtlas = new TextureAtlas(Gdx.files.internal("BirdGameMeteorPack.pack"));

        setScreen(new GamePlayScene(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        atlas.dispose();
        meteorAtlas.dispose();
    }

    @Override
    public void render() {
        fpsLogger.log(); //muestra en el logcat/Debug los FPS a los que va la aplicacion. TAG: fps
        super.render();
    }

    //Sirve para que, al cambiar el tamaño de la ventana, el viewport lo recoja y se actualice
	/*
	@Override
	public void resize (int width, int height)
	{
		viewport.update(width, height);
	}*/
}
