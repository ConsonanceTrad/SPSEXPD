/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.PuddingCup;
import com.shatteredpixel.shatteredpixeldungeon.items.StoneOre;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.DungeonBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.sellitem.DevUpPlan;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTest;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** The eight sequential coconut guides used by the original tutorial. */
public class Leadercn extends TownNpc {

	private static final String LESSON = "lesson";
	private int lesson;

	{
		configure(Spec.COCONUT);
		properties.remove(Property.IMMOVABLE);
		properties.add(Property.MECH);
	}

	@Override public String name() { return Messages.get(this, "name"); }
	@Override public String description() { return Messages.get(this, "desc"); }
	@Override public int defenseSkill(Char enemy) { return 1000; }
	@Override public void damage(int damage, Object source) { }
	@Override public boolean add(Buff buff) { return false; }
	@Override protected Char chooseEnemy() { return null; }
	@Override public boolean reset() { return true; }
	@Override public DevUpPlan SupercreateLoot() { return new DevUpPlan(); }

	@Override
	protected boolean act() {
		throwCoveredItem();
		separateOverlappingMobs();
		return super.act();
	}

	@Override
	public boolean interact(Char ch) {
		if (ch != Dungeon.hero) return super.interact(ch);
		if (sprite != null) {
			sprite.turnTo(pos, ch.pos);
			sprite.emitter().burst(Speck.factory(Speck.STEAM), 6);
		}

		int currentLesson = Math.min(lesson, 7);
		Game.runOnRenderThread(() -> GameScene.show(
				new WndQuest(Leadercn.this, Messages.get(Leadercn.this, "yell" + (currentLesson + 1)))));
		dropLessonReward(currentLesson);

		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (mob instanceof Leadercn && mob.isAlive()) ((Leadercn) mob).lesson++;
		}
		destroy();
		if (sprite != null) sprite.die();
		return true;
	}

	private void dropLessonReward(int currentLesson) {
		com.shatteredpixel.shatteredpixeldungeon.items.Item reward = rewardForLesson(currentLesson);
		if (reward != null) drop(reward);
	}

	public static com.shatteredpixel.shatteredpixeldungeon.items.Item rewardForLesson(int lesson) {
		switch (lesson) {
			case 0: return new IronKey(Dungeon.depth);
			case 4: return new StoneOre();
			case 5: return new WandOfTest().identify();
			case 6: return new DungeonBomb();
			case 7: return new PuddingCup();
			default: return null;
		}
	}

	private void drop(com.shatteredpixel.shatteredpixeldungeon.items.Item item) {
		Heap heap = Dungeon.level.drop(item, lesson == 7 ? pos : Dungeon.hero.pos);
		if (heap.sprite != null) heap.sprite.drop();
	}

	private void throwCoveredItem() {
		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap == null || heap.type == Heap.Type.FOR_SALE || heap.type == Heap.Type.FOR_LIFE) return;
		ArrayList<Integer> candidates = neighbourCells();
		if (candidates.isEmpty()) return;
		com.shatteredpixel.shatteredpixeldungeon.items.Item item = heap.pickUp();
		if (item == null) return;
		Heap moved = Dungeon.level.drop(item, Random.element(candidates));
		if (moved.sprite != null) moved.sprite.drop(pos);
	}

	private void separateOverlappingMobs() {
		for (Mob other : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (other == this || other.pos != pos) continue;
			ArrayList<Integer> candidates = neighbourCells();
			candidates.removeIf(cell -> Actor.findChar(cell) != null);
			if (!candidates.isEmpty()) other.move(Random.element(candidates));
		}
	}

	private ArrayList<Integer> neighbourCells() {
		ArrayList<Integer> result = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = pos + offset;
			if (Dungeon.level.insideMap(cell)
					&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])) result.add(cell);
		}
		return result;
	}

	public int lessonForTesting() { return lesson; }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LESSON, lesson);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		lesson = bundle.getInt(LESSON);
	}
}
