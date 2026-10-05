package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.plants.*;

import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class RainbowPansy extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RainbowPansy.class)
			.t("name", "七色堇")
			.t("desc", "彩虹三色堇的一部分，可以食用。食用后获得 70 回合的幸运 +2 加成。");
	}

	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.RAINBOW_PANSY; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, LuckyMoment.class, 70f);
	}
}
