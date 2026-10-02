/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.equipment.weapon.missiles.throwing.Skull;
import pd.sprites.DemonGooSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for demon goo. */
public class DemonGoo extends SpsHallsMobs.DemonGoo {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DemonGoo.class)
			.t("name", "暗黑黏咕")
			.t("desc", "恶魔黏咕通常由熔岩、黏液、暗能量与恶意构成，其中绝大部分都是黑暗。不要任其分裂，即使最弱的碎块也能再次变强。")
			.t("divide", "暗黑黏咕分裂了！");
	}


	{
		spriteClass = DemonGooSprite.class;
		properties.add(Property.ELEMENT);
	}

	@Override
	protected SpsHallsMobs.DemonGoo newSplit() {
		return new DemonGoo();
	}

	@Override
	public Item SupercreateLoot() {
		return new Skull(3);
	}

	public static Class<?> specialLootType() {
		return Skull.class;
	}
}
