package pd.items.consum.food.processed;

import pd.actors.buffs.ArcaneArmor;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class CrystalShard extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.CRYSTAL_SHARD; }
	static {
		InlineText.of(CrystalShard.class)
			.t("name", "水晶碎片")
			.t("desc", "水晶花结出的碎片，看上去不是食物。可以作为炼金或烹饪原料。");
	}

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Barrier.class).incShield(Math.max(3, hero.HT / 4));
		Buff.affect(hero, ArcaneArmor.class).set(5 + hero.lvl / 3, 30);
	}
}
