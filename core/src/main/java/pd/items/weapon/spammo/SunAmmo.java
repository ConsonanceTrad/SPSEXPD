/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.GrowSeed;
import pd.actors.damagetype.DamageType;
import pd.effects.particles.EarthParticle;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

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
