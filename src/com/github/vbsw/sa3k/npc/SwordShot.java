
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.npc;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.DataBox;
import com.github.vbsw.sa3k.Graphic;
import com.github.vbsw.sa3k.effect.Explosion1;
import com.github.vbsw.sa3k.engine.Collision;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.prompt.play.CollisionGroup;
import com.github.vbsw.sa3k.prompt.play.Demage;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class SwordShot extends Group implements Layered.Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    // static
    private static final int         pointForHit          = 0;
    private static final int         pointForDest         = 0;
    private static final Duration    glowDuration         = Duration.millis(50);
    private static final Duration    movementDuration     = Duration.seconds(5);
    private static final double      range                = App.HEIGHT+App.WIDTH/2;

    // object
    private final ImageView          ship                 = new ImageView(Graphic.swordShot);
    private final ImageView          shipGlowOver         = new ImageView(Graphic.swordShot);
    private final ParallelTransition parallelTransition   = new ParallelTransition(this);
    private final HitDetection       hitDetection;
    private final int                layer;
    private final double             velX;
    private final double             velY;

    private Duration                 timeWhenGlowingStops = Duration.millis(0);
    private int                      lives                = 30;
    private boolean                  enabled              = true;

    private void setGlowing() {
        timeWhenGlowingStops = App.game.getStack().topPane().getTime().add(glowDuration);
        shipGlowOver.setVisible(true);
    }

    private void explode() {
        final double centerX = getTranslateX()+getBoundsInParent().getWidth()/2;
        final double centerY = getTranslateY()+getBoundsInParent().getHeight()/2;
        DataBox.score += pointForDest;

        hitDetection.setEnabled(false);
        App.game.getStack().topPane().addAction(new Explosion1(centerX, centerY, velX, velY));
        destroy();
    }

    private void beenHit(final int demage) {
        if(lives>0) {
            final int livesLost = lives-demage<0 ? lives : demage;
            lives -= livesLost;
            DataBox.score += (pointForHit*livesLost);
            if(lives>0)
                setGlowing();
            else
                explode();
        }
    }

    private void destroy() {
        final Layered layered = App.game.getStack().topPane();
        enabled = false;
        setDisable(true);
        hitDetection.setEnabled(false);
        parallelTransition.stop();
        layered.removeAnimation(parallelTransition);
    }

    /**
     * 
     * @author Vitali Baumtrok
     */
    private class HitDetection extends Collision.Detection {

        private HitDetection(final SwordShot ship, final Rectangle[] hitboxes) {
            super(ship, hitboxes);
        }

        /*
         * *****************************
         * public
         * *****************************
         */

        @Override
        public void beenHit(final Node byNode) {
            SwordShot.this.beenHit(Demage.get(byNode));
        }

    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public SwordShot(final double centerX, final double centerY, final double directionDegrees, final int layer) {
        final double x = centerX-Graphic.swordShotMeta.getWidth()/2;
        final double y = centerY-Graphic.swordShotMeta.getHeight()/2;
        final double rangeX = Math.sin(Math.toRadians(directionDegrees))*range;
        final double rangeY = -1*(Math.cos(Math.toRadians(directionDegrees))*range);
        final double toX = x+rangeX;
        final double toY = y+rangeY;
        final RotateTransition rotateTransition = new RotateTransition(Duration.millis(300), this);
        final TranslateTransition translTransition = new TranslateTransition(movementDuration, this);
        hitDetection = new HitDetection(this, Graphic.swordShotMeta.getHitboxes(0));
        this.layer = layer;
        velX = rangeX/movementDuration.toMillis();
        velY = rangeY/movementDuration.toMillis();

        getChildren().addAll(ship, shipGlowOver);
        getChildren().addAll(hitDetection.getSubHitboxes());
        setTranslateX(x);
        setTranslateY(y);
        rotateTransition.setCycleCount(Animation.INDEFINITE);
        rotateTransition.setToAngle(-360);
        rotateTransition.setInterpolator(Interpolator.LINEAR);
        translTransition.setToX(toX);
        translTransition.setToY(toY);
        translTransition.setInterpolator(Interpolator.LINEAR);
        translTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent ae) {
                destroy();
            }
        });
        shipGlowOver.setVisible(false);
        shipGlowOver.setBlendMode(BlendMode.ADD);
        parallelTransition.getChildren().addAll(rotateTransition, translTransition);
    }

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();

        layered.addAnimation(parallelTransition);
        layered.addNode(this, layer);
        layered.getCollision().addDetection(CollisionGroup.ENEMY_SHIP, hitDetection);
        layered.getCollision().addDetection(CollisionGroup.ENEMY_BULLET, hitDetection);
        parallelTransition.play();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void update() {
        final Layered layered = App.game.getStack().topPane();
        if(timeWhenGlowingStops.lessThanOrEqualTo(layered.getTime()))
            shipGlowOver.setVisible(false);
    }

}
