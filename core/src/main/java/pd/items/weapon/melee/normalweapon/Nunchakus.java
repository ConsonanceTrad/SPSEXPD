package pd.items.weapon.melee.normalweapon;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HolyStun;
import pd.sprites.ItemSpriteSheet;
import render.utils.Random;

public class Nunchakus extends NormalMeleeWeapon {
	public Nunchakus() { super(3, 1f, 1f, 1, 18, 27, ItemSpriteSheet.SPS_WEP_NUNCHAKUS); }
	@Override protected void applyLegacyUpgrade(Stats s) { if (s.delay > .75f) s.delay -= .05f; s.min += 2; s.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 20) Buff.prolong(defender, HolyStun.class, 2f);
		return super.proc(attacker, defender, damage);
	}
}
