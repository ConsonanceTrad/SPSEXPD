/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles.arrows;

import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.sprites.ItemSpriteSheet;

public class ToxicFruit extends SpsFruit {
	public ToxicFruit() { this(1); }
	public ToxicFruit(int number) { super(ItemSpriteSheet.SPS_SEED_SORROWMOSS, 10, 10); quantity(number); }
	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) seed(cell, 8, ToxicGas.class);
		else super.onThrow(cell);
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Poison.class).set(damage / 2f);
		return super.proc(attacker, defender, damage);
	}
}
