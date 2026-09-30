package pd.items;
import pd.sprites.ItemSpriteSheet;
public class AdamantWeapon extends Item {
	{ image = ItemSpriteSheet.ADAMANT_WEAPON; unique = true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 250 * quantity; }
}
