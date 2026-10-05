package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.plants.*;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.FireImbue;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Chili extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Chili.class)
			.t("name", "辣椒")
			.t("desc", "火焰花的一部分，可以食用。食用后你会着火，但在 2 回合内免疫火焰伤害。");
	}

	{ image = ConsumPotionSeedSeedDict.FIRE_PEPPER; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Burning.class);
		Buff.affect(hero, FireImmunity.class, FireImmunity.DURATION);
	}
}
