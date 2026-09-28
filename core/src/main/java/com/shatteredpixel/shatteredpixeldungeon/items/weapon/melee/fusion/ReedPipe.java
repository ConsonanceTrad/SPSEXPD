/*
 * Content adapted from Magic Ling Pixel Dungeon for Shattered Pixel Dungeon 4.0.
 * Distributed under the GNU General Public License v3 or later.
 */

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Whip;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class ReedPipe extends Whip {

	{
		image = ItemSpriteSheet.WAND_MAGIC_MISSILE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.35f;
		tier = 2;
	}

	@Override
	public int min(int lvl) {
		return 2 + lvl;
	}

	@Override
	public int max(int lvl) {
		return 10 + 3 * lvl;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(7) == 0) {
			Buff.prolong(defender, Daze.class, 2f);
		}
		return super.proc(attacker, defender, damage);
	}
}
