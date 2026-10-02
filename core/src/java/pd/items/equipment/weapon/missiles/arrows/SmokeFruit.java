/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.Char;
import pd.actors.blobs.DarkGas;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class SmokeFruit extends SpsFruit {
	{
		image = SpecificPlaceHolderDict.SEED_HOLDER_0;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SmokeFruit.class)
			.t("name", "烟雾果")
			.t("desc", "人工种植的消逝草结出的果实。直接命中会致盲目标，落地则会令周围陷入黑暗。");
	}



	public SmokeFruit() { this(1); }
	public SmokeFruit(int number) { super(ConsumPotionSeedSeedDict.SEED_FADELEAF_0, 10, 10); quantity(number); }
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
