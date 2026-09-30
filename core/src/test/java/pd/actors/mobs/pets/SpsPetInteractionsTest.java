package pd.actors.mobs.pets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.HasteBuff;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.items.Item;
import pd.items.food.completefood.PetFood;
import pd.items.potions.PotionOfFrost;
import pd.items.scrolls.ScrollOfUpgrade;
import pd.items.weapon.melee.special.SJRBMusic;
import pd.items.weapon.missiles.MoneyPack;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.utils.SparseArray;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public final class SpsPetInteractionsTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		try {
			testFoodInteraction();
			testRewardCollection();
			testStayFollowAndSwap();
			testWindowAndMusicResources();
			System.out.println("SPS宠物交互测试通过：喂食、收获、留守、跟随、交换位置、35类产物和音乐套装均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
		}
	}

	private static void testFoodInteraction() {
		Hero hero = heroAndLevel(10);
		BlueDragon pet = new BlueDragon();
		Actor.add(pet);
		pet.HP = 50;
		pet.cooldown = 41;
		PotionOfFrost food = new PotionOfFrost();
		hero.belongings.backpack.items.add(food);
		int missing = pet.HT - pet.HP;
		int healed = pet.feed(food, hero);
		check(healed == (int)(missing * 0.8f) && pet.HP == 50 + healed,
				"喜爱食物没有恢复缺失生命的80%");
		check(pet.rewardCooldown() == 20, "喂食没有将当前冷却减半");
		check(!hero.belongings.backpack.items.contains(food), "喂食后物品没有消耗");
		check(pet.buff(HasteBuff.class) != null, "喂食后宠物没有获得10回合加速");

		ScrollOfUpgrade refused = new ScrollOfUpgrade();
		hero.belongings.backpack.items.add(refused);
		int hp = pet.HP;
		int cooldown = pet.rewardCooldown();
		check(pet.feed(refused, hero) == -1 && pet.HP == hp && pet.rewardCooldown() == cooldown
				&& hero.belongings.backpack.items.contains(refused), "拒绝食物仍然被消耗或改变了宠物状态");
		check(!new BugDragon().lovefood(new PetFood()), "BUG龙不应接受任何食物");
		check(new YearPet().lovefood(new MoneyPack()), "年兽没有接受钱袋");
		check(new CocoCat().lovefood(new pd.items.food.fusion.Nut()),
				"可可猫没有接受坚果");
		check(new Velocirooster().lovefood(new pd.plants.Sungrass.Seed()),
				"迅猛鸡没有接受种子");
	}

	private static void testRewardCollection() {
		heroAndLevel(10);
		LegacyPet[] pets = {
				new BlueDragon(), new GreenDragon(), new LightDragon(), new RedDragon(), new ShadowDragon(),
				new VioletDragon(), new GoldDragon(), new BugDragon(), new BlueGirl(), new LeryFire(),
				new Scorpion(), new YearPet(), new Bunny(), new CocoCat(), new Velocirooster(), new Haro(),
				new PigPet(), new Abi(), new ButterflyPet(), new Chocobo(), new Datura(), new DogPet(),
				new DwarfBoy(), new FoxHelper(), new FrogPet(), new GentleCrab(), new Kodora(), new LitDemon(),
				new Monkey(), new Snake(), new Spider(), new StarKid(), new Stone(), new Fly(), new RibbonRat()
		};
		String[] rewards = {
				"WandOfFreeze|WandOfFlow", "WandOfLightning|WandOfTCloud", "WandOfLight|WandOfCharm",
				"WandOfFirebolt|WandOfMeteorite", "WandOfFlock|WandOfBlood", "WandOfSwamp|WandOfAcid",
				"WandOfMagicMissile|WandOfDisintegration", "ErrorAmmo|ErrorArmor|ErrorW|WandOfError", "StoneArmor",
				"BottleFire", "PotionOfToxicGas", "MoneyPack", "EasterEgg", "DungeonBomb", "SJRBMusic",
				"ScrollOfRecharging", "Honeymeat", "WandOfLight", "Garbage", "RandomEasterEgg", "Seed",
				"MoonCake", "NormalRation", "UpgradeBlobRed", "Whip", "PotionOfShield", "PotionOfLiquidFlame",
				"ScrollOfRage", "Fruit",
				"PotionOfToxicGas", "WoodenArmor", "PotionOfExperience", "NornStone",
				"PotionOfMending", "ScrollOfMirrorImage"
		};
		check(pets.length == 35 && rewards.length == pets.length, "宠物交互测试清单数量错误");
		for (int i = 0; i < pets.length; i++) {
			LegacyPet pet = pets[i];
			pet.cooldown = 4;
			check(!pet.rewardReady() && pet.claimReward() == null && pet.rewardCooldown() == 4,
					pet.getClass().getSimpleName() + "在冷却4时错误开放了收获");
			pet.cooldown = 3;
			Item reward = pet.claimReward();
			boolean rewardMatches = reward != null && Arrays.asList(rewards[i].split("\\|"))
					.contains(reward.getClass().getSimpleName());
			if (pet instanceof Monkey) {
				rewardMatches = reward instanceof pd.items.food.fruit.Fruit;
			} else if (pet instanceof Stone) {
				rewardMatches = reward instanceof pd.items.nornstone.NornStone;
			}
			check(rewardMatches,
					pet.getClass().getSimpleName() + "收获产物错误: " + (reward == null ? "null" : reward.getClass().getSimpleName()));
			int expectedReset = pet instanceof FoxHelper ? 135 : pet instanceof Abi ? 60
					: pet instanceof Bunny || pet instanceof CocoCat || pet instanceof Velocirooster
					|| pet instanceof Haro || pet instanceof YearPet ? 10 : 30;
			check(pet.rewardCooldown() == expectedReset,
					pet.getClass().getSimpleName() + "收获后的冷却重置错误");
			if (reward instanceof MoneyPack) check(reward.quantity() == 5, "年兽产出的钱袋数量不是5");
		}
	}

	private static void testStayFollowAndSwap() {
		Hero hero = heroAndLevel(10);
		BlueDragon pet = new BlueDragon();
		pet.pos = hero.pos + 1;
		Dungeon.level.mobs.add(pet);
		Actor.add(pet);
		pet.stayHere();
		check(pet.staying(), "宠物没有立即进入原地留守状态");
		pet.followHero();
		check(!pet.staying(), "宠物切换跟随后仍处于留守状态");
		int heroPos = hero.pos;
		int petPos = pet.pos;
		check(pet.swapPlaces(hero) && hero.pos == petPos && pet.pos == heroPos, "相邻宠物交换位置失败");
		pet.pos = hero.pos + Dungeon.level.width() * 2;
		check(!pet.swapPlaces(hero), "非相邻宠物错误地交换了位置");
	}

	private static void testWindowAndMusicResources() throws Exception {
		String zhWindows = Files.readString(Paths.get("messages/windows/windows_zh.properties"), StandardCharsets.UTF_8);
		String enWindows = Files.readString(Paths.get("messages/windows/windows.properties"), StandardCharsets.UTF_8);
		String zhItems = Files.readString(Paths.get("messages/items/items_zh.properties"), StandardCharsets.UTF_8);
		check(zhWindows.contains("windows.wndpetinfo.title=宠物信息")
				&& zhWindows.contains("windows.wndpetinfo.change=交换")
				&& zhWindows.contains("windows.wndpetinfo.recover=收获")
				&& enWindows.contains("windows.wndpetinfo.recover=Get reward, cooldown less than 5"),
				"宠物五项交互窗口资源缺失或与0.9.8不一致");
		check(zhItems.contains("items.weapon.melee.special.sjrbmusic.name=S-J-R-B音乐套装")
				&& zhItems.contains("2019暑假快乐！\\n共振，高级穿刺，迷人，喧闹")
				&& zhItems.contains("items.weapon.melee.special.sjrbmusic.rap=鸡你太美!!!"),
				"S-J-R-B音乐套装中文资源缺失、被改写或乱码");
		SJRBMusic music = new SJRBMusic();
		check(music.image == ItemSpriteSheet.SJRB_MUSIC && music.tier == 1
				&& music.min(0) == 3 && music.max(0) == 6, "S-J-R-B音乐套装图标或基础属性错误");
	}

	private static Hero heroAndLevel(int level) {
		Actor.clear();
		Hero hero = new Hero();
		Talent.initClassTalents(hero);
		hero.HP = hero.HT = 100;
		hero.petLevel = level;
		hero.pos = 100;
		Dungeon.hero = hero;
		Dungeon.level = new TestLevel();
		Actor.add(hero);
		return hero;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static class TestLevel extends Level {
		TestLevel() {
			setSize(32, 32);
			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<>();
			traps = new SparseArray<>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			Arrays.fill(map, Terrain.EMPTY);
			Arrays.fill(passable, true);
			buildFlagMaps();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private SpsPetInteractionsTest() {
	}
}
