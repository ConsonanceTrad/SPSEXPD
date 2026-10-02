/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.EquipmentNonEquipDict;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.scenes.InterlevelScene;
import render.noosa.Game;

import java.util.ArrayList;
import pd.messages.InlineText;

/** The legacy SPS portable elevator, usable on the first 25 dungeon floors. */
public class Elevator extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Elevator.class)
			.t("name", "社会升降器")
			.t("desc", "曾经有一位疯狂的古神信徒。他得到了古神的奖励，被封印在这件上流社会的服装中。\n这件道具可以使你自由穿梭于主地牢的第0至25层。")
			.t("ac_up", "上楼")
			.t("ac_down", "下楼");
	}



	public static final String AC_UP = "UP";
	public static final String AC_DOWN = "DOWN";

	//SPS: 可达主地牢 0（初始层）至 25 层
	private static final int MIN_DEPTH = 0;
	private static final int MAX_DEPTH = 25;

	{
		image = EquipmentNonEquipDict.ELEVATOR;
		stackable = true;
		unique = true;
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (Dungeon.branch == 0 && Dungeon.depth >= MIN_DEPTH && Dungeon.depth <= MAX_DEPTH) {
			//SPS: 0 层之上已无处可去，25 层以下也不再是主地牢层
			if (Dungeon.depth > MIN_DEPTH) actions.add(AC_UP);
			if (Dungeon.depth < MAX_DEPTH) actions.add(AC_DOWN);
		}
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (AC_UP.equals(action)) {
			PocketBallFull.removePet(hero);
			InterlevelScene.mode = InterlevelScene.Mode.ASCEND;
			Game.switchScene(InterlevelScene.class);
		} else if (AC_DOWN.equals(action)) {
			PocketBallFull.removePet(hero);
			InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
			Game.switchScene(InterlevelScene.class);
		} else super.execute(hero, action);
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
