package pd.items.consum.food.fruit;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Durian extends Fruit {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Durian.class)
			.t("name", "榴莲")
			.t("desc", "坚果林的秘密武器之一。带刺的果肉会赋予食用者强韧的树肤保护。");
	}



	{ image = ConsumPotionSeedSeedDict.DURIAN; energy = Hunger.HUNGRY / 3f; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Barkskin.class).set(4 + hero.lvl / 3, 30);
	}
	@Override public int value() { return 5 * quantity; }
}
