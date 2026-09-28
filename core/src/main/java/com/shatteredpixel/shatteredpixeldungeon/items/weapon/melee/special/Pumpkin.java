/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

/** The original Halloween pumpkin lamp weapon. */
public class Pumpkin extends MeleeWeapon {

	public static final int EFFECT_CHANCE = 20;
	public static final int HEALING = 10;
	public static final float LIGHT_DURATION = 50f;

	{
		image = ItemSpriteSheet.SPS_PUMPKIN;
		tier = 1;
		usesTargeting = true;
	}

	@Override public int min(int lvl) { return 1 + Math.max(0, lvl); }
	@Override public int max(int lvl) { return 5 + Math.max(0, lvl); }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < EFFECT_CHANCE) {
			Buff.affect(defender, Burning.class).reignite(defender, 5f);
			defender.damage(Random.Int(1, Math.max(2, buffedLvl() + 2)), this);
			if (defender.sprite != null) defender.sprite.emitter().burst(FlameParticle.FACTORY, buffedLvl() + 1);
		}
		if (Random.Int(100) < EFFECT_CHANCE) {
			Buff.affect(defender, Terror.class, 3f).object = attacker.id();
		}
		if (Random.Int(100) < EFFECT_CHANCE && attacker.HP < attacker.HT) {
			attacker.HP = Math.min(attacker.HT, attacker.HP + HEALING);
			if (attacker.sprite != null) attacker.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 1);
		}
		Buff.prolong(attacker, Light.class, LIGHT_DURATION);
		return super.proc(attacker, defender, damage);
	}
}
