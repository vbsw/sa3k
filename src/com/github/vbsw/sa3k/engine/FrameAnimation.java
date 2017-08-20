
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.engine;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * A class to show an animation consisting of frames.
 * 
 * @author Vitali Baumtrok
 */
public class FrameAnimation extends ImageView {

    /*
     * *****************************
     * private
     * *****************************
     */

    /**
     * The transition setting the frames.
     */
    private final FrameTransition animation;

    /**
     * A class to set the frames in the ImageView while
     * time processing.
     * 
     * @author Vitali Baumtrok
     */
    private static class FrameTransition extends Transition {

        /**
         * The ImageView in which the frame is to be set.
         */
        private final ImageView     imageView;

        /**
         * View ports, i.e. the frames, of the image.
         */
        private final Rectangle2D[] viewPorts;

        /**
         * Time fractions indicating when a view port is to change.
         */
        private final double[]      fractions;

        /*
         * *****************************
         * protected
         * *****************************
         */

        /**
         * Sets the view port, i.e. the frame.
         */
        @Override
        protected void interpolate(final double frac) {
            if(fractions.length>0) {
                int index = 0;
                // searching for index
                while((index<fractions.length)&&(fractions[index]<frac))
                    index++;
                // setting the view port
                imageView.setViewport(viewPorts[index]);
            }
        }

        /*
         * *****************************
         * public
         * *****************************
         */

        /**
         * The constructor.<br>
         * <br>
         * This class sets the frames in the ImageView while time processing.
         * 
         * @param imageView
         *            The ImageView of the framed image.
         * @param meta
         *            Meta data belonging to the framed image.
         */
        public FrameTransition(final ImageView imageView, final ItemMeta meta) {
            final int framesNumber = meta.framesX.length;

            this.imageView = imageView;
            this.viewPorts = new Rectangle2D[framesNumber];
            this.fractions = new double[framesNumber];

            double totalDuration = 0;
            for(int i = 0; i<meta.framesX.length; i++) {
                totalDuration += meta.framesDelay[i];
                viewPorts[i] = new Rectangle2D(meta.framesX[i], meta.framesY[i], meta.width, meta.height);
            }
            for(int i = 0; i<fractions.length-1; i++) {
                double delayUpToIndex = 0;
                for(int j = 0; j<=i; j++)
                    delayUpToIndex += meta.framesDelay[j];
                fractions[i] = delayUpToIndex/totalDuration;
            }
            fractions[fractions.length-1] = 1.0;
            this.imageView.setViewport(viewPorts[0]);

            setCycleDuration(Duration.millis(totalDuration));
            setInterpolator(Interpolator.LINEAR);
        }

        /**
         * Returns the number of frames.
         * 
         * @return The number of frames.
         */
        public int getFramesNumber() {
            return viewPorts.length;
        }

        /**
         * Returns the current frame number.
         * 
         * @return The current frame number.
         */
        public int getCurrentFrameNumber() {
            final Rectangle2D currentViewPort = imageView.getViewport();
            int index = 0;
            while(viewPorts[index]!=currentViewPort)
                index++;

            return index;
        }

    }

    /*
     * *****************************
     * public
     * *****************************
     */

    /**
     * The constructor.<br>
     * <br>
     * This class to shows an animation consisting of frames.<br>
     * <br>
     * Use {@link Animation#INDEFINITE} to set the cycles to infinity.
     * 
     * @param image
     *            The image consisting of frames.
     * @param meta
     *            The meta data for the given image.
     * @param cycleCount
     *            Number of cycles to play the animation.
     */
    public FrameAnimation(final Image image, final ItemMeta meta, final int cycleCount) {
        super(image);
        animation = new FrameAnimation.FrameTransition(this, meta);
        animation.setCycleCount(cycleCount);
    }

    /**
     * Returns the animation.
     * 
     * @return The animation.
     */
    public Animation getAnimation() {
        return animation;
    }

    /**
     * Returns the number of frames.
     * 
     * @return The number of frames.
     */
    public int getFramesNumber() {
        return animation.getFramesNumber();
    }

    /**
     * Return the frame number.
     * 
     * @return The frame number.
     */
    public int getCurrentFrameNumber() {
        return animation.getCurrentFrameNumber();
    }

    /**
     * Disables the node and stops the animation.
     */
    public void destroy() {
        setDisable(true);
        animation.stop();
    }

}
