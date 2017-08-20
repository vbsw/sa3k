
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.pc;

import com.github.vbsw.sa3k.engine.Input;

import javafx.scene.input.KeyCode;

/**
 * 
 * @author Vitali Baumtrok
 */
class ShipControlKeysListener implements Input.Key.Listener {

    /*
     * *****************************
     * private
     * *****************************
     */

    private final PlayerShip playerShip;

    private boolean          isUp_Arrow, isDown_Arrow, isRight_Arrow, isLeft_Arrow, isUp_Char, isDown_Char, isRight_Char, isLeft_Char;
    private boolean          enabled = true;

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

    public ShipControlKeysListener(final PlayerShip playerShip) {
        this.playerShip = playerShip;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void keyPressed(final KeyCode keyCode) {}

    @Override
    public void keyPressedCurrently(final KeyCode keyCode) {
        // arrow keys
        if(keyCode==KeyCode.UP) {
            isUp_Arrow = true;
            if(!(isDown_Arrow||isDown_Char)) // check if already moving
                playerShip.moveToTop();
        }
        else if(keyCode==KeyCode.DOWN) {
            isDown_Arrow = true;
            if(!(isUp_Arrow||isUp_Char)) // check if already moving
                playerShip.moveToBottom();
        }
        else if(keyCode==KeyCode.LEFT) {
            isLeft_Arrow = true;
            if(!(isRight_Arrow||isRight_Char)) // check if already moving
                playerShip.moveToLeft();
        }
        else if(keyCode==KeyCode.RIGHT) {
            isRight_Arrow = true;
            if(!(isLeft_Arrow||isLeft_Char)) // check if already moving
                playerShip.moveToRight();
        }
        // char keys
        else if(keyCode==KeyCode.I) {
            isUp_Char = true;
            if(!(isDown_Arrow||isDown_Char)) // check if already moving
                playerShip.moveToTop();
        }
        else if(keyCode==KeyCode.K) {
            isDown_Char = true;
            if(!(isUp_Arrow||isUp_Char)) // check if already moving
                playerShip.moveToBottom();
        }
        else if(keyCode==KeyCode.J) {
            isLeft_Char = true;
            if(!(isRight_Arrow||isRight_Char)) // check if already moving
                playerShip.moveToLeft();
        }
        else if(keyCode==KeyCode.L) {
            isRight_Char = true;
            if(!(isLeft_Arrow||isLeft_Char)) // check if already moving
                playerShip.moveToRight();
        }
    }

    @Override
    public void keyReleased(final KeyCode keyCode) {
        // arrow keys
        if(keyCode==KeyCode.UP&&isUp_Arrow) {
            isUp_Arrow = false;
            if(!isUp_Char)
                playerShip.moveToTopStop();
            if(isDown_Arrow||isDown_Char)
                playerShip.moveToBottom();
        }
        else if(keyCode==KeyCode.DOWN&&isDown_Arrow) {
            isDown_Arrow = false;
            if(!isDown_Char)
                playerShip.moveToBottomStop();
            if(isUp_Arrow||isUp_Char)
                playerShip.moveToTop();
        }
        else if(keyCode==KeyCode.LEFT&&isLeft_Arrow) {
            isLeft_Arrow = false;
            if(!isLeft_Char)
                playerShip.moveToLeftStop();
            if(isRight_Arrow||isRight_Char)
                playerShip.moveToRight();
        }
        else if(keyCode==KeyCode.RIGHT&&isRight_Arrow) {
            isRight_Arrow = false;
            if(!isRight_Char)
                playerShip.moveToRightStop();
            if(isLeft_Arrow||isLeft_Char)
                playerShip.moveToLeft();
        }
        // char keys
        else if(keyCode==KeyCode.I&&isUp_Char) {
            isUp_Char = false;
            if(!isUp_Arrow)
                playerShip.moveToTopStop();
            if(isDown_Arrow||isDown_Char)
                playerShip.moveToBottom();
        }
        else if(keyCode==KeyCode.K&&isDown_Char) {
            isDown_Char = false;
            if(!isDown_Arrow)
                playerShip.moveToBottomStop();
            if(isUp_Arrow||isUp_Char)
                playerShip.moveToTop();
        }
        else if(keyCode==KeyCode.J&&isLeft_Char) {
            isLeft_Char = false;
            if(!isLeft_Arrow)
                playerShip.moveToLeftStop();
            if(isRight_Arrow||isRight_Char)
                playerShip.moveToRight();
        }
        else if(keyCode==KeyCode.L&&isRight_Char) {
            isRight_Char = false;
            if(!isRight_Arrow)
                playerShip.moveToRightStop();
            if(isLeft_Arrow||isLeft_Char)
                playerShip.moveToLeft();
        }
    }

}
