/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.meatfood.Meat;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.LuckyBadge;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GreyRatSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

/** Original SPS-PD runtime and save identity for the grey rat. */
public class GreyRat extends Mob {

	private static final float SPAWN_DELAY = 2f;

	{
		spriteClass = GreyRatSprite.class;
		HP = HT = 80 + legacyDepthAdjustment(0) * Random.NormalIntRange(3, 5);
		defenseSkill = 8 + legacyDepthAdjustment(0);
		EXP = 5;
		loot = Meat.class;
		lootChance = 0.5f;
		properties.add(Property.BEAST);
		resistances.add(ToxicGas.class);
		immunities.add(Amok.class);
		immunities.add(Sleep.class);
		immunities.add(Terror.class);
		immunities.add(Burning.class);
		immunities.add(ScrollOfPsionicBlast.class);
		immunities.add(Vertigo.class);
		immunities.add(Poison.class);
		immunities.add(StenchGas.class);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(1, 7 + legacyDepthAdjustment(0)); }
	@Override public float attackDelay() { return 0.8f; }
	@Override public int attackSkill(Char target) { return 6 + legacyDepthAdjustment(0); }
	@Override public int drRoll() { return Random.NormalIntRange(0, 3); }

	@Override
	public void rollToDropLoot() {
		if (Dungeon.hero == null || Dungeon.level == null || Dungeon.hero.lvl > maxLvl + 800) return;
		float bonus = 0.02f * LuckyBadge.luckBonus(Dungeon.hero);
		Item item = null;
		if (Random.Float() < 0.5f + bonus) item = new Meat();
		else if (Random.Float() < 0.25f + bonus) item = Generator.random(Generator.Category.MUSHROOM);
		if (item != null) {
			Heap heap = Dungeon.level.drop(item, pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}
		rollKnowledgeLoot();
	}

	public static void spawnAround(int center) {
		if (Dungeon.level == null) return;
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = center + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
					&& Actor.findChar(cell) == null) spawnAt(cell);
		}
	}

	public static GreyRat spawnAt(int cell) {
		GreyRat rat = new GreyRat();
		rat.pos = cell;
		rat.state = rat.HUNTING;
		GameScene.add(rat, SPAWN_DELAY);
		return rat;
	}
}
