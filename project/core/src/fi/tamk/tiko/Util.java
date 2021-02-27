package fi.tamk.tiko;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Contains helper methods for handling TextureRegions.
 *
 * @author Jussi Pohjolainen
 */
public class Util {
    /**
     * Transforms 2D TextureRegion array to 1D.
     *
     * @param tr 2D TextureRegion array
     * @param cols col number
     * @param rows row number
     * @return Transformed 1D array
     */
    public static TextureRegion[] toTextureArray(TextureRegion [][]tr, int cols, int rows ) {
        TextureRegion [] frames
                = new TextureRegion[cols * rows];

        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                frames[index++] = tr[i][j];
            }
        }

        return frames;
    }

    /**
     * Go all the TextureRegion through and flip them.
     *
     * @param animation the animation which frames will be flipped.
     */
    public static void flip(Animation<TextureRegion> animation) {
        TextureRegion[] regions = animation.getKeyFrames();
        for(TextureRegion r : regions) {
            r.flip(true, false);
        }
    }
}

