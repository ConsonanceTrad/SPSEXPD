/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.damagetype.DamageType;
import pd.effects.particles.FlameParticle;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.ConsumUsefulUsefulDict;

public class FireAmmo extends SpAmmo {
	{
		image = ConsumUsefulUsefulDict.SP_AMMO;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FireAmmo.class)
			.t("name", "燃烧弹")
			.t("desc", "将原石和火焰种锻造而成的特殊子弹，能使武器附带火焰伤害。");
	}



	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (defender.sprite != null) defender.sprite.emitter().burst(FlameParticle.FACTORY, 5);
		if (Random.Int(5) == 4) Buff.affect(defender, Burning.class).reignite(defender, 5f);
		else defender.damage((int)(0.25f * damage), DamageType.FIRE_DAMAGE);
	}
}
