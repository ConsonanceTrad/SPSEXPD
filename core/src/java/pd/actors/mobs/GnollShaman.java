/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.actors.blobs.Electricity;
import pd.items.Item;
import pd.items.equipment.artifacts.SandalsOfNature;
import pd.items.consum.potions.PotionOfLevitation;
import pd.items.consum.scrolls.ScrollOfRegrowth;
import pd.sprites.GnollShamanSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the cave shaman. */
public class GnollShaman extends SpsCaveMobs.GnollShaman {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GnollShaman.class)
			.t("name", "豺狼萨满")
			.t("desc", "那些最具有智慧的豺狼可以掌握萨满魔法，豺狼萨满由于缺少力量所以更喜欢使用战斗法术。任何敢于质疑它们在部落里地位的人都会被萨满用法术毫不留情地消灭。");
	}


	{
		spriteClass = GnollShamanSprite.class;
		properties.add(Property.ORC);
		properties.add(Property.MAGICER);
		resistances.add(Electricity.class);
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(new ScrollOfRegrowth(), new PotionOfLevitation(), new SandalsOfNature());
	}

	public static Class<?>[] specialLootTypes() {
		return new Class<?>[]{ScrollOfRegrowth.class, PotionOfLevitation.class, SandalsOfNature.class};
	}
}
