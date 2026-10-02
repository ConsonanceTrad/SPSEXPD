/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.messages.InlineText;

public class ToxicFruit extends SpsFruit {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ToxicFruit.class)
			.t("name", "毒液果")
			.t("desc", "人工种植的断肠苔结出的果实。直接命中会使目标中毒，落地则会释放毒气。");
	}

	public ToxicFruit() { this(1); }
	public ToxicFruit(int number) { super(ConsumPotionSeedSeedDict.SEED_SORROWMOSS_0, 10, 10); quantity(number); }
	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) seed(cell, 8, ToxicGas.class);
		else super.onThrow(cell);
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Poison.class).set(damage / 2f);
		return super.proc(attacker, defender, damage);
	}
}
