
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt.play;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.DataBox;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.prompt.Font;
import com.github.vbsw.sa3k.prompt.Text;
import com.github.vbsw.sa3k.prompt.menu.Menu;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class StatusText extends Group implements Layered.Action {

    private final String         text;
    private boolean              enabled            = true;
    private Duration             timeToEndShowing;

    public static final Duration SHOW_TEXT_DURATION = Duration.seconds(8);

    public StatusText(final String text) {
        this.text = text;
    }

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();
        final Text gameOverText = new Text(text, Font.TITLE);
        final Text scoreText = new Text("score: "+DataBox.score, Font.COMMENT_BOLD);

        timeToEndShowing = layered.getTime().add(SHOW_TEXT_DURATION);
        gameOverText.set((App.WIDTH-gameOverText.getWidth())/2, Menu.TOP_MARGIN, Color.WHITE);
        scoreText.set((App.WIDTH-scoreText.getWidth())/2, gameOverText.getTranslateY()+10, Color.WHITE);
        getChildren().addAll(gameOverText, scoreText);
        layered.addNode(this, Play.LAYER_TEXT);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void update() {
        if(timeToEndShowing.lessThanOrEqualTo(App.game.getStack().topPane().getTime())&&enabled) {
            destroy();
            Play.getInstance().statusEnded();
        }
    }

    public void destroy() {
        enabled = false;
        setDisable(true);
    }

}
