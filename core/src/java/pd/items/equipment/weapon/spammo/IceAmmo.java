/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Chill;
import pd.actors.buffs.Frost;
import pd.actors.buffs.Wet;
import pd.actors.damagetype.DamageType;
import pd.effects.particles.SnowParticle;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class IceAmmo extends SpAmmo {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(IceAmmo.class)
			.t("name", "寒霜弹")
			.t("desc", "将原石和寒冰种锻造而成的特殊子弹，能使武器附带寒冰伤害。");
	}

	private static final ItemSprite.Glowing BLUE = new ItemSprite.Glowing(0x0000FF);
	@Override public ItemSprite.Glowing glowing() { return BLUE; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		defender.damage((int)(0.25f * damage), DamageType.ICE_DAMAGE);
		if (Random.Int(4) == 3) {
			Buff.affect(defender, Frost.class, 5f * Random.Float(2f, 4f));
			if (defender.sprite != null) defender.sprite.emitter().burst(SnowParticle.FACTORY, 5);
		} else {
			Buff.prolong(defender, Wet.class, 2f);
			Buff.prolong(defender, Chill.class, 2f);
		}
	}
}
