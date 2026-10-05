package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.plants.*;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Levitation;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Tulip extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Tulip.class)
			.t("name", "郁金香")
			.t("desc", "风暴藤的一部分，可以食用。食用后你会眩晕。");
	}

	{ image = ConsumPotionSeedSeedDict.TULIP; }
	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Vertigo.class, Vertigo.DURATION);
	}
}
