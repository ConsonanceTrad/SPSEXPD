/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import render.utils.math.Random;
import pd.messages.InlineText;

public class NutCake extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(NutCake.class)
			.t("name", "坚果布丁")
			.t("desc", "浓郁的坚果甜点，能永久提高生命力、治疗伤势并提供物理护盾。");
	}

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
