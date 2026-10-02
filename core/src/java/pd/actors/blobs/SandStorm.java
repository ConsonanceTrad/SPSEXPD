/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dry;
import pd.effects.BlobEmitter;
import pd.effects.particles.SandParticle;
import pd.items.Heap;
import pd.levels.Level;
import pd.messages.Messages;
import pd.messages.InlineText;

/** Short-lived SPS sand cloud which dries actors and earth-hits floor items. */
public class SandStorm extends Blob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SandStorm.class)
			.t("desc", "这里盘绕着干燥的沙尘暴，会使生物陷入干燥，并以土元素影响地上的物品。");
	}



	@Override
	protected void evolve() {
		int width = Dungeon.level.width();
		for (int x = Math.max(1, area.left); x < Math.min(width - 1, area.right); x++) {
			for (int y = Math.max(1, area.top); y < Math.min(Dungeon.level.height() - 1, area.bottom); y++) {
				int cell = x + y * width;
				int value = cur[cell] > 0 ? cur[cell] - 1 : 0;
				off[cell] = value;
				if (cur[cell] <= 0) continue;
				Char ch = Actor.findChar(cell);
				if (ch != null && !ch.isImmune(getClass())) Buff.affect(ch, Dry.class, 4f);
				Heap heap = Dungeon.level.heaps.get(cell);
				if (heap != null) heap.earthhit();
				volume += value;
			}
		}
	}

	@Override
	public void seed(Level level, int cell, int amount) {
		if (cur == null) cur = new int[level.length()];
		if (off == null) off = new int[cur.length];
		if (cur[cell] != 0) return;
		cur[cell] = amount;
		volume += amount;
		area.union(cell % level.width(), cell / level.width());
	}

	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(SandParticle.FACTORY, 0.5f, 0);
	}
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
