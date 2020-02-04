package com.horizontal.birdgame;

import com.badlogic.gdx.graphics.Texture;

public class HUD {

    private float contadorComida;
    private int porcentajeComida;
    private Texture indicadorComida;

    public Texture getIndicadorComida() {
        return indicadorComida;
    }

    public HUD(){
        indicadorComida = new Texture("UI/medalGold.png");
        inicializarHUD();
    }

    public float getContadorComida() {
        return contadorComida;
    }

    public void setContadorComida(float contadorComida) {
        this.contadorComida = contadorComida;
    }

    public int getPorcentajeComida() {
        return porcentajeComida;
    }

    public void setPorcentajeComida(int porcentajeComida) {
        this.porcentajeComida = porcentajeComida;
    }

    public void inicializarHUD(){
        setPorcentajeComida(indicadorComida.getWidth());
        contadorComida=100;
    }

    public void dispose(){
        indicadorComida.dispose();
    }
}
