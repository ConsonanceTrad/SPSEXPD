package pd.items.weapon.melee.normalweapon;

import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Random;

public class Handaxe extends NormalMeleeWeapon {
	public Handaxe() { super(2, 1f, 1f, 1, 11, 22, ItemSpriteSheet.SPS_WEP_HANDAXE); }
	@Override protected void applyLegacyUpgrade(Stats s) {
		if (s.accuracy < 1.5f) s.accuracy += .1f;
		if (s.accuracy > 1.4f && s.strength > 10) s.strength--;
		s.min += 2; s.max += 3;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 40) Buff.affect(defender, Bleeding.class).set(safeRandom(2, damage));
		return super.proc(attacker, defender, damage);
	}
}
