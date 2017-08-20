
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
import com.github.vbsw.sa3k.engine.FrameAnimation;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.prompt.play.CollisionGroup;
import com.github.vbsw.sa3k.prompt.play.Demage;
import com.github.vbsw.sa3k.prompt.play.Play;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class BugShot extends Group implements Layered.Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    // static
    private static final int          pointForHit          = 0;
    private static final int          pointForDest         = 0;
    private static final Duration     glowDuration         = Duration.millis(50);
    private static final Duration     movementDuration     = Duration.seconds(8);
    private static final double       range                = App.HEIGHT+App.WIDTH/2;

    // object
    private final FrameAnimation      shipView             = new FrameAnimation(Graphic.bugShot, Graphic.bugShotMeta, Animation.INDEFINITE);
    private final FrameAnimation      shipGlowOver         = new FrameAnimation(Graphic.bugShot, Graphic.bugShotMeta, Animation.INDEFINITE);
    private final TranslateTransition translTransition     = new TranslateTransition(movementDuration, this);
    private final HitDetection        hitDetection;
    private final double              velX;
    private final double              velY;

    private Duration                  timeWhenGlowingStops = Duration.millis(0);
    private int                       lives                = 2;
    private boolean                   enabled              = true;

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
        translTransition.stop();
        layered.removeAnimation(translTransition);
        layered.removeAnimation(shipView.getAnimation());
        layered.removeAnimation(shipGlowOver.getAnimation());
    }

    /**
     * 
     * @author Vitali Baumtrok
     */
    private class HitDetection extends Collision.Detection {

        private HitDetection(final BugShot topBox, final Rectangle[] hitboxes) {
            super(topBox, hitboxes);
        }

        /*
         * *****************************
         * public
         * *****************************
         */

        @Override
        public void beenHit(final Node byNode) {
            BugShot.this.beenHit(Demage.get(byNode));
        }

    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public BugShot(final double centerX, final double centerY, final double rotateDegrees) {
        final double x = centerX-Graphic.bugShotMeta.getWidth()/2;
        final double y = centerY-Graphic.bugShotMeta.getHeight()/2;
        final double rangeX = Math.sin(Math.toRadians(rotateDegrees))*range;
        final double rangeY = -1*(Math.cos(Math.toRadians(rotateDegrees))*range);
        final double toX = x+rangeX;
        final double toY = y+rangeY;
        velX = rangeX/movementDuration.toMillis();
        velY = rangeY/movementDuration.toMillis();
        hitDetection = new HitDetection(this, Graphic.bugShotMeta.getHitboxes(0));

        shipGlowOver.setVisible(false);
        shipGlowOver.setBlendMode(BlendMode.ADD);
        getChildren().addAll(shipView, shipGlowOver);
        getChildren().addAll(hitDetection.getSubHitboxes());
        setTranslateX(x);
        setTranslateY(y);
        setRotate(rotateDegrees);
        translTransition.setToX(toX);
        translTransition.setToY(toY);
        translTransition.setInterpolator(Interpolator.LINEAR);
    }

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();

        layered.addAnimation(translTransition);
        layered.addAnimation(shipView.getAnimation());
        layered.addAnimation(shipGlowOver.getAnimation());
        layered.addNode(this, Play.LAYER_ENEMY_FG);
        layered.getCollision().addDetection(CollisionGroup.ENEMY_SHIP, hitDetection);
        layered.getCollision().addDetection(CollisionGroup.ENEMY_BULLET, hitDetection);
        translTransition.play();
        shipView.getAnimation().play();
        shipGlowOver.getAnimation().play();
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
