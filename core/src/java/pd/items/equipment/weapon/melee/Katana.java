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

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.messages.InlineText;

public class Katana extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Katana.class)
			.t("name", "武士刀")
			.t("stats_desc", "这件武器可以格挡0~3点伤害。")
			.t("ability_name", "弓步刺")
			.t("typical_ability_desc", "决斗家可用武士刀对相距1格的敌人使出_弓步刺_。这一击会向敌人突进，一般造成_%1$d~%2$d点伤害_，且必定命中。")
			.t("ability_desc", "决斗家可用武士刀对相距1格的敌人使出_弓步刺_。这一击会向敌人突进，造成_%1$d~%2$d点伤害_，且必定命中。")
			.t("desc", "一把在握柄上方带有巨大金属护手的修长刀刃。");
	}




	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.1f;

		tier = 4;
	}

	@Override
	public int max(int lvl) {
		return  4*(tier+1) +    //20 base, down from 25
				lvl*(tier+1);   //scaling unchanged
	}

	@Override
	public int defenseFactor( Char owner ) {
		return 3;	//3 extra defence
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//+(8+2*lvl) damage, roughly +67% damage
		int dmgBoost = augment.damageFactor(8 + Math.round(2f*buffedLvl()));
		Rapier.lungeAbility(hero, target, 1, dmgBoost, this);
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = levelKnown ? 8 + Math.round(2f*buffedLvl()) : 8;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
		}
	}

	public String upgradeAbilityStat(int level){
		int dmgBoost = 8 + Math.round(2f*level);
		return augment.damageFactor(min(level)+dmgBoost) + "-" + augment.damageFactor(max(level)+dmgBoost);
	}

}
