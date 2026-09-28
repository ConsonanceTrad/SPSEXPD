package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class UpgradeBlobYellow extends UpgradeBlob {
	{ image = ItemSpriteSheet.UPGRADE_GOO_YELLOW; }
	@Override protected int upgrades() { return 1; }
}
