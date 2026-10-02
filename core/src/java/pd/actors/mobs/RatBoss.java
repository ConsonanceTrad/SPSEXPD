/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.SaveYourLife;
import pd.sprites.RatBossSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the leader rat. */
public class RatBoss extends SpsSewerMobs.RatBoss {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(RatBoss.class)
			.t("name", "领头鼠")
			.t("desc", "领头鼠是鼠群的头领。虽然它不像鼠王一样有威信，但它依然可以叫来鼠群。")
			.t("spawn", "这里出现了一群老鼠！");
	}


	{
		spriteClass = RatBossSprite.class;
	}

	public static Class<?> specialLootType() {
		return SaveYourLife.class;
	}
}
