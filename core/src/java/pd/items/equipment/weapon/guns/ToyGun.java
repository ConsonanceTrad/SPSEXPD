package pd.items.equipment.weapon.guns;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;

public class ToyGun extends GunWeapon {
	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
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
