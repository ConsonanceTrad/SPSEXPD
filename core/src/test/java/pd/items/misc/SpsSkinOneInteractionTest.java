package pd.items.misc;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.QuickSlot;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.Locked;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.Muscle;
import pd.actors.buffs.NewCombo;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.Silent;
import pd.actors.buffs.Slow;
import pd.actors.buffs.SuperArcane;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Heap;
import pd.items.Item;
import pd.items.artifacts.AlienBag;
import pd.items.artifacts.TimeOclock;
import pd.items.artifacts.TimekeepersHourglass;
import pd.items.bombs.BuildBomb;
import pd.items.bombs.DarkBomb;
import pd.items.bombs.DungeonBomb;
import pd.items.bombs.EarthBomb;
import pd.items.bombs.FishingBomb;
import pd.items.bombs.HugeBomb;
import pd.items.bombs.IceBomb;
import pd.items.bombs.LightBomb;
import pd.items.bombs.SpsFireBomb;
import pd.items.bombs.StormBomb;
import pd.items.food.completefood.CompleteFood;
import pd.items.medicine.Foamedbeverage;
import pd.items.medicine.Pill;
import pd.items.medicine.TimePill;
import pd.items.medicine.Timepill2;
import pd.items.wands.CannonOfMage;
import pd.items.weapon.melee.start.BraveBook;
import pd.items.weapon.melee.start.DiamondPickaxe;
import pd.items.weapon.melee.start.HolyMace;
import pd.items.weapon.melee.start.PixelTorch;
import pd.items.weapon.missiles.buildblock.BookBlock;
import pd.items.weapon.missiles.buildblock.DoorBlock;
import pd.items.weapon.missiles.buildblock.StoneBlock;
import pd.items.weapon.missiles.buildblock.WallBlock;
import pd.items.weapon.missiles.buildblock.WoodenBlock;
import pd.items.weapon.missiles.fusion.RocketMissile;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.plants.Plant;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public final class SpsSkinOneInteractionTest {

	private static final int CENTER = 8 + 8 * 16;

	public static void main(String[] args) {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		try {
			testAttackShoes();
			testDiamondPickaxe();
			testTimeOclock();
			testTimePills();
			testAlienBag();
			testSkinOneCombat();
			System.out.println("SPS皮肤1交互测试通过：三格跳跃、范围伤害、六类地形挖掘、时间控制、肩包升级充能、战斗技能与补给均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
			app.exit();
		}
	}

	private static void testAttackShoes() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		hero.lvl = 10;
		TestMob mob = new TestMob();
		mob.HP = mob.HT = 100;
		mob.pos = CENTER + 3 + level.width();
		level.mobs().add(mob);
		Actor.add(mob);
		Random.pushGenerator(0x53484F4553L);
		try {
			check(new AttackShoes().jumpTo(hero, CENTER + 6), "攻击鞋没有执行三格跳跃");
		} finally {
			Random.popGenerator();
		}
		check(hero.pos == CENTER + 3, "攻击鞋落点不是路径上的第三格：" + hero.pos);
		check(mob.HP == 40, "攻击鞋落地范围伤害错误：" + mob.HP);
		hero.rooted = true;
		check(!new AttackShoes().jumpTo(hero, hero.pos + 2), "缠绕状态下仍能使用攻击鞋");
	}

	private static void testDiamondPickaxe() {
		int[] terrains = {Terrain.WALL, Terrain.DOOR, Terrain.BOOKSHELF, Terrain.GLASS_WALL,
				Terrain.BARRICADE, Terrain.STATUE};
		Class<?>[] drops = {WallBlock.class, DoorBlock.class, BookBlock.class, null,
				WoodenBlock.class, StoneBlock.class};
		for (int i = 0; i < terrains.length; i++) {
			TestLevel level = freshLevel();
			Hero hero = freshHero(level);
			int target = CENTER - level.width() - 1;
			Level.set(target, terrains[i], level);
			Random.pushGenerator(0x5049434B0000L + i);
			try {
				check(new DiamondPickaxe().mine(hero), "钻石镐无法挖掘地形：" + terrains[i]);
			} finally {
				Random.popGenerator();
			}
			check(level.map[target] == Terrain.EMBERS, "挖掘后没有留下余烬地面：" + terrains[i]);
			Heap heap = level.heaps.get(hero.pos);
			if (drops[i] == null) check(heap == null, "玻璃墙错误地产生了方块");
			else check(heap != null && drops[i].isInstance(heap.peek()), "挖掘掉落类型错误：" + terrains[i]);
		}
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		check(!new DiamondPickaxe().mine(hero), "周围没有可挖地形时仍执行了挖掘");
	}

	private static void testTimeOclock() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		TestMob mob = new TestMob();
		mob.HP = mob.HT = 100;
		mob.pos = CENTER + 2;
		level.mobs().add(mob);
		TimeOclock clock = new TimeOclock();
		check(clock.useFreeze(hero), "怀表无法发动全层冻结");
		check(clock.charge() == 4 && hero.buff(HasteBuff.class) != null, "怀表没有扣除充能或赋予加速");
		check(mob.buff(Paralysis.class) != null && mob.buff(Slow.class) != null
				&& mob.buff(ArmorBreak.class) != null, "怀表没有给本层怪物施加三种减益");

		level = freshLevel();
		hero = freshHero(level);
		clock = new TimeOclock();
		hero.belongings.artifact = clock;
		clock.activate(hero);
		check(clock.useStasis(hero) && hero.invisible == 1 && clock.charge() == 4, "怀表静止状态没有正确启动");
		Bundle stasisState = new Bundle();
		clock.storeInBundle(stasisState);
		check(stasisState.contains("buff"), "怀表没有保存0.9.8的完整静止状态字段");
		check(clock.doUnequip(hero, true, false), "怀表无法在静止期间卸下");
		check(hero.invisible == 0 && hero.buff(TimeOclock.TimeStasis.class) == null, "卸下怀表后静止状态没有解除");

		Bundle oneChargeState = new Bundle();
		oneChargeState.put("charge", 1);
		TimeOclock oneCharge = new TimeOclock();
		oneCharge.restoreFromBundle(oneChargeState);
		hero.belongings.artifact = oneCharge;
		check(oneCharge.actions(hero).contains(TimeOclock.AC_ACTIVATE),
				"怀表在1点充能时没有保留0.9.8的可见激活动作");
	}

	private static void testTimePills() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		Statistics.foodEaten = 0;
		TimePill sandPill = new TimePill();
		sandPill.collect(hero.belongings.backpack);
		sandPill.execute(hero, Pill.AC_EAT);
		check(hero.buff(HasteBuff.class) != null && hero.buff(HasteBuff.class).cooldown() >= 399f,
				"炼金时之块没有提供旧版400回合加速");
		check(heapContains(level.heaps.get(hero.pos), TimekeepersHourglass.sandBag.class),
				"炼金时之块没有掉落时光沙");

		level = freshLevel();
		hero = freshHero(level);
		Timepill2 springPill = new Timepill2();
		springPill.collect(hero.belongings.backpack);
		springPill.execute(hero, Pill.AC_EAT);
		check(hero.buff(HasteBuff.class) != null && hero.buff(HasteBuff.class).cooldown() >= 399f,
				"锻造时之块没有提供旧版400回合加速");
		check(heapContains(level.heaps.get(hero.pos), TimeOclock.Clock.class),
				"锻造时之块没有掉落怀表发条");
		check(Statistics.foodEaten == 2, "两种时之块没有计入旧版食用统计");

		Pill locked = new Pill();
		locked.collect(hero.belongings.backpack);
		Buff.affect(hero, Locked.class, 5f);
		check(!locked.actions(hero).contains(Pill.AC_EAT), "封印状态仍显示药丸食用动作");
		locked.execute(hero, Pill.AC_EAT);
		check(hero.belongings.backpack.contains(locked) && Statistics.foodEaten == 2,
				"封印状态错误消耗药丸或增加食用统计");
	}

	private static boolean heapContains(Heap heap, Class<? extends Item> type) {
		if (heap == null) return false;
		for (Item item : heap.items) if (type.isInstance(item)) return true;
		return false;
	}

	private static void testAlienBag() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		AlienBag bag = new AlienBag();
		hero.belongings.artifact = bag;
		bag.activate(hero);
		for (int i = 0; i < 11; i++) bag.gainExp();
		check(bag.level() == 1, "异星肩包没有在第11次击杀后升级");

		AlienBag.BagRecharge recharge = hero.buff(AlienBag.BagRecharge.class);
		check(recharge != null, "异星肩包没有附加被动充能状态");
		recharge.act();
		Bundle savedBag = new Bundle();
		bag.storeInBundle(savedBag);
		check(savedBag.contains("partialcharge") && savedBag.contains("partialCharge")
				&& savedBag.getFloat("partialcharge") == savedBag.getFloat("partialCharge"),
				"异星肩包未双写新旧部分充能字段");
		float savedPartial = savedBag.getFloat("partialCharge");
		savedBag.remove("partialcharge");
		AlienBag legacyBag = new AlienBag();
		legacyBag.restoreFromBundle(savedBag);
		Bundle migratedBag = new Bundle();
		legacyBag.storeInBundle(migratedBag);
		check(migratedBag.getFloat("partialcharge") == savedPartial,
				"异星肩包未读取旧版partialCharge");
		Bundle preferredBag = new Bundle();
		bag.storeInBundle(preferredBag);
		preferredBag.put("partialCharge", 9f);
		AlienBag currentBag = new AlienBag();
		currentBag.restoreFromBundle(preferredBag);
		Bundle currentState = new Bundle();
		currentBag.storeInBundle(currentState);
		check(currentState.getFloat("partialcharge") != 9f,
				"异星肩包未优先读取新部分充能字段");
		for (int i = 0; i < 500; i++) recharge.act();
		check(bag.charge() == 100, "异星肩包没有充满100点能量：" + bag.charge());
		check(bag.shield(hero), "满能量异星肩包无法启动护盾");
		check(bag.charge() == 0 && hero.buff(EnergyArmor.class) != null
				&& hero.buff(DefenceUp.class) != null && hero.buff(Invisibility.class) != null
				&& hero.buff(Levitation.class) != null && hero.buff(HasteBuff.class) != null,
				"异星肩包护盾没有施加完整的五种状态");

		Class<?>[] expected = {BuildBomb.class, DungeonBomb.class, HugeBomb.class, RocketMissile.class,
				SpsFireBomb.class, IceBomb.class, EarthBomb.class, StormBomb.class, LightBomb.class,
				DarkBomb.class, FishingBomb.class};
		float[] weights = {0, 3, 0, 1, 1, 1, 1, 1, 1, 1, 1};
		check(Arrays.equals(expected, AlienBag.bombSupplyClasses()), "异星肩包炸弹补给池条目错误");
		check(Arrays.equals(weights, AlienBag.bombSupplyWeights()), "异星肩包炸弹补给池权重错误");
		for (int i = 0; i < 500; i++) {
			Item item = AlienBag.randomBombSupply();
			check(!(item instanceof BuildBomb) && !(item instanceof HugeBomb), "异星肩包生成了旧版零权重炸弹");
		}

		bag.level(3);
		check(bag.actions(hero).contains(AlienBag.AC_BOMB), "3级异星肩包没有显示旧版爆破动作");
		check(bag.bombs(hero) && bag.level() == 1, "3级异星肩包没有按旧版消耗2级");
		bag.level(4);
		Random.pushGenerator(0x424147535550504CL);
		try {
			check(bag.bombs(hero), "4级异星肩包无法产生补给");
		} finally {
			Random.popGenerator();
		}
		check(bag.level() == 2 && level.heaps.get(hero.pos) != null
				&& level.heaps.get(hero.pos).items.size() == 2, "异星肩包补给数量或等级消耗错误");
		boolean hasHighFood = false;
		for (Item item : level.heaps.get(hero.pos).items) {
			hasHighFood |= item instanceof CompleteFood || item instanceof Foamedbeverage;
		}
		check(hasHighFood, "异星肩包没有从旧版高级食物池生成补给");
	}

	private static void testSkinOneCombat() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		hero.lvl = 10;
		TestMob target = mobAt(level, CENTER + 1, 100);
		AttackShield shield = new AttackShield();
		for (int i = 0; i < 10; i++) shield.gainCharge();
		check(shield.castAt(hero, target.pos) && shield.charge() == 0 && target.HP < 60
				&& target.buff(Vertigo.class) != null, "波动拳射击的耗能、伤害或晕向错误");
		for (int i = 0; i < 20; i++) shield.gainCharge();
		check(shield.blast(hero) && hero.buff(AttackShield.LongBuff.class) != null,
				"波动拳满充能没有启动隆拳状态");

		target.HP = target.HT = 100;
		target.pos = CENTER + 1;
		NewCombo combo = Buff.affect(hero, NewCombo.class);
		combo.hit(); combo.hit();
		check(combo.count() == 2 && combo.finisherReady(), "隆拳连击两次命中后没有解锁终结技");
		check(combo.finish(hero, target) && target.pos == CENTER + 2 && target.buff(Vertigo.class) != null,
				"隆拳击垮没有造成击退与晕向");
		check(hero.buff(NewCombo.class) == null, "隆拳击垮后没有清空连击");
		combo = Buff.affect(hero, NewCombo.class); combo.hit(); combo.hit(); combo.miss(); combo.miss();
		check(hero.buff(NewCombo.class) == null, "隆拳连击没有在连续两次落空后中断");

		level = freshLevel(); hero = freshHero(level); hero.fieldOfView = level.heroFOV;
		TestMob regular = mobAt(level, CENTER + 1, 2000);
		HolyMace mace = new HolyMace();
		for (int i = 0; i < 5; i++) mace.proc(hero, regular, 20);
		check(mace.light(hero) && mace.charge() == 0 && hero.buff(pd.actors.buffs.Light.class) != null
				&& regular.buff(Terror.class) != null, "圣锤强光没有照明、恐惧或正确耗能");
		TaggedMob demon = taggedMobAt(level, CENTER + level.width(), 1000, pd.actors.Char.Property.DEMONIC);
		int before = demon.HP;
		for (int i = 0; i < 10; i++) mace.proc(hero, demon, 20);
		check(mace.charge() == 10 && demon.HP == before - 100, "圣锤反恶魔伤害或充能错误");
		check(mace.trial(hero, demon.pos) && mace.charge() == 0 && demon.HP < before - 100,
				"圣锤审判没有命中或正确耗能");

		BraveBook book = new BraveBook();
		regular.HP = regular.HT = 5000;
		for (int i = 0; i < 5; i++) book.proc(hero, regular, 20);
		check(book.improve(hero) && book.charge() == 0 && hero.buff(Muscle.class) != null
				&& hero.buff(SuperArcane.class) != null, "勇者之书强化没有施加力量与法强");
		for (int i = 0; i < 10; i++) book.proc(hero, regular, 20);
		check(book.heal(hero) && book.charge() == 0 && hero.buff(ShieldArmor.class) != null,
				"勇者之书治疗没有施加生命强化与物理护盾");
		regular.HP = regular.HT = 1000;
		book.proc(hero, regular, 20);
		check(regular.buff(Silent.class) != null, "勇者之书首次命中没有施加沉默");
		before = regular.HP; book.proc(hero, regular, 20);
		check(regular.HP <= before - 10, "勇者之书对已沉默目标没有追加半额伤害");

		PixelTorch torch = new PixelTorch();
		int spp = hero.spp;
		for (int i = 0; i < 100; i++) torch.proc(hero, regular, 1);
		check(hero.spp == spp + 100, "像素火把没有按命中积蓄能量");
		hero.spp = 51;
		torch.execute(hero, PixelTorch.AC_TLIGHT);
		check(hero.spp == 1 && hero.buff(pd.actors.buffs.Light.class) != null,
				"像素火把强化照明没有消耗50点能量");

		CannonOfMage cannon = new CannonOfMage();
		for (int effect = 0; effect < 7; effect++) {
			TestMob cannonTarget = mobAt(level, CENTER - (effect + 1) * level.width(), 5000);
			cannon.applyRandomEffect(cannonTarget, effect);
			switch (effect) {
				case 0: check(cannonTarget.HP < cannonTarget.HT, "七彩大炮额外伤害效果缺失"); break;
				case 1: check(cannonTarget.buff(pd.actors.buffs.Burning.class) != null, "七彩大炮燃烧效果缺失"); break;
				case 2: check(cannonTarget.buff(pd.actors.buffs.Shocked.class) != null, "七彩大炮电击效果缺失"); break;
				case 3: check(cannonTarget.buff(pd.actors.buffs.Ooze.class) != null, "七彩大炮腐蚀效果缺失"); break;
				case 4: check(cannonTarget.buff(pd.actors.buffs.Frost.class) != null, "七彩大炮冰冻效果缺失"); break;
				case 5: check(cannonTarget.buff(pd.actors.buffs.AttackDown.class) != null
						&& cannonTarget.buff(ArmorBreak.class) != null, "七彩大炮降攻或破甲效果缺失"); break;
				case 6: check(cannonTarget.buff(pd.actors.buffs.Blindness.class) != null, "七彩大炮失明效果缺失"); break;
			}
		}
	}

	private static TestMob mobAt(TestLevel level, int pos, int health) {
		TestMob mob = new TestMob(); mob.HP = mob.HT = health; mob.pos = pos;
		level.mobs().add(mob); Actor.add(mob); return mob;
	}

	private static TaggedMob taggedMobAt(TestLevel level, int pos, int health,
			pd.actors.Char.Property property) {
		TaggedMob mob = new TaggedMob(property); mob.HP = mob.HT = health; mob.pos = pos;
		level.mobs().add(mob); Actor.add(mob); return mob;
	}

	private static TestLevel freshLevel() {
		Actor.clear();
		Dungeon.depth = 1;
		Dungeon.branch = 0;
		Dungeon.quickslot = new QuickSlot();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		return level;
	}

	private static Hero freshHero(TestLevel level) {
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		hero.pos = CENTER;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestMob extends Mob {
		@Override public int drRoll() { return 0; }
	}
	private static final class TaggedMob extends Mob {
		TaggedMob(Property property) { properties.add(property); }
		@Override public int drRoll() { return 0; }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(16, 16);
			Arrays.fill(map, Terrain.EMPTY);
			mobs().clear();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			buildFlagMaps();
			Arrays.fill(heroFOV, true);
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) { heap = new Heap(); heap.pos = cell; heaps.put(cell, heap); }
			heap.drop(item);
			return heap;
		}
	}

	private SpsSkinOneInteractionTest() { }
}
