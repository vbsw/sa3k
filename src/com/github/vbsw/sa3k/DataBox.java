
//          Copyright 2013, Vitali Baumtrok.
// Distributed under the Boost Software License, Version 1.0.
//      (See accompanying file BSLv1.LICENSE or copy at
//        http://www.boost.org/LICENSE_1_0.txt)


package com.github.vbsw.sa3k;

/**
 * This class suits the purpose of message passing, by being
 * global memory.
 * 
 * @author Vitali Baumtrok
 */
public class DataBox {

    public static double  playerShipCenterCoordX = 0;
    public static double  playerShipCenterCoordY = 0;

    public static int     score                  = 0;
    public static int     destroyedGrells        = 0;

    public static boolean playerAlive            = true;

}
