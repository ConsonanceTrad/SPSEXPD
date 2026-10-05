package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.plants.*;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Haste;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class Sorrel extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Sorrel.class)
			.t("name", "酢浆草")
			.t("desc", "迅捷草的一部分，可以食用。食用后获得 4 回合的时间气泡。");
	}

	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.SORREL; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Swiftthistle.TimeBubble.class).reset(4);
	}
}
