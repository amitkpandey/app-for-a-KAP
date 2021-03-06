package gdx.kapotopia.Animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import gdx.kapotopia.AssetsManaging.AssetsManager;
import gdx.kapotopia.Helpers.Builders.AnimationBuilder;

public class LetsgoG1Animation extends AnimationAbstract {

    public LetsgoG1Animation(Animation.PlayMode playMode) {

        TextureAtlas atlas = AssetsManager.getInstance().getAtlasByPath("World1/Game1/actiontext.atlas");
        Array<TextureAtlas.AtlasRegion> r = atlas.findRegions("actiontext");
        TextureAtlas.AtlasRegion[] array = r.toArray();

        setAnimation(new AnimationBuilder(0.04f).withPlayMode(playMode)
                .addFrames(array).build());
    }
}
