package pd.items.weapon.spammo;

import pd.actors.Char;

public class EmptyAmmo extends SpAmmo {
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Char.hasProp(defender, Char.Property.BOSS)) {
			defender.damage(Math.min(defender.HT / 20, 3000), this);
		}
	}
}
