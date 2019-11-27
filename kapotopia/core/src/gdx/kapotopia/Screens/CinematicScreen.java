package gdx.kapotopia.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;

import java.util.Iterator;

import gdx.kapotopia.AssetsManaging.AssetsManager;
import gdx.kapotopia.AssetsManaging.FontHelper;
import gdx.kapotopia.AssetsManaging.UseFont;
import gdx.kapotopia.DialogsScreen.DialogueElement;
import gdx.kapotopia.DialogsScreen.FixedDialogueSequence;
import gdx.kapotopia.Helpers.Builders.LabelBuilder;
import gdx.kapotopia.Helpers.Builders.TextButtonBuilder;
import gdx.kapotopia.Kapotopia;
import gdx.kapotopia.ScreenType;
import gdx.kapotopia.Helpers.StandardInputAdapter;
import gdx.kapotopia.Utils;

/**
 * This class define a common base for screens where only cinematics, shown with static pictures, are shown
 * The class has a list of Image to show in a specific order, only two buttons are used "next" and "finish".
 * "Next" is used to change of picture, "Finish" end the cinematic and leads to another screen.
 * Sounds can be played when Images are changed
 */
public abstract class CinematicScreen implements Screen {
    /* VARIABLES */
    protected Kapotopia game;
    protected Stage stage;
    private String screenName;
    private boolean initialized; // Indicate if the applyBundle function has been called or not
    // Graphics
    private FixedDialogueSequence sequence;
    private Image fond;
    private int curImg;
    // Sounds
    private Sound changeOfImageSound;
    private Sound endSound;
    private Sound pauseSound;
    // Interaction
    private Button next;
    private Button finish;

    /* FUNCTIONS */

    // Constructors

