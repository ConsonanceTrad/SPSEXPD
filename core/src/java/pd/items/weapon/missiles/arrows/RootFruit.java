/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles.arrows;

import pd.actors.Char;
import pd.actors.blobs.Web;
import pd.actors.blobs.damageblobs.EarthEffectDamage;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Roots;
import pd.sprites.ItemSpriteSheet;

public class RootFruit extends SpsFruit {
	public RootFruit() { this(1); }
	public RootFruit(int number) { super(ItemSpriteSheet.SPS_SEED_EARTHROOT, 20, 20); quantity(number); }
	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) {
			seed(cell, 4, Web.class);
			seedAround(cell, 4, EarthEffectDamage.class);
		} else super.onThrow(cell);
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.prolong(defender, Roots.class, 8f);
		return super.proc(attacker, defender, damage);
	}
}
