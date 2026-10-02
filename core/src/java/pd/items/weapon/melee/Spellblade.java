/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package pd.items.weapon.melee;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;

/** A deliberately modest starter weapon for the Spellsword. */
public class Spellblade extends MeleeWeapon {

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.15f;
		tier = 1;
		ACC = 1.1f;
	}

	@Override
	public int max(int lvl) {
		return 8 + 2 * lvl;
	}
}
