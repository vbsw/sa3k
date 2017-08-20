
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k.prompt.credits;

import com.github.vbsw.sa3k.App;
import com.github.vbsw.sa3k.Graphic;
import com.github.vbsw.sa3k.engine.FrameShow;
import com.github.vbsw.sa3k.prompt.Font;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * 
 * @author Vitali Baumtrok
 */
class CreditsPane extends VBox {

    /*
     * *****************************
     * private
     * *****************************
     */

    private Text createComment(final String s, final boolean bold) {
        final Text text = new Text(s);

        if(bold)
            text.setFont(Font.COMMENT_BOLD.getJfxFont());
        else
            text.setFont(Font.COMMENT.getJfxFont());
        text.setFill(Color.WHITE);

        return text;
    }

    private Rectangle createSeperator(final double height) {
        final Rectangle seperator = new Rectangle(0, 0, 10, height);
        seperator.setFill(Color.TRANSPARENT);
        return seperator;
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public CreditsPane() {
        super(4);
        setMinWidth(App.WIDTH);
        setMinHeight(App.HEIGHT);
        setPrefWidth(App.WIDTH);
        setPrefHeight(App.HEIGHT);
        setAlignment(Pos.TOP_CENTER);
        setTranslateY(1000);
        setCacheHint(CacheHint.QUALITY);
        setCache(true);

        final ObservableList<Node> nodes = getChildren();
        final Text tTitle = new Text(App.GAME_TITLE);
        final HBox playerShot_toxicShot = new HBox(10);
        final HBox boss = new HBox(10);

        tTitle.setFont(Font.TITLE.getJfxFont());
        tTitle.setFill(Color.WHITE);
        playerShot_toxicShot.setAlignment(Pos.CENTER);
        playerShot_toxicShot.getChildren().add(new ImageView(Graphic.playerShot));
        playerShot_toxicShot.getChildren().add(new ImageView(Graphic.toxicShot));
        playerShot_toxicShot.getChildren().add(new FrameShow(Graphic.upCarrier, Graphic.upCarrierMeta));
        boss.setAlignment(Pos.CENTER);
        boss.getChildren().add(new ImageView(Graphic.bossHull));
        boss.getChildren().add(new FrameShow(Graphic.bossFlesh, Graphic.bossFleshMeta));
        boss.getChildren().add(new FrameShow(Graphic.bossLauncher, Graphic.bossLauncherMeta));
        boss.getChildren().add(new FrameShow(Graphic.energyShot, Graphic.energyShotMeta));

        nodes.add(tTitle);
        nodes.add(createSeperator(Graphic.SECTION_GAP));
        nodes.add(createComment("programmed by", true));
        nodes.add(createComment("Vitali Baumtrok", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(createComment("graphics and audio from", true));
        nodes.add(createComment("OpenGameArt.org", false));
        // graphics
        nodes.add(createSeperator(Graphic.SECTION_GAP));
        nodes.add(createComment("graphics", true));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(new ImageView(Graphic.Compact.bgSpace));
        nodes.add(createComment("published by", false));
        nodes.add(createComment("Jattenalle (www.GodsAndIdols.com)", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(new ImageView(Graphic.selectorAngel));
        nodes.add(createComment("published by Tracy", false));
        nodes.add(createComment("[modified by Vitali Baumtrok]", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(new FrameShow(Graphic.selectorFire, Graphic.selectorFireMeta));
        nodes.add(createComment("published by Mikodrak", false));
        nodes.add(createComment("[modified by Vitali Baumtrok]", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(new FrameShow(Graphic.playerShip, Graphic.playerShipMeta));
        nodes.add(createComment("published by surt", false));
        nodes.add(createComment("[modified by Vitali Baumtrok]", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(new ImageView(Graphic.Compact.evilCarrier));
        nodes.add(createComment("created and published by Skorpio", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(playerShot_toxicShot);
        nodes.add(createComment("created by Daniel Cook", false));
        nodes.add(createComment("published by bart", false));
        nodes.add(createComment("[modified by Vitali Baumtrok]", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(boss);
        nodes.add(createComment("created by Daniel Cook", false));
        nodes.add(createComment("published by bart", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(new FrameShow(Graphic.explosion1, Graphic.explosion1Meta));
        nodes.add(createComment("created and published by Cuzco", false));
        nodes.add(createComment("[modified by Vitali Baumtrok]", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(new FrameShow(Graphic.grell, Graphic.grellMeta));
        nodes.add(createComment("created by Pawel \"Nmn\" Zarczynski", false));
        nodes.add(createComment("published by Anonymous", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(new ImageView(Graphic.goody));
        nodes.add(createComment("published by Mumu", false));
        nodes.add(createComment("[modified by Vitali Baumtrok]", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(new ImageView(Graphic.littleDragon));
        nodes.add(createComment("created and published by CharlesGabriel", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(new ImageView(Graphic.skullShot));
        nodes.add(createComment("created by Stephen \"Redshrike\" Challener", false));
        nodes.add(createComment("published by Redshrike", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(new FrameShow(Graphic.bossHead, Graphic.bossHeadMeta));
        nodes.add(createComment("created by Warlock's Gauntlet team", false));
        nodes.add(createComment("(rAum, jackFlower, DrZoliparia, Neil2D)", false));
        nodes.add(createComment("published by Liosan", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(new FrameShow(Graphic.bossHands, Graphic.bossHandsMeta));
        nodes.add(createComment("created by random223", false));
        nodes.add(createComment("(http://random223.deviantart.com)", false));
        nodes.add(createComment("(Ancient Beast project, http://AncientBeast.com)", false));
        nodes.add(createComment("published by Dread Knight", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(new ImageView(Graphic.swordShot));
        nodes.add(createComment("created and published by Gwes", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(new FrameShow(Graphic.bugShot, Graphic.bugShotMeta));
        nodes.add(createComment("created by Redshrike (Stephen Challener)", false));
        nodes.add(createComment("published by Redshrike", false));
        // music
        nodes.add(createSeperator(Graphic.SECTION_GAP));
        nodes.add(createComment("music", true));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(createComment("\"Insistent: background loop\"", false));
        nodes.add(createComment("published by yd", false));
        nodes.add(createComment("[played in credits]", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(createComment("\"anxious\"", false));
        nodes.add(createComment("created by", false));
        nodes.add(createComment("Devon Baumgarten (devonbaumgarten.com)", false));
        nodes.add(createComment("published by lalanl (@lalanl)", false));
        nodes.add(createComment("[played in level 1, prelude]", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(createComment("\"Crispy Town\"", false));
        nodes.add(createComment("created and published by Szymon Matuszewski", false));
        nodes.add(createComment("[played in level 1, main theme]", false));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(createComment("\"Sharp's theme\"", false));
        nodes.add(createComment("created by Johan Brodd", false));
        nodes.add(createComment("published by jobromedia", false));
        nodes.add(createComment("[played in level 1, boss theme]", false));
        // sound effects
        nodes.add(createSeperator(Graphic.SECTION_GAP));
        nodes.add(createComment("sound effects", true));
        nodes.add(createSeperator(Graphic.INFO_GAP));
        nodes.add(createComment("\"Warm Digital Accept Button HP\"", false));
        nodes.add(createComment("created and published by Circlerun", false));
        nodes.add(createComment("[selector movement]", false));
    }

}
