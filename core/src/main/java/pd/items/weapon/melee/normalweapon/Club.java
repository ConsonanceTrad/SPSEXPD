package pd.items.weapon.melee.normalweapon;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Random;

public class Club extends NormalMeleeWeapon {
	public Club() { super(4, 1f, 1f, 1, 28, 40, ItemSpriteSheet.SPS_WEP_CLUB); }
	@Override protected void applyLegacyUpgrade(Stats s) { if (s.accuracy < 1.5f) s.accuracy += .05f; s.min += 3; s.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 15) Buff.prolong(defender, Paralysis.class, 2f);
		return super.proc(attacker, defender, damage);
	}
}
