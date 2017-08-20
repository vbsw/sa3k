
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.pc;

import java.util.ArrayList;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.prompt.play.Play;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * 
 * @author Vitali Baumtrok
 */
class PlayerLives implements Layered.Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    private static final double   margin            = 5;
    private static final double   barsMargin        = 3;
    private static final double   barHeight         = 7;

    private final ArrayList<Node> bars              = new ArrayList<>();
    private double                marginAccumulated = margin;
    private int                   lives             = 0;

    /*
     * *****************************
     * package
     * *****************************
     */

    void add(final int lives) {
        this.lives += lives;
        for(int i = 0; i<lives; i++) {
            final double startX = App.WIDTH-marginAccumulated;
            final double startY = App.HEIGHT-margin-barHeight;
            final double endX = startX;
            final double endY = startY+barHeight;
            final Line bar = new Line(startX, startY, endX, endY);
            marginAccumulated += barsMargin;

            bar.setStroke(new Color(0, 1, 0, 1));
            bars.add(bar);
            App.game.getStack().topPane().addNode(bar, Play.LAYER_PLAYER_FG);
        }
    }

    void subtract(final int lives) {
        final int livesLost = this.lives-lives<0 ? this.lives : lives;
        this.lives -= livesLost;
        marginAccumulated -= (barsMargin*livesLost);

        // removing bars
        for(int i = this.lives+livesLost-1; i>=this.lives; i--)
            bars.remove(i).setDisable(true);
    }

    int get() {
        return lives;
    }

    public void destroy() {
        subtract(lives*2); // remove all for sure
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    @Override
    public void init() {
        add(5);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void update() {}

}
