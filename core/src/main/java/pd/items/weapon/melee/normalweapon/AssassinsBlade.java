package pd.items.weapon.melee.normalweapon;

import pd.actors.Char;
import pd.sprites.ItemSpriteSheet;
import render.utils.Random;

public class AssassinsBlade extends NormalMeleeWeapon {
	public AssassinsBlade() { super(4, 1f, 1f, 1, 26, 34, ItemSpriteSheet.SPS_WEP_ASSASSINS_BLADE); }
	@Override protected void applyLegacyUpgrade(Stats s) { s.min += 3; s.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 50) {
			int roll = attackerRoll(attacker);
			defender.damage(safeRandom(roll / 4, roll / 2), this);
		}
		return super.proc(attacker, defender, damage);
	}
}
