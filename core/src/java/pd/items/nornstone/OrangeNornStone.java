package pd.items.nornstone;

import pd.sprites.ItemSpriteSheet;

public class OrangeNornStone extends NornStone {
	{
		type = 3;
		image = ItemSpriteSheet.NORN_ORANGE;
	}

	@Override
	public int value() {
		return 50 * quantity();
	}
}
