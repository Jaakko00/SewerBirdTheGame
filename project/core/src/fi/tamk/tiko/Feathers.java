package fi.tamk.tiko;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Feathers {
    private Texture featherSheet;
    private Animation<TextureRegion> hitAnimation;
    private float stateTime;
    private TextureRegion currentFrameTexture;
    private float height;
    private float width;

    public Feathers() {
        // Load the image.
        featherSheet = new Texture("featherSheet.png");


        createHitAnimation();

        // pixels -> meters
        height = 409 / 400f;
        width = 409 / 400f;


    }
    private void createHitAnimation() {
        final int FRAME_COLS = 6;
        final int FRAME_ROWS = 1;

        /** CREATE THE WALK ANIM **/

        // Calculate the tile width from the sheet
        int tileWidth = featherSheet.getWidth() / FRAME_COLS;

        // Calculate the tile height from the sheet
        int tileHeight = featherSheet.getHeight() / FRAME_ROWS;

        // Create 2D array from the texture (REGIONS of a TEXTURE).
        TextureRegion[][] tmp = TextureRegion.split(featherSheet, tileWidth, tileHeight);

        // Transform the 2D array to 1D
        TextureRegion[] allFrames = Util.toTextureArray( tmp, FRAME_COLS, FRAME_ROWS );

        hitAnimation = new Animation(2 / 60f,allFrames);

        currentFrameTexture = hitAnimation.getKeyFrame(stateTime, false);
    }
    public void hit() {
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrameTexture = hitAnimation.getKeyFrame(stateTime, false);
    }
    public void draw(SpriteBatch batch, float x, float y) {
        batch.draw(currentFrameTexture, x, y, width, height);
    }
    public void reset() {
        stateTime = 0;
    }
}