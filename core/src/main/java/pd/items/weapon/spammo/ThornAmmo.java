/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

public class ThornAmmo extends SpAmmo {
	private static final ItemSprite.Glowing BROWN = new ItemSprite.Glowing(0xCC6600);
	@Override public ItemSprite.Glowing glowing() { return BROWN; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Random.Int(5) == 3) {
			int upper = Math.max(5, damage);
			Buff.affect(defender, Bleeding.class).set(Random.IntRange(5, upper));
		} else {
			Buff.prolong(defender, Cripple.class, 3f);
		}
		defender.damage((int)(0.20f * damage), Bleeding.class);
	}
}
