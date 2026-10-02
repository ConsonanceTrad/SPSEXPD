package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Vertigo;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

public class WoodenAmmo extends SpAmmo {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);
	@Override public ItemSprite.Glowing glowing() { return BLACK; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		Buff.prolong(defender, Vertigo.class, 3f);
		if (Random.Int(8) == 1) Buff.prolong(defender, Paralysis.class, 3f);
	}
}
