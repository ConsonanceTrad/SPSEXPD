package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Preferences;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import pd.SPDSettings;
import pd.ShatteredPixelDungeon;
import pd.scenes.AlchemyScene;
import pd.scenes.GameScene;
import pd.scenes.GiftShopScene;
import pd.scenes.InterlevelScene;
import pd.scenes.TitleScene;
import watabou.noosa.Game;
import watabou.utils.FileUtils;

import java.io.File;

/** Captures actual OpenGL pixels without adding diagnostic code to release builds. */
public final class DesktopSmokeCapture {

	private DesktopSmokeCapture() { }

	public static void main(String[] args) {
		String mode = args[0];
		String output = new File(args[1]).getAbsolutePath();
		int width = Integer.parseInt(args[2]);
		int height = Integer.parseInt(args[3]);
		String dataPath = new File(output).getParentFile().getAbsolutePath() + File.separator + "smoke-data" + File.separator;

		Game.version = System.getProperty("Specification-Version", "smoke");
		Game.versionCode = Integer.parseInt(System.getProperty("Implementation-Version", "1"));
		Lwjgl3Preferences preferences = new Lwjgl3Preferences(new Lwjgl3FileHandle(
				dataPath + SPDSettings.DEFAULT_PREFS_FILE, Files.FileType.Absolute));
		preferences.putBoolean(SPDSettings.KEY_FULLSCREEN, false);
		preferences.putBoolean(SPDSettings.KEY_WINDOW_MAXIMIZED, false);
		preferences.putInteger(SPDSettings.KEY_WINDOW_WIDTH, width);
		preferences.putInteger(SPDSettings.KEY_WINDOW_HEIGHT, height);
		preferences.putInteger(SPDSettings.KEY_UI_SIZE, 0);
		preferences.flush();
		SPDSettings.set(preferences);
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute, dataPath);

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("SPS-SPD framebuffer smoke");
		config.setWindowedMode(width, height);
		config.setWindowSizeLimits(320, 240, -1, -1);
		config.setForegroundFPS(60);
		config.useVsync(false);
		new Lwjgl3Application(new SmokeGame(mode, output), config);
	}

	private static final class SmokeGame extends ShatteredPixelDungeon {
		private final String mode;
		private final String output;
		private int stableFrames;
		private boolean alchemyRequested;
		private boolean giftShopRequested;

		SmokeGame(String mode, String output) {
			super(prepare(mode));
			this.mode = mode;
			this.output = output;
		}

		private static DesktopPlatformSupport prepare(String mode) {
			sceneClass = isDungeonMode(mode) ? InterlevelScene.class : TitleScene.class;
			return new DesktopPlatformSupport();
		}

		private static boolean isDungeonMode(String mode) {
			return "tutorial".equals(mode) || "alchemy".equals(mode);
		}

		@Override
		public void create() {
			super.create();
			if (isDungeonMode(mode)) TitleScene.prepareTutorial();
		}

		@Override
		public void render() {
			super.render();
			if ("alchemy".equals(mode) && !alchemyRequested) {
				stableFrames = scene() instanceof GameScene ? stableFrames + 1 : 0;
				if (stableFrames == 30) {
					alchemyRequested = true;
					stableFrames = 0;
					Game.switchScene(AlchemyScene.class);
				}
				return;
			}
			//SPS: 礼物商店烟测——标题稳定后切入 GiftShopScene 截图
			if ("giftshop".equals(mode) && !giftShopRequested) {
				stableFrames = scene() instanceof TitleScene ? stableFrames + 1 : 0;
				if (stableFrames == 30) {
					giftShopRequested = true;
					stableFrames = 0;
					Game.switchScene(GiftShopScene.class);
				}
				return;
			}
			boolean ready;
			if ("alchemy".equals(mode)) ready = scene() instanceof AlchemyScene;
			else if ("giftshop".equals(mode)) ready = scene() instanceof GiftShopScene;
			else ready = "tutorial".equals(mode) ? scene() instanceof GameScene : scene() instanceof TitleScene;
			stableFrames = ready ? stableFrames + 1 : 0;
			if (stableFrames == 90) {
				capture(output);
				Gdx.app.exit();
			}
		}

		private static void capture(String output) {
			int width = Gdx.graphics.getBackBufferWidth();
			int height = Gdx.graphics.getBackBufferHeight();
			byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, width, height, true);
			Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
			BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
			PixmapIO.writePNG(Gdx.files.absolute(output), pixmap);
			pixmap.dispose();
			System.out.println("SPS desktop smoke captured: " + output);
		}
	}
}
