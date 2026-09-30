/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Vertigo;
import pd.actors.damagetype.DamageType;
import pd.effects.Speck;
import pd.sprites.ItemSprite;
import render.utils.Random;

public class BlindAmmo extends SpAmmo {
	private static final ItemSprite.Glowing YELLOW = new ItemSprite.Glowing(0xFFFF44);
	@Override public ItemSprite.Glowing glowing() { return YELLOW; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Random.Int(5) == 3) {
			Buff.prolong(defender, Blindness.class, 3f);
			if (defender.sprite != null) defender.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 6);
		} else if (Random.Int(5) == 3) {
			Buff.prolong(defender, Vertigo.class, 3f);
		} else {
			defender.damage((int)(0.15f * damage), DamageType.ENERGY_DAMAGE);
		}
	}
}
