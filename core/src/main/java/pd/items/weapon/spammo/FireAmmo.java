/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.damagetype.DamageType;
import pd.effects.particles.FlameParticle;
import pd.sprites.ItemSprite;
import render.utils.Random;

public class FireAmmo extends SpAmmo {
	private static final ItemSprite.Glowing ORANGE = new ItemSprite.Glowing(0xFF4400);
	@Override public ItemSprite.Glowing glowing() { return ORANGE; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (defender.sprite != null) defender.sprite.emitter().burst(FlameParticle.FACTORY, 5);
		if (Random.Int(5) == 4) Buff.affect(defender, Burning.class).reignite(defender, 5f);
		else defender.damage((int)(0.25f * damage), DamageType.FIRE_DAMAGE);
	}
}
