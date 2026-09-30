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

package pd.items.quest;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.scrolls.ScrollOfTeleportation;
import pd.items.spells.Spell;
import pd.journal.Catalog;
import pd.sprites.ItemSprite;
import pd.sprites.ItemSpriteSheet;

public class VaultBeacon extends Spell {

	{
		image = ItemSpriteSheet.RETURN_BEACON;

		stackable = true;
		unique = true;
	}

	@Override
	protected void onCast(Hero hero) {
		if (ScrollOfTeleportation.teleportToLocation(hero, Dungeon.level.entrance())){
			hero.spendAndNext( 1f );
			Catalog.countUse(getClass());
			detach(hero.belongings.backpack);
		}
	}

	private static final ItemSprite.Glowing WHITE = new ItemSprite.Glowing( 0xFFFFFF );

	@Override
	public ItemSprite.Glowing glowing() {
		return WHITE;
	}

}
