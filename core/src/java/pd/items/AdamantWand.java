package pd.items;
import pd.sprites.ItemSpriteSheet;
public class AdamantWand extends Item {
	{ image = ItemSpriteSheet.ADAMANT_WAND; unique = true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 150 * quantity; }
}
