package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.plants.*;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Dewcharge;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class DewSpore extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DewSpore.class)
			.t("name", "绿菌孢")
			.t("desc", "消逝草的一部分，可以食用。食用后，你的下一次攻击会散落黄色、红色与紫色露珠（总计 10~120 能量）。");
	}

	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.DEW_SPORE; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, DewScatter.class, DewScatter.DURATION);
	}
}
