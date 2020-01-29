package com.horizontal.birdgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class PickUp
{
    public static final int STAR =1;
    public static final int SHIELD =2;
    public static final int FUEL =3;
    TextureRegion pickupTexture;
    Vector2 pickupPosition = new Vector2();
    int type;
    int pickupValue;
    Sound pickupSound;

    public PickUp(int type)
    {

        this.type=type;
        switch(this.type){
            case STAR:
                pickupTexture= new TextureRegion(new Texture("starGold.png"));
                pickupValue=5;
                //pickupSound = manager.get("sounds/star.ogg", Sound.class);
                break;
            case SHIELD:
                pickupTexture= new TextureRegion(new Texture("love-shield.png"));
                pickupValue=15;
                //pickupSound = manager.get("sounds/shield.ogg", Sound.class);
                break;
            case FUEL:
                pickupTexture= new TextureRegion(new Texture("apple.png"));
                pickupValue=100;
                //pickupSound = manager.get("sounds/fuel.ogg", Sound.class);
                break;
        }
    }
}
