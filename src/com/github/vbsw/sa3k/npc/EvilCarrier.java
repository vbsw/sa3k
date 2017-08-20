
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
import com.github.vbsw.sa3k.pc.PlayerShip;
import com.github.vbsw.sa3k.prompt.play.CollisionGroup;
import com.github.vbsw.sa3k.prompt.play.Demage;
import com.github.vbsw.sa3k.prompt.play.Level1;
import com.github.vbsw.sa3k.prompt.play.Play;

import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class EvilCarrier extends Group implements Layered.Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    // static
    private static final int           pointForHit          = 10;
    private static final int           pointForDest         = 1000;
    private static final Duration      ASSIM_DURATION       = Duration.seconds(1);

    // object
    private final ImageView            shipGlowOver         = new ImageView(Graphic.evilCarrier);
    private final Layered.ActionsQueue explosions           = new Layered.ActionsQueue(Graphic.evilCarrierMeta.getExplosionsNumber());
    private final TranslateTransition  transTransition      = new TranslateTransition(movementDuration, this);
    private final SequentialTransition seqTransition        = new SequentialTransition();
    private final PerspectiveTransform perspTransform       = new PerspectiveTransform();
    private final Level1               level1;
    private final HitDetection         hitDetection;
    private final PlayerShip           playerShip;

    private Duration                   timeWhenGlowingStops = Duration.millis(0);
    private Duration                   timeToAssimilate     = timeWhenGlowingStops;
    private int                        lives                = 200;
    private boolean                    enabled              = true;
    private boolean                    exploding            = false;
    private double                     lX;
    private double                     tY;

    private void setGlowing() {
        timeWhenGlowingStops = App.game.getStack().topPane().getTime().add(glowDuration);
        shipGlowOver.setVisible(true);
    }

    private void explode() {
        final double shipX = getTranslateX();
        final double shipY = getTranslateY();
        final double velX = (getEndX()-getStartX())/movementDuration.toMillis();
        final double velY = (getEndY()-getStartY())/movementDuration.toMillis();

        DataBox.score += pointForDest;
        exploding = true;
        explosions.reset(App.game.getStack().topPane().getTime());
        for(int i = 0; i<Graphic.evilCarrierMeta.getExplosionsNumber(); i++)
            explosions.add(new Explosion1(shipX+Graphic.evilCarrierMeta.getExplosionX(i), shipY+Graphic.evilCarrierMeta.getExplosionY(i), velX, velY), Duration.millis(Graphic.evilCarrierMeta.getExplosionDelay(i)));
        level1.evilCarrierExploded();
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
        layered.removeAnimation(seqTransition);
        layered.removeAnimation(transTransition);
    }

    /**
     * 
     * @author Vitali Baumtrok
     */
    private class HitDetection extends Collision.Detection {

        private HitDetection(final EvilCarrier evilCarrier, final Rectangle[] hitboxes) {
            super(evilCarrier, hitboxes);
        }

        /*
         * *****************************
         * public
         * *****************************
         */

        @Override
        public void beenHit(final Node byNode) {
            EvilCarrier.this.beenHit(Demage.get(byNode));
        }

    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public static final Duration glowDuration     = Duration.millis(50);

    public static final Duration movementDuration = Duration.seconds(50);

    public EvilCarrier(final Level1 level1, final PlayerShip playerShip) {
        final Rectangle[] hitboxes = Graphic.evilCarrierMeta.getHitboxes(0);

        getChildren().addAll(new ImageView(Graphic.evilCarrier), shipGlowOver);
        getChildren().addAll(hitboxes);

        this.level1 = level1;
        this.playerShip = playerShip;
        hitDetection = new HitDetection(this, hitboxes);
    }

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();
        final Collision collision = layered.getCollision();
        final ScaleTransition scaleTransition1 = new ScaleTransition(Duration.millis(150), this);
        final ScaleTransition scaleTransition2 = new ScaleTransition(Duration.millis(150), this);
        final ScaleTransition scaleTransition3 = new ScaleTransition(Duration.millis(75), this);

        setTranslateX(getStartX());
        setTranslateY(getStartY());
        setScaleX(0);
        setScaleY(0);
        scaleTransition1.setToX(0.3);
        scaleTransition1.setToY(0.7);
        scaleTransition2.setToX(1);
        scaleTransition2.setToY(0.4);
        scaleTransition3.setToY(1);
        layered.addAnimation(seqTransition);
        layered.addAnimation(transTransition);
        layered.addNode(this, Play.LAYER_ENEMY_BG);
        collision.addDetection(CollisionGroup.ENEMY_SHIP, hitDetection);
        timeToAssimilate = layered.getTime().add(Duration.seconds(20));
        shipGlowOver.setVisible(false);
        shipGlowOver.setBlendMode(BlendMode.ADD);
        seqTransition.getChildren().addAll(scaleTransition1, scaleTransition2, scaleTransition3);
        seqTransition.play();
        transTransition.setToY(getEndY());
        transTransition.play();
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
            final double midX = getTranslateX()+Graphic.evilCarrier.getWidth()/2;
            final double midY = getTranslateY()+Graphic.evilCarrier.getHeight()/2;
            destroy();
            layered.addAction(Grell.createThrowOut(midX, midY, midX-100, midY-20));
            layered.addAction(Grell.createThrowOut(midX, midY, midX+100, midY-20));
            layered.addAction(Grell.createThrowOut(midX, midY, midX+50, midY+20));
            layered.addAction(Grell.createThrowOut(midX-10, midY, midX-90, midY+30));
            layered.addAction(Grell.createThrowOut(midX-10, midY-10, 10, 20));
            layered.addAction(Grell.createThrowOut(midX, midY-10, midX+90, midY+70));
            layered.addAction(Grell.createThrowOut(midX+10, midY+10, midX+100, midY-50));
            layered.addAction(Grell.createThrowOut(midX-10, midY, midX-100, midY-50));
            layered.addAction(Grell.createThrowOut(midX+5, midY-15, midX+60, midY+20));
            layered.addAction(Grell.createThrowOut(midX+15, midY-5, midX+40, midY+50));
            layered.addAction(Grell.createThrowOut(midX+5, midY-50, App.WIDTH-Graphic.grellMeta.getWidth()-3, midY-60));
            layered.addAction(Grell.createThrowOut(midX-20, midY-40, midX-40, 0));
            layered.addAction(Grell.createThrowOut(midX+20, midY-45, midX+40, 20));
            layered.addAction(Grell.createThrowOut(midX, midY+30, midX+10, midY+100));
            layered.addAction(Grell.createThrowOut(midX-10, midY+30, midX-90, midY+10));
            layered.addAction(Grell.createThrowOut(midX+10, midY+30, midX+90, midY+20));
            layered.addAction(Grell.createThrowOut(midX-10, midY-30, midX-150, midY));
            layered.addAction(Grell.createThrowOut(midX+10, midY-30, midX+100, midY-130));
            layered.addAction(Grell.createThrowOut(midX, midY, midX-20, midY-30));
            layered.addAction(Grell.createThrowOut(midX, midY, midX-35, midY-5));
            layered.addAction(Grell.createThrowOut(midX, midY, midX-15, midY+30));
            layered.addAction(Grell.createThrowOut(midX, midY, midX+25, midY-30));
            layered.addAction(Grell.createThrowOut(midX, midY, midX+20, midY-5));
            layered.addAction(Grell.createThrowOut(midX, midY, midX+20, midY+30));
            layered.addAction(Grell.createThrowOut(midX, midY, midX-15, midY+10));
            layered.addAction(Grell.createThrowOut(midX, midY, midX+5, midY+15));
            layered.addAction(Grell.createThrowOut(midX, midY, midX-15, midY+25));
            layered.addAction(Grell.createThrowOut(midX, midY, midX-40, midY+25));
            layered.addAction(Grell.createThrowOut(midX, midY, midX-35, midY+20));
            layered.addAction(Grell.createThrowOut(midX, midY, midX-60, midY+22));
            layered.addAction(Grell.createThrowOut(midX, midY, midX-32, midY+50));
            layered.addAction(Grell.createThrowOut(midX-4, midY+20, midX-40, midY+70));
            layered.addAction(Grell.createThrowOut(midX-8, midY+15, midX-45, midY+60));
            layered.addAction(Grell.createThrowOut(midX-5, midY+5, midX-50, midY+65));
            layered.addAction(Grell.createThrowOut(midX-7, midY+7, midX-60, midY+70));
            layered.addAction(Grell.createThrowOut(midX-10, midY+5, midX-60, midY+50));
            layered.addAction(Grell.createThrowOut(midX-5, midY-10, midX-60, midY-100));
            layered.addAction(Grell.createThrowOut(midX-5, midY-10, midX-32, midY-50));
            layered.addAction(Grell.createThrowOut(midX-10, midY-5, midX-90, midY-65));
            layered.addAction(Grell.createThrowOut(midX, midY, midX-60, midY-50));
            layered.addAction(Grell.createThrowOut(midX, midY, 15, midY+30));
            layered.addAction(Grell.createThrowOut(midX-5, midY-30, 30, midY-130));
            layered.addAction(Grell.createThrowOut(midX-7, midY-20, 20, midY-140));
            layered.addAction(Grell.createThrowOut(midX+10, midY-24, midX+130, midY-140));
        }
        // assimilate
        if(timeToAssimilate.lessThanOrEqualTo(layered.getTime())) {
            @SuppressWarnings("deprecation")
            final double fraction = layered.getTime().subtract(timeToAssimilate).divide(ASSIM_DURATION).toMillis();

            if(getEffect()==null) {
                lX = getTranslateX();
                tY = getTranslateY();
                final double width = Graphic.evilCarrierMeta.getWidth();
                final double height = Graphic.evilCarrierMeta.getHeight();
                hitDetection.setEnabled(false);
                transTransition.stop();
                perspTransform.setUlx(0);
                perspTransform.setUly(0);
                perspTransform.setUrx(width);
                perspTransform.setUry(0);
                perspTransform.setLlx(0);
                perspTransform.setLly(height);
                perspTransform.setLrx(width);
                perspTransform.setLry(height);
                setEffect(perspTransform);
            }
            // distort
            if(fraction<1) {
                final double psX = playerShip.getCenterX();
                final double psY = playerShip.getCenterY();
                final double width = Graphic.evilCarrierMeta.getWidth();
                final double height = Graphic.evilCarrierMeta.getHeight();
                final double rX = lX+width;
                final double bY = tY+height;
                // ul
                // perspTransform.setUlx((psX-lX)*(fraction-1.0));
                // perspTransform.setUly((psY-tY)*(fraction-1.0));
                // ur
                // perspTransform.setUrx(width+(psX-rX)*(fraction-1.0));
                // perspTransform.setUry((psY-tY)*(fraction-1.0));
                // ll
                perspTransform.setLlx((psX-lX)*fraction);
                perspTransform.setLly(height+(psY-bY)*fraction);
                // lr
                perspTransform.setLrx(width+(psX-rX)*fraction);
                perspTransform.setLry(height+(psY-bY)*fraction);
            }
            // destroy
            else {
                destroy();
                playerShip.destroy();
            }
        }
    }

    public double getStartX() {
        return (App.WIDTH-Graphic.evilCarrierMeta.getWidth())/2;
    }

    public double getStartY() {
        return -Graphic.evilCarrierMeta.getHeight()*2/3;
    }

    public double getEndX() {
        return getStartX();
    }

    public double getEndY() {
        return App.HEIGHT;
    }

}
