package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.plants.*;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.buffs.BlobImmunity;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Marigold extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Marigold.class)
			.t("name", "金盏花")
			.t("desc", "金盏花的一部分，可以食用。食用后你会被致盲。");
	}

	{ image = ConsumPotionSeedSeedDict.MARIGOLD; }
	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Blindness.class, Blindness.DURATION);
	}
}
