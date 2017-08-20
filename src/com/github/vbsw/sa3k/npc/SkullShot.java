
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
import com.github.vbsw.sa3k.prompt.play.Play;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
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
public class SkullShot extends Group implements Layered.Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    // static
    private static final int           pointForHit          = 10;
    private static final int           pointForDest         = 10;
    private static final Duration      glowDuration         = Duration.millis(50);
    private static final Duration      movementDuration     = Duration.seconds(8);
    private static final double        range                = App.HEIGHT+App.WIDTH/2;

    // object
    private final ImageView            shipView             = new ImageView(Graphic.skullShot);
    private final ImageView            shipGlowOver         = new ImageView(Graphic.skullShot);
    private final Layered.ActionsQueue explosions           = new Layered.ActionsQueue(Graphic.skullShotMeta.getExplosionsNumber());
    private final TranslateTransition  translTransition     = new TranslateTransition(movementDuration, this);
    private final HitDetection         hitDetection;
    private final double               velX;
    private final double               velY;

    private Duration                   timeWhenGlowingStops = Duration.millis(0);
    private int                        lives                = 2;
    private boolean                    enabled              = true;
    private boolean                    exploding            = false;

    private void setGlowing() {
        timeWhenGlowingStops = App.game.getStack().topPane().getTime().add(glowDuration);
        shipGlowOver.setVisible(true);
    }

    private void explode() {
        DataBox.score += pointForDest;
        exploding = true;
        explosions.reset(App.game.getStack().topPane().getTime());
        for(int i = 0; i<Graphic.skullShotMeta.getExplosionsNumber(); i++)
            explosions.add(new Explosion1(getTranslateX()+Graphic.skullShotMeta.getExplosionX(i), getTranslateY()+Graphic.skullShotMeta.getExplosionY(i), velX, velY), Duration.millis(Graphic.skullShotMeta.getExplosionDelay(i)));
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

        private HitDetection(final SkullShot topBox, final Rectangle[] hitboxes) {
            super(topBox, hitboxes);
        }

        /*
         * *****************************
         * public
         * *****************************
         */

        @Override
        public void beenHit(final Node byNode) {
            SkullShot.this.beenHit(Demage.get(byNode));
        }

    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public SkullShot(final double centerX, final double centerY, final double rotateDegrees) {
        final double x = centerX-Graphic.skullShotMeta.getWidth()/2;
        final double y = centerY-Graphic.skullShotMeta.getHeight()/2;
        final double rangeX = -1*(Math.sin(Math.toRadians(rotateDegrees))*range);
        final double rangeY = Math.cos(Math.toRadians(rotateDegrees))*range;
        final double toX = x+rangeX;
        final double toY = y+rangeY;
        velX = rangeX/movementDuration.toMillis();
        velY = rangeY/movementDuration.toMillis();
        hitDetection = new HitDetection(this, Graphic.skullShotMeta.getHitboxes(0));

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
        layered.addNode(this, Play.LAYER_ENEMY_FG);
        layered.getCollision().addDetection(CollisionGroup.ENEMY_SHIP, hitDetection);
        layered.getCollision().addDetection(CollisionGroup.ENEMY_BULLET, hitDetection);
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
        if(explosions.isEnded())
            destroy();
    }

}
