
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.engine;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

/**
 * Class to manage <tt>Layered</tt> in a stack.<br>
 * <br>
 * Every <tt>Layered</tt> that is added to the stack,
 * can also be removed. How the <tt>Layered</tt> behaves
 * depends on programmer not on stack. The <tt>Layered</tt> should
 * be enabled, disabled and destroyed by the programer.
 * 
 * @author Vitali Baumtrok
 */
public class Stack extends StackPane {

    /*
     * *****************************
     * private
     * *****************************
     */

    /**
     * Indicates whether a background node is set.
     */
    private boolean bgIsSet = false;

    /*
     * *****************************
     * package
     * *****************************
     */

    /**
     * The constructor.<br>
     * <br>
     * This class is used to stack <tt>Layered</tt> that are
     * layered panes.<br>
     * <br>
     * Every <tt>Layered</tt> that is added to the stack,
     * can also be removed. How the <tt>Layered</tt> behaves
     * depends on programmer not on stack. The <tt>Layered</tt> should
     * be enabled, disabled and destroyed by the programer.
     * 
     * @param width
     *            The width of the view.
     * @param height
     *            The height of the view.
     */
    Stack(final double width, final double height) {
        setMinSize(width, height);
        setMaxSize(width, height);
    }

    /**
     * Updates all stacked panes. A pane isn't updated if
     * it is disabled.
     * 
     * @param currentNanoSeconds
     *            The current time in nano seconds.
     */
    void update(final long currentNanoSeconds) {
        final ArrayList<Node> children = new ArrayList<>(getChildren());
        for(Node node : children) {
            if(!node.isDisabled()) {
                if(node instanceof Layered) {
                    final Layered layered = (Layered)node;
                    layered.update(currentNanoSeconds);
                }
            }
        }
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    /**
     * Creates a new <tt>Layered</tt> and pushes it on the top
     * of this <tt>Stack</tt>.
     * 
     * @param layerNumber
     *            The number of layers of the new <tt>Layered</tt>.
     */
    public void pushPane(final int layerNumber) {
        final Layered layered = new Layered(getMinWidth(), getMinHeight(), layerNumber);
        final Input input = layered.getInput();

        getScene().addEventHandler(KeyEvent.KEY_PRESSED, input.getKeyPressedHandler());
        getScene().addEventHandler(KeyEvent.KEY_RELEASED, input.getKeyReleasedHandler());
        // TODO add mouse listeners

        getChildren().add(layered);
    }

    /**
     * Returns the top <tt>Layered</tt> from this <tt>Stack</tt>.
     * 
     * @return The top <tt>Layered</tt> from this <tt>Stack</tt>.
     */
    public Layered topPane() {
        final ObservableList<Node> panes = getChildren();
        final Node topNode = panes.get(panes.size()-1);

        return (Layered)topNode;
    }

    /**
     * Removes the top <tt>Layered</tt> from this <tt>Stack</tt> and
     * returns it.
     * 
     * @return The top <tt>Layered</tt> from this <tt>Stack</tt>.
     */
    public Layered popPane() {
        final ObservableList<Node> panes = getChildren();
        final Node topNode = panes.remove(panes.size()-1);
        final Layered layered = (Layered)topNode;

        getScene().removeEventHandler(KeyEvent.KEY_PRESSED, layered.getInput().getKeyPressedHandler());
        getScene().removeEventHandler(KeyEvent.KEY_RELEASED, layered.getInput().getKeyReleasedHandler());
        // TODO remove mouse listeners

        return layered;
    }

    /**
     * Sets a node as the background for this <tt>Stack</tt>.
     * 
     * @param node
     *            The new backgound.
     */
    public void setBg(final Node node) {
        if(bgIsSet) {
            final ObservableList<Node> nodes = getChildren();
            if(node==null)
                nodes.remove(0);
            else
                nodes.set(0, node);
        }
        else {
            if(node!=null) {
                final ObservableList<Node> nodes = getChildren();
                nodes.add(0, node);
            }
        }
    }

}
