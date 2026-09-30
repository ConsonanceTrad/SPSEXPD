/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.Light;
import pd.items.Heap;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.artifacts.RobotDMT;
import pd.items.misc.LuckyBadge;
import pd.items.potions.PotionOfLiquidFlame;
import pd.items.scrolls.ScrollOfRecharging;
import pd.items.weapon.melee.normalweapon.ShortSword;
import pd.scenes.GameScene;
import render.utils.PathFinder;
import render.utils.Random;

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
