/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.Dungeon;
import pd.actors.buffs.Awareness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Haste;
import pd.actors.buffs.Hunger;
import pd.actors.buffs.MechArmor;
import pd.actors.buffs.MindVision;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

public class BShovel extends Item {
	public static final String AC_USE = "USE";
	public static final int FULL_CHARGE = 150;
	public static final int USE_COST = 65;
	private static final String CHARGE = "charge";
	private int charge;
	{ image = ItemSpriteSheet.LEGACY_B_SHOVEL; defaultAction = AC_USE; unique = true; }

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge >= USE_COST) actions.add(AC_USE);
		actions.remove(AC_THROW); actions.remove(AC_DROP);
		return actions;
	}
	@Override public void execute(Hero hero, String action) {
		if (AC_USE.equals(action)) {
			if (!use(hero, Random.Int(6))) GLog.i(Messages.get(this, "break"));
		} else super.execute(hero, action);
	}
	public boolean use(Hero hero, int effect) {
		if (hero == null || Dungeon.level == null || charge < USE_COST) return false;
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = hero.pos + offset;
			if (!Dungeon.level.insideMap(cell)) continue;
			if (Dungeon.level.map[cell] == Terrain.WALL || Dungeon.level.map[cell] == Terrain.GLASS_WALL) {
				Level.set(cell, Terrain.DOOR, Dungeon.level); GameScene.updateMap(cell);
			}
		}
		charge -= USE_COST;
		Hunger hunger = hero.buff(Hunger.class);
		if (hunger != null && !hunger.isStarving()) { hunger.satisfy(-10); BuffIndicator.refreshHero(); }
		switch (Math.floorMod(effect, 6)) {
			case 0: Buff.affect(hero, MindVision.class, 5f); break;
			case 1: Buff.affect(hero, Haste.class, 5f); break;
			case 2: Buff.affect(hero, Awareness.class, 5f); break;
			case 3: Dungeon.gold += hero.lvl * 10; break;
			case 4: hero.HP = hero.HT; break;
			case 5: Buff.affect(hero, MechArmor.class).level(30); break;
			default: break;
		}
		hero.spendAndNext(1f); Dungeon.observe(); updateQuickslot(); return true;
	}
	public void gainCharge() { charge = Math.min(FULL_CHARGE, charge + 1); }
	public void gainCharge(int amount) { charge = Math.min(FULL_CHARGE, charge + Math.max(0, amount)); }
	public int charge() { return charge; }
	@Override public String status() { return Integer.toString(charge / USE_COST); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE))); }
}
