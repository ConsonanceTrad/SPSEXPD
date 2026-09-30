package pd.items.weapon.spammo;

import pd.actors.Char;
import pd.sprites.ItemSprite;

public class HeavyAmmo extends SpAmmo {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);
	@Override public ItemSprite.Glowing glowing() { return BLACK; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		defender.damage((int)(0.75f * damage), attacker);
	}
}
