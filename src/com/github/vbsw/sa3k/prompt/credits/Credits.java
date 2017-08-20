
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt.credits;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.Audio;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.engine.Layered.Action;
import com.github.vbsw.sa3k.prompt.Font;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class Credits implements Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    private static final Duration SCROLLING_DURATION = Duration.seconds(128);                // originaly 125, 25 á 1000
    private static final double   END_X              = -5000;

    private static Credits        instance           = null;

    private final CreditsPane     creditsPane        = new CreditsPane();
    private final Animation       scrollingAnimation = createScrollingAnimation(creditsPane);
    private final BackListener    backListener       = new BackListener();
    private final MediaPlayer     music              = new MediaPlayer(Audio.Music.credits);

    private boolean               enabled            = true;

    private Credits() {}

    private Animation createScrollingAnimation(final Node node) {
        final SequentialTransition seqTransition = new SequentialTransition();
        final FadeTransition fadeTransparent = new FadeTransition(Duration.millis(1), node);
        final TranslateTransition translateToBeginning = new TranslateTransition(Duration.millis(1), node);
        final FadeTransition fadeOpaque = new FadeTransition(Duration.seconds(3), node);
        final TranslateTransition translateToEnd = new TranslateTransition(SCROLLING_DURATION, node);

        fadeTransparent.setToValue(0);
        translateToBeginning.setToY(App.HEIGHT/2-Font.TITLE.getHeight());
        fadeOpaque.setToValue(1);
        translateToEnd.setToY(END_X);
        translateToEnd.setInterpolator(Interpolator.LINEAR);
        seqTransition.setAutoReverse(false);
        seqTransition.setCycleCount(Animation.INDEFINITE);
        seqTransition.setInterpolator(Interpolator.LINEAR);
        seqTransition.getChildren().addAll(fadeTransparent, translateToBeginning, fadeOpaque, translateToEnd);

        return seqTransition;
    }

    /*
     * *****************************
     * package
     * *****************************
     */

    void backToMenu() {
        scrollingAnimation.stop();
        instance = null;
        enabled = false;
        App.game.getStack().popPane().destory();

        // enable menu
        App.game.getStack().topPane().setVisible(true);
        App.game.getStack().topPane().setEnabled(true);
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public static final int LAYERS_NUMBER = 1;

    public static Credits getInstance() {
        if(instance==null)
            instance = new Credits();

        return instance;
    }

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();

        layered.addAnimation(scrollingAnimation);
        layered.addNode(creditsPane, 0);
        layered.getInput().addKeyListener(backListener);
        layered.addMediaPlayer(music);
        scrollingAnimation.play();
        music.setCycleCount(MediaPlayer.INDEFINITE);
        music.setAutoPlay(true);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void update() {}

}
