/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles.arrows;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.blobs.ParalyticGas;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;

public class CharmFruit extends SpsFruit {
	public CharmFruit() { this(1); }
	public CharmFruit(int number) { super(SpecificPlaceHolderDict.SOMETHING_0, 10, 10); quantity(number); }
	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) seed(cell, 10, ParalyticGas.class);
		else super.onThrow(cell);
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.prolong(defender, Charm.class, 10f).object = attacker.id();
		Buff.prolong(defender, Amok.class, 10f);
		return super.proc(attacker, defender, damage);
	}
}
