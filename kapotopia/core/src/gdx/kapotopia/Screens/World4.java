package gdx.kapotopia.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import gdx.kapotopia.AssetsManaging.AssetsManager;
import gdx.kapotopia.Fonts.UseFont;
import gdx.kapotopia.Helpers.Align;
import gdx.kapotopia.Helpers.Alignement;
import gdx.kapotopia.Helpers.Builders.ImageButtonBuilder;
import gdx.kapotopia.Helpers.Builders.LabelBuilder;
import gdx.kapotopia.Helpers.Builders.TextButtonBuilder;
import gdx.kapotopia.Helpers.ChangeScreenListener;
import gdx.kapotopia.Kapotopia;
import gdx.kapotopia.Localisation;
import gdx.kapotopia.STIDex.STI;
import gdx.kapotopia.STIDex.STIData;
import gdx.kapotopia.ScreenType;
import gdx.kapotopia.Utils;


public class World4 implements Screen {

    /*
     *
     *      ###########################
     *      #                         #
     *      #            W2           #
     *      #        ##########       #
     *      #        #        #       #
     *      #     H2 #        #       #
     *      #        #        #       #
     *      #        #        #       #
     *      #        ##########       #
     *      #     X2(x,y)             #
     *      #                         #
     *      #           W1            #
     *      #   ###################   #
     *      #   #                 #   #
     *      #   #                 #   #
     *      #   #                 #   #
     *      #   #                 #   #
     *      # H1#                 #   #
     *      #   #                 #   #
     *      #   #  <:  next   :>  #   #
     *      #   #                 #   #
     *      #   ###################   #
     *      #  X1(x,y)                #
     *      #                         #
     *      ###########################
     */

    private final String TAG = this.getClass().getSimpleName();

    private Kapotopia game;
    private Stage stage;
    private Image displayedIstSprite;
    private float istSpriteWidth, istSpriteHeight;
    private Label nameLab, descriptionLab;
    private int istIndex;

    private STI[] data;

    public World4(final Kapotopia game) {

        preload();

        this.game = game;
        Texture fond = AssetsManager.getInstance().getTextureByPath("Pokedex.png");
        Image imgFond = new Image(fond);
        stage = new Stage(game.viewport);
        stage.addActor(imgFond);

        TextButton.TextButtonStyle style = Utils.getStyleFont("COMMS.ttf", 60);


        final TextButton back = new TextButtonBuilder(Localisation.getInstance().getString("back_button")).withStyle(style)
                .withListener(new ChangeScreenListener(game, ScreenType.MAINMENU, ScreenType.WORLD4)).build();
        back.setPosition((game.viewport.getWorldWidth() / 2) - back.getWidth() / 2, 50);
        back.setVisible(true);
        stage.addActor(back);

        // Right Arrow texture loading (we need it's dimensions for computing certain coordinates)
        Texture rightArrowT = AssetsManager.getInstance().getTextureByPath("ui_arrow.png");

        /* COMPUTING COORDINATES (for info about variable names, see the draw at the top of this file) */
        final float ww = game.viewport.getWorldWidth();
        final float wh = game.viewport.getWorldHeight();

        // Computing arrows coordinates
        final float arrowY = wh * 0.0275f;
        final float rightArrowX = 0.9f * ww - rightArrowT.getWidth();
        final float leftArrowX = ww - rightArrowX - rightArrowT.getWidth();

        // Computing gameboy downscreen coordinates
        final float x1_x = ww * 0.075f;
        final float x1_y = wh * 0.282f;
        final float w1 = ww * 0.85f;
        //final float h1 = wh * 0.15f;

        // Computing gameboy upscreen coordinates
        final float x2_x = ww * 0.275f;
        final float x2_y = wh * 0.675f;
        final float w2 = ww * 0.45f;
        final float h2 = wh * 0.25f;

        //this code is used to update the displayedIstSprite
        displayedIstSprite = new Image(AssetsManager.getInstance().getTextureByPath(data[istIndex].getTexturePath()));
        displayedIstSprite.setBounds(x2_x, x2_y, w2, h2);
        displayedIstSprite.setVisible(true);
        displayedIstSprite.setTouchable(Touchable.enabled);
        stage.addActor(displayedIstSprite);

        //right arrow

        ImageButton rightArrow = new ImageButtonBuilder().withImageUp(rightArrowT)
                .withPosition(rightArrowX, arrowY)
                .withListener(new InputListener() {
                    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                        istIndex = (istIndex + 1) % data.length;
                        nameLab.setText(data[istIndex].getName());
                        Align.centerLabel(nameLab, Alignement.CENTER);
                        descriptionLab.setText(data[istIndex].getDescription());
                        updateSti();
                        return true;
                    }
                })
                .build();
        stage.addActor(rightArrow);

        //left arrow, first gotta flip the texture
        TextureRegion reversedArrow = new TextureRegion(rightArrowT);
        reversedArrow.flip(true, false);
        ImageButton leftArrow = new ImageButtonBuilder().withImageUp(reversedArrow)
                .withPosition(leftArrowX, arrowY)
                .withListener(new InputListener() {
                    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                        istIndex = (istIndex - 1) % data.length;
                        if (istIndex < 0) istIndex = data.length-1; // Circular list
                        nameLab.setText(data[istIndex].getName());
                        Align.centerLabel(nameLab, Alignement.CENTER);
                        descriptionLab.setText(data[istIndex].getDescription());
                        updateSti();
                        return true;
                    }
                })
                .build();
        stage.addActor(leftArrow);

        // label containing STI name
        nameLab = new LabelBuilder(data[istIndex].getName()).withY(wh * 0.625f)
                .withStyle(UseFont.CLASSIC_SANS_MIDDLE_BLACK)
                .withAlignment(Alignement.CENTER).build();
        stage.addActor(nameLab);

        // label containing the STI descriptionLab
        descriptionLab = new LabelBuilder(data[istIndex].getDescription()).isWrapped(true)
                .withStyle(UseFont.CLASSIC_SANS_MIDDLE_BLACK)
                .withPosition(x1_x, x1_y).withWidth(w1)
                //.withBounds(x1_x, x1_y, w1, h1)
                .build();
        stage.addActor(descriptionLab);

        AssetsManager.getInstance().addStage(stage, "world4");
    }

    private void updateSti(){
        displayedIstSprite.setDrawable(
                new TextureRegionDrawable(
                        new TextureRegion(AssetsManager.getInstance().getTextureByPath(
                                data[istIndex].getTexturePath())
                        )
                )
        );
    }

    private void preload(){
        Gdx.app.log(TAG, "Preloading stuff... ");
        this.data = STIData.getIstAndMaybeIsts();
        for (STI sti : data) {
            istIndex = (istIndex + 1) % data.length;
            Texture t = AssetsManager.getInstance().getTextureByPath(sti.getTexturePath());
            // We imposed that every virus sprite texture has the same dimension, thus we can just take the last texture width/height
            istSpriteWidth = t.getWidth();
            istSpriteHeight = t.getHeight();
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }


    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        AssetsManager.getInstance().disposeStage("world4");
    }



}
