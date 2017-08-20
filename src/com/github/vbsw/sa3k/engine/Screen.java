
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.engine;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * Class to manage screen settings.
 * 
 * @author Vitali Baumtrok
 */
public class Screen {

    /*
     * *****************************
     * private
     * *****************************
     */

    /**
     * The listener for JavaFX to change the
     * flags <tt>fullscreen</tt> and <tt>closableByEscape</tt> when
     * a switch between windowed and fullscreen mode happens.
     */
    private final FullscreenListener fullscreenListener;
    /**
     * The JavaFX stage of this JavaFX-application.
     */
    private final Stage              stage;

    /**
     * A flag indicating whether the window can be closed
     * with the key ESCAPE or not.
     */
    private boolean                  closableByEscape;
    /**
     * A flag indicating whether the stage is in fullscreen.
     */
    private boolean                  fullscreen;

    /**
     * The listener for JavaFX to change the
     * flags <tt>fullscreen</tt> and <tt>closableByEscape</tt> when
     * a switch between windowed and fullscreen mode happens.
     * 
     * @author Vitali Baumtrok
     */
    private class FullscreenListener implements Input.Key.Listener, ChangeListener<Boolean> {

        /**
         * Returns always <tt>true</tt>, because it lasts
         * for the whole game.
         * 
         * @return <tt>true</tt>
         */
        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void keyPressed(KeyCode keyCode) {}

        @Override
        public void keyPressedCurrently(KeyCode keyCode) {}

        /**
         * Changes the flag <tt>closableByEscape</tt> when
         * the key ESCAPE has been released.
         */
        @Override
        public void keyReleased(KeyCode keyCode) {
            if(keyCode==KeyCode.ESCAPE&&!closableByEscape)
                closableByEscape = true;
        }

        /**
         * Changes the flags <tt>fullscreen</tt> and <tt>closableByEscape</tt> when
         * the stage switches between windowed and fullscreen mode.
         */
        @Override
        public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue, final Boolean newValue) {
            fullscreen = newValue;
            if(fullscreen)
                closableByEscape = false;
        }

    }

    /*
     * *****************************
     * package
     * *****************************
     */

    /**
     * Returns a JavaFX key listener.
     * 
     * @return A JavaFX key listener.
     */
    Input.Key.Listener getKeyListener() {
        return fullscreenListener;
    }

    /**
     * Returns a JavaFX change listener.
     * 
     * @return A JavaFX change listener.
     */
    ChangeListener<Boolean> getFullscreenListener() {
        return fullscreenListener;
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    /**
     * The constructor.<br>
     * <br>
     * This class manages screen settings.
     * 
     * @param stage
     *            The JavaFX stage of this application.
     * @param isFullscreen
     *            If <tt>true</tt> the stage will be set
     *            to fullscreen, otherwise to windowed mode.
     */
    public Screen(final Stage stage, final boolean isFullscreen) {
        fullscreenListener = new FullscreenListener();
        this.stage = stage;

        closableByEscape = !isFullscreen;
        fullscreen = isFullscreen;
    }

    /**
     * Sets the stage to fullscreen.
     * 
     * @param beFullscreen
     *            If <tt>true</tt> the stage will be set
     *            to fullscreen, otherwise to windowed mode.
     */
    public void setFullscreen(boolean beFullscreen) {
        fullscreen = beFullscreen;
        stage.setFullScreen(beFullscreen);
    }

    /**
     * Returns whether the stage is in fullscreen.
     * 
     * @return <tt>true</tt> if the stage is in
     *         fullscreen, <tt>false</tt> otherwise.
     */
    public boolean isFullscreen() {
        return fullscreen;
    }

    /**
     * Returns <tt>true</tt>, if stage is in window
     * mode, <tt>false</tt> otherwise.<br>
     * <br>
     * It may not always be desired to close the application
     * with the key ESCAPE,
     * because by default when the key ESCAPE is pressed
     * the stage returns from fullscreen to windowed mode.
     * 
     * @return <tt>true</tt>, if stage is in window
     *         mode, <tt>false</tt> otherwise.
     */
    public boolean isClosableByEscape() {
        return closableByEscape;
    }

}
