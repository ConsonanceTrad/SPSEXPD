/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.Char;
import pd.actors.blobs.damageblobs.IceEffectDamage;
import pd.actors.blobs.effectblobs.FrostCloud;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FrostIce;

public class IceFruit extends SpsFruit {
	public IceFruit() { this(1); }
	public IceFruit(int number) { super(ConsumPotionSeedSeedDict.SEED_ICECAP, 10, 10); quantity(number); }
	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) {
			seed(cell, 4, FrostCloud.class);
			seedAround(cell, 4, IceEffectDamage.class);
		} else super.onThrow(cell);
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, FrostIce.class).level(5);
		return super.proc(attacker, defender, damage);
	}
}
