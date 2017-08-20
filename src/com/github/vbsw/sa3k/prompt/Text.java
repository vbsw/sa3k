
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt;

import javafx.scene.paint.Color;

/**
 * 
 * @author Vitali Baumtrok
 */
public class Text extends javafx.scene.text.Text {

    private final double width;
    private final double height;

    public Text(final String s, final Font font) {
        super(s);
        setFont(font.getJfxFont());
        width = getBoundsInLocal().getWidth();
        height = font.getHeight();
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void set(final double x, final double y, final Color color) {
        setTranslateX(x);
        setTranslateY(y+height);
        setFill(color);
    }

}
