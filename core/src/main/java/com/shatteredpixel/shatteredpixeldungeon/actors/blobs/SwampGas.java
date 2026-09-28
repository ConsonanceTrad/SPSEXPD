/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SpeedSlow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.StandDown;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Random;

/** SPS-PD's stacking slow and vulnerability cloud. */
public class SwampGas extends Blob {

	@Override
	protected void evolve() {
		super.evolve();

		for (int x = area.left; x < area.right; x++) {
			for (int y = area.top; y < area.bottom; y++) {
				int cell = x + y * Dungeon.level.width();
				if (cur[cell] <= 0) continue;

				Char ch = Actor.findChar(cell);
				if (ch != null && !ch.isImmune(getClass())) {
					Buff.affect(ch, ArmorBreak.class, 3f).level(20);
					if (ch.buff(StandDown.class) != null) {
						Buff.affect(ch, StandDown.class, 2f);
					} else {
						Buff.affect(ch, SpeedSlow.class, 3f);
						SpeedSlow slow = ch.buff(SpeedSlow.class);
						if (slow != null && shouldStandDown(slow.cooldown())) {
							Buff.affect(ch, StandDown.class, 5f);
						}
					}
				}
			}
		}

		Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
		if (fire == null) return;

		int width = Dungeon.level.width();
		int height = Dungeon.level.height();
		for (int x = area.left; x < area.right; x++) {
			for (int y = area.top; y < area.bottom; y++) {
				int cell = x + y * width;
				if (cur[cell] <= 0 || fire.cur[cell] >= 2) continue;

				int flammability = 0;
				for (int dx = -1; dx <= 1; dx++) {
					for (int dy = -1; dy <= 1; dy++) {
						if ((dx == 0 && dy == 0) || x + dx < 0 || x + dx >= width
								|| y + dy < 0 || y + dy >= height) continue;
						if (fire.cur[cell + dx + dy * width] > 0) flammability++;
					}
				}

				if (ignites(Random.Int(4), flammability)) {
					int oldFire = fire.cur[cell];
					fire.cur[cell] = 2;
					fire.volume += 2 - oldFire;
					int consumed = cur[cell] / 2;
					cur[cell] -= consumed;
					volume -= consumed;
				}
			}
		}
	}

	public static boolean shouldStandDown(float cooldown) {
		return cooldown >= 10f;
	}

	public static boolean ignites(int roll, int burningNeighbours) {
		return roll < burningNeighbours;
	}

	@Override
	public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.pour(Speck.factory(Speck.TARGAS, true), 0.6f);
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}
