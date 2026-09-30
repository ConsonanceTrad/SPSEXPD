package pd.items;
import pd.sprites.ItemSpriteSheet;
public class AdamantRing extends Item {
	{ image = ItemSpriteSheet.ADAMANT_RING; unique = true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 200 * quantity; }
}
