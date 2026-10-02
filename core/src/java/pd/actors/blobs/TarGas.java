/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Tar;
import pd.effects.BlobEmitter;
import pd.effects.Speck;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.messages.InlineText;

/** Flammable SPS oil mist which coats occupants in tar. */
public class TarGas extends Blob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(TarGas.class)
			.t("desc", "这里盘绕着粘稠油雾，会在接触到的生物身上凝结出焦油。");
	}



	@Override
	protected void evolve() {
		super.evolve();
		for (int x = area.left; x < area.right; x++) {
			for (int y = area.top; y < area.bottom; y++) {
				int cell = x + y * Dungeon.level.width();
				if (cur[cell] <= 0) continue;
				Char ch = Actor.findChar(cell);
				if (ch != null && !ch.isImmune(getClass())) {
					Buff.affect(ch, Tar.class);
					if (ch.buff(Burning.class) != null) GameScene.add(Blob.seed(cell, 2, Fire.class));
				}
			}
		}
	}

	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.pour(Speck.factory(Speck.STENCH), 0.6f);
	}

	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
