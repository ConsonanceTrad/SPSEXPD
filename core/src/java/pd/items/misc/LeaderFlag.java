/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.equipment.weapon.missiles.buildblock.BookBlock;
import pd.items.equipment.weapon.missiles.buildblock.DoorBlock;
import pd.items.equipment.weapon.missiles.buildblock.StoneBlock;
import pd.items.equipment.weapon.missiles.buildblock.WallBlock;
import pd.items.equipment.weapon.missiles.buildblock.WaterBlock;
import pd.items.equipment.weapon.missiles.buildblock.WoodenBlock;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

public class LeaderFlag extends Item {

	public static final String AC_REMOVE = "REMOVE";
	public static final String AC_RECRUIT = "RECRUIT";
	public static final String AC_EXILE = "EXILE";
	public static final String AC_LEVY = "LEVY";
	public static final int FULL_CHARGE = 1440;
	private static final String CHARGE = "charge";
	private static final String DAY_PROGRESS = "day_progress";
	private int charge = 1000;
	private float dayProgress;

	{ image = SpecificPlaceHolderDict.SOMETHING_0; unique = true; defaultAction = AC_REMOVE; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		if (charge >= 1000) actions.add(AC_LEVY);
		if (charge >= 600) actions.add(AC_RECRUIT);
		if (hero.spp > hero.lvl && charge >= 600) actions.add(AC_EXILE);
		if (charge >= 100) actions.add(AC_REMOVE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		boolean done;
		if (AC_REMOVE.equals(action)) done = removeAround(hero);
		else if (AC_RECRUIT.equals(action)) done = recruit(hero);
		else if (AC_EXILE.equals(action)) done = exile(hero);
		else if (AC_LEVY.equals(action)) done = levy(hero);
		else { super.execute(hero, action); return; }
		if (!done) GLog.i(Messages.get(this, charge < 100 ? "need_time" : "need_charge"));
	}

	public boolean removeAround(Hero hero) {
		if (hero == null || Dungeon.level == null || charge < 100 || hero.spp < hero.lvl + 5) return false;
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = hero.pos + offset;
			if (!Dungeon.level.insideMap(cell) || protectedTerrain(Dungeon.level.map[cell])) continue;
			Item recovered = recoveredBlock(Dungeon.level.map[cell]);
			if (recovered != null) drop(recovered, hero.pos);
			Level.set(cell, Terrain.EMPTY, Dungeon.level);
			GameScene.updateMap(cell);
		}
		charge -= 100;
		Dungeon.observe();
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	private static boolean protectedTerrain(int terrain) {
		return terrain == Terrain.ENTRANCE || terrain == Terrain.EXIT || terrain == Terrain.LOCKED_DOOR
				|| terrain == Terrain.LOCKED_EXIT || terrain == Terrain.ALCHEMY || terrain == Terrain.WELL
				|| terrain == Terrain.EMPTY_WELL || terrain == Terrain.IRON_MAKER;
	}

	private static Item recoveredBlock(int terrain) {
		if (terrain == Terrain.WALL) return new WallBlock();
		if (terrain == Terrain.WATER) return new WaterBlock();
		if (terrain == Terrain.DOOR) return new DoorBlock();
		if (terrain == Terrain.BOOKSHELF) return new BookBlock();
		if (terrain == Terrain.BARRICADE) return new WoodenBlock();
		if (terrain == Terrain.STATUE) return new StoneBlock();
		return null;
	}

	public boolean recruit(Hero hero) {
		if (hero == null || charge < 600) return false;
		charge -= 600;
		hero.spp += hero.lvl;
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	public boolean exile(Hero hero) {
		if (hero == null || charge < 600 || hero.spp <= hero.lvl) return false;
		charge -= 600;
		Dungeon.gold += (hero.spp - hero.lvl) * 10;
		hero.spp = hero.lvl;
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	public boolean levy(Hero hero) {
		if (hero == null || Dungeon.level == null || charge < 1000) return false;
		charge -= 1000;
		int count = Math.max(hero.spp / 50, 1);
		for (int i = 0; i < count; i++) {
			Item item = Generator.random();
			if (item != null) drop(item, hero.pos);
		}
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	private static void drop(Item item, int cell) {
		Heap heap = Dungeon.level.drop(item, cell);
		if (heap.sprite != null) heap.sprite.drop();
	}

	public int charge() { return charge; }
	public void charge(int value) { charge = Math.max(0, Math.min(FULL_CHARGE, value)); updateQuickslot(); }
	public void resetDaily() { charge(FULL_CHARGE); }
	public float dayProgress() { return dayProgress; }
	public void advanceTime(Hero hero, float time) {
		if (hero == null || time <= 0 || Float.isNaN(time) || Float.isInfinite(time)) return;
		dayProgress += time;
		while (dayProgress >= FULL_CHARGE) {
			dayProgress -= FULL_CHARGE;
			Dungeon.gold = Math.max(0, Dungeon.gold - Math.max(0, hero.spp));
			resetDaily();
		}
	}
	@Override public String status() { return Integer.toString(charge); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "time", charge); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
		bundle.put(DAY_PROGRESS, dayProgress);
	}
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge(bundle.getInt(CHARGE));
		dayProgress = Math.max(0, bundle.getFloat(DAY_PROGRESS)) % FULL_CHARGE;
	}
}
