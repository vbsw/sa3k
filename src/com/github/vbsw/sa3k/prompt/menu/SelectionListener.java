
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt.menu;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.Audio;
import com.github.vbsw.sa3k.engine.Input;
import com.github.vbsw.sa3k.prompt.GameCtrl;

import javafx.scene.input.KeyCode;

/**
 * 
 * @author Vitali Baumtrok
 */
class SelectionListener implements Input.Key.Listener {

    /*
     * *****************************
     * private
     * *****************************
     */

    private final Selector selector;

    private boolean        enabled         = true;
    private int            sectionSelected = Menu.SECTION_PLAY;

    /*
     * *****************************
     * package
     * *****************************
     */

    SelectionListener(final Selector selector) {
        this.selector = selector;
    }

    void destroy() {
        enabled = false;
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void keyPressed(final KeyCode keyCode) {
        // move selector up
        if(GameCtrl.isUpPressed(keyCode)) {
            sectionSelected = (sectionSelected+4-1)%4;
            selector.setIconOffsetY(sectionSelected);
            Audio.Fx.moveSelector.play();
        }
        // move selector down
        else if(GameCtrl.isDownPressed(keyCode)) {
            sectionSelected = (sectionSelected+4+1)%4;
            selector.setIconOffsetY(sectionSelected);
            Audio.Fx.moveSelector.play();
        }
    }

    @Override
    public void keyPressedCurrently(final KeyCode keyCode) {
        // select
        if(GameCtrl.isSelectionPressed(keyCode)) {
            Menu.instance.select(sectionSelected);
        }
        // quit game
        else if(GameCtrl.isBackPressed(keyCode)) {
            App.game.quit();
        }
    }

    @Override
    public void keyReleased(final KeyCode keyCode) {}

}
