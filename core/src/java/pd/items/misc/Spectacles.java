/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.items.rings.Ring;
import pd.sprites.ItemSpriteSheet;

/** Otiluke's spectacles. The original MagicSight buff is intentionally passive. */
public class Spectacles extends Ring {
	public Spectacles() {
		anonymize();
		image = ItemSpriteSheet.SPECTACLES;
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
