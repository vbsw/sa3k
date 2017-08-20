
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.engine.Input;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class GameCtrl implements Input.Key.Listener {

    private boolean paused = false;

    public static boolean isBackPressed(final KeyCode keyCode) {
        return ((keyCode==KeyCode.ESCAPE)&&App.game.getScreen().isClosableByEscape())||keyCode==KeyCode.Q||keyCode==KeyCode.BACK_SPACE;
    }

    public static boolean isSelectionPressed(final KeyCode keyCode) {
        return keyCode==KeyCode.F||keyCode==KeyCode.SPACE||keyCode==KeyCode.ENTER;
    }

    public static boolean isUpPressed(final KeyCode keyCode) {
        return keyCode==KeyCode.UP||keyCode==KeyCode.I;
    }

    public static boolean isDownPressed(final KeyCode keyCode) {
        return keyCode==KeyCode.DOWN||keyCode==KeyCode.K;
    }

    public static Timeline createMovementToADirection(final Duration duration, final DoubleProperty axis, final double value) {
        final KeyFrame keyFrame = new KeyFrame(duration, new KeyValue(axis, value));
        final Timeline timeline = new Timeline(keyFrame);
        return timeline;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void keyPressed(final KeyCode keyCode) {}

    @Override
    public void keyPressedCurrently(final KeyCode keyCode) {
        // fullscreen
        if(keyCode==KeyCode.F11)
            App.game.getScreen().setFullscreen(true);

        // pause game
        if(keyCode==KeyCode.P) {
            if(paused) {
                paused = false;
                App.game.getStack().topPane().setEnabled(true);
            }
            else {
                paused = true;
                App.game.getStack().topPane().setEnabled(false);
            }
        }
    }

    @Override
    public void keyReleased(final KeyCode keyCode) {}

}
