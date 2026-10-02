/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Slow;
import pd.effects.BlobEmitter;
import pd.effects.particles.WebParticle;
import pd.messages.Messages;
import pd.messages.InlineText;

/** Persistent SPS web which slows occupants each turn. */
public class SlowWeb extends Blob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SlowWeb.class)
			.t("desc", "这里覆盖着粘稠的蛛网，会持续拖慢身处其中的生物。");
	}



	@Override
	protected void evolve() {
		for (int x = area.left; x < area.right; x++) {
			for (int y = area.top; y < area.bottom; y++) {
				int cell = x + y * Dungeon.level.width();
				int value = cur[cell] > 0 ? cur[cell] - 1 : 0;
				off[cell] = value;
				if (value <= 0) continue;
				volume += value;
				Char ch = Actor.findChar(cell);
				if (ch != null && !ch.isImmune(getClass())) Buff.prolong(ch, Slow.class, TICK);
			}
		}
	}

	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.pour(WebParticle.FACTORY, 0.4f);
	}

	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
