/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.actors.buffs.Burning;
import pd.items.equipment.wands.WandOfFirebolt;
import pd.sprites.ZombieSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the infected zombie. */
public class Zombie extends SpsPrisonMobs.Zombie {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Zombie.class)
			.t("name", "丧尸")
			.t("desc", "这并不是行动迟缓、毫无思维的普通尸体，而是一名感染者。");
	}




	{
		spriteClass = ZombieSprite.class;
		weaknesses.add(Burning.class);
		weaknesses.add(WandOfFirebolt.class);
	}

	public static Class<?> specialLootType() {
		//SPSEXPD: 安卡不再有「未祝福」形态
		return pd.items.Ankh.class;
	}
}
