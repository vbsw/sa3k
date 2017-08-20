
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
import com.github.vbsw.sa3k.engine.FrameShow;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.prompt.play.CollisionGroup;
import com.github.vbsw.sa3k.prompt.play.Demage;
import com.github.vbsw.sa3k.prompt.play.Play;

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
public class UpgradeCarrier extends Group implements Layered.Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    // static
    private static final int           pointForHit          = 10;
    private static final int           pointForDest         = 500;
    private static final Duration      glowDuration         = Duration.millis(50);
    private static final Duration      movementDuration     = Duration.seconds(30);
    private static final double        scale                = 1.5;

    // object
    private final FrameShow            shipView             = new FrameShow(Graphic.upCarrier, Graphic.upCarrierMeta);
    private final FrameShow            shipGlowOver         = new FrameShow(Graphic.upCarrier, Graphic.upCarrierMeta);
    private final Layered.ActionsQueue explosions           = new Layered.ActionsQueue(Graphic.upCarrierMeta.getExplosionsNumber());
    private final TranslateTransition  translTransition     = new TranslateTransition(movementDuration, this);
    private final HitDetection         hitDetection;

    private Duration                   timeWhenGlowingStops = Duration.millis(0);
    private int                        lives                = 50;
    private boolean                    enabled              = true;
    private boolean                    exploding            = false;

    private double getStartY() {
        return -Graphic.upCarrierMeta.getHeight()*scale;
    }

    private double getEndY() {
        return App.HEIGHT+Graphic.upCarrierMeta.getHeight()*(scale-1.0);
    }

    private void setGlowing() {
        timeWhenGlowingStops = App.game.getStack().topPane().getTime().add(glowDuration);
        shipGlowOver.setVisible(true);
    }

    private void explode() {
        final double velX = 0;
        final double velY = (getEndY()-getStartY())/movementDuration.toMillis();

        DataBox.score += pointForDest;
        exploding = true;
        explosions.reset(App.game.getStack().topPane().getTime());
        for(int i = 0; i<Graphic.upCarrierMeta.getExplosionsNumber(); i++)
            explosions.add(new Explosion1(getTranslateX()+Graphic.upCarrierMeta.getExplosionX(i), getTranslateY()+Graphic.upCarrierMeta.getExplosionY(i), velX, velY), Duration.millis(Graphic.upCarrierMeta.getExplosionDelay(i)));
        hitDetection.setEnabled(false);
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
    }

    /**
     * 
     * @author Vitali Baumtrok
     */
    private class HitDetection extends Collision.Detection {

        private HitDetection(final UpgradeCarrier upCarrier, final Rectangle[] hitboxes) {
            super(upCarrier, hitboxes);
        }

        /*
         * *****************************
         * public
         * *****************************
         */

        @Override
        public void beenHit(final Node byNode) {
            UpgradeCarrier.this.beenHit(Demage.get(byNode));
        }

    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public UpgradeCarrier() {
        this(App.WIDTH/2);
    }

    public UpgradeCarrier(final double centerX) {
        hitDetection = new HitDetection(this, Graphic.upCarrierMeta.getHitboxes(0));

        shipGlowOver.setVisible(false);
        shipGlowOver.setBlendMode(BlendMode.ADD);
        shipGlowOver.setFrame(1);
        setScaleX(scale);
        setScaleY(scale);
        getChildren().addAll(shipView, shipGlowOver);
        getChildren().addAll(hitDetection.getSubHitboxes());
        setTranslateX(centerX-Graphic.upCarrierMeta.getWidth()/2);
        setTranslateY(getStartY());
        translTransition.setToY(getEndY());
        translTransition.setInterpolator(Interpolator.LINEAR);
    }

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();

        layered.addAnimation(translTransition);
        layered.addNode(this, Play.LAYER_ENEMY_FG);
        layered.getCollision().addDetection(CollisionGroup.ENEMY_SHIP, hitDetection);
        translTransition.play();
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
        if(exploding)
            explosions.update(layered.getTime(), layered);
        if(explosions.isEnded()) {
            final double centerX = getTranslateX()+Graphic.upCarrierMeta.getWidth();
            final double centerY = getTranslateY()+Graphic.upCarrierMeta.getHeight();

            destroy();
            layered.addAction(new Goody(centerX, centerY, Goody.TYPE_UPGRADE));
        }
    }

}
