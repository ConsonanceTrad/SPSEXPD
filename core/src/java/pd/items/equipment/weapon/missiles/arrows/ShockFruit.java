/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.Char;
import pd.actors.blobs.damageblobs.ShockEffectDamage;
import pd.actors.blobs.effectblobs.ElectriShock;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Shocked;
import pd.messages.InlineText;

public class ShockFruit extends SpsFruit {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ShockFruit.class)
			.t("name", "乱流果")
			.t("desc", "人工种植的风暴藤结出的果实。直接命中会电击目标，落地则会释放电流。");
	}



	public ShockFruit() { this(1); }
	public ShockFruit(int number) { super(ConsumPotionSeedSeedDict.SEED_STORMVINE, 10, 10); quantity(number); }
	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) {
			seed(cell, 4, ElectriShock.class);
			seedAround(cell, 4, ShockEffectDamage.class);
		} else super.onThrow(cell);
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Shocked.class).level(5);
		return super.proc(attacker, defender, damage);
	}
}
