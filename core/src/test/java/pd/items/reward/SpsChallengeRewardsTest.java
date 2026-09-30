package pd.items.reward;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.hero.Hero;
import pd.actors.mobs.FishProtector;
import pd.actors.mobs.ForestProtector;
import pd.actors.mobs.GnollArcher;
import pd.actors.mobs.GoldThief;
import pd.actors.mobs.GraveProtector;
import pd.actors.mobs.MossySkeleton;
import pd.actors.mobs.SpsChallengeKillRewards;
import pd.actors.mobs.VaultProtector;
import pd.items.AncientCoin;
import pd.items.Bone;
import pd.items.ConchShell;
import pd.items.Crystalnucleus;
import pd.items.Dewdrop;
import pd.items.Heap;
import pd.items.Item;
import pd.items.RedDewdrop;
import pd.items.SacrificeBook;
import pd.items.StoneOre;
import pd.items.TreasureMap;
import pd.items.YellowDewdrop;
import pd.items.food.GoldenNut;
import pd.items.food.fruit.Blackberry;
import pd.items.food.fruit.Blueberry;
import pd.items.food.fruit.Cloudberry;
import pd.items.food.fruit.FullMoonberry;
import pd.items.food.fruit.Moonberry;
import pd.levels.SewerChallengeLevel;
import watabou.noosa.Game;
import watabou.utils.Bundle;
import watabou.utils.FileUtils;
import watabou.utils.Random;
import watabou.utils.SparseArray;

