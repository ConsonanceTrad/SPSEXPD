package pd.items;

import pd.sprites.ItemSpriteSheet;

public class UpgradeBlobRed extends UpgradeBlob {
	{ image = ItemSpriteSheet.UPGRADE_GOO_RED; }
	@Override protected int upgrades() { return 3; }
}
