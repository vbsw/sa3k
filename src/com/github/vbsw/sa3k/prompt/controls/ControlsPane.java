
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt.controls;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.Graphic;
import com.github.vbsw.sa3k.prompt.Font;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * 
 * @author Vitali Baumtrok
 */
class ControlsPane extends VBox {

    /*
     * *****************************
     * private
     * *****************************
     */

    private Text createComment(final String s, final boolean bold) {
        final Text text = new Text(s);

        if(bold)
            text.setFont(Font.COMMENT_BOLD.getJfxFont());
        else
            text.setFont(Font.COMMENT.getJfxFont());
        text.setFill(Color.WHITE);

        return text;
    }

    private Rectangle createSeperator(final double height) {
        final Rectangle seperator = new Rectangle(0, 0, 10, height);
        seperator.setFill(Color.TRANSPARENT);
        return seperator;
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public ControlsPane() {
        super(4);
        setMinWidth(App.WIDTH);
        setMinHeight(App.HEIGHT);
        setPrefWidth(App.WIDTH);
        setPrefHeight(App.HEIGHT);
        setAlignment(Pos.TOP_CENTER);
        setTranslateY(70);
        setCacheHint(CacheHint.QUALITY);
        setCache(true);

        final ObservableList<Node> nodes = getChildren();
        final Text tTitle = new Text("Controls");

        tTitle.setFont(Font.TITLE.getJfxFont());
        tTitle.setFill(Color.WHITE);
        nodes.add(tTitle);
        nodes.add(createSeperator(Graphic.INFO_GAP/2));
        nodes.add(createComment("movement", true));
        nodes.add(createComment("j, i, k, l, left, up, right, down", false));
        // nodes.add(createSeperator(Graphic.INFO_GAP/3));
        // nodes.add(createComment("switch weapon", true));
        // nodes.add(createComment("s", false));
        // nodes.add(createSeperator(Graphic.INFO_GAP/3));
        // nodes.add(createComment("selection, super fire", true));
        // nodes.add(createComment("f, enter, space", false));
        nodes.add(createSeperator(Graphic.INFO_GAP/3));
        nodes.add(createComment("navigate forward, accept", true));
        nodes.add(createComment("f, enter, space", false));
        nodes.add(createSeperator(Graphic.INFO_GAP/3));
        nodes.add(createComment("back, end/quit game", true));
        nodes.add(createComment("q, back space, esc (windowed)", false));
        nodes.add(createSeperator(Graphic.INFO_GAP/3));
        nodes.add(createComment("switch to fullscreen/windowed", true));
        nodes.add(createComment("F11/esc", false));
    }

}
