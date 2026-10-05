package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.plants.*;

import pd.actors.buffs.Buff;
import pd.actors.buffs.ToxicImbue;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedSeedDict;

public class ToxicEggplant extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ToxicEggplant.class)
			.t("name", "毒茄子")
			.t("desc", "腐梅草的一部分，可以食用。食用后你会中毒。");
	}

	{ image = ConsumPotionSeedSeedDict.TOXIC_EGGPLANT; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Poison.class).set(3f + hero.lvl / 5f);
	}
}
