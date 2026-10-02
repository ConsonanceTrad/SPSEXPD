package pd.items.consum.food.fruit;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.AdrenalineSurge;
import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Moonberry extends Fruit {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Moonberry.class)
			.t("name", "青色浆果")
			.t("desc", "充满力量的青蓝色浆果。食用后可暂时增强力量，还有几率使皮肤硬化。");
	}

	{ image = ConsumFoodFoodDict.MOONBERRY; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, AdrenalineSurge.class).reset(1, 40f);
		if (Random.Int(2) == 0) Buff.affect(hero, Barkskin.class).set(hero.lvl, 30);
	}
}
