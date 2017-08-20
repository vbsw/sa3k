
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt.play;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.DataBox;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.prompt.Font;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * 
 * @author Vitali Baumtrok
 */
public class Score extends Text implements Layered.Action {

    private static final double marginRight = 5;

    private boolean             enabled     = true;

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();

        setFill(Color.WHITE);
        setFont(Font.SCORE.getJfxFont());
        layered.addNode(this, Play.LAYER_TEXT);

        setTranslateY(getBoundsInParent().getHeight());
        update();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void update() {
        setText(Integer.toString(DataBox.score));

        final double offsetRight = getBoundsInParent().getWidth()+marginRight;

        setTranslateX(App.WIDTH-offsetRight);
    }

    public void destroy() {
        enabled = false;
        setDisable(true);
    }

}
