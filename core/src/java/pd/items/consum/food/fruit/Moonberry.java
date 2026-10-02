package pd.items.consum.food.fruit;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.AdrenalineSurge;
import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import render.utils.math.Random;

public class Moonberry extends Fruit {
	{ image = ConsumFoodFoodDict.MOONBERRY; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, AdrenalineSurge.class).reset(1, 40f);
		if (Random.Int(2) == 0) Buff.affect(hero, Barkskin.class).set(hero.lvl, 30);
	}
}
