/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.Char;
import pd.actors.blobs.damageblobs.FireEffectDamage;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.messages.InlineText;

public class FireFruit extends SpsFruit {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FireFruit.class)
			.t("name", "火焰果")
			.t("desc", "人工种植的烈焰花结出的果实。直接命中会点燃目标，落地则会释放火焰。");
	}



	public FireFruit() { this(1); }
	public FireFruit(int number) { super(ConsumPotionSeedSeedDict.SEED_FIREBLOOM, 10, 10); quantity(number); }
	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) {
			seed(cell, 4, pd.actors.blobs.effectblobs.Fire.class);
			seedAround(cell, 4, FireEffectDamage.class);
		} else super.onThrow(cell);
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Burning.class).reignite(defender, 5f);
		return super.proc(attacker, defender, damage);
	}
}