    /**
     * Build helper function. Create A cinematicScreen with the given arguments
     * @param nextScreen of enum type ScreenType, is the screen that will be shown after the user touched the finish button
     * @param imagesTexturePaths the paths of the images shown, shown by increasing order
     * @param labels
     * @param fondPath the path of the background shown when the finish button appear
     * @param changeOfImageSoundPath the path of the sound file that plays when screen is changed
     * @param endSoundPath the path of the sound file that plays before the screen is changed to @nextScreen
     * @param pauseSoundPath the path of the sound file that plays when the game is paused
     * @param nextBtnLabel the text displayed by the "next" button
     * @param finishBtnLabel the text displayed by the "finish" button
     * @param stylePath the path to the style used for texts
     * @param textColor the text color given by a constant in Color class of libgdx
     * @param usualFont
     * @param timerScheduleTime the time between the player pressed the "finish" button and when it change screen
     * @param vibrationTime the amount of time that the phone vibrate when pressing "next" and "finish" buttons (the time for "next" button pressed is fourth time less than the time given)
     */
    private void builder(final ScreenType nextScreen,
                         String[] imagesTexturePaths, Label[] labels, String fondPath, String changeOfImageSoundPath,
                         String endSoundPath, String pauseSoundPath, String nextBtnLabel,
                         String finishBtnLabel, String stylePath, Color textColor, UseFont usualFont,
                         final float timerScheduleTime, final int vibrationTime) {
        // Graphics
        if(imagesTexturePaths == null) {
            this.sequence = null;
        } else {
            if(labels == null) {
                // If there aren't any label specified, we'll build an empty list
                final int size = imagesTexturePaths.length;
                Label[] labelList = new Label[size];
                for (int i=0; i<size; i++)
                    labelList[i] = new LabelBuilder("").withStyle(UseFont.CLASSIC_SANS_NORMAL_BLACK).build();
                this.sequence = new FixedDialogueSequence(imagesTexturePaths, labelList);
            } else {
                this.sequence = new FixedDialogueSequence(imagesTexturePaths, labels);
            }

            Iterator<DialogueElement> iterator = this.sequence.iterator();
            while (iterator.hasNext()) {
                DialogueElement element = iterator.next();
                Image img = element.getImage();
                img.setWidth(this.game.viewport.getWorldWidth());
                img.setHeight(this.game.viewport.getWorldHeight());
                Label label = element.getLabel();
                img.setVisible(false);
                label.setVisible(false);
                this.stage.addActor(img);
                this.stage.addActor(label);
            }
            this.sequence.getFirstElement().getImage().setVisible(true);
            this.sequence.getFirstElement().getLabel().setVisible(true);
        }

        this.curImg = 0;
        this.fond = new Image(AssetsManager.getInstance().getTextureByPath(fondPath));
        this.fond.setVisible(false);
        this.stage.addActor(this.fond);
        // Sounds
        this.changeOfImageSound = AssetsManager.getInstance().getSoundByPath(changeOfImageSoundPath);
        this.endSound = AssetsManager.getInstance().getSoundByPath(endSoundPath);
        this.pauseSound = AssetsManager.getInstance().getSoundByPath(pauseSoundPath);
        // Buttons
        TextButton.TextButtonStyle style;
        TextButton.TextButtonStyle style_black; //TODO generify this
        if(usualFont != null) {
            style = FontHelper.getStyleFont(usualFont);
            style_black = FontHelper.getStyleFont(UseFont.AESTHETIC_NORMAL_BLACK);
        }else{
            style = Utils.getStyleFont(stylePath, 60, textColor);
            style_black = Utils.getStyleFont(stylePath, 60, Color.BLACK);
        }

        final float xButton = this.game.viewport.getWorldWidth() / 2.5f;
        this.next = new TextButtonBuilder(nextBtnLabel).withStyle(style).isVisible(true)
                .withPosition(xButton, this.game.viewport.getWorldHeight() / 10f).withListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Gdx.input.vibrate(vibrationTime / 4);
                        if(!nextImage()) {
                            // In the case when the image queue is empty (is == null or we saw every image)
                            next.setVisible(false);
                            finish.setVisible(true);
                        }
                    }
                }).build();
        this.finish = new TextButtonBuilder(finishBtnLabel).withStyle(style_black).isVisible(false)
                .withPosition(xButton, this.game.viewport.getWorldHeight() / 2f).withListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Gdx.input.vibrate(vibrationTime);
                        endSound.play();
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                resetScreen();
                                game.changeScreen(nextScreen);
                            }
                        }, timerScheduleTime);
                    }
                }).build();

        this.stage.addActor(this.next);
        this.stage.addActor(this.finish);
    }

    /**
     * Initialize the basic variables using the game, the stage and the screenName.
     * ATTENTION : the method *applyBundle* MUST come after this call ! Or nothing will appear on the screen
     * @param game the Kapotopia game
     * @param stage a stage that has been instancied beforehand
     * @param screenName the name of the screen, e.g. "mockupG1"
     */
    public CinematicScreen(final Kapotopia game, Stage stage, String screenName) {
        this.game = game;
        this.stage = stage;
        this.screenName = screenName;
        this.initialized = false;

        //We set elements to null because they MUST be initialized beforehand
        this.sequence = null;
        this.fond = null;
        this.curImg = -1;
        this.changeOfImageSound = null;
        this.endSound = null;
        this.pauseSound = null;
        this.next = null;
        this.finish = null;

        AssetsManager.getInstance().addStage(stage, screenName);
    }

    /**
     * MUST come after the constructor
     * @param params the bundle of parameters that will define the screen, use the specified object API for more information
     */
    protected void applyBundle(ParameterBundleBuilder params) {
        builder(params.getNextScreen(), params.getImagesTexturePaths(),
                params.getLabels(), params.getFondPath(), params.getChangeOfImageSoundPath(),
                params.getEndSoundPath(), params.getPauseSoundPath(), params.getNextBtnLabel(),
                params.getFinishBtnLabel(), params.getStylePath(), params.getTextColor(),
                params.getUsualfont(), params.getTimerScheduleTime(), params.getVibrationTime());
        initialized = true;
    }

    // Regular functions

    /**
     * Show the next image in the queue. If the queue is null or is empty, return false
     * @return false if the queue is null, or if is empty. True otherwise
     */
    public boolean nextImage() {
        if(sequence != null) {
            // We hide the current element
            setElementVisibility(false, curImg);
            if (curImg < sequence.getSize()-1) {
                // We make the next element visible
                setElementVisibility(true, ++curImg);
                changeOfImageSound.play();
                return true;
            } else {
                // We hide the last elements
                setElementVisibility(false, curImg-1);
                fond.setVisible(true);
            }
        }
        return false;
    }

    /**
     * Reset the screen at it's initial state
     */
    public void resetScreen() {
        curImg = 0;

        if(sequence != null) {
            Iterator<DialogueElement> iterator = sequence.iterator();
            while(iterator.hasNext()) {
                DialogueElement element = iterator.next();
                element.getImage().setVisible(false);
                element.getLabel().setVisible(false);
            }
            setElementVisibility(true, curImg);
            finish.setVisible(false);
            next.setVisible(true);
            fond.setVisible(false);
        } else {
            finish.setVisible(true);
            next.setVisible(false);
            fond.setVisible(true);
        }
    }

    /**
     * Set the element at the index in the sequence as visible or not
     * @param isVisible
     * @param index the index must be within 0 and sequence.size-1 or it will throw an AssertionError
     */
    private void setElementVisibility(boolean isVisible, int index) {
        DialogueElement element = sequence.getDialogueElement(index);
        element.getImage().setVisible(isVisible);
        element.getLabel().setVisible(isVisible);
    }

    /**
     * Set up the input processor with the StandardInputAdapter
     */
    protected void setUpInputProcessor() {
        InputMultiplexer im = new InputMultiplexer();
        im.addProcessor(new StandardInputAdapter(this, game));
        im.addProcessor(stage);
        Gdx.input.setInputProcessor(im);
    }



    @Override
    public void pause() {
        if(this.pauseSound != null) {
            this.pauseSound.play();
        }
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        if(initialized)
            resetScreen();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        AssetsManager.getInstance().disposeStage(screenName);
    }

    public class ParameterBundleBuilder {
        private ScreenType nextScreen;
        private String[] imagesTexturePaths;
        private Label[] labels;
        private String fondPath;
        private String changeOfImageSoundPath;
        private String endSoundPath;
        private String pauseSoundPath;
        private String nextBtnLabel;
        private String finishBtnLabel;
        private String stylePath;
        private Color textColor;
        private UseFont usualfont;
        private float timerScheduleTime;
        private int vibrationTime;

        public ParameterBundleBuilder(ScreenType nextScreen) {
            this.nextScreen = nextScreen;
            this.imagesTexturePaths = null;
            this.labels = null;
            this.fondPath = "FondNiveauBlanc2.png";
            this.changeOfImageSoundPath = "sound/bruitage/cmdrobot_videogame-jump.ogg";
            this.endSoundPath = "sound/bruitage/plasterbrain_game-start.ogg";
            this.pauseSoundPath = "sound/bruitage/crisstanza_pause.mp3";
            this.nextBtnLabel = "Next";
            this.finishBtnLabel = "Play";
            this.stylePath = "COMMS.ttf";
            this.textColor = Color.BLACK;
            this.usualfont = null;
            this.timerScheduleTime = 2f;
            this.vibrationTime = 200;
        }

        public ParameterBundleBuilder withTextures(String[] texturePaths) {
            this.imagesTexturePaths = texturePaths;
            return this;
        }

        public ParameterBundleBuilder withLabels(Label[] labels) {
            this.labels = labels;
            return this;
        }

        public ParameterBundleBuilder withFond(String fondPath) {
            this.fondPath = fondPath;
            return this;
        }

        public ParameterBundleBuilder withSoundToChangeImg(String changeOfImageSoundPath) {
            this.changeOfImageSoundPath = changeOfImageSoundPath;
            return this;
        }

        public ParameterBundleBuilder withSoundToEnd(String soundToEnd) {
            this.endSoundPath = soundToEnd;
            return this;
        }

        public ParameterBundleBuilder withSoundToPause(String pauseSoundPath) {
            this.pauseSoundPath = pauseSoundPath;
            return this;
        }

        public ParameterBundleBuilder withNextBtnTxt(String nextBtnTxt) {
            this.nextBtnLabel = nextBtnTxt;
            return this;
        }

        public ParameterBundleBuilder withFinishBtnTxt(String finishBtnTxt) {
            this.finishBtnLabel = finishBtnTxt;
            return this;
        }

        public ParameterBundleBuilder withStyle(String stylePath) {
            this.stylePath = stylePath;
            return this;
        }

        public ParameterBundleBuilder withTxtColor(Color txtColor) {
            this.textColor = txtColor;
            return this;
        }

        public ParameterBundleBuilder withStyle(UseFont useFont) {
            this.usualfont = useFont;
            return this;
        }

        public ParameterBundleBuilder withTimerScheduleTime(float timerScheduleTime) {
            this.timerScheduleTime = timerScheduleTime;
            return this;
        }

        public ParameterBundleBuilder withVibrationTime(int vibrationTime) {
            this.vibrationTime = vibrationTime;
            return this;
        }

        public ScreenType getNextScreen() {
            return nextScreen;
        }

        public String[] getImagesTexturePaths() {
            return imagesTexturePaths;
        }

        public Label[] getLabels() {
            return labels;
        }

        public String getFondPath() {
            return fondPath;
        }

        public String getChangeOfImageSoundPath() {
            return changeOfImageSoundPath;
        }

        public String getEndSoundPath() {
            return endSoundPath;
        }

        public String getPauseSoundPath() {
            return pauseSoundPath;
        }

        public String getNextBtnLabel() {
            return nextBtnLabel;
        }

        public String getFinishBtnLabel() {
            return finishBtnLabel;
        }

        public String getStylePath() {
            return stylePath;
        }

        public Color getTextColor() {
            return textColor;
        }

        public UseFont getUsualfont() {
            return usualfont;
        }

        public float getTimerScheduleTime() {
            return timerScheduleTime;
        }

        public int getVibrationTime() {
            return vibrationTime;
        }
    }

}
