/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.scenes;

import pd.Assets;
import pd.Badges;
import pd.Chrome;
import pd.Dungeon;
import pd.GamesInProgress;
import pd.Rankings;
import pd.SPDSettings;
import pd.ShatteredPixelDungeon;
import pd.effects.BannerSprites;
import pd.effects.Fireball;
import pd.journal.Document;
import pd.journal.Journal;
import pd.messages.Messages;
import pd.ui.Icons;
import pd.ui.RenderedTextBlock;
import pd.ui.StyledButton;
import pd.ui.TitleBackground;
import pd.windows.WndError;
import pd.windows.WndHardNotification;
import render.glwrap.Blending;
import render.input.ControllerHandler;
import render.noosa.Camera;
import render.noosa.ColorBlock;
import render.noosa.Game;
import render.noosa.Image;
import render.noosa.audio.Music;
import render.utils.geom.RectF;
import render.utils.serialize.FileUtils;

import java.util.Collections;
import pd.messages.InlineText;

public class WelcomeScene extends PixelScene {
	//SPSEXPD: inline Chinese text (generated from messages/scenes/zh)
	static {
		InlineText.of(WelcomeScene.class)
			.t("update_intro", "SPS-SPD 已经更新！")
			.t("update_msg", "SPS-SPD 4.0.0-sps.1 已建立特别惊喜像素地牢基于破碎 4.0 的独立移植工程。现有异界路线、挑战、伙伴、装备、九格快捷栏和露珠瓶操作均予保留，其余旧版系统将依据可核验的内容台账继续迁移。")
			.t("patch_intro", "破碎的像素地牢补丁已经成功安装！")
			.t("patch_bugfixes", "本次补丁包含少量Bug修复。")
			.t("patch_translations", "本次补丁包含翻译文本更新。")
			.t("patch_balance", "本次更新包含游戏平衡性改动。")
			.t("patch_msg", "本次补丁同样包含少量微调和性能优化。")
			.t("what_msg", "看起来你正使用的存档来自于新版本的破碎地牢！\n\n谨慎行事！你的存档可能包含一些这个版本中不存在的东西，这可能会导致一些非常奇怪的错误发生。")
			.t("lang_warning_title", "翻译未完成")
			.t("lang_warning_msg", "本次更新内容还未被完全翻译。\n\n部分词汇可能还是英语。\n\n翻译组正全力解决这个问题，完整翻译将在不久后放出。\n\n谢谢您的耐心等待。")
			.t("continue", "继续")
			.t("changelist", "变动列表")
			.t("save_warning", "最近，破碎的像素地牢在进行存档时被打断了。\n\n本游戏内置有保护机制，所以你的游戏数据可能不会丢失。\n\n这通常发生在你的设备突然断电，或没有以合适方式关闭本游戏的时候。移动设备的省电功能通常会导致这种情况。\n\n如果你经常看见此提示，可以尝试通过重启设备的方法来解决。")
			.t("controller_title", "手柄输入")
			.t("controller_body", "正在使用手柄进行游玩吗？使用右摇杆来操纵屏幕上的光标。\n\n按下RT/R2键或右摇杆以进行点击。")
			.t("controller_okay", "开启");
	}




	private static final int LATEST_UPDATE = ShatteredPixelDungeon.v4_0_0;

	//used so that the game does not keep showing the window forever if cleaning fails
	private static boolean triedCleaningTemp = false;

