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

public class Gauntlet extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Gauntlet.class)
			.t("name", "魔岩拳套")
			.t("stats_desc", "这是一件非常快的武器。")
			.t("ability_name", "连击")
			.t("typical_ability_desc", "决斗家可以使用魔岩拳套进行_连击_。这次攻击必定命中，并且决斗家每使用近战或者投掷武器成功命中一次，这次攻击的伤害一般就_增加%d点_。连续5回合(击杀后为15回合)未成功命中会重置连击。")
			.t("ability_desc", "决斗家可以使用魔岩拳套进行_连击_。这次攻击必定命中，并且决斗家每使用近战或者投掷武器成功命中一次，这次攻击的伤害就_增加%d点_。连续5回合(击杀后为15回合)未成功命中会重置连击。")
			.t("desc", "这个巨大的拳套由一匹红布和层层覆盖在布上的魔法岩石交织而成。戴上后，布料紧紧裹住你的整个前臂，让厚重的岩板变得像一层坚硬的皮肤。要有足够的力量才能将如此沉重的武器自如挥舞，但正是这种力量和重量的结合让这件武器发挥出可怕的威力。");
	}

	
	{
		image = EquipmentEquipWeaponBasicWeaponDict.GAUNTLETS_0;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 1.2f;
		
		tier = 5;
		DLY = 0.5f; //2x speed
	}
	
	@Override
	public int max(int lvl) {
		return  Math.round(2.5f*(tier+1)) +     //15 base, down from 30
				lvl*Math.round(0.5f*(tier+1));  //+3 per level, down from +6
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//+(5+lvl) damage, roughly +50% base damage, +50% scaling
		int dmgBoost = augment.damageFactor(5 + buffedLvl());
		Sai.comboStrikeAbility(hero, target, 0, dmgBoost, this);
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = levelKnown ? 5 + buffedLvl() : 5;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", augment.damageFactor(dmgBoost));
		}
	}

	public String upgradeAbilityStat(int level){
		return "+" + augment.damageFactor(5 + level);
	}

}
