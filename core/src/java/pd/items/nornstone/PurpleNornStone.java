package pd.items.nornstone;

import pd.sprites.ItemSpriteSheet;

public class PurpleNornStone extends NornStone {
	{
		type = 4;
		image = ItemSpriteSheet.NORN_PURPLE;
	}

	@Override
	public int value() {
		return 50 * quantity();
	}
}
