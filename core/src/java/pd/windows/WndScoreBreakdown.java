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

import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.Statistics;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.ui.Icons;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import render.noosa.Group;

import java.text.NumberFormat;
import pd.messages.InlineText;

public class WndScoreBreakdown extends Window {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndScoreBreakdown.class)
			.t("title", "得分详情")
			.t("progress_title", "游戏")
			.t("progress_desc", "基于最深层数与英雄等级。")
			.t("treasure_title", "寻宝")
			.t("treasure_desc", "基于收集的金币与随身物品的价值。")
			.t("treasure_desc_old", "基于收集的金币。")
			.t("explore_title", "探索")
			.t("explore_desc", "基于已探索的楼层。遗漏物品、隐藏门道与未解谜题会降低分数。")
			.t("bosses_title", "Boss战")
			.t("bosses_desc", "基于已击败的boss。在boss战中受到可规避的攻击会降低分数。")
			.t("quests_title", "任务")
			.t("quests_desc", "基于已完成的任务。受到可规避的攻击或未完成任务目标会降低分数。")
			.t("win_multiplier", "通关倍率")
			.t("challenge_multiplier", "挑战倍率")
			.t("total", "总分")
			.t("old_score_desc", "在v1.3版本之前开始的游戏得分项目较少，但来自进度项目的得分会增加约50%，并且来自寻宝项目的得分上限也会增加。");
	}




	private static final int WIDTH			= 115;

	private int GAP	= 4;

	public WndScoreBreakdown(){

		IconTitle title = new IconTitle( Icons.get(Icons.INFO), Messages.get(this, "title"));
		title.setRect(0, 0, WIDTH, 16);
		add(title);

		float pos = title.bottom()+2;

		NumberFormat num = NumberFormat.getInstance(Messages.locale());
		if (Dungeon.initialVersion > ShatteredPixelDungeon.v1_2_3) {
			pos = statSlot(this, Messages.get(this, "progress_title"),
					num.format(Statistics.progressScore), pos, Statistics.progressScore >= 50_000);
			pos = addInfo(this, Messages.get(this, "progress_desc"), pos);
			pos = statSlot(this, Messages.get(this, "treasure_title"),
					num.format(Statistics.treasureScore), pos, Statistics.treasureScore >= 20_000);
			pos = addInfo(this, Messages.get(this, "treasure_desc"), pos);
			pos = statSlot(this, Messages.get(this, "explore_title"),
					num.format(Statistics.exploreScore), pos, Statistics.exploreScore >= 20_000);
			pos = addInfo(this, Messages.get(this, "explore_desc"), pos);
			pos = statSlot(this, Messages.get(this, "bosses_title"),
					num.format(Statistics.totalBossScore), pos, Statistics.totalBossScore >= 15_000);
			pos = addInfo(this, Messages.get(this, "bosses_desc"), pos);
			pos = statSlot(this, Messages.get(this, "quests_title"),
					num.format(Statistics.totalQuestScore), pos, Statistics.totalQuestScore >= 10_000);
			pos = addInfo(this, Messages.get(this, "quests_desc"), pos);
		} else {
			pos = statSlot(this, Messages.get(this, "progress_title"),
					num.format(Statistics.progressScore), pos, Statistics.progressScore >= 78_000);
			pos = addInfo(this, Messages.get(this, "progress_desc"), pos);
			pos = statSlot(this, Messages.get(this, "treasure_title"),
					num.format(Statistics.treasureScore), pos, Statistics.treasureScore >= 30_000);
			pos = addInfo(this, Messages.get(this, "treasure_desc_old"), pos);
		}

		if (Statistics.winMultiplier > 1) {
			pos = statSlot(this, Messages.get(this, "win_multiplier"), Messages.decimalFormat("#.##", Statistics.winMultiplier) + "x", pos, false);
		}
		if (Statistics.chalMultiplier > 1) {
			pos = statSlot(this, Messages.get(this, "challenge_multiplier"), Messages.decimalFormat("#.##", Statistics.chalMultiplier) + "x", pos, false);
		}
		pos = statSlot(this, Messages.get(this, "total"), num.format(Statistics.totalScore), pos, false);

		if (Dungeon.initialVersion <= ShatteredPixelDungeon.v1_2_3){
			pos = addInfo(this, Messages.get(this, "old_score_desc"), pos);
		}

		resize(WIDTH, (int)pos);

	}

	private float statSlot(Group parent, String label, String value, float pos, boolean highlight ) {

		RenderedTextBlock txt = PixelScene.renderTextBlock( label, 7 );
		if (highlight) txt.hardlight(Window.TITLE_COLOR);
		txt.setPos(0, pos);
		parent.add( txt );

		txt = PixelScene.renderTextBlock( value, 7 );
		if (highlight) txt.hardlight(Window.TITLE_COLOR);
		txt.setPos(WIDTH * 0.7f, pos);
		PixelScene.align(txt);
		parent.add( txt );

		return pos + GAP + txt.height();
	}

	private float addInfo(Group parent, String info, float pos){

		RenderedTextBlock txt = PixelScene.renderTextBlock( info, 5 );
		txt.maxWidth(WIDTH);
		txt.hardlight(0x999999);
		txt.setPos(0, pos-2);
		parent.add( txt );

		return pos - 2 + GAP + txt.height();

	}


}
