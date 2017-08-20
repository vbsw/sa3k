
//          Copyright 2013, 2017, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k;

import com.github.vbsw.sa3k.engine.ItemMeta;
import com.github.vbsw.sa3k.engine.Util;

import javafx.scene.image.Image;

/**
 * Contains images and animations.
 * 
 * @author Vitali Baumtrok
 */
public class Graphic {

    /*
     * *****************************
     * private
     * *****************************
     */

    private static final String graphicDir          = "res/image";

    private static final String bgSpaceDir          = graphicDir+"/space/Gods-and-Idols-2012-04-11-21-42-08-81.jpg";
    private static final String selectorAngelDir    = graphicDir+"/angel/AngelNPC.png";
    private static final String selectorFireDir     = graphicDir+"/fire/fire.png";
    private static final String selectorFireMetaDir = graphicDir+"/fire/item.properties";
    private static final String playerShipDir       = graphicDir+"/playership/ships_human_0.png";
    private static final String playerShipMetaDir   = graphicDir+"/playership/item.properties";
    private static final String evilCarrierDir      = graphicDir+"/evilcarrier/4.png";
    private static final String evilCarrierMetaDir  = graphicDir+"/evilcarrier/item.properties";
    private static final String playerShotDir       = graphicDir+"/playershot/flamer.png";
    private static final String playerShotMetaDir   = graphicDir+"/playershot/item.properties";
    private static final String explosion1Dir       = graphicDir+"/explosion1/exp2_0.png";
    private static final String explosion1MetaDir   = graphicDir+"/explosion1/item.properties";
    private static final String grellDir            = graphicDir+"/grell/grell_sheet.png";
    private static final String grellMetaDir        = graphicDir+"/grell/item.properties";
    private static final String toxicShotDir        = graphicDir+"/toxicshot/flame.png";
    private static final String toxicShotMetaDir    = graphicDir+"/toxicshot/item.properties";
    private static final String goodyDir            = graphicDir+"/goodies/Misc_Jewels_0.png";
    private static final String goodyMetaDir        = graphicDir+"/goodies/item.properties";
    private static final String upCarrierDir        = graphicDir+"/upgradecarrier/beacon2.png";
    private static final String upCarrierMetaDir    = graphicDir+"/upgradecarrier/item.properties";
    private static final String littleDragonDir     = graphicDir+"/littledragon/enemies.png";
    private static final String littleDragonMetaDir = graphicDir+"/littledragon/item.properties";
    private static final String skullShotDir        = graphicDir+"/skullshot/rpgcritter-update-formatted-transparent.png";
    private static final String skullShotMetaDir    = graphicDir+"/skullshot/item.properties";
    private static final String bossHullDir         = graphicDir+"/bosshull/factory2.png";
    private static final String bossHullMetaDir     = graphicDir+"/bosshull/item.properties";
    private static final String bossFleshDir        = graphicDir+"/bossflesh/cargo1b.png";
    private static final String bossFleshMetaDir    = graphicDir+"/bossflesh/item.properties";
    private static final String bossHeadDir         = graphicDir+"/bosshead/demon.png";
    private static final String bossHeadMetaDir     = graphicDir+"/bosshead/item.properties";
    private static final String bossHandsDir        = graphicDir+"/bosshands/abolished-claw-Swipe.png";
    private static final String bossHandsMetaDir    = graphicDir+"/bosshands/item.properties";
    private static final String bossLauncherDir     = graphicDir+"/bosslauncher/launcher.png";
    private static final String bossLauncherMetaDir = graphicDir+"/bosslauncher/item.properties";
    private static final String energyShotDir       = graphicDir+"/energyshot/bomb1.png";
    private static final String energyShotMetaDir   = graphicDir+"/energyshot/item.properties";
    private static final String swordShotDir        = graphicDir+"/swordshot/00_items_0.png";
    private static final String swordShotMetaDir    = graphicDir+"/swordshot/item.properties";
    private static final String bugShotDir          = graphicDir+"/bugshot/manaworldbeetle1-x.png";
    private static final String bugShotMetaDir      = graphicDir+"/bugshot/item.properties";

    private static Image loadImage(final String relativePath) {
        return Util.loadImage(App.DIRECTORY_PATH.resolve(relativePath));
    }

    /*
     * *****************************
     * public
     * *****************************
     */

