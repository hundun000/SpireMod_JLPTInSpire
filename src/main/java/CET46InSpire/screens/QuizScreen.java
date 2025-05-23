package CET46InSpire.screens;

import CET46InSpire.helpers.CNFontHelper;
import CET46InSpire.ui.*;
import basemod.abstracts.CustomScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CET46InSpire.relics.CETRelic;
import CET46InSpire.helpers.ImageElements;

import java.util.ArrayList;

public class QuizScreen extends CustomScreen {
    private static final Logger logger = LogManager.getLogger(QuizScreen.class.getName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CET46:WordScreen");
    private static final String[] TEXT;
    private static final float FRAME_X;
    private static final float FRAME_Y;
    private static final float FRAME_WIDTH;
    private static final float FRAME_HEIGHT;
    private static final float QUESTION_CX;
    private static final float QUESTION_CY;
    private static final float LEXICON_X;
    private static final float LEXICON_Y;
    private static final float WORD_CX;
    private static final float WORD_CY;
    private static final float WORD_PAD_CX;
    private static final float WORD_PAD_CY;
    public static final int WORD_COL_MAX;
    public static final int WORD_ROW_MAX;
    private static final float SCORE_X;
    private static final float SCORE_Y;
    public static final float WORD_BUT_W;
    public static final float WORD_BUT_H;
    private static final float BOTTOM_BUT_X;
    private static final float BOTTOM_BUT_Y;
    private static final float TIP_X;
    private static final float TIP_Y;
    private float delta_y = 0.0F;
    private String word;
    private String word_id;
    private String lexicon;
    private ArrayList<String> right_ans_list;
    private ArrayList<String> meaning_list;
    private boolean correction;
    private final CheckButton checkButton;
    private final ReturnButton returnButton;
    private final ArrayList<WordButton> wordButtons;
    private final InfoTip infoTip;
    public boolean ans_checked;
    public int right_ans_num;
    public int wrong_ans_num;
    public int score;
    private boolean isPerfect = false;
    private BitmapFont titleFont = CNFontHelper.charTitleFont;
    private BitmapFont descFont = CNFontHelper.charDescFont;

    public QuizScreen() {
        this.checkButton = new CheckButton(BOTTOM_BUT_X,FRAME_Y + BOTTOM_BUT_Y);
        this.checkButton.attached = true;
        this.checkButton.font_center = true;
        this.returnButton = new ReturnButton(BOTTOM_BUT_X,FRAME_Y + BOTTOM_BUT_Y);
        this.returnButton.attached = true;
        this.returnButton.font_center = true;
        this.infoTip = new InfoTip(FRAME_X + TIP_X,FRAME_Y + TIP_Y);
        this.infoTip.attached = true;
        this.wordButtons = new ArrayList<>();
        for (int i = 0; i < WORD_COL_MAX * WORD_ROW_MAX; i ++) {
            int col = i % WORD_COL_MAX;
            int row = i / WORD_ROW_MAX;
            WordButton w = new WordButton(WORD_CX + (col - 1) * WORD_PAD_CX, WORD_CY - (row - 1) * WORD_PAD_CY);
            w.attached = true;
            w.font_center = true;
            this.wordButtons.add(w);
        }

    }

    @Override
    public AbstractDungeon.CurrentScreen curScreen() {
        return Enum.WORD_SCREEN;
    }

    public void open(String word, String lexicon, ArrayList<String> right_ans_list, ArrayList<String> meaning_list,
                     String word_id, boolean correction) {
        this.word = word;
        this.word_id = word_id;
        this.lexicon = lexicon;
        this.right_ans_list = right_ans_list;
        this.meaning_list = meaning_list;
        this.correction = correction;
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.NONE) {
            logger.info("wtf? why?");
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        if (CET46Panel.pureFont) {
            this.titleFont = CNFontHelper.pureTitleFont;
            this.descFont = CNFontHelper.pureDescFont;
        } else {
            this.titleFont = CNFontHelper.charTitleFont;
            this.descFont = CNFontHelper.charDescFont;
        }
        reopen();
    }

    @Override
    public void reopen() {
        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.screen = curScreen();
        AbstractDungeon.overlayMenu.proceedButton.hide();
        AbstractDungeon.overlayMenu.cancelButton.hide();
        // fix black screen
        AbstractDungeon.overlayMenu.hideBlackScreen();
        this.delta_y = Settings.HEIGHT;

        this.infoTip.show("text");
        this.checkButton.show(TEXT[0]);
        for (WordButton w: this.wordButtons) {
            w.reset();
        }
        for (int i = 0; i < this.meaning_list.size(); i++) {
            this.wordButtons.get(i).show(this.meaning_list.get(i));
        }

        this.ans_checked = false;
        this.right_ans_num = 0;
        this.wrong_ans_num = 0;
        this.score = 0;

    }

    @Override
    public void close() {
        // get score
        for (AbstractRelic r: AbstractDungeon.player.relics) {
            if (r instanceof CETRelic) {
                if (!this.correction) {
                    ((CETRelic) r).scoreCounter = this.score;
                    ((CETRelic) r).updatePerfectCounter(this.isPerfect);
                } else {
                    ((CETRelic) r).scoreCounter = Math.max(((CETRelic) r).scoreCounter, this.score);
                }
                if (this.score == 0) {
                    ((CETRelic) r).notebook.addItem(this.word_id);
                }
                if (this.correction && this.isPerfect) {
                    ((CETRelic) r).notebook.reduceItem(this.word_id);
                    ((CETRelic) r).givePotion();
                }
                break;
            }
        }
        this.infoTip.hideInstantly();
        // same as AbstractDungeon.genericScreenOverlayReset
        if (AbstractDungeon.previousScreen == null) {
            if (AbstractDungeon.player.isDead) {
                AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.DEATH;
            } else {
                AbstractDungeon.isScreenUp = false;
                AbstractDungeon.overlayMenu.hideBlackScreen();
            }
        }
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.player.isDead) {
            AbstractDungeon.overlayMenu.showCombatPanels();
        }
    }

