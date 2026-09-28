/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;

/** Compatibility base for SPS-PD's identified, non-upgradable disposable missiles. */
public abstract class TossWeapon extends MissileWeapon {
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
