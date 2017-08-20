
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.engine;

import javafx.geometry.Bounds;
import javafx.scene.Node;

/**
 * Class for collision detection.<br>
 * <br>
 * Example:
 * 
 * <code><pre>
 * public static final int GROUP_ENEMY_BULLET = 0;
 * public static final int GROUP_PLAYER_SHIP  = 1;
 * 
 * public void setUpCollisionDetection(final Game game) {
 *   final Collision collision = game.getStack().topPane().getCollision();
 *   collision.addCollisionMapping(GROUP_ENEMY_BULLET, GROUP_PLAYER_SHIP);
 * }
 * 
 * public void addBulletDetection(final Game game, final Detection d) {
 *   final Collision collision = game.getStack().topPane().getCollision();
 *   collision.addDetection(GROUP_ENEMY_BULLET, d);
 * }
 * 
 * public void addPlayerDetection(final Game game, final Detection d) {
 *   final Collision collision = game.getStack().topPane().getCollision();
 *   collision.addDetection(GROUP_PLAYER_SHIP, d);
 * }
 * </pre></code>
 * 
 * @author Vitali Baumtrok
 */
public class Collision {

    /*
     * *****************************
     * private
     * *****************************
     */

    /**
     * The <tt>Detection</tt>s, i.e. the hit boxes,
     * organized in groups.
     */
    private final Detection[][] groups;

    /**
     * The actual size of a group. The size of a group is dynamic.
     */
    private final int[]         groupsSize;

    /**
     * The mapping what groups are to check for a collision.
     */
    private final int[]         collisionMap;

    /**
     * A guard to prevent deleting collisions and collision maps
     * while update, i.e. when method update is running this variable
     * is <tt>true</tt>, otherwise <tt>false</tt>.
     */
    private boolean             updating       = false;

    /**
     * Indicates whether to delete collisions and collision maps
     * after update.
     */
    private boolean             clearCollision = false;

    /**
     * Removes a detection from a detection group.
     * 
     * @param group
     *            The group from which the <tt>Detection</tt> is to remove.
     * @param i
     *            The index of the <tt>Detection</tt> wich is to be removed
     *            in the given group.
     * @param group1Index
     */
    private void remove(final Detection[] group, final int i, final int groupIndex) {
        final int lastIndex = --groupsSize[groupIndex];
        group[i] = group[lastIndex];
        group[lastIndex] = null;
    }

    /*
     * *****************************
     * package
     * *****************************
     */

    /**
     * The constructor. Class for collision detection.
     * 
     * @param groupsCount
     *            The number of <tt>Detection</tt> groups.
     */
    Collision(final int groupsCount) {
        final int mappings = groupsCount-1; // -1, because two groups need one mapping not two.
        final int maxGroups = mappings*(mappings+1)/2;

        groups = new Detection[groupsCount][];
        groupsSize = new int[groupsCount]; // initialized to zero
        for(int i = 0; i<groups.length; i++)
            groups[i] = new Detection[3];
        collisionMap = new int[maxGroups*2]; // a mapping consists of two numbers
        for(int i = 0; i<collisionMap.length; i++)
            collisionMap[i] = -1; // -1 means no mapping
    }

    /**
     * Checks for collisions. The collision detection are done
     * only for groups for which a collision mapping has been set.
     */
    void update() {
        updating = true;
        for(int groupIndex = 0; groupIndex<collisionMap.length&&collisionMap[groupIndex]>=0; groupIndex += 2) {
            final int group1Index = collisionMap[groupIndex];
            final int group2Index = collisionMap[groupIndex+1];
            final Detection[] group1 = groups[group1Index];
            final Detection[] group2 = groups[group2Index];
            int group1Size = groupsSize[group1Index];
            int group2Size = groupsSize[group2Index];

            for(int i = 0; i<group1Size; i++) {
                final Detection detection1 = group1[i];
                if(detection1.enabled) {
                    for(int j = 0; j<group2Size; j++) {
                        final Detection detection2 = group2[j];
                        if(detection2.enabled) {
                            if(detection1.detect(detection2)) {
                                detection1.beenHit(detection2.topBoundsNode);
                                detection2.beenHit(detection1.topBoundsNode);
                            }
                        }
                        else {
                            remove(group2, j, group2Index);
                            group2Size--;
                            j--;
                        }
                    }
                }
                else {
                    remove(group1, i, group1Index);
                    group1Size--;
                    i--;
                }
            }
        }
        updating = false;
        if(clearCollision) {
            clearCollision = false;
            clear();
        }
    }

