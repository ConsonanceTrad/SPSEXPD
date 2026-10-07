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

	//SPSEXPD: 食用时额外满足 25 点饱食——「食用」角标里的实际回复量也要算上
	@Override public float foodValue(Hero hero) {
		return super.foodValue(hero) + 25f;
	}

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Hunger.class).satisfy(25f);
	}
}
