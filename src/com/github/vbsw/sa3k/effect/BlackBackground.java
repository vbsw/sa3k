
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.effect;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.DataBox;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.prompt.play.Play;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class BlackBackground implements Layered.Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    private boolean              enabled         = true;

    /*
     * *****************************
     * public
     * *****************************
     */

    public static final Duration EXPAND_DURATION = Duration.seconds(1);

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();
        final Color circleColor = new Color(0, 0, 0, 0.5);
        final Circle circle = new Circle(DataBox.playerShipCenterCoordX, DataBox.playerShipCenterCoordY, 0, circleColor);
        final Rectangle clippingArea = new Rectangle(0, 0, App.WIDTH, App.HEIGHT);
        final Group group = new Group();
        final Timeline animation = new Timeline(new KeyFrame(EXPAND_DURATION, new KeyValue(circle.radiusProperty(), 400, Interpolator.EASE_IN)));

        clippingArea.setFill(Color.TRANSPARENT);
        group.setBlendMode(BlendMode.SRC_OVER);
        group.getChildren().addAll(circle, clippingArea);

        layered.addNode(group, Play.LAYER_BG);
        layered.addAnimation(animation);
        animation.play();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void update() {}

}
