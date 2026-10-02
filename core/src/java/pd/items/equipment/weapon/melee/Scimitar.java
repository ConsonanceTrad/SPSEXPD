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
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class Scimitar extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Scimitar.class)
			.t("name", "弯刀")
			.t("stats_desc", "这是一件比较快的武器。")
			.t("ability_name", "剑舞")
			.t("typical_ability_desc", "决斗家可用弯刀进入_剑舞_。进入这个姿态不消耗时间，并使决斗家一般在_%d回合_内攻击速度+60%%且精准+50%%。")
			.t("ability_desc", "决斗家可用弯刀进入_剑舞_。进入这个姿态不消耗时间，并使决斗家在 _%d回合_内攻击速度+60%%且精准+50%%。")
			.t("upgrade_ability_stat_name", "武技持续时间")
			.t("desc", "一把厚重的弯刀。它的形状能让它进行更快但不甚强力的攻击。")
			.t("$sworddance.name", "剑舞")
			.t("$sworddance.desc", "决斗家正以一种舞蹈的形式展开疾风骤雨般的攻击。当这个架势激活时，她的攻击速度增加60%%(在使用弯刀的情况下刚好足以一回合攻击两次)，且具有+50%%的精准。\n\n剩余回合数：%s");
	}




	{
		image = EquipmentEquipWeaponBasicWeaponDict.SCIMITAR;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.2f;

		tier = 3;
		DLY = 0.8f; //1.25x speed
	}

	@Override
	public int max(int lvl) {
		return  4*(tier+1) +    //16 base, down from 20
				lvl*(tier+1);   //scaling unchanged
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		beforeAbilityUsed(hero, null);
		//1 turn less as using the ability is instant
		Buff.prolong(hero, SwordDance.class, 3+buffedLvl());
		hero.sprite.operate(hero.pos);
		hero.next();
		afterAbilityUsed(hero);
	}

	@Override
	public String abilityInfo() {
		if (levelKnown){
			return Messages.get(this, "ability_desc", 4+buffedLvl());
		} else {
			return Messages.get(this, "typical_ability_desc", 4);
		}
	}

	@Override
	public String upgradeAbilityStat(int level) {
		return Integer.toString(4+level);
	}

	public static class SwordDance extends FlavourBuff {

		{
			announced = true;
			type = buffType.POSITIVE;
		}

		@Override
		public int icon() {
			return BuffIndicator.DUEL_DANCE;
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (4 - visualcooldown()) / 4);
		}
	}

}
