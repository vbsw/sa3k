
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt.play;

import com.github.vbsw.sa3k.engine.Input;
import com.github.vbsw.sa3k.prompt.GameCtrl;

import javafx.scene.input.KeyCode;

/**
 * 
 * @author Vitali Baumtrok
 */
class BackListener implements Input.Key.Listener {

    /*
     * *****************************
     * private
     * *****************************
     */

    private boolean enabled = true;

    /*
     * *****************************
     * package
     * *****************************
     */

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
    public void keyPressed(final KeyCode keyCode) {}

    @Override
    public void keyPressedCurrently(final KeyCode keyCode) {
        if(GameCtrl.isBackPressed(keyCode)) {
            Play.getInstance().backToMenu();
        }
    }

    @Override
    public void keyReleased(final KeyCode keyCode) {}

}
