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
import pd.SPDSettings;
import pd.ShatteredPixelDungeon;
import pd.actors.hero.HeroClass;
import pd.effects.BannerSprites;
import pd.effects.Fireball;
import pd.messages.Languages;
import pd.messages.Messages;
import pd.services.updates.AvailableUpdateData;
import pd.services.updates.Updates;
import pd.sprites.CharSprite;
import pd.ui.ActionIndicator;
import pd.ui.ExitButton;
import pd.ui.IconButton;
import pd.ui.Icons;
import pd.ui.StyledButton;
import pd.ui.TitleBackground;
import pd.ui.Window;
import pd.windows.WndOptions;
import pd.windows.WndSettings;
import pd.windows.WndVictoryCongrats;
import render.glwrap.Blending;
import render.input.PointerEvent;
import render.noosa.BitmapText;
import render.noosa.Camera;
import render.noosa.Game;
import render.noosa.Image;
import render.noosa.PointerArea;
import render.noosa.audio.Music;
import render.noosa.tweeners.Tweener;
import render.utils.geom.RectF;
import render.utils.math.ColorMath;
import render.utils.math.GameMath;
import render.utils.platform.DeviceCompat;
import pd.messages.InlineText;

public class TitleScene extends PixelScene {
	//SPSEXPD: inline Chinese text (generated from messages/scenes/zh)
	static {
		InlineText.of(TitleScene.class)
			.t("play", "开始")
			.t("enter", "进入地牢")
			.t("continue", "继续")
			.t("learn", "新手教程")
			.t("rankings", "排行榜")
			.t("journal", "日志")
			.t("news", "游戏新闻")
			.t("changes", "改动")
			.t("update", "更新")
			.t("install", "安装")
			.t("settings", "设置")
			.t("about", "关于")
			.t("support", "加入交流群")
			.t("$changesbutton.title", "检测到新版本！")
			.t("$changesbutton.versioned_title", "最新版本：%s")
			.t("$changesbutton.desc", "破碎的像素地牢会时常更新以变更既有内容，或是加入新东西！\n\n游戏平衡也会经常得到调整，维持物品、英雄、敌人强度的大致均衡。\n\n更新还包含漏洞修复与各种稳定性提升。")
			.t("$changesbutton.update", "前往更新详情页")
			.t("$changesbutton.changes", "近期更新界面")
			.t("patreon_body", "《破碎像素地牢》是一款完全免费的游戏，有玩家的大方捐献支持我才能一直坚持开发。\n\n如果想支持我，最好的方法是使用Patreon平台。Patreon能提供一个稳定的收入源，也让我有方法回馈我的支持者！\n\nPatreon支持者每周都可以看一篇独家文章，抢先于其他所有人了解我的下一步开发想法！\n\n你可以访问我的Patreon页面获悉最新的回馈详情。感谢你的支持！\n\n(Patreon奖励只能提供英语内容，请见谅)")
			.t("patreon_button", "Patreon赞助页面")
			.t("giftshop", "礼物商店");
	}




	private Image title;
	private Fireball leftFB;
	private Fireball rightFB;
	private Image signs;

	private StyledButton btnPlay;
	//SPS: 继续游戏（原创缺口，原版无此按钮）
	private StyledButton btnContinue;
	private StyledButton btnSupport;
	private StyledButton btnRankings;
	//SPS: 关于/日志收敛为左上角小图标
	private IconButton btnJournal;
	//SPS: 礼物商店（S金购买永久强化解锁，用户裁决 2026-09-30）
	private StyledButton btnGiftShop;
	private StyledButton btnChanges;
	private StyledButton btnSettings;
	private IconButton btnAbout;

	private BitmapText version;
	private ExitButton btnExit;

