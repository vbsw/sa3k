
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
import com.github.vbsw.sa3k.engine.Layered.Action;
import com.github.vbsw.sa3k.prompt.play.CollisionGroup;
import com.github.vbsw.sa3k.prompt.play.Demage;
import com.github.vbsw.sa3k.prompt.play.Play;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.VLineTo;
import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class Grell extends Group implements Layered.Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    // static
    private static final double   shotOffsetX          = 17;
    private static final double   shotOffsetY          = 28;
    private static final int      scoreForHit          = 5;
    private static final int      scoreForDest         = 40;

    private static final Duration glowDuration         = Duration.millis(50);
    private static final Duration throwOutDuration     = Duration.millis(1000);
    private static final Duration flyDuration          = Duration.seconds(20);
    private static final Duration shootInterval        = Duration.millis(3000);

    // object
    private final FrameAnimation  shipFrames           = new FrameAnimation(Graphic.grell, Graphic.grellMeta, 1);
    private final FrameAnimation  shipGlowOver         = new FrameAnimation(Graphic.grell, Graphic.grellMeta, 1);
    private final HitDetection    hitDetection;
    private final Animation       movementAnimation;
    private final Duration        shootTimerOffset;

    private Duration              timeTillNextShot     = Duration.millis(0);
    private Duration              timeWhenGlowingStops = timeTillNextShot;
    private int                   lives                = 2;
    private boolean               shotReady            = false;

    private boolean               enabled              = true;

    private Grell(final double x, final double y, final Animation animation, final Duration shootTimeOffset) {
        hitDetection = new HitDetection(this, Graphic.grellMeta.getHitboxes(0));
        movementAnimation = animation;
        shootTimerOffset = shootTimeOffset;

        getChildren().addAll(shipFrames, shipGlowOver);
        getChildren().addAll(Graphic.grellMeta.getHitboxes(0));
        setTranslateX(x);
        setTranslateY(y);
        movementAnimation.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent ae) {
                destory();
            }
        });
    }

    private void destory() {
        final Layered layered = App.game.getStack().topPane();

        enabled = false;
        setDisable(true);
        hitDetection.setEnabled(false);
        layered.removeAnimation(movementAnimation);
        layered.removeAnimation(shipFrames.getAnimation());
        layered.removeAnimation(shipGlowOver.getAnimation());
    }

    private void beenHit(final int demage) {
        if(lives>0) {
            final int livesLost = lives-demage<0 ? lives : demage;

            DataBox.score += (scoreForHit*livesLost);
            lives -= livesLost;

            // glowing
            if(lives>0) {
                timeWhenGlowingStops = App.game.getStack().topPane().getTime().add(glowDuration);
                shipGlowOver.setVisible(true);
            }
            // explode
            else if(lives==0) {
                final Layered layered = App.game.getStack().topPane();
                final double x = getTranslateX()+Graphic.grellMeta.getExplosionX(0);
                final double y = getTranslateY()+Graphic.grellMeta.getExplosionY(0);
                final double velX = 0;
                final double velY = 1;

                DataBox.score += scoreForDest;
                DataBox.destroyedGrells++;
                Goody.spawnAfterGrell(x, y, Goody.TYPE_LIFE);
                destory();
                layered.addAction(new Explosion1(x, y, velX, velY));
            }
        }
    }

    /**
     * 
     * @author Vitali Baumtrok
     */
    private static class HitDetection extends Collision.Detection {

        private final Grell ship;

        /*
         * *****************************
         * public
         * *****************************
         */

        public HitDetection(final Grell ship, final Rectangle[] hitboxes) {
            super(ship, hitboxes);
            this.ship = ship;
        }

        @Override
        public void beenHit(final Node byNode) {
            ship.beenHit(Demage.get(byNode));
        }

    }

    /**
     * 
     * @author Vitali Baumtrok
     */
    private static class GrellSpawner implements Layered.Action {

        private final Layered.ActionsQueue queue;
        private boolean                    enabled = true;

        public GrellSpawner(final Layered.ActionsQueue queue) {
            this.queue = queue;
        }

        @Override
        public void init() {
            queue.reset(App.game.getStack().topPane().getTime());
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void update() {
            final Layered layered = App.game.getStack().topPane();
            queue.update(layered.getTime(), layered);
            if(queue.isEnded())
                enabled = false;
        }

    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public static Grell createThrowOut(final double fromX, final double fromY, final double toX, final double toY) {
        final TranslateTransition transTransition1 = new TranslateTransition(throwOutDuration);
        final TranslateTransition transTransition2 = new TranslateTransition(flyDuration);
        final SequentialTransition seqTransition = new SequentialTransition(transTransition1, transTransition2);
        final Grell grell = new Grell(fromX, fromY, seqTransition, Duration.hours(1));

        transTransition1.setToX(toX);
        transTransition1.setToY(toY);
        transTransition1.setInterpolator(Interpolator.EASE_OUT);
        transTransition2.setToY(toY+800);
        transTransition2.setInterpolator(Interpolator.LINEAR);
        seqTransition.setNode(grell);

        return grell;
    }

    public static Layered.Action createSpawner_Arc(final double marginX, final double distanceY, final int amount, final boolean toLeft) {
        final Layered.ActionsQueue queue = new Layered.ActionsQueue(amount);
        final GrellSpawner spawner = new GrellSpawner(queue);

        final double shootOffsetMillis = 800;
        // final double shootIntervalOffsetMillis = 50;
        final double viewWidth = App.WIDTH;
        final double shipWidth = Graphic.grellMeta.getWidth();
        final double shipHeight = Graphic.grellMeta.getHeight();
        final double x = toLeft ? viewWidth-(shipWidth+marginX) : marginX;
        final double y = -shipHeight-10;
        final double radiusX = Math.abs(viewWidth/2-x);
        final double outerX = toLeft ? -(10+shipWidth) : viewWidth+10;

        final Duration spawnInterval = Duration.millis(900);
        final Duration moveDuration = Duration.millis(11*(distanceY+radiusX*2+(viewWidth/2+10)));

        for(int i = 0; i<amount; i++) {
            final PathElement startPoint = new MoveTo(x+shipWidth/2, y+shipHeight/2);
            final PathElement down = new VLineTo(distanceY+y);
            final PathElement curveToRight = new ArcTo(radiusX, radiusX, 0, viewWidth/2, distanceY+y+radiusX, false, toLeft);
            final PathElement toRight = new HLineTo(outerX);
            final Path path = new Path(startPoint, down, curveToRight, toRight);
            final PathTransition pathTransition = new PathTransition(moveDuration, path);
            final Duration shootOffset = Duration.millis(shootOffsetMillis);
            final Grell grell = new Grell(x, y, pathTransition, shootOffset);

            pathTransition.setInterpolator(Interpolator.LINEAR);
            pathTransition.setOrientation(PathTransition.OrientationType.NONE);
            pathTransition.setCycleCount(1);
            pathTransition.setAutoReverse(false);
            pathTransition.setNode(grell);
            queue.add(grell, spawnInterval);
        }

        return spawner;
    }

    public static Action createSpawner_Circle(final double entryX) {
        final int GRELLS_NUMBER = 10;
        final double width = Graphic.grellMeta.getWidth();
        final double height = Graphic.grellMeta.getHeight();
        final double circleRadius = 60;
        final double yMargin = height;
        final double yOffsetCenter = circleRadius+yMargin;
        final double vFlyDistance = App.HEIGHT+circleRadius*2+yMargin*2;

        final Layered.ActionsQueue queue = new Layered.ActionsQueue(GRELLS_NUMBER+1);
        final GrellSpawner spawner = new GrellSpawner(queue);
        final Duration flyDuration = Duration.millis(10000);
        final Duration spawnTime = Duration.millis(0);
        final Duration shootTimeGrell = Duration.seconds(1);

        final TranslateTransition straightTrans = new TranslateTransition(flyDuration);
        final Grell centerGrell = new Grell(entryX-width/2, -yOffsetCenter, straightTrans, shootTimeGrell);

        straightTrans.setNode(centerGrell);
        straightTrans.setToY(vFlyDistance-yOffsetCenter);
        straightTrans.setInterpolator(Interpolator.LINEAR);
        queue.add(centerGrell, spawnTime);

        for(int i = 0; i<GRELLS_NUMBER; i++) {
            final double stepInRad = (2*Math.PI)/GRELLS_NUMBER;
            final double xOffsetEdge = entryX+(Math.sin(stepInRad*i)*circleRadius)-width/2;
            final double yOffsetEdge = yOffsetCenter+(Math.cos(stepInRad*i)*circleRadius);
            final TranslateTransition straightTransEdge = new TranslateTransition(flyDuration);
            final Grell edgeGrell = new Grell(xOffsetEdge, -yOffsetEdge, straightTransEdge, shootTimeGrell);

            straightTransEdge.setNode(edgeGrell);
            straightTransEdge.setToY(vFlyDistance-yOffsetEdge);
            straightTransEdge.setInterpolator(Interpolator.LINEAR);
            queue.add(edgeGrell, spawnTime);
        }

        return spawner;
    }

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();

        shipGlowOver.setVisible(false);
        shipGlowOver.setBlendMode(BlendMode.ADD);
        timeTillNextShot = layered.getTime().add(shootTimerOffset);
        layered.addNode(this, Play.LAYER_ENEMY_BG);
        layered.addAnimation(movementAnimation);
        layered.addAnimation(shipFrames.getAnimation());
        layered.addAnimation(shipGlowOver.getAnimation());
        layered.getCollision().addDetection(CollisionGroup.ENEMY_SHIP, hitDetection);
        movementAnimation.play();
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
        if(timeTillNextShot.lessThanOrEqualTo(layered.getTime())) {
            timeTillNextShot = timeTillNextShot.add(shootInterval);
            // prepare shot
            shipFrames.getAnimation().play();
            shipGlowOver.getAnimation().play();
            shotReady = true;
        }
        if(shotReady&&(shipFrames.getCurrentFrameNumber()==5)) {
            shotReady = false;
            layered.addAction(new ToxicShot(getTranslateX()+shotOffsetX, getTranslateY()+shotOffsetY));
        }
    }

}
