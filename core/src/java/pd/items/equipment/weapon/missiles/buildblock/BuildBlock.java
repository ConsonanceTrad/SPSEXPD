/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.buildblock;

import pd.items.equipment.weapon.missiles.MissileWeapon;

/** Base class for the reusable SPS terrain-building projectiles. */
public abstract class BuildBlock extends MissileWeapon {

	{
		tier = 1;
		baseUses = 100f;
		levelKnown = true;
		cursedKnown = true;
	}

	@Override public int defaultQuantity() { return 1; }
	@Override public int min(int level) { return 1; }
	@Override public int max(int level) { return 1; }
	@Override public int STRReq(int level) { return 10; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
