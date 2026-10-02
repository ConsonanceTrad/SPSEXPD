/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import render.utils.math.Random;

public class NutCake extends CompleteFood {
	{
		image = ConsumFoodFoodDict.NUT_CAKE;
		energy = 450f;
	}
	@Override protected void doEat(Hero hero) {
		hero.HTBoost += Random.Int(7, 14);
		int permanentHT = hero.permanentHT();
		heal(hero, (permanentHT - hero.HP) / 2);
		Buff.affect(hero, ShieldArmor.class).level(hero.HT / 3);
		hero.updateHT(true);
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 4);
	}
	@Override public int value() { return quantity; }
}
