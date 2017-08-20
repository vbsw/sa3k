
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt.menu;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.Graphic;
import com.github.vbsw.sa3k.engine.FrameAnimation;
import com.github.vbsw.sa3k.engine.Layered;
import com.github.vbsw.sa3k.engine.Layered.Action;
import com.github.vbsw.sa3k.prompt.Font;

import javafx.animation.Animation;
import javafx.scene.Group;
import javafx.scene.image.ImageView;

/**
 * 
 * @author Vitali Baumtrok
 */
class Selector implements Action {

    /*
     * *****************************
     * private
     * *****************************
     */

    private final SelectionListener listener     = new SelectionListener(this);
    private final Icon              selectorIcon = new Icon();

    private boolean                 enabled      = true;

    /**
     * 
     * @author Vitali Baumtrok
     */
    private static class Icon extends Group {

        private static final double  TOP_MARGIN_SELECTOR = Menu.TOP_MARGIN_MENU-40;

        private final ImageView      angelImage          = new ImageView(Graphic.selectorAngel);
        private final FrameAnimation fireImage           = new FrameAnimation(Graphic.selectorFire, Graphic.selectorFireMeta, Animation.INDEFINITE);

        private Icon() {
            // position of the icon
            final double x = (App.WIDTH-Menu.MIDDLE_GAP)/2-angelImage.getBoundsInLocal().getWidth();
            setTranslateX(x);
            setTranslateY(TOP_MARGIN_SELECTOR);
            getChildren().add(angelImage);
            getChildren().add(fireImage);
            // position fire image
            fireImage.setScaleX(0.8);
            fireImage.setScaleY(0.8);
            fireImage.setTranslateX(45);
            fireImage.setTranslateY(-3);
        }

        private void destory() {
            setDisable(true);
            fireImage.destroy();
        }

    }

    /*
     * *****************************
     * package
     * *****************************
     */

    void setIconOffsetY(final double offsetIndex) {
        final double offsetGap = Font.MENU.getHeight()+10;
        selectorIcon.setTranslateY(Icon.TOP_MARGIN_SELECTOR+offsetIndex*offsetGap);
    }

    void destroy() {
        // remove listener
        listener.destroy();
        // remove icon
        selectorIcon.destory();
        // remove self
        enabled = false;
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    @Override
    public void init() {
        final Layered layered = App.game.getStack().topPane();

        layered.getInput().addKeyListener(listener);
        layered.addNode(selectorIcon, Menu.LAYER_BG);
        layered.addAnimation(selectorIcon.fireImage.getAnimation());

        selectorIcon.fireImage.getAnimation().play();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void update() {}

}
