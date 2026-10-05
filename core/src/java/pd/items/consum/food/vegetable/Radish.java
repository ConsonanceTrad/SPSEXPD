package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.plants.*;

import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class Radish extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Radish.class)
			.t("name", "萝卜")
			.t("desc", "地缚根的一部分，可以食用。食用后获得植物护甲，强度为地缚根的一半。");
	}

	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.RADISH; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Earthroot.Armor.class).level(hero.HT / 2);
	}
}
