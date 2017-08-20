
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.engine;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Wrapper for the engine components. Subclass this
 * class to be able to call the start method. The start
 * method starts the game.
 * 
 * @author Vitali Baumtrok
 */
public class Game {

    /*
     * *****************************
     * private
     * *****************************
     */

    /**
     * Input component.
     */
    private final Input    input = new Input();

    /**
     * Time component.
     */
    private final Time     time  = new Time();

    /**
     * Loop component.
     */
    private final GameLoop loop  = new GameLoop();

    /**
     * Screen component.
     */
    private final Screen   screen;

    /**
     * The JavaFX <tt>Stage</tt> of this application.
     */
    private final Stage    stage;

    /**
     * Class that manages the time of the game.
     * 
     * @author Vitali Baumtrok
     */
    private static class Time {

        /**
         * The time when this class has been instantiated.
         * It's used to compute passed time since the
         * game has been started.
         */
        private final long startTime  = System.nanoTime();
        /**
         * The passed time in milli seconds since the game
         * has been started.
         */
        private Duration   passedTime = Duration.millis(0);

        /*
         * *****************************
         * public
         * *****************************
         */

        /**
         * Updates the time since this class has been
         * instantiated, i.e. game has been started.
         * 
         * @param currentNanoSeconds
         *            The current system time in nano seconds.
         */
        public void update(final long currentNanoSeconds) {
            final long newPassedTimeNano = currentNanoSeconds-startTime;
            final double newPassedTimeMillis = newPassedTimeNano/1_000_000.0;
            passedTime = Duration.millis(newPassedTimeMillis);
        }

        /**
         * Returns the passed time in milli seconds since the game
         * has been started.
         * 
         * @return The passed time in milli seconds since the game
         *         has been started.
         */
        public Duration getTime() {
            return passedTime;
        }

    }

    /**
     * This is a listener. It listens to the size of the game
     * window. If the size of the window changes the listener
     * adjusts the size of the game field view.
     * 
     * @author Vitali Baumtrok
     */
    private class SizeScaleListener implements ChangeListener<Number> {

        /*
         * *****************************
         * private
         * *****************************
         */

        /**
         * The 1:1 width of the view, i.e. the initial width.
         */
        private final double WIDTH;

        /**
         * The 1:1 height of the view, i.e. the initial height.
         */
        private final double HEIGHT;

        /*
         * *****************************
         * public
         * *****************************
         */

        /**
         * The constructor.<br>
         * <br>
         * The Listener listens to the size of the game
         * window. If the size of the window changes the listener
         * adjusts the size of the game field view.
         * 
         * @param width
         *            The initial width of the view.
         * @param height
         *            The initial height of the view.
         */
        public SizeScaleListener(final double width, final double height) {
            WIDTH = width;
            HEIGHT = height;
        }

        /**
         * A callback method of the <tt>ChangeListener</tt>.
         * It is called when the size of the window has changed.<br>
         * <br>
         * Adjusts the size of the game view according to the size
         * of the window while keeping the initial aspect ratio
         * of the game field view.
         * 
         * @param observable
         *            The <tt>ObservableValue</tt> which value changed.
         *            Isn't used here.
         * @param oldValue
         *            The old value. Isn't used here.
         * @param newValue
         *            The new value. Isn't used here.
         */
        @Override
        public void changed(final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) {
            final Scene scene = stage.getScene();
            final StackPane root = (StackPane)scene.getRoot();
            final double rootWidth = scene.getWidth();
            final double rootHeight = scene.getHeight();
            final double widthRatio = rootWidth/WIDTH;
            final double heightRatio = rootHeight/HEIGHT;
            final double scaleRatioX, scaleRatioY;
            final double correctionFactor = widthRatio==1.0||heightRatio==1.0 ? 1.0 : 1.003;

            scaleRatioX = scaleRatioY = (widthRatio<heightRatio ? widthRatio : heightRatio)*correctionFactor;
            root.setScaleX(scaleRatioX);
            root.setScaleY(scaleRatioY);
        }

    }

    /**
     * The game loop.<br>
     * <br>
     * The game loop updates the time and the scene graph.
     * 
     * @author Vitali Baumtrok
     */
    private class GameLoop extends AnimationTimer {

        /*
         * *****************************
         * public
         * *****************************
         */

        /**
         * The update method.<br>
         * <br>
         * The update method updates the time and the scene graph.
         * 
         * @param currentTime
         *            The current system time in milli seconds.
         */
        @Override
        public void handle(final long currentTime) {
            final Stack stack = (Stack)stage.getScene().getRoot();

            time.update(currentTime);
            stack.update(currentTime);
        }

    }

    /*
     * *****************************
     * protected
     * *****************************
     */

    /**
     * Starts the game.<br>
     * <br>
     * It is protected to hide it from casual game components.
     */
    protected void start() {
        loop.start();
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    /**
     * The constructor.<br>
     * <br>
     * <tt>Game</tt> is a Wrapper for the engine components. Subclass
     * this class to be able to call the start method. The start
     * method starts the game.
     * 
     * @param stage
     *            The JavaFX <tt>Stage</tt>.
     * @param width
     *            The initial width of the game field view.
     * @param height
     *            The initial height of the game field view.
     * @param fullscreen
     *            Start game in fullscreen mode. If true
     *            game is in fullscreen, else in window mode.
     */
    public Game(final Stage stage, final double width, final double height, final boolean fullscreen) {
        this.screen = new Screen(stage, fullscreen);
        this.stage = stage;

        final Stack root = new Stack(width, height);
        final Scene scene = new Scene(root, width, height);
        final SizeScaleListener sizeScaleListener = new SizeScaleListener(width, height);

        input.addKeyListener(screen.getKeyListener());
        scene.addEventHandler(KeyEvent.KEY_PRESSED, input.getKeyPressedHandler());
        scene.addEventHandler(KeyEvent.KEY_RELEASED, input.getKeyReleasedHandler());
        scene.widthProperty().addListener(sizeScaleListener);
        scene.heightProperty().addListener(sizeScaleListener);
        stage.setScene(scene);
        stage.fullScreenProperty().addListener(screen.getFullscreenListener());
        stage.show();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
        stage.setFullScreen(fullscreen);
    }

    /**
     * Sets the very background color of the window.
     * 
     * @param color
     *            The color of the background.
     */
    public void setBgColor(final Color color) {
        stage.getScene().setFill(color);
    }

    /**
     * Getter for the <tt>Screen</tt>.
     * 
     * @return Returns the <tt>Screen</tt>.
     */
    public Screen getScreen() {
        return screen;
    }

    /**
     * Getter for the <tt>Stack</tt>.
     * 
     * @return Returns the <tt>Stack</tt>.
     */
    public Stack getStack() {
        return (Stack)stage.getScene().getRoot();
    }

    /**
     * Getter for the <tt>Time</tt>.
     * 
     * @return Returns the <tt>Time</tt>.
     */
    public Duration getTime() {
        return time.getTime();
    }

    /**
     * End the game and the program.
     */
    public void quit() {
        System.exit(0);
    }

}
