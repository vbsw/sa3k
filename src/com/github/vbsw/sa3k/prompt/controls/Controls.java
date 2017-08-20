
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt.controls;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.engine.Layered.Action;

/**
 * 
 * @author Vitali Baumtrok
 */
public class Controls implements Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    private static Controls    instance     = null;

    private final ControlsPane controlsPane = new ControlsPane();
    private final BackListener backListener = new BackListener();

    private boolean            enabled      = true;

    /*
     * *****************************
     * package
     * *****************************
     */

    void backToMenu() {
        instance = null;
        enabled = false;
        App.game.getStack().popPane().destory();

        // enable menu
        App.game.getStack().topPane().setVisible(true);
        App.game.getStack().topPane().setEnabled(true);
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public static final int LAYERS_NUMBER = 1;

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();

        layered.addNode(controlsPane, 0);
        layered.getInput().addKeyListener(backListener);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void update() {}

    public static Controls getInstance() {
        if(instance==null)
            instance = new Controls();

        return instance;
    }

}
