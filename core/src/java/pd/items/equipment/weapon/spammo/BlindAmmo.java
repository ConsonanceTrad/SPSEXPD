/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Vertigo;
import pd.actors.damagetype.DamageType;
import pd.effects.Speck;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.ConsumUsefulUsefulDict;

public class BlindAmmo extends SpAmmo {
	{
		image = ConsumUsefulUsefulDict.SP_AMMO;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BlindAmmo.class)
			.t("name", "闪光弹")
			.t("desc", "将原石和致盲种锻造而成的特殊子弹，能使武器附带致盲效果。");
	}



	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Random.Int(5) == 3) {
			Buff.prolong(defender, Blindness.class, 3f);
			if (defender.sprite != null) defender.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 6);
		} else if (Random.Int(5) == 3) {
			Buff.prolong(defender, Vertigo.class, 3f);
		} else {
			defender.damage((int)(0.15f * damage), DamageType.ENERGY_DAMAGE);
		}
	}
}
