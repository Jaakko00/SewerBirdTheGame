package fi.tamk.tiko;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Instructions extends Sprite {
    public Texture instructionsTexture;

    public Instructions() {
        instructionsTexture = new Texture("instructions.png");
    }
}
