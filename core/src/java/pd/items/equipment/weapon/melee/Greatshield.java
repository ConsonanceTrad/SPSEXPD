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

package pd.items.equipment.weapon.melee;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.messages.InlineText;

public class Greatshield extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Greatshield.class)
			.t("name", "巨型方盾")
			.t("typical_stats_desc", "这件武器通常能格挡0~%d点伤害。格挡量随升级而增长。")
			.t("stats_desc", "这件武器能格挡0~%d点伤害。格挡量随升级而增长。")
			.t("ability_name", "护卫")
			.t("typical_ability_desc", "决斗家可以用巨型方盾_护卫_自己，一般会在_%d回合_内完全抵挡针对自己的物理或魔法攻击。一旦决斗家抵挡过攻击，再行还击或施法，都会终止护卫状态。")
			.t("ability_desc", "决斗家可以用巨型方盾_护卫_自己，在_%d回合_内完全抵挡针对自己的物理或魔法攻击。一旦决斗家抵挡过攻击，再行还击或施法，都会终止护卫状态。")
			.t("upgrade_ability_stat_name", "武技持续时间")
			.t("desc", "与其说它是一面盾，不如说它是一堵能移动的墙。这一大块金属对于防御十分有效，但没有在攻击方面留下多少余地。");
	}




	{
		image = EquipmentEquipWeaponBasicWeaponDict.GREATSHIELD_0;

		tier = 5;
	}

	@Override
	public int max(int lvl) {
		return  Math.round(3f*(tier+1)) +   //18 base, down from 20
				lvl*(tier-1);               //+3 per level, down from +6
	}

	@Override
	public int defenseFactor( Char owner ) {
		return DRMax();
	}

	public int DRMax(){
		return DRMax(buffedLvl());
	}

	//6 extra defence, plus 2 per level
	public int DRMax(int lvl){
		return 6 + 2*lvl;
	}
	
	public String statsInfo(){
		if (isIdentified()){
			return Messages.get(this, "stats_desc", 6+2*buffedLvl());
		} else {
			return Messages.get(this, "typical_stats_desc", 6);
		}
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		RoundShield.guardAbility(hero, 3+buffedLvl(), this);
	}

	@Override
	public String abilityInfo() {
		if (levelKnown){
			return Messages.get(this, "ability_desc", 3+buffedLvl());
		} else {
			return Messages.get(this, "typical_ability_desc", 3);
		}
	}

	@Override
	public String upgradeAbilityStat(int level) {
		return Integer.toString(3 + level);
	}
}
