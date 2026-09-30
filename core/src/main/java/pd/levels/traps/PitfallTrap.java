/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels.traps;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.FlavourBuff;
import pd.actors.mobs.Mob;
import pd.effects.CellEmitter;
import pd.effects.particles.PitfallParticle;
import pd.effects.particles.WindParticle;
import pd.items.Heap;
import pd.items.Item;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.features.Chasm;
import pd.scenes.GameScene;
import watabou.noosa.Game;
import watabou.utils.Bundle;

import java.util.ArrayList;

public class PitfallTrap extends Trap {
	{
		color = RED;
		shape = DIAMOND;
	}

	@Override
	public void activate() {
		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap != null) {
			for (Item item : heap.items.toArray(new Item[0])) Dungeon.dropToChasm(item);
			if (heap.sprite != null) {
				heap.sprite.kill();
				GameScene.discard(heap);
			}
			Dungeon.level.heaps.remove(pos);
		}

		Char target = Actor.findChar(pos);
		if (target == Dungeon.hero) {
			Chasm.heroFall(pos);
		} else if (target instanceof Mob) {
			Chasm.mobFall((Mob)target);
		}
	}

	@Override
	public void disarm() {
		super.disarm();
		int width = Dungeon.level.width();
		int north = pos - width;
		int south = pos + width;
		int west = pos - 1;
		int east = pos + 1;
		if (north < 0 || south >= Dungeon.level.length() || west < 0 || east >= Dungeon.level.length()) return;
		if ((Dungeon.level.solid[north] && Dungeon.level.solid[south])
				|| (Dungeon.level.solid[west] && Dungeon.level.solid[east])) return;

		Level.set(pos, Terrain.CHASM, Dungeon.level);
		if (Game.instance != null && Game.scene() != null) Game.scene().add(new WindParticle.Wind(pos));
		GameScene.updateMap(pos);
	}

	/** Retained for Shattered cursed-wand effects and existing save compatibility. */
	public static class DelayedPit extends FlavourBuff {
		{
			revivePersists = true;
		}

		public int[] positions = new int[0];
		public int depth;
		public int branch;
		public boolean ignoreAllies;

		@Override
		public boolean act() {
			boolean heroFell = false;
			if (depth == Dungeon.depth && branch == Dungeon.branch && positions != null) {
				for (int cell : positions) {
					if (!Dungeon.level.insideMap(cell)
							|| (Dungeon.level.solid[cell] && !Dungeon.level.passable[cell])) continue;
					if (Game.instance != null && Game.scene() != null) {
						CellEmitter.floor(cell).burst(PitfallParticle.FACTORY8, 12);
					}
					Char target = Actor.findChar(cell);
					if (target != null && !target.flying
							&& !(target.alignment == Char.Alignment.NEUTRAL
							&& Char.hasProp(target, Char.Property.IMMOVABLE))
							&& !(target.alignment == Char.Alignment.ALLY && ignoreAllies)) {
						if (target == Dungeon.hero) heroFell = true;
						else if (target instanceof Mob) Chasm.mobFall((Mob)target);
					}
					Heap heap = Dungeon.level.heaps.get(cell);
					if (heap != null && !ignoreAllies
							&& heap.type != Heap.Type.FOR_SALE
							&& heap.type != Heap.Type.FOR_LIFE
							&& heap.type != Heap.Type.LOCKED_CHEST
							&& heap.type != Heap.Type.CRYSTAL_CHEST) {
						for (Item item : heap.items.toArray(new Item[0])) Dungeon.dropToChasm(item);
						if (heap.sprite != null) {
							heap.sprite.kill();
							GameScene.discard(heap);
						}
						Dungeon.level.heaps.remove(cell);
					}
				}
			}
			if (heroFell) Chasm.heroFall(Dungeon.hero.pos);
			detach();
			return !heroFell;
		}

		public void setPositions(ArrayList<Integer> cells) {
			positions = new int[cells.size()];
			for (int i = 0; i < positions.length; i++) positions[i] = cells.get(i);
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put("positions", positions);
			bundle.put("depth", depth);
			bundle.put("branch", branch);
			bundle.put("ignore_allies", ignoreAllies);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			positions = bundle.getIntArray("positions");
			depth = bundle.getInt("depth");
			branch = bundle.getInt("branch");
			ignoreAllies = bundle.getBoolean("ignore_allies");
		}
	}
}
