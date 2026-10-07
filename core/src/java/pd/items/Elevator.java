/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.EquipmentNonEquipDict;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.InterlevelScene;
import pd.utils.GLog;
import pd.windows.WndTextInput;
import render.noosa.Game;

import java.util.ArrayList;
import pd.messages.InlineText;

/** The legacy SPS portable elevator, usable on the main dungeon's normal floors. */
public class Elevator extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Elevator.class)
			.t("name", "社会升降器")
			.t("desc", "曾经有一位疯狂的古神信徒。他得到了古神的奖励，被封印在这件上流社会的服装中。\n这件道具可以使你自由穿梭于主地牢的第0至38层（普通楼层）。")
			.t("ac_up", "上楼")
			.t("ac_down", "下楼")
			.t("ac_goto", "快速抵达")
			.t("goto_title", "快速抵达")
			.t("goto_body", "输入要抵达的楼层（%d - %d）：")
			.t("goto_confirm", "出发")
			.t("goto_cancel", "取消")
			.t("goto_invalid", "没有这个楼层。")
			.t("goto_same", "你已经在这一层了。");
	}



	public static final String AC_UP = "UP";
	public static final String AC_DOWN = "DOWN";
	public static final String AC_GOTO = "GOTO";

	//SPS: 可达主地牢 0（初始层）至最后一个普通层（章 5 的 38 层；39 是 boss 层、40 是终层）
	private static final int MIN_DEPTH = 0;
	private static final int MAX_DEPTH = Dungeon.LAST_LEVEL_DEPTH - 2;

	/** 快速抵达的目标深度；-1 表示当前没有正在进行的连续跳层。 */
	private static int gotoDepth = -1;

	{
		image = EquipmentNonEquipDict.ELEVATOR;
		stackable = true;
		unique = true;
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (Dungeon.branch == 0 && Dungeon.depth >= MIN_DEPTH && Dungeon.depth <= MAX_DEPTH) {
			//SPS: 0 层之上已无处可去，最后一个普通层以下也不再是普通楼层
			if (Dungeon.depth > MIN_DEPTH) actions.add(AC_UP);
			if (Dungeon.depth < MAX_DEPTH) actions.add(AC_DOWN);
			actions.add(AC_GOTO);
		}
		return actions;
	}

	//SPSXPD: 上楼/下楼排第二行，快速抵达单独占第三行
	@Override public boolean actionBreakBefore(String action) {
		return AC_UP.equals(action) || AC_GOTO.equals(action);
	}

	//SPSXPD: 允许放入快捷栏，但不设置默认行为（点击快捷栏会打开动作菜单而不是直接跳层）
	@Override public boolean canQuickSlot() { return true; }

	@Override public void execute(Hero hero, String action) {
		if (AC_UP.equals(action)) {
			jump(InterlevelScene.Mode.ASCEND);
		} else if (AC_DOWN.equals(action)) {
			jump(InterlevelScene.Mode.DESCEND);
		} else if (AC_GOTO.equals(action)) {
			promptGoto();
		} else super.execute(hero, action);
	}

	/** 单层跳上/跳下。 */
	private static void jump(InterlevelScene.Mode mode) {
		gotoDepth = -1;
		pd.items.consum.eggs.Egg.recallProjection(Dungeon.hero);
		InterlevelScene.mode = mode;
		Game.switchScene(InterlevelScene.class);
	}

	/** 快速抵达：输入楼层数字，连续跳层直到抵达。 */
	private static void promptGoto() {
		GameScene.show(new WndTextInput(
				Messages.get(Elevator.class, "goto_title"),
				Messages.get(Elevator.class, "goto_body", MIN_DEPTH, MAX_DEPTH),
				"", 2, false,
				Messages.get(Elevator.class, "goto_confirm"),
				Messages.get(Elevator.class, "goto_cancel")) {
			@Override
			public void onSelect(boolean positive, String text) {
				if (!positive) return;
				int depth;
				try {
					depth = Integer.parseInt(text.trim());
				} catch (NumberFormatException e) {
					GLog.w(Messages.get(Elevator.class, "goto_invalid"));
					return;
				}
				if (depth < MIN_DEPTH || depth > MAX_DEPTH) {
					GLog.w(Messages.get(Elevator.class, "goto_invalid"));
					return;
				}
				if (depth == Dungeon.depth) {
					GLog.i(Messages.get(Elevator.class, "goto_same"));
					return;
				}
				gotoDepth = depth;
				pd.items.consum.eggs.Egg.recallProjection(Dungeon.hero);
				InterlevelScene.mode = depth > Dungeon.depth
						? InterlevelScene.Mode.DESCEND : InterlevelScene.Mode.ASCEND;
				Game.switchScene(InterlevelScene.class);
			}
		});
	}

	/**
	 * SPSXPD: 快速抵达的连续跳层调度 —— 每跳完一层后由 InterlevelScene 询问是否继续。
	 *
	 * @return 下一次跳层的方向；已抵达目标或没有目标时返回 null
	 */
	public static InterlevelScene.Mode nextGotoMode() {
		if (gotoDepth < 0) return null;
		if (Dungeon.depth == gotoDepth) {
			gotoDepth = -1;
			return null;
		}
		return Dungeon.depth < gotoDepth ? InterlevelScene.Mode.DESCEND : InterlevelScene.Mode.ASCEND;
	}

	/** 中断快速抵达（例如中途遇到错误）。 */
	public static void cancelGoto() {
		gotoDepth = -1;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
