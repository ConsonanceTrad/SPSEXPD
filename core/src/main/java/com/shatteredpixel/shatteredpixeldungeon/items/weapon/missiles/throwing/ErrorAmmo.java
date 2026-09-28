/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

/** SPS-PD's deliberately overpowered error projectile. */
public class ErrorAmmo extends MissileWeapon {
	{
		image = ItemSpriteSheet.SPS_ERROR_AMMO;
		tier = 0;
		baseUses = 1;
	}

	public ErrorAmmo() { this(1); }
	public ErrorAmmo(int quantity) { quantity(quantity); }

	@Override public int min(int level) { return 10000; }
	@Override public int max(int level) { return 10000; }
	@Override public int STRReq(int level) { return 0; }
	@Override public int value() { return 0; }

	@Override
	public Item random() {
		quantity(Random.Int(5, 8));
		return this;
	}
}
