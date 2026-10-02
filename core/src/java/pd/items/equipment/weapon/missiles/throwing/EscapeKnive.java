/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.throwing;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HolyStun;
import pd.items.Item;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import render.utils.math.Random;
import pd.messages.InlineText;

public class EscapeKnive extends MissileWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EscapeKnive.class)
			.t("name", "逃脱小刀")
			.t("desc", "简单的金属片，但可以击晕猎物，帮你从困境中逃脱。");
	}



	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		tier = 1;
		baseUses = 1;
		DLY = 0.5f;
		bones = false;
	}

	public EscapeKnive() { this(1); }
	public EscapeKnive(int number) { quantity(number); }

	@Override public int min(int lvl) { return 5; }
	@Override public int max(int lvl) { return 10; }
	@Override public int STRReq(int lvl) { return 10; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		Buff.prolong(defender, HolyStun.class, 4f);
		return result;
	}

	@Override public Item random() { return quantity(Random.Int(2, 5)); }
	@Override public int value() { return quantity * 10; }
}
