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

package pd.items.potions.exotic;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Freezing;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Roots;
import pd.sprites.ItemSpriteSheet;
import watabou.noosa.audio.Sample;
import watabou.utils.PathFinder;

public class PotionOfSnapFreeze extends ExoticPotion {
	
	{
		icon = ItemSpriteSheet.Icons.POTION_SNAPFREEZ;
	}
	
	@Override
	public void shatter(int cell) {

		splash( cell );
		if (Dungeon.level.heroFOV[cell]) {
			identify();

			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
		}
		
		for (int offset : PathFinder.NEIGHBOURS9){
			if (!Dungeon.level.solid[cell+offset]) {
				
				Freezing.affect( cell + offset );
				
				Char ch = Actor.findChar( cell + offset);
				if (ch != null){
					Buff.prolong(ch, Roots.class, Roots.DURATION*2f);
				}
				
			}
		}
	}
}
