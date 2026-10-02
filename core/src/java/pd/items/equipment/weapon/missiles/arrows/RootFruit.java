/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.Char;
import pd.actors.blobs.Web;
import pd.actors.blobs.damageblobs.EarthEffectDamage;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Roots;
import pd.messages.InlineText;

public class RootFruit extends SpsFruit {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RootFruit.class)
			.t("name", "缠绕果")
			.t("desc", "人工种植的地缚根结出的果实。直接命中会缠绕目标，落地则会散布根须与蛛网。");
	}



	public RootFruit() { this(1); }
	public RootFruit(int number) { super(ConsumPotionSeedSeedDict.SEED_EARTHROOT_0, 20, 20); quantity(number); }
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