	@Override
	public void create() {
		
		super.create();

		Music.INSTANCE.play(Assets.Music.SPS_THEME, true);
		Music.INSTANCE.volume(1f);

		uiCamera.visible = false;
		
		int w = Camera.main.width;
		int h = Camera.main.height;

		RectF insets = getCommonInsets();

		TitleBackground BG = new TitleBackground( w, h );
		add( BG );

		w -= insets.left + insets.right;
		h -= insets.top + insets.bottom;

		title = BannerSprites.get( landscape() ? BannerSprites.Type.TITLE_LAND : BannerSprites.Type.TITLE_PORT);
		add( title );

		float topRegion = Math.max(title.height - 6, h*0.45f);

		title.x = insets.left + (w - title.width()) / 2f;
		title.y = insets.top + 2 + (topRegion - title.height()) / 2f;

		align(title);

		if (landscape()){
			leftFB = placeTorch(title.x + 30, title.y + 35);
			rightFB = placeTorch(title.x + title.width - 30, title.y + 35);
		} else {
			leftFB = placeTorch(title.x + 16, title.y + 70);
			rightFB = placeTorch(title.x + title.width - 16, title.y + 70);
		}

		signs = new Image(BannerSprites.get( landscape() ? BannerSprites.Type.TITLE_GLOW_LAND : BannerSprites.Type.TITLE_GLOW_PORT)){
			private float time = 0;
			@Override
			public void update() {
				super.update();
				am = Math.max(0f, (float)Math.sin( time += Game.elapsed ));
				am = Math.min(am, title.am);
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

		final Chrome.Type GREY_TR = Chrome.Type.GREY_BUTTON_TR;
		
		btnPlay = new StyledButton(GREY_TR, Messages.get(this, "enter")){
			@Override
			protected void onClick() {
				if (GamesInProgress.checkAll().size() == 0){
					GamesInProgress.selectedClass = null;
					GamesInProgress.curSlot = 1;
					ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
				} else {
					ShatteredPixelDungeon.switchNoFade( StartScene.class );
				}
			}
			
			@Override
			protected boolean onLongClick() {
				//making it easier to start runs quickly while debugging
				if (DeviceCompat.isDebug()) {
					GamesInProgress.selectedClass = null;
					GamesInProgress.curSlot = 1;
					ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
					return true;
				}
				return super.onLongClick();
			}
		};
		btnPlay.icon(Icons.get(Icons.ENTER));
		add(btnPlay);

		//SPS: 继续游戏 —— 快速进入上次游玩的对局（进入序列同 WndGameInProgress.cont）
		final GamesInProgress.Info latestGame = latestSave();
		btnContinue = new StyledButton(GREY_TR, Messages.get(this, "continue")){
			@Override
			protected void onClick() {
				if (latestGame == null) return;

				GamesInProgress.curSlot = latestGame.slot;

				Dungeon.hero = null;
				Dungeon.daily = Dungeon.dailyReplay = false;
				ActionIndicator.clearAction();
				InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
				ShatteredPixelDungeon.switchScene(InterlevelScene.class);
			}
		};
		btnContinue.icon(Icons.get(Icons.DEMON_BLADE));
		btnContinue.visible = latestGame != null; //无存档时隐藏，保持上游原版布局
		add(btnContinue);

		btnSupport = new SupportButton(GREY_TR, Messages.get(this, "support"));
		add(btnSupport);

		btnRankings = new StyledButton(GREY_TR,Messages.get(this, "rankings")){
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.switchNoFade( RankingsScene.class );
			}
		};
		btnRankings.icon(Icons.get(Icons.RANKINGS));
		add(btnRankings);
		Dungeon.daily = Dungeon.dailyReplay = false;

		//SPS: 日志改为左上角小图标（不常用功能收敛）
		btnJournal = new IconButton(Icons.get(Icons.JOURNAL)){
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.switchNoFade( JournalScene.class );
			}
		};
		add(btnJournal);

		//SPS: 礼物商店入口（用户裁决 2026-09-30：放在「改动」行左侧）
		btnGiftShop = new StyledButton(GREY_TR, Messages.get(this, "giftshop")){
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.switchNoFade( GiftShopScene.class );
			}
		};
		btnGiftShop.icon(Icons.get(Icons.BADGES));
		add(btnGiftShop);

		btnChanges = new ChangesButton(GREY_TR, Messages.get(this, "changes"));
		btnChanges.icon(Icons.get(Icons.CHANGES));
		add(btnChanges);

		btnSettings = new SettingsButton(GREY_TR, Messages.get(this, "settings"));
		add(btnSettings);

