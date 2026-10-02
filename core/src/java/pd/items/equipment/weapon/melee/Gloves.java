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

public class Gloves extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Gloves.class)
			.t("name", "镶钉手套")
			.t("stats_desc", "这是一件非常快的武器。")
			.t("ability_name", "连击")
			.t("typical_ability_desc", "决斗家可以使用镶钉手套进行_连击_。这次攻击必定命中，并且决斗家每使用近战或者投掷武器成功命中一次，这次攻击的伤害一般就_增加%d点_。连续5回合(击杀后为15回合)未成功命中会重置连击。")
			.t("ability_desc", "决斗家可以使用魔岩拳套进行_连击_。这次攻击必定命中，并且决斗家每使用近战或者投掷武器成功命中一次，这次攻击的伤害就_增加%d点_。连续5回合(击杀后为15回合)未成功命中会重置连击。")
			.t("desc", "这双镶钉手套没有多少防护作用，但能在勉强当作武器使用的同时腾出双手。")
			.t("discover_hint", "某位英雄初始携带该物品。");
	}




	{
		image = EquipmentEquipWeaponBasicWeaponDict.GLOVES;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.3f;

		tier = 1;
		DLY = 0.5f; //2x speed
		
		bones = false;
	}

	@Override
	public int max(int lvl) {
		return  Math.round(2.5f*(tier+1)) +     //5 base, down from 10
				lvl*Math.round(0.5f*(tier+1));  //+1 per level, down from +2
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//+(3+0.75*lvl) damage, roughly +100% base damage, +100% scaling
		int dmgBoost = augment.damageFactor(3 + buffedLvl());
		Sai.comboStrikeAbility(hero, target, 0, dmgBoost, this);
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = levelKnown ? 3 + buffedLvl() : 3;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", augment.damageFactor(dmgBoost));
		}
	}

	public String upgradeAbilityStat(int level){
		return "+" + augment.damageFactor(3 + level);
	}

}
