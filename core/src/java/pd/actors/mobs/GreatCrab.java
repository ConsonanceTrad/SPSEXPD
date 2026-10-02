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

package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.blobs.Electricity;
import pd.actors.mobs.npcs.Ghost;
import pd.items.consum.food.MysteryMeat;
import pd.items.equipment.wands.Wand;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.sprites.GreatCrabSprite;
import pd.utils.GLog;

public class GreatCrab extends Crab {

	{
		spriteClass = GreatCrabSprite.class;

		HP = HT = 100;
		defenseSkill = 0; //see damage()
		baseSpeed = 1f;

		EXP = 6;

		state = WANDERING;

		properties.add(Property.MINIBOSS);
		properties.add(Property.BEAST);
	}

	private int moving = 0;

	@Override
	protected boolean getCloser( int target ) {
		//this is used so that the crab remains slower, but still detects the player at the expected rate.
		moving++;
		if (moving < 3) {
			return super.getCloser( target );
		} else {
			moving = 0;
			return true;
		}

	}

	@Override
	public void damage( int dmg, Object src ){
		if (legacyBlocksDamage(src)) {
			GLog.n( Messages.get(this, "noticed") );
			if (sprite != null) sprite.showStatus(CharSprite.NEUTRAL, Messages.get(this, "blocked"));
		} else {
			super.damage( dmg, src );
		}
	}

	boolean legacyBlocksDamage(Object src) {
		return enemySeen && state != SLEEPING && paralysed == 0
				&& (src instanceof Wand || src instanceof Char || src == Electricity.class);
	}

	@Override
	public void die( Object cause ) {
		super.die( cause );

		Ghost.Quest.process();
		if (Dungeon.level != null) {
			Dungeon.level.drop(new MysteryMeat(), pos);
			pd.items.Heap heap =
					Dungeon.level.drop(new MysteryMeat(), pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}
	}
}
