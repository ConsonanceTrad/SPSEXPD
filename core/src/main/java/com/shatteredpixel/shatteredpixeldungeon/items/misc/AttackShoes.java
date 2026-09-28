/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.misc;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class AttackShoes extends Item {
	public static final String AC_JUMP = "JUMP";
	{ image = ItemSpriteSheet.LEGACY_ATTACK_SHOES; defaultAction = AC_JUMP; unique = true; usesTargeting = true; }
	@Override public ArrayList<String> actions(Hero hero) { ArrayList<String> a=super.actions(hero); a.add(AC_JUMP); a.remove(AC_DROP); a.remove(AC_THROW); return a; }
	@Override public void execute(Hero hero, String action) { if (AC_JUMP.equals(action)) { curUser=hero; GameScene.selectCell(jumper); } else super.execute(hero, action); }
	public boolean jumpTo(Hero hero, int target) {
		if (hero == null || Dungeon.level == null || !Dungeon.level.insideMap(target) || target == hero.pos || hero.rooted) return false;
		Ballistica route = new Ballistica(hero.pos, target, Ballistica.PROJECTILE);
		int index = Math.min(3, route.dist);
		int cell = route.path.get(index);
		if (!Dungeon.level.passable[cell] || Actor.findChar(cell) != null) return false;
		hero.pos = cell; Dungeon.level.occupyCell(hero); Dungeon.level.pressCell(cell); Dungeon.observe();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int adjacent = cell + offset;
			if (!Dungeon.level.insideMap(adjacent)) continue;
			Char ch = Actor.findChar(adjacent);
			if (ch != null && ch != hero && ch.isAlive()) ch.damage(30 + hero.lvl * 3, this);
		}
		if (Random.Int(20) == 10) Dungeon.level.plant((Plant.Seed)Generator.random(Generator.Category.SEED), cell);
		hero.spendAndNext(2f); return true;
	}
	private final CellSelector.Listener jumper = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) { if (target != null) jumpTo(curUser, target); }
		@Override public String prompt() { return Messages.get(AttackShoes.class, "prompt"); }
	};
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
