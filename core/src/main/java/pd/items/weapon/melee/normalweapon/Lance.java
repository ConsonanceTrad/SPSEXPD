package pd.items.weapon.melee.normalweapon;

import pd.actors.Char;
import pd.sprites.ItemSpriteSheet;
import render.utils.math.Random;

public class Lance extends NormalMeleeWeapon {
	public Lance() { super(5, 1f, 1f, 1, 35, 44, ItemSpriteSheet.SPS_WEP_LANCE); }
	@Override protected void applyLegacyUpgrade(Stats s) { s.min++; s.max += 3; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(4) == 0) {
			int roll = attackerRoll(attacker);
			defender.damage(safeRandom(roll / 4, roll / 2), this);
		}
		return super.proc(attacker, defender, damage);
	}
}
