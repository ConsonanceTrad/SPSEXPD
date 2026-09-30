/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.melee.special;

import pd.actors.Char;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Terror;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Random;

public class KeyWeapon extends SpsSpecialMeleeWeapon {
	public KeyWeapon() { super(1, 1f, 1f, 1, 1, 10, ItemSpriteSheet.SPS_KEY_WEAPON); }

	@Override public int proc(Char attacker, Char defender, int damage) {
		int roll = Math.max(0, attacker.damageRoll());
		if (Random.Int(100) < 40) Buff.prolong(defender, Paralysis.class, 2f);
		if (Random.Int(100) < 40) defender.damage(safeRandom(roll / 2, roll / 4 * 3), this);
		if (Random.Int(100) < 40) {
			Buff.affect(defender, Charm.class, 5f).object = attacker.id();
			Buff.affect(defender, Terror.class, 5f).object = attacker.id();
			Buff.affect(defender, Amok.class, 5f);
		}
		return super.proc(attacker, defender, damage);
	}
}
