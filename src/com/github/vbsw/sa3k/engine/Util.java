
//          Copyright 2013, 2017, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.engine;

import java.net.URI;
import java.nio.file.Path;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * This class contains different helper methods.
 * 
 * @author Vitali Baumtrok
 */
public class Util {

    /*
     * *****************************
     * private
     * *****************************
     */

    /**
     * The class starts the play of a MediaPlayer when an Animation starts.
     * 
     * @author Vitali Baumtrok
     */
    private static class PlayStarter implements ChangeListener<Animation.Status> {

        /**
         * The MediaPlayer to start playing when Animation starts.
         */
        private final MediaPlayer player;

        /**
         * The constructor.<br>
         * <br>
         * The class starts the play of a MediaPlayer when an Animation starts.
         * 
         * @param player
         *            The MediaPlayer to start playing.
         */
        public PlayStarter(final MediaPlayer player) {
            this.player = player;
        }

        /**
         * Start playing the MediaPlayer when Animation starts running.
         */
        @Override
        public void changed(final ObservableValue<? extends Status> observable, final Status oldValue, final Status newValue) {
            if((oldValue!=newValue)&&(newValue==Animation.Status.RUNNING))
                player.setAutoPlay(true);
        }

    }

    /**
     * The class ends the play of a MediaPlayer when an Animation stops.
     * 
     * @author Vitali Baumtrok
     */
    private static class PlayEnder implements ChangeListener<Animation.Status> {

        /**
         * The MediaPlayer to stop playing when Animation stops.
         */
        private final MediaPlayer player;

        /**
         * The constructor.<br>
         * <br>
         * The class stops the play of a MediaPlayer when an Animation stops.
         * 
         * @param player
         *            The MediaPlayer to stop playing.
         */
        public PlayEnder(final MediaPlayer player) {
            this.player = player;
        }

        /**
         * Stops playing the MediaPlayer when Animation gets stopped.
         */
        @Override
        public void changed(final ObservableValue<? extends Status> observable, final Status oldValue, final Status newValue) {
            if((oldValue!=newValue)&&(newValue==Animation.Status.STOPPED))
                player.stop();
        }

    }

    /*
     * *****************************
     * public
     * *****************************
     */

    /**
     * Creates a Timeline, which effects a MediaPlayer to play.
     * The play will increase from volume of 0 to the given
     * volume in the given duration.
     * 
     * @param player
     *            The MediaPlayer.
     * @param toVolume
     *            The volume to increase to. Number between 0.0 and 1.0.
     * @param duration
     *            The duration of increasing.
     * @return The Timeline.
     */
    public static Timeline createAudioFadeIn(final MediaPlayer player, final double toVolume, final Duration duration) {
        final KeyValue fromValue = new KeyValue(player.volumeProperty(), 0.0);
        final KeyValue toValue = new KeyValue(player.volumeProperty(), toVolume, Interpolator.EASE_IN);
        final KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, fromValue);
        final KeyFrame keyFrame2 = new KeyFrame(duration, toValue);
        final Timeline timeline = new Timeline(keyFrame1, keyFrame2);

        // start mediaplayer when timeline starts
        timeline.statusProperty().addListener(new PlayStarter(player));

        return timeline;
    }

    /**
     * Creates a Timeline, which effects a MediaPlayer to stop playing.
     * Before it stops playing the volume will decrease from the
     * given number to 0 in the given duration.
     * 
     * @param player
     *            The MediaPlayer.
     * @param fromVolume
     *            The volume to decrease from. Number between 0.0 and 1.0.
     * @param duration
     *            The duration of increasing.
     * @return The Timeline.
     */
    public static Timeline createAudioFadeOut(final MediaPlayer player, final double fromVolume, final Duration duration) {
        final KeyValue fromValue = new KeyValue(player.volumeProperty(), fromVolume);
        final KeyValue toValue = new KeyValue(player.volumeProperty(), 0.0, Interpolator.EASE_IN);
        final KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, fromValue);
        final KeyFrame keyFrame2 = new KeyFrame(duration, toValue);
        final Timeline timeline = new Timeline(keyFrame1, keyFrame2);

        // stop mediaplayer when timeline stops
        timeline.statusProperty().addListener(new PlayEnder(player));

        return timeline;
    }

    /**
     * Loads an audio file as <tt>Media</tt>.
     * 
     * @param appPath
     *            The absolute path.
     * @param relativePath
     *            The relative path to the audio file which should be
     *            loaded.
     * @return <tt>Media</tt> of the audio file.
     */
    public static Media loadMedia(final Path path) {
        final URI uri = path.toUri();
        final Media media = new Media(uri.toString());

        return media;
    }

    /**
     * Loads an audio file as <tt>AudioClip</tt>.
     * 
     * @param appPath
     *            The absolute path.
     * @param relativePath
     *            The relative path to the audio file which should be
     *            loaded.
     * @return <tt>AudioClip</tt> of the audio file.
     */
    public static AudioClip loadClip(final Path path) {
        final URI uri = path.toUri();
        final AudioClip audioClip = new AudioClip(uri.toString());

        return audioClip;
    }

    /**
     * Loads an image file.
     * 
     * @param appPath
     *            The absolute path.
     * @param relativePath
     *            The relative path to the audio file which should be
     *            loaded.
     * @return The image.
     */
    public static Image loadImage(final Path path) {
        final URI uri = path.toUri();
        final Image image = new Image(uri.toString());

        return image;
    }

    /**
     * Loads an image file and resizes it.
     * 
     * @param appPath
     *            The absolute path.
     * @param relativePath
     *            The relative path to the audio file which should be
     *            loaded.
     * @param width
     *            The maximum width of the image.
     * @param height
     *            The maximum height of the image.
     * @return The image.
     */
    public static Image loadImageResized(final Path path, final double width, final double height) {
        final URI uri = path.toUri();
        final Image image = new Image(uri.toString(), width, height, true, true);

        return image;
    }

}
