/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.EquipmentEquipArmorUniqueArmorDict;

import pd.items.equipment.rings.Ring;
import pd.messages.InlineText;

/** Otiluke's spectacles. The original MagicSight buff is intentionally passive. */
public class Spectacles extends Ring {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Spectacles.class)
			.t("name", "眼镜")
			.t("desc", "十分普通的眼镜，但是能够强化使用者的灵能。");
	}



	public Spectacles() {
		anonymize();
		image = EquipmentEquipArmorUniqueArmorDict.SPECTACLES;
	}

	@Override
	protected RingBuff buff() {
		return new MagicSight();
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public int value() {
		return 500 * quantity;
	}

	public class MagicSight extends RingBuff {
	}
}
