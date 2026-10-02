/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.effects.BlobEmitter;
import pd.effects.particles.DarkLightParticle;
import pd.messages.Messages;
import pd.messages.InlineText;

/** SPS darkness cloud which repeatedly blinds occupants. */
public class DarkGas extends Blob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DarkGas.class)
			.t("desc", "这里盘绕着黑色浓烟，会使烟雾中的生物暂时失明。");
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
				if (ch != null && !ch.isImmune(getClass())) Buff.prolong(ch, Blindness.class, 3f);
			}
		}
	}

	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(DarkLightParticle.FACTORY, 0.8f, 0);
	}

	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
