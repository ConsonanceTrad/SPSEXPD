/*
 * Content adapted from Magic Ling Pixel Dungeon for Shattered Pixel Dungeon 4.0.
 * Distributed under the GNU General Public License v3 or later.
 */

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Quarterstaff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class VerdantGuard extends Quarterstaff implements FusionWeapon {

	{
		image = ItemSpriteSheet.ROUND_SHIELD;
		hitSound = Assets.Sounds.HIT_CRUSH;
		tier = 3;
	}

	@Override
	public int max(int lvl) {
		return 14 + 4 * lvl;
	}

	@Override
	public int defenseFactor(Char owner) {
		return 3 + Math.max(0, buffedLvl()) / 2;
	}
}
