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

package pd.items.consum.scrolls.exotic;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dread;
import pd.actors.buffs.Terror;
import pd.actors.mobs.Mob;
import pd.effects.Flare;
import pd.sprites.ItemIconSheet;
import render.noosa.audio.Sample;

public class ScrollOfDread extends ExoticScroll {
	
	{
		icon = ItemIconSheet.SCROLL_DREAD;
	}
	
	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
		new Flare( 5, 32 ).color( 0xFF0000, true ).show( curUser.sprite, 2f );
		Sample.INSTANCE.play( Assets.Sounds.READ );

		for (Mob mob : Dungeon.level.mobs().toArray( new Mob[0] )) {
			if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
				if (!mob.isImmune(Dread.class)){
					Buff.affect( mob, Dread.class ).object = curUser.id();
				} else {
					Buff.affect( mob, Terror.class, Terror.DURATION ).object = curUser.id();
				}
			}
		}

		identify();
		
		readAnimation();
	}
}
