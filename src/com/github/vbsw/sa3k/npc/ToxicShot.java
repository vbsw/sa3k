
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.npc;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.Graphic;
import com.github.vbsw.sa3k.engine.Collision;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.prompt.play.CollisionGroup;
import com.github.vbsw.sa3k.prompt.play.Play;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class ToxicShot extends Group implements Layered.Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    private static final Duration     flyTime   = Duration.millis(4500);

    private final TranslateTransition animation = new TranslateTransition(flyTime, this);
    private final HitDetection        hitDetection;

    private void destroy() {
        final Layered layered = App.game.getStack().topPane();

        setDisable(true);
        animation.stop();
        hitDetection.setEnabled(false);
        layered.removeAnimation(animation);
    }

    private static class HitDetection extends Collision.Detection {

        private final ToxicShot toxicShot;

        public HitDetection(final ToxicShot toxicShot, final Rectangle[] rectangles) {
            super(toxicShot, rectangles);
            this.toxicShot = toxicShot;
        }

        @Override
        public void beenHit(final Node byNode) {
            toxicShot.destroy();
        }

    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public ToxicShot(final double x, final double y) {
        hitDetection = new HitDetection(this, Graphic.toxicShotMeta.getHitboxes(0));
        setTranslateX(x);
        setTranslateY(y);
        animation.setInterpolator(Interpolator.LINEAR);
        animation.setToX(x);
        animation.setToY(y+App.HEIGHT+App.HEIGHT/2);
        animation.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent ae) {
                destroy();
            }
        });
        getChildren().add(new ImageView(Graphic.toxicShot));
        getChildren().addAll(hitDetection.getSubHitboxes());
    }

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();

        layered.addNode(this, Play.LAYER_ENEMY_BULLETS);
        layered.addAnimation(animation);
        layered.getCollision().addDetection(CollisionGroup.ENEMY_BULLET, hitDetection);
        animation.play();
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void update() {}

}
