package pd.items.weapon.melee.normalweapon;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Random;

public class MageBook extends NormalMeleeWeapon {
	public MageBook() { super(1, 1f, 1f, 1, 1, 10, ItemSpriteSheet.SPS_WEP_MAGE_BOOK); }
	@Override protected void applyLegacyUpgrade(Stats s) {
		if (s.strength > 1) s.strength--;
		if (s.strength < 3 && s.reach < 3) s.reach++;
		s.min++; s.max += 2;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 50) Buff.prolong(defender, Paralysis.class, 2f);
		return super.proc(attacker, defender, damage);
	}
}
