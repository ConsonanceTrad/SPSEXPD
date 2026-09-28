package com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ToyGun extends GunWeapon {
	{
		image = ItemSpriteSheet.TOY_GUN;
		reinforced = true;
	}
	public ToyGun() { super(1, 10); }
	@Override public int min(int lvl) { return 1 + lvl; }
	@Override public int max(int lvl) { return 10 + 3 * lvl; }
	@Override protected boolean supportsSpecialAmmo() { return false; }
	@Override protected boolean canShoot(Hero hero) { return isEquipped(hero); }
	@Override protected boolean canReload(Hero hero) { return isEquipped(hero); }
	@Override protected boolean causesVertigo() { return false; }
	@Override protected boolean addsEnergyDamage() { return false; }
	@Override protected float reloadTime(Hero hero, boolean automatic, int loaded) {
		return automatic ? 3f : loaded / 2f;
	}
}
