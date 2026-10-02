/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Gold;
import pd.items.Item;
import pd.items.equipment.artifacts.MasterThievesArmband;
import pd.items.specific.sellitem.VIPcard;
import pd.sprites.GoldCollectorSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the goblin tax collector. */
public class GoldCollector extends SpsPrisonMobs.GoldCollector {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GoldCollector.class)
			.t("name", "税收官")
			.t("desc", "背着巨大口袋的哥布林税收官，会偷走你的金币并将其转化为护盾。");
	}




	{
		spriteClass = GoldCollectorSprite.class;
		properties.add(Property.GOBLIN);
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(new Gold(100), new VIPcard(), new MasterThievesArmband());
	}

	public static Class<?>[] specialLootTypes() {
		return new Class<?>[]{Gold.class, VIPcard.class, MasterThievesArmband.class};
	}
}
