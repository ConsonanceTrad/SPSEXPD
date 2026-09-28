/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class FireAmmo extends SpAmmo {
	private static final ItemSprite.Glowing ORANGE = new ItemSprite.Glowing(0xFF4400);
	@Override public ItemSprite.Glowing glowing() { return ORANGE; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (defender.sprite != null) defender.sprite.emitter().burst(FlameParticle.FACTORY, 5);
		if (Random.Int(5) == 4) Buff.affect(defender, Burning.class).reignite(defender, 5f);
		else defender.damage((int)(0.25f * damage), DamageType.FIRE_DAMAGE);
	}
}
