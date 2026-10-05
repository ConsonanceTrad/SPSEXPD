package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.plants.*;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.ArcaneArmor;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.PhysicalEmpower;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedSeedDict;

public class BattleFlower extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BattleFlower.class)
			.t("name", "星花瓣")
			.t("desc", "星陨花的一部分，可以食用。食用后获得相当于当前等级所需经验 30% 的经验值。");
	}



	{ image = ConsumPotionSeedSeedDict.YAM_FLOWER; }
	@Override protected void onEat(Hero hero) {
		hero.earnExp(Math.max(1, Math.round(hero.maxExp() * 0.3f)), this.getClass());
	}
}