    @Override
    public void update() {
        updateFrame();
        if (this.ans_checked) {
            this.returnButton.attachedUpdate(FRAME_Y + BOTTOM_BUT_Y + this.delta_y);
        } else {
            this.checkButton.attachedUpdate(FRAME_Y + BOTTOM_BUT_Y + this.delta_y);
        }
        this.infoTip.attachedUpdate(FRAME_Y + TIP_Y + this.delta_y);
        for (WordButton w: this.wordButtons) {
            if (!w.isHidden) {
                w.attachedRelUpdate(FRAME_Y + this.delta_y);
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.draw(ImageElements.WORD_SCREEN_BASE, FRAME_X, FRAME_Y + this.delta_y, FRAME_WIDTH, FRAME_HEIGHT);
        Color font_color = Color.BLACK.cpy();
        if (ImageElements.darkMode) {
            font_color = Color.WHITE.cpy();
        }
        this.renderQuestion(sb, font_color);
        this.infoTip.render(sb);
        if (this.ans_checked) {
            FontHelper.renderFontLeft(sb, this.descFont, uiStrings.TEXT[2] + this.score,
                    SCORE_X, SCORE_Y + this.delta_y, font_color);
            this.returnButton.fontColor = font_color;
            this.returnButton.render(sb);
        } else {
            this.checkButton.fontColor = font_color;
            this.checkButton.render(sb);
        }
    }

    @Override
    public void openingSettings() {
        AbstractDungeon.previousScreen = curScreen();
    }

    private void updateFrame() {
        if (this.delta_y == 0.0F) {
            return;
        }
        if (CET46Panel.fastMode) {
            this.delta_y = MathUtils.lerp(this.delta_y, Settings.HEIGHT / 2.0F - 540.0F * Settings.yScale,
                    Gdx.graphics.getDeltaTime() * 50.0F);
            if (Math.abs(this.delta_y - 0.0F) < 5.0F)
                this.delta_y = 0.0F;
        } else {
            this.delta_y = MathUtils.lerp(this.delta_y, Settings.HEIGHT / 2.0F - 540.0F * Settings.yScale,
                    Gdx.graphics.getDeltaTime() * 5.0F);
            if (Math.abs(this.delta_y - 0.0F) < 0.5F)
                this.delta_y = 0.0F;
        }
    }

    private void renderQuestion(SpriteBatch sb, Color font_color) {
        FontHelper.renderFontCentered(sb, this.titleFont, this.word,
                QUESTION_CX, FRAME_Y + QUESTION_CY + this.delta_y, font_color);
        if (CET46Panel.showLexicon) {
            String lexicon = this.correction ? this.lexicon : TEXT[3] + this.lexicon;
            FontHelper.renderFontLeftTopAligned(sb, this.descFont, lexicon,
                    LEXICON_X, FRAME_Y + LEXICON_Y + this.delta_y, font_color);
        }
        for (WordButton w: this.wordButtons) {
            if (!w.isHidden) {
                w.fontColor = font_color;
                w.render(sb, this.descFont);
            }
        }
    }

    public void checkAns() {
        if (this.ans_checked) {
            return;
        }
        this.ans_checked = true;
        // check
        this.isPerfect = true;
        for (WordButton w: this.wordButtons) {
            if (w.isHidden) {
                continue;
            }
            w.lockGlowState = true;
            if (w.glowing) {
                if (this.right_ans_list.contains(w.buttonText)) {
                    this.right_ans_num++;
                    w.setGlowColor(Color.GREEN.cpy());
                } else {
                    this.isPerfect = false;
                    this.wrong_ans_num++;
                    w.setGlowColor(Color.RED.cpy());
                }
            } else if (this.right_ans_list.contains(w.buttonText)) {
                // right but not chosen
                this.isPerfect = false;
                w.glowing = true;
                w.setGlowColor(Color.YELLOW.cpy());
            }
        }
        getScore();
        if (Settings.isDebug) {
            logger.info("Right: {}, Wrong: {}", this.right_ans_num, this.wrong_ans_num);
        }
        if (CET46Panel.ignoreCheck) {
            this.returnButton.buttonClicked();
        } else {
            this.returnButton.showInstantly(TEXT[1]);
        }
    }

    public void getScore() {
        this.score = this.right_ans_num - this.wrong_ans_num;
        if (CET46Panel.casualMode && this.score < 1) {
            this.score = 1;
            return;
        }
        if (this.score < 0) {
            this.score = 0;
        }
    }

    public static class Enum
    {
        @SpireEnum
        public static AbstractDungeon.CurrentScreen WORD_SCREEN;
    }

    static {
        TEXT = uiStrings.TEXT;
        FRAME_WIDTH = 1520.0F * Settings.xScale;
        FRAME_HEIGHT = 800.0F * Settings.yScale;
        FRAME_X = 0.5F * (Settings.WIDTH - FRAME_WIDTH);
        FRAME_Y = 0.5F * (Settings.HEIGHT - FRAME_HEIGHT);
        QUESTION_CX = 0.5F * Settings.WIDTH;
        QUESTION_CY = 680.0F * Settings.yScale;
        LEXICON_X = QUESTION_CX - 500.0F * Settings.xScale;
        LEXICON_Y = QUESTION_CY;
        WORD_CX = 0.5F * Settings.WIDTH;
        WORD_CY = 340.0F * Settings.yScale;
        WORD_PAD_CX = 0.305F * FRAME_WIDTH;
        WORD_PAD_CY = 0.2F * FRAME_HEIGHT;
        SCORE_X = 0.7F * Settings.WIDTH;
        SCORE_Y = 0.7F * Settings.HEIGHT;
        WORD_BUT_W = 0.3F * FRAME_WIDTH;
        WORD_BUT_H = 0.195F * FRAME_HEIGHT;
        BOTTOM_BUT_X = 0.5F * (Settings.WIDTH - ReturnButton.IMG_W);
        BOTTOM_BUT_Y = 30.0F * Settings.yScale;
        TIP_X = 0.05F * FRAME_WIDTH;
        TIP_Y = 0.85F * FRAME_HEIGHT;
        WORD_COL_MAX = 3;
        WORD_ROW_MAX = 3;
    }
}
