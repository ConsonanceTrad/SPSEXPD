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

public class RingOfSharpshooting extends Ring {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RingOfSharpshooting.class)
			.t("name", "神射之戒")
			.t("stats", "佩戴这枚戒指时，你的投掷武器伤害会获得_%1$d_级的提升，耐久度则会提升_%2$s%%_。")
			.t("typical_stats", "佩戴这枚戒指时，你的投掷武器伤害通常会获得_%1$d_级的提升，耐久度则会提升_%2$s%%_。")
			.t("combined_stats", "你已装备的戒指正联结它们的力量，一共提高了_%1$d_级投掷武器等级，并且增加了其_%2$s%%_耐久。")
			.t("upgrade_stat_name_1", "等级伤害加成")
			.t("upgrade_stat_name_2", "耐久度加成")
			.t("desc", "这枚戒指会加强佩戴者的瞄准能力，使投掷武器变得更加致命且耐用。被诅咒的戒指则会起反作用。");
	}


	{
		icon = ItemIconSheet.RING_SHARPSHOOT;
		buffClass = Aim.class;
	}

	public String statsInfo() {
		if (isIdentified()){
			String info = Messages.get(this, "stats",
					soloBuffedBonus(), Messages.decimalFormat("#.##", 100f * (Math.pow(1.2, soloBonus()) - 1f)));
			if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)){
				info += "\n\n" + Messages.get(this, "combined_stats",
						combinedBuffedBonus(Dungeon.hero), Messages.decimalFormat("#.##", 100f * (Math.pow(1.2, combinedBonus(Dungeon.hero)) - 1f)));
			}
			return info;
		} else {
			return Messages.get(this, "typical_stats", 1, Messages.decimalFormat("#.##", 20f));
		}
	}

	@Override
	public String upgradeStat1(int level) {
		if (cursed && cursedKnown) level = Math.min(-1, level-3);
		return Integer.toString(level+1);
	}

	@Override
	public String upgradeStat2(int level) {
		if (cursed && cursedKnown) level = Math.min(-1, level-3);
		return Messages.decimalFormat("#.##", 100f * (Math.pow(1.2, level+1)-1f)) + "%";
	}
	
	@Override
	protected RingBuff buff( ) {
		return new Aim();
	}
	
	public static int levelDamageBonus( Char target ){
		return getBuffedBonus(target, RingOfSharpshooting.Aim.class);
	}
	
	public static float durabilityMultiplier( Char target ){
		return (float)(Math.pow(1.2, getBonus(target, Aim.class)));
	}

	public class Aim extends RingBuff {
	}
}
