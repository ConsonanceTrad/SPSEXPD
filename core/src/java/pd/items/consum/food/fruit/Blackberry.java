package pd.items.consum.food.fruit;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.buffs.MindVision;
import pd.actors.hero.Hero;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Blackberry extends Fruit {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Blackberry.class)
			.t("name", "粉色浆果")
			.t("desc", "野生浆果的一种，富含生命能量。食用后会快速恢复生命，还有几率短暂感知本层所有生物。");
	}



	{ image = ConsumFoodFoodDict.BLACKBERRY; }
	@Override protected void onEat(Hero hero) {
		int healing = Math.max(hero.HT / (Random.Int(5) == 0 ? 8 : 10), 15);
		Buff.affect(hero, Healing.class).setHeal(healing, 0.25f, 0);
		if (Random.Int(5) == 0) {
			Buff.prolong(hero, MindVision.class, MindVision.DURATION);
			Dungeon.observe();
		}
	}
}
