
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.pc;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.Graphic;
import com.github.vbsw.sa3k.engine.Collision;
import com.github.vbsw.sa3k.engine.FrameShow;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.prompt.play.CollisionGroup;
import com.github.vbsw.sa3k.prompt.play.Play;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class PlayerShot extends Group implements Layered.Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    private static final Duration flyDuration   = Duration.millis(1000);
    private static final double   range         = App.HEIGHT;

    private static final int      DEMAGE_TYPE_A = 1;

    private final FrameShow       shotView      = new FrameShow(Graphic.playerShot, Graphic.playerShotMeta);
    private final HitDetection    hitDetection;
    private final int             demage;
    private final Rectangle[]     hitboxes;
    private final Timeline        animation;

    private void destroy() {
        final Layered layered = App.game.getStack().topPane();

        setDisable(true);
        hitDetection.setEnabled(false);
        animation.stop();
        layered.removeAnimation(animation);
    }

    private class HitDetection extends Collision.Detection {

        public HitDetection(final PlayerShot playerShot, final Rectangle[] hitboxes) {
            super(playerShot, hitboxes);
        }

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

    public static final int TYPE_A_LEFT_1  = 0;
    public static final int TYPE_A_RIGHT_1 = 1;
    public static final int TYPE_A_LEFT_2  = 2;
    public static final int TYPE_A_RIGHT_2 = 3;
    public static final int TYPE_A_LEFT_3  = 4;
    public static final int TYPE_A_RIGHT_3 = 5;

    public PlayerShot(final double x, final double y, final int shotType) {
        final double x2Offset = Math.cos(Math.PI*3/8)*range;
        final double y2Offset = Math.sin(Math.PI*3/8)*range;
        final double x3Offset = Math.cos(Math.PI/4)*range;
        final double y3Offset = Math.sin(Math.PI/4)*range;

        switch(shotType) {
            case TYPE_A_LEFT_1:
                animation = new Timeline(new KeyFrame(flyDuration, new KeyValue(translateYProperty(), y-range, Interpolator.LINEAR)));
                demage = DEMAGE_TYPE_A;
                shotView.setFrame(0);
                break;
            case TYPE_A_RIGHT_1:
                animation = new Timeline(new KeyFrame(flyDuration, new KeyValue(translateYProperty(), y-range, Interpolator.LINEAR)));
                demage = DEMAGE_TYPE_A;
                shotView.setFrame(1);
                break;
            case TYPE_A_LEFT_2:
                animation = new Timeline(new KeyFrame(flyDuration, new KeyValue(translateXProperty(), x-x2Offset, Interpolator.LINEAR), new KeyValue(translateYProperty(), y-y2Offset, Interpolator.LINEAR)));
                demage = DEMAGE_TYPE_A;
                shotView.setFrame(2);
                setRotate(20);
                break;
            case TYPE_A_RIGHT_2:
                animation = new Timeline(new KeyFrame(flyDuration, new KeyValue(translateXProperty(), x+x2Offset, Interpolator.LINEAR), new KeyValue(translateYProperty(), y-y2Offset, Interpolator.LINEAR)));
                demage = DEMAGE_TYPE_A;
                shotView.setFrame(3);
                setRotate(360-20);
                break;
            case TYPE_A_LEFT_3:
                animation = new Timeline(new KeyFrame(flyDuration, new KeyValue(translateXProperty(), x-x3Offset, Interpolator.LINEAR), new KeyValue(translateYProperty(), y-y3Offset, Interpolator.LINEAR)));
                demage = DEMAGE_TYPE_A;
                shotView.setFrame(4);
                setRotate(5);
                break;
            default:
            case TYPE_A_RIGHT_3:
                animation = new Timeline(new KeyFrame(flyDuration, new KeyValue(translateXProperty(), x+x3Offset, Interpolator.LINEAR), new KeyValue(translateYProperty(), y-y3Offset, Interpolator.LINEAR)));
                demage = DEMAGE_TYPE_A;
                shotView.setFrame(5);
                setRotate(360-5);
                break;
        }
        hitboxes = Graphic.playerShotMeta.getHitboxes(shotType);
        hitDetection = new HitDetection(this, hitboxes);

        setTranslateX(x);
        setTranslateY(y);
        getChildren().add(shotView);
        for(Rectangle hitbox : hitboxes)
            getChildren().add(hitbox);
    }

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();
        layered.addNode(this, Play.LAYER_PLAYER_BULLETS);
        layered.getCollision().addDetection(CollisionGroup.PLAYER_BULLET, hitDetection);
        layered.addAnimation(animation);
        animation.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                destroy();
            }
        });
        animation.play();
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void update() {}

    public int getDemage() {
        return demage;
    }

}
