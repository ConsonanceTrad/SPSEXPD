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

import pd.Assets;
import pd.Dungeon;
import pd.GamesInProgress;
import pd.ShatteredPixelDungeon;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.messages.Messages;
import pd.scenes.InterlevelScene;
import pd.scenes.PixelScene;
import pd.scenes.StartScene;
import pd.sprites.HeroSprite;
import pd.ui.ActionIndicator;
import pd.ui.IconButton;
import pd.ui.Icons;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import pd.utils.DungeonSeed;
import pd.utils.GLog;
import render.noosa.Game;
import render.noosa.Image;

import java.util.Locale;
import pd.messages.InlineText;

public class WndGameInProgress extends Window {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndGameInProgress.class)
			.t("title", "%1$d级%2$s")
			.t("challenges", "挑战")
			.t("exp", "经验")
			.t("str", "力量")
			.t("health", "生命")
			.t("gold", "金币收集数")
			.t("depth", "最高层数")
			.t("current_depth", "当前楼层")
			.t("dungeon_seed", "地牢种子")
			.t("custom_seed", "_自定义种子_")
			.t("daily_for", "_日常挑战于_")
			.t("replay_for", "_重玩于_")
			.t("continue", "继续")
			.t("erase", "删除")
			.t("erase_warn_title", "你确定要删除这个存档吗？")
			.t("erase_warn_body", "你将失去这局游戏的所有进度")
			.t("erase_warn_yes", "是，删除这个存档")
			.t("erase_warn_no", "不，我要接着玩")
			.t("rename_title", "为这个角色命名")
			.t("rename_desc", "给这名冒险者起一个名字，会显示在存档列表、角色详情与排行榜上。_留空表示清除，仍使用职业名。_")
			.t("export", "导出存档")
			.t("export_title", "导出存档")
			.t("export_desc", "输入导出文件的完整路径（JSON 格式，包含该存档的所有楼层数据）。_")
			.t("export_ok", "导出")
			.t("export_cancel", "取消")
			.t("export_done", "已导出到：%1$s")
			.t("export_failed", "导出失败。")
			.t("rename_ok", "确定")
			.t("rename_clear", "清除");
	}



	
	private static final int WIDTH    = 120;
	
	private int GAP	  = 6;
	
	private float pos;
	
	public WndGameInProgress(final int slot){
		
		final GamesInProgress.Info info = GamesInProgress.check(slot);
		
		String className = null;
		if (info.subClass != HeroSubClass.NONE){
			className = info.subClass.title();
		} else {
			className = info.heroClass.title();
		}
		//SPSEXPD: 附上自定义角色名
		if (info.heroName != null && !info.heroName.isEmpty()){
			className = className + "·" + info.heroName;
		}
		
		IconTitle title = new IconTitle();
		title.icon( HeroSprite.avatar(info.heroClass, info.armorTier, info.skin) );
		title.label((Messages.get(this, "title", info.level, className)).toUpperCase(Locale.ENGLISH));
		title.color(Window.TITLE_COLOR);
		//SPSEXPD: 名称行右端留出改名按钮
		title.setRect( 0, 0, WIDTH - 16, 0 );
		add(title);

		//SPSEXPD: 名称行右端的书本按钮 —— 为这个存档改名
		IconButton btnRename = new IconButton( new Image(Assets.Interfaces.NAMESHOW) ){
			@Override
			protected void onClick(){
				super.onClick();
				//SPSEXPD: 清掉 IconButton 的按压高亮残留
				icon().resetColor();
				rename( slot );
			}

			@Override
			protected String hoverText(){
				return Messages.get(WndGameInProgress.class, "rename_title");
			}
		};
		btnRename.setRect( WIDTH - 16, 0, 16, 16 );
		add(btnRename);
		
		if (info.challenges > 0) GAP -= 2;
		
		pos = title.bottom() + GAP;
		
		if (info.challenges > 0) {
			RedButton btnChallenges = new RedButton( Messages.get(this, "challenges") ) {
				@Override
				protected void onClick() {
					Game.scene().add( new WndChallenges( info.challenges, false ) );
				}
			};
			btnChallenges.icon(Icons.get(Icons.CHALLENGE_COLOR));
			float btnW = btnChallenges.reqWidth() + 2;
			btnChallenges.setRect( (WIDTH - btnW)/2, pos, btnW , 18 );
			add( btnChallenges );
			
			pos = btnChallenges.bottom() + GAP;
		}
		
		pos += GAP;

		int strBonus = info.strBonus;
		if (strBonus > 0)           statSlot( Messages.get(this, "str"), info.str + " + " + strBonus );
		else if (strBonus < 0)      statSlot( Messages.get(this, "str"), info.str + " - " + -strBonus );
		else                        statSlot( Messages.get(this, "str"), info.str );
		if (info.shld > 0)  statSlot( Messages.get(this, "health"), info.hp + "+" + info.shld + "/" + info.ht );
		else                statSlot( Messages.get(this, "health"), (info.hp) + "/" + info.ht );
		statSlot( Messages.get(this, "exp"), info.exp + "/" + Hero.maxExp(info.level) );
		
		pos += GAP;
		statSlot( Messages.get(this, "gold"), info.goldCollected );
		//SPSEXPD: 当前楼层从存档槽位移到这里显示
		statSlot( Messages.get(this, "current_depth"), info.depth );
		statSlot( Messages.get(this, "depth"), info.maxDepth );
		if (info.daily) {
			if (info.dailyReplay) {
				statSlot(Messages.get(this, "replay_for"), "_" + info.customSeed + "_");
			} else {
				statSlot(Messages.get(this, "daily_for"), "_" + info.customSeed + "_");
			}
		} else if (!info.customSeed.isEmpty()){
			statSlot( Messages.get(this, "custom_seed"), "_" + info.customSeed + "_" );
		} else {
			statSlot( Messages.get(this, "dungeon_seed"), DungeonSeed.convertToCode(info.seed) );
		}
		
		pos += GAP;
		
		RedButton cont = new RedButton(Messages.get(this, "continue")){
			@Override
			protected void onClick() {
				super.onClick();
				
				GamesInProgress.curSlot = slot;
				
				Dungeon.hero = null;
				Dungeon.daily = Dungeon.dailyReplay = false;
				ActionIndicator.clearAction();
				InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
				ShatteredPixelDungeon.switchScene(InterlevelScene.class);
			}
		};
		
		RedButton erase = new RedButton( Messages.get(this, "erase")){
			@Override
			protected void onClick() {
				super.onClick();
				
				ShatteredPixelDungeon.scene().add(new WndOptions(Icons.get(Icons.WARNING),
						Messages.get(WndGameInProgress.class, "erase_warn_title"),
						Messages.get(WndGameInProgress.class, "erase_warn_body"),
						Messages.get(WndGameInProgress.class, "erase_warn_yes"),
						Messages.get(WndGameInProgress.class, "erase_warn_no") ) {
					@Override
					protected void onSelect( int index ) {
						if (index == 0) {
							Dungeon.deleteGame(slot, true);
							ShatteredPixelDungeon.switchNoFade(StartScene.class);
						}
					}
				} );
			}
		};

		cont.icon(Icons.get(Icons.ENTER));
		cont.setRect(0, pos, WIDTH/2 -1, 20);
		add(cont);

		erase.icon(Icons.get(Icons.CLOSE));
		erase.setRect(WIDTH/2 + 1, pos, WIDTH/2 - 1, 20);
		add(erase);

		pos = (int)cont.bottom() + GAP;

		//SPSEXPD: 导出这个存档（docs/alchemy-rework.md §9）
		RedButton export = new RedButton(Messages.get(this, "export")) {
			@Override
			protected void onClick() {
				super.onClick();
				promptExport(slot);
			}
		};
		export.setRect(0, pos, WIDTH, 20);
		add(export);

		resize(WIDTH, (int)export.bottom()+1);
	}
	
	/**
	 * SPSEXPD: 导出这个存档（docs/alchemy-rework.md §9）。
	 * 安卓端直接写到系统 Download；桌面端弹输入框让玩家确认/修改路径。
	 */
	private void promptExport(final int slot) {
		if (pd.GameSaves.isAndroid()) {
			runExport(slot, pd.GameSaves.defaultExportName(slot));
			return;
		}
		ShatteredPixelDungeon.scene().addToFront(new WndTextInput(
				Messages.get(WndGameInProgress.class, "export_title"),
				Messages.get(WndGameInProgress.class, "export_desc"),
				pd.GameSaves.suggestDesktopPath(slot), 240, false,
				Messages.get(WndGameInProgress.class, "export_ok"),
				Messages.get(WndGameInProgress.class, "export_cancel")) {
			@Override
			public void onSelect(boolean positive, String text) {
				if (!positive || text == null || text.trim().isEmpty()) return;
				runExport(slot, text.trim());
			}
		});
	}

	private void runExport(int slot, String path) {
		try {
			String written = pd.GameSaves.exportSlot(slot, path);
			GLog.p(Messages.get(WndGameInProgress.class, "export_done", written));
		} catch (java.io.IOException e) {
			GLog.w(e.getMessage() == null
					? Messages.get(WndGameInProgress.class, "export_failed")
					: e.getMessage());
		}
	}

	//SPSEXPD: 改名（名称行右端书本按钮）—— 写入存档并重开存档列表以刷新显示
	private void rename( final int slot ){		GamesInProgress.Info info = GamesInProgress.check(slot);
		String current = (info != null && info.heroName != null) ? info.heroName : "";
		ShatteredPixelDungeon.scene().addToFront(new WndTextInput(
				Messages.get(WndGameInProgress.class, "rename_title"),
				Messages.get(WndGameInProgress.class, "rename_desc"),
				current, 12, false,
				Messages.get(WndGameInProgress.class, "rename_ok"),
				Messages.get(WndGameInProgress.class, "rename_clear")){
			@Override
			public void onSelect(boolean positive, String text){
				String name = (positive && text != null) ? text.trim() : "";
				GamesInProgress.setHeroName( slot, name );
				ShatteredPixelDungeon.seamlessResetScene();
			}
		});
	}

	private void statSlot( String label, String value ) {
		int size = 8;
		RenderedTextBlock txt;
		do {
			txt = PixelScene.renderTextBlock( label, size );
			size--;
		} while (txt.width() >= WIDTH * 0.55f);
		txt.setPos(0, pos + (6 - txt.height())/2);
		PixelScene.align(txt);
		add( txt );

		size = 8;
		do {
			txt = PixelScene.renderTextBlock( value, size );
			size--;
		} while (txt.width() >= WIDTH * 0.45f);
		txt.setPos(WIDTH * 0.55f, pos + (6 - txt.height())/2);
		PixelScene.align(txt);
		add( txt );
		
		pos += GAP + txt.height();
	}
	
	private void statSlot( String label, int value ) {
		statSlot( label, Integer.toString( value ) );
	}
}
