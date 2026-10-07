package pd.items.consum.food.fruit;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.Dungeon;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.items.equipment.bombs.Bomb;
import pd.messages.InlineText;

public class Cherry extends Fruit {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Cherry.class)
			.t("name", "樱桃")
			.t("desc", "遗迹附近守卫植物结出的果实。它的果核极不稳定，食用后会化为一枚已点燃的炸弹。");
	}



	{ image = ConsumFoodFoodDict.CHERRY; energy = 30f; /*SPSEXPD: 原 Hunger.HUNGRY/10f*/ }
	@Override protected void onEat(Hero hero) {
		Dungeon.level.drop(new Bomb(), hero.pos).sprite.drop();
	}
	@Override public int value() { return 5 * quantity; }
}
