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

public class RingOfFuror extends Ring {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RingOfFuror.class)
			.t("name", "狂怒戒指")
			.t("stats", "佩戴这枚戒指时，你的武器的攻击速度会提升_%1$s%%_，你的基础伤害提升_%2$s_点。")
			.t("typical_stats", "佩戴这枚戒指时，你的攻击速度通常会提升_%s%%_。")
			.t("combined_stats", "你已装备的戒指正联结它们的力量，一共增加了_%s%%_的攻击速度。")
			.t("upgrade_stat_name_1", "攻速加成")
			.t("desc", "这枚戒指会激发配戴者内心的怒火，使其能够更有力更迅猛地攻击。在30级时这枚戒指效果达到上限。");
	}




	{
		icon = ItemIconSheet.RING_FUROR;
		buffClass = Furor.class;
	}

	public String statsInfo() {
		if (isIdentified()){
			return Messages.get(this, "stats",
					Messages.decimalFormat("#.##", Math.min(300f, level() * 10f)), level());
		} else {
			return "???";
		}
	}

	public String upgradeStat1(int level){
		return Messages.decimalFormat("#.##", Math.min(300f, level * 10f)) + "%";
	}

	@Override
	protected RingBuff buff( ) {
		return new Furor();
	}
	
	public static float attackSpeedMultiplier(Char target ){
		return Math.min(4f, 1f + getBonus(target, Furor.class) / 10f);
	}

	public static int damageBonus(Char target) {
		return Math.max(0, getBonus(target, Furor.class));
	}

	public class Furor extends RingBuff {
		@Override public int level() { return RingOfFuror.this.level(); }
		@Override public int buffedLvl() { return level(); }
	}
}
