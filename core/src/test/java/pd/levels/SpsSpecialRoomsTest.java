package pd.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Badges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.blobs.Alchemy;
import pd.actors.blobs.Foliage;
import pd.actors.blobs.MemoryFire;
import pd.actors.blobs.WaterOfAwareness;
import pd.actors.blobs.WaterOfHealth;
import pd.actors.blobs.WaterOfTransmutation;
import pd.actors.blobs.WellWater;
import pd.actors.blobs.weather.WeatherOfDead;
import pd.actors.buffs.ExProtect;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.Talent;
import pd.actors.mobs.ArmorStatue;
import pd.actors.mobs.Greatmoss;
import pd.actors.mobs.Mimic;
import pd.actors.mobs.Piranha;
import pd.actors.mobs.Statue;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.Ankh;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Honeypot;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.armor.Armor;
import pd.items.armor.normalarmor.NormalArmor;
import pd.items.artifacts.Artifact;
import pd.items.bags.ShoppingCart;
import pd.items.bombs.Bomb;
import pd.items.bombs.DungeonBomb;
import pd.items.food.BugMeat;
import pd.items.food.SmallRation;
import pd.items.keys.GoldenKey;
import pd.items.keys.IronKey;
import pd.items.nornstone.NornStone;
import pd.items.potions.Potion;
import pd.items.potions.PotionOfInvisibility;
import pd.items.potions.PotionOfLiquidFlame;
import pd.items.potions.brews.CausticBrew;
import pd.items.rings.Ring;
import pd.items.scrolls.Scroll;
import pd.items.scrolls.ScrollOfTeleportation;
import pd.items.wands.Wand;
import pd.items.weapon.Weapon;
import pd.items.weapon.missiles.buildblock.DoorBlock;
import pd.items.weapon.missiles.buildblock.PlantPotBlock;
import pd.levels.rooms.Room;
import pd.levels.rooms.special.CryptRoom;
import pd.levels.rooms.special.GardenRoom;
import pd.levels.rooms.special.LibraryRoom;
import pd.levels.rooms.special.PoolRoom;
import pd.levels.rooms.special.SpecialRoom;
import pd.levels.rooms.special.SpsBarricadedRoom;
import pd.levels.rooms.special.SpsCookingRoom;
import pd.levels.rooms.special.SpsGlassRoom;
import pd.levels.rooms.special.SpsJungleRoom;
import pd.levels.rooms.special.SpsMagicWellRoom;
import pd.levels.rooms.special.SpsMaterialRoom;
import pd.levels.rooms.special.SpsMemoryRoom;
import pd.levels.rooms.special.SpsPitRoom;
import pd.levels.rooms.special.SpsRuinRoom;
import pd.levels.rooms.special.SpsTentRoom;
import pd.levels.rooms.special.SpsWishPoolRoom;
import pd.levels.rooms.special.StatueRoom;
import pd.levels.rooms.special.StorageRoom;
import pd.levels.rooms.special.TreasuryRoom;
import pd.levels.rooms.standard.EmptyRoom;
import pd.plants.BlandfruitBush;
import pd.plants.Fadeleaf;
import pd.plants.Plant;
import pd.plants.ReNepenth;
import pd.plants.Seedpod;
import pd.plants.StarEater;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** Behavioral checks for the restored SPS special-room painters and utility items. */
public final class SpsSpecialRoomsTest {

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Dungeon.depth = 7;
		Dungeon.branch = 0;
		Dungeon.hero = new Hero();
		Talent.initClassTalents(Dungeon.hero);
		Scroll.initLabels();
		Potion.initColors();
		Ring.initGems();
		Generator.fullReset();

		testMaterialRoom();
		testCookingRoom();
		testJungleRoom();
		testStatueRoom();
		testPoolRoom();
		testCryptRoom();
		testGardenRoom();
		testLibraryRoom();
		testStorageRoom();
		testTreasuryRoom();
		testTentRoom();
		testRuinRoom();
		testWishPoolRoom();
		testMemoryRoom();
		testPitRoom();
		testBarricadedRoom();
		testMagicWellRoom();
		testGlassRoom();
		testOldHighGrass();
		testBadgeFreeCatalogValidation();
		testUtilityItems();

