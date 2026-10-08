/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Poison;
import pd.actors.damagetype.DamageType;
import pd.effects.particles.EarthParticle;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.ConsumUsefulUsefulDict;

public class MossAmmo extends SpAmmo {
	{
		image = ConsumUsefulUsefulDict.SP_AMMO;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MossAmmo.class)
			.t("name", "淬毒弹")
			.t("desc", "将原石和毒药种锻造而成的特殊子弹，能使武器附带毒液伤害。");
	}



	@Override public void onHit(Char attacker, Char defender, int damage) {
		defender.damage((int)(0.10f * damage), DamageType.EARTH_DAMAGE);
		if (Random.Int(4) == 3) {
			Buff.affect(defender, Ooze.class).set(5f);
			if (defender.sprite != null) defender.sprite.emitter().burst(EarthParticle.FACTORY, 5);
		} else {
			Buff.affect(defender, Poison.class).set(Random.IntRange(4, 5));
		}
	}
}