public final class SpsChallengeRewardsTest {
	private SpsChallengeRewardsTest() { }

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-challenge-rewards" + java.io.File.separator);
		Game.version = "test";
		checkIntegerScaling();
		checkExclusiveDewDrops();
		checkKillMilestones();
		checkRewardContents();
		checkGoldenNut();
		checkSaveContracts();
		System.out.println("SPS百杀奖励测试通过：旧版整数成长、互斥露珠、25/50/100奖励、四区域奖励包、金色坚果和存档字段均正常。");
	}

	private static void checkIntegerScaling() {
		Statistics.gnollArchersKilled = 9;
		Statistics.mossySkeletonsKilled = 9;
		Statistics.albinoPiranhasKilled = 9;
		Statistics.goldThievesKilled = 9;
		Random.pushGenerator(0x5350534D4F42534CL);
		try {
			GnollArcher archer = new GnollArcher();
			ForestProtector forest = new ForestProtector();
			MossySkeleton mossy = new MossySkeleton();
			GraveProtector grave = new GraveProtector();
			FishProtector fish = new FishProtector();
			VaultProtector vault = new VaultProtector();
			for (int i = 0; i < 10000; i++) {
				between(archer.damageRoll(), 1, 9, "豺狼弓箭手");
				between(forest.damageRoll(), 5, 11, "森林守卫");
				between(mossy.damageRoll(), 20, 46, "青苔骷髅");
				between(grave.damageRoll(), 8, 16, "墓穴守卫");
				between(fish.damageRoll(), 8, 11, "鱼人守卫");
				between(vault.damageRoll(), 8, 11, "宝库守卫");
			}
			check(new GoldThief().drRoll() == 23, "黄金小盗护甲未保留旧版表达式语义");
		} finally {
			Random.popGenerator();
		}
	}

	private static void checkExclusiveDewDrops() {
		RecordingLevel level = new RecordingLevel();
		Dungeon.level = level;
		Dungeon.hero = new Hero();
		MossySkeleton mossy = new MossySkeleton();
		mossy.pos = 1;
		int yellow = 0;
		int red = 0;
		Random.pushGenerator(0x5350534445574C4CL);
		try {
			for (int i = 0; i < 30000; i++) {
				int before = count(level, Dewdrop.class);
				mossy.rollToDropLoot();
				int after = count(level, Dewdrop.class);
				check(after - before <= 1, "青苔骷髅一次判定掉落了两颗露珠");
			}
		} finally {
			Random.popGenerator();
		}
		yellow = count(level, YellowDewdrop.class);
		red = count(level, RedDewdrop.class);
		check(yellow > 14400 && yellow < 15600, "黄色露珠概率偏离50%，实际=" + yellow);
		check(red > 1200 && red < 1800, "红色露珠有效概率偏离5%，实际=" + red);
	}

	private static void checkKillMilestones() {
		RecordingLevel level = new RecordingLevel();
		Dungeon.level = level;
		resetKills();
		Statistics.gnollArchersKilled = 25;
		SpsChallengeKillRewards.gnollArcher(1);
		check(count(level, TreasureMap.class) == 1, "弓箭手25杀未掉落藏宝图");
		level.clearDrops();
		Statistics.mossySkeletonsKilled = 25;
		SpsChallengeKillRewards.mossySkeleton(1);
		check(count(level, Bone.class) == 1, "青苔骷髅25杀未掉落亡灵短骨");
		level.clearDrops();
		Statistics.albinoPiranhasKilled = 25;
		SpsChallengeKillRewards.albinoPiranha(1);
		check(count(level, ConchShell.class) == 1, "白化食人鱼25杀未掉落巨蟹海螺");
		level.clearDrops();
		Statistics.goldThievesKilled = 25;
		SpsChallengeKillRewards.goldThief(1);
		check(count(level, AncientCoin.class) == 1, "黄金小盗25杀未掉落上朝贡物");

		level.clearDrops();
		Statistics.goldThievesKilled = 50;
		SpsChallengeKillRewards.goldThief(1);
		check(count(level, SacrificeBook.class) == 1, "50杀未掉落献祭之书");

		level.clearDrops();
		Statistics.gnollArchersKilled = 100;
		Statistics.mossySkeletonsKilled = 100;
		Statistics.albinoPiranhasKilled = 100;
		Statistics.goldThievesKilled = 99;
		SpsChallengeKillRewards.gnollArcher(1);
		check(count(level, SewerReward.class) == 1 && count(level, GoldenNut.class) == 0,
				"弓箭手100杀奖励或金色坚果门槛错误");
		level.clearDrops();
		Statistics.goldThievesKilled = 100;
		SpsChallengeKillRewards.goldThief(1);
		check(count(level, CityReward.class) == 1 && count(level, GoldenNut.class) == 1,
				"四类100杀未同时掉落城市奖励和唯一金色坚果");
	}

	private static void checkRewardContents() {
		Item[] sewer = new SewerReward().contents();
		check(sewer.length == 1 && sewer[0] instanceof StoneOre && sewer[0].quantity() == 20,
				"下水道奖励包内容错误");
		Item[] prison = new PrisonReward().contents();
		check(prison.length == 1 && prison[0] instanceof FullMoonberry, "监狱奖励包内容错误");
		Item[] cave = new CaveReward().contents();
		check(cave.length == 4 && cave[0] instanceof Moonberry && cave[1] instanceof Cloudberry
				&& cave[2] instanceof Blueberry && cave[3] instanceof Blackberry, "洞穴奖励包莓果类型错误");
		for (Item item : cave) check(item.quantity() == 10, "洞穴奖励包莓果数量错误");
		Item[] city = new CityReward().contents();
		check(city.length == 5, "城市奖励包晶核数量错误");
		for (Item item : city) check(item instanceof Crystalnucleus && item.value() == 1000,
				"城市奖励包晶核类型或价值错误");
		for (int i = 1; i < city.length; i++) check(city[i] != city[0], "城市奖励包重复使用同一物品实例");
	}

	private static void checkGoldenNut() {
		Hero fortified = new Hero();
		new TestGoldenNut().apply(fortified, 0);
		check(fortified.HTBoost == 40 && fortified.STR == 11 && fortified.magicSkill() == 1,
				"金色坚果全能力分支错误");
		Hero strong = new Hero();
		new TestGoldenNut().apply(strong, 1);
		check(strong.HTBoost == 10 && strong.STR == 13 && strong.magicSkill() == 0,
				"金色坚果力量分支错误");
	}

	private static void checkSaveContracts() {
		Statistics.gnollArchersKilled = 25;
		Statistics.mossySkeletonsKilled = 50;
		Statistics.albinoPiranhasKilled = 75;
		Statistics.goldThievesKilled = 100;
		Bundle stats = new Bundle();
		Statistics.storeInBundle(stats);
		resetKills();
		Statistics.restoreFromBundle(stats);
		check(Statistics.gnollArchersKilled == 25 && Statistics.mossySkeletonsKilled == 50
				&& Statistics.albinoPiranhasKilled == 75 && Statistics.goldThievesKilled == 100,
				"四类击杀计数存档往返失败");
		for (Item key : new Item[]{new Bone(), new ConchShell(), new AncientCoin()}) {
			Bundle bundle = new Bundle();
			key.storeInBundle(bundle);
			Item restored = key.duplicate();
			check(restored != null && restored.image() == key.image(), key.getClass().getSimpleName() + "存档往返失败");
		}
	}

	private static void resetKills() {
		Statistics.gnollArchersKilled = 0;
		Statistics.mossySkeletonsKilled = 0;
		Statistics.albinoPiranhasKilled = 0;
		Statistics.goldThievesKilled = 0;
	}

	private static int count(RecordingLevel level, Class<? extends Item> type) {
		int result = 0;
		for (Heap heap : level.heaps.values()) for (Item item : heap.items) {
			if (type.isInstance(item)) result += item.quantity();
		}
		return result;
	}

	private static void between(int value, int min, int max, String name) {
		check(value >= min && value <= max, name + "数值越界：" + value + "，应为" + min + "至" + max);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestGoldenNut extends GoldenNut {
		void apply(Hero hero, int blessing) { applyBlessing(hero, blessing); }
	}

	private static final class RecordingLevel extends SewerChallengeLevel {
		RecordingLevel() { heaps = new SparseArray<>(); }
		@Override public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) {
				heap = new Heap();
				heap.pos = cell;
				heaps.put(cell, heap);
			}
			heap.drop(item);
			return heap;
		}
		void clearDrops() { heaps.clear(); }
	}
}
