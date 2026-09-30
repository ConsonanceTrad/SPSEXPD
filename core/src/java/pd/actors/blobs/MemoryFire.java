/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs;

import pd.Assets;
import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.BlobEmitter;
import pd.effects.particles.MemoryParticle;
import pd.journal.Notes;
import pd.messages.Messages;
import pd.scenes.MemorySaveScene;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;

import java.io.IOException;

/** A stationary fire which opens the legacy multi-slot memory save screen. */
public class MemoryFire extends Blob {

	private int pos = -1;

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		findPosition();
	}

	private void findPosition() {
		pos = -1;
		if (cur == null) return;
		for (int i = 0; i < cur.length; i++) {
			if (cur[i] > 0) {
				pos = i;
				return;
			}
		}
	}

	@Override
	protected void evolve() {
		if (pos < 0 || cur == null || pos >= cur.length || cur[pos] <= 0) {
			findPosition();
			if (pos < 0) return;
		}

		off[pos] = cur[pos];
		volume = off[pos];
		Char ch = Actor.findChar(pos);
		if (ch == Dungeon.hero && visible(pos)) {
			Sample.INSTANCE.play(Assets.Sounds.BURNING);
			cur[pos] = 0;
			off[pos] = 0;
			volume = 0;
			area.setEmpty();
			Notes.remove(Notes.Landmark.MEMORY_FIRE);
			try {
				Dungeon.saveAll();
				Game.switchScene(MemorySaveScene.class);
			} catch (IOException exception) {
				ShatteredPixelDungeon.reportException(exception);
			}
		}
	}

	private static boolean visible(int cell) {
		return Dungeon.level != null && Dungeon.level.heroFOV != null
				&& cell >= 0 && cell < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[cell];
	}

	@Override
	public void seed(pd.levels.Level level, int cell, int amount) {
		if (cur == null || cur.length != level.length()) {
			cur = new int[level.length()];
			off = new int[level.length()];
		}
		if (pos >= 0 && pos < cur.length) {
			volume -= cur[pos];
			cur[pos] = 0;
		}
		pos = cell;
		cur[pos] = Math.max(0, amount);
		volume += cur[pos];
		area.union(cell % level.width(), cell / level.width());
	}

	@Override
	public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.pour(MemoryParticle.FACTORY, 0.04f);
	}

	@Override public Notes.Landmark landmark() { return Notes.Landmark.MEMORY_FIRE; }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
