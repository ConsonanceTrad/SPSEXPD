package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.plants.*;

import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class QuartzFlower extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(QuartzFlower.class)
			.t("name", "水晶花")
			.t("desc", "石英花的一部分，可以食用。它是水晶植物，食用会让你损失 10% 的最大生命。");
	}

	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.QUARTZ_FLOWER; }
	@Override protected void onEat(Hero hero) {
		hero.damage(Math.max(1, hero.HT / 10), this);
	}
}
