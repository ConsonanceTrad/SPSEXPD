/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.throwing;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.items.Item;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import render.utils.math.Random;
import pd.messages.InlineText;

/** SPS-PD's deliberately overpowered error projectile. */
public class ErrorAmmo extends MissileWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ErrorAmmo.class)
			.t("name", "错误弹丸")
			.t("desc", "这个是个错误");
	}

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
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
