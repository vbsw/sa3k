
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.pc;

import static com.github.vbsw.sa3k.pc.PlayerShip.glowDuration;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.engine.Layered;

import javafx.util.Duration;

/**
 * 
 * @author Vitali Baumtrok
 */
public class ShipInitializer implements Layered.Action {

    private final Layered.ActionsQueue actionsQueue = new Layered.ActionsQueue(18);
    private final PlayerShip           ship;

    private boolean                    enabled      = true;

    public ShipInitializer(final PlayerShip playerShip) {
        ship = playerShip;
    }

    @Override
    public void init() {
        actionsQueue.reset(App.game.getStack().topPane().getTime());
        actionsQueue.add(new GlowSetter(), glowDuration);
        actionsQueue.add(new GlowSetter(), glowDuration.add(glowDuration));
        actionsQueue.add(new GlowSetter(), glowDuration.add(glowDuration));
        actionsQueue.add(new GlowSetter(), glowDuration.add(glowDuration));
        actionsQueue.add(new GlowSetter(), glowDuration.add(glowDuration));
        actionsQueue.add(new GlowSetter(), glowDuration.add(glowDuration));
        actionsQueue.add(new GlowSetter(), glowDuration.add(glowDuration));
        actionsQueue.add(new GlowSetter(), glowDuration.add(glowDuration));
        actionsQueue.add(new ShipEnabler(), Duration.millis(0));
        actionsQueue.add(new ShotEnabler(), Duration.seconds(5));
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void update() {
        final Layered layered = App.game.getStack().topPane();
        actionsQueue.update(layered.getTime(), layered);
        if(actionsQueue.isEnded())
            enabled = false;
    }

    /**
     * 
     * @author Vitali Baumtrok
     */
    private class GlowSetter implements Layered.Action {

        @Override
        public void init() {
            ship.glow();
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void update() {}

    }

    /**
     * 
     * @author Vitali Baumtrok
     */
    private class ShipEnabler implements Layered.Action {

        @Override
        public void init() {
            ship.enableMovement();
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void update() {}

    }

    /**
     * 
     * @author Vitali Baumtrok
     */
    private class ShotEnabler implements Layered.Action {

        @Override
        public void init() {
            ship.setShootingEnabled(true);
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void update() {}

    }

}
