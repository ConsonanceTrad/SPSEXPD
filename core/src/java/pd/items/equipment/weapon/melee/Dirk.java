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

import pd.Assets;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.messages.Messages;
import pd.messages.InlineText;

public class Dirk extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Dirk.class)
			.t("name", "长匕首")
			.t("stats_desc", "这件武器对未察觉你的敌人更有效。")
			.t("ability_name", "潜行")
			.t("typical_ability_desc", "决斗家握持长匕首时可以_潜行_。这个武技能够使决斗家立即闪现最多4格远，并一般给予_%d回合隐形_。")
			.t("ability_desc", "决斗家握持长匕首时可以_潜行_。这个武技能够使决斗家立即闪现最多4格远，并给予_%d回合隐形_。")
			.t("upgrade_ability_stat_name", "武技持续时间")
			.t("desc", "一个长一些的刺击型匕首，可以往敌人身体里捅得更深一点。");
	}


	{
		image = EquipmentEquipWeaponBasicWeaponDict.DIRK_0;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1f;

		tier = 2;
	}

	@Override
	public int max(int lvl) {
		return  4*(tier+1) +    //12 base, down from 15
				lvl*(tier+1);   //scaling unchanged
	}
	
	@Override
	public int damageRoll(Char owner) {
		if (owner instanceof Hero) {
			Hero hero = (Hero)owner;
			Char enemy = hero.attackTarget();
			if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
				//deals 67% toward max to max on surprise, instead of min to max.
				int diff = max() - min();
				int damage = augment.damageFactor(Hero.heroDamageIntRange(
						min() + Math.round(diff*0.67f),
						max()));
				int exStr = hero.STR() - STRReq();
				if (exStr > 0) {
					damage += Hero.heroDamageIntRange(0, exStr);
				}
				return damage;
			}
		}
		return super.damageRoll(owner);
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	public boolean useTargeting(){
		return false;
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		Dagger.sneakAbility(hero, target, 4, 2+buffedLvl(), this);
	}

	@Override
	public String abilityInfo() {
		if (levelKnown){
			return Messages.get(this, "ability_desc", 2+buffedLvl());
		} else {
			return Messages.get(this, "typical_ability_desc", 2);
		}
	}

	@Override
	public String upgradeAbilityStat(int level) {
		return Integer.toString(2+level);
	}

}
