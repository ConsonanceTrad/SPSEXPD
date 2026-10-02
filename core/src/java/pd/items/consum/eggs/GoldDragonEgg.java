/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.BugDragon;
import pd.actors.mobs.pets.GoldDragon;
import pd.actors.mobs.pets.LegacyPet;
import render.utils.math.Random;

import java.util.Calendar;
import pd.messages.InlineText;

public class GoldDragonEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GoldDragonEgg.class)
			.t("name", "金龙之魂")
			.t("desc", "万物合为一体的龙之灵魂。");
	}



	{
		image = ConsumSummorDict.GOLD_DRAGON_EGG_0;
		moves = 2000; burns = freezes = poisons = lits = darks = lights = 20;
	}
	@Override protected LegacyPet hatchling() {
		return Calendar.getInstance().get(Calendar.MONTH) == Calendar.SEPTEMBER || Random.Int(50) == 0
				? new BugDragon() : new GoldDragon();
	}
	@Override public int value() { return 500 * quantity; }
}
