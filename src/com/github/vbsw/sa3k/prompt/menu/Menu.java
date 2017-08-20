
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt.menu;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.engine.Layered.Action;
import com.github.vbsw.sa3k.prompt.controls.Controls;
import com.github.vbsw.sa3k.prompt.credits.Credits;
import com.github.vbsw.sa3k.prompt.play.Play;

/**
 * 
 * @author Vitali Baumtrok
 */
public class Menu implements Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    private final Text     text     = new Text();

    private final Selector selector = new Selector();

    private boolean        enabled  = true;

    private Menu() {}

    /*
     * *****************************
     * package
     * *****************************
     */

    static final int SECTION_PLAY     = 0;
    static final int SECTION_CONTROLS = 1;
    static final int SECTION_CREDITS  = 2;
    static final int SECTION_QUIT     = 3;

    void select(final int section) {
        // no clean up
        // enabled = false;
        // selector.destroy();
        // text.destroy();

        // instead of clean up, disable Layered
        App.game.getStack().topPane().setEnabled(false);
        App.game.getStack().topPane().setVisible(false);

        // set selected section of game
        switch(section) {
            case SECTION_PLAY:
                // set Layered for game play
                App.game.getStack().pushPane(Play.LAYERS_NUMBER);
                App.game.getStack().topPane().addAction(Play.getInstance());
                break;
            case SECTION_CONTROLS:
                // set Layered for game controls
                App.game.getStack().pushPane(Controls.LAYERS_NUMBER);
                App.game.getStack().topPane().addAction(Controls.getInstance());
                break;
            case SECTION_CREDITS:
                // set Layered for credits
                App.game.getStack().pushPane(Credits.LAYERS_NUMBER);
                App.game.getStack().topPane().addAction(Credits.getInstance());
                break;
            case SECTION_QUIT:
            default:
                App.game.quit();
        }
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public static final int  LAYERS_NUMBER   = 2;

    public static final int  LAYER_BG        = 0;
    public static final int  LAYER_TEXT      = 1;

    public static final int  TOP_MARGIN      = 50;
    public static final int  TOP_MARGIN_MENU = 180;
    public static final int  MIDDLE_GAP      = 10;

    public static final Menu instance        = new Menu();

    @Override
    public void init() {
        final Layered pane = App.game.getStack().topPane();

        pane.addAction(selector);
        pane.addNode(text.textTitle, Menu.LAYER_TEXT);
        pane.addNode(text.textVersion, Menu.LAYER_TEXT);
        pane.addNode(text.textPlay, Menu.LAYER_TEXT);
        pane.addNode(text.textControls, Menu.LAYER_TEXT);
        pane.addNode(text.textCredits, Menu.LAYER_TEXT);
        pane.addNode(text.textQuit, Menu.LAYER_TEXT);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void update() {}

}
