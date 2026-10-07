package pd.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.scenes.InterlevelScene;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * SPSEXPD: 社会升降器的无图形验证——可达楼层、动作集合与排版、快捷栏准入（无默认行为）、
 * 以及"快速抵达"的连续跳层调度。
 */
public final class SpsElevatorTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();

		testDepthRange();
		testActions();
		testLayoutAndQuickSlot();
		testGotoScheduling();

		System.out.println("SPS社会升降器测试通过：楼层上限、动作集合与排版、快捷栏准入（无默认行为）、快速抵达调度均正常。");
	}

	/** 可达范围 0 至最后一个普通层（38）；boss 层与终层不可达。 */
	private static void testDepthRange() throws Exception {
		Field max = Elevator.class.getDeclaredField("MAX_DEPTH");
		max.setAccessible(true);
		int maxDepth = max.getInt(null);
		check(maxDepth == Dungeon.LAST_LEVEL_DEPTH - 2 && maxDepth == 38,
				"升降器上限不是最后一个普通层 38：" + maxDepth);

		Elevator elevator = new Elevator();
		Dungeon.branch = 0;

		//38 层仍可用：可上不可下
		Dungeon.depth = 38;
		ArrayList<String> at38 = elevator.actions(new Hero());
		check(at38.contains(Elevator.AC_UP) && !at38.contains(Elevator.AC_DOWN)
						&& at38.contains(Elevator.AC_GOTO),
				"38 层动作错误：" + at38);

		//39 层（boss）越界：不提供任何升降动作
		Dungeon.depth = 39;
		ArrayList<String> at39 = elevator.actions(new Hero());
		check(!at39.contains(Elevator.AC_UP) && !at39.contains(Elevator.AC_DOWN)
						&& !at39.contains(Elevator.AC_GOTO),
				"39 层（boss）不应提供升降动作：" + at39);

		//支线层不可用
		Dungeon.depth = 10;
		Dungeon.branch = 1;
		ArrayList<String> branch = elevator.actions(new Hero());
		check(!branch.contains(Elevator.AC_GOTO), "支线层不应提供快速抵达：" + branch);
		Dungeon.branch = 0;
	}

	private static void testActions() {
		Elevator elevator = new Elevator();
		Dungeon.depth = 10;
		Dungeon.branch = 0;

		ArrayList<String> actions = elevator.actions(new Hero());
		check(actions.contains(Elevator.AC_UP) && actions.contains(Elevator.AC_DOWN)
						&& actions.contains(Elevator.AC_GOTO),
				"10 层动作不完整：" + actions);
		check(actions.indexOf(Elevator.AC_UP) < actions.indexOf(Elevator.AC_DOWN)
						&& actions.indexOf(Elevator.AC_DOWN) < actions.indexOf(Elevator.AC_GOTO),
				"动作顺序不是 上楼→下楼→快速抵达：" + actions);

		//0 层：没有上楼
		Dungeon.depth = 0;
		check(!elevator.actions(new Hero()).contains(Elevator.AC_UP), "0 层不应提供上楼");
	}

	/** 排版：上楼另起一行（连同下楼）、快速抵达再另起一行；快捷栏可加入但不设默认行为。 */
	private static void testLayoutAndQuickSlot() {
		Elevator elevator = new Elevator();

		check(elevator.actionBreakBefore(Elevator.AC_UP), "上楼没有另起一行");
		check(!elevator.actionBreakBefore(Elevator.AC_DOWN), "下楼不应另起一行");
		check(elevator.actionBreakBefore(Elevator.AC_GOTO), "快速抵达没有另起一行");

		check(elevator.defaultAction() == null, "升降器不应设置默认行为");
		check(elevator.canQuickSlot(), "升降器不允许加入快捷栏");
		check(elevator.quickSlotOpensMenu(), "点击快捷栏应打开动作菜单而不是直接执行默认动作");
	}

	/** 快速抵达：连续跳层方向由目标深度与当前深度决定。 */
	private static void testGotoScheduling() throws Exception {
		Field gotoDepth = Elevator.class.getDeclaredField("gotoDepth");
		gotoDepth.setAccessible(true);

		//无目标 → 不干预正常跳层
		gotoDepth.setInt(null, -1);
		Dungeon.depth = 5;
		check(Elevator.nextGotoMode() == null, "没有目标时不应继续跳层");

		//目标更深
		gotoDepth.setInt(null, 20);
		Dungeon.depth = 5;
		check(Elevator.nextGotoMode() == InterlevelScene.Mode.DESCEND, "目标更深时应继续下楼");

		//越过目标后回头
		Dungeon.depth = 21;
		check(Elevator.nextGotoMode() == InterlevelScene.Mode.ASCEND, "越过目标后应回头上楼");

		//到达后停止并清空目标
		Dungeon.depth = 20;
		check(Elevator.nextGotoMode() == null, "到达目标后应停止跳层");
		check(gotoDepth.getInt(null) == -1, "到达目标后没有清空目标深度");

		//取消
		gotoDepth.setInt(null, 30);
		Elevator.cancelGoto();
		check(gotoDepth.getInt(null) == -1, "取消快速抵达没有清空目标深度");
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsElevatorTest() {
	}
}
