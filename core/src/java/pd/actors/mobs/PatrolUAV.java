/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.actors.blobs.effectblobs.ElectriShock;
import pd.items.Item;
import pd.items.consum.scrolls.ScrollOfRecharging;
import pd.items.equipment.wands.WandOfLightning;
import pd.items.equipment.wands.WandOfTCloud;
import pd.sprites.PatrolUAVSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the patrol drone. */
public class PatrolUAV extends SpsSewerMobs.PatrolUAV {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(PatrolUAV.class)
			.t("name", "巡逻无人机")
			.t("desc", "一种高科技的无人机，被用于清理下水道垃圾。\n机械");
	}




	{
		spriteClass = PatrolUAVSprite.class;
		properties.remove(Property.INORGANIC);
		properties.add(Property.MECH);
		immunities.add(ElectriShock.class);
		immunities.add(WandOfLightning.class);
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(new ScrollOfRecharging(), new WandOfTCloud());
	}

	public static Class<?>[] specialLootTypes() {
		return new Class<?>[]{ScrollOfRecharging.class, WandOfTCloud.class};
	}
}
