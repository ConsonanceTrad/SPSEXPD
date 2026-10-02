package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.actors.Char;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Sling extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Sling.class)
			.t("name", "投石索")
			.t("desc", "由数条皮带制成的简单武器，能够把普通弹丸变成致命的投射物。");
	}

	{ image = EquipmentEquipWeaponBasicWeaponDict.SLING; }
	public Sling() { super(0, 1); }
	@Override public int min(int lvl) { return 3 + 2 * lvl; }
	@Override public int max(int lvl) { return 7 + 4 * lvl; }
	@Override public int damageRoll(Char owner) { return Random.Int(min(), max()) / 2; }
}
