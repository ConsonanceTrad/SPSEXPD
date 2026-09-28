package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

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
