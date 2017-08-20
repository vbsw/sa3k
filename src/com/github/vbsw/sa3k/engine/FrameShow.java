
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.engine;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * A class to show frames. Similar to {@link FrameAnimation}.
 * Actually you can use <tt>FrameAnimation</tt> to show
 * single frames, but this class allocates less memory
 * then <tt>FrameAnimation</tt>.
 * 
 * @author Vitali Baumtrok
 */
public class FrameShow extends ImageView {

    /*
     * *****************************
     * private
     * *****************************
     */

    /**
     * View ports, i.e. the frames, of the image.
     */
    private final Rectangle2D[] viewPorts;

    /**
     * The number of frames.
     */
    private final int           framesNumber;

    /*
     * *****************************
     * public
     * *****************************
     */

    /**
     * The constructor.<br>
     * <br>
     * This class shows frames. Similar to {@link FrameAnimation}.
     * Actually you can use <tt>FrameAnimation</tt> to show
     * single frames, but this class allocates less memory
     * then <tt>FrameAnimation</tt>.
     * 
     * @param image
     *            The image consisting of frames.
     * @param frames
     *            The meta data for the given image.
     */
    public FrameShow(final Image image, final ItemMeta frames) {
        super(image);

        framesNumber = frames.framesX.length;
        viewPorts = new Rectangle2D[framesNumber];
        for(int i = 0; i<frames.framesX.length; i++)
            viewPorts[i] = new Rectangle2D(frames.framesX[i], frames.framesY[i], frames.width, frames.height);

        setFrame(0);
    }

    /**
     * Returns the number of frames.
     * 
     * @return The number of frames.
     */
    public int getFramesNumber() {
        return framesNumber;
    }

    /**
     * The index of the first frame is 0. The index of the
     * Nth frame is N-1.
     * 
     * @param frameIndex
     *            The index of the frame. 0 or higher.
     */
    public void setFrame(final int frameIndex) {
        setViewport(viewPorts[frameIndex]);
    }

}
