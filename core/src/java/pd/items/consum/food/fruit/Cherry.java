package pd.items.consum.food.fruit;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.Dungeon;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.items.equipment.bombs.Bomb;

public class Cherry extends Fruit {
	{ image = ConsumFoodFoodDict.CHERRY; energy = Hunger.HUNGRY / 10f; }
	@Override protected void onEat(Hero hero) {
		Dungeon.level.drop(new Bomb(), hero.pos).sprite.drop();
	}
	@Override public int value() { return 5 * quantity; }
}
