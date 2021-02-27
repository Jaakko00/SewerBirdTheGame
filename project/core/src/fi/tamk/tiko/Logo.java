package fi.tamk.tiko;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Logo extends Sprite {
    public Texture logoTexture;

    public Logo() {
        logoTexture = new Texture("logo.png");
    }
}