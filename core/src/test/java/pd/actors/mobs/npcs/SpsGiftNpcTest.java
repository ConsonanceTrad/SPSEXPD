package pd.actors.mobs.npcs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ExProtect;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.mobs.Mob;
import pd.actors.mobs.SpsExitMobs;
import pd.actors.mobs.pets.CocoCat;
import pd.actors.mobs.pets.Haro;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.PigPet;
import pd.actors.mobs.pets.Velocirooster;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.armor.normalarmor.MachineArmor;
import pd.items.armor.specialarmor.RenBArmor;
import pd.items.eggs.CocoCatEgg;
import pd.items.eggs.EasterEgg;
import pd.items.eggs.Egg;
import pd.items.eggs.HaroEgg;
import pd.items.eggs.PigpetEgg;
import pd.items.eggs.VelociroosterEgg;
import pd.items.food.AflyFood;
import pd.items.food.completefood.Fruitsalad;
import pd.items.food.completefood.NutCake;
import pd.items.food.fruit.Strawberry;
import pd.items.food.vegetable.Truffles;
import pd.items.nornstone.NornStone;
import pd.items.scrolls.Scroll;
import pd.items.scrolls.ScrollOfIdentify;
import pd.items.sellitem.LingHeart;
import pd.items.sellitem.MiniBunny;
import pd.items.weapon.guns.GunE;
import pd.items.weapon.melee.normalweapon.Club;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.rooms.Room;
import pd.levels.rooms.special.SpsTentRoom;
import pd.levels.rooms.standard.EmptyRoom;
import pd.plants.Plant;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import render.noosa.Game;
import render.noosa.MovieClip;
import render.noosa.TextureFilm;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.imageio.ImageIO;

public final class SpsGiftNpcTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Generator.fullReset();
		Scroll.initLabels();
		try {
			testResidentsAndRewards();
			testFriendshipPersistenceAndTownSources();
			 testPetsAndEggs();
			 testTentPopulation();
			 testAssets();
			testAnimationFrames();
			System.out.println("SPS帐篷礼物居民测试通过：好感、奖励、宠物、孵化、存档、地图分支、原始素材和动画帧均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
		}
	}

	private static void testResidentsAndRewards() {
		Hero hero = hero(10);
		GiftRen ren = new GiftRen(); ren.friendship(90);
		check(ren.acceptsGift(new Club()) && !ren.acceptsGift(new NutCake()), "REN礼物筛选错误");
		check(has(ren.receiveGift(new Club(), hero), RenBArmor.class), "REN满好感奖励错误");

		GiftAshWolf ash = new GiftAshWolf(); ash.friendship(90);
		check(has(ash.receiveGift(new Fruitsalad(), hero), PigpetEgg.class), "阿萨满好感没有奖励像素猪之魂");
		ash.friendship(20);
		check(has(ash.receiveGift(new Fruitsalad(), hero), Truffles.class), "阿萨30好感没有奖励松露");

		GiftCoconut coconut = new GiftCoconut();
		int attack = hero.attackSkill(null);
		coconut.friendship(20); coconut.receiveGift(new NutCake(), hero);
		check(hero.attackSkill(null) == attack + 1, "椰子30好感没有永久提升命中");
		int strength = hero.STR;
		coconut.friendship(90); coconut.receiveGift(new NutCake(), hero);
		check(hero.STR == strength + 1, "椰子100好感没有永久提升力量");

		GiftBegger begger = new GiftBegger(); begger.friendship(90);
		check(has(begger.receiveGift(new NutCake(), hero), VelociroosterEgg.class), "乞丐满好感奖励错误");

		GiftAFly afly = new GiftAFly(); afly.friendship(90);
		check(count(afly.receiveGift(new Fruitsalad(), hero), AflyFood.class) == 3, "阿飞满好感饭团数量错误");

		GiftBaMech mech = new GiftBaMech(); mech.friendship(95);
		GiftNpc.GiftResult mechReward = mech.receiveGift(new Plant.Seed(), hero);
		check(mech.friendship() == 100 && has(mechReward, MachineArmor.class) && has(mechReward, GunE.class),
				"壁垒探机净好感或满好感奖励错误");

		GiftFruitWorker farmer = new GiftFruitWorker(); farmer.friendship(90);
		check(farmer.receiveGift(new Strawberry(), hero).items.size() == 3, "果农满好感稀有种子数量错误");

		GiftMeatSeller seller = new GiftMeatSeller(); seller.friendship(90);
		check(seller.acceptsGift(new HaroEgg()) && !seller.acceptsGift(new NutCake()), "肉商价值筛选错误");
		check(seller.receiveGift(new HaroEgg(), hero).items.size() == 3, "肉商满好感蜂蜜肉数量错误");

		GiftTorch torch = new GiftTorch(); torch.friendship(90);
		GiftNpc.GiftResult stones = torch.receiveGift(new ScrollOfIdentify(), hero);
		check(count(stones, NornStone.class) == 5, "火堆满好感没有奖励五种诺恩石");

		GiftFlyLing ling = new GiftFlyLing(); ling.friendship(90);
		check(has(ling.receiveGift(new Strawberry(), hero), LingHeart.class), "澪满好感水晶项链奖励错误");

		GiftBunnyKeeper bunny = new GiftBunnyKeeper(); bunny.friendship(30);
		check(has(bunny.receiveGift(new Egg(), hero), MiniBunny.class), "养兔人40好感小兔子奖励错误");
		bunny.friendship(90);
		check(has(bunny.receiveGift(new Egg(), hero), EasterEgg.class), "养兔人满好感大兔子奖励错误");

		for (GiftNpc npc : allResidents()) {
			check(npc.properties().contains(Char.Property.IMMOVABLE)
					&& npc.properties().contains(Char.Property.MINIBOSS), "礼物居民缺少旧版固定属性");
			int hp = npc.HP; npc.damage(99, hero); check(npc.HP == hp, "礼物居民可以被玩家伤害");
		}
	}

	private static void testFriendshipPersistenceAndTownSources() {
		GiftAshWolf original = new GiftAshWolf(); original.friendship(70);
		Bundle bundle = new Bundle(); original.storeInBundle(bundle);
		GiftAshWolf restored = new GiftAshWolf(); restored.restoreFromBundle(bundle);
		check(restored.friendship() == 70, "礼物居民好感没有随存档恢复");

		TownNpc ash = new TownNpc().configure(TownNpc.Spec.ASH_WOLF);
		TownNpc coconut = new TownNpc().configure(TownNpc.Spec.COCONUT);
		check(ash.SupercreateLoot() instanceof HaroEgg && ash.properties().contains(Char.Property.ORC),
				"城镇阿萨没有提供哈罗或兽人属性");
		check(coconut.SupercreateLoot() instanceof CocoCatEgg && coconut.properties().contains(Char.Property.MECH),
				"城镇椰子没有提供爆破之魂或机械属性");
		Bundle townBundle = new Bundle(); ash.storeInBundle(townBundle);
		TownNpc restoredTown = new TownNpc(); restoredTown.restoreFromBundle(townBundle);
		check(restoredTown.spec() == TownNpc.Spec.ASH_WOLF && restoredTown.properties().contains(Char.Property.ORC),
				"城镇NPC读档后丢失身份属性");
	}

	private static void testPetsAndEggs() throws Exception {
		Hero hero = hero(10); hero.petLevel = 10;
		Haro haro = new Haro(); PigPet pig = new PigPet(); CocoCat cat = new CocoCat(); Velocirooster bird = new Velocirooster();
		check(haro.HT == 120 && haro.defenseSkill == 10 && haro.attackSkill(null) == 30, "哈罗成长错误");
		check(pig.HT == 170 && pig.defenseSkill == 10 && pig.attackSkill(null) == 15, "像素猪成长错误");
		check(cat.HT == 220 && cat.defenseSkill == 13 && cat.attackSkill(null) == 15, "椰子猫成长错误");
		check(bird.HT == 150 && bird.defenseSkill == 20 && bird.attackSkill(null) == 20
				&& Math.abs(bird.speed() - 1.5f) < 0.001f, "公鸡成长或速度错误");
		check(hatch(new HaroEgg()) instanceof Haro && hatch(new PigpetEgg()) instanceof PigPet
				&& hatch(new CocoCatEgg()) instanceof CocoCat && hatch(new VelociroosterEgg()) instanceof Velocirooster,
				"四种礼物宠物蛋孵化类型错误");
		check(new HaroEgg().image == ItemSpriteSheet.HARO_EGG && new PigpetEgg().image == ItemSpriteSheet.PIG_PET_EGG
				&& new CocoCatEgg().image == ItemSpriteSheet.COCO_CAT_EGG
				&& new VelociroosterEgg().image == ItemSpriteSheet.VELOCIROOSTER_EGG,
				"四种礼物宠物蛋图标索引错误");
	}

	private static void testTentPopulation() {
		Dungeon.branch = 0;
		Dungeon.depth = 0;   //SPS: 0 层为带商店的特殊初始层
		TestLevel shop = paintTent();
		check(shop.mobs.size() == 1, "商店层帐篷没有生成一名居民");
		for (Mob mob : shop.mobs) check(mob instanceof GiftNpc, "商店层帐篷生成了错误居民");

		Dungeon.depth = 2;
		TestLevel ordinary = paintTent();
		check(ordinary.mobs.size() == 2, "普通层帐篷精英数量错误");
		HashSet<Integer> positions = new HashSet<>();
		for (Mob guard : ordinary.mobs) {
			check(guard instanceof SpsExitMobs.ExitGuard && guard.buff(ExProtect.class) != null,
					"普通层帐篷没有生成受精英保护的旧版守卫");
			check(positions.add(guard.pos), "普通层帐篷守卫位置重叠");
		}
	}

	private static void testAssets() throws Exception {
		checkFile("sprites/mobs/sps_haro.png", "286D2E0D2B70EF907187ACD532CB8AAD0B83A6FCA6A656CB07EABC23A61D9BFE");
		checkFile("sprites/pets/sps_pig.png", "2C91956431114BC4A36FC8815C841D718E4CC58FABDDA5688E48DABD8D9EBD4B");
		checkFile("sprites/mobs/sps_velocirooster.png", "D60CC7F9EF73570ABD9DEC5C77A6E499AE325D5566EAED80FA9A1DA6F57E7B08");
		checkFile("sprites/mobs/sps_gift_torch.png", "30752F818F74FD992271F4B0082CBF1EE7A50958D8768B6DB260B7197AE95DA6");
		checkFile("sprites/mobs/sps_gift_meat_seller.png", "51A71AF196254988ED299B0C6E9DC56FC4C6E25004A26E62004C249F95E29C84");
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		checkIcon(sheet, 0, "A4109B561FDBD5E8CD0EBF97C1A595255E36F67D47EBA1D8615A75093B412C09");
		checkIcon(sheet, 16, "20019781BC4F95CE2CAE7BCCA8F2687356220D1031819A966A1B75B365B4A0EB");
		checkIcon(sheet, 32, "02C51E3FDE5697311074823E0E8A9029BB79364B593CC185B732B5DE99346D18");
		checkIcon(sheet, 48, "71F147266C5CA5D7521EE346D74E049D43EEC77F8A9ADD3152EBC38D06580A83");
		checkIcon(sheet, 64, "78C7EFE54D09EF2892E44AAC922FB6F11F8F28DFBA8C0DE4B054058E9605A2D1");
		checkIcon(sheet, 80, "B8EBAB1F8E39E3412C4B47D12481C91E98046043FAA2481FD55DA042C5301890");
	}

	private static void testAnimationFrames() throws Exception {
		TextureFilm primitiveArrayFilm = new TextureFilm(32, 16, 16, 16);
		MovieClip.Animation primitiveArrayAnimation = new MovieClip.Animation(1, true)
				.frames(primitiveArrayFilm, new int[]{0, 1});
		check(primitiveArrayAnimation.frames.length == 2
				&& primitiveArrayAnimation.frames[0] != null
				&& primitiveArrayAnimation.frames[1] != null,
				"整数数组动画帧没有逐项解析");

		check(GiftNpc.Visual.values().length == 11, "礼物居民视觉定义数量错误");
		for (GiftNpc.Visual visual : GiftNpc.Visual.values()) {
			BufferedImage sheet = ImageIO.read(new File(visual.asset));
			check(sheet != null, "无法读取礼物居民动画：" + visual.asset);
			int columns = sheet.getWidth() / visual.frameWidth;
			int rows = sheet.getHeight() / visual.frameHeight;
			check(columns > 0 && rows > 0, "礼物居民动画切片尺寸错误：" + visual.asset);
			int frameCount = columns * rows;
			checkFrames(visual.asset, "idle", frameCount, visual.idleFrames);
			checkFrames(visual.asset, "run", frameCount, visual.runFrames);
			checkFrames(visual.asset, "attack", frameCount, visual.attackFrames);
			checkFrames(visual.asset, "die", frameCount, visual.dieFrames);
		}
	}

	private static void checkFrames(String asset, String animation, int frameCount, int[] frames) {
		check(frames.length > 0, "礼物居民动画没有帧：" + asset + " " + animation);
		for (int frame : frames) {
			check(frame >= 0 && frame < frameCount,
					"礼物居民动画帧越界：" + asset + " " + animation + " #" + frame + "/" + frameCount);
		}
	}

	private static Hero hero(int petLevel) {
		Actor.clear();
		Hero hero = new Hero(); Talent.initClassTalents(hero); hero.HP = hero.HT = 100; hero.petLevel = petLevel;
		Dungeon.hero = hero; return hero;
	}

	private static LegacyPet hatch(Egg egg) throws Exception {
		Method method = Egg.class.getDeclaredMethod("hatchling"); method.setAccessible(true);
		return (LegacyPet)method.invoke(egg);
	}

	private static TestLevel paintTent() {
		Random.pushGenerator(0x4754465454454E54L + Dungeon.depth);
		try {
			TestLevel level = new TestLevel(); Dungeon.level = level;
			SpsTentRoom room = new SpsTentRoom(); room.set(10, 10, 17, 17);
			EmptyRoom neighbour = new EmptyRoom(); neighbour.set(5, 10, 10, 17);
			Room.Door door = new Room.Door(10, 13);
			room.connected.put(neighbour, door); neighbour.connected.put(room, door);
			room.paint(level); return level;
		} finally { Random.popGenerator(); }
	}

	private static GiftNpc[] allResidents() {
		return new GiftNpc[]{new GiftRen(), new GiftAshWolf(), new GiftCoconut(), new GiftBegger(), new GiftAFly(),
				new GiftBaMech(), new GiftFruitWorker(), new GiftMeatSeller(), new GiftTorch(), new GiftFlyLing(),
				new GiftBunnyKeeper()};
	}

	private static boolean has(GiftNpc.GiftResult result, Class<?> type) { return count(result, type) > 0; }
	private static int count(GiftNpc.GiftResult result, Class<?> type) {
		int count = 0; if (result != null) for (Item item : result.items) if (type.isInstance(item)) count++; return count;
	}

	private static void checkFile(String path, String expected) throws Exception {
		check(expected.equals(hex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(new File(path).toPath())))),
				"原始动画文件不一致：" + path);
	}

	private static void checkIcon(BufferedImage sheet, int x0, String expected) throws Exception {
		ByteBuffer data = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = 896; y < 912; y++) for (int x = x0; x < x0 + 16; x++) data.putInt(sheet.getRGB(x, y));
		check(expected.equals(hex(MessageDigest.getInstance("SHA-256").digest(data.array()))), "礼物宠物图标不一致：" + x0);
	}

	private static String hex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(32, 32); mobs = new HashSet<>(); heaps = new SparseArray<>(); blobs = new HashMap<>();
			plants = new SparseArray<Plant>(); traps = new SparseArray<>(); transitions = new ArrayList<>();
			customTiles = new ArrayList<>(); customTerrain = new ArrayList<>(); customWalls = new ArrayList<>();
			Arrays.fill(map, Terrain.EMPTY); Arrays.fill(passable, true); buildFlagMaps();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
	private SpsGiftNpcTest() { }
}
