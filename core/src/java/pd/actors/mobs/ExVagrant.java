/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.equipment.weapon.melee.special.SJRBMusic;
import pd.sprites.ExVagrantSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the infected vagrant. */
public class ExVagrant extends SpsSewerMobs.ExVagrant {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ExVagrant.class)
			.t("name", "感染流浪者")
			.t("desc", "被源石感染的流浪者，有着极高的恢复能力生命偷取，并且会在死亡时污染周围的环境。");
	}




	{
		spriteClass = ExVagrantSprite.class;
		properties.add(Property.HUMAN);
	}

	@Override
	public Item SupercreateLoot() {
		return new SJRBMusic();
	}

	public static Class<?> specialLootType() {
		return SJRBMusic.class;
	}
}
