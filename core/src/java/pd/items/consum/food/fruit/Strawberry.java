package pd.items.consum.food.fruit;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.buffs.Levitation;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Strawberry extends Fruit {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Strawberry.class)
			.t("name", "草莓")
			.t("desc", "据说生长在宫殿境内最高山峰的稀有果实。食用后身体会轻盈到足以漂浮。");
	}



	{ image = ConsumFoodFoodDict.STRAWBERRY; energy = 30f; /*SPSEXPD: 原 Hunger.HUNGRY / 10f*/ }
	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Levitation.class, 20f);
	}
	@Override public int value() { return 5 * quantity; }
}
