/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Shocked;
import pd.actors.damagetype.DamageType;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

public class StormAmmo extends SpAmmo {
	private static final ItemSprite.Glowing WHITE = new ItemSprite.Glowing(0xFFFFFF);
	@Override public ItemSprite.Glowing glowing() { return WHITE; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Random.Int(6) == 3) Buff.affect(defender, Shocked.class).level(2);
		else defender.damage((int)(0.40f * damage), DamageType.SHOCK_DAMAGE);
	}
}
