package pd.items.quest;

import pd.items.Item;
import pd.sprites.ItemSpriteSheet;

public class GnollClothes extends Item {
	{
		image = ItemSpriteSheet.GNOLL_CLOTHES;
		stackable = true;
		unique = true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}
}
