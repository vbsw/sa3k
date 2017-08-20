
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.engine;

import java.util.ArrayList;
import java.util.HashSet;

import javafx.animation.Animation;
import javafx.collections.ObservableList;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * This is a pane that manages <tt>Action</tt>s, animations,
 * music ({@link javafx.scene.media.MediaPlayer})
 * and <tt>Node</tt>s. The <tt>Node</tt>s are grouped in layers.
 * Therefore its a layered pane.<br>
 * <br>
 * How to use:<br>
 * An <tt>Action</tt> is a piece of program that is executed every
 * iteration of the game loop. An <tt>Action</tt> should contain logic
 * of the items (i.e. nodes) but not their movement or animation.
 * The movement and animation of the items should be implemented
 * with {@link javafx.animation.Animation} and then added to
 * this <tt>Layered</tt>. <br>
 * Every animation, action, music and input listeners
 * that has been added to this <tt>Layered</tt> will be enabled and
 * disabled together with the <tt>Layered</tt>. That acts like a pause
 * function.<br>
 * When the <tt>Layered</tt> gets disabled animations and music are set
 * to paused. The adding, removing and stopping animations and music
 * on this <tt>Layered</tt> has to be done by the programer.
 * 
 * @author Vitali Baumtrok
 */
public class Layered extends StackPane {

    /*
     * *****************************
     * private
     * *****************************
     */

    /**
     * The time of this Layered.
     */
    private final Timer                  timer              = new Timer();

    /**
     * Actions that will be updated every game loop iteration.
     * They are in a flat structure.
     */
    private final ArrayList<Action>      actions            = new ArrayList<>();

    /**
     * Animations belonging to this pane.
     */
    private final HashSet<Animation>     animations         = new HashSet<>();

    /**
     * Animations that are on pause.
     */
    private final ArrayList<Animation>   animationsPaused   = new ArrayList<>();

    /**
     * <tt>MediaPlayer</tt>s belonging to this pane.
     */
    private final HashSet<MediaPlayer>   mediaPlayers       = new HashSet<>();

    /**
     * <tt>MediaPlayer</tt>s that are on pause.
     */
    private final ArrayList<MediaPlayer> mediaPlayersPaused = new ArrayList<>();

    /**
     * The input handler. Every layered pane, <tt>Layered</tt>, has its own.
     */
    private final Input                  input              = new Input();
    /**
     * The collision detector. Every layered pane, <tt>Layered</tt>, has its own.
     */
    private final Collision              collision;

    /**
     * The layeres in which the nodes are grouped.
     */
    private final AnchorPane[]           layers;

    /**
     * Indicates whether a background node is set.
     */
    private boolean                      bgIsSet            = false;

    /**
     * A class to hold the time of the Layered.
     * 
     * @author Vitali Baumtrok
     */
    private static class Timer {

        /**
         * The time when this class has been instantiated.
         * It's used to compute running duration.
         */
        private Duration startTime       = null;
        /**
         * The running duration saves the duration how long
         * the Layered was in use.
         */
        private Duration runningDuration = Duration.millis(0);
        /**
         * The running duration saves the duration how long
         * the Layered was paused.
         */
        private Duration pausedDuration  = Duration.millis(0);

        /**
         * Indicates that the timer has been paused. It is used
         * to compute the interval between the pause event and
         * the continuation of the timer (this is when the update
         * method is called again).
         */
        private boolean  paused          = false;

        /*
         * *****************************
         * public
         * *****************************
         */

        /**
         * Updates the timer. If the timer has been set to pause
         * previously then the timer is continued with this
         * method call.
         * 
         * @param currentNanoSeconds
         *            The time in nano seconds.
         */
        public void update(final long currentNanoSeconds) {
            final double currentTimeMillis = currentNanoSeconds/1_000_000.0;
            final Duration currentTime = Duration.millis(currentTimeMillis);
            if(startTime==null)
                startTime = currentTime;
            final Duration passedDuration = currentTime.subtract(startTime);
            if(paused) {
                final Duration passedDurationRegistered = runningDuration.add(pausedDuration);
                final Duration passedDurationUnregistered = passedDuration.subtract(passedDurationRegistered);
                pausedDuration = pausedDuration.add(passedDurationUnregistered);
                paused = false;
            }
            else {
                runningDuration = passedDuration.subtract(pausedDuration);
            }
        }

        /**
         * Returns the current time.
         * 
         * @return The current time.
         */
        public Duration getTime() {
            return runningDuration;
        }

