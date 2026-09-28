/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class MossAmmo extends SpAmmo {
	private static final ItemSprite.Glowing PURPLE = new ItemSprite.Glowing(0x8844CC);
	@Override public ItemSprite.Glowing glowing() { return PURPLE; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		defender.damage((int)(0.10f * damage), DamageType.EARTH_DAMAGE);
		if (Random.Int(4) == 3) {
			Buff.affect(defender, Ooze.class).set(5f);
			if (defender.sprite != null) defender.sprite.emitter().burst(EarthParticle.FACTORY, 5);
		} else {
			Buff.affect(defender, Poison.class).set(Random.IntRange(4, 5));
		}
	}
}
