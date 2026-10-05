package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.GrowSeed;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

public class GoldenJelly extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GoldenJelly.class)
			.t("name", "寄生孢子瓶")
			.t("desc", "释放大量的孢子飞向四周，减缓生物的移动的同时寄生目标。");
	}



	{ image = ConsumPotionSeedBasicPotionDict.LETHAL_FUNGUS; }
	public GoldenJelly() { this(1); }
	public GoldenJelly(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) Buff.affect(mob, GrowSeed.class).set(10f);
		Buff.affect(hero, Vertigo.class, 10f);
	}
}