        /**
         * Pause the timer. The timer will start on his own when
         * the update method is called.
         */
        public void pause() {
            paused = true;
        }

    }

    /*
     * *****************************
     * package
     * *****************************
     */

    /**
     * The constructor.<br>
     * <br>
     * <tt>Layered</tt> is a pane that manages <tt>Action</tt>s and <tt>Node</tt>s.
     * The <tt>Node</tt>s are grouped in layers. Therefore its a
     * layered pane.<br>
     * <br>
     * How to use:<br>
     * An <tt>Action</tt> is a piece of program that is executed every
     * iteration of the game loop. An <tt>Action</tt> should contain logic
     * of the items (i.e. nodes) but not their movement or animation.
     * The movement and animation of the items should be implemented
     * with {@link javafx.animation.Animation} and then added to
     * this <tt>Layered</tt>. <br>
     * Every animation, action, music and input listeners
     * that has been added to this <tt>Layered</tt> will be enabled and
     * disabled together with the <tt>Layered</tt>. That acts like a pause
     * function.<br>
     * When the <tt>Layered</tt> gets disabled animations and music are set
     * to paused. The adding, removing and stopping animations and music
     * on this <tt>Layered</tt> has to be done by the programer.
     * 
     * @param width
     *            The width of the pane.
     * @param height
     *            The height of the pane.
     * @param layersNumber
     *            The number of layers this <tt>Layered</tt> should have.
     */
    Layered(final double width, final double height, final int layersNumber) {
        collision = new Collision(layersNumber);
        layers = new AnchorPane[layersNumber];

        setCacheHint(CacheHint.QUALITY);
        setMinSize(width, height);
        setMaxSize(width, height);
        setClip(new Rectangle(width, height));

        final ObservableList<Node> nodes = getChildren();

        for(int i = 0; i<layers.length; i++) {
            final AnchorPane layer = new AnchorPane();
            layers[i] = layer;
            nodes.add(layer);
        }
    }

