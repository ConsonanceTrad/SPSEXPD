/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;

import java.util.ArrayList;
import pd.messages.InlineText;

public class RockManJumpshoes extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RockManJumpshoes.class)
			.t("name", "洛克之鞋")
			.t("ac_jump", "跳跃")
			.t("prompt", "选择三格范围内的目的地")
			.t("desc", "这双鞋允许穿戴者跳跃最多三格，消耗的时间等于实际跳跃距离。");
	}



	public static final String AC_JUMP = "JUMP";
	public static final int RANGE = 3;
	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		defaultAction = AC_JUMP;
		unique = true;
		usesTargeting = true;
	}
	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_JUMP);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		return actions;
	}
	@Override public void execute(Hero hero, String action) {
		if (AC_JUMP.equals(action)) { curUser = hero; GameScene.selectCell(jumper); }
		else super.execute(hero, action);
	}
	public boolean jumpTo(Hero hero, int target) {
		if (hero == null || Dungeon.level == null || hero.rooted || target == hero.pos
				|| !Dungeon.level.insideMap(target)) return false;
		Ballistica route = new Ballistica(hero.pos, target, Ballistica.PROJECTILE);
		int index = Math.min(route.dist, RANGE);
		while (index > 0) {
			int cell = route.path.get(index);
			if (Actor.findChar(cell) == null && (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])) {
				int origin = hero.pos;
				hero.move(cell, false);
				Dungeon.level.pressCell(cell);
				Dungeon.observe();
				hero.spendAndNext(Math.max(1, Dungeon.level.distance(origin, cell)));
				return true;
			}
			index--;
		}
		return false;
	}
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
	private final CellSelector.Listener jumper = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) { if (target != null) jumpTo(curUser, target); }
		@Override public String prompt() { return Messages.get(RockManJumpshoes.class, "prompt"); }
	};
}
