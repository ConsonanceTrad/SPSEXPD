/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.mobs.Mob;
import pd.effects.Speck;
import pd.items.Heap;
import pd.items.PuddingCup;
import pd.items.StoneOre;
import pd.items.equipment.bombs.DungeonBomb;
import pd.items.specific.keys.IronKey;
import pd.items.specific.sellitem.DevUpPlan;
import pd.items.equipment.wands.WandOfTest;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.windows.WndQuest;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

/** The eight sequential coconut guides used by the original tutorial. */
public class Leadercn extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Leadercn.class)
			.t("name", "领航椰子")
			.t("desc", "跟着这只猫完成这个教程吧。")
			.t("yell1", "欢迎，欢迎来到测试，拿起这把钥匙，然后打开前面的门。")
			.t("yell2", "你现在啥都没有，捡起周围的装备，打开右下角的背包，点击物品然后装备吧。之后可以直接击败前面的家伙。")
			.t("yell3", "找不到路了？看左下角的按钮，有一个放大镜，双击放大镜可以进行搜索，在周围的墙边找找，没准会有发现。")
			.t("yell4", "哦，你踩到了一个陷阱。看吧，左上角，你的生命已经很差了，找找周围的宝箱，或者土堆，里面可能有好东西。")
			.t("yell5", "不出所料，你有些饿了，那几个张网里面可能会有吃的，但我不建议你直接开。看到中间的那个奇怪的空地吗，把这个扔过去。")
			.t("yell6", "一个法杖，你已经可以造成魔法伤害了，前面的那家伙可不吃物理伤害，找到合适的属性来击败它吧。")
			.t("yell7", "好了，你已经了解到这个程序基本的运行方法了，那么，靠你自己完成接下来的内容吧。这是一枚可以破坏墙壁的炸弹。")
			.t("yell8", "恭喜你，你找到了最后一个分身。捡起这个布丁，教程就结束了。再见了，期待之后和你见面。");
	}


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

		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
			if (mob instanceof Leadercn && mob.isAlive()) ((Leadercn) mob).lesson++;
		}
		destroy();
		if (sprite != null) sprite.die();
		return true;
	}

	private void dropLessonReward(int currentLesson) {
		pd.items.Item reward = rewardForLesson(currentLesson);
		if (reward != null) drop(reward);
	}

	public static pd.items.Item rewardForLesson(int lesson) {
		switch (lesson) {
			case 0: return new IronKey(Dungeon.depth);
			case 4: return new StoneOre();
			case 5: return new WandOfTest().identify();
			case 6: return new DungeonBomb();
			case 7: return new PuddingCup();
			default: return null;
		}
	}

	private void drop(pd.items.Item item) {
		Heap heap = Dungeon.level.drop(item, lesson == 7 ? pos : Dungeon.hero.pos);
		if (heap.sprite != null) heap.sprite.drop();
	}

	private void throwCoveredItem() {
		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap == null || heap.type == Heap.Type.FOR_SALE || heap.type == Heap.Type.FOR_LIFE) return;
		ArrayList<Integer> candidates = neighbourCells();
		if (candidates.isEmpty()) return;
		pd.items.Item item = heap.pickUp();
		if (item == null) return;
		Heap moved = Dungeon.level.drop(item, Random.element(candidates));
		if (moved.sprite != null) moved.sprite.drop(pos);
	}

	private void separateOverlappingMobs() {
		for (Mob other : Dungeon.level.mobs().toArray(new Mob[0])) {
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
