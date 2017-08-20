
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.pc;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.DataBox;
import com.github.vbsw.sa3k.Graphic;
import com.github.vbsw.sa3k.effect.Explosion1;
import com.github.vbsw.sa3k.engine.Collision;
import com.github.vbsw.sa3k.engine.FrameShow;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.npc.Goody;
import com.github.vbsw.sa3k.prompt.GameCtrl;
import com.github.vbsw.sa3k.prompt.play.CollisionGroup;
import com.github.vbsw.sa3k.prompt.play.Demage;
import com.github.vbsw.sa3k.prompt.play.Play;

import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class PlayerShip extends AnchorPane implements Layered.Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    private static final int              FRAME_STILL          = 0;
    private static final int              FRAME_UP             = 1;
    private static final int              FRAME_STILL_LEFT     = 2;
    private static final int              FRAME_UP_LEFT        = 3;
    private static final int              FRAME_UP_RIGHT       = 4;
    private static final int              FRAME_STILL_RIGHT    = 5;

    private static final Duration         moventDuration       = Duration.seconds(500);
    private static final double           movenetRange         = 100000;

    private final ShipControlKeysListener listener             = new ShipControlKeysListener(this);
    private final Shooting                shooting             = new Shooting();
    private final FrameShow               shipGlowOver         = new FrameShow(Graphic.playerShip, Graphic.playerShipMeta);
    private final Timeline                moveToTop            = GameCtrl.createMovementToADirection(moventDuration, translateYProperty(), -movenetRange);
    private final Timeline                moveToBottom         = GameCtrl.createMovementToADirection(moventDuration, translateYProperty(), movenetRange);
    private final Timeline                moveToLeft           = GameCtrl.createMovementToADirection(moventDuration, translateXProperty(), -movenetRange);
    private final Timeline                moveToRight          = GameCtrl.createMovementToADirection(moventDuration, translateXProperty(), movenetRange);

    private final PlayerLives             lives                = new PlayerLives();
    private final FrameShow               shipView             = new FrameShow(Graphic.playerShip, Graphic.playerShipMeta);
    private Duration                      timeWhenGlowingStops = Duration.millis(0);
    private final HitDetection            hitDetection;

    private boolean                       enabled              = true;

    private void coughtLife() {
        lives.add(1);
    }

    private void upgrade() {
        shooting.upgrade();
    }

    /**
     * 
     * @author Vitali Baumtrok
     */
    private class HitDetection extends Collision.Detection {

        /*
         * *****************************
         * public
         * *****************************
         */

        public HitDetection(final PlayerShip playerShip, final Rectangle[] hitboxes) {
            super(playerShip, hitboxes);
        }

        @Override
        public void beenHit(final Node byNode) {
            if(byNode instanceof Goody) {
                final Goody goody = (Goody)byNode;
                if(goody.getType()==Goody.TYPE_LIFE)
                    PlayerShip.this.coughtLife();
                else if(goody.getType()==Goody.TYPE_UPGRADE)
                    PlayerShip.this.upgrade();
            }
            else {
                PlayerShip.this.beenHit(Demage.get(byNode));
            }
        }

    }

    /**
     * 
     * @author Vitali Baumtrok
     */
    private class Shooting implements Layered.Action {

        private final Duration shootingInterval = Duration.millis(100);
        private Duration       timeForNextShot  = null;
        private int            upgrade          = 0;

        private boolean        enabled          = true;

        /*
         * *****************************
         * public
         * *****************************
         */

        @Override
        public void init() {
            enabled = true;
            timeForNextShot = App.game.getStack().topPane().getTime().add(shootingInterval);
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void update() {
            final Layered layered = App.game.getStack().topPane();
            if(timeForNextShot.lessThanOrEqualTo(layered.getTime())) {
                final double bulletWidth = Graphic.playerShotMeta.getWidth();
                final double shipWidth = Graphic.playerShipMeta.getWidth();
                final double shipX = PlayerShip.this.getTranslateX();
                final double shipY = PlayerShip.this.getTranslateY();
                timeForNextShot = timeForNextShot.add(shootingInterval);

                switch(upgrade) {
                    case 0:
                        layered.addAction(new PlayerShot(shipX, shipY, PlayerShot.TYPE_A_LEFT_1));
                        layered.addAction(new PlayerShot(shipX+shipWidth-19, shipY, PlayerShot.TYPE_A_RIGHT_1));
                        break;
                    case 1:
                        layered.addAction(new PlayerShot(shipX, shipY, PlayerShot.TYPE_A_LEFT_1));
                        layered.addAction(new PlayerShot(shipX+shipWidth-bulletWidth, shipY, PlayerShot.TYPE_A_RIGHT_1));
                        layered.addAction(new PlayerShot(shipX, shipY, PlayerShot.TYPE_A_LEFT_3));
                        layered.addAction(new PlayerShot(shipX+shipWidth-bulletWidth, shipY, PlayerShot.TYPE_A_RIGHT_3));
                        break;
                    case 2:
                    default:
                        layered.addAction(new PlayerShot(shipX, shipY, PlayerShot.TYPE_A_LEFT_1));
                        layered.addAction(new PlayerShot(shipX+shipWidth-bulletWidth, shipY, PlayerShot.TYPE_A_RIGHT_1));
                        layered.addAction(new PlayerShot(shipX, shipY, PlayerShot.TYPE_A_LEFT_2));
                        layered.addAction(new PlayerShot(shipX+shipWidth-bulletWidth, shipY, PlayerShot.TYPE_A_RIGHT_2));
                        layered.addAction(new PlayerShot(shipX, shipY, PlayerShot.TYPE_A_LEFT_3));
                        layered.addAction(new PlayerShot(shipX+shipWidth-bulletWidth, shipY, PlayerShot.TYPE_A_RIGHT_3));
                }
            }
        }

        public void destroy() {
            enabled = false;
        }

        public void upgrade() {
            upgrade++;
        }

    }

    /*
     * *****************************
     * package
     * *****************************
     */

    void beenHit(final int demage) {
        glow();
        lives.subtract(demage);
        if(lives.get()==0)
            destroy();
    }

    void enableMovement() {
        final Layered layered = App.game.getStack().topPane();
        layered.getInput().addKeyListener(listener);
        layered.addAnimation(moveToTop);
        layered.addAnimation(moveToBottom);
        layered.addAnimation(moveToLeft);
        layered.addAnimation(moveToRight);
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public static final Duration glowDuration = Duration.millis(80);

    public void setShootingEnabled(final boolean enabled) {
        if(enabled)
            App.game.getStack().topPane().addAction(shooting);
        else
            shooting.destroy();
    }

    public void glow() {
        timeWhenGlowingStops = App.game.getStack().topPane().getTime().add(glowDuration);
        shipGlowOver.setVisible(true);
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public PlayerShip() {
        final Rectangle[] hitboxes = Graphic.playerShipMeta.getHitboxes(0);
        final double shipWidth = Graphic.playerShipMeta.getWidth();
        final double shipHeight = Graphic.playerShipMeta.getHeight();
        final double x = (App.WIDTH-shipWidth)/2;
        final double y = App.HEIGHT*2/3;
        hitDetection = new HitDetection(this, hitboxes);

        getChildren().addAll(shipView, shipGlowOver);
        getChildren().addAll(hitboxes);
        shipGlowOver.setBlendMode(BlendMode.ADD);

        setTranslateX(x);
        setTranslateY(y);
        DataBox.playerShipCenterCoordX = x+shipWidth/2;
        DataBox.playerShipCenterCoordY = y+shipHeight/2;
    }

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();
        final Collision collision = layered.getCollision();

        shipGlowOver.setVisible(false);
        layered.addNode(this, Play.LAYER_PLAYER_FG);
        layered.addAction(new ShipInitializer(this));
        layered.addAction(lives);
        collision.addDetection(CollisionGroup.PLAYER_SHIP, hitDetection);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void update() {
        final Layered layered = App.game.getStack().topPane();

        // stop moving when border
        if((moveToTop.getStatus()==Animation.Status.RUNNING)&&getTranslateY()<3)
            moveToTopStop();
        if((moveToBottom.getStatus()==Animation.Status.RUNNING)&&getTranslateY()+getBoundsInLocal().getHeight()+3>App.HEIGHT)
            moveToBottomStop();
        if((moveToLeft.getStatus()==Animation.Status.RUNNING)&&getTranslateX()<3)
            moveToLeftStop();
        if((moveToRight.getStatus()==Animation.Status.RUNNING)&&getTranslateX()+getBoundsInLocal().getWidth()+3>App.WIDTH)
            moveToRightStop();
        // stops glowing
        if(timeWhenGlowingStops.lessThanOrEqualTo(layered.getTime()))
            shipGlowOver.setVisible(false);
    }

    public void destroy() {
        final Layered layered = App.game.getStack().topPane();

        setDisable(true);
        listener.destroy();
        shooting.destroy();
        moveToTop.stop();
        moveToBottom.stop();
        moveToLeft.stop();
        moveToRight.stop();
        hitDetection.setEnabled(false);
        lives.destroy();
        layered.removeAnimation(moveToTop);
        layered.removeAnimation(moveToBottom);
        layered.removeAnimation(moveToLeft);
        layered.removeAnimation(moveToRight);
        layered.addAction(new Explosion1(getCenterX(), getCenterY(), 0, 0));
        DataBox.playerAlive = false;
    }

    public double getCenterX() {
        return getTranslateX()+Graphic.playerShipMeta.getWidth()/2;
    }

    public double getCenterY() {
        return getTranslateY()+Graphic.playerShipMeta.getHeight()/2;
    }

    public void moveToTop() {
        moveToTop.play();
        if(moveToLeft.getStatus()==Animation.Status.RUNNING)
            shipView.setFrame(FRAME_UP_LEFT);
        else if(moveToRight.getStatus()==Animation.Status.RUNNING)
            shipView.setFrame(FRAME_UP_RIGHT);
        else
            shipView.setFrame(FRAME_UP);
    }

    public void moveToBottom() {
        moveToBottom.play();
    }

    public void moveToLeft() {
        moveToLeft.play();
        if(moveToTop.getStatus()==Animation.Status.RUNNING)
            shipView.setFrame(FRAME_UP_LEFT);
        else
            shipView.setFrame(FRAME_STILL_LEFT);
    }

    public void moveToRight() {
        moveToRight.play();
        if(moveToTop.getStatus()==Animation.Status.RUNNING)
            shipView.setFrame(FRAME_UP_RIGHT);
        else
            shipView.setFrame(FRAME_STILL_RIGHT);
    }

    public void moveToTopStop() {
        moveToTop.stop();
        if(moveToLeft.getStatus()==Animation.Status.RUNNING)
            shipView.setFrame(FRAME_STILL_LEFT);
        else if(moveToRight.getStatus()==Animation.Status.RUNNING)
            shipView.setFrame(FRAME_STILL_RIGHT);
        else
            shipView.setFrame(FRAME_STILL);
    }

    public void moveToBottomStop() {
        moveToBottom.stop();
    }

    public void moveToLeftStop() {
        moveToLeft.stop();
        if(moveToTop.getStatus()==Animation.Status.RUNNING)
            shipView.setFrame(FRAME_UP);
        else
            shipView.setFrame(FRAME_STILL);
    }

    public void moveToRightStop() {
        moveToRight.stop();
        if(moveToTop.getStatus()==Animation.Status.RUNNING)
            shipView.setFrame(FRAME_UP);
        else
            shipView.setFrame(FRAME_STILL);
    }

}
