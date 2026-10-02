/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Item;
import render.utils.math.Random;

/** The original 2020 red packet, converting carried gold directly into damage. */
public class MoneyPack extends MissileWeapon {

	{
		image = EquipmentEquipWeaponBasicWeaponDict.MONEY_PACK;
		tier = 1;
		baseUses = 1;
		sticky = false;
		DLY = 0.1f;
	}

	public MoneyPack() { this(1); }
	public MoneyPack(int number) { quantity = number; }

	@Override public int min(int lvl) { return 1; }
	@Override public int max(int lvl) { return 1; }
	@Override public int STRReq(int lvl) { return 10; }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		damage = super.proc(attacker, defender, damage);
		int payment = Math.min(defender.HP, Dungeon.gold);
		defender.damage(payment, this);
		Dungeon.gold -= payment;
		return damage;
	}

	@Override public Item random() { quantity = Random.Int(5, 10); return this; }
	@Override public int value() { return quantity * 100; }
}
