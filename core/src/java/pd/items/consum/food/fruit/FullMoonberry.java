package pd.items.consum.food.fruit;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FullMoonStrength;
import pd.actors.buffs.Light;
import pd.actors.buffs.MoonFury;
import pd.actors.hero.Hero;
import render.utils.math.Random;
import pd.messages.InlineText;

public class FullMoonberry extends Fruit {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FullMoonberry.class)
			.t("name", "满月浆果")
			.t("desc", "野生浆果的一种，富含大量维生素和矿物质，这种浆果由于受到了月亮女神的祝福，食用后将给你提供十分强大的效果。");
	}

	{ image = ConsumFoodFoodDict.FULLMOONBERRY; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, MoonFury.class);
		Buff.affect(hero, FullMoonStrength.class);
		Buff.prolong(hero, Light.class, Light.DURATION);
		if (Random.Int(2) == 1) Buff.affect(hero, Barkskin.class).set(hero.lvl, 1);
	}
	@Override public int value() { return 5 * quantity; }
}