    public static final Image    bgSpace          = loadImage(bgSpaceDir);
    public static final Image    selectorAngel    = loadImage(selectorAngelDir);
    public static final Image    selectorFire     = loadImage(selectorFireDir);
    public static final ItemMeta selectorFireMeta = new ItemMeta(App.DIRECTORY_PATH.resolve(selectorFireMetaDir));
    public static final Image    playerShip       = loadImage(playerShipDir);
    public static final ItemMeta playerShipMeta   = new ItemMeta(App.DIRECTORY_PATH.resolve(playerShipMetaDir));
    public static final Image    evilCarrier      = loadImage(evilCarrierDir);
    public static final ItemMeta evilCarrierMeta  = new ItemMeta(App.DIRECTORY_PATH.resolve(evilCarrierMetaDir));
    public static final Image    playerShot       = loadImage(playerShotDir);
    public static final ItemMeta playerShotMeta   = new ItemMeta(App.DIRECTORY_PATH.resolve(playerShotMetaDir));
    public static final Image    explosion1       = loadImage(explosion1Dir);
    public static final ItemMeta explosion1Meta   = new ItemMeta(App.DIRECTORY_PATH.resolve(explosion1MetaDir));
    public static final Image    grell            = loadImage(grellDir);
    public static final ItemMeta grellMeta        = new ItemMeta(App.DIRECTORY_PATH.resolve(grellMetaDir));
    public static final Image    toxicShot        = loadImage(toxicShotDir);
    public static final ItemMeta toxicShotMeta    = new ItemMeta(App.DIRECTORY_PATH.resolve(toxicShotMetaDir));
    public static final Image    goody            = loadImage(goodyDir);
    public static final ItemMeta goodyMeta        = new ItemMeta(App.DIRECTORY_PATH.resolve(goodyMetaDir));
    public static final Image    upCarrier        = loadImage(upCarrierDir);
    public static final ItemMeta upCarrierMeta    = new ItemMeta(App.DIRECTORY_PATH.resolve(upCarrierMetaDir));
    public static final Image    littleDragon     = loadImage(littleDragonDir);
    public static final ItemMeta littleDragonMeta = new ItemMeta(App.DIRECTORY_PATH.resolve(littleDragonMetaDir));
    public static final Image    skullShot        = loadImage(skullShotDir);
    public static final ItemMeta skullShotMeta    = new ItemMeta(App.DIRECTORY_PATH.resolve(skullShotMetaDir));
    public static final Image    bossHull         = loadImage(bossHullDir);
    public static final ItemMeta bossHullMeta     = new ItemMeta(App.DIRECTORY_PATH.resolve(bossHullMetaDir));
    public static final Image    bossFlesh        = loadImage(bossFleshDir);
    public static final ItemMeta bossFleshMeta    = new ItemMeta(App.DIRECTORY_PATH.resolve(bossFleshMetaDir));
    public static final Image    bossHead         = loadImage(bossHeadDir);
    public static final ItemMeta bossHeadMeta     = new ItemMeta(App.DIRECTORY_PATH.resolve(bossHeadMetaDir));
    public static final Image    bossHands        = loadImage(bossHandsDir);
    public static final ItemMeta bossHandsMeta    = new ItemMeta(App.DIRECTORY_PATH.resolve(bossHandsMetaDir));
    public static final Image    bossLauncher     = loadImage(bossLauncherDir);
    public static final ItemMeta bossLauncherMeta = new ItemMeta(App.DIRECTORY_PATH.resolve(bossLauncherMetaDir));
    public static final Image    energyShot       = loadImage(energyShotDir);
    public static final ItemMeta energyShotMeta   = new ItemMeta(App.DIRECTORY_PATH.resolve(energyShotMetaDir));
    public static final Image    swordShot        = loadImage(swordShotDir);
    public static final ItemMeta swordShotMeta    = new ItemMeta(App.DIRECTORY_PATH.resolve(swordShotMetaDir));
    public static final Image    bugShot          = loadImage(bugShotDir);
    public static final ItemMeta bugShotMeta      = new ItemMeta(App.DIRECTORY_PATH.resolve(bugShotMetaDir));

    public static final double   SECTION_GAP      = App.HEIGHT/2;
    public static final double   INFO_GAP         = App.HEIGHT/12;

    /**
     * Contains compact images.
     * 
     * @author Vitali Baumtrok
     */
    public static class Compact {

        /*
         * *****************************
         * private
         * *****************************
         */

        private static final double MAX_IMAGE_SIZE = 150;

        private static Image loadCompactImage(final String relativePath, final double width, final double height) {
            return Util.loadImageResized(App.DIRECTORY_PATH.resolve(relativePath), width, height);
        }

        /*
         * *****************************
         * public
         * *****************************
         */

        public static final Image bgSpace     = loadCompactImage(bgSpaceDir, MAX_IMAGE_SIZE, 0);
        public static final Image evilCarrier = loadCompactImage(evilCarrierDir, 0, MAX_IMAGE_SIZE);

    }

}
