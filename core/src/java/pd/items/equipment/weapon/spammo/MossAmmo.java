/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Poison;
import pd.actors.damagetype.DamageType;
import pd.effects.particles.EarthParticle;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

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
