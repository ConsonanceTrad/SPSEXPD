package pd.items;

import pd.sprites.ItemSpriteSheet;

public class UpgradeBlobViolet extends UpgradeBlob {
	{ image = ItemSpriteSheet.UPGRADE_GOO_VIOLET; }
	@Override protected int upgrades() { return 5; }
}
