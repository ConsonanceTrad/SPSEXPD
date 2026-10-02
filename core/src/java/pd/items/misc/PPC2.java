/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.buffs.mindbuff.AmokMind;
import pd.actors.buffs.mindbuff.CrazyMind;
import pd.actors.buffs.mindbuff.HopeMind;
import pd.actors.buffs.mindbuff.KeepMind;
import pd.actors.buffs.mindbuff.LoseMind;
import pd.actors.buffs.mindbuff.MindBuff;
import pd.actors.buffs.mindbuff.TerrorMind;
import pd.actors.buffs.mindbuff.WeakMind;
import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Gold;
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
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;

import java.util.ArrayList;

public class PPC2 extends Item {

	public static final String AC_TRY = "TRY";
	public static final String AC_HEAL = "HEAL";
	public static final String AC_MIND = "MIND";
	public static final float TIME_TO_MINE = 3f;

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		defaultAction = AC_TRY;
		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		actions.add(AC_TRY);
		if (hero != null && hero.spp > healCost(hero)) actions.add(AC_HEAL);
		actions.add(AC_MIND);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_TRY.equals(action)) {
			if (!mineAdjacent(hero)) GLog.w(Messages.get(this, isHungry(hero) ? "break" : "no_thing"));
		} else if (AC_HEAL.equals(action)) {
			if (!randomMind(hero)) GLog.w(Messages.get(this, "need_spp", healCost(hero)));
		} else if (AC_MIND.equals(action)) {
			if (!clearMind(hero)) GLog.i(Messages.get(this, "clear"));
		} else super.execute(hero, action);
	}

	public int healCost(Hero hero) { return hero == null ? 0 : Math.max(0, hero.lvl * 2); }

	public boolean mineAdjacent(Hero hero) {
		if (hero == null || Dungeon.level == null || isHungry(hero)) return false;
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = hero.pos + offset;
			if (Dungeon.level.insideMap(cell) && isMineable(Dungeon.level.map[cell])) return mine(hero, cell);
		}
		return false;
	}

	public boolean mine(Hero hero, int cell) {
		if (hero == null || Dungeon.level == null || isHungry(hero) || !Dungeon.level.insideMap(cell)
				|| Dungeon.level.distance(hero.pos, cell) != 1) return false;
		int terrain = Dungeon.level.map[cell];
		if (!isMineable(terrain)) return false;
		Level.set(cell, Terrain.EMBERS, Dungeon.level);
		GameScene.updateMap(cell);
		rollDrops(hero, terrain);
		Hunger hunger = hero.buff(Hunger.class);
		if (hunger != null && !hunger.isStarving()) {
			hunger.satisfy(-10);
			BuffIndicator.refreshHero();
		}
		Sample.INSTANCE.play(Assets.Sounds.EVOKE);
		hero.spendAndNext(TIME_TO_MINE);
		return true;
	}

	private static boolean isMineable(int terrain) {
		return terrain == Terrain.WALL || terrain == Terrain.DOOR || terrain == Terrain.BOOKSHELF
				|| terrain == Terrain.BARRICADE || terrain == Terrain.WATER || terrain == Terrain.STATUE;
	}

	private static boolean isHungry(Hero hero) {
		Hunger hunger = hero == null ? null : hero.buff(Hunger.class);
		return hunger != null && hunger.isStarving();
	}

	private void rollDrops(Hero hero, int terrain) {
		int chance = terrain == Terrain.BARRICADE ? 15 : 30;
		if (Random.Int(chance) != 1) return;
		Item loot;
		Item block;
		if (terrain == Terrain.WALL) { loot = new Gold(50); block = new WallBlock(); }
		else if (terrain == Terrain.DOOR) { loot = Generator.random(Generator.Category.SEED); block = new DoorBlock(); }
		else if (terrain == Terrain.BOOKSHELF) { loot = Generator.random(Generator.Category.SCROLL); block = new BookBlock(); }
		else if (terrain == Terrain.BARRICADE) { loot = Generator.random(Generator.Category.MUSHROOM); block = new WoodenBlock(); }
		else if (terrain == Terrain.WATER) { loot = Generator.random(Generator.Category.SEED); block = new WaterBlock(); }
		else { loot = Generator.random(); block = new StoneBlock(); }
		drop(hero, loot);
		drop(hero, block);
	}

	private static void drop(Hero hero, Item item) {
		if (item == null) return;
		Heap heap = Dungeon.level.drop(item, hero.pos);
		if (heap != null && heap.sprite != null) heap.sprite.drop();
	}

	public boolean randomMind(Hero hero) {
		if (hero == null || hero.spp <= healCost(hero)) return false;
		switch (Random.Int(8)) {
			case 0: Buff.affect(hero, HopeMind.class); break;
			case 1: Buff.affect(hero, KeepMind.class); break;
			case 2: Buff.affect(hero, AmokMind.class); break;
			case 3: Buff.affect(hero, CrazyMind.class); break;
			case 4: Buff.affect(hero, WeakMind.class); break;
			case 5: Buff.affect(hero, LoseMind.class); break;
			case 6: Buff.affect(hero, TerrorMind.class); break;
			default: Buff.prolong(hero, Bless.class, 20f); break;
		}
		hero.spp -= healCost(hero);
		hero.spendAndNext(1f);
		return true;
	}

	public boolean clearMind(Hero hero) {
		if (hero == null) return false;
		for (Class<? extends MindBuff> type : negativeMinds()) {
			MindBuff buff = hero.buff(type);
			if (buff == null) continue;
			buff.detach();
			hero.HP = Math.min(hero.HT, hero.HP + hero.HT / 5);
			hero.spp += Math.max(0, hero.lvl - 1);
			hero.spendAndNext(1f);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends MindBuff>[] negativeMinds() {
		return new Class[]{CrazyMind.class, WeakMind.class, AmokMind.class, TerrorMind.class, LoseMind.class};
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
}
