package pd.items.weapon.melee.normalweapon;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.sprites.ItemSpriteSheet;
import render.utils.Random;

public class Spear extends NormalMeleeWeapon {
	public Spear() { super(2, 1f, 1.5f, 2, 14, 30, ItemSpriteSheet.SPS_WEP_SPEAR); }
	@Override protected void applyLegacyUpgrade(Stats s) {
		if (s.delay > 1.2f) s.delay -= .05f;
		if (s.delay < 1.2f && s.reach < 3) s.reach++;
		s.min++; s.max += 4;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 40) Buff.affect(defender, Cripple.class, 3f);
		return super.proc(attacker, defender, damage);
	}
}
