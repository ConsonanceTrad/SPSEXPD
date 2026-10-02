/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.GrowSeed;
import pd.actors.damagetype.DamageType;
import pd.effects.particles.EarthParticle;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class SunAmmo extends SpAmmo {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SunAmmo.class)
			.t("name", "活性弹")
			.t("desc", "将原石和恢复种锻造而成的特殊子弹，能使目标长出吸取生命的寄生种子。");
	}



	private static final ItemSprite.Glowing PINK = new ItemSprite.Glowing(0xCCAA88);
	@Override public ItemSprite.Glowing glowing() { return PINK; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Random.Int(7) == 3) {
			Buff.affect(defender, GrowSeed.class).set(5f);
			if (defender.sprite != null) defender.sprite.emitter().burst(EarthParticle.FACTORY, 5);
		} else {
			defender.damage((int)(0.20f * damage), DamageType.LIGHT_DAMAGE);
		}
	}
}
