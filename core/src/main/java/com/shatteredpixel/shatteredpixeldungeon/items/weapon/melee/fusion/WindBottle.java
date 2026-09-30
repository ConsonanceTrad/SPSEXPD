/* Special Surprise content rebuilt for Shattered Pixel Dungeon 4.0. GPLv3+. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class WindBottle extends Sword implements FusionWeapon {
	{ image = ItemSpriteSheet.WAND_BLAST_WAVE; tier = 3; }
	@Override public int min(int lvl) { return 4 + lvl; }
	@Override public int max(int lvl) { return 17 + 4 * lvl; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		if (Random.Int(5) == 0) {
			int beyond = defender.pos + (defender.pos - attacker.pos);
			Ballistica path = new Ballistica(defender.pos, beyond, Ballistica.PROJECTILE);
			WandOfBlastWave.throwChar(defender, path, 1, false, false, this);
		}
		return result;
	}
}
