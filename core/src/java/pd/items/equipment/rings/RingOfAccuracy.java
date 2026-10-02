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

package pd.items.equipment.rings;

import pd.actors.Char;
import pd.messages.Messages;
import pd.sprites.ItemIconSheet;
import pd.messages.InlineText;

public class RingOfAccuracy extends Ring {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RingOfAccuracy.class)
			.t("name", "精准戒指")
			.t("stats", "佩戴这枚戒指时，你的精准度会增加_%1$d_点，攻击距离会增加_%2$d_格。")
			.t("typical_stats", "佩戴这枚戒指时，你的精准属性通常会增加_%s%%_。")
			.t("combined_stats", "你已装备的戒指正联结它们的力量，一共增加了你_%s%%_的精准。")
			.t("upgrade_stat_name_1", "精准加成")
			.t("desc", "这枚戒指提高了你的专注力，使敌人难以躲闪你的攻击。该戒指每10级提供1格额外的攻击距离，但在30级效果达到上限。");
	}


	{
		icon = ItemIconSheet.RING_ACCURACY;
		buffClass = Accuracy.class;
	}
	
	public String statsInfo() {
		if (isIdentified()){
			return Messages.get(this, "stats", level(), Math.min(3, level() / 10));
		} else {
			return "???";
		}
	}

	public String upgradeStat1(int level){
		return Integer.toString(level);
	}
	
	@Override
	protected RingBuff buff( ) {
		return new Accuracy();
	}
	
	public static float accuracyMultiplier( Char target ){
		return (float)Math.pow(0.75, -getBonus(target, Accuracy.class));
	}

	public static int reachBonus(Char target) {
		int bonus = 0;
		for (Accuracy buff : target.buffs(Accuracy.class)) bonus += Math.min(buff.level(), 30);
		return bonus / 10;
	}
	
	public class Accuracy extends RingBuff {
		@Override public int level() { return RingOfAccuracy.this.level(); }
		@Override public int buffedLvl() { return level(); }
	}
}
