/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.misc;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class RockManJumpshoes extends Item {
	public static final String AC_JUMP = "JUMP";
	public static final int RANGE = 3;
	{
		image = ItemSpriteSheet.SPS_ROCKMAN_JUMP;
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
