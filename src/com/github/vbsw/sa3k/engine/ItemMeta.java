
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * A class to fetch image/item meta data from a file.
 * 
 * @author Vitali Baumtrok
 */
public class ItemMeta {

    /*
     * *****************************
     * private
     * *****************************
     */

    /**
     * The color of the hitboxes. It's transparent.<br>
     * Sometimes it's handy to change this color to make hitboxes visible.
     */
    private static final Color HITBOX_COLOR = Color.TRANSPARENT;

    /**
     * Reads a file encoded in UTF-8, that contains properties. Then returns
     * the properties.
     * 
     * @param path
     *            The absolute path to the properties file.
     * @return The properties.
     */
    private static Properties readProperties(final Path path) {
        final Properties properties = new Properties();
        final Charset charset = Charset.forName("UTF-8");
        try {
            final BufferedReader reader = Files.newBufferedReader(path, charset);
            properties.load(reader);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /*
     * *****************************
     * package
     * *****************************
     */

    final double         width;
    final double         height;
    final double[]       framesX;
    final double[]       framesY;
    final double[]       framesDelay;
    final Rectangle[][]  hitboxes;
    final double[]       explosionsX;
    final double[]       explosionsY;
    final double[]       explosionsDelay;

    /*
     * *****************************
     * public
     * *****************************
     */

    public static String FRAME_WIDTH      = "frame.width";
    public static String FRAME_HEIGHT     = "frame.height";
    public static String FRAMES_X         = "frames.x";
    public static String FRAMES_Y         = "frames.y";
    public static String FRAMES_DELAY     = "frames.delay";

    public static String HITBOX_WIDTH     = "hitbox.width";
    public static String HITBOX_HEIGHT    = "hitbox.height";
    public static String HITBOX_X         = "hitbox.x";
    public static String HITBOX_Y         = "hitbox.y";

    public static String EXPLOSIONS_X     = "explosions.x";
    public static String EXPLOSIONS_Y     = "explosions.y";
    public static String EXPLOSIONS_DELAY = "explosions.delay";

    /**
     * The constructor.<br>
     * <br>
     * This class fetches image/item meta data from a file. File must
     * be encoded in UTF-8.
     * 
     * @param path
     *            The absolute path to the properties file.
     */
    public ItemMeta(final Path path) {
        final Properties properties = readProperties(path);

        // read image frames
        final String frameWidthStr = properties.getProperty(FRAME_WIDTH, "0");
        final String frameHeightStr = properties.getProperty(FRAME_HEIGHT, "0");
        final String framesXStr = properties.getProperty(FRAMES_X, "");
        final String framesYStr = properties.getProperty(FRAMES_Y, "");
        final String framesDelayStr = properties.getProperty(FRAMES_DELAY, "");

        final String[] framesXStrArray = framesXStr.split(" +");
        final String[] framesYStrArray = framesYStr.split(" +");
        final String[] framesDelayStrArray = framesDelayStr.split(" +");

        width = Double.parseDouble(frameWidthStr);
        height = Double.parseDouble(frameHeightStr);

        framesX = new double[framesXStrArray.length];
        for(int i = 0; i<framesX.length; i++)
            framesX[i] = Double.parseDouble(framesXStrArray[i]);

        framesY = new double[framesYStrArray.length];
        for(int i = 0; i<framesY.length; i++)
            framesY[i] = Double.parseDouble(framesYStrArray[i]);

        framesDelay = new double[framesDelayStrArray.length];
        for(int i = 0; i<framesDelay.length; i++)
            framesDelay[i] = Double.parseDouble(framesDelayStrArray[i]);

        // read hit boxes
        final String hitboxXsStr = properties.getProperty(HITBOX_X, "");
        if(!hitboxXsStr.equals("")) {
            final String hitboxWidthsStr = properties.getProperty(HITBOX_WIDTH, "0");
            final String hitboxHeightsStr = properties.getProperty(HITBOX_HEIGHT, "0");
            final String hitboxYsStr = properties.getProperty(HITBOX_Y, "");

            final String[] hitboxesWidthsStrArray = hitboxWidthsStr.split(" +");
            final String[] hitboxesHeightsStrArray = hitboxHeightsStr.split(" +");
            final String[] hitboxesXsStrArray = hitboxXsStr.split(" +");
            final String[] hitboxesYsStrArray = hitboxYsStr.split(" +");

            hitboxes = new Rectangle[hitboxesWidthsStrArray.length][];
            for(int i = 0; i<hitboxesWidthsStrArray.length; i++) {
                final String[] hitboxWidthsStrArray = hitboxesWidthsStrArray[i].split(";+");
                final String[] hitboxHeightsStrArray = hitboxesHeightsStrArray[i].split(";+");
                final String[] hitboxXsStrArray = hitboxesXsStrArray[i].split(";+");
                final String[] hitboxYsStrArray = hitboxesYsStrArray[i].split(";+");
                Rectangle[] frameHitboxes = new Rectangle[hitboxWidthsStrArray.length];
                for(int j = 0; j<frameHitboxes.length; j++) {
                    final double hitboxWidth = Double.parseDouble(hitboxWidthsStrArray[j]);
                    final double hitboxHeight = Double.parseDouble(hitboxHeightsStrArray[j]);
                    final double hitboxX = Double.parseDouble(hitboxXsStrArray[j]);
                    final double hitboxY = Double.parseDouble(hitboxYsStrArray[j]);
                    final Rectangle hitbox = new Rectangle(hitboxWidth, hitboxHeight);
                    hitbox.setTranslateX(hitboxX);
                    hitbox.setTranslateY(hitboxY);
                    hitbox.setFill(HITBOX_COLOR);
                    frameHitboxes[j] = hitbox;
                }
                hitboxes[i] = frameHitboxes;
            }
        }
        else {
            hitboxes = null;
        }

        // read explsions
        final String explsXsStr = properties.getProperty(EXPLOSIONS_X, "");
        if(!explsXsStr.equals("")) {
            final String explsYsStr = properties.getProperty(EXPLOSIONS_Y, "");
            final String explsDelayStr = properties.getProperty(EXPLOSIONS_DELAY, "");

            final String[] explsXsStrArray = explsXsStr.split(" +");
            final String[] explsYsStrArray = explsYsStr.split(" +");
            final String[] explsDelaysStrArray = explsDelayStr.split(" +");

            explosionsX = new double[explsXsStrArray.length];
            explosionsY = new double[explsYsStrArray.length];
            explosionsDelay = new double[explsDelaysStrArray.length];
            for(int i = 0; i<explsXsStrArray.length; i++)
                explosionsX[i] = Double.parseDouble(explsXsStrArray[i]);
            for(int i = 0; i<explsYsStrArray.length; i++)
                explosionsY[i] = Double.parseDouble(explsYsStrArray[i]);
            for(int i = 0; i<explsDelaysStrArray.length; i++)
                explosionsDelay[i] = Double.parseDouble(explsDelaysStrArray[i]);
        }
        else {
            explosionsX = null;
            explosionsY = null;
            explosionsDelay = null;
        }
    }

    /**
     * The constructor.<br>
     * <br>
     * This class contains meta data for an image/item.
     * 
     * @param frameWidth
     *            The width of a frame.
     * @param frameHeight
     *            The height of a frame.
     * @param framesX
     *            The x coordinates of the frames.
     * @param framesY
     *            The y coordinates of the frames.
     * @param framesDelay
     *            The delays in milli seconds of the frames.
     */
    public ItemMeta(final double frameWidth, final double frameHeight, final double[] framesX, final double[] framesY, final double[] framesDelay) {
        this(frameWidth, frameHeight, framesX, framesY, framesDelay, null, null, null, null);
    }

    /**
     * The constructor.<br>
     * <br>
     * This class contains meta data for an image/item.
     * 
     * @param frameWidth
     *            The width of a frame.
     * @param frameHeight
     *            The height of a frame.
     * @param framesX
     *            The x coordinates of the frames.
     * @param framesY
     *            The y coordinates of the frames.
     * @param framesDelay
     *            The delays in milli seconds of the frames.
     * @param hitboxes
     *            The hitboxes of the frames.
     * @param explosionsX
     *            The x coordinates of the explosions when item is destroyed.
     * @param explosionsY
     *            The y coordinates of the explosions when item is destroyed.
     * @param explosionsDelay
     *            The delays of the explosions when item is destroyed.
     */
    public ItemMeta(final double frameWidth, final double frameHeight, final double[] framesX, final double[] framesY, final double[] framesDelay, final Rectangle[][] hitboxes, final double[] explosionsX, final double[] explosionsY, final double[] explosionsDelay) {
        this.width = frameWidth;
        this.height = frameHeight;
        this.framesX = framesX;
        this.framesY = framesY;
        this.framesDelay = framesDelay;
        this.hitboxes = hitboxes;
        this.explosionsX = explosionsX;
        this.explosionsY = explosionsY;
        this.explosionsDelay = explosionsDelay;
    }

    /**
     * Returns the width of the frames.
     * 
     * @return The width of the frames.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the height of the frames.
     * 
     * @return The height of the frames.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Return the x coordinates of the frames.
     * 
     * @return The x coordinates of the frames.
     */
    public double[] getFramesX() {
        return framesX;
    }

    /**
     * Return the y coordinates of the frames.
     * 
     * @return The y coordinates of the frames.
     */
    public double[] getFramesY() {
        return framesY;
    }

    /**
     * Return the delays of the frames.
     * 
     * @return The delays coordinates of the frames.
     */
    public double[] getFramesDelay() {
        return framesDelay;
    }

    /**
     * Returns the time it takes to complete the frame animation.
     * It's the sum of all frame delays.
     * 
     * @return The time to complete the animation.
     */
    public Duration getCompleteDelay() {
        double completeDelay = 0;
        for(double delay : framesDelay)
            completeDelay += delay;

        return Duration.millis(completeDelay);
    }

    /**
     * Returns the x coordinate of the first frame. If no
     * frames has been defined 0 is returned.
     * 
     * @return The x coordinate of the first frame or in
     *         case of no frames 0.
     */
    public double getX() {
        return framesX.length!=0 ? framesX[0] : 0;
    }

    /**
     * Returns the y coordinate of the first frame. If no
     * frames has been defined 0 is returned.
     * 
     * @return The y coordinate of the first frame or in
     *         case of no frames 0.
     */
    public double getY() {
        return framesY.length!=0 ? framesY[0] : 0;
    }

    /**
     * Returns a copy of the hitboxes belonging to a frame of
     * the given index.
     * 
     * @param hitboxesIndex
     *            The index for the hitboxes of a frame.
     * @return A copy of the hitboxes.
     */
    public Rectangle[] getHitboxes(final int hitboxesIndex) {
        final Rectangle[] hitboxes = this.hitboxes[hitboxesIndex];
        final Rectangle[] hitboxesCopy;
        if(hitboxes!=null) {
            hitboxesCopy = new Rectangle[hitboxes.length];
            for(int i = 0; i<hitboxes.length; i++) {
                final Rectangle newHitbox = new Rectangle(hitboxes[i].getWidth(), hitboxes[i].getHeight());
                newHitbox.setTranslateX(hitboxes[i].getTranslateX());
                newHitbox.setTranslateY(hitboxes[i].getTranslateY());
                newHitbox.setFill(HITBOX_COLOR);
                hitboxesCopy[i] = newHitbox;
            }
        }
        else {
            hitboxesCopy = null;
        }

        return hitboxesCopy;
    }

    /**
     * Returns the number of explosions.
     * 
     * @return The number of explosions.
     */
    public int getExplosionsNumber() {
        return explosionsX.length;
    }

    /**
     * Returns the x coordinate of an explosion of the given index.
     * 
     * @param index
     *            The explosion index.
     * @return The x coordinate of an explosion of the given index.
     */
    public double getExplosionX(final int index) {
        return explosionsX[index];
    }

    /**
     * Returns the y coordinate of an explosion of the given index.
     * 
     * @param index
     *            The explosion index.
     * @return The y coordinate of an explosion of the given index.
     */
    public double getExplosionY(final int index) {
        return explosionsY[index];
    }

    /**
     * Returns the delay of an explosion of the given index.
     * 
     * @param index
     *            The explosion index.
     * @return The delay of an explosion of the given index.
     */
    public double getExplosionDelay(final int index) {
        return explosionsDelay[index];
    }

}
