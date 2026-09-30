package pd.items.weapon.melee.normalweapon;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class WarHammer extends NormalMeleeWeapon {
	public WarHammer() { super(5, 1f, 1f, 1, 41, 56, ItemSpriteSheet.SPS_WEP_WAR_HAMMER); }
	@Override protected void applyLegacyUpgrade(Stats s) { if (s.accuracy < 2f) s.accuracy += .1f; s.min++; s.max += 3; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 10) Buff.prolong(defender, Paralysis.class, 2f);
		return super.proc(attacker, defender, damage);
	}
}
