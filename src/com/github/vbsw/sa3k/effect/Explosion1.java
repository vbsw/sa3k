
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.effect;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.Graphic;
import com.github.vbsw.sa3k.engine.FrameAnimation;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.prompt.play.Play;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class Explosion1 implements Layered.Action, EventHandler<ActionEvent> {

    private final FrameAnimation      framesView = new FrameAnimation(Graphic.explosion1, Graphic.explosion1Meta, 1);
    private final TranslateTransition movementAnimation;

    public Explosion1(final double centerX, final double centerY, final double velX, final double velY) {
        final Duration movementCompleteDelay = Graphic.explosion1Meta.getCompleteDelay().add(Duration.millis(20));
        final double x = centerX-Graphic.explosion1Meta.getWidth()/2;
        final double y = centerY-Graphic.explosion1Meta.getHeight()/2;
        final double toX = x+velX*movementCompleteDelay.toMillis();
        final double toY = y+velY*movementCompleteDelay.toMillis();
        movementAnimation = new TranslateTransition(movementCompleteDelay, framesView);

        framesView.setTranslateX(x);
        framesView.setTranslateY(y);
        movementAnimation.setToX(toX);
        movementAnimation.setToY(toY);
    }

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();
        layered.addAnimation(framesView.getAnimation());
        layered.addNode(framesView, Play.LAYER_FG);
        framesView.getAnimation().play();
        framesView.getAnimation().setOnFinished(this);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void update() {}

    @Override
    public void handle(final ActionEvent ae) {
        final Layered layered = App.game.getStack().topPane();
        framesView.setDisable(true);
        layered.removeAnimation(framesView.getAnimation());
        layered.removeAnimation(movementAnimation);
    }

}
