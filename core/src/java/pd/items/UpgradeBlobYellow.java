package pd.items;

import pd.sprites.ItemSpriteSheet;

public class UpgradeBlobYellow extends UpgradeBlob {
	{ image = ItemSpriteSheet.UPGRADE_GOO_YELLOW; }
	@Override protected int upgrades() { return 1; }
}
