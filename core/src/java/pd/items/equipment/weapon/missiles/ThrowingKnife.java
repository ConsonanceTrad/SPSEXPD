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

package pd.items.equipment.weapon.missiles;

import pd.atlas.items.ConsumThrowsDict;

import pd.Assets;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.messages.InlineText;

public class ThrowingKnife extends MissileWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ThrowingKnife.class)
			.t("name", "飞刀")
			.t("stats_desc", "这件武器对未察觉你的敌人更有效。")
			.t("desc", "这些轻便的小刀不同于普通的匕首，平衡的配重使其能在飞行后准确地刺穿目标。")
			.t("discover_hint", "某位英雄初始携带该物品。");
	}



	
	{
		image = ConsumThrowsDict.THROWING_KNIFE_0;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.2f;
		
		bones = false;
		
		tier = 1;
		baseUses = 5;
	}
	
	@Override
	public int max(int lvl) {
		return  6 * tier +                      //6 base, up from 5
				(tier == 1 ? 2*lvl : tier*lvl); //scaling unchanged
	}
	
	@Override
	public int damageRoll(Char owner) {
		if (owner instanceof Hero) {
			Hero hero = (Hero)owner;
			Char enemy = hero.attackTarget();
			if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
				//deals 75% toward max to max on surprise, instead of min to max.
				int diff = max() - min();
				int damage = augment.damageFactor(Hero.heroDamageIntRange(
						min() + Math.round(diff*0.75f),
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
}
