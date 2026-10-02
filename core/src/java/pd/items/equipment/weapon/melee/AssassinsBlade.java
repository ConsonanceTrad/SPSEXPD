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

public class AssassinsBlade extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AssassinsBlade.class)
			.t("name", "暗杀之刃")
			.t("stats_desc", "这件武器对未察觉你的敌人更有效。")
			.t("ability_name", "潜行")
			.t("typical_ability_desc", "决斗家握持暗杀之刃时可以_潜行_。这个武技能够使决斗家立即闪现最多3格远，并一般会给予_%d回合隐形_。")
			.t("ability_desc", "决斗家握持暗杀之刃时可以_潜行_。这个武技能够使决斗家立即闪现最多3格远，并给予_%d回合隐形_。")
			.t("upgrade_ability_stat_name", "武技持续时间")
			.t("desc", "黑曜石制的波浪形短刃，虽轻便但不易用，如果能击中要害足以致命。");
	}




	{
		image = EquipmentEquipWeaponBasicWeaponDict.ASSASSINS_BLADE_0;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 0.9f;

		tier = 4;
	}

	@Override
	public int max(int lvl) {
		return  4*(tier+1) +    //20 base, down from 25
				lvl*(tier+1);   //scaling unchanged
	}

	@Override
	public int damageRoll(Char owner) {
		if (owner instanceof Hero) {
			Hero hero = (Hero)owner;
			Char enemy = hero.attackTarget();
			if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
				//deals 50% toward max to max on surprise, instead of min to max.
				int diff = max() - min();
				int damage = augment.damageFactor(Hero.heroDamageIntRange(
						min() + Math.round(diff*0.50f),
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
		Dagger.sneakAbility(hero, target, 3, 2+buffedLvl(), this);
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