/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class MemoryOfSand extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(MemoryOfSand.class)
			.t("desc", "这是一个对发芽改有重大贡献的玩家。")
			.t("name", "义往尘沙")
			.t("yell1", "嗨，酒馆现在开放了。随便选个食物尝尝吧。")
			.t("yell2", "原来我也是个冒险者，直到我试了试发芽改...");
	}



	public MemoryOfSand() {
		configure(Spec.MEMORY_OF_SAND);
		spriteClass = pd.sprites.MemoryOfSandSprite.class;
	}
}
