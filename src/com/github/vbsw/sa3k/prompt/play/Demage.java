
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt.play;

import com.github.vbsw.sa3k.npc.BugShot;
import com.github.vbsw.sa3k.npc.SkullShot;
import com.github.vbsw.sa3k.npc.SwordShot;
import com.github.vbsw.sa3k.pc.PlayerShot;

import javafx.scene.Node;

/**
 * Contains demage specifications.
 * 
 * @author Vitali Baumtrok
 */
public class Demage {

    /**
     * Returns the demage dealt by a node.
     * 
     * @param node
     *            The node from which the demage comes.
     * @return The demage of the given node.
     */
    public static int get(final Node node) {
        if(node instanceof PlayerShot)
            return ((PlayerShot)node).getDemage();
        else if(node instanceof SwordShot)
            return 3;
        else if(node instanceof SkullShot)
            return 2;
        else if(node instanceof BugShot)
            return 2;
        // else if(node instanceof EnergyShot)
        // return 2;
        else
            return 1;
    }
}
