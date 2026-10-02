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
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.Talent;
import pd.messages.Messages;
import pd.sprites.ItemIconSheet;
import pd.messages.InlineText;

public class RingOfEnergy extends Ring {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RingOfEnergy.class)
			.t("name", "能量之戒")
			.t("stats", "佩戴这枚戒指时，你持有的法杖，神器及英雄护甲充能速度将提高_%s%%_。")
			.t("typical_stats", "佩戴这枚戒指时，通常情况下，你持有的法杖，神器及英雄护甲充能速度将提高_%s%%_。")
			.t("combined_stats", "你已装备的戒指正联结它们的力量，一共增加了_%s%%_的充能速度。")
			.t("upgrade_stat_name_1", "充能加成")
			.t("desc", "在戒指的奥术领域笼罩下，你持有的所有魔法装备充能速度都会得到提升。被诅咒的戒指反而会减慢充能速度。");
	}




	{
		icon = ItemIconSheet.RING_ENERGY;
		buffClass = Energy.class;
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
			return Messages.get(this, "typical_stats",
					Messages.decimalFormat("#.##", 17.5f));
		}
	}

	public String upgradeStat1(int level){
		if (cursed && cursedKnown) level = Math.min(-1, level-3);
		return Messages.decimalFormat("#.##", 100f * (Math.pow(1.175f, level+1)-1f)) + "%";
	}
	
	@Override
	protected RingBuff buff( ) {
		return new Energy();
	}
	
	public static float wandChargeMultiplier( Char target ){
		float bonus = (float)Math.pow(1.175, getBuffedBonus(target, Energy.class));

		if (target instanceof Hero && ((Hero) target).heroClass != HeroClass.CLERIC && ((Hero) target).hasTalent(Talent.LIGHT_READING)){
			bonus *= 1f + (0.2f * ((Hero) target).pointsInTalent(Talent.LIGHT_READING)/3f);
		}

		return bonus;
	}

	public static float artifactChargeMultiplier( Char target ){
		float bonus = (float)Math.pow(1.175, getBuffedBonus(target, Energy.class));

		if (target instanceof Hero && ((Hero) target).heroClass != HeroClass.ROGUE && ((Hero) target).hasTalent(Talent.LIGHT_CLOAK)){
			bonus *= 1f + (0.2f * ((Hero) target).pointsInTalent(Talent.LIGHT_CLOAK)/3f);
		}

		return bonus;
	}

	public static float armorChargeMultiplier( Char target ){
		return (float)Math.pow(1.175, getBuffedBonus(target, Energy.class));
	}
	
	public class Energy extends RingBuff {
	}
}
