package pd.items.consum.food.fruit;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Foresight;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Blueberry extends Fruit {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Blueberry.class)
			.t("name", "蓝色浆果")
			.t("desc", "充满知识的神秘浆果。食用后可获得先见效果，在探索时发现附近的地形、秘密与敌人。");
	}

	{ image = ConsumFoodFoodDict.BLUEBERRY; }
	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Foresight.class, Foresight.DURATION);
	}
}
