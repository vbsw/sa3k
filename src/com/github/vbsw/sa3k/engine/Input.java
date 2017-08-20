
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.engine;

import java.util.ArrayList;
import java.util.Iterator;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;

/**
 * Class to handle key (and mouse [not implemented]) input.
 * 
 * @author Vitali Baumtrok
 */
public class Input {

    /*
     * *****************************
     * private
     * *****************************
     */

    /**
     * Listeners which are notified when input from the keyboard
     * has occured.
     */
    private final ArrayList<Key.Listener>   keyListeners       = new ArrayList<>();
    /**
     * Listeners which are notified when input from the mouse
     * has occured.
     */
    private final ArrayList<Mouse.Listener> mouseListeners     = new ArrayList<>();

    /**
     * Holds the keys that are down.
     */
    private final ArrayList<KeyCode>        pressedKeys        = new ArrayList<>();
    /**
     * Holds the keys that are down.
     */
    private final ArrayList<KeyCode>        pressedKeysMemo    = new ArrayList<>();

    /**
     * The handler for JavaFX to handle pressed keys.
     */
    private final KeyPressedHandler         keyPressedHandler  = new KeyPressedHandler();
    /**
     * The handler for JavaFX to handle released keys.
     */
    private final KeyReleasedHandler        keyReleasedHandler = new KeyReleasedHandler();

    /**
     * A flag for enabling the input processing.
     */
    private boolean                         enabled            = true;

    /**
     * A guard to prevent deleting listeners while notifying them,
     * i.e. when method notifyListenersOf* is running, this variable
     * is <tt>true</tt>, otherwise <tt>false</tt>.
     */
    private boolean                         notifyingListeners = false;

    /**
     * Indicates whether to delete listeners after notification.
     */
    private boolean                         clearListeners     = false;

    /**
     * Notifies enabled listeners of a pressed key. Disabled
     * listeners are removed.
     * 
     * @param keyCode
     *            The key code of the key that has been pressed.
     */
    private void notifyListenersOfPressedKey(final KeyCode keyCode) {
        notifyingListeners = true;
        final Iterator<Key.Listener> iter = keyListeners.iterator();

        while(iter.hasNext()) {
            final Key.Listener l = iter.next();

            if(l.isEnabled())
                l.keyPressed(keyCode);
            else
                iter.remove();
        }
        notifyingListeners = false;
        if(clearListeners) {
            clearListeners = false;
            clear();
        }
    }

    /**
     * Notifies enabled listeners of a currently pressed key.
     * Disabled listeners are removed.
     * 
     * @param keyCode
     *            The key code of the key that has been pressed
     *            currently.
     */
    private void notifyListenersOfCurrentlyPressedKey(final KeyCode keyCode) {
        notifyingListeners = true;
        final Iterator<Key.Listener> iter = keyListeners.iterator();

        while(iter.hasNext()) {
            final Key.Listener l = iter.next();

            if(l.isEnabled())
                l.keyPressedCurrently(keyCode);
            else
                iter.remove();
        }
        notifyingListeners = false;
        if(clearListeners) {
            clearListeners = false;
            clear();
        }
    }

    /**
     * Notifies enabled listeners of a released key. Disabled
     * listeners are removed.
     * 
     * @param keyCode
     *            The key code of the key that has been released.
     */
    private void notifyListenersOfReleasedKey(final KeyCode keyCode) {
        notifyingListeners = true;
        final Iterator<Key.Listener> iter = keyListeners.iterator();

        while(iter.hasNext()) {
            final Key.Listener l = iter.next();

            if(l.isEnabled())
                l.keyReleased(keyCode);
            else
                iter.remove();
        }
        notifyingListeners = false;
        if(clearListeners) {
            clearListeners = false;
            clear();
        }
    }

    // TODO implement mouse input notification

    /**
     * The handler for JavaFX to handle pressed keys.
     * 
     * @author Vitali Baumtrok
     */
    private class KeyPressedHandler implements EventHandler<KeyEvent> {

        /*
         * *****************************
         * public
         * *****************************
         */

        /**
         * Notifies the listeners of a pressed key
         * if <tt>Input</tt> is enabled.
         * 
         * @param event
         *            The key event.
         */
        @Override
        public void handle(final KeyEvent event) {
            final KeyCode keyCode = event.getCode();

            if(pressedKeys.contains(keyCode)) {
                if(enabled)
                    notifyListenersOfPressedKey(keyCode);
            }
            else {
                if(enabled) {
                    notifyListenersOfPressedKey(keyCode);
                    notifyListenersOfCurrentlyPressedKey(keyCode);
                }
                pressedKeys.add(keyCode);
            }
        }

    }

    /**
     * The handler for JavaFX to handle released keys.
     * 
     * @author Vitali Baumtrok
     */
    private class KeyReleasedHandler implements EventHandler<KeyEvent> {

        /*
         * *****************************
         * public
         * *****************************
         */

        /**
         * Notifies the listeners of a released key
         * if <tt>Input</tt> is enabled.
         * 
         * @param event
         *            The key event.
         */
        @Override
        public void handle(final KeyEvent event) {
            final KeyCode keyCode = event.getCode();

            if(enabled)
                notifyListenersOfReleasedKey(keyCode);
            pressedKeys.remove(keyCode);
        }

    }

    /*
     * *****************************
     * package
     * *****************************
     */

