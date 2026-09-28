/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

/** SPS corruption cloud: percentage damage, bleeding, and crippling. */
public class CorruptGas extends Blob implements Hero.Doom {
	@Override
	protected void evolve() {
		super.evolve();
		int levelDamage = 5 + Dungeon.legacyDepth() / 2;
		for (int x = area.left; x < area.right; x++) {
			for (int y = area.top; y < area.bottom; y++) {
				int cell = x + y * Dungeon.level.width();
				Char ch = cur[cell] > 0 ? Actor.findChar(cell) : null;
				if (ch == null || ch.isImmune(getClass())) continue;
				Buff.affect(ch, Bleeding.class).set(levelDamage);
				Buff.prolong(ch, Cripple.class, Cripple.DURATION);
				int numerator = ch.HT / 2 + levelDamage;
				int damage = numerator / 40;
				if (Random.Int(40) < numerator % 40) damage++;
				ch.damage(damage, this);
			}
		}
	}
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.pour(ShadowParticle.UP, 0.6f);
	}
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
	@Override public void onDeath() {
		Badges.validateDeathFromGas();
		Dungeon.fail(this);
		GLog.n(Messages.get(this, "ondeath"));
	}
}
