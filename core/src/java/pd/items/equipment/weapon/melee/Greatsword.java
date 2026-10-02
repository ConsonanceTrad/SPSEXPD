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
import pd.messages.Messages;
import pd.messages.InlineText;

public class Greatsword extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Greatsword.class)
			.t("name", "巨剑")
			.t("ability_name", "顺劈")
			.t("typical_ability_desc", "决斗家可以用巨剑_顺劈_敌人。这一般会造成_%1$d~%2$d点伤害_且必定命中。如果顺劈击杀了一名敌人，这一击将不消耗时间，并且决斗家可在5回合内额外使用一次不消耗充能数的顺劈。")
			.t("ability_desc", "决斗家可以用巨剑_顺劈_敌人，造成_%1$d~%2$d点伤害_且必定命中。如果顺劈击杀了一名敌人，这一击将不消耗时间，并且决斗家可在5回合内额外使用一次不消耗充能数的顺劈。")
			.t("desc", "这把大剑进行的每次沉重挥舞都能造成大量伤害。");
	}


	{
		image = EquipmentEquipWeaponBasicWeaponDict.GREATSWORD_0;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		tier = 5;
	}

	@Override
	protected int baseChargeUse(Hero hero, Char target){
		if (hero.buff(Sword.CleaveTracker.class) != null){
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//+(7+lvl) damage, roughly +40% base dmg, +30% scaling
		int dmgBoost = augment.damageFactor(7 + buffedLvl());
		Sword.cleaveAbility(hero, target, 1, dmgBoost, this);
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = levelKnown ? 7 + buffedLvl() : 7;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
		}
	}

	public String upgradeAbilityStat(int level){
		int dmgBoost = 7 + level;
		return augment.damageFactor(min(level)+dmgBoost) + "-" + augment.damageFactor(max(level)+dmgBoost);
	}

}