	@Override
	public void create() {
		super.create();

		final int previousVersion = SPDSettings.version();

		if (!triedCleaningTemp && FileUtils.cleanTempFiles()){
			add(new WndHardNotification(Icons.get(Icons.WARNING),
					Messages.get(WndError.class, "title"),
					Messages.get(this, "save_warning"),
					Messages.get(this, "continue"),
					5){
				@Override
				public void hide() {
					super.hide();
					triedCleaningTemp = true;
					ShatteredPixelDungeon.resetScene();
				}
			});
			return;
		}

		//SPS: 只要不需要首次安装的引导，就直接进标题界面。
		//版本号变化时不再插入"进入 / 改动"界面，但仍执行一次老存档数据迁移。
		if (!SPDSettings.intro()) {

			if (previousVersion != 0 && previousVersion < ShatteredPixelDungeon.versionCode) {
				updateVersion(previousVersion);
			}

			ShatteredPixelDungeon.switchNoFade(TitleScene.class);
			return;
		}

		Music.INSTANCE.playTracks(
				new String[]{Assets.Music.THEME_1, Assets.Music.THEME_2},
				new float[]{1, 1},
				false);

		uiCamera.visible = false;

		int w = Camera.main.width;
		int h = Camera.main.height;
		RectF insets = getCommonInsets();

		TitleBackground BG = new TitleBackground(w, h);
		add( BG );

		//darkens the arches
		add(new ColorBlock(w, h, 0x44000000));

		w -= insets.left + insets.right;
		h -= insets.top + insets.bottom;

		Image title = BannerSprites.get( landscape() ? BannerSprites.Type.TITLE_LAND : BannerSprites.Type.TITLE_PORT);
		add( title );

		float topRegion = Math.max(title.height - 6, h*0.45f);

		title.x = insets.left + (w - title.width()) / 2f;
		title.y = insets.top + 2 + (topRegion - title.height()) / 2f;

		align(title);

		if (landscape()){
			placeTorch(title.x + 30, title.y + 35);
			placeTorch(title.x + title.width - 30, title.y + 35);
		} else {
			placeTorch(title.x + 16, title.y + 70);
			placeTorch(title.x + title.width - 16, title.y + 70);
		}

		Image signs = new Image(BannerSprites.get( landscape() ? BannerSprites.Type.TITLE_GLOW_LAND : BannerSprites.Type.TITLE_GLOW_PORT)){
			private float time = 0;
			@Override
			public void update() {
				super.update();
				am = Math.max(0f, (float)Math.sin( time += Game.elapsed ));
				if (time >= 1.5f*Math.PI) time = 0;
			}
			@Override
			public void draw() {
				Blending.setLightMode();
				super.draw();
				Blending.setNormalMode();
			}
		};
		signs.x = title.x + (title.width() - signs.width())/2f;
		signs.y = title.y;
		add( signs );
		
		StyledButton okay = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "continue")){
			@Override
			protected void onClick() {
				super.onClick();
				if (previousVersion == 0 || SPDSettings.intro()){

					if (previousVersion > 0){
						updateVersion(previousVersion);
					}

					SPDSettings.version(ShatteredPixelDungeon.versionCode);
					GamesInProgress.selectedClass = null;
					GamesInProgress.curSlot = GamesInProgress.firstEmpty();
					if (GamesInProgress.curSlot == -1 || Rankings.INSTANCE.totalNumber > 0){
						SPDSettings.intro(false);
						ShatteredPixelDungeon.switchScene(TitleScene.class);
					} else {
						ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
					}
				} else {
					updateVersion(previousVersion);
					ShatteredPixelDungeon.switchScene(TitleScene.class);
				}
			}
		};

		float buttonY = insets.top + Math.min(topRegion + (PixelScene.landscape() ? 60 : 120), h - 24);

		float buttonAreaWidth = landscape() ? PixelScene.MIN_WIDTH_L-6 : PixelScene.MIN_WIDTH_P-2;
		float btnAreaLeft = insets.left + (w - buttonAreaWidth) / 2f;
		if (previousVersion != 0 && !SPDSettings.intro()){
			StyledButton changes = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(TitleScene.class, "changes")){
				@Override
				protected void onClick() {
					super.onClick();
					updateVersion(previousVersion);
					ShatteredPixelDungeon.switchScene(ChangesScene.class);
				}
			};
			okay.setRect(btnAreaLeft, buttonY, (buttonAreaWidth/2)-1, 20);
			add(okay);

			changes.setRect(okay.right()+1, buttonY, okay.width(), 20);
			changes.icon(Icons.get(Icons.CHANGES));
			add(changes);
		} else {
			okay.text(Messages.get(TitleScene.class, "enter"));
			okay.setRect(btnAreaLeft, buttonY, buttonAreaWidth, 20);
			okay.icon(Icons.get(Icons.ENTER));
			add(okay);
		}

		RenderedTextBlock text = PixelScene.renderTextBlock(6);
		String message;
		if (previousVersion == 0 || SPDSettings.intro()) {
			message = Document.INTROS.pageBody(0);
		} else if (previousVersion <= ShatteredPixelDungeon.versionCode) {
			if (previousVersion < LATEST_UPDATE){
				message = Messages.get(this, "update_intro");
				message += "\n\n" + Messages.get(this, "update_msg");
			} else {
				//TODO: change the messages here in accordance with the type of patch.
				message = Messages.get(this, "patch_intro");
				message += "\n";
				//message += "\n" + Messages.get(this, "patch_balance");
				message += "\n" + Messages.get(this, "patch_bugfixes");
				message += "\n" + Messages.get(this, "patch_translations");

			}
		} else {
			message = Messages.get(this, "what_msg");
		}

		text.text(message, Math.min(w-20, 300));
		float titleBottom = title.y + title.height();
		float textSpace = okay.top() - titleBottom - 4;
		text.setPos(insets.left + (w - text.width()) / 2f, (titleBottom + 2) + (textSpace - text.height())/2);
		add(text);

		if (SPDSettings.intro() && ControllerHandler.isControllerConnected()){
			addToFront(new WndHardNotification(Icons.CONTROLLER.get(),
					Messages.get(WelcomeScene.class, "controller_title"),
					Messages.get(WelcomeScene.class, "controller_body"),
					Messages.get(WelcomeScene.class, "controller_okay"),
					0){
				@Override
				public void onBackPressed() {
					//do nothing, must press the okay button
				}
			});
		}
	}

	private void placeTorch( float x, float y ) {
		Fireball fb = new Fireball();
		fb.x = x - fb.width()/2f;
		fb.y = y - fb.height();

		align(fb);
		add( fb );
	}

	private void updateVersion(int previousVersion){

		//update rankings, to update any data which may be outdated
		if (previousVersion < LATEST_UPDATE){

			Badges.loadGlobal();
			Journal.loadGlobal();

			try {
				Rankings.INSTANCE.load();
				for (Rankings.Record rec : Rankings.INSTANCE.records.toArray(new Rankings.Record[0])){
					try {
						Rankings.INSTANCE.loadGameData(rec);
						Rankings.INSTANCE.saveGameData(rec);
					} catch (Exception e) {
						//if we encounter a fatal per-record error, then clear that record's data
						rec.gameData = null;
						Game.reportException( new RuntimeException("Rankings Updating Failed!",e));
					}
				}
				if (Rankings.INSTANCE.latestDaily != null){
					try {
						Rankings.INSTANCE.loadGameData(Rankings.INSTANCE.latestDaily);
						Rankings.INSTANCE.saveGameData(Rankings.INSTANCE.latestDaily);
					} catch (Exception e) {
						//if we encounter a fatal per-record error, then clear that record's data
						Rankings.INSTANCE.latestDaily.gameData = null;
						Game.reportException( new RuntimeException("Rankings Updating Failed!",e));
					}
				}
				Collections.sort(Rankings.INSTANCE.records, Rankings.scoreComparator);
				Rankings.INSTANCE.save();
			} catch (Exception e) {
				//if we encounter a fatal error, then just clear the rankings
				FileUtils.deleteFile( Rankings.RANKINGS_FILE );
				Game.reportException( new RuntimeException("Rankings Updating Failed!",e));
			}
			Dungeon.daily = Dungeon.dailyReplay = false;

			Badges.saveGlobal(true);
			Journal.saveGlobal(true);

		}

		SPDSettings.version(ShatteredPixelDungeon.versionCode);
	}
	
}