    /**
     * Updates the collision detection and actions. Afterwards the
     * nodes which are disabled are removed.
     * 
     * @param currentTime
     */
    void update(final long currentNanoSeconds) {
        // update time
        timer.update(currentNanoSeconds);

        // update collision
        collision.update();

        // update actions
        for(int i = 0; i<actions.size(); i++) {
            final Action action = actions.get(i);
            if(action.isEnabled()) {
                action.update();
            }
            else {
                actions.remove(i);
                i--;
            }
        }

        // clean up nodes
        for(int i = 0; i<layers.length; i++) {
            final AnchorPane layer = layers[i];
            final ObservableList<Node> nodes = layer.getChildren();

            for(int j = 0; j<nodes.size(); j++) {
                final Node node = nodes.get(j);
                if(node.isDisabled()) {
                    nodes.remove(j);
                    j--;
                }
            }
        }
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    /**
     * Returns the input handler.
     * 
     * @return The input handler.
     */
    public Input getInput() {
        return input;
    }

    /**
     * Returns the collision detection handler.
     * 
     * @return The collision detection handler.
     */
    public Collision getCollision() {
        return collision;
    }

    /**
     * Enables or disables this <tt>Layered</tt>.<br>
     * <br>
     * Disabling means that the background is frozen,
     * the input isn't handled anymore and all
     * animations are stopped.
     * 
     * @param b
     *            If <tt>true</tt> the <tt>Layered</tt> is enabled,
     *            otherwise disabled.
     */
    public void setEnabled(final boolean b) {
        if(isDisabled()!=(!b)) {
            // enable / continue time, animations and media players
            if(b) {
                // timer
                // timer is continued automaticly by next timer.update

                // animations
                for(Animation animation : animationsPaused)
                    if(animation.getStatus()==Animation.Status.PAUSED)
                        animation.play();
                animationsPaused.clear();
                // media players
                for(MediaPlayer mediaPlayer : mediaPlayersPaused)
                    if(mediaPlayer.getStatus()==MediaPlayer.Status.PAUSED)
                        mediaPlayer.play();
                mediaPlayersPaused.clear();
            }
            // disable / pause time, animations and media players
            else {
                // time
                timer.pause();
                // animations
                for(Animation animation : animations) {
                    if(animation.getStatus()==Animation.Status.RUNNING) {
                        animation.pause();
                        animationsPaused.add(animation);
                    }
                }
                // media players
                for(MediaPlayer mediaPlayer : mediaPlayers) {
                    if(mediaPlayer.getStatus()==MediaPlayer.Status.PLAYING) {
                        mediaPlayer.pause();
                        mediaPlayersPaused.add(mediaPlayer);
                    }
                }
            }
        }
        setDisable(!b);
        setCache(b);
        input.setEnabled(b);
    }

    /**
     * Sets a node as the background for this <tt>Layered</tt>.
     * 
     * @param node
     *            The new backgound.
     */
    public void setBg(final Node node) {
        if(bgIsSet) {
            final ObservableList<Node> nodes = getChildren();
            if(node==null)
                nodes.remove(0);
            else
                nodes.set(0, node);
        }
        else {
            if(node!=null) {
                final ObservableList<Node> nodes = getChildren();
                nodes.add(0, node);
            }
        }
    }

    /**
     * Adds a new <tt>Stack</tt> to a layer.<br>
     * <br>
     * CAUTION: This functionality may not work properly,
     * i.e. cause bugs in your program!
     * 
     * @param width
     *            The width of the <tt>Stack</tt>.
     * @param height
     *            The height of the <tt>Stack</tt>.
     * @param layerIndex
     *            The index of the layer to which
     *            the <tt>Stack</tt> is to be added.
     * @return The new added <tt>Stack</tt>.
     */
    public Stack addStack(final double width, final double height, final int layerIndex) {
        final Stack stack = new Stack(width, height);
        layers[layerIndex].getChildren().add(stack);
        // TODO can't remember, something with adjust stack
        return stack;
    }

    /**
     * Adds a node to a layer.
     * <p>
     * To remove a node disable it.
     * 
     * @param node
     *            The node to be added.
     * @param layerIndex
     *            The index of the layer to which the node
     *            is to be added.
     */
    public void addNode(final Node node, final int layerIndex) {
        layers[layerIndex].getChildren().add(node);
    }

    /**
     * Adds an <tt>Action</tt>. The method <tt>init()</tt> of
     * this <tt>Action</tt> is called.
     * <p>
     * To remove an action, disable it.
     * 
     * @param action
     *            The <tt>Action</tt> to be added.
     */
    public void addAction(final Action action) {
        actions.add(action);
        action.init();
    }

    /**
     * Register an animation. This animation will be set on pause if
     * the <tt>Layered</tt> gets disabled.
     * 
     * @param animation
     *            The animation to add.
     */
    public void addAnimation(final Animation animation) {
        animations.add(animation);
    }

    /**
     * Removes previously added animation. If the animation
     * is currently set on pause it will be stopped.
     * 
     * @param animation
     *            The animation to remove.
     */
    public void removeAnimation(final Animation animation) {
        animations.remove(animation);
        if(isDisabled()&&animationsPaused.remove(animation))
            animation.stop();
    }

    /**
     * Register a <tt>MediaPlayer</tt>. This animation will be set on
     * pause if the <tt>Layered</tt> gets disabled.
     * 
     * @param mediaPlayer
     *            The <tt>MediaPlayer</tt> to add.
     */
    public void addMediaPlayer(final MediaPlayer mediaPlayer) {
        mediaPlayers.add(mediaPlayer);
    }

    /**
     * Removes a previously added <tt>MediaPlayer</tt>. If
     * the <tt>MediaPlayer</tt> is currently set on pause
     * it will be stopped.
     * 
     * @param mediaPlayer
     *            The <tt>MediaPlayer</tt> to remove.
     */
    public void removeMediaPlayer(final MediaPlayer mediaPlayer) {
        mediaPlayers.remove(mediaPlayer);
        if(isDisabled()&&mediaPlayersPaused.remove(mediaPlayer))
            mediaPlayer.stop();
    }

    /**
     * Returns the current time of the Layered.
     * 
     * @return The current time.
     */
    public Duration getTime() {
        return timer.getTime();
    }

    /**
     * Stops the animations and media player. Removes everything
     * from this Layered, for example the input listeners and collision
     * detections with the collision detection maps.<br>
     * <br>
     * It does not disable the Layered.
     */
    public void destory() {
        for(Animation animation : animations)
            animation.stop();
        for(MediaPlayer mediaPlayer : mediaPlayers)
            mediaPlayer.stop();
        actions.clear();
        animations.clear();
        mediaPlayers.clear();
        input.clear();
        collision.clear();
    }

    /**
     * Inteface for an action. An action can be added to
     * a <tt>Layered</tt> where it is called every iteration
     * of the game loop.
     * 
     * @author Vitali Baumtrok
     */
    public static interface Action {

        /**
         * An <tt>Action</tt> as null-object (it does nothing).
         */
        public static final Action NULL = new Action() {
                                            @Override
                                            public void init() {}

                                            @Override
                                            public boolean isEnabled() {
                                                return false;
                                            }

                                            @Override
                                            public void update() {}
                                        };

        /**
         * Is called when added to a <tt>Layered</tt>.
         */
        public void init();

        /**
         * Indicates whether this action is enabled. If this
         * returns <tt>false</tt> while the iteration of the
         * game loop, then this action is removed from
         * this <tt>Layered</tt>.
         * 
         * @return <tt>true</tt>, if <tt>Action</tt> is enabled, <tt>false</tt> otherwise.
         */
        public boolean isEnabled();

        /**
         * Updates the the action.<br>
         * <br>
         * This is called one time per iteration of the game loop.
         */
        public void update();

    }

    /**
     * A class that adds actions to a layered in certain time delays.<br>
     * Use the reset method to reset the adding and use the update
     * method to process the adding.
     * 
     * @author Vitali Baumtrok
     */
    public static class ActionsQueue {

        /*
         * *****************************
         * private
         * *****************************
         */

        /**
         * Indicates the end of the queue.
         */
        private static final Duration  BLOCKER         = Duration.INDEFINITE;

        /**
         * The actions to add to a Layered.
         */
        private final Layered.Action[] actions;
        /**
         * The delays between the adding of the actions.
         */
        private final Duration[]       delays;

        /**
         * The time from which the queue starts its messurements.
         */
        private Duration               startTime       = BLOCKER;
        /**
         * The sum of time of the already added actions.
         */
        private Duration               accumulatedTime = Duration.ZERO;
        /**
         * The current read index of the actions array.
         */
        private int                    rIndex          = 0;
        /**
         * The current write index of the actions array.
         */
        private int                    wIndex          = 0;

        /*
         * *****************************
         * public
         * *****************************
         */

        /**
         * The constructor.<br>
         * <br>
         * This class adds actions to a layered in certain time delays.<br>
         * Use the reset method to reset the adding and use the update
         * method to process the adding.
         * 
         * @param size
         *            The maximum number of actions that can be added.
         */
        public ActionsQueue(final int size) {
            actions = new Layered.Action[size+1];
            delays = new Duration[size+1];
            // null object, it marks the end of array
            actions[0] = Layered.Action.NULL;
            delays[0] = BLOCKER;
        }

        /**
         * Resets the time of this queue. This starts the queue.
         * 
         * @param time
         *            The current time of the Layered.
         */
        public void reset(final Duration time) {
            startTime = time;
            accumulatedTime = Duration.ZERO;
            rIndex = 0;
        }

        /**
         * Adds an Action to the queue, which will be later added to a
         * Layered. Later means when the update method is called.
         * 
         * @param action
         *            The action.
         * @param delay
         *            The delay after the last action has been added to
         *            the Layered and before this action is added.
         */
        public void add(final Layered.Action action, final Duration delay) {
            // shift the null object one further
            actions[wIndex+1] = actions[wIndex];
            delays[wIndex+1] = delays[wIndex];
            // add new action
            actions[wIndex] = action;
            delays[wIndex] = delay;

            wIndex++;
        }

        /**
         * Adds actions to a Layered.
         * 
         * @param currentTime
         *            The current time, i.e. the time of the given
         *            Layered.
         * @param layered
         *            The Layered.
         */
        public void update(final Duration currentTime, final Layered layered) {
            final Duration currentQueueTime = currentTime.subtract(startTime);
            Duration duration = delays[rIndex];
            while(accumulatedTime.add(duration).lessThanOrEqualTo(currentQueueTime)) {
                // spawn action
                layered.addAction(actions[rIndex]);
                // update indices
                accumulatedTime = accumulatedTime.add(duration);
                rIndex++;
                duration = delays[rIndex];
            }
        }

        /**
         * Indicates whether the queue can not add actions to a Layered,
         * because it has alreaded added all of them.
         * 
         * @return <tt>true</tt>, if all actions has been added, <tt>false</tt> otherwise.
         */
        public boolean isEnded() {
            return (rIndex>=delays.length-1)||(rIndex>0&&delays[rIndex]==BLOCKER);
        }

        /**
         * Disables the <tt>ActionQueue</tt>.<br>
         * <br>
         * To enable it again call the <tt>reset</tt> method.
         */
        public void setDisabled() {
            startTime = BLOCKER;
        }

    }

}
