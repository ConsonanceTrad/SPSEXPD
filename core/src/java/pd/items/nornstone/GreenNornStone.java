package pd.items.nornstone;

import pd.sprites.ItemSpriteSheet;

public class GreenNornStone extends NornStone {
	{
		type = 1;
		image = ItemSpriteSheet.NORN_GREEN;
	}

	@Override
	public int value() {
		return 50 * quantity();
	}
}
