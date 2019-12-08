package gdx.kapotopia.Animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import gdx.kapotopia.Helpers.Builders.AnimationBuilder;

public class DifficultyScreenAnimation {

    private Animation<TextureRegion> animation;

    public DifficultyScreenAnimation(Animation.PlayMode playMode) {
        final String [] FRAMES = new String[]{"World1/Game1/EcranJeu1.png",
                "World1/Game1/EcranJeu1V2.png", "World1/Game1/EcranJeu1V3.png"};

        this.animation = new AnimationBuilder(0.0625f).withPlayMode(playMode)
                .addFrames(FRAMES).build();
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }
}