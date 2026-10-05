package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.plants.*;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class NutVegetable extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(NutVegetable.class)
			.t("name", "坚果藤")
			.t("desc", "坚果藤的一部分，可以食用。食用后额外恢复 25 点饱食度。");
	}



	{ image = ConsumPotionSeedSeedDict.NUT_VEGETABLE; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Hunger.class).satisfy(25f);
	}
}
