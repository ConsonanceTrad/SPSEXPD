/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.misc;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class GrassBook extends Item {
	public static final String AC_READ = "READ";
	public static final String AC_READ2 = "READ2";
	public static final int GOLD_COST = 500;
	private static final String CHARGE = "charge";
	private int charge;

	{
		image = ItemSpriteSheet.SPS_GRASS_BOOK;
		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (Dungeon.gold > GOLD_COST) {
			actions.add(AC_READ);
			actions.add(AC_READ2);
		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_READ.equals(action)) createNaturalItem(hero);
		else if (AC_READ2.equals(action)) growGrass(hero);
		else super.execute(hero, action);
	}

	public boolean createNaturalItem(Hero hero) {
		if (!canUse(hero)) return false;
		payAndOperate(hero);
		Item item = randomNaturalItem();
		Heap heap = Dungeon.level.drop(item, hero.pos);
		if (heap.sprite != null) heap.sprite.drop(hero.pos);
		return true;
	}

	public Item randomNaturalItem() {
		if (Random.Int(5) == 1) return Generator.random(Generator.Category.MUSHROOM);
		if (Random.Int(4) == 1) return Generator.random(Generator.Category.SPS_BERRY);
		if (Random.Int(3) == 1) return new ScrollOfRegrowth();
		return Generator.random(Generator.Category.SPS_SEED);
	}

	public boolean growGrass(Hero hero) {
		if (!canUse(hero)) return false;
		payAndOperate(hero);
		Buff.affect(hero, Levitation.class, 30f);
		Buff.affect(hero, ShieldArmor.class).level(hero.lvl + 10);
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = hero.pos + offset;
			if (!Dungeon.level.insideMap(cell)) continue;
			int terrain = Dungeon.level.map[cell];
			if (terrain == Terrain.EMPTY || terrain == Terrain.EMPTY_DECO
					|| terrain == Terrain.EMPTY_SP || terrain == Terrain.GRASS) {
				Level.set(cell, Terrain.OLD_HIGH_GRASS);
				GameScene.updateMap(cell);
			}
		}
		Dungeon.observe();
		return true;
	}

	private boolean canUse(Hero hero) {
		return hero != null && Dungeon.level != null && Dungeon.gold > GOLD_COST;
	}

	private void payAndOperate(Hero hero) {
		Dungeon.gold -= GOLD_COST;
		hero.spend(1f);
		hero.busy();
		if (hero.sprite != null) hero.sprite.operate(hero.pos);
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 50 * quantity; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = bundle.getInt(CHARGE); }
}
