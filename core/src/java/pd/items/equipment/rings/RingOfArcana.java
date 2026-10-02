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

import pd.Dungeon;
import pd.actors.Char;
import pd.messages.Messages;
import pd.sprites.ItemIconSheet;
import pd.messages.InlineText;

public class RingOfArcana extends Ring {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RingOfArcana.class)
			.t("name", "奥术之戒")
			.t("stats", "佩戴这枚戒指时，你的附魔和刻印效果会提升_%s%%_。")
			.t("typical_stats", "佩戴这枚戒指时，你的附魔和刻印效果通常会提升_%s%%_。")
			.t("combined_stats", "你已装备的戒指正联结它们的力量，一共增强了_%s%%_的附魔与刻印效果。")
			.t("upgrade_stat_name_1", "奥术加成")
			.t("desc", "这枚戒指增强了武器上的附魔和护甲上的刻印，也包括诅咒！戒指会先提高它们的触发概率，当触发概率达到100%时，会转而增加它们效果的强度。被诅咒的戒指则会反过来削弱附魔、刻印以及诅咒。");
	}




	{
		icon = ItemIconSheet.RING_ARCANA;
		buffClass = Arcana.class;
	}

	public String statsInfo() {
		if (isIdentified()){
			String info = Messages.get(this, "stats",
					Messages.decimalFormat("#.##", 100f * (Math.pow(1.175f, soloBuffedBonus()) - 1f)));
			if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)){
				info += "\n\n" + Messages.get(this, "combined_stats",
						Messages.decimalFormat("#.##", 100f * (Math.pow(1.175f, combinedBuffedBonus(Dungeon.hero)) - 1f)));
			}
			return info;
		} else {
			return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 17.5f));
		}
	}

	public String upgradeStat1(int level){
		if (cursed) level = Math.min(-1, level-3);
		return Messages.decimalFormat("#.##", 100f * (Math.pow(1.175f, level+1)-1f)) + "%";
	}

	@Override
	protected RingBuff buff( ) {
		return new Arcana();
	}

	public static float enchantPowerMultiplier(Char target ){
		return (float)Math.pow(1.175f, getBuffedBonus(target, Arcana.class));
	}

	public class Arcana extends RingBuff {
	}

}
