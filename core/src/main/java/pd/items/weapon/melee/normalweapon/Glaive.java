package pd.items.weapon.melee.normalweapon;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.sprites.ItemSpriteSheet;
import render.utils.Random;

public class Glaive extends NormalMeleeWeapon {
	public Glaive() { super(4, 1f, 1.75f, 2, 42, 60, ItemSpriteSheet.SPS_WEP_GLAIVE); }
	@Override protected void applyLegacyUpgrade(Stats s) { if (s.delay > 1.4f) s.delay -= .05f; s.min++; s.max += 6; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 20) Buff.affect(defender, Cripple.class, 3f);
		return super.proc(attacker, defender, damage);
	}
}
