/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.spammo;

import pd.Badges;
import pd.actors.Char;
import pd.actors.damagetype.DamageType;
import pd.actors.hero.Hero;
import pd.effects.particles.ShadowParticle;
import pd.sprites.ItemSprite;
import com.watabou.utils.Random;

public class StarAmmo extends SpAmmo {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);
	@Override public ItemSprite.Glowing glowing() { return BLACK; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Random.Int(20) == 1) {
			int amount;
			if (Char.hasProp(defender, Char.Property.BOSS) || Char.hasProp(defender, Char.Property.MINIBOSS)) {
				amount = Random.IntRange(Math.max(0, defender.HT / 8), Math.max(0, defender.HT / 4));
			} else {
				amount = Random.IntRange(Math.max(0, defender.HT), Math.max(0, defender.HT * 2));
			}
			defender.damage(amount, DamageType.DARK_DAMAGE);
			if (defender.sprite != null) defender.sprite.emitter().burst(ShadowParticle.UP, 5);
			if (!defender.isAlive() && attacker instanceof Hero) Badges.validateGrimWeapon();
		} else {
			defender.damage((int)(0.40f * damage), DamageType.DARK_DAMAGE);
		}
	}
}
