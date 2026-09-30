package pd.items.weapon.guns;

import pd.actors.Char;
import pd.sprites.ItemSpriteSheet;
import render.utils.math.Random;

public class Sling extends GunWeapon {
	{ image = ItemSpriteSheet.SLING; }
	public Sling() { super(0, 1); }
	@Override public int min(int lvl) { return 3 + 2 * lvl; }
	@Override public int max(int lvl) { return 7 + 4 * lvl; }
	@Override public int damageRoll(Char owner) { return Random.Int(min(), max()) / 2; }
}
