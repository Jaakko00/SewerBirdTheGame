package fi.tamk.tiko;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Hearts extends Sprite {
    public Texture heartTexture;

    public Hearts() {
        heartTexture = new Texture("3lives.png");
    }

    @Override
    public void setTexture(Texture texture) {
        super.setTexture(texture);
    }
}