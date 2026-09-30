/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.buffs.WarGroove;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Item;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import watabou.noosa.audio.Sample;
import watabou.utils.Bundle;
import watabou.utils.PathFinder;

import java.util.ArrayList;

public class Shovel extends Item {
	public static final String AC_USE = "USE";
	public static final String AC_BUILD = "BUILD";
	public static final int FULL_CHARGE = 120;
	public static final int BREAK_COST = 40;
	public static final int BUILD_COST = 100;
	private static final String CHARGE = "charge";

	private int charge;

	{
		image = ItemSpriteSheet.LEGACY_SHOVEL;
		defaultAction = AC_USE;
		unique = true;
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge >= BREAK_COST) actions.add(AC_USE);
		if (charge >= BUILD_COST) actions.add(AC_BUILD);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (AC_USE.equals(action)) {
			if (charge < BREAK_COST) {
				GLog.i(Messages.get(this, "break"));
				return;
			}
			if (!breakAdjacentWall(hero)) GLog.i(Messages.get(this, "not_wall"));
		} else if (AC_BUILD.equals(action)) {
			if (charge < BUILD_COST) {
				GLog.i(Messages.get(this, "break"));
				return;
			}
			buildAdjacentWalls(hero);
		} else {
			super.execute(hero, action);
		}
	}

	public boolean breakAdjacentWall(Hero hero) {
		if (hero == null || Dungeon.level == null || charge < BREAK_COST) return false;
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = hero.pos + offset;
			if (!Dungeon.level.insideMap(cell)) continue;
			if (Dungeon.level.map[cell] == Terrain.WALL || Dungeon.level.map[cell] == Terrain.GLASS_WALL) {
				Level.set(cell, Terrain.EMBERS, Dungeon.level);
				GameScene.updateMap(cell);
				if (Dungeon.level.heroFOV[cell]) CellEmitter.center(cell).burst(Speck.factory(Speck.STAR), 7);
				Sample.INSTANCE.play(Assets.Sounds.EVOKE);
				consume(hero, BREAK_COST);
				Buff.affect(hero, WarGroove.class);
				Dungeon.observe();
				return true;
			}
		}
		return false;
	}

	public boolean buildAdjacentWalls(Hero hero) {
		if (hero == null || Dungeon.level == null || charge < BUILD_COST) return false;
		boolean built = false;
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = hero.pos + offset;
			if (!Dungeon.level.insideMap(cell)) continue;
			int terrain = Dungeon.level.map[cell];
			if (terrain == Terrain.EMPTY || terrain == Terrain.EMPTY_DECO
					|| terrain == Terrain.EMPTY_SP || terrain == Terrain.GRASS) {
				Level.set(cell, Terrain.WALL, Dungeon.level);
				GameScene.updateMap(cell);
				built = true;
			}
		}
		if (built) {
			consume(hero, BUILD_COST);
			Dungeon.observe();
		}
		return built;
	}

	private void consume(Hero hero, int amount) {
		charge = Math.max(0, charge - amount);
		Hunger hunger = hero.buff(Hunger.class);
		if (hunger != null && !hunger.isStarving()) {
			hunger.satisfy(-10);
			BuffIndicator.refreshHero();
		}
		hero.spendAndNext(1f);
		updateQuickslot();
	}

	public void gainCharge() { if (charge < FULL_CHARGE) charge++; }
	public void gainCharge(int amount) { charge = Math.min(FULL_CHARGE, charge + Math.max(0, amount)); }
	public int charge() { return charge; }
	@Override public String status() { return Integer.toString(charge / BREAK_COST); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE))); }
}
