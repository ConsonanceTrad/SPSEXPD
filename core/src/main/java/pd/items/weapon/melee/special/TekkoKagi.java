/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.melee.special;

import pd.Badges;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.effects.particles.ShadowParticle;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Random;

public class TekkoKagi extends SpsSpecialMeleeWeapon {
	public TekkoKagi() { super(1, 1f, 1f, 1, 6, 12, ItemSpriteSheet.SPS_TEKKO_KAGI); }

	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 20) {
			defender.damage(safeRandom(defender.HT / 4, defender.HT / 2), this);
			if (defender.sprite != null) defender.sprite.emitter().burst(ShadowParticle.UP, 5);
			if (!defender.isAlive() && attacker instanceof Hero) Badges.validateGrimWeapon();
		}
		return super.proc(attacker, defender, damage);
	}
}
