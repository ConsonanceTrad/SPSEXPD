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
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.messages.InlineText;

public class Cudgel extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Cudgel.class)
			.t("name", "权杖")
			.t("stats_desc", "这是一件比较精准的武器。")
			.t("ability_name", "重击")
			.t("typical_ability_desc", "决斗家可以用权杖使出_重击_。这用尽全力的一击如果是在伏击敌人，则一般会造成_%1$d~%2$d点伤害_并施加5回合的恍惚，使其精准与闪避均降低50%%。重击必定命中，但若非伏击则只会造成普通的伤害。")
			.t("ability_desc", "决斗家可以用权杖使出_重击_。这用尽全力的一击如果是在伏击敌人，则会造成_%1$d~%2$d点伤害_并施加5回合的恍惚，使其精准与闪避均降低50%%。重击必定命中，但若非伏击则只会造成普通的伤害。")
			.t("desc", "一把小型黄铜锤，更多的作为贵族们的自卫工具而非冒险用武器使用。")
			.t("discover_hint", "某位英雄初始携带该物品。");
	}




	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 1.2f;

		tier = 1;
		ACC = 1.40f; //40% boost to accuracy

		bones = false;
	}

	@Override
	public int max(int lvl) {
		return  4*(tier+1) +    //8 base, down from 10
				lvl*(tier+1);   //scaling unchanged
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//+(3+1.5*lvl) damage, roughly +67% base dmg, +100% scaling
		int dmgBoost = augment.damageFactor(3 + Math.round(1.5f*buffedLvl()));
		Mace.heavyBlowAbility(hero, target, 1, dmgBoost, this);
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = levelKnown ? 3 + Math.round(1.5f*buffedLvl()) : 3;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
		}
	}

	public String upgradeAbilityStat(int level){
		int dmgBoost = 3 + Math.round(1.5f*level);
		return augment.damageFactor(min(level)+dmgBoost) + "-" + augment.damageFactor(max(level)+dmgBoost);
	}

}
