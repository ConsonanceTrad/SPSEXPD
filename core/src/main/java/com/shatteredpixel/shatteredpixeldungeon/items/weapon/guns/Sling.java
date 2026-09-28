package com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Sling extends GunWeapon {
	{ image = ItemSpriteSheet.SLING; }
	public Sling() { super(0, 1); }
	@Override public int min(int lvl) { return 3 + 2 * lvl; }
	@Override public int max(int lvl) { return 7 + 4 * lvl; }
	@Override public int damageRoll(Char owner) { return Random.Int(min(), max()) / 2; }
}
