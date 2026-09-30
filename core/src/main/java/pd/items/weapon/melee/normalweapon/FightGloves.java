package pd.items.weapon.melee.normalweapon;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Random;

public class FightGloves extends NormalMeleeWeapon {
	public FightGloves() { super(2, 1f, 1f, 1, 11, 17, ItemSpriteSheet.SPS_WEP_FIGHT_GLOVES); }
	@Override protected void applyLegacyUpgrade(Stats s) {
		if (s.accuracy < 1.5f) s.accuracy += .1f;
		if (s.accuracy > 1.45f && s.delay > .55f) s.delay -= .1f;
		if (s.delay < .55f && s.reach < 2) s.reach++;
		s.min++; s.max++;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 40) Buff.prolong(defender, Paralysis.class, 2f);
		return super.proc(attacker, defender, damage);
	}
}
