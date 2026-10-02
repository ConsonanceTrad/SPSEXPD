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

package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.items.equipment.wands.WandOfBlastWave;
import pd.items.equipment.weapon.SpiritBow;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.mechanics.Ballistica;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Elastic extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Elastic.class)
			.t("name", "弹性%s")
			.t("desc", "弹性武器在攻击时有几率把敌人击退较短的距离。")
			.t("elestrike_desc", "武器拥有弹性附魔时，元素打击会将范围内的所有敌人都击退5格。");
	}



	
	private static ItemSprite.Glowing PINK = new ItemSprite.Glowing( 0xFF00FF );
	
	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage ) {
		int level = Math.max( 0, weapon.buffedLvl() );

		// lvl 0 - 20%
		// lvl 1 - 33%
		// lvl 2 - 43%
		float procChance = (level+1f)/(level+5f) * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {

			float powerMulti = Math.max(1f, procChance);

			//trace a ballistica to our target (which will also extend past them
			Ballistica trajectory = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET);
			//trim it to just be the part that goes past them
			trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
			//knock them back along that ballistica
			WandOfBlastWave.throwChar(defender,
					trajectory,
					Math.round(2 * powerMulti),
					!(weapon instanceof MissileWeapon || weapon instanceof SpiritBow),
					true,
					this);
		}
		
		return damage;
	}
	
	@Override
	public ItemSprite.Glowing glowing() {
		return PINK;
	}

}