		System.out.println("SPS特殊房测试通过：材料、厨房、丛林、双石像、水池、墓室、花园、图书馆、储藏室、宝库、帐篷、废墟、许愿池、记忆房、陷坑、路障房、魔法井、玻璃房、旧式高草及建筑物品均正常。");
	}

	private static void testMaterialRoom() {
		RoomFixture f = paint(new SpsMaterialRoom(), 1);
		check(f.door.type == Room.Door.Type.LOCKED, "材料房没有上锁");
		check(countTerrain(f.level, Terrain.IRON_MAKER) == 1, "材料房铁砧数量错误");
		check(countItems(f.level, DoorBlock.class) == 3, "材料房门方块数量错误");
		int bombs = countItems(f.level, Bomb.class);
		check(bombs >= 2 && bombs <= 3, "材料房炸弹数量错误");
		check(f.level.findPrizeItem(IronKey.class) != null, "材料房没有生成铁钥匙");
	}

	private static void testCookingRoom() {
		Dungeon.LimitedDrops.reset();
		RoomFixture f = paint(new SpsCookingRoom(), 2);
		check(f.door.type == Room.Door.Type.LOCKED, "厨房没有上锁");
		check(countTerrain(f.level, Terrain.ALCHEMY) == 1, "厨房炼金锅数量错误");
		check(f.level.blobs.get(Alchemy.class) != null, "厨房炼金能量缺失");
		check(countItems(f.level, ShoppingCart.class) == 1, "厨房购物车数量错误");
		int heaps = f.level.heaps.valueList().size();
		check(heaps >= 4 && heaps <= 6, "厨房食物堆数量错误");
		check(f.level.findPrizeItem(IronKey.class) != null, "厨房没有生成铁钥匙");
	}

	private static void testJungleRoom() {
		RoomFixture f = paint(new SpsJungleRoom(), 3);
		check(f.door.type == Room.Door.Type.LOCKED, "丛林房没有上锁");
		check(f.level.plants.valueList().size() == 3, "丛林房植物数量错误");
		check(countItems(f.level, Honeypot.class) == 1, "丛林房蜂蜜罐缺失");
		check(countItems(f.level, PlantPotBlock.class) == 1, "丛林房种植盆缺失");
		int moss = 0;
		for (pd.actors.mobs.Mob mob : f.level.mobs()) {
			if (mob instanceof Greatmoss) moss++;
		}
		check(moss == 3, "丛林房苔藓数量错误");
		check(countTerrain(f.level, Terrain.HIGH_GRASS) > 0, "丛林房没有高草");
		check(f.level.findPrizeItem(IronKey.class) != null, "丛林房没有生成铁钥匙");
	}

	private static void testStatueRoom() {
		RoomFixture f = paint(new StatueRoom(), 31);
		check(f.door.type == Room.Door.Type.LOCKED, "雕像房没有上锁");
		check(f.level.findPrizeItem(IronKey.class) != null, "雕像房没有生成铁钥匙");
		int weaponStatues = 0;
		int armorStatues = 0;
		ArmorStatue armorStatue = null;
		for (pd.actors.mobs.Mob mob : f.level.mobs()) {
			if (mob.getClass() == Statue.class) weaponStatues++;
			if (mob instanceof ArmorStatue) {
				armorStatues++;
				armorStatue = (ArmorStatue)mob;
			}
		}
		check(weaponStatues == 1 && armorStatues == 1 && f.level.mobs().size() == 2,
				"雕像房没有固定生成一只武器石像和一只护甲石像");

		int prizes = 0;
		for (Heap heap : f.level.heaps.valueList()) {
			for (Item item : heap.items) {
				check(item instanceof Armor || item instanceof Weapon, "雕像房生成了武器护甲以外的奖励");
				prizes++;
			}
		}
		check(prizes >= 2 && prizes <= 3, "雕像房地面奖励不是2至3件");
		check(countTerrain(f.level, Terrain.STATUE) == 6, "雕像房装饰雕像墙数量错误");

		check(armorStatue != null && armorStatue.HT == 15 + Dungeon.depth * 5
				&& armorStatue.EXP == 50 + Dungeon.depth * 2, "护甲石像生命或经验错误");
		check(armorStatue.attackSkill(Dungeon.hero) == 9 + Dungeon.depth * 3,
				"护甲石像命中错误");
		check(armorStatue.armor() instanceof NormalArmor && armorStatue.armor().isIdentified()
				&& armorStatue.armor().glyph != null, "护甲石像没有生成已鉴定刻印普通护甲");
		int expectedDefense = Math.round(((NormalArmor)armorStatue.armor()).DEX * (4 + Dungeon.depth));
		check(armorStatue.defenseSkill(Dungeon.hero) == expectedDefense, "护甲石像闪避没有使用护甲敏捷");
		for (int i = 0; i < 50; i++) {
			int damage = armorStatue.damageRoll();
			check(damage >= Dungeon.depth && damage <= Dungeon.depth * 2, "护甲石像伤害超出旧版范围");
		}

		Bundle bundle = new Bundle();
		armorStatue.storeInBundle(bundle);
		ArmorStatue restored = new ArmorStatue();
		restored.restoreFromBundle(bundle);
		check(restored.armor() != null && restored.armor().getClass() == armorStatue.armor().getClass()
				&& restored.armor().glyph != null, "护甲石像存档没有恢复护甲和刻印");

		try {
			String en = java.nio.file.Files.readString(Path.of("messages", "actors", "en", "actors.properties"), StandardCharsets.UTF_8);
			String zh = java.nio.file.Files.readString(Path.of("messages", "actors", "zh", "actors.properties"), StandardCharsets.UTF_8);
			check(en.contains("actors.mobs.armorstatue.name=") && en.contains("actors.mobs.armorstatue.desc="),
					"护甲石像缺少英文资源");
			check(zh.contains("actors.mobs.armorstatue.name=活化装甲石像") && !zh.contains("�"),
					"护甲石像中文资源缺失或乱码");
		} catch (java.io.IOException error) {
			throw new AssertionError("无法读取护甲石像双语资源", error);
		}
	}

	private static void testRuinRoom() {
		Dungeon.LimitedDrops.reset();
		RoomFixture f = paint(new SpsRuinRoom(), 4);
		check(f.door.type == Room.Door.Type.LOCKED, "废墟房没有上锁");
		check(countItems(f.level, ShoppingCart.class) == 1, "废墟房购物车数量错误");
		check(countHeapType(f.level, Heap.Type.CHEST) == 1, "废墟房宝箱数量错误");
		check(countHeapType(f.level, Heap.Type.M_WEB) == 1, "废墟房蛛网堆数量错误");
		check(countHeapType(f.level, Heap.Type.REMAINS) == 1, "废墟房遗骸数量错误");
		check(countHeapType(f.level, Heap.Type.E_DUST) == 1, "废墟房尘土堆数量错误");
		check(countTerrain(f.level, Terrain.IRON_MAKER) == 1, "废墟房铁砧数量错误");
		check(f.level.findPrizeItem(IronKey.class) != null, "废墟房没有生成铁钥匙");
	}

	private static void testPoolRoom() {
		RoomFixture f = paint(new PoolRoom(), 32);
		check(f.door.type == Room.Door.Type.REGULAR, "水池房门类型错误");
		check(countTerrain(f.level, Terrain.WATER) == 35, "水池房没有保持旧版全水内室");
		check(countTerrain(f.level, Terrain.EMPTY_SP) == 0, "水池房错误保留了现代安全通道");
		check(countTerrain(f.level, Terrain.PEDESTAL) == 1, "水池房奖励基座数量错误");
		check(f.level.heaps.valueList().size() == 1, "水池房奖励堆数量错误");
		Heap reward = f.level.heaps.valueList().get(0);
		check(reward.type == Heap.Type.CHEST || reward.type == Heap.Type.HEAP, "水池房奖励堆类型错误");
		check(reward.items.size() == 1 && (reward.peek() instanceof Armor || reward.peek() instanceof Weapon),
				"水池房奖励没有使用旧版武器护甲池");
		int piranhas = 0;
		for (pd.actors.mobs.Mob mob : f.level.mobs()) {
			if (mob.getClass() == Piranha.class) piranhas++;
			check(f.level.map[mob.pos] == Terrain.WATER, "水池房食人鱼没有落在水中");
		}
		check(piranhas == 4 && f.level.mobs().size() == 4, "水池房没有固定生成四条普通食人鱼");
		check(f.level.findPrizeItem(PotionOfInvisibility.class) != null, "水池房没有安排隐形药剂");
	}

	private static void testCryptRoom() {
		boolean sawWeather = false;
		boolean sawMimic = false;
		boolean sawLooseGold = false;
		for (int attempt = 0; attempt < 20; attempt++) {
			RoomFixture f = paint(new CryptRoom(), 100 + attempt);
			check(f.door.type == Room.Door.Type.LOCKED, "墓室没有上锁");
			check(f.level.findPrizeItem(IronKey.class) != null, "墓室没有生成铁钥匙");
			check(countTerrain(f.level, Terrain.STATUE) == 2, "墓室装饰雕像数量错误");
			check(countHeapType(f.level, Heap.Type.TOMB) == 1, "墓室中央坟墓数量错误");
			Heap tomb = null;
			for (Heap heap : f.level.heaps.valueList()) if (heap.type == Heap.Type.TOMB) tomb = heap;
			check(tomb != null && tomb.items.size() == 1 && tomb.peek() instanceof Armor,
					"墓室坟墓没有生成旧版护甲奖励");

			int gold = countItems(f.level, pd.items.Gold.class);
			int mimics = countHeapType(f.level, Heap.Type.MIMIC);
			int loose = countHeapType(f.level, Heap.Type.HEAP);
			if (mimics == 1) {
				sawMimic = true;
				check(gold >= 2 && gold <= 3 && countHeapType(f.level, Heap.Type.CHEST) == gold - 1,
						"墓室宝箱分支的金币堆或拟态箱数量错误");
				Heap mimic = null;
				for (Heap heap : f.level.heaps.valueList()) if (heap.type == Heap.Type.MIMIC) mimic = heap;
				check(mimic != null && mimic.peek() instanceof pd.items.Gold,
						"墓室拟态箱没有保留金币奖励");
				Bundle saved = new Bundle();
				mimic.storeInBundle(saved);
				Heap restored = new Heap();
				restored.restoreFromBundle(saved);
				check(restored.type == Heap.Type.MIMIC && restored.peek() instanceof pd.items.Gold,
						"墓室拟态箱存档丢失类型或金币");
				int mimicCell = mimic.pos;
				mimic.open(Dungeon.hero);
				Mimic awakened = null;
				for (pd.actors.mobs.Mob mob : f.level.mobs()) {
					if (mob.getClass() == Mimic.class) awakened = (Mimic)mob;
				}
				check(f.level.heaps.get(mimicCell) == null && awakened != null
						&& awakened.items != null && awakened.items.size() == 1
						&& awakened.items.get(0) instanceof pd.items.Gold,
						"打开墓室拟态箱没有生成携带金币的宝箱怪");
			} else {
				sawLooseGold = true;
				check(mimics == 0 && gold >= 4 && gold <= 6 && loose == gold,
						"墓室散落金币分支数量错误");
			}

			WeatherOfDead weather = (WeatherOfDead)f.level.blobs.get(WeatherOfDead.class);
			if (weather != null) {
				sawWeather = true;
				check(weather.volume == 36, "墓室死亡天气没有覆盖整个内室");
				for (int y = 11; y < 17; y++) {
					for (int x = 11; x < 17; x++) {
						check(weather.cur[x + y * f.level.width()] == 1, "墓室死亡天气覆盖不完整");
					}
				}
			}
		}
		check(sawWeather, "墓室死亡天气分支没有恢复");
		check(sawMimic && sawLooseGold, "墓室金币奖励的两种旧版分支没有恢复");
	}

	private static void testGardenRoom() {
		RoomFixture f = paint(new GardenRoom(), 140);
		check(f.door.type == Room.Door.Type.REGULAR, "花园房被错误上锁");
		check(countTerrain(f.level, Terrain.WATER) == 16, "花园房中央水池尺寸错误");
		check(countItems(f.level, Honeypot.class) == 1, "花园房缺少深度25层前必出的蜂蜜罐");
		check(f.level.findPrizeItem(IronKey.class) == null, "花园房错误生成铁钥匙");
		Foliage foliage = (Foliage)f.level.blobs.get(Foliage.class);
		check(foliage != null && foliage.volume == 36, "花园房植被环境没有覆盖整个内室");
		check(f.level.plants.valueList().size() <= 2, "花园房植物数量超出旧版范围");
		for (Plant plant : f.level.plants.valueList()) {
			check(plant instanceof Seedpod || plant instanceof BlandfruitBush, "花园房生成了旧版以外的植物");
		}
	}

	private static void testLibraryRoom() {
		RoomFixture f = paint(new LibraryRoom(), 141);
		check(f.door.type == Room.Door.Type.LOCKED, "图书馆没有上锁");
		check(f.level.findPrizeItem(IronKey.class) != null, "图书馆没有生成铁钥匙");
		int bookshelves = countTerrain(f.level, Terrain.BOOKSHELF);
		check(bookshelves == 5 || bookshelves == 6, "图书馆书架或炼金角布局错误");
		check(countTerrain(f.level, Terrain.ALCHEMY) == 1, "图书馆缺少炼金锅");
		Alchemy alchemy = (Alchemy)f.level.blobs.get(Alchemy.class);
		check(alchemy != null && alchemy.volume == 1, "图书馆炼金能量缺失");
		int scrolls = countItems(f.level, Scroll.class);
		int potions = countItems(f.level, Potion.class);
		check(scrolls == potions && (scrolls == 2 || scrolls == 3), "图书馆卷轴和药剂奖励数量错误");
	}

	private static void testStorageRoom() {
		RoomFixture f = paint(new StorageRoom(), 142);
		check(f.door.type == Room.Door.Type.REGULAR, "储藏室错误使用了现代路障门");
		check(countTerrain(f.level, Terrain.BOOKSHELF) == 6, "储藏室书架隔断尺寸错误");
		int skeletons = countHeapType(f.level, Heap.Type.SKELETON);
		check(skeletons >= 2 && skeletons <= 4, "储藏室骨堆数量错误");
		check(countHeapType(f.level, Heap.Type.CHEST) == 1, "储藏室宝箱数量错误");
		check(f.level.heaps.valueList().size() == skeletons + 1, "储藏室出现奖励重叠或额外物品堆");
		check(f.level.findPrizeItem(PotionOfLiquidFlame.class) != null, "储藏室没有安排液火药剂");
	}

	private static void testTreasuryRoom() {
		RoomFixture f = paint(new TreasuryRoom(), 143);
		check(f.door.type == Room.Door.Type.LOCKED, "宝库没有上锁");
		check(f.level.findPrizeItem(IronKey.class) != null, "宝库没有生成铁钥匙");
		check(f.level.findPrizeItem(GoldenKey.class) != null, "宝库没有生成旧版黄金钥匙");
		check(countTerrain(f.level, Terrain.EMPTY) == 16 && countTerrain(f.level, Terrain.EMPTY_SP) == 20,
				"宝库双层地面布局错误");
		check(countHeapType(f.level, Heap.Type.CRYSTAL_CHEST) == 3, "宝库没有生成三个水晶箱");
		check(f.level.mobs().isEmpty(), "宝库错误生成现代拟态怪");
		for (Heap heap : f.level.heaps.valueList()) {
			check(heap.items.size() == 1, "宝库箱内奖励数量错误");
			Item item = heap.peek();
			check(item instanceof Wand || item instanceof Ring || item instanceof Artifact,
					"宝库奖励没有使用旧版法杖、戒指、神器池");
			check(!item.cursed || !item.isUpgradable(), "宝库生成了可净化却仍带诅咒的奖励");
		}
	}

	private static void testTentRoom() {
		RoomFixture f = paint(new SpsTentRoom(), 144);
		check(f.door.type == Room.Door.Type.REGULAR, "帐篷房门类型错误");
		check(countTerrain(f.level, Terrain.FLOWER_POT) == 1, "帐篷房花盆地形在种植时丢失");
		check(countTerrain(f.level, Terrain.ALCHEMY) == 1, "帐篷房炼金锅缺失");
		check(countTerrain(f.level, Terrain.IRON_MAKER) == 1, "帐篷房铁砧缺失");
		check(countTerrain(f.level, Terrain.TENT) == 1, "帐篷房休息点缺失");
		check(countTerrain(f.level, Terrain.STATUE_SP) == 1, "帐篷房中心雕像缺失");
		check(f.level.plants.valueList().size() == 1, "帐篷房花盆没有强化植物");
		check(f.level.mobs().size() == 2, "非商店层帐篷房守卫数量错误");
		HashSet<Integer> guardCells = new HashSet<>();
		for (pd.actors.mobs.Mob guard : f.level.mobs()) {
			check(guard.buff(ExProtect.class) != null, "非商店层帐篷守卫缺少额外保护");
			check(guardCells.add(guard.pos), "非商店层帐篷守卫位置重叠");
		}
	}

	private static void testWishPoolRoom() {
		RoomFixture f = paint(new SpsWishPoolRoom(), 5);
		check(f.door.type == Room.Door.Type.HIDDEN, "许愿池门没有隐藏");
		check(countTerrain(f.level, Terrain.WATER) == 8, "许愿池水环不是八格");
		check(countTerrain(f.level, Terrain.STATUE) == 1, "许愿池中心雕像错误");
		check(f.level.heaps.valueList().size() == 9, "许愿池奖励数量错误");
		for (int cell = 0; cell < f.level.length(); cell++) {
			if (f.level.map[cell] == Terrain.WATER) check(f.level.heaps.get(cell) != null, "水环缺少露珠");
		}
		check(f.level.mobs().size() == 1 && f.level.mobs().iterator().next() instanceof TownNpc
				&& ((TownNpc)f.level.mobs().iterator().next()).spec() == TownNpc.Spec.HMDZL001,
				"许愿池作者NPC错误");
		checkRoomWalls(f.level, "许愿池覆盖了房间外墙");
	}

	private static void testMemoryRoom() {
		RoomFixture f = paint(new SpsMemoryRoom(), 6);
		check(f.door.type == Room.Door.Type.HIDDEN, "记忆房门没有隐藏");
		check(countTerrain(f.level, Terrain.PEDESTAL) == 1, "记忆火焰基座错误");
		check(countTerrain(f.level, Terrain.EMBERS) == 8, "记忆火焰余烬区域错误");
		MemoryFire fire = (MemoryFire)f.level.blobs.get(MemoryFire.class);
		check(fire != null && fire.volume == 40, "记忆火焰强度错误");
	}

	private static void testPitRoom() {
		RoomFixture f = paint(new SpsPitRoom(), 7);
		check(f.door.type == Room.Door.Type.REGULAR, "陷坑房适配门类型错误");
		check(countTerrain(f.level, Terrain.EMPTY_WELL) == 1, "陷坑房枯井数量错误");
		check(f.level.pitSign >= 0 && f.level.map[f.level.pitSign] == Terrain.SIGN,
				"陷坑房专用提示牌缺失");
		int well = -1;
		for (int cell = 0; cell < f.level.length(); cell++) {
			if (f.level.map[cell] == Terrain.EMPTY_WELL) well = cell;
		}
		check(well % f.level.width() == 16 && (well / f.level.width() == 11
				|| well / f.level.width() == 16), "陷坑房枯井不在入口远角");
		check(f.level.heaps.valueList().size() == 1, "陷坑房奖励没有集中在一个骨堆");
		Heap remains = f.level.heaps.valueList().get(0);
		check(remains.type == Heap.Type.SKELETON, "陷坑房遗骸不是骨堆");
		check(countItems(f.level, ScrollOfTeleportation.class) >= 1, "陷坑房缺少传送卷轴");
		check(countItems(f.level, Ankh.class) == 1, "陷坑房缺少十字章");
		int fadeleafSeeds = 0;
		int equipment = 0;
		for (Item item : remains.items) {
			if (item instanceof Fadeleaf.Seed) fadeleafSeeds += item.quantity();
			if (item instanceof Ring || item instanceof Artifact
					|| item instanceof Weapon || item instanceof Armor) equipment++;
		}
		check(fadeleafSeeds == 2, "陷坑房遁隐草种子数量错误");
		check(equipment >= 1, "陷坑房缺少戒指、神器或武器护甲奖励");

		Bundle saved = new Bundle();
		f.level.storeInBundle(saved);
		check(saved.getInt("pit_sign") == f.level.pitSign, "陷坑提示牌位置没有写入存档");
		try {
			String en = java.nio.file.Files.readString(Path.of("messages", "levels", "en", "levels.properties"), StandardCharsets.UTF_8);
			String zh = java.nio.file.Files.readString(Path.of("messages", "levels", "zh", "levels.properties"), StandardCharsets.UTF_8);
			check(en.contains("levels.features.sign.pit_message=Note to self:"), "陷坑提示牌缺少英文文案");
			check(zh.contains("levels.features.sign.pit_message=这块地方没有出口") && !zh.contains("�"),
					"陷坑提示牌中文文案缺失或乱码");
		} catch (java.io.IOException error) {
			throw new AssertionError("无法读取陷坑提示牌双语资源", error);
		}
	}

	private static void testBarricadedRoom() {
		RoomFixture f = paint(new SpsBarricadedRoom(), 8);
		check(f.door.type == Room.Door.Type.HIDDEN, "路障房门没有隐藏");
		check(countTerrain(f.level, Terrain.BOOKSHELF) == 12, "路障房书架长度存在坐标偏差");
		check(countTerrain(f.level, Terrain.EMPTY_SP) == 24, "路障房可行走区域尺寸错误");
		int caches = countHeapType(f.level, Heap.Type.SKELETON);
		check(caches >= 2 && caches <= 3, "路障房骨堆数量错误");
		for (Heap heap : f.level.heaps.valueList()) {
			check(!heap.items.isEmpty() && heap.items.get(0) != null, "路障房生成了空奖励");
		}
	}

	private static void testMagicWellRoom() {
		RoomFixture f = paint(new SpsMagicWellRoom(), 9);
		check(f.door.type == Room.Door.Type.HIDDEN, "魔法井房门没有隐藏");
		check(countTerrain(f.level, Terrain.WELL) == 1, "魔法井房水井数量错误");
		check(f.level.plants.valueList().size() == 1, "魔法井房特殊植物数量错误");
		Plant plant = f.level.plants.valueList().get(0);
		check(plant instanceof StarEater || plant instanceof BlandfruitBush || plant instanceof ReNepenth,
				"魔法井房植物不在旧版三种植物池中");
		int waters = 0;
		for (Class<? extends WellWater> type : Arrays.asList(WaterOfAwareness.class,
				WaterOfHealth.class, WaterOfTransmutation.class)) {
			WellWater water = (WellWater) f.level.blobs.get(type);
			if (water != null) {
				waters++;
				check(water.volume == 1, "魔法井房井水强度错误");
			}
		}
		check(waters == 1, "魔法井房没有生成唯一的旧版井水");
		check(f.level.findPrizeItem(IronKey.class) == null, "魔法井房错误生成了现代铁钥匙");
	}

	private static void testGlassRoom() {
		RoomFixture f = paint(new SpsGlassRoom(), 10);
		check(f.door.type == Room.Door.Type.HIDDEN, "玻璃房门没有隐藏");
		int center = 13 + 13 * f.level.width();
		check(f.level.map[center] == Terrain.PEDESTAL, "玻璃房奖励没有使用旧版固定中心");
		check(countTerrain(f.level, Terrain.GLASS_WALL) == 20, "玻璃房玻璃墙尺寸错误");
		check(countItems(f.level, NornStone.class) == 1, "玻璃房中心诺恩石数量错误");
		int ore = countItems(f.level, StoneOre.class);
		check(ore >= 2 && ore <= 3, "玻璃房原石数量错误");
		check(f.level.findPrizeItem(DungeonBomb.DoubleBomb.class) != null,
				"玻璃房没有加入双重地牢炸弹待生成奖励");
	}

	private static void testUtilityItems() {
		ShoppingCart cart = new ShoppingCart();
		check(cart.capacity() == 34, "购物车容量错误");
		check(cart.canHold(new SmallRation()), "购物车不能装食物");
		check(cart.canHold(new CausticBrew()), "购物车不能装酿造物");
		check(!cart.canHold(new BugMeat()), "购物车错误接收虫肉");

		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Dungeon.hero.pos = 5 + 5 * level.width();
		Arrays.fill(level.map, Terrain.EMPTY);
		level.buildFlagMaps();
		int doorCell = 7 + 7 * level.width();
		new ExposedDoorBlock().build(doorCell);
		check(level.map[doorCell] == Terrain.DOOR, "门方块没有建造门");
		int potCell = 8 + 7 * level.width();
		new ExposedPlantPotBlock().build(potCell);
		check(level.map[potCell] == Terrain.FLOWER_POT, "种植盆方块没有建造花盆");
	}

	private static void testOldHighGrass() {
		Actor.clear();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Hero hero = new Hero();
		hero.subClass = HeroSubClass.WARDEN;
		hero.pos = 6 + 6 * level.width();
		Dungeon.hero = hero;
		Actor.add(hero);
		level.map[hero.pos] = Terrain.OLD_HIGH_GRASS;
		level.buildFlagMaps();

		level.pressCell(hero.pos);
		Invisibility invisibility = hero.buff(Invisibility.class);
		check(invisibility != null && invisibility.cooldown() == 4f,
				"旧式高草没有给予守望者4回合隐形");
		check(level.map[hero.pos] == Terrain.OLD_HIGH_GRASS, "旧式高草在踩踏后被错误移除");
		Actor.clear();
	}

	private static void testBadgeFreeCatalogValidation() {
		try {
			Field global = Badges.class.getDeclaredField("global");
			global.setAccessible(true);
			Object previous = global.get(null);
			global.set(null, null);
			Badges.validateCatalogBadges();
			global.set(null, previous);
		} catch (ReflectiveOperationException error) {
			throw new AssertionError("无法验证未加载徽章时的目录更新", error);
		}
	}

	private static RoomFixture paint(SpecialRoom room, long seed) {
		Random.pushGenerator(0x535053524F4F4D00L + seed);
		try {
			TestLevel level = new TestLevel();
			Dungeon.level = level;
			room.set(10, 10, 17, 17);
			EmptyRoom neighbour = new EmptyRoom();
			neighbour.set(5, 10, 10, 17);
			Room.Door door = new Room.Door(10, 13);
			room.connected.put(neighbour, door);
			neighbour.connected.put(room, door);
			room.paint(level);
			return new RoomFixture(level, door);
		} finally {
			Random.popGenerator();
		}
	}

	private static int countTerrain(Level level, int terrain) {
		int result = 0;
		for (int value : level.map) if (value == terrain) result++;
		return result;
	}

	private static int countItems(Level level, Class<?> type) {
		int result = 0;
		for (Heap heap : level.heaps.valueList()) {
			for (Item item : heap.items) if (type.isInstance(item)) result++;
		}
		return result;
	}

	private static int countHeapType(Level level, Heap.Type type) {
		int result = 0;
		for (Heap heap : level.heaps.valueList()) if (heap.type == type) result++;
		return result;
	}

	private static void checkRoomWalls(Level level, String message) {
		for (int x = 10; x <= 17; x++) {
			check(level.map[x + 10 * level.width()] == Terrain.WALL, message);
			check(level.map[x + 17 * level.width()] == Terrain.WALL, message);
		}
		for (int y = 10; y <= 17; y++) {
			check(level.map[10 + y * level.width()] == Terrain.WALL, message);
			check(level.map[17 + y * level.width()] == Terrain.WALL, message);
		}
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class RoomFixture {
		final TestLevel level;
		final Room.Door door;
		RoomFixture(TestLevel level, Room.Door door) { this.level = level; this.door = door; }
	}

	private static final class ExposedDoorBlock extends DoorBlock {
		void build(int cell) { onThrow(cell); }
	}

	private static final class ExposedPlantPotBlock extends PlantPotBlock {
		void build(int cell) { onThrow(cell); }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(32, 32);
			mobs().clear();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
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
	}

	private SpsSpecialRoomsTest() { }
}
