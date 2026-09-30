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

package pd.items.weapon.enchantments;

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Adrenaline;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Corruption;
import pd.actors.mobs.Mob;
import pd.items.armor.curses.Multiplicity;
import pd.items.weapon.Weapon;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import render.utils.Random;

public class Corrupting extends Weapon.Enchantment {
	
	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x440066 );
	
	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		int level = Math.max( 0, weapon.buffedLvl() );

		// lvl 0 - 20%
		// lvl 1 ~ 23%
		// lvl 2 ~ 26%
		float procChance = (level+5f)/(level+25f) * procChanceMultiplier(attacker);
		if (Random.Float() < procChance
				&& attacker.alignment == Char.Alignment.ALLY //enemies cannot inflict corruption
				&& !defender.isImmune(Corruption.class)
				&& defender.buff(Corruption.class) == null
				&& defender instanceof Mob
				&& defender.isAlive()){

			//we use a tracker so that anything that kills the enemy as part of this attack triggers
			Buff.affect(defender, CorruptingTracker.class).powerMulti = Math.max(1f, procChance);

		}
		
		return damage;
	}

	public static class CorruptingTracker extends Buff {

		{
			actPriority = Actor.VFX_PRIO;
		}

		float powerMulti = 1f;

		@Override
		public boolean act() {
			detach();
			return true;
		}

		@Override
		public void detach() {
			if (!target.isAlive()){

				Mob corrupted = Multiplicity.duplicate((Mob)target);

				if (corrupted != null) {
					target.sprite.killAndErase();

					corrupted.timeToNow();
					corrupted.pos = target.pos;
					GameScene.add(corrupted);

					Corruption.corruptionHeal(corrupted);
					Buff.affect(corrupted, Corruption.class);

					if (powerMulti > 1.1f) {
						//1 turn of adrenaline for each 20% above 100% proc rate
						Buff.affect(corrupted, Adrenaline.class, Math.round(5 * (powerMulti - 1f)));
					}
				}

			}
			super.detach();
		}
	}
	
	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}
}
