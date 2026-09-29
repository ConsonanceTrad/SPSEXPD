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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.effects.Fireball;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.services.news.News;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.AvailableUpdateData;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.Updates;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSettings;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndVictoryCongrats;
import com.watabou.glwrap.Blending;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.ColorMath;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.GameMath;
import com.watabou.utils.RectF;

import java.util.Date;

public class TitleScene extends PixelScene {

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
			float third = (buttonAreaWidth - 4) / 3f;
			float rowTop = insets.top + topRegion + GAP / 2f;
			//SPS: 有存档时继续游戏与开始游戏并排第一行（继续在左）
			if (btnContinue.visible) {
				btnContinue.setRect(btnAreaLeft, rowTop, third, BTN_HEIGHT);
				align(btnContinue);
				btnPlay.setRect(btnContinue.right()+2, rowTop, third, BTN_HEIGHT);
				align(btnPlay);
				btnSupport.setRect(btnPlay.right()+2, rowTop, third, BTN_HEIGHT);
				align(btnSupport);
			} else {
				btnPlay.setRect(btnAreaLeft, rowTop, third, BTN_HEIGHT);
				align(btnPlay);
				btnSupport.setRect(btnPlay.right()+2, rowTop, third, BTN_HEIGHT);
				align(btnSupport);
				btnRankings.setRect(btnSupport.right()+2, rowTop, third, BTN_HEIGHT);
				align(btnRankings);
			}
			float row2 = rowTop + BTN_HEIGHT + GAP;
			btnRankings.setRect(btnAreaLeft, row2, buttonAreaWidth, BTN_HEIGHT);
			align(btnRankings);
			btnChanges.setRect(btnAreaLeft, btnRankings.bottom() + GAP, buttonAreaWidth, BTN_HEIGHT);
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
			btnChanges.setRect(btnAreaLeft, btnRankings.bottom() + GAP, buttonAreaWidth, BTN_HEIGHT);
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
		btnChanges.enable(alpha != 0);
		btnSettings.enable(alpha != 0);
		btnAbout.enable(alpha != 0);

		btnPlay.alpha(alpha);
		btnSupport.alpha(alpha);
		btnRankings.alpha(alpha);
		btnJournal.visible = alpha > 0;   //IconButton 无 alpha(float)，用显隐跟随淡出
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

	private static class NewsButton extends StyledButton {

		public NewsButton(Chrome.Type type, String label ){
			super(type, label);
			if (SPDSettings.news()) News.checkForNews();
		}

		int unreadCount = -1;

		@Override
		public void update() {
			super.update();

			if (unreadCount == -1 && News.articlesAvailable()){
				long lastRead = SPDSettings.newsLastRead();
				if (lastRead == 0){
					if (News.articles().get(0) != null) {
						SPDSettings.newsLastRead(News.articles().get(0).date.getTime());
					}
				} else {
					unreadCount = News.unreadArticles(new Date(SPDSettings.newsLastRead()));
					if (unreadCount > 0) {
						unreadCount = Math.min(unreadCount, 9);
						text(text() + "(" + unreadCount + ")");
					}
				}
			}

			if (unreadCount > 0){
				textColor(ColorMath.interpolate( 0xFFFFFF, Window.SHPX_COLOR, 0.5f + (float)Math.sin(Game.timeTotal*5)/2f));
			}
		}

		@Override
		protected void onClick() {
			super.onClick();
			ShatteredPixelDungeon.switchNoFade( NewsScene.class );
		}
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
