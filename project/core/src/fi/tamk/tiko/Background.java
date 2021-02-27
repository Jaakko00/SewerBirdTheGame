package fi.tamk.tiko;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Background extends Sprite {
    public Texture backgroundTexture;

    public Background() {
        backgroundTexture = new Texture("background.png");
    }

}