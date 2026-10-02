/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.SpeedSlow;
import pd.actors.buffs.StandDown;
import pd.effects.BlobEmitter;
import pd.effects.particles.SnowParticle;
import pd.items.Heap;
import pd.messages.Messages;
import pd.messages.InlineText;

public class SlowGas extends Blob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SlowGas.class)
			.t("desc", "这里盘绕着一片极寒雪雾，会持续拖慢身处其中的生物。");
	}


	@Override
	protected void evolve() {
		int width = Dungeon.level.width();
		int left = Math.max(1, area.left - 1);
		int right = Math.min(Dungeon.level.width() - 2, area.right);
		int top = Math.max(1, area.top - 1);
		int bottom = Math.min(Dungeon.level.height() - 2, area.bottom);
		for (int x = left; x <= right; x++) {
			for (int y = top; y <= bottom; y++) {
				int cell = x + y * width;
				if (cur[cell] > 0) {
					affectCell(cell);
					off[cell] = cur[cell] - 1;
					volume += off[cell];
				} else {
					off[cell] = 0;
				}
			}
		}
	}

	private void affectCell(int cell) {
		Char target = Actor.findChar(cell);
		if (target != null && !target.isImmune(getClass())) {
			if (target.buff(StandDown.class) != null) {
				Buff.prolong(target, StandDown.class, 2f);
			} else {
				SpeedSlow slow = Buff.prolong(target, SpeedSlow.class, 3f);
				if (slow.cooldown() >= 10f) Buff.prolong(target, StandDown.class, 5f);
			}
		}
		Heap heap = Dungeon.level.heaps.get(cell);
		if (heap != null) heap.icehit();
	}

	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(SnowParticle.FACTORY, 0.05f, 0);
	}
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
