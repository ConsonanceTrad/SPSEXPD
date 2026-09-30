/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.spammo;

import pd.actors.Char;
import pd.items.Item;
import pd.sprites.ItemSpriteSheet;

public abstract class SpAmmo extends Item {
	{
		image = ItemSpriteSheet.SP_AMMO;
		stackable = false;
	}

	public abstract void onHit(Char attacker, Char defender, int damage);
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }
}
