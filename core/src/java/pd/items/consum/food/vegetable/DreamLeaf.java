package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.plants.*;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.hero.Hero;
import pd.items.consum.potions.PotionOfHealing;
import pd.messages.InlineText;

public class DreamLeaf extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DreamLeaf.class)
			.t("name", "好梦叶")
			.t("desc", "夜梦草的一部分，可以食用。食用后你会进入魔法睡眠。");
	}



	{ image = ConsumPotionSeedSeedDict.DREAM_LEAF; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, MagicalSleep.class);
	}
}
