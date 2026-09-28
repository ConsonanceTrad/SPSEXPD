/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.StoneOre;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.RobotDMT;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.LuckyBadge;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.ShortSword;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

/** Original SPS-PD runtime and save identity for the damaged cave robot. */
public class BrokenRobot extends SpsDM300.BrokenRobot {

	{
		viewDistance = Light.DISTANCE;
		loot = ScrollOfRecharging.class;
		lootChance = 0.25f;
		properties.remove(Property.INORGANIC);
		properties.add(Property.MAGICER);
		properties.add(Property.MECH);
	}

	public static float legacyDropChance(int luckBonus) {
		return 0.25f + 0.02f * luckBonus;
	}

	@Override
	public void rollToDropLoot() {
		if (Dungeon.hero == null || Dungeon.level == null || Dungeon.hero.lvl > maxLvl + 800) return;
		float chance = legacyDropChance(LuckyBadge.luckBonus(Dungeon.hero));
		Item item = null;
		if (Random.Float() < chance) item = new ScrollOfRecharging();
		else if (Random.Float() < chance) item = new StoneOre();
		if (item != null) {
			Heap heap = Dungeon.level.drop(item, pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}
		rollKnowledgeLoot();
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(new PotionOfLiquidFlame(), new ShortSword(), new RobotDMT());
	}

	public static Class<?>[] specialLootTypes() {
		return new Class<?>[]{PotionOfLiquidFlame.class, ShortSword.class, RobotDMT.class};
	}

	public static void spawnAround(int center) {
		if (Dungeon.level == null) return;
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = center + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
					&& Actor.findChar(cell) == null) spawnAt(cell);
		}
	}

	public static BrokenRobot spawnAt(int cell) {
		BrokenRobot robot = new BrokenRobot();
		robot.pos = cell;
		robot.state = robot.HUNTING;
		GameScene.add(robot, 2f);
		return robot;
	}
}
