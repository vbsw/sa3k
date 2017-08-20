
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt.menu;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.prompt.Font;

import javafx.scene.paint.Color;

/**
 * 
 * @author Vitali Baumtrok
 */
class Text {

    /*
     * *****************************
     * package
     * *****************************
     */

    final com.github.vbsw.sa3k.prompt.Text textTitle    = new com.github.vbsw.sa3k.prompt.Text(App.GAME_TITLE, Font.TITLE);
    final com.github.vbsw.sa3k.prompt.Text textVersion  = new com.github.vbsw.sa3k.prompt.Text("Version "+App.GAME_VERSION, Font.COMMENT);

    final com.github.vbsw.sa3k.prompt.Text textPlay     = new com.github.vbsw.sa3k.prompt.Text("Play", Font.MENU);
    final com.github.vbsw.sa3k.prompt.Text textControls = new com.github.vbsw.sa3k.prompt.Text("Controls", Font.MENU);
    final com.github.vbsw.sa3k.prompt.Text textCredits  = new com.github.vbsw.sa3k.prompt.Text("Credits", Font.MENU);
    final com.github.vbsw.sa3k.prompt.Text textQuit     = new com.github.vbsw.sa3k.prompt.Text("Quit", Font.MENU);

    Text() {
        final double menuX = (App.WIDTH+Menu.MIDDLE_GAP)/2;

        textTitle.set((App.WIDTH-textTitle.getWidth())/2, Menu.TOP_MARGIN, Color.WHITE);
        textVersion.set((App.WIDTH-textVersion.getWidth())/2, textTitle.getTranslateY()+10, Color.WHITE);
        textPlay.set(menuX, Menu.TOP_MARGIN_MENU, Color.WHITE);
        textControls.set(menuX, textPlay.getTranslateY()+10, Color.WHITE);
        textCredits.set(menuX, textControls.getTranslateY()+10, Color.WHITE);
        textQuit.set(menuX, textCredits.getTranslateY()+10, Color.WHITE);
    }

    void destroy() {
        textTitle.setDisable(true);
        textVersion.setDisable(true);
        textPlay.setDisable(true);
        textControls.setDisable(true);
        textCredits.setDisable(true);
        textQuit.setDisable(true);
    }

}
