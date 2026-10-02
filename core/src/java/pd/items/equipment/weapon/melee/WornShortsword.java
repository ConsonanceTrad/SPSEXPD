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

public class WornShortsword extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WornShortsword.class)
			.t("name", "破旧的短剑")
			.t("ability_name", "顺劈")
			.t("typical_ability_desc", "决斗家可以用破旧的短剑_顺劈_敌人。这一般会造成_%1$d~%2$d点伤害_并且必定命中。如果顺劈击杀了一名敌人，决斗家可在5回合内额外使用一次无消耗顺劈。")
			.t("ability_desc", "决斗家可以用破旧的短剑_顺劈_敌人。造成_%1$d~%2$d点伤害_并且必定命中。如果顺劈击杀了一名敌人，决斗家可在5回合内额外使用一次无消耗顺劈。")
			.t("desc", "一把十分短的剑，在大量使用后有一定的磨损。它比正常情况的短剑更轻，也更弱。")
			.t("discover_hint", "某位英雄初始携带该物品。");
	}




	{
		image = EquipmentEquipWeaponBasicWeaponDict.WORN_SHORTSWORD_0;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.1f;

		tier = 1;
		
		bones = false;
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
		//+(3+lvl) damage, roughly +55% base dmg, +67% scaling
		int dmgBoost = augment.damageFactor(3 + buffedLvl());
		Sword.cleaveAbility(hero, target, 1, dmgBoost, this);
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = levelKnown ? 3 + buffedLvl() : 3;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
		}
	}

	public String upgradeAbilityStat(int level){
		int dmgBoost = 3 + level;
		return augment.damageFactor(min(level)+dmgBoost) + "-" + augment.damageFactor(max(level)+dmgBoost);
	}

}
