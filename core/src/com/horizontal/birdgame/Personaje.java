package com.horizontal.birdgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Personaje {

    private Animation personajeAnimation;
    private float personajeFrameAct;
    private Vector2 velocidadPersonaje;
    private Vector2 posicionPersonaje;
    private Vector2 posicionDefectoPersonaje;
    private TextureRegion[] arrTexturasPersonaje;


    public Personaje(float duracionFrame, TextureRegion[] arrTexturasPersonaje, Animation.PlayMode repeticion){
        this.arrTexturasPersonaje = arrTexturasPersonaje;
        personajeAnimation = new Animation(duracionFrame, this.arrTexturasPersonaje, repeticion);
        this.personajeFrameAct =0;
        this.velocidadPersonaje = new Vector2();
        this.posicionPersonaje= new Vector2();
        this.posicionDefectoPersonaje= new Vector2();
    }

    public Animation getPersonajeAnimation() {
        return personajeAnimation;
    }

    public void setPersonajeAnimation(Animation personajeAnimation) {
        this.personajeAnimation = personajeAnimation;
    }

    public TextureRegion getTexturaActual(){
        return this.arrTexturasPersonaje[(int) getPersonajeFrameAct()];
    }

    public float getPersonajeFrameAct() {
        return this.personajeFrameAct;
    }

    public void setPersonajeFrameAct(float personajeFrameAct) {
        if (this.personajeFrameAct>3)
            personajeFrameAct=0;
        this.personajeFrameAct = personajeFrameAct;
    }

    public float getVelocidadPersonajeX() {
        return velocidadPersonaje.x;
    }
    public float getVelocidadPersonajeY() {
        return velocidadPersonaje.y;
    }

    public void setVelocidadPersonaje(float X, float Y) {
        this.velocidadPersonaje.set(X, Y);
    }

    public float getPosicionPersonajeX() {
        return posicionPersonaje.x;
    }
    public float getPosicionPersonajeY() {
        return posicionPersonaje.y;
    }

    public void setPosicionPersonaje(float X, float Y) {
        this.posicionPersonaje.set(X, Y);
    }

    public float getPosicionDefectoPersonajeX() {
        return posicionDefectoPersonaje.x;
    }
    public float getPosicionDefectoPersonajeY() {
        return posicionDefectoPersonaje.y;
    }

    public void setPosicionDefectoPersonaje(float X, float Y) {
        this.posicionDefectoPersonaje.set(X, Y);
    }

    public Vector2 getVelocidadPersonaje() {
        return velocidadPersonaje;
    }

    public Vector2 getPosicionPersonaje() {
        return posicionPersonaje;
    }

    public Vector2 getPosicionDefectoPersonaje() {
        return posicionDefectoPersonaje;
    }

    public void setVelocidadPersonaje(Vector2 velocidadPersonaje) {
        this.velocidadPersonaje = velocidadPersonaje;
    }

    public void setPosicionPersonaje(Vector2 posicionPersonaje) {
        this.posicionPersonaje = posicionPersonaje;
    }

    public void setPosicionDefectoPersonaje(Vector2 posicionDefectoPersonaje) {
        this.posicionDefectoPersonaje = posicionDefectoPersonaje;
    }
}
