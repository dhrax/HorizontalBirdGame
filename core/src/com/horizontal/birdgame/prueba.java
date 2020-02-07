package com.horizontal.birdgame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class prueba extends ScreenAdapter implements InputProcessor {

    Begin juego;
    SpriteBatch batch;
    OrthographicCamera camera;
    TextureAtlas atlas;
    TextureRegion pilar, pajaro;
    Rectangle pilarRect=new Rectangle(), pajaroRect=new Rectangle();
    ShapeRenderer shapeRenderer;
    boolean ex;

    public prueba(Begin begin){
        this.juego = begin;
        this.batch = juego.batch;
        this.camera = juego.camera;
        this.atlas = juego.atlas;
        Gdx.input.setInputProcessor(this);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        pilar = new TextureRegion(new Texture("rockGrass.png"));
        pajaro = new TextureRegion(new Texture("PNG/Dragon Orange/1.png"));

        shapeRenderer = new ShapeRenderer();


        pilarRect.set(0, 0, pilar.getRegionWidth(), pilar.getRegionHeight());
        pajaroRect.set(400, 0, pajaro.getRegionWidth(), pajaro.getRegionHeight());

    }

    @Override
    public void render(float delta) {
        update();
        draw();
    }

    private void update() {
    }

    private void draw() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.enableBlending();
        batch.draw(pilar, 0, 0, pilar.getRegionWidth(), pilar.getRegionHeight());
        batch.draw(pajaro, 400, 0, pajaro.getRegionWidth(), pajaro.getRegionHeight());

        batch.end();

            Gdx.gl.glLineWidth(5f);
            shapeRenderer.setAutoShapeType(true);
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin();
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(pilarRect.x, pilarRect.y, pilarRect.width, pilarRect.height);
            shapeRenderer.rect(pajaroRect.x, pajaroRect.y, pajaroRect.width, pajaroRect.height);
            shapeRenderer.end();




    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Gdx.app.debug("X", String.valueOf(screenX));
        Gdx.app.debug("Y", String.valueOf(screenY));
        if(pilarRect.contains(screenX,screenY))
        {
            Gdx.app.debug("--PILAR TOCADO--", "SI");
            //juego.setScreen(new GamePlayScene((juego)));
        }
        if(pajaroRect.contains(screenX,screenY))
        {
            Gdx.app.debug("--PAJARO TOCADO--", "SI");
            //juego.setScreen(new GamePlayScene((juego)));
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
