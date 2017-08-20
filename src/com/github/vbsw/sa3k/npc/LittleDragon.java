
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
public class LittleDragon extends Group implements Layered.Action {

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
    private static final Duration      shootOffset          = Duration.seconds(3);
    private static final Duration      smallShootInterval   = Duration.millis(100);
    private static final Duration      bigShootInterval     = smallShootInterval;
    private static final double        degOffset            = 90;
    private static final int           shotsPerRound        = 20;

    // object
    private final ImageView            shipView             = new ImageView(Graphic.littleDragon);
    private final ImageView            shipGlowOver         = new ImageView(Graphic.littleDragon);
    private final Layered.ActionsQueue explosions           = new Layered.ActionsQueue(Graphic.littleDragonMeta.getExplosionsNumber());
    private final TranslateTransition  translTransition     = new TranslateTransition(movementDuration, this);
    private final HitDetection         hitDetection;

    private Duration                   timeWhenGlowingStops = Duration.millis(0);
    private Duration                   timeForShot          = timeWhenGlowingStops;
    private int                        lives                = 200;
    private boolean                    enabled              = true;
    private boolean                    exploding            = false;
    private int                        shotCounter          = shotsPerRound;

    private double getStartX() {
        return (App.WIDTH-Graphic.littleDragonMeta.getWidth())/2;
    }

    private double getStartY() {
        return -Graphic.littleDragonMeta.getHeight();
    }

    private double getEndX() {
        return getStartX();
    }

    private double getEndY() {
        return App.HEIGHT+Graphic.littleDragonMeta.getHeight();
    }

    private void setGlowing() {
        timeWhenGlowingStops = App.game.getStack().topPane().getTime().add(glowDuration);
        shipGlowOver.setVisible(true);
    }

    private void explode() {
        final double velX = (getEndX()-getStartX())/movementDuration.toMillis();
        final double velY = (getEndY()-getStartY())/movementDuration.toMillis();

        DataBox.score += pointForDest;
        exploding = true;
        explosions.reset(App.game.getStack().topPane().getTime());
        for(int i = 0; i<Graphic.littleDragonMeta.getExplosionsNumber(); i++)
            explosions.add(new Explosion1(getTranslateX()+Graphic.littleDragonMeta.getExplosionX(i), getTranslateY()+Graphic.littleDragonMeta.getExplosionY(i), velX, velY), Duration.millis(Graphic.littleDragonMeta.getExplosionDelay(i)));
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

        private HitDetection(final LittleDragon topBox, final Rectangle[] hitboxes) {
            super(topBox, hitboxes);
        }

        /*
         * *****************************
         * public
         * *****************************
         */

        @Override
        public void beenHit(final Node byNode) {
            LittleDragon.this.beenHit(Demage.get(byNode));
        }

    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public LittleDragon(final double centerX) {
        hitDetection = new HitDetection(this, Graphic.littleDragonMeta.getHitboxes(0));

        shipGlowOver.setVisible(false);
        shipGlowOver.setBlendMode(BlendMode.ADD);
        getChildren().addAll(shipView, shipGlowOver);
        getChildren().addAll(hitDetection.getSubHitboxes());
        setTranslateX(centerX-Graphic.littleDragonMeta.getWidth()/2);
        setTranslateY(getStartY());
        translTransition.setToY(getEndY());
        translTransition.setInterpolator(Interpolator.LINEAR);
    }

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();

        timeForShot = layered.getTime().add(shootOffset);
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

        if(timeForShot.lessThanOrEqualTo(layered.getTime())) {
            final int shotNumber = shotsPerRound-shotCounter;
            final double leftX = getTranslateX()+15;
            final double leftY = getTranslateY()+35;
            final double rightX = getTranslateX()+Graphic.littleDragonMeta.getWidth()-15;
            final double rightY = getTranslateY()+35;
            final double leftDeg = 180+(degOffset+(360.0/shotsPerRound)*shotNumber);
            final double rightDeg = 180-(degOffset+(360.0/shotsPerRound)*shotNumber);

            layered.addAction(new SkullShot(leftX, leftY, leftDeg));
            layered.addAction(new SkullShot(rightX, rightY, rightDeg));

            shotCounter--;
            if(shotCounter<=0) {
                shotCounter = shotsPerRound;
                timeForShot = timeForShot.add(bigShootInterval);
            }
            else {
                timeForShot = timeForShot.add(smallShootInterval);
            }
        }
        if(timeWhenGlowingStops.lessThanOrEqualTo(layered.getTime()))
            shipGlowOver.setVisible(false);
        if(exploding)
            explosions.update(layered.getTime(), layered);
        if(explosions.isEnded())
            destroy();
    }

    public static LittleDragon createInCenter() {
        return new LittleDragon(App.WIDTH/2);
    }

    public static LittleDragon createAside(final boolean onLeft) {
        final double offset = Graphic.littleDragonMeta.getWidth()/2+10;

        if(onLeft)
            return new LittleDragon(App.WIDTH/2-offset);
        else
            return new LittleDragon(App.WIDTH/2+offset);
    }

}
