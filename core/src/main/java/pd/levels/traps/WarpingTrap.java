package pd.levels.traps;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Heap;
import pd.items.Item;
import pd.items.artifacts.TimekeepersHourglass;
import pd.items.scrolls.ScrollOfTeleportation;
import pd.messages.Messages;
import pd.scenes.InterlevelScene;
import pd.utils.GLog;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.Random;

import java.util.ArrayList;

public class WarpingTrap extends Trap {

	{
		color = TEAL;
		shape = LARGE_DOT;
	}

	@Override
	public void activate() {
		if (Dungeon.level.heroFOV[pos]) {
			CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
			Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		}
		if (Dungeon.depth <= 1 || Dungeon.bossLevel()) return;

		int destinationDepth = weightedEarlierDepth();
		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap != null) {
			ArrayList<Item> dropped = Dungeon.droppedItems.get(destinationDepth);
			if (dropped == null) {
				Dungeon.droppedItems.put(destinationDepth, dropped = new ArrayList<>());
			}
			dropped.addAll(heap.items);
			heap.destroy();
		}

		Char target = Actor.findChar(pos);
		if (target == Dungeon.hero) {
			Buff freeze = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
			if (freeze != null) freeze.detach();
			InterlevelScene.mode = InterlevelScene.Mode.RETURN;
			InterlevelScene.returnDepth = Dungeon.depth - 1;
			InterlevelScene.returnBranch = Dungeon.branch;
			InterlevelScene.returnPos = -1;
			Game.switchScene(InterlevelScene.class);
		} else if (target != null) {
			int destination = legacyDestination(target);
			if (destination == -1) {
				GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele"));
			} else {
				target.pos = destination;
				if (target.sprite != null) {
					target.sprite.place(destination);
					target.sprite.visible = Dungeon.level.heroFOV[destination];
				}
			}
		}
	}

	private int weightedEarlierDepth() {
		float[] weights = new float[Dungeon.depth - 1];
		for (int depth = 1; depth < Dungeon.depth; depth++) weights[depth - 1] = depth;
		return 1 + Random.chances(weights);
	}

	private int legacyDestination(Char target) {
		for (int attempt = 0; attempt < 10; attempt++) {
			int destination = Dungeon.level.randomRespawnCell(target);
			if (destination != -1) return destination;
		}
		return -1;
	}
}
