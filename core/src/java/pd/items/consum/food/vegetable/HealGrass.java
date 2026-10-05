package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.plants.*;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.ArcaneArmor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedSeedDict;

public class HealGrass extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HealGrass.class)
			.t("name", "生命草")
			.t("desc", "阳春草的一部分，可以食用。食用后恢复 20% 的最大生命值。");
	}



	{ image = ConsumPotionSeedSeedDict.HEAL_GRASS; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Healing.class).setHeal(Math.max(1, Math.round(hero.HT * 0.2f)), 0.25f, 0, true);
	}
}
