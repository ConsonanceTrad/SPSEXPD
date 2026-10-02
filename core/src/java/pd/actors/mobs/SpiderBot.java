/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.consum.food.BugMeat;
import pd.sprites.SpiderBotSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the scavenger. */
public class SpiderBot extends SpsCityMobs.SpiderBot {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SpiderBot.class)
			.t("name", "贪食者")
			.t("desc", "战争后期出现在城区内的生物。它们以尸体为食，并将幼体射到敌对生物身上。死亡时，体内的幼体还会洒落一地。")
			.t("yell", "奇怪的虫子爬进了你的背包。");
	}




	{
		spriteClass = SpiderBotSprite.class;
	}

	@Override
	public Item SupercreateLoot() {
		return new BugMeat();
	}

	public static Class<?> specialLootType() {
		return BugMeat.class;
	}
}
