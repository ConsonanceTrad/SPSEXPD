/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.throwing;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.items.Item;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import render.utils.math.Random;

public class Wave extends MissileWeapon {

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		tier = 1;
		baseUses = 1;
		DLY = 0.1f;
		levelKnown = true;
	}

	public Wave() { this(1); }
	public Wave(int number) { quantity(number); }
	@Override public int min(int lvl) { return 1; }
	@Override public int max(int lvl) { return 3; }
	@Override public int STRReq(int lvl) { return 10; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Amok.class, 10f);
		Buff.affect(defender, Charm.class, 5f).object = attacker.id();
		defender.damage(defender.HP / 4, defender);
		return super.proc(attacker, defender, damage);
	}
	@Override public Item random() { return quantity(Random.Int(2, 7)); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return quantity * 10; }
}
