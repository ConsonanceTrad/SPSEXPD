package pd.items.consum.food.processed;

import pd.actors.buffs.BlobImmunity;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.consum.potions.PotionOfHealing;
import pd.messages.InlineText;

public class TransmutePowder extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.TRANSMUTE_POWDER; }
	static {
		InlineText.of(TransmutePowder.class)
			.t("name", "转换粉")
			.t("desc", "由金盏花研磨成的中和粉末，看上去不是食物。可以作为炼金或烹饪原料。");
	}

	@Override protected void onEat(Hero hero) {
		PotionOfHealing.cure(hero);
		Buff.affect(hero, BlobImmunity.class, BlobImmunity.DURATION);
	}
}
