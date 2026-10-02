/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.spammo;

import pd.Badges;
import pd.actors.Char;
import pd.actors.damagetype.DamageType;
import pd.actors.hero.Hero;
import pd.effects.particles.ShadowParticle;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.ConsumUsefulUsefulDict;

public class StarAmmo extends SpAmmo {
	{
		image = ConsumUsefulUsefulDict.SP_AMMO;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StarAmmo.class)
			.t("name", "陨星弹")
			.t("desc", "将原石和星陨种锻造而成的特殊子弹，能使武器附带更高的暗属性伤害。");
	}



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
