
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
import com.github.vbsw.sa3k.engine.FrameShow;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.pc.PlayerShip;
import com.github.vbsw.sa3k.prompt.play.CollisionGroup;
import com.github.vbsw.sa3k.prompt.play.Demage;
import com.github.vbsw.sa3k.prompt.play.Level1;
import com.github.vbsw.sa3k.prompt.play.Play;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class Boss1 extends Group implements Layered.Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    // static
    private static final double   Y_START                 = -150;
    private static final double   Y_RANGE                 = 150;
    private static final double   SHIP_WIDTH              = 170;
    private static final double   MARGIN_X_SHOULDER       = 5;
    private static final Duration MOVE_DURATION           = Duration.seconds(3);
    private static final Duration OPEN_HAND_DURATION      = Duration.seconds(1);
    private static final Duration HANDS_SHOOT_INTERVAL    = Duration.millis(840);
    private static final Duration LAUNCHER_SHOOT_INTERVAL = Duration.millis(400);

    // object
    private final Hand            leftHand                = new Hand(true);
    private final Hand            rightHand               = new Hand(false);
    private final Shoulder        leftShoulder            = new Shoulder(0);
    private final Shoulder        rightShoulder           = new Shoulder(1);
    private final Shoulder        middleShoulder          = new Shoulder(2);
    private final Hull            hull                    = new Hull();
    private final Head            head                    = new Head();
    private final Launcher        leftLauncher            = new Launcher(0);
    private final Launcher        rightLauncher           = new Launcher(1);
    private final Launcher        middleLauncher          = new Launcher(2);

    private final Level1          level1;
    private final PlayerShip      playerShip;

    private Duration              timeToTransform         = Duration.INDEFINITE;
    private int                   action                  = 0;
    private boolean               enabled                 = true;

    private void destroy() {
        enabled = false;
        setDisable(true);
        level1.bossDestroyed();
    }

    private class Hand extends Group implements Layered.Action {

        private static final int          pointForHit          = 10;
        private static final int          pointForDest         = 1000;

        private final TranslateTransition movement             = new TranslateTransition(MOVE_DURATION, this);
        private final FrameShow           shipGlowOver         = new FrameShow(Graphic.bossHands, Graphic.bossHandsMeta);
        private final HitDetection        hitDetection;
        private final Timeline            open;
        private final Timeline            close;
        private final boolean             left;

        private Duration                  timeWhenNextAction;
        private Duration                  timeWhenGlowingStops = Duration.millis(0);
        private int                       lives                = 200;
        private int                       action               = 0;
        private boolean                   enabled              = true;

        private void setGlowing() {
            timeWhenGlowingStops = App.game.getStack().topPane().getTime().add(glowDuration);
            shipGlowOver.setVisible(true);
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

        private void explode() {
            final Layered layered = App.game.getStack().topPane();
            final double x = getTranslateX()+Graphic.bossHandsMeta.getWidth()/2;
            final double y = getTranslateY()+Graphic.bossHandsMeta.getHeight()/2;
            final double offset = 10;
            DataBox.score += pointForDest;

            destroy();
            layered.addAction(new Explosion1(x, y, 0, 0));
            layered.addAction(new Explosion1(x+offset, y-offset, 0, 0));
            layered.addAction(new Explosion1(x-offset, y-offset, 0, 0));
            if(left)
                layered.addAction(new Explosion1(x-offset, y+offset, 0, 0));
            else
                layered.addAction(new Explosion1(x+offset, y+offset, 0, 0));
        }

        public Hand(final boolean leftHand) {
            final double yOffset = 70;
            final double yStart = Y_START+yOffset;
            final Rotate rotate = new Rotate();
            final FrameShow hand = new FrameShow(Graphic.bossHands, Graphic.bossHandsMeta);
            left = leftHand;

            if(left) {
                hitDetection = new HitDetection(this, Graphic.bossHandsMeta.getHitboxes(0));
                open = new Timeline(new KeyFrame(OPEN_HAND_DURATION, new KeyValue(rotate.angleProperty(), 50.0)));
                close = new Timeline(new KeyFrame(OPEN_HAND_DURATION, new KeyValue(rotate.angleProperty(), 0.0)));
                shipGlowOver.setFrame(0);
                hand.setFrame(0);
                setTranslateX(App.CENTER_X-SHIP_WIDTH/2);
                rotate.setPivotX(33);
            }
            else {
                final double handsWidth = Graphic.bossHandsMeta.getWidth();
                hitDetection = new HitDetection(this, Graphic.bossHandsMeta.getHitboxes(1));
                open = new Timeline(new KeyFrame(OPEN_HAND_DURATION, new KeyValue(rotate.angleProperty(), -50.0)));
                close = new Timeline(new KeyFrame(OPEN_HAND_DURATION, new KeyValue(rotate.angleProperty(), 0.0)));
                shipGlowOver.setFrame(1);
                hand.setFrame(1);
                setTranslateX(App.CENTER_X+SHIP_WIDTH/2-handsWidth);
                rotate.setPivotX(handsWidth-33);
            }
            shipGlowOver.setBlendMode(BlendMode.ADD);
            shipGlowOver.setVisible(false);
            rotate.setPivotY(0);
            movement.setToY(Y_RANGE+yStart);
            setTranslateY(yStart);
            getTransforms().add(rotate);
            getChildren().addAll(hand, shipGlowOver);
            getChildren().addAll(hitDetection.getSubHitboxes());
        }

        @Override
        public void init() {
            final Layered layered = App.game.getStack().topPane();

            layered.addNode(this, Play.LAYER_ENEMY_BG);
            layered.addAnimation(movement);
            layered.addAnimation(open);
            layered.addAnimation(close);
            layered.getCollision().addDetection(CollisionGroup.ENEMY_SHIP, hitDetection);
            timeWhenNextAction = layered.getTime().add(MOVE_DURATION.add(OPEN_HAND_DURATION));
            movement.play();
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void update() {
            final Layered layered = App.game.getStack().topPane();
            final double centerX = getTranslateX()+Graphic.bossHandsMeta.getWidth()/2+(left ? -30 : 30);
            final double centerY = getTranslateY()+Graphic.bossHandsMeta.getHeight()/2-10;

            if(timeWhenGlowingStops.lessThanOrEqualTo(layered.getTime()))
                shipGlowOver.setVisible(false);
            if(timeWhenNextAction.lessThanOrEqualTo(layered.getTime())) {
                switch(action) {
                    case 0: // open hand
                        open.play();
                        timeWhenNextAction = layered.getTime().add(OPEN_HAND_DURATION);
                        action = 1;
                        break;
                    case 1: // shoot
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        timeWhenNextAction = layered.getTime().add(HANDS_SHOOT_INTERVAL);
                        action++;
                        EnergyShot.spawnMany(centerX, centerY, Play.LAYER_BG_ITEMS);
                        break;
                    case 9: // close hand
                        close.play();
                        timeWhenNextAction = layered.getTime().add(OPEN_HAND_DURATION.add(HANDS_SHOOT_INTERVAL).add(OPEN_HAND_DURATION));
                        action = 0;
                }
            }
        }

        public void destroy() {
            final Layered layered = App.game.getStack().topPane();

            enabled = false;
            setDisable(true);
            hitDetection.setEnabled(false);
            layered.removeAnimation(movement);
            layered.removeAnimation(open);
            layered.removeAnimation(close);
        }

        /**
         * 
         * @author Vitali Baumtrok
         */
        public class HitDetection extends Collision.Detection {

            public HitDetection(final Group ship, final Rectangle[] hitboxes) {
                super(ship, hitboxes);
            }

            /*
             * *****************************
             * public
             * *****************************
             */

            @Override
            public void beenHit(final Node byNode) {
                Hand.this.beenHit(Demage.get(byNode));
            }

        }

    }

    private class Shoulder extends Group implements Layered.Action {

        private static final int          pointForHit          = 10;
        private static final int          pointForDest         = 1000;

        private final TranslateTransition movement             = new TranslateTransition(MOVE_DURATION, this);
        private final FrameShow           shipGlowOver         = new FrameShow(Graphic.bossFlesh, Graphic.bossFleshMeta);
        private final TranslateTransition pullingOut           = new TranslateTransition(OPEN_HAND_DURATION, this);
        private final HitDetection        hitDetection;
        private final int                 type;

        private Duration                  timeWhenGlowingStops = Duration.millis(0);
        private int                       lives                = 100;
        private boolean                   enabled              = true;

        private void setGlowing() {
            timeWhenGlowingStops = App.game.getStack().topPane().getTime().add(glowDuration);
            shipGlowOver.setVisible(true);
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

        private void explode() {
            final Layered layered = App.game.getStack().topPane();
            final double shoulderTrueWidth = 45;
            DataBox.score += pointForDest;

            destroy();
            if(type<2) {
                final double x = getTranslateX()+Graphic.bossFleshMeta.getWidth()/2;
                final double y = getTranslateY()+shoulderTrueWidth/2;
                layered.addAction(new Explosion1(x, y, 0, 0));
                if(type==0)
                    leftLauncher.destroy();
                else
                    rightLauncher.destroy();
            }
            else {
                final double x = getTranslateX()+shoulderTrueWidth/2;
                final double y = getTranslateY()+Graphic.bossFleshMeta.getWidth()/2;
                layered.addAction(new Explosion1(x, y, 0, 0));
                middleLauncher.destroy();
            }
        }

        private void pullOutLauncher() {
            switch(type) {
                case 0:
                    leftLauncher.pullOut();
                    break;
                case 1:
                    rightLauncher.pullOut();
                    break;
                case 2:
                default:
                    middleLauncher.pullOut();
            }
        }

        /**
         * 
         * @author Vitali Baumtrok
         */
        private class HitDetection extends Collision.Detection {

            public HitDetection(final Group ship, final Rectangle[] hitboxes) {
                super(ship, hitboxes);
            }

            /*
             * *****************************
             * public
             * *****************************
             */

            @Override
            public void beenHit(final Node byNode) {
                Shoulder.this.beenHit(Demage.get(byNode));
            }

        }

        /**
         * @param type
         *            if 0, then it's left; if 1, then it's right;
         *            if 2, then it's middle
         */
        public Shoulder(final int type) {
            final double shoulderTrueWidth = 45;
            final double yOffset = type<2 ? 32 : 45;
            final double yStart = Y_START+yOffset;
            final double pullOutRange = 20;
            final FrameShow shoulder = new FrameShow(Graphic.bossFlesh, Graphic.bossFleshMeta);
            this.type = type;

            switch(type) {
                case 0: // left
                    final double xLeft = App.CENTER_X+MARGIN_X_SHOULDER-SHIP_WIDTH/2;
                    hitDetection = new HitDetection(this, Graphic.bossFleshMeta.getHitboxes(0));
                    setTranslateX(xLeft);
                    pullingOut.setToX(xLeft-pullOutRange);
                    shipGlowOver.setFrame(0);
                    shoulder.setFrame(0);
                    break;
                case 1: // right
                    final double xRight = App.CENTER_X-MARGIN_X_SHOULDER+SHIP_WIDTH/2-Graphic.bossFleshMeta.getWidth();
                    hitDetection = new HitDetection(this, Graphic.bossFleshMeta.getHitboxes(1));
                    setTranslateX(xRight);
                    pullingOut.setToX(xRight+pullOutRange);
                    shipGlowOver.setFrame(1);
                    shoulder.setFrame(1);
                    break;
                case 2: // middle
                default:
                    final double xMiddle = App.CENTER_X-shoulderTrueWidth/2;
                    hitDetection = new HitDetection(this, Graphic.bossFleshMeta.getHitboxes(2));
                    setTranslateX(xMiddle);
                    pullingOut.setToY(yStart+Y_RANGE+pullOutRange);
                    shipGlowOver.setFrame(2);
                    shoulder.setFrame(2);
            }
            setTranslateY(yStart);
            movement.setToY(yStart+Y_RANGE);
            shipGlowOver.setBlendMode(BlendMode.ADD);
            shipGlowOver.setVisible(false);
            pullingOut.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent ae) {
                    final Layered layered = App.game.getStack().topPane();
                    layered.getCollision().addDetection(CollisionGroup.ENEMY_SHIP, hitDetection);
                    pullOutLauncher();
                }
            });

            getChildren().addAll(shoulder, shipGlowOver);
            getChildren().addAll(hitDetection.getSubHitboxes());
        }

        @Override
        public void init() {
            final Layered layered = App.game.getStack().topPane();

            layered.addNode(this, Play.LAYER_ENEMY_BG);
            layered.addAnimation(movement);
            layered.addAnimation(pullingOut);
            movement.play();
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

        public void destroy() {
            final Layered layered = App.game.getStack().topPane();

            enabled = false;
            setDisable(true);
            hitDetection.setEnabled(false);
            layered.removeAnimation(movement);
            layered.removeAnimation(pullingOut);
        }

        public void pullOut() {
            pullingOut.play();
        }

    }

    /**
     * 
     * @author Vitali Baumtrok
     */
    private class Launcher extends FrameAnimation implements Layered.Action {

        private final TranslateTransition movement    = new TranslateTransition(MOVE_DURATION, this);
        private final TranslateTransition pullingOut  = new TranslateTransition(OPEN_HAND_DURATION, this);
        private final Transition          blendIn     = new BlendIn(OPEN_HAND_DURATION);
        private final int                 type;

        private Duration                  timeToShoot = Duration.INDEFINITE;
        private boolean                   enabled     = true;

        /**
         * 
         * @author Vitali Baumtrok
         */
        private class BlendIn extends Transition {

            private boolean init = true;

            public BlendIn(final Duration duration) {
                setCycleDuration(duration);
                setCycleCount(1);
            }

            @Override
            protected void interpolate(final double frac) {
                final double height = frac*Graphic.bossLauncherMeta.getHeight();
                final Rectangle2D viewPort = getViewport();
                final Rectangle2D newViewPort = new Rectangle2D(viewPort.getMinX(), viewPort.getMinY(), viewPort.getWidth(), height);
                setViewport(newViewPort);
                if(init) {
                    init = false;
                    setVisible(true);
                }
            }

        }

        public Launcher(final int type) {
            super(Graphic.bossLauncher, Graphic.bossLauncherMeta, 1);

            final double yOffset = type<2 ? 32 : 65;
            final double yStart = Y_START+yOffset;
            final double marginX = MARGIN_X_SHOULDER;
            this.type = type;

            switch(type) {
                case 0: // left
                    final double xLeft = App.CENTER_X+marginX-SHIP_WIDTH/2;
                    setRotate(180);
                    setTranslateX(xLeft);
                    break;
                case 1: // right
                    final double xRight = App.CENTER_X-marginX+SHIP_WIDTH/2-Graphic.bossLauncherMeta.getWidth();
                    setRotate(180);
                    setTranslateX(xRight);
                    break;
                case 2: // middle
                default:
                    final double xMiddle = App.CENTER_X-Graphic.bossLauncherMeta.getWidth()/2;
                    setTranslateX(xMiddle);
            }
            setTranslateY(yStart);
            setVisible(false);
            movement.setToY(yStart+Y_RANGE);
            pullingOut.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent ae) {
                    getAnimation().play();
                }
            });
            getAnimation().setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent ae) {
                    timeToShoot = App.game.getStack().topPane().getTime().add(LAUNCHER_SHOOT_INTERVAL);
                }
            });
        }

        @Override
        public void init() {
            final Layered layered = App.game.getStack().topPane();

            layered.addNode(this, Play.LAYER_ENEMY_BG);
            layered.addAnimation(movement);
            layered.addAnimation(pullingOut);
            layered.addAnimation(blendIn);
            layered.addAnimation(getAnimation());
            timeToShoot = Duration.INDEFINITE;
            movement.play();
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void update() {
            final Layered layered = App.game.getStack().topPane();

            if(timeToShoot.lessThanOrEqualTo(layered.getTime())) {
                final double centerX = getTranslateX()+getBoundsInParent().getWidth()/2;
                final double centerY = getTranslateY()+getBoundsInParent().getHeight()/2;
                final double fromDeg = 45;
                final double toDeg = 315;
                final int numberOfStrains = 7;
                final double degreesStep = (toDeg-fromDeg)/numberOfStrains;
                timeToShoot = layered.getTime().add(LAUNCHER_SHOOT_INTERVAL);

                for(int i = 0; i<numberOfStrains; i++) {
                    layered.addAction(new BugShot(centerX, centerY, fromDeg+i*degreesStep+degreesStep/2));
                }
            }
        }

        public void destroy() {
            final Layered layered = App.game.getStack().topPane();

            enabled = false;
            setDisable(true);
            layered.removeAnimation(movement);
            layered.removeAnimation(pullingOut);
            layered.removeAnimation(blendIn);
            layered.removeAnimation(getAnimation());
        }

        public void pullOut() {
            final double pullOutRange = type<2 ? 38 : -38;
            pullingOut.setToY(getTranslateY()+pullOutRange);
            pullingOut.play();
            blendIn.play();
        }

    }

    private class Hull extends Group implements Layered.Action {

        private static final int           V_STEPS_NUMBER        = 3;
        private static final int           EXPLOSION_STEPS       = 10;
        private static final int           EXPLOSION_NUMBER      = (EXPLOSION_STEPS*2)*V_STEPS_NUMBER+(EXPLOSION_STEPS*2);

        private static final int           pointForDest          = 2000;

        private final TranslateTransition  movement              = new TranslateTransition(MOVE_DURATION, this);
        private final ImageView            shipGlowOver          = new ImageView(Graphic.bossHull);
        private final Layered.ActionsQueue explosions            = new Layered.ActionsQueue(EXPLOSION_NUMBER);

        private Duration                   timeWhenGlowingStarts = Duration.INDEFINITE;
        private Duration                   timeWhenGlowingStops  = Duration.INDEFINITE;
        private boolean                    exploding             = false;
        private int                        timesToGlow           = 20;
        private boolean                    enabled               = true;

        /**
         * Sets the ship glowing. Also initializes the explosion.
         */
        private void setGlowing() {
            timeWhenGlowingStops = App.game.getStack().topPane().getTime().add(PlayerShip.glowDuration);
            shipGlowOver.setVisible(true);
            timesToGlow--;
        }

        public Hull() {
            final double yOffset = 10;
            final double x = App.CENTER_X-Graphic.bossHullMeta.getWidth()/2;
            final double yStart = Y_START+yOffset;
            final ImageView hull = new ImageView(Graphic.bossHull);

            setTranslateX(x);
            setTranslateY(yStart);
            getChildren().addAll(hull, shipGlowOver);
            movement.setToY(yStart+Y_RANGE);
            shipGlowOver.setBlendMode(BlendMode.ADD);
            shipGlowOver.setVisible(false);
            explosions.reset(Duration.INDEFINITE);
        }

        @Override
        public void init() {
            final Layered layered = App.game.getStack().topPane();

            layered.addNode(this, Play.LAYER_ENEMY_BG);
            layered.addAnimation(movement);
            movement.play();
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void update() {
            final Layered layered = App.game.getStack().topPane();

            if(timeWhenGlowingStarts.lessThanOrEqualTo(layered.getTime())) {
                timeWhenGlowingStarts = Duration.INDEFINITE; // no next glow
                setGlowing();
            }
            else if(timeWhenGlowingStops.lessThanOrEqualTo(layered.getTime())) {
                // stop glowing
                timeWhenGlowingStops = Duration.INDEFINITE; // no next glow stop
                shipGlowOver.setVisible(false);
                // next glow or..
                if(timesToGlow>0) {
                    timeWhenGlowingStarts = layered.getTime().add(PlayerShip.glowDuration);
                }
                // ..start explosions, executed only once
                else if(timesToGlow==0) {
                    DataBox.score += pointForDest;
                    exploding = true;
                    timesToGlow--;
                    explosions.reset(layered.getTime());
                    playerShip.setShootingEnabled(false);
                }
                // otherwise do nothing
            }
            if(exploding) {
                explosions.update(layered.getTime(), layered);
                if(explosions.isEnded())
                    destroy();
            }
        }

        /**
         * Destroys the ship. Also destroys the boss.
         */
        public void destroy() {
            final Layered layered = App.game.getStack().topPane();

            enabled = false;
            setDisable(true);
            layered.removeAnimation(movement);
            Boss1.this.destroy();
        }

        public void explode() {
            final double centerX = getTranslateX()+getBoundsInParent().getWidth()/2;
            final double centerY = getTranslateY()+getBoundsInParent().getHeight()/2;
            final double margin = 30;
            final double stepWidth = (App.WIDTH/2-margin)/EXPLOSION_STEPS;
            final double stepHeight = 7;
            final Duration zeroInterval = Duration.millis(0);
            final Duration explosionInterval = Duration.millis(30);

            setGlowing();
            explosions.add(new Explosion1(centerX, centerY, 0, 0), zeroInterval);
            for(int i = 1; i<EXPLOSION_STEPS; i++) {
                // left
                explosions.add(new Explosion1(centerX-stepWidth*i, centerY, 0, 0), zeroInterval);
                // right
                explosions.add(new Explosion1(centerX+stepWidth*i, centerY, 0, 0), zeroInterval);
            }
            for(int i = 1; i<=V_STEPS_NUMBER; i++) {
                final double topY = centerY-i*stepHeight;
                final double bottomY = centerY+i*stepHeight;
                // top left
                explosions.add(new Explosion1(centerX-stepWidth, topY, 0, 0), explosionInterval);
                // top right
                explosions.add(new Explosion1(centerX+stepWidth, topY, 0, 0), zeroInterval);
                // bottom left
                explosions.add(new Explosion1(centerX-stepWidth, bottomY, 0, 0), zeroInterval);
                // bottom right
                explosions.add(new Explosion1(centerX+stepWidth, bottomY, 0, 0), zeroInterval);
                // center
                explosions.add(new Explosion1(centerX, centerY, 0, 0), zeroInterval);
                for(int j = 2; j<EXPLOSION_STEPS-i*(EXPLOSION_STEPS/V_STEPS_NUMBER); j++) {
                    // top left
                    explosions.add(new Explosion1(centerX-stepWidth*j, topY, 0, 0), zeroInterval);
                    // top right
                    explosions.add(new Explosion1(centerX+stepWidth*j, topY, 0, 0), zeroInterval);
                    // bottom left
                    explosions.add(new Explosion1(centerX-stepWidth*j, bottomY, 0, 0), zeroInterval);
                    // bottom right
                    explosions.add(new Explosion1(centerX+stepWidth*j, bottomY, 0, 0), zeroInterval);
                }
            }
        }

    }

    private class Head extends Group implements Layered.Action {

        private static final int           pointForHit          = 10;
        private static final int           pointForDest         = 1000;

        private final TranslateTransition  movement             = new TranslateTransition(MOVE_DURATION, this);
        private final FrameAnimation       ship                 = new FrameAnimation(Graphic.bossHead, Graphic.bossHeadMeta, 1);
        private final FrameAnimation       shipGlowOver         = new FrameAnimation(Graphic.bossHead, Graphic.bossHeadMeta, 1);
        private final Layered.ActionsQueue explosions           = new Layered.ActionsQueue(Graphic.bossHeadMeta.getExplosionsNumber());
        private final HitDetection         hitDetection;

        private Duration                   timeWhenNextAction;
        private Duration                   timeWhenGlowingStops = Duration.millis(0);
        private boolean                    exploding            = false;
        private int                        lives                = 200;
        private int                        action               = 0;
        private boolean                    enabled              = true;

        private void setGlowing() {
            timeWhenGlowingStops = App.game.getStack().topPane().getTime().add(glowDuration);
            shipGlowOver.setVisible(true);
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

        private void explode() {
            final double x = getTranslateX();
            final double y = getTranslateY();
            exploding = true;
            DataBox.score += pointForDest;

            hitDetection.setEnabled(false);
            explosions.reset(App.game.getStack().topPane().getTime());
            for(int i = 0; i<Graphic.bossHeadMeta.getExplosionsNumber(); i++)
                explosions.add(new Explosion1(x+Graphic.bossHeadMeta.getExplosionX(i), y+Graphic.bossHeadMeta.getExplosionY(i), 0, 0), Duration.millis(Graphic.bossHeadMeta.getExplosionDelay(i)));
        }

        public Head() {
            final double x = App.CENTER_X-Graphic.bossHeadMeta.getWidth()/2;
            final double yStart = Y_START;
            hitDetection = new HitDetection(this, Graphic.bossHeadMeta.getHitboxes(0));

            setTranslateX(x);
            setTranslateY(yStart);
            movement.setToY(yStart+Y_RANGE);
            shipGlowOver.setBlendMode(BlendMode.ADD);
            shipGlowOver.setVisible(false);
            explosions.reset(Duration.INDEFINITE);

            getChildren().addAll(ship, shipGlowOver);
            getChildren().addAll(hitDetection.getSubHitboxes());
        }

        @Override
        public void init() {
            final Layered layered = App.game.getStack().topPane();
            timeWhenNextAction = layered.getTime().add(MOVE_DURATION).add(OPEN_HAND_DURATION).add(OPEN_HAND_DURATION);

            layered.addNode(this, Play.LAYER_ENEMY_BG);
            layered.addAnimation(movement);
            layered.addAnimation(ship.getAnimation());
            layered.addAnimation(shipGlowOver.getAnimation());
            layered.getCollision().addDetection(CollisionGroup.ENEMY_SHIP, hitDetection);
            movement.play();
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void update() {
            final Layered layered = App.game.getStack().topPane();

            if(exploding) {
                explosions.update(layered.getTime(), layered);
                if(explosions.isEnded())
                    destroy();
            }
            if(timeWhenGlowingStops.lessThanOrEqualTo(layered.getTime()))
                shipGlowOver.setVisible(false);
            if(timeWhenNextAction.lessThanOrEqualTo(layered.getTime())) {
                final double centerX = getTranslateX()+Graphic.bossHeadMeta.getWidth()/2;
                final double centerY = getTranslateY()+Graphic.bossHeadMeta.getHeight();
                final double distanceX = playerShip.getCenterX()-centerX;
                final double distanceY = -1*(playerShip.getCenterY()-centerY);
                final double directionDegrees = Math.toDegrees(Math.atan(distanceX/distanceY))+(distanceY<0 ? 180 : 0);

                switch(action) {
                    case 0: // prepare to shoot
                        action = 1;
                        timeWhenNextAction = layered.getTime().add(Duration.millis(300)); // 5th frame
                        ship.getAnimation().play();
                        shipGlowOver.getAnimation().play();
                        break;
                    case 1: // shoot
                        action = 0;
                        timeWhenNextAction = layered.getTime().add(OPEN_HAND_DURATION.multiply(2));
                        layered.addAction(new SwordShot(centerX, centerY, directionDegrees, Play.LAYER_ENEMY_FG));
                    default:
                }
            }
        }

        public void destroy() {
            final Layered layered = App.game.getStack().topPane();

            enabled = false;
            setDisable(true);
            hitDetection.setEnabled(false);
            layered.removeAnimation(movement);
            layered.removeAnimation(ship.getAnimation());
            layered.removeAnimation(shipGlowOver.getAnimation());
        }

        /**
         * 
         * @author Vitali Baumtrok
         */
        public class HitDetection extends Collision.Detection {

            public HitDetection(final Group ship, final Rectangle[] hitboxes) {
                super(ship, hitboxes);
            }

            /*
             * *****************************
             * public
             * *****************************
             */

            @Override
            public void beenHit(final Node byNode) {
                Head.this.beenHit(Demage.get(byNode));
            }

        }

    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public static final Duration glowDuration     = Duration.millis(50);

    public static final Duration movementDuration = Duration.seconds(50);

    public Boss1(final Level1 level1, final PlayerShip playerShip) {
        this.level1 = level1;
        this.playerShip = playerShip;
    }

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();

        layered.addAction(leftHand);
        layered.addAction(rightHand);
        layered.addAction(leftLauncher);
        layered.addAction(rightLauncher);
        layered.addAction(leftShoulder);
        layered.addAction(rightShoulder);
        layered.addAction(middleShoulder);
        layered.addAction(hull);
        layered.addAction(head);
        layered.addAction(middleLauncher);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void update() {
        final Layered layered = App.game.getStack().topPane();

        // wait before transform
        if((action==0)&&!leftHand.isEnabled()&&!rightHand.isEnabled()&&!head.isEnabled()) {
            action = 1;
            timeToTransform = layered.getTime().add(OPEN_HAND_DURATION);
        }
        // transform
        else if((action==1)&&timeToTransform.lessThanOrEqualTo(layered.getTime())) {
            action = 2;
            timeToTransform = Duration.INDEFINITE;
            leftShoulder.pullOut();
            rightShoulder.pullOut();
            middleShoulder.pullOut();
        }
        // destroy
        else if((action==2)&&!leftShoulder.isEnabled()&&!rightShoulder.isEnabled()&&!middleShoulder.isEnabled()) {
            hull.explode();
            action = 3;
        }
    }

}
