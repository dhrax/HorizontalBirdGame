package com.horizontal.birdgame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class prueba extends ScreenAdapter implements InputProcessor {

    Begin juego;
    SpriteBatch batch;
    OrthographicCamera camera;
    TextureAtlas atlas;
    TextureRegion tx;
    Rectangle textureBounds;
    ShapeRenderer shapeRenderer;
    boolean ex;

    public prueba(Begin begin){
        this.juego = begin;
        this.batch = juego.batch;
        this.camera = juego.camera;
        this.atlas = juego.atlas;

        tx = atlas.findRegion("background");
        tx.setRegionX(0);
        tx.setRegionY(0);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        shapeRenderer = new ShapeRenderer();
        ex=false;

        int ancho = Gdx.graphics.getWidth();
        int alto = Gdx.graphics.getHeight();
        textureBounds=new Rectangle(tx.getRegionX(),tx.getRegionY(),ancho,alto);
        Gdx.input.setInputProcessor(this);
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
        batch.begin();

        batch.draw(tx, 0, 0);

        batch.end();
        if(ex){
            Gdx.gl.glLineWidth(5f);
            shapeRenderer.setAutoShapeType(true);
            shapeRenderer.begin();
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(textureBounds.x, textureBounds.y, textureBounds.width, textureBounds.height);
            shapeRenderer.end();
        }

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

        if(textureBounds.contains(screenX,screenY))
        {
            Gdx.app.debug("Textura tocada", "SI");
            juego.setScreen(new GamePlayScene((juego)));
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