		//SPS: 关于改为左上角小图标（不常用功能收敛）
		btnAbout = new IconButton(Icons.get(Icons.SHPX)){
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.switchScene( AboutScene.class );
			}
		};
		add(btnAbout);
		
		final int BTN_HEIGHT = 20;
		//SPS: 排行榜/改动/设置各占满一行（避免整体太空旷）；关于与日志为左上角小图标。
		//按钮间距固定 4px（隐藏布局按钮已移除，无需再避让底部）
		int rows = landscape() ? 4 : 5;
		final int GAP = 4;

		float buttonAreaWidth = landscape() ? PixelScene.MIN_WIDTH_L-6 : PixelScene.MIN_WIDTH_P-2;
		float btnAreaLeft = insets.left + (w - buttonAreaWidth) / 2f;

		//SPS: 左上角一行小图标（日志、关于）
		btnJournal.setRect(insets.left + 2, insets.top + 2, 20, 20);
		align(btnJournal);
		btnAbout.setRect(btnJournal.right() + 2, btnJournal.top(), 20, 20);
		align(btnAbout);

		if (landscape()) {
			float rowTop = insets.top + topRegion + GAP / 2f;
			float halfLand = (buttonAreaWidth - 2) / 2f;
			//SPS: 有存档时继续游戏与开始游戏各占半宽、占满第一行（用户裁决 2026-09-30）
			if (btnContinue.visible) {
				btnContinue.setRect(btnAreaLeft, rowTop, halfLand, BTN_HEIGHT);
				align(btnContinue);
				btnPlay.setRect(btnContinue.right()+2, rowTop, halfLand, BTN_HEIGHT);
				align(btnPlay);
			} else {
				btnPlay.setRect(btnAreaLeft, rowTop, buttonAreaWidth, BTN_HEIGHT);
				align(btnPlay);
			}
			float row2 = rowTop + BTN_HEIGHT + GAP;
			btnSupport.setRect(btnAreaLeft, row2, halfLand, BTN_HEIGHT);
			align(btnSupport);
			btnRankings.setRect(btnSupport.right()+2, row2, halfLand, BTN_HEIGHT);
			align(btnRankings);
			//SPS: 礼物商店在「改动」行左侧，各占半宽
			btnGiftShop.setRect(btnAreaLeft, btnRankings.bottom() + GAP, halfLand, BTN_HEIGHT);
			align(btnGiftShop);
			btnChanges.setRect(btnGiftShop.right()+2, btnGiftShop.top(), halfLand, BTN_HEIGHT);
			align(btnChanges);
			btnSettings.setRect(btnAreaLeft, btnChanges.bottom() + GAP, buttonAreaWidth, BTN_HEIGHT);
			align(btnSettings);
		} else {
			float rowTop = insets.top + topRegion + GAP / 2f;
			float half = (buttonAreaWidth - 2) / 2f;
			//SPS: 有存档时继续游戏与开始游戏并排第一行（继续在左）
			if (btnContinue.visible) {
				btnContinue.setRect(btnAreaLeft, rowTop, half, BTN_HEIGHT);
				align(btnContinue);
				btnPlay.setRect(btnContinue.right()+2, rowTop, half, BTN_HEIGHT);
				align(btnPlay);
			} else {
				btnPlay.setRect(btnAreaLeft, rowTop, buttonAreaWidth, BTN_HEIGHT);
				align(btnPlay);
			}
			btnSupport.setRect(btnAreaLeft, btnPlay.bottom() + GAP, buttonAreaWidth, BTN_HEIGHT);
			align(btnSupport);
			btnRankings.setRect(btnAreaLeft, btnSupport.bottom() + GAP, buttonAreaWidth, BTN_HEIGHT);
			align(btnRankings);
			//SPS: 礼物商店在「改动」行左侧，各占半宽
			btnGiftShop.setRect(btnAreaLeft, btnRankings.bottom() + GAP, half, BTN_HEIGHT);
			align(btnGiftShop);
			btnChanges.setRect(btnGiftShop.right()+2, btnGiftShop.top(), half, BTN_HEIGHT);
			align(btnChanges);
			btnSettings.setRect(btnAreaLeft, btnChanges.bottom() + GAP, buttonAreaWidth, BTN_HEIGHT);
			align(btnSettings);
		}

		version = new BitmapText( "v" + Game.version, pixelFont);
		version.measure();
		version.hardlight( 0x888888 );
		version.x = insets.left + w - version.width() - (DeviceCompat.isDesktop() ? 4 : 8);
		version.y = insets.top + h - version.height() - (DeviceCompat.isDesktop() ? 2 : 4);
		add( version );

		if (DeviceCompat.isDesktop()) {
			btnExit = new ExitButton();
			btnExit.setPos( w - btnExit.width(), 0 );
			add( btnExit );
		}

		Badges.loadGlobal();
		if (Badges.isUnlocked(Badges.Badge.VICTORY) && !SPDSettings.victoryNagged()) {
			SPDSettings.victoryNagged(true);
			add(new WndVictoryCongrats());
		}

		fadeIn();
	}

	/** Shared by the title button and the desktop framebuffer smoke test. */
	public static void prepareTutorial() {
		Dungeon.hero = null;
		Dungeon.daily = Dungeon.dailyReplay = false;
		Dungeon.initSeed();
		GamesInProgress.curSlot = 0;
		GamesInProgress.selectedClass = HeroClass.NEWPLAYER;
		GamesInProgress.selectedSkin = 0;
		InterlevelScene.mode = InterlevelScene.Mode.LEARN;
	}

	private float uiAlpha;

	public void updateFade() {
		float alpha = GameMath.gate(0f, uiAlpha, 1f);

		title.am = alpha;
		leftFB.am = alpha;
		rightFB.am = alpha;
		//signs.am = alpha; handles this itself

		btnPlay.enable(alpha != 0);
		btnSupport.enable(alpha != 0);
		btnRankings.enable(alpha != 0);
		btnJournal.enable(alpha != 0);
		btnGiftShop.enable(alpha != 0);
		btnChanges.enable(alpha != 0);
		btnSettings.enable(alpha != 0);
		btnAbout.enable(alpha != 0);

		btnPlay.alpha(alpha);
		btnSupport.alpha(alpha);
		btnRankings.alpha(alpha);
		btnJournal.visible = alpha > 0;   //IconButton 无 alpha(float)，用显隐跟随淡出
		btnGiftShop.alpha(alpha);
		btnChanges.alpha(alpha);
		btnSettings.alpha(alpha);
		btnAbout.visible = alpha > 0;

		//SPS: 继续游戏按钮随 UI 淡出
		if (btnContinue != null && btnContinue.visible){
			btnContinue.enable(alpha != 0);
			btnContinue.alpha(alpha);
		}

		version.alpha(alpha);
		if (btnExit != null){
			btnExit.enable(alpha != 0);
			btnExit.icon().alpha(alpha);
		}

	}

	private Fireball placeTorch(float x, float y ) {
		Fireball fb = new Fireball();
		fb.x = x - fb.width()/2f;
		fb.y = y - fb.height();

		align(fb);
		add( fb );
		return fb;
	}

	//SPS: 上次游玩的对局（lastPlayed 最大的非空存档；无存档返回 null）
	private static GamesInProgress.Info latestSave(){
		GamesInProgress.Info latest = null;
		for (GamesInProgress.Info info : GamesInProgress.checkAll()){
			if (latest == null || info.lastPlayed > latest.lastPlayed){
				latest = info;
			}
		}
		return latest;
	}

	private static class ChangesButton extends StyledButton {

		public ChangesButton( Chrome.Type type, String label ){
			super(type, label);
			if (SPDSettings.updates()) Updates.checkForUpdate();
		}

		boolean updateShown = false;

		@Override
		public void update() {
			super.update();

			if (!updateShown && Updates.updateAvailable()){
				updateShown = true;
				text(Messages.get(TitleScene.class, "update"));
			}

			if (updateShown){
				textColor(ColorMath.interpolate( 0xFFFFFF, Window.SHPX_COLOR, 0.5f + (float)Math.sin(Game.timeTotal*5)/2f));
			}
		}

		@Override
		protected void onClick() {
			if (Updates.updateAvailable()){
				AvailableUpdateData update = Updates.updateData();

				ShatteredPixelDungeon.scene().addToFront( new WndOptions(
						Icons.get(Icons.CHANGES),
						update.versionName == null ? Messages.get(this,"title") : Messages.get(this,"versioned_title", update.versionName),
						update.desc == null ? Messages.get(this,"desc") : update.desc,
						Messages.get(this,"update"),
						Messages.get(this,"changes")
				) {
					@Override
					protected void onSelect(int index) {
						if (index == 0) {
							Updates.launchUpdate(Updates.updateData());
						} else if (index == 1){
							ChangesScene.changesSelected = 0;
							ShatteredPixelDungeon.switchNoFade( ChangesScene.class );
						}
					}
				});

			} else {
				ChangesScene.changesSelected = 0;
				ShatteredPixelDungeon.switchNoFade( ChangesScene.class );
			}
		}

	}

	private static class SettingsButton extends StyledButton {

		public SettingsButton( Chrome.Type type, String label ){
			super(type, label);
			if (Messages.lang().status() == Languages.Status.X_UNFINISH){
				icon(Icons.get(Icons.LANGS));
				icon.hardlight(1.5f, 0, 0);
			} else {
				icon(Icons.get(Icons.PREFS));
			}
		}

		@Override
		public void update() {
			super.update();

			if (Messages.lang().status() == Languages.Status.X_UNFINISH){
				textColor(ColorMath.interpolate( 0xFFFFFF, CharSprite.NEGATIVE, 0.5f + (float)Math.sin(Game.timeTotal*5)/2f));
			}
		}

		@Override
		protected void onClick() {
			if (Messages.lang().status() == Languages.Status.X_UNFINISH){
				WndSettings.last_index = 5;
			}
			ShatteredPixelDungeon.scene().add(new WndSettings());
		}
	}

	private static class SupportButton extends StyledButton{

		public SupportButton( Chrome.Type type, String label ){
			super(type, label);
			//SPS: 图标改为幸运徽章（音频/社区入口）
			icon(Icons.get(Icons.LUCKY_BADGE));
			//SPS: 支持游戏开发改为白色字体
			textColor( 0xFFFFFF );
		}

		@Override
		protected void onClick() {
			ShatteredPixelDungeon.switchNoFade(SupporterScene.class);
		}
	}
}
