/*
 *   Copyright 2013, 2017 Vitali Baumtrok <vbsw@mailbox.org>
 * Distributed under the Boost Software License, Version 1.0.
 *   (See accompanying file LICENSES/BSL-1.0.txt or copy at
 *        http://www.boost.org/LICENSE_1_0.txt)
 */

package com.github.vbsw.sa3k;


import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.vbsw.sa3k.prompt.GameCtrl;
import com.github.vbsw.sa3k.prompt.menu.Menu;

import javafx.application.Application;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


/**
 * Application start class. Contains main-method.
 * 
 * @author Vitali Baumtrok
 */
public class App extends Application {

	/*
	 * *****************************
	 * private
	 * *****************************
	 */

	/**
	 * Wrapper for the program components.
	 * 
	 * @author Vitali Baumtrok
	 */
	private static class Game extends com.github.vbsw.sa3k.engine.Game {

		public Game ( final Stage stage, final double width, final double height, final boolean fulllscreen ) {
			super(stage,width,height,fulllscreen);
		}

		@Override
		public void start ( ) {
			super.start();
		}

	}

	/**
	 * Determines the path to the application folder.
	 * 
	 * @return Absolute path to the application folder.
	 */
	private static Path getGameDirectory ( ) {
		try {
			final String classFileExtension = ".class";
			final String jarPrefix = "jar:";
			final String binFolder = "/bin";
			final String className = App.class.getSimpleName() + classFileExtension;
			final URL classUrl = App.class.getResource(className);
			final String classUrlStr = classUrl.toString();
			final String canonicalClassName = App.class.getCanonicalName();
			final int canonicalClassLength = canonicalClassName.length() + classFileExtension.length();
			final boolean isFileAJar = classUrlStr.startsWith(jarPrefix);

			if ( isFileAJar ) {
				final String jarDirStr = classUrlStr.substring(4,classUrlStr.length() - canonicalClassLength - 2);
				final URL jarDirUrl = new URL(jarDirStr);
				final URI jarDirUri = jarDirUrl.toURI();
				final Path path = Paths.get(jarDirUri);

				// example:
				// path == "<some path>/sa3k/bin/SpaceAction3000.jar"
				// return "<some path>/sa3k/"
				return path.getParent().getParent();

			} else {
				final String projDirStr = classUrlStr.substring(0,classUrlStr.length() - canonicalClassLength - binFolder.length());
				final URL projDirUrl = new URL(projDirStr);
				final URI projDirUri = projDirUrl.toURI();
				final Path path = Paths.get(projDirUri);

				return path;
			}

		} catch ( final Exception e ) {
			e.printStackTrace();

			return null;
		}
	}

	/*
	 * *****************************
	 * public
	 * *****************************
	 */

	/**
	 * The initial width of the game field view.
	 */
	public static final double WIDTH = 360;

	/**
	 * The initial height of the game field view.
	 */
	public static final double HEIGHT = 480;

	/**
	 * The center of the x coordinate of the game field view.
	 */
	public static final double CENTER_X = WIDTH / 2;

	/**
	 * If <tt>true</tt> application will start in fullscreen,
	 * otherwise it will have at least the size defined
	 * with <tt>WIDTH</tt> and <tt>HEIGHT</tt>.
	 */
	public static final boolean START_IN_FULLSCREEN = false;

	/**
	 * The directory of the game.
	 */
	public static final Path DIRECTORY_PATH = getGameDirectory();

	/**
	 * The title of the game.
	 */
	public static final String GAME_TITLE = "Space Action 3000";

	/**
	 * The version of the game.
	 */
	public static final String GAME_VERSION = "0.8.1";

	/**
	 * Provides components of the game engine.
	 */
	public static com.github.vbsw.sa3k.engine.Game game;

	/**
	 * Launches the JavaFX application thread.
	 * 
	 * @param args
	 *            Commandline arguments. Not used.
	 */
	public static void main ( final String[] args ) {
		Application.launch(args);
	}

	/**
	 * A JavaFX method overriden to initialize and start the game.
	 */
	@Override
	public void start ( final Stage stage ) throws Exception {
		final Game game = new Game(stage,WIDTH,HEIGHT,START_IN_FULLSCREEN);
		App.game = game;

		game.getStack().setBg(new ImageView(Graphic.bgSpace));
		game.getStack().pushPane(0);
		game.getStack().topPane().getInput().addKeyListener(new GameCtrl());
		game.getStack().pushPane(Menu.LAYERS_NUMBER);
		game.getStack().topPane().addAction(Menu.instance);
		game.start();
	}

}
