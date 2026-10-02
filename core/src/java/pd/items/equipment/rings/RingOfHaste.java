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

public class RingOfHaste extends Ring {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RingOfHaste.class)
			.t("name", "疾速戒指")
			.t("stats", "佩戴这枚戒指时，你的移动速度会提升_%s%%_。")
			.t("typical_stats", "佩戴这枚戒指时，你的移动速度通常会提升_%s%%_。")
			.t("combined_stats", "你已装备的戒指正联结它们的力量，一共增加了_%s%%_的移动速度。")
			.t("upgrade_stat_name_1", "移速加成")
			.t("desc", "这枚戒指减轻了配戴者在移动时的负担，使其能够飞速奔跑。在30级时这枚戒指效果达到上限。");
	}


	{
		icon = ItemIconSheet.RING_HASTE;
		buffClass = Haste.class;
	}

	public String statsInfo() {
		if (isIdentified()){
			return Messages.get(this, "stats",
					Messages.decimalFormat("#.##", 100f * Math.min(3f, level() * 0.1f)));
		} else {
			return "???";
		}
	}

	public String upgradeStat1(int level){
		return Messages.decimalFormat("#.##", 100f * Math.min(3f, level * 0.1f)) + "%";
	}
	
	@Override
	protected RingBuff buff( ) {
		return new Haste();
	}
	
	public static float speedMultiplier( Char target ){
		int bonus = getBonus(target, Haste.class);
		return bonus >= 30 ? 4f : 1f + bonus / 10f;
	}
	
	public class Haste extends RingBuff {
		@Override public int level() { return RingOfHaste.this.level(); }
		@Override public int buffedLvl() { return level(); }
	}
}
