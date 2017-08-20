
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt.play;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.Audio;
import com.github.vbsw.sa3k.effect.BlackBackground;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.engine.Util;
import com.github.vbsw.sa3k.npc.Boss1;
import com.github.vbsw.sa3k.npc.EvilCarrier;
import com.github.vbsw.sa3k.npc.Grell;
import com.github.vbsw.sa3k.npc.LittleDragon;
import com.github.vbsw.sa3k.npc.UpgradeCarrier;
import com.github.vbsw.sa3k.pc.PlayerShip;

import javafx.animation.Animation;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class Level1 implements Layered.Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    private static final double        musicPreludeVolume   = 0.8;
    private static final double        musicLevelVolume     = 0.3;
    private static final double        musicBossVolume      = musicLevelVolume;
    private static final Duration      fadeInOutDuration    = Duration.seconds(4);
    private static final Duration      beforeFadeInDuration = Duration.seconds(3);

    private final Layered.ActionsQueue preludeQueue         = new Layered.ActionsQueue(3);
    private final Layered.ActionsQueue levelQueue           = new Layered.ActionsQueue(20);
    private final MediaPlayer          musicPrelude         = new MediaPlayer(Audio.Music.level1Prelude);
    private final MediaPlayer          musicLevel           = new MediaPlayer(Audio.Music.level1);
    private final MediaPlayer          musicBoss            = new MediaPlayer(Audio.Music.level1Boss);
    private final Animation            musicPreludeFadeOut  = Util.createAudioFadeOut(musicPrelude, musicPreludeVolume, fadeInOutDuration);
    private final Animation            musicLevelFadeIn     = Util.createAudioFadeIn(musicLevel, musicLevelVolume, fadeInOutDuration);
    private final Animation            musicPlayFadeOut     = Util.createAudioFadeOut(musicLevel, musicLevelVolume, fadeInOutDuration);
    private final Animation            musicBossFadeIn      = Util.createAudioFadeIn(musicBoss, musicBossVolume, fadeInOutDuration);
    private final Animation            musicBossFadeOut     = Util.createAudioFadeOut(musicBoss, musicBossVolume, fadeInOutDuration);

    private boolean                    enabled              = true;

    /**
     * 
     * @author Vitali Baumtrok
     */
    private class EndPreludeStartPlay implements Layered.Action {

        private boolean  enabled = true;
        private Duration timeToEndPrelude;
        private Duration timeToStartPlay;

        @Override
        public void init() {
            timeToEndPrelude = App.game.getStack().topPane().getTime().add(Duration.seconds(1));
            timeToStartPlay = Duration.INDEFINITE;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void update() {
            final Layered layered = App.game.getStack().topPane();

            if(timeToEndPrelude.lessThanOrEqualTo(layered.getTime())) {
                timeToEndPrelude = Duration.INDEFINITE;
                timeToStartPlay = layered.getTime().add(beforeFadeInDuration);
                musicPreludeFadeOut.play();
            }
            if(timeToStartPlay.lessThanOrEqualTo(layered.getTime())) {
                enabled = false;
                musicLevelFadeIn.play();
                levelQueue.reset(layered.getTime());
            }
        }

    }

    /**
     * 
     * @author Vitali Baumtrok
     */
    private class StartBossMusic implements Layered.Action {

        private boolean  enabled = true;
        private Duration timeToStartPlay;

        @Override
        public void init() {
            timeToStartPlay = App.game.getStack().topPane().getTime().add(beforeFadeInDuration);
            musicPlayFadeOut.play();
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void update() {
            if(timeToStartPlay.lessThanOrEqualTo(App.game.getStack().topPane().getTime())) {
                enabled = false;
                musicBossFadeIn.play();
            }
        }

    }

    /**
     * 
     * @author Vitali Baumtrok
     */
    private class EndBossMusic implements Layered.Action {

        private boolean  enabled = true;
        private Duration timeToStartPlay;

        @Override
        public void init() {
            timeToStartPlay = App.game.getStack().topPane().getTime().add(StatusText.SHOW_TEXT_DURATION.subtract(fadeInOutDuration));
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void update() {
            if(timeToStartPlay.lessThanOrEqualTo(App.game.getStack().topPane().getTime())) {
                enabled = false;
                musicBossFadeOut.play();
            }
        }

    }

    /*
     * *****************************
     * package
     * *****************************
     */

    /**
     * Is never called. Because there is no second level and
     * on return to menu the whole Layered is cleared anyway.
     */
    void destroy() {
        final Layered layered = App.game.getStack().topPane();
        enabled = false;

        layered.removeMediaPlayer(musicPrelude);
        layered.removeMediaPlayer(musicLevel);
        layered.removeMediaPlayer(musicBoss);
        layered.removeAnimation(musicPreludeFadeOut);
        layered.removeAnimation(musicLevelFadeIn);
        layered.removeAnimation(musicPlayFadeOut);
        layered.removeAnimation(musicBossFadeIn);
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public Level1() {
        musicPrelude.setCycleCount(MediaPlayer.INDEFINITE);
        musicPrelude.setVolume(musicPreludeVolume);
        musicLevel.setCycleCount(MediaPlayer.INDEFINITE);
        musicLevel.setVolume(musicLevelVolume);
        musicBoss.setCycleCount(MediaPlayer.INDEFINITE);
        musicBoss.setVolume(musicBossVolume);
    }

    @Override
    public void init() {
        final PlayerShip playerShip = new PlayerShip();
        final Layered layered = App.game.getStack().topPane();

        preludeQueue.reset(App.game.getStack().topPane().getTime());
        preludeQueue.add(new BlackBackground(), Duration.millis(0));
        preludeQueue.add(playerShip, Duration.millis(600));
        // preludeQueue.add(new Boss1(this, playerShip), BlackBackground.EXPAND_DURATION.add(Duration.seconds(1)));
        preludeQueue.add(new EvilCarrier(this, playerShip), BlackBackground.EXPAND_DURATION.add(Duration.seconds(1)));
        levelQueue.setDisabled();
        levelQueue.add(Grell.createSpawner_Arc(10, 230, 40, false), Duration.millis(0));
        levelQueue.add(Grell.createSpawner_Arc(10, 200, 40, true), Duration.millis(500));
        levelQueue.add(Grell.createSpawner_Arc(60, 140, 40, false), Duration.millis(1000));
        levelQueue.add(Grell.createSpawner_Arc(60, 110, 40, true), Duration.millis(1500));
        levelQueue.add(Grell.createSpawner_Circle(App.WIDTH/2), Duration.seconds(5));
        levelQueue.add(Grell.createSpawner_Circle(App.WIDTH/2), Duration.seconds(3));
        levelQueue.add(Grell.createSpawner_Circle(App.WIDTH/2), Duration.seconds(3));
        levelQueue.add(Grell.createSpawner_Circle(App.WIDTH/2), Duration.seconds(3));
        levelQueue.add(Grell.createSpawner_Circle(App.WIDTH/2), Duration.seconds(3));
        levelQueue.add(Grell.createSpawner_Circle(App.WIDTH/2), Duration.seconds(3));
        levelQueue.add(Grell.createSpawner_Circle(App.WIDTH/2), Duration.seconds(3));
        levelQueue.add(new UpgradeCarrier(), Duration.seconds(3));
        levelQueue.add(LittleDragon.createInCenter(), Duration.seconds(8));
        levelQueue.add(new UpgradeCarrier(App.WIDTH*2/3), Duration.seconds(6));
        levelQueue.add(LittleDragon.createAside(true), Duration.seconds(8));
        levelQueue.add(LittleDragon.createAside(false), Duration.seconds(0));
        levelQueue.add(new StartBossMusic(), Duration.seconds(17));
        levelQueue.add(new Boss1(this, playerShip), Duration.seconds(3));
        musicPrelude.setAutoPlay(true);
        layered.addMediaPlayer(musicPrelude);
        layered.addMediaPlayer(musicLevel);
        layered.addMediaPlayer(musicBoss);
        layered.addAnimation(musicPreludeFadeOut);
        layered.addAnimation(musicLevelFadeIn);
        layered.addAnimation(musicPlayFadeOut);
        layered.addAnimation(musicBossFadeIn);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void update() {
        final Layered layered = App.game.getStack().topPane();

        preludeQueue.update(layered.getTime(), layered);
        levelQueue.update(layered.getTime(), layered);
    }

    public void evilCarrierExploded() {
        App.game.getStack().topPane().addAction(new EndPreludeStartPlay());
    }

    public void bossDestroyed() {
        final Layered layered = App.game.getStack().topPane();
        layered.addAction(new StatusText("You Win"));
        layered.addAction(new EndBossMusic());
    }

}
