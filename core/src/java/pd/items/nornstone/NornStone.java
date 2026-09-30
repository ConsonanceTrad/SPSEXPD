package pd.items.nornstone;

import pd.items.Item;
import pd.sprites.ItemSpriteSheet;

public class NornStone extends Item {

	public int type;

	{
		stackable = true;
		image = ItemSpriteSheet.NORN_GREEN;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 100 * quantity();
	}
}
