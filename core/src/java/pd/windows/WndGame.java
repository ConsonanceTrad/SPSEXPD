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

package pd.windows;

import pd.Challenges;
import pd.Dungeon;
import pd.GamesInProgress;
import pd.SPDSettings;
import pd.ShatteredPixelDungeon;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.HeroSelectScene;
import pd.scenes.InterlevelScene;
import pd.scenes.RankingsScene;
import pd.scenes.TitleScene;
import pd.ui.Icons;
import pd.ui.RedButton;
import pd.ui.Window;
import render.noosa.Game;
import render.utils.platform.DeviceCompat;

import java.io.IOException;
import pd.messages.InlineText;

public class WndGame extends Window {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndGame.class)
			.t("settings", "设置")
			.t("challenges", "挑战")
			.t("rankings", "排行榜")
			.t("start", "踏上征途")
			.t("menu", "主菜单")
			.t("exit", "退出游戏")
			.t("return", "继续冒险")
			.t("debug_items", "调试物品")
		.t("debug_mobs", "召唤怪物")
		.t("debug_perks", "调试特质")
		.t("debug_time", "时间 +6 小时")
		.t("time_advanced", "游戏内时间推进了 6 小时。");
	}




	private static final int WIDTH		= 120;
	private static final int BTN_HEIGHT	= 20;
	private static final int GAP		= 2;
	
	private int pos;
	
	public WndGame() {
		
		super();

		//settings
		RedButton curBtn;
		addButton( curBtn = new RedButton( Messages.get(this, "settings") ) {
			@Override
			protected void onClick() {
				hide();
				GameScene.show(new WndSettings());
			}
		});
		curBtn.icon(Icons.get(Icons.PREFS));

		//SPS: 调试物品工具（原创缺口）。只要开启「测试时间」挑战就显示，与构建类型无关
		if (Dungeon.isChallenged(Challenges.TEST_TIME)) {
			addButton( curBtn = new RedButton( Messages.get(this, "debug_items") ) {
				@Override
				protected void onClick() {
					hide();
					GameScene.show( new WndDebugItems() );
				}
			} );
			curBtn.icon(new render.noosa.Image(pd.Assets.Interfaces.ADD_THINGS));

			//SPS: 调试怪物工具（原创缺口）。在英雄身旁召唤任意怪物，协助测试战斗/特效/新怪
			addButton( curBtn = new RedButton( Messages.get(this, "debug_mobs") ) {
				@Override
				protected void onClick() {
					hide();
					GameScene.show( new WndDebugMobs() );
				}
			} );
			curBtn.icon(new render.noosa.Image(pd.Assets.Interfaces.ADD_THINGS));

			//SPS: 调试特质工具（原创缺口）。直接给英雄授予任意特质，便于测试特质效果
			addButton( curBtn = new RedButton( Messages.get(this, "debug_perks") ) {
				@Override
				protected void onClick() {
					hide();
					GameScene.show( new WndDebugPerks() );
				}
			} );
			curBtn.icon(new render.noosa.Image(pd.Assets.Interfaces.ADD_THINGS));

			//SPS: 时间推进（原创缺口）。游戏内时间 +6 小时，用来测试昼夜（夜晚视野、向日葵传送、满月之力等）。
			//不关窗，方便连点推进到想要的时刻。
			addButton( curBtn = new RedButton( Messages.get(this, "debug_time") ) {
				@Override
				protected void onClick() {
					pd.Statistics.advanceSpsHours( 6f );
					pd.utils.GLog.i( Messages.get( WndGame.class, "time_advanced" ) );
				}
			} );
			curBtn.icon(new render.noosa.Image(pd.Assets.Interfaces.ADD_THINGS));
		}

		// Challenges window
		if (Dungeon.challenges > 0) {
			addButton( curBtn = new RedButton( Messages.get(this, "challenges") ) {
				@Override
				protected void onClick() {
					hide();
					GameScene.show( new WndChallenges( Dungeon.challenges, false ) );
				}
			} );
			curBtn.icon(Icons.get(Icons.CHALLENGE_COLOR));
		}

		// Restart
		if (Dungeon.hero == null || !Dungeon.hero.isAlive()) {

			addButton( curBtn = new RedButton( Messages.get(this, "start") ) {
				@Override
				protected void onClick() {
					GamesInProgress.selectedClass = Dungeon.hero.heroClass;
					GamesInProgress.selectedSkin = Dungeon.hero.skin;
					GamesInProgress.selectedStyle = Dungeon.hero.combatStyle;
					GamesInProgress.curSlot = GamesInProgress.firstEmpty();
					ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
				}
			} );
			curBtn.icon(Icons.get(Icons.ENTER));
			curBtn.textColor(Window.TITLE_COLOR);
			
			addButton( curBtn = new RedButton( Messages.get(this, "rankings") ) {
				@Override
				protected void onClick() {
					InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
					Game.switchScene( RankingsScene.class );
				}
			} );
			curBtn.icon(Icons.get(Icons.RANKINGS));
		}

		// Main menu
		addButton(curBtn = new RedButton(Messages.get(this, "menu")) {
			@Override
			protected void onClick() {
				try {
					Dungeon.saveAll();
				} catch (IOException e) {
					ShatteredPixelDungeon.reportException(e);
				}
				Game.switchScene(TitleScene.class);
			}
		});
		curBtn.icon(Icons.get(Icons.DISPLAY));
		if (SPDSettings.intro()) curBtn.enable(false);

		resize( WIDTH, pos );
	}
	
	private void addButton( RedButton btn ) {
		add( btn );
		btn.setRect( 0, pos > 0 ? pos += GAP : 0, WIDTH, BTN_HEIGHT );
		pos += BTN_HEIGHT;
	}

	private void addButtons( RedButton btn1, RedButton btn2 ) {
		add( btn1 );
		btn1.setRect( 0, pos > 0 ? pos += GAP : 0, (WIDTH - GAP) / 2, BTN_HEIGHT );
		add( btn2 );
		btn2.setRect( btn1.right() + GAP, btn1.top(), WIDTH - btn1.right() - GAP, BTN_HEIGHT );
		pos += BTN_HEIGHT;
	}
}
