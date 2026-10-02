/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.spammo;

import pd.atlas.items.ConsumUsefulUsefulDict;

import pd.actors.Char;
import pd.items.Item;

public abstract class SpAmmo extends Item {
	{
		image = ConsumUsefulUsefulDict.SP_AMMO;
		stackable = false;
	}

	public abstract void onHit(Char attacker, Char defender, int damage);
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }
}
