package pd.items.nornstone;

import pd.sprites.ItemSpriteSheet;

public class BlueNornStone extends NornStone {
	{
		type = 2;
		image = ItemSpriteSheet.NORN_BLUE;
	}

	@Override
	public int value() {
		return 50 * quantity();
	}
}
