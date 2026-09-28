/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GrowSeed;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class SunAmmo extends SpAmmo {
	private static final ItemSprite.Glowing PINK = new ItemSprite.Glowing(0xCCAA88);
	@Override public ItemSprite.Glowing glowing() { return PINK; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Random.Int(7) == 3) {
			Buff.affect(defender, GrowSeed.class).set(5f);
			if (defender.sprite != null) defender.sprite.emitter().burst(EarthParticle.FACTORY, 5);
		} else {
			defender.damage((int)(0.20f * damage), DamageType.LIGHT_DAMAGE);
		}
	}
}
