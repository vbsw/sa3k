
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt.play;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.DataBox;
import com.github.vbsw.sa3k.engine.Collision;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.engine.Layered.Action;

/**
 * 
 * @author Vitali Baumtrok
 */
public class Play implements Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    private static Play        instance     = null;

    private final BackListener backListener = new BackListener();
    private final Score        score        = new Score();

    private boolean            enabled      = true;

    /*
     * *****************************
     * package
     * *****************************
     */

    void backToMenu() {
        instance = null;
        enabled = false;
        DataBox.score = 0;
        DataBox.destroyedGrells = 0;
        DataBox.playerAlive = true;
        App.game.getStack().popPane().destory();

        // enable menu
        App.game.getStack().topPane().setVisible(true);
        App.game.getStack().topPane().setEnabled(true);
    }

    void statusEnded() {
        backToMenu();
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public static final int LAYERS_NUMBER        = 10;

    public static final int LAYER_BG             = 0;
    public static final int LAYER_BG_ITEMS       = 1;
    public static final int LAYER_ENEMY_BG       = 2;
    public static final int LAYER_ENEMY_BULLETS  = 3;
    public static final int LAYER_ENEMY_FG       = 4;
    public static final int LAYER_PLAYER         = 5;
    public static final int LAYER_PLAYER_BULLETS = 6;
    public static final int LAYER_PLAYER_FG      = 7;
    public static final int LAYER_FG             = 8;
    public static final int LAYER_TEXT           = 9;

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();
        final Collision collision = layered.getCollision();

        layered.getInput().addKeyListener(backListener);
        layered.addAction(new Level1());
        layered.addAction(score);
        collision.addCollisionMapping(CollisionGroup.ENEMY_SHIP, CollisionGroup.PLAYER_BULLET);
        collision.addCollisionMapping(CollisionGroup.PLAYER_SHIP, CollisionGroup.ENEMY_BULLET);
        collision.addCollisionMapping(CollisionGroup.PLAYER_SHIP, CollisionGroup.GOODIES);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void update() {
        if(!DataBox.playerAlive) {
            enabled = false;
            score.destroy();
            App.game.getStack().topPane().addAction(new StatusText("Game Over"));
        }
    }

    public static Play getInstance() {
        if(instance==null)
            instance = new Play();

        return instance;
    }

}
