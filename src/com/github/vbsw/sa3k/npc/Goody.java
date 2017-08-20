
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.npc;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.DataBox;
import com.github.vbsw.sa3k.Graphic;
import com.github.vbsw.sa3k.engine.Collision;
import com.github.vbsw.sa3k.engine.FrameShow;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.prompt.GameCtrl;
import com.github.vbsw.sa3k.prompt.play.CollisionGroup;
import com.github.vbsw.sa3k.prompt.play.Play;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class Goody extends Group implements Layered.Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    // static
    private static final Duration moventDuration = Duration.seconds(500);
    private static final double   movenetRange   = 50000;

    private static boolean        flyToRight     = true;

    // object
    private final Timeline        moveToTop      = GameCtrl.createMovementToADirection(moventDuration, translateYProperty(), -movenetRange);
    private final Timeline        moveToBottom   = GameCtrl.createMovementToADirection(moventDuration, translateYProperty(), movenetRange);
    private final Timeline        moveToLeft     = GameCtrl.createMovementToADirection(moventDuration, translateXProperty(), -movenetRange);
    private final Timeline        moveToRight    = GameCtrl.createMovementToADirection(moventDuration, translateXProperty(), movenetRange);
    private final FadeTransition  blinking       = new FadeTransition(Duration.millis(300));
    private final int             type;
    private final HitDetection    hitDetection;

    private boolean               enabled        = true;

    private class HitDetection extends Collision.Detection {

        public HitDetection(final Node topBounds, final Node[] subBounds) {
            super(topBounds, subBounds);
        }

        @Override
        public void beenHit(final Node byNode) {
            setEnabled(false);
            destroy();
        }

    }

    private void destroy() {
        final Layered layered = App.game.getStack().topPane();

        enabled = false;
        moveToTop.stop();
        moveToBottom.stop();
        moveToLeft.stop();
        moveToRight.stop();
        blinking.stop();
        hitDetection.setEnabled(false);
        setDisable(true);
        layered.removeAnimation(moveToTop);
        layered.removeAnimation(moveToBottom);
        layered.removeAnimation(moveToLeft);
        layered.removeAnimation(moveToRight);
        layered.removeAnimation(blinking);
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public static final int TYPE_ENERGY  = 0;
    public static final int TYPE_LIFE    = 1;
    public static final int TYPE_UPGRADE = 2;

    public static void spawnAfterGrell(final double centerX, final double centerY, final int type) {
        if(DataBox.destroyedGrells%75==0)
            App.game.getStack().topPane().addAction(new Goody(centerX, centerY, type));
    }

    public Goody(final double centerX, final double centerY, final int type) {
        final Rectangle[] hitboxes = Graphic.goodyMeta.getHitboxes(0);
        final FrameShow ship = new FrameShow(Graphic.goody, Graphic.goodyMeta);
        final FrameShow shipGlowOver = new FrameShow(Graphic.goody, Graphic.goodyMeta);
        this.type = type;
        hitDetection = new HitDetection(this, hitboxes);

        blinking.setNode(shipGlowOver);
        blinking.setAutoReverse(true);
        blinking.setCycleCount(Animation.INDEFINITE);
        blinking.setFromValue(0);
        blinking.setToValue(0.5);
        ship.setFrame(type);
        shipGlowOver.setFrame(type);
        shipGlowOver.setBlendMode(BlendMode.ADD);
        getChildren().addAll(ship, shipGlowOver);
        getChildren().addAll(hitboxes);
        setTranslateX(centerX-Graphic.goodyMeta.getWidth()/2);
        setTranslateY(centerY-Graphic.goodyMeta.getHeight()/2);
    }

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();

        layered.addNode(this, Play.LAYER_PLAYER_FG);
        layered.getCollision().addDetection(CollisionGroup.GOODIES, hitDetection);
        layered.addAnimation(moveToTop);
        layered.addAnimation(moveToBottom);
        layered.addAnimation(moveToLeft);
        layered.addAnimation(moveToRight);
        layered.addAnimation(blinking);
        if(flyToRight)
            moveToRight.play();
        else
            moveToLeft.play();
        moveToBottom.play();
        blinking.play();

        flyToRight = !flyToRight;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void update() {
        final double x = getTranslateX();
        final double y = getTranslateY();
        final double width = Graphic.goodyMeta.getWidth();
        final double height = Graphic.goodyMeta.getHeight();
        if(x<0) {
            moveToLeft.stop();
            moveToRight.play();
        }
        else if(x>(App.WIDTH-width)) {
            moveToRight.stop();
            moveToLeft.play();
        }
        if(y<0) {
            moveToTop.stop();
            moveToBottom.play();
        }
        else if(y>(App.HEIGHT-height)) {
            moveToBottom.stop();
            moveToTop.play();
        }
    }

    public int getType() {
        return type;
    }
}
