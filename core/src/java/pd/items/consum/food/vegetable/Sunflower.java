package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.plants.*;

import pd.actors.buffs.Buff;
import pd.actors.buffs.MindVision;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class Sunflower extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Sunflower.class)
			.t("name", "向日葵")
			.t("desc", "消逝草的一部分，可以食用。白天食用会传送回楼层入口，夜晚则随机传送。");
	}

	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.SUNFLOWER; }
	@Override protected void onEat(Hero hero) {
		int target = FullMoonStrength.isNightNow() ? Dungeon.level.randomRespawnCell(hero) : Dungeon.level.entrance();
		ScrollOfTeleportation.appear(hero, target);
	}
}
