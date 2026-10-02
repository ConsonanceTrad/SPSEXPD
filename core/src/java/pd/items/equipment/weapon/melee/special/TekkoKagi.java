/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.Badges;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.effects.particles.ShadowParticle;
import render.utils.math.Random;
import pd.messages.InlineText;

public class TekkoKagi extends SpsSpecialMeleeWeapon {
	{
		image = EquipmentEquipWeaponBasicWeaponDict.SPS_TEKKO_KAGI_0;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TekkoKagi.class)
			.t("name", "攻击之爪")
			.t("desc", "看起来像金刚狼爪子的忍者武器。——Typedscroll\n致死");
	}



	public TekkoKagi() { super(1, 1f, 1f, 1, 6, 12, EquipmentEquipWeaponBasicWeaponDict.SPS_TEKKO_KAGI_0); }

	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 20) {
			defender.damage(safeRandom(defender.HT / 4, defender.HT / 2), this);
			if (defender.sprite != null) defender.sprite.emitter().burst(ShadowParticle.UP, 5);
			if (!defender.isAlive() && attacker instanceof Hero) Badges.validateGrimWeapon();
		}
		return super.proc(attacker, defender, damage);
	}
}
