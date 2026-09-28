/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Wet;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class IceAmmo extends SpAmmo {
	private static final ItemSprite.Glowing BLUE = new ItemSprite.Glowing(0x0000FF);
	@Override public ItemSprite.Glowing glowing() { return BLUE; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		defender.damage((int)(0.25f * damage), DamageType.ICE_DAMAGE);
		if (Random.Int(4) == 3) {
			Buff.affect(defender, Frost.class, 5f * Random.Float(2f, 4f));
			if (defender.sprite != null) defender.sprite.emitter().burst(SnowParticle.FACTORY, 5);
		} else {
			Buff.prolong(defender, Wet.class, 2f);
			Buff.prolong(defender, Chill.class, 2f);
		}
	}
}
