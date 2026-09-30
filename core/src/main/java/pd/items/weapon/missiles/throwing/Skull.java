/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles.throwing;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.SoulMark;
import pd.items.Item;
import pd.items.weapon.missiles.MissileWeapon;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Random;

public class Skull extends MissileWeapon {

	{
		image = ItemSpriteSheet.LEGACY_SKULL;
		tier = 1;
		baseUses = 1;
		DLY = 0.1f;
		levelKnown = true;
	}

	public Skull() { this(1); }
	public Skull(int number) { quantity(number); }
	@Override public int min(int lvl) { return 1; }
	@Override public int max(int lvl) { return 4; }
	@Override public int STRReq(int lvl) { return 10; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, SoulMark.class, 10f);
		return super.proc(attacker, defender, damage);
	}
	@Override public Item random() { return quantity(Random.Int(3, 5)); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return quantity * 10; }
}
