package pd.items.consum.food.processed;

import pd.actors.buffs.Amok;
import pd.actors.buffs.ArcaneArmor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Terror;
import pd.actors.hero.Hero;
import pd.items.consum.potions.PotionOfHealing;
import pd.messages.InlineText;

public class Sedative extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.SEDATIVE; }
	static {
		InlineText.of(Sedative.class)
			.t("name", "镇静剂")
			.t("desc", "用星花瓣配制的镇静剂，看上去不是食物。可以作为炼金或烹饪原料。");
	}

	@Override protected void onEat(Hero hero) {
		PotionOfHealing.cure(hero);
		Buff.detach(hero, Terror.class);
		Buff.detach(hero, Amok.class);
		Buff.affect(hero, ArcaneArmor.class).set(6 + hero.lvl / 3, 40);
	}
}
