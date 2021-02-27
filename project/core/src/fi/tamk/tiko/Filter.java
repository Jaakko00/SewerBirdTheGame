package fi.tamk.tiko;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Filter extends Sprite {
    public Texture filterTexture;

    public Filter() {
        filterTexture = new Texture("filter.png");
    }
}
