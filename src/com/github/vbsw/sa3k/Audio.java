
//          Copyright 2013, 2017, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;

/**
 * Contains references to <tt>AudioClip</tt>s.
 * 
 * @author Vitali Baumtrok
 */
public class Audio {

    /*
     * *****************************
     * private
     * *****************************
     */

    private static final String audioDir         = "res/sound";

    private static final String creditsMusicDir  = audioDir+"/Insistent.wav";
    private static final String moveSelectorDir  = audioDir+"/WarmDigitalAcceptButtonHP.wav";
    private static final String level1PreludeDir = audioDir+"/anxious.wav";
    private static final String level1Dir        = audioDir+"/CrispyTown.wav";
    private static final String level1BossDir    = audioDir+"/sharp_0.wav";

    /**
     * Loads an audio file as <tt>Media</tt>.
     * 
     * @param relativePath
     *            The relative path to the audio file which should be
     *            loaded.
     * @return <tt>Media</tt> of the audio file.
     */
    private static Media loadMedia(final String relativePath) {
        return com.github.vbsw.sa3k.engine.Util.loadMedia(App.DIRECTORY_PATH.resolve(relativePath));
    }

    /**
     * Loads an audio file as <tt>AudioClip</tt>.
     * 
     * @param relativePath
     *            The relative path to the audio file which should be
     *            loaded.
     * @return <tt>AudioClip</tt> of the audio file.
     */
    private static AudioClip loadClip(final String relativePath) {
    	final AudioClip audioClip = com.github.vbsw.sa3k.engine.Util.loadClip(App.DIRECTORY_PATH.resolve(relativePath));

        return audioClip;
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    /**
     * Contains loaded audio files
     * as <tt>MediaPlayer</tt>s. <tt>MediaPlayer</tt>s
     * can be played, paused and stopped.
     * 
     * @author Vitali Baumtrok
     */
    public static class Music {

        public static final Media credits       = loadMedia(creditsMusicDir);
        public static final Media level1Prelude = loadMedia(level1PreludeDir);
        public static final Media level1        = loadMedia(level1Dir);
        public static final Media level1Boss    = loadMedia(level1BossDir);

    }

    /**
     * Contains loaded audio files as <tt>AudioClip</tt>s. <tt>AudioClip</tt>s
     * can be played and stopped, but not paused.
     * 
     * @author Vitali Baumtrok
     */
    public static class Fx {

        public static final AudioClip moveSelector = loadClip(moveSelectorDir);

    }

}