    /**
     * Enables or disables the notification of the input listeners.<br>
     * <br>
     * CAUTION: This method shouldn't be called more than one time
     * simultaneously.
     * 
     * @param b
     *            If <tt>true</tt> the notification of the
     *            input listeners are enabled, otherwise disabled.
     */
    void setEnabled(final boolean b) {
        if(enabled!=b) {
            // enable input
            if(b) {
                // notify of newly released keys
                for(KeyCode keyCode : pressedKeysMemo) {
                    if(!pressedKeys.contains(keyCode))
                        notifyListenersOfReleasedKey(keyCode);
                }
                // notify of newly pressed keys
                for(KeyCode keyCode : pressedKeys) {
                    if(!pressedKeysMemo.contains(keyCode))
                        notifyListenersOfPressedKey(keyCode);
                }
                pressedKeysMemo.clear();
            }
            // disable input
            else {
                // memorize pressed keys
                pressedKeysMemo.addAll(pressedKeys);
            }
        }
        enabled = b;
    }

    /**
     * Returns a JavaFX handler to handle pressed keys.
     * 
     * @return The JavaFX handler to handle pressed keys.
     */
    EventHandler<KeyEvent> getKeyPressedHandler() {
        return keyPressedHandler;
    }

    /**
     * Returns a JavaFX handler to handle released keys.
     * 
     * @return The JavaFX handler to handle released keys.
     */
    EventHandler<KeyEvent> getKeyReleasedHandler() {
        return keyReleasedHandler;
    }

    void clear() {
        if(notifyingListeners) {
            clearListeners = true;
        }
        else {
            keyListeners.clear();
            mouseListeners.clear();
        }
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    /**
     * Adds a listener to listen to key input.
     * 
     * @param l
     *            The key listener to be added.
     */
    public void addKeyListener(Key.Listener l) {
        keyListeners.add(l);
    }

    /**
     * Adds a listener to listen to mouse input.<br>
     * MOUSE HANDLING IS NOT IMPLEMENTED.
     * 
     * @param l
     *            The mouse listener to be added.
     */
    public void addMouseListener(Mouse.Listener l) {
        mouseListeners.add(l);
    }

    /**
     * Removes a listener which listens for key input.
     * 
     * @param l
     *            The listener to be removed.
     */
    public void removeKeyListener(Key.Listener l) {
        keyListeners.remove(l);
    }

    /**
     * Removes a listener which listens for mouse input.<br>
     * MOUSE HANDLING IS NOT IMPLEMENTED.
     * 
     * @param l
     *            The listener to be removed.
     */
    public void removeMouseListener(Mouse.Listener l) {
        mouseListeners.remove(l);
    }

    /**
     * Contains keyboard related classes.
     * 
     * @author Vitali Baumtrok
     */
    public static class Key {

        /**
         * A Listener for key input.
         * 
         * @author Vitali Baumtrok
         */
        public static interface Listener {

            /**
             * Indicates if the listener is to be notified. If this
             * method returns <tt>false</tt> this listener is removed.
             * 
             * @return <tt>true</tt> if the listener is to be notified
             *         when input occures, <tt>false</tt> otherwise.
             */
            public boolean isEnabled();

            /**
             * Is called when a key has been pressed.
             * 
             * @param keyCode
             *            The code of the key that has
             *            been pressed.
             */
            public void keyPressed(KeyCode keyCode);

            /**
             * Is called when a key has been pressed currently.
             * 
             * @param keyCode
             *            The code of the key that has
             *            been pressed currently.
             */
            public void keyPressedCurrently(KeyCode keyCode);

            /**
             * Is called when a key has been released.
             * 
             * @param keyCode
             *            The code of the key that has
             *            been released.
             */
            public void keyReleased(KeyCode keyCode);

        }

    }

    /**
     * Contains mouse related classes.
     * 
     * @author Vitali Baumtrok
     */
    public static class Mouse {

        /**
         * A Listener for mouse input.<br>
         * MOUSE HANDLING IS NOT IMPLEMENTED.
         * 
         * @author Vitali Baumtrok
         */
        public static interface Listener {

            /**
             * Indicates if the listener is to be notified. If this
             * method returns <tt>false</tt> this listener is removed.
             * 
             * @return <tt>true</tt> if the listener is to be notified
             *         when input occures, <tt>false</tt> otherwise.
             */
            public boolean isEnabled();

            /**
             * Is called when a mouse button has been pressed.
             * 
             * @param button
             *            The mouse button that has been pressed.
             * @param x
             *            The x coordinate of the mouse.
             * @param y
             *            The y coordinate of the mouse.
             */
            public void buttonPressed(MouseButton button, double x, double y);

            /**
             * Is called when a mouse button has been pressed currently.
             * 
             * @param button
             *            The mouse button that has been pressed currently.
             * @param x
             *            The x coordinate of the mouse.
             * @param y
             *            The y coordinate of the mouse.
             */
            public void buttonPressedCurrently(MouseButton button, double x, double y);

            /**
             * Is called when a mouse button has been released.
             * 
             * @param button
             *            The mouse button that has been released.
             * @param x
             *            The x coordinate of the mouse.
             * @param y
             *            The y coordinate of the mouse.
             */
            public void buttonReleased(MouseButton button, double x, double y);

            /**
             * Is called when the mouse has been moved.
             * 
             * @param dX
             *            The difference of the x position to the previous
             *            position.
             * @param dY
             *            The difference of the y position to the previous
             *            position.
             */
            public void mouseMoved(double dX, double dY);

            /**
             * Is called when the mouse wheel has been moved.
             * 
             * @param range
             *            // TODO explanation for range
             */
            public void wheelMoved(int range);

        }

    }

}
