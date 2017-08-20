
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt;

import javafx.scene.text.Text;

/**
 * 
 * @author Vitali Baumtrok
 */
public class Font {

    /*
     * *****************************
     * private
     * *****************************
     */

    private final javafx.scene.text.Font font;
    private final double                 height;

    /*
     * *****************************
     * public
     * *****************************
     */

    public static final Font             TITLE        = new Font("Arial bold", 30.0);
    public static final Font             MENU         = new Font("Arial bold", 20.0);

    public static final Font             COMMENT      = new Font("Arial Regular", 15.0);
    public static final Font             COMMENT_BOLD = new Font("Arial Bold", 15.0);
    public static final Font             SCORE        = new Font("monospace", 8.0);

    public Font(final String name, final double size) {
        final Text text = new Text("QWqglpFf?");
        font = new javafx.scene.text.Font(name, size);
        text.setFont(font);
        height = text.getBoundsInLocal().getHeight();
    }

    public javafx.scene.text.Font getJfxFont() {
        return font;
    }

    public double getHeight() {
        return height;
    }

}
