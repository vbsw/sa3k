
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.npc;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.Graphic;
import com.github.vbsw.sa3k.engine.Collision;
import com.github.vbsw.sa3k.engine.FrameAnimation;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.prompt.play.CollisionGroup;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class EnergyShot extends Group implements Layered.Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    // static
    private static final Duration     movementDuration = Duration.seconds(8);
    private static final double       range            = App.HEIGHT+App.WIDTH/2;

    // object
    private final FrameAnimation      animation        = new FrameAnimation(Graphic.energyShot, Graphic.energyShotMeta, Animation.INDEFINITE);
    private final TranslateTransition translTransition = new TranslateTransition(movementDuration, this);
    private final HitDetection        hitDetection;
    private final int                 layer;

    private void destroy() {
        final Layered layered = App.game.getStack().topPane();
        setDisable(true);
        hitDetection.setEnabled(false);
        translTransition.stop();
        layered.removeAnimation(translTransition);
        layered.removeAnimation(animation.getAnimation());
    }

    /**
     * 
     * @author Vitali Baumtrok
     */
    private class HitDetection extends Collision.Detection {

        private HitDetection(final EnergyShot ship, final Rectangle[] hitboxes) {
            super(ship, hitboxes);
        }

        /*
         * *****************************
         * public
         * *****************************
         */

        @Override
        public void beenHit(final Node byNode) {
            destroy();
        }

    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public EnergyShot(final double centerX, final double centerY, final double directionDegrees, final int layer) {
        final double x = centerX-Graphic.energyShotMeta.getWidth()/2;
        final double y = centerY-Graphic.energyShotMeta.getHeight()/2;
        final double rangeX = Math.sin(Math.toRadians(directionDegrees))*range;
        final double rangeY = -1*(Math.cos(Math.toRadians(directionDegrees))*range);
        final double toX = x+rangeX;
        final double toY = y+rangeY;
        hitDetection = new HitDetection(this, Graphic.energyShotMeta.getHitboxes(2));
        this.layer = layer;

        getChildren().add(animation);
        getChildren().addAll(hitDetection.getSubHitboxes());
        setTranslateX(x);
        setTranslateY(y);
        animation.getAnimation().setAutoReverse(true);
        translTransition.setToX(toX);
        translTransition.setToY(toY);
        translTransition.setInterpolator(Interpolator.LINEAR);
    }

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();

        layered.addAnimation(translTransition);
        layered.addAnimation(animation.getAnimation());
        layered.addNode(this, layer);
        layered.getCollision().addDetection(CollisionGroup.ENEMY_BULLET, hitDetection);
        animation.getAnimation().play();
        translTransition.play();
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void update() {}

    public static void spawnMany(final double centerX, final double centerY, final int layer) {
        final Layered layered = App.game.getStack().topPane();
        final double fromDegree = 90;
        final double toDegree = 270;
        final int shotsNumber = 11;
        final double degreeStep = (toDegree-fromDegree)/shotsNumber;
        for(int i = 0; i<shotsNumber; i++)
            layered.addAction(new EnergyShot(centerX, centerY, fromDegree+degreeStep*i+degreeStep/2, layer));
    }

}
