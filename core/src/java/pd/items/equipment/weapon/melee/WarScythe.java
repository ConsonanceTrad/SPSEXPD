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
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.messages.InlineText;

public class WarScythe extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WarScythe.class)
			.t("name", "战镰")
			.t("stats_desc", "这是一件不太精准的武器。")
			.t("ability_name", "收割")
			.t("typical_ability_desc", "决斗家可以用战镰来_收割_敌人，这破坏性的一击必定命中，一般会造成_%d点伤害_。若敌人不免疫流血，则造成的伤害会转化为等值流血效果。")
			.t("ability_desc", "决斗家可以用战镰来_收割_敌人，这破坏性的一击必定命中，并会造成_%d点伤害_。若敌人不免疫流血，则造成的伤害会转化为等值流血效果。")
			.t("desc", "这件巨大又笨重的农具被加固了。现在比起收割庄稼，它更适宜于收割敌人。");
	}




	{
		image = EquipmentEquipWeaponBasicWeaponDict.WAR_SCYTHE_0;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 0.9f;

		tier = 5;
		ACC = 0.8f; //20% penalty to accuracy
	}

	@Override
	public int max(int lvl) {
		return  Math.round(6.67f*(tier+1)) +    //40 base, up from 30
				lvl*(tier+1);                   //scaling unchanged
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//replaces damage with 30+4.5*lvl bleed, roughly 133% avg base dmg, 129% avg scaling
		int bleedAmt = augment.damageFactor(Math.round(30f + 4.5f*buffedLvl()));
		Sickle.harvestAbility(hero, target, 0f, bleedAmt, this);
	}

	@Override
	public String abilityInfo() {
		int bleedAmt = levelKnown ? Math.round(30f + 4.5f*buffedLvl()) : 30;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(bleedAmt));
		} else {
			return Messages.get(this, "typical_ability_desc", bleedAmt);
		}
	}

	@Override
	public String upgradeAbilityStat(int level) {
		return Integer.toString(augment.damageFactor(Math.round(30f + 4.5f*level)));
	}

}
