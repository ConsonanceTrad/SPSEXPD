package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class UpgradeBlobViolet extends UpgradeBlob {
	{ image = ItemSpriteSheet.UPGRADE_GOO_VIOLET; }
	@Override protected int upgrades() { return 5; }
}
