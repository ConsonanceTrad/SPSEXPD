/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles.arrows;

import pd.actors.Char;
import pd.actors.blobs.DarkGas;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.sprites.ItemSpriteSheet;

public class SmokeFruit extends SpsFruit {
	public SmokeFruit() { this(1); }
	public SmokeFruit(int number) { super(ItemSpriteSheet.SPS_SEED_FADELEAF, 10, 10); quantity(number); }
	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) seedAround(cell, 8, DarkGas.class);
		else super.onThrow(cell);
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.prolong(defender, Blindness.class, 5f);
		seedAround(defender.pos, 5, DarkGas.class);
		return super.proc(attacker, defender, damage);
	}
}
