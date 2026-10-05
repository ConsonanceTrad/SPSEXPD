package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.plants.*;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.FrostImbue;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class IceMint extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(IceMint.class)
			.t("name", "冰薄荷")
			.t("desc", "寒冰草的一部分，可以食用。食用后你会被短暂冰冻。");
	}

	{ image = ConsumPotionSeedSeedDict.ICE_MINT; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Frost.class, Frost.DURATION);
	}
}