    /**
     * Deletes all groups and collision maps.
     */
    void clear() {
        if(updating) {
            clearCollision = true;
        }
        else {
            for(int i = 0; i<groups.length; i++)
                for(int j = 0; j<groups[i].length; j++)
                    groups[i][j] = null;
            for(int i = 0; i<groupsSize.length; i++)
                groupsSize[i] = 0;
            for(int i = 0; i<collisionMap.length; i++)
                collisionMap[i] = -1; // -1 means no mapping
        }
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    /**
     * Adds a detection to a detection group. The groupIndex is the
     * group id to which the item (i.e. node) belongs to.<br>
     * <br>
     * To remove a detection set it disabled.
     * 
     * @param groupIndex
     *            The index of the group to which
     *            the <tt>Detection</tt> is to be added and it
     *            is the one to which the item belongs to.
     * @param detection
     *            The <tt>Detection</tt> for the item.
     */
    public void addDetection(final int groupIndex, final Detection detection) {
        Detection[] detectionGroup = groups[groupIndex];
        final int layerSize = groupsSize[groupIndex];
        // extend group, if needed
        if(layerSize==detectionGroup.length) {
            final Detection[] newDetectionLayer = new Detection[layerSize*2+1];
            System.arraycopy(detectionGroup, 0, newDetectionLayer, 0, layerSize);
            detectionGroup = newDetectionLayer;
            groups[groupIndex] = detectionGroup;
        }
        // add detection
        detectionGroup[layerSize] = detection;
        groupsSize[groupIndex]++;
    }

    /**
     * Adds a collision mapping for groups. All nodes belonging to the
     * given groups, <tt>group1Index</tt> and <tt>group2Index</tt>,
     * will be checked for a collision while collision detection.
     * 
     * @param group1Index
     *            The index of the first group.
     * @param group2Index
     *            The index of the second group.
     */
    public void addCollisionMapping(final int group1Index, final int group2Index) {
        int index = 0;
        while(collisionMap[index]>=0&&(collisionMap[index]!=group1Index||collisionMap[index]!=group2Index)&&(collisionMap[index]!=group2Index||collisionMap[index]!=group1Index))
            index += 2;
        collisionMap[index] = group1Index;
        collisionMap[index+1] = group2Index;
    }

    /**
     * Removes a collision mapping for groups. All nodes belonging to the
     * given groups, <tt>group1Index</tt> and <tt>group2Index</tt>,
     * will be no more checked for a collision while collision detection.
     * 
     * @param group1Index
     *            The index of the first group.
     * @param group2Index
     *            The index of the second group.
     */
    public void removeCollisionMapping(final int group1Index, final int group2Index) {
        final int length = collisionMap.length;
        int index = 0;

        while((collisionMap[index]!=group1Index||collisionMap[index]!=group2Index)&&(collisionMap[index]!=group2Index||collisionMap[index]!=group1Index))
            index += 2;

        if(index<length-2)
            System.arraycopy(collisionMap, index+2, collisionMap, index, length-(index+2));
        else
            collisionMap[index] = collisionMap[index+1] = -1;
    }

    /**
     * This class is kind of a hit box. It holds bounds
     * within a collision can occur.
     * 
     * @author Vitali Baumtrok
     */
    public static abstract class Detection {

        /*
         * *****************************
         * private
         * *****************************
         */

        /**
         * The enclosing bound for the fine bounds.
         */
        private final Node   topBoundsNode;

        /**
         * The fine bounds. Are only checked for collision when the
         * enclosing bound has been checked for collision to true.
         */
        private final Node[] subBoundsNodes;

        /**
         * Flag for collision enabling. If <tt>true</tt> this <tt>Detection</tt> will
         * be checked for collision, otherwise not and is removed from the collision
         * detection.
         */
        private boolean      enabled = true;

        /**
         * Checks if there is a collision between this <tt>Detection</tt> and the given one.
         * 
         * @param oDetection
         *            Other <tt>Detection</tt> the collision is to be
         *            checked with.
         * @return <tt>true</tt> if collision occures, <tt>false</tt> otherwise.
         */
        private boolean detect(final Detection oDetection) {
            final Bounds oTopBounds = oDetection.topBoundsNode.getBoundsInParent();
            final Bounds topBounds = topBoundsNode.getBoundsInParent();
            if(oTopBounds.intersects(topBounds)) {
                if(oDetection.subBoundsNodes!=null&&oDetection.subBoundsNodes.length>0) {
                    if(subBoundsNodes!=null&&subBoundsNodes.length>0) {
                        for(Node oBoundsNode : oDetection.subBoundsNodes) {
                            final Bounds oBounds = oDetection.topBoundsNode.localToParent(oBoundsNode.getBoundsInParent());
                            for(Node boundsNode : subBoundsNodes) {
                                final Bounds bounds = topBoundsNode.localToParent(boundsNode.getBoundsInParent());
                                if(oBounds.intersects(bounds))
                                    return true;
                            }
                        }
                    }
                    else {
                        for(Node oBoundsNode : oDetection.subBoundsNodes) {
                            final Bounds oBounds = oDetection.topBoundsNode.localToParent(oBoundsNode.getBoundsInParent());
                            if(oBounds.intersects(topBounds))
                                return true;
                        }
                    }
                }
                else if(subBoundsNodes!=null&&subBoundsNodes.length>0) {
                    for(Node boundsNode : subBoundsNodes) {
                        final Bounds bounds = topBoundsNode.localToParent(boundsNode.getBoundsInParent());
                        if(bounds.intersects(oTopBounds))
                            return true;
                    }
                }
                else {
                    return true;
                }
            }

            return false;
        }

        /*
         * *****************************
         * public
         * *****************************
         */

        /**
         * The constructor.<br>
         * <br>
         * The <tt>Detection</tt> is kind of a hit box. It holds
         * bounds within a collision can occure.
         * 
         * @param topBounds
         *            The enclosing bound for the fine bounds.
         * @param subBounds
         *            The fine bounds. Are only checked for collision when the
         *            enclosing bound has been checked for collision to true.
         */
        public Detection(final Node topBounds, final Node... subBounds) {
            this.topBoundsNode = topBounds;
            this.subBoundsNodes = subBounds;
        }

        /**
         * Sets the checking for collision for this <tt>Detection</tt>. If
         * it is set to <tt>false</tt> the detection is removed from the
         * collision detection.
         * 
         * @param b
         *            If <tt>true</tt> collision will be checked, otherwise not
         *            and is removed from collision detection.
         */
        public void setEnabled(final boolean b) {
            enabled = b;
        }

        /**
         * The sub hitboxes of the detection.
         * 
         * @return The sub hitboxes.
         */
        public Node[] getSubHitboxes() {
            return subBoundsNodes;
        }

        /**
         * This method is called whenever a collision has occured.
         * 
         * @param byNode
         *            The <tt>Node</tt> with which the collision
         *            has occured.
         */
        public abstract void beenHit(final Node byNode);

    }

}
