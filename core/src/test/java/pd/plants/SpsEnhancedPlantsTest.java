package pd.plants;

import pd.actors.buffs.Healing;

import pd.actors.buffs.Paralysis;

import pd.actors.buffs.Cold;

import pd.atlas.items.ConsumPotionSeedSeedDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.DarkGas;
import pd.actors.blobs.HealLight;
import pd.actors.blobs.ParalyticGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.blobs.Web;
import pd.actors.blobs.damageblobs.EarthEffectDamage;
import pd.actors.blobs.damageblobs.FireEffectDamage;
import pd.actors.blobs.damageblobs.IceEffectDamage;
import pd.actors.blobs.damageblobs.ShockEffectDamage;
import pd.actors.blobs.effectblobs.ElectriShock;
import pd.actors.blobs.effectblobs.Fire;
import pd.actors.blobs.effectblobs.FrostCloud;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Charm;
import pd.actors.buffs.FrostIce;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Roots;
import pd.actors.buffs.ShadowCurse;
import pd.actors.buffs.Shocked;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.TransmutationBall;
import pd.items.consum.food.vegetable.StarEaterFlower;
import pd.items.consum.food.Blandfruit;
import pd.items.consum.food.fruit.Durian;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.vegetable.Vegetable;
import pd.items.nornstone.NornStone;
import pd.items.quest.AdventureJournal;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.items.equipment.weapon.missiles.arrows.BlindFruit;
import pd.items.equipment.weapon.missiles.arrows.CharmFruit;
import pd.items.equipment.weapon.missiles.arrows.FireFruit;
import pd.items.equipment.weapon.missiles.arrows.GlassFruit;
import pd.items.equipment.weapon.missiles.arrows.HealFruit;
import pd.items.equipment.weapon.missiles.arrows.IceFruit;
import pd.items.equipment.weapon.missiles.arrows.LargeHealFruit;
import pd.items.equipment.weapon.missiles.arrows.NutFruit;
import pd.items.equipment.weapon.missiles.arrows.RotFruit;
import pd.items.equipment.weapon.missiles.arrows.StarEaterFruit;
import pd.items.equipment.weapon.missiles.arrows.StarFruit;
import pd.items.equipment.weapon.missiles.arrows.RootFruit;
import pd.items.equipment.weapon.missiles.arrows.ShockFruit;
import pd.items.equipment.weapon.missiles.arrows.SmokeFruit;
import pd.items.equipment.weapon.missiles.arrows.ToxicFruit;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.features.LevelTransition;
import pd.levels.traps.Trap;
import pd.mechanics.pathfind.PathFinder;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import javax.imageio.ImageIO;
import pd.atlas.IconEntry;

/** Regression coverage for SPS-PD's entrance-room enhanced plants and fruit missiles. */
public final class SpsEnhancedPlantsTest {

	private static final Class<?>[] PLANTS = {
			Firebloom.class, Icecap.class, Sorrowmoss.class, Blindweed.class, Sungrass.class,
			Earthroot.class, Fadeleaf.class, Rotberry.class, BlandfruitBush.class, Dreamfoil.class,
			Stormvine.class, NutPlant.class, Starflower.class, ReNepenth.class, StarEater.class,
			Dewcatcher.class, Seedpod.class, Freshberry.class, SiOtwoFlower.class, Swiftthistle.class
	};
	private static final Class<?>[] ENHANCED = {
			Firebloom.ExFirebloom.class, Icecap.ExIcecap.class, Sorrowmoss.ExSorrowmoss.class,
			Blindweed.ExBlindweed.class, Sungrass.ExSungrass.class, Earthroot.ExEarthroot.class,
			Fadeleaf.ExFadeleaf.class, Rotberry.ExRotberry.class, BlandfruitBush.ExBlandfruitBush.class,
			Dreamfoil.ExDreamfoil.class, Stormvine.ExStormvine.class, NutPlant.ExNutPlant.class,
			Starflower.ExStarflower.class, ReNepenth.ExReNepenth.class, StarEater.ExStarEater.class,
			Dewcatcher.ExDewcatcher.class, Seedpod.ExSeedpod.class, Freshberry.ExFreshberry.class,
			SiOtwoFlower.ExSiOtwoFlower.class, Swiftthistle.ExSwiftthistle.class
	};
	private static final int[] PLANT_IMAGES = {
			0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 9, 17, 11, 14, 15, 12, 13, 7, 18, 2
	};
	private static final IconEntry[] SEED_IMAGES = {
			ConsumPotionSeedSeedDict.SEED_FIREBLOOM, ConsumPotionSeedSeedDict.SEED_ICECAP,
			ConsumPotionSeedSeedDict.SEED_SORROWMOSS_0, ConsumPotionSeedSeedDict.SEED_BLINDWEED_0,
			ConsumPotionSeedSeedDict.SEED_SUNGRASS, ConsumPotionSeedSeedDict.SEED_EARTHROOT_0,
			ConsumPotionSeedSeedDict.SEED_FADELEAF_0, ConsumPotionSeedSeedDict.SEED_ROT_BERRY,
			ConsumPotionSeedSeedDict.SEED_BLANDFRUIT, ConsumPotionSeedSeedDict.SEED_MAGEROYAL_0,
			ConsumPotionSeedSeedDict.SEED_STORMVINE, ConsumPotionSeedSeedDict.SEED_NUTVINE,
			ConsumPotionSeedSeedDict.SEED_STARFLOWER_0, ConsumPotionSeedSeedDict.SEED_TRANSMUTE_CAGE,
			ConsumPotionSeedSeedDict.SEED_STAREATER, ConsumPotionSeedSeedDict.SEED_DEWCATCHER,
			ConsumPotionSeedSeedDict.SEED_POD, ConsumPotionSeedSeedDict.SEED_ROT_BERRY,
			ConsumPotionSeedSeedDict.SEED_QUARTZFLOWER, ConsumPotionSeedSeedDict.SEED_SWIFTTHISTLE
	};
	//SPSEXPD: 果实/蔬菜掉落已改为统一规则，不再按果丛逐个配置旧产出表；
	//果丛只保留“附加产出”：浆果果丛的浆果（harvestCount/harvestCategory）、腐莓果丛的中心腐莓之种（centerClass）。

	private static final String[] SEED_HASHES = {
			"EC1D12B4FD149963B61DA4405CE9835ED0DF458D1DEF85CB04B894A62EF69AE0",
			"6736F005A6E0FBF5772F3E0B9AD4E1E4FAB3FE0AE866292564EB259AA82E95DF",
			"BA2924ECA68F8A75D49167D0C3FFB9A01173AEDB7C996CE3BFFADC08B9EC1738",
			"6B2AC4241E62E8EF4CDE7A046A9916AD32FFEF4A62309DC42219C7B595D312D1",
			"02E89B80AAB69C10BE3D109DC825E0AF9FFFE41B28EF69F9321B437C7AB0B2C0",
			"3DF54F87525395A9884A27EC03BFE1A6CEFEDE690B3349B656577CA2B2BD44F4",
			"573EEEADB27A36FA2DA02D43900E1E8377AEB6719C50D18A6429625E816876DA",
			"68845A7924731164ACFBC2B4932F6981B1FECCD1B946131E2C3E97271E59CDF7",
			"5F7FC1BD8532E70B28A453C45F14E4F1EAF99E4911D2901DE90EE3639815E16E",
			"FBFEE7121E67A96A864FB3BDCD4375B77D175684530B204B3039372E9AC48DAB",
			"BA8FC71E0E8A2CD88EA89C22A8ADF7382F08F18C1545C2E8B1A02B426FCBE693",
			"9C4E9AA70F38E28D2B521F4A18EC7C77A5E918DD21C8CD267E00C3791C32EF7B",
			"ABB12616B90AE15D2B21867344411C06EE3B54E973EA8BDB919950FC68943DB0",
			"5EAEE5F54B5BB14F2E7A318A1017FD8BDE490C92F95C677719CC2774E2731D72",
			"48607BB45013DDFD4D3F081777723DFABF3C5C38414CD7F7DF47E2075ACE1F92",
			"284AC8E854AF52F499B8820D5FFF7D7F9831B51D4162BA06E94454564EFD1445",
			"4DA8CD10AC7923DDBF3CFDA0DA251498618DAB13ADCB94A7D2191429E739F8A9",
			"32B8690EB663E030C686168CFB211C42B9FDCA56D91C0DD4D4BCB47CE41386C7"
	};
	private static final String[] PLANT_HASHES = {
			"464085C7ACE44CEE4AE529A6703EC6C364851DD2F8FFD82FE384398C74650D80",
			"ACA1E871BD951601A2062249FF0AB2B087448F6BAF9D455B15B799EADE8E842C",
			"4076F02AD2260A4EEC91B5BDD5BA2AED1020AAEF7F7A605EDACB0F02B994D8A4",
			"C85230491025434397CCECD57C8EA4661CD84A43A6F052A45CDD5B285D64399F",
			"C7AA54828CE18A28E8F6398DD69F15D97685D474476B0649B476544C4456E343",
			"9DD7AFAE05125A125CC9D33C686BF9C6F1EA77AA010B08D3AA4CF9F82E05E57D",
			"B5A27C862709CCD487DE07F3A4A09596D8FC7E0B7A4681A05EA1F0B5FEE732EC",
			"6DCE8ED97324DFBD98E1013B357D85BA59739D0672DB59FFC19184AAD4D310BE",
			"6EB03D85D67E53B777F2A5C070D907AE906574A279EA6D8199A5A53726438BB2",
			"A26E4F4D6C3C773620C9FE2F1D927AFE30DE4528489D7705E884C64B71AAE079",
			"62EC5F8A041BA0685DC6F0CCD7ED599E5A27F61B6CF80119A3E18F2F097D24BC",
			"DD8015969F07E4C81B806A73F9818E964904575A7DDD8EC5C0D9196773D72406",
			"91EBB9C32064785B938432639AA68F0FFCB4DFA78D865B6B2EF6CAF6E8FB6678",
			"F0705EBC13C9459B15D998840375529E9EB27EDB57F6001E0CC9AAAAECDE2BAA",
			"E3CD15EB33C3CA9CE2A5844765D38A717F7A74FF768DD58F5EAC8F06B0E8707B",
			"C08067B7599E52D642D2D9060088B0467605F6EFC8900AF3D7F54C51495F9F8A",
			"F76ED47413E27F7B01EF16E9E265654ED20488063B05294BD0EECAF48B44D82D",
			"EB59E0356036FCA6EBF52B4929D479C6EF68C00BAE509BF89CBD9631852A1EE7",
			"91832C2F6DBC6E795903D864B11DD450B187FEA2326AF32C05DF64130870B6B2"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Dungeon.hero = new Hero();
		Dungeon.depth = 2;
		Dungeon.branch = 0;
		Generator.fullReset();
		testMappingsAndHarvests();
		testBoundaryHarvest();
		testFruitEffects();
		testSorrowmossDepthEffect();
		testLegacyPixels();
		System.out.println("SPS强化植物测试通过：20种种子映射、果丛掉落、存档、果实效果及旧版像素均正确。");
	}

	private static void testSorrowmossDepthEffect() {
		Actor.clear();
		Dungeon.level = new TestLevel(9, 9);
		Dungeon.depth = 14;
		Dungeon.branch = AdventureJournal.branchFor(22);
		TestMob target = new TestMob(100);
		target.pos = 40;
		Actor.add(target);
		Sorrowmoss plant = new Sorrowmoss();
		plant.pos = target.pos;
		plant.activate(target);
		Poison poison = target.buff(Poison.class);
		check(poison != null && "46".equals(poison.iconTextDisplay()),
				"忧伤苔藓没有按混沌85层施加46回合中毒");
		check(target.buff(ShadowCurse.class) != null,
				"忧伤苔藓没有恢复旧版暗影诅咒");
		Dungeon.branch = 0;
	}

	private static void testMappingsAndHarvests() {
		Class<?>[] seeds = Generator.Category.SPS_SEED.classes;
		check(seeds.length == 20, "强化种子牌组数量不是20");
		for (int i = 0; i < seeds.length; i++) {
			Plant.Seed seed = (Plant.Seed)Reflection.newInstance(seeds[i]);
			check(seed.plantClass == PLANTS[i], "第" + i + "种普通植物映射错误");
			check(seed.explantClass == ENHANCED[i], "第" + i + "种强化植物映射错误");
			check(seed.image() == SEED_IMAGES[i], "第" + i + "种种子图标错误");

			TestLevel level = new TestLevel(9, 9);
			SpsFruitBush plant = (SpsFruitBush)seed.excouch(40, level);
			check(plant.getClass() == ENHANCED[i] && plant.image == PLANT_IMAGES[i],
					"第" + i + "种强化植物类型或图像错误");
			//SPSEXPD: 果实/蔬菜掉落改为统一规则（人工 1~2 果实 + 1 蔬菜、精心 2~3 + 2~3），
			//果丛只保留“附加产出”：浆果果丛的浆果、腐莓果丛的中心腐莓之种
			check(plant.harvestCount == (i == 17 ? 3 : 0), "第" + i + "种果丛附加产出数量错误");
			Generator.Category expectedCategory = i == 17 ? Generator.Category.SPS_BERRY : null;
			check(plant.harvestCategory == expectedCategory, "第" + i + "种果丛随机牌组错误");
			check(plant.centerClass == (i == 7 ? Rotberry.Seed.class : null),
					"第" + i + "种果丛中心掉落错误");

			Dungeon.level = level;
			Generator.fullReset();
			Random.pushGenerator(0x535053504C414E54L + i);
			try {
				plant.activate(null);
			} finally {
				Random.popGenerator();
			}
			validateHarvest(level, i);

			Bundle bundle = new Bundle();
			plant.storeInBundle(bundle);
			Plant restored = (Plant)Reflection.newInstance(ENHANCED[i]);
			restored.restoreFromBundle(bundle);
			check(restored.pos == 40, "第" + i + "种强化植物存档位置错误");
		}
	}

	private static void validateHarvest(TestLevel level, int index) {
		//SPSEXPD: 人工种植 = 散落 1~2 枚投掷果实到邻格；浆果果丛额外散落 3 个浆果
		int bonus = index == 17 ? 3 : 0;
		boolean fruitSeen = false;
		boolean bonusSeen = bonus == 0;
		int neighbourHeaps = 0;
		int neighbourItems = 0;
		for (int offset : PathFinder.NEIGHBOURS8) {
			Heap heap = level.heaps.get(40 + offset);
			if (heap == null) continue;
			neighbourHeaps++;
			neighbourItems += heap.items.size();
			check(heap.items.size() == 1, "第" + index + "种果丛在同一邻格重复掉落");
			Item item = heap.peek();
			if (item instanceof MissileWeapon) fruitSeen = true;
			if (index == 17 && item instanceof Fruit) bonusSeen = true;
		}
		check(neighbourHeaps == neighbourItems, "第" + index + "种果丛邻格掉落堆叠异常");
		check(neighbourItems >= 1 + bonus && neighbourItems <= 2 + bonus,
				"第" + index + "种果丛邻格掉落总数错误");
		check(fruitSeen, "第" + index + "种果丛没有散落投掷果实");
		check(bonusSeen, "第" + index + "种果丛附加产出丢失");

		//SPSEXPD: 蔬菜不再散落，全部落在踩踏地（中心格），人工种植固定 1 个
		Heap center = level.heaps.get(40);
		PlantHarvest.Species species = PlantHarvest.speciesFor(ENHANCED[index]);
		check(center != null && center.items.stream().anyMatch(item -> species.vegetable.isInstance(item)),
				"第" + index + "种果丛没有在踩踏地掉落蔬菜");
		if (index == 7) check(center.items.stream().anyMatch(item -> item instanceof Rotberry.Seed),
				"腐莓果丛没有在中心返还种子");
	}

	private static void testBoundaryHarvest() {
		TestLevel level = new TestLevel(11, 11);
		Dungeon.level = level;
		Firebloom.ExFirebloom plant = new Firebloom.ExFirebloom();
		plant.pos = 12;
		Random.pushGenerator(0x424F554E44415259L);
		try {
			plant.activate(null);
		} finally {
			Random.popGenerator();
		}
		int width = level.width();
		int px = 12 % width;
		int py = 12 / width;
		for (int cell = 0; cell < level.length(); cell++) {
			if (level.heaps.get(cell) == null || cell == 12) continue;
			int dx = Math.abs(cell % width - px);
			int dy = Math.abs(cell / width - py);
			check((dx + dy) > 0 && dx <= 1 && dy <= 1, "地图边缘强化植物发生越界或横向绕回");
		}
		check(level.heaps.get(12) != null, "地图边缘强化植物没有在踩踏地掉落蔬菜");
		//SPSEXPD: 果实散落到相邻格，具体落点随机，只要求至少有一格
		boolean edgeFruit = false;
		for (int offset : PathFinder.NEIGHBOURS8) {
			if (level.heaps.get(12 + offset) != null) edgeFruit = true;
		}
		check(edgeFruit, "地图边缘强化植物没有散落果实");
	}

	private static void testFruitEffects() {
		checkStats(new HealFruit(), 20, 20);
		checkStats(new RootFruit(), 20, 20);
		for (Item item : new Item[]{new ShockFruit(), new ToxicFruit(), new FireFruit(),
				new CharmFruit(), new SmokeFruit(), new NutFruit(), new IceFruit()}) {
			checkStats(item, 10, 10);
		}

		TestMob attacker = new TestMob(100);
		TestMob target = new TestMob(100);
		target.HP = 70;
		new HealFruit().proc(attacker, target, 0);
		//SPSEXPD: 果实治疗已改为缓慢治愈 buff
		check(target.buff(Healing.class) != null, "阳春果实没有给予缓慢治愈效果");

		checkBuff(new ShockFruit(), attacker, Paralysis.class, "乱流果没有施加麻痹");
		checkBuff(new ToxicFruit(), attacker, Poison.class, "毒液果没有施加中毒");
		checkBuff(new RootFruit(), attacker, Roots.class, "缠绕果没有施加缠绕");
		checkBuff(new FireFruit(), attacker, Burning.class, "火焰果没有施加燃烧");
		target = new TestMob(100);
		new CharmFruit().proc(attacker, target, 10);
		check(target.buff(Charm.class) != null,
				"魅惑果没有施加魅惑");
		checkBuff(new SmokeFruit(), attacker, Blindness.class, "烟雾果没有施加致盲");
		checkBuff(new IceFruit(), attacker, Cold.class, "冰霜果没有施加寒冷");

		//SPSEXPD: 同种果实按种类堆叠合并（不再受投掷武器 setID/每组3个的限制）
		FireFruit fruitA = new FireFruit(2);
		FireFruit fruitB = new FireFruit(3);
		check(fruitA.isSimilar(fruitB) && fruitA.merge(fruitB) == fruitA
				&& fruitA.quantity() == 5 && fruitB.quantity() == 0,
				"投掷果实没有按种类合并堆叠");

		TestLevel level = new TestLevel(11, 11);
		Dungeon.level = level;
		land(level, new GroundShockFruit(), ElectriShock.class, 6);
		land(level, new GroundToxicFruit(), ToxicGas.class, 4);
		new GroundRootFruit().land(60);
		check(level.map[60] == Terrain.HIGH_GRASS, "缠绕果落地没有生成高草");
		land(level, new GroundFireFruit(), Fire.class, 6);
		land(level, new GroundSmokeFruit(), DarkGas.class, 6);
		land(level, new GroundIceFruit(), FrostCloud.class, 6);
		land(level, new GroundRotFruit(), ToxicGas.class, 8);
		land(level, new GroundBlindFruit(), pd.actors.blobs.ConfusionGas.class, 4);
		land(level, new GroundStarEaterFruit(), pd.actors.blobs.CorrosiveGas.class, 6);
		//SPSEXPD: 星陨果落地降下圣光审判之场
		land(level, new GroundStarFruit(), pd.actors.blobs.effectblobs.HolyLight.class, 3);

		//SPSEXPD: 梦叶果实落地会清空周围 3x3 的有害气体
		pd.scenes.GameScene.add(pd.actors.blobs.Blob.seed(60, 100, ToxicGas.class));
		check(level.blobs.get(ToxicGas.class) != null && level.blobs.get(ToxicGas.class).volume > 0,
				"测试前置的毒气没有被放置");
		new GroundCharmFruit().land(60);
		check(level.blobs.get(ToxicGas.class) == null || level.blobs.get(ToxicGas.class).volume <= 0,				"梦叶果实落地没有清空周围的有害气体");

		//SPSEXPD: 坚果果实命中按主武器两成伤害结算（投掷时 attackingWeapon 是果实自己，必须取主武器）
		Hero nutHero = Dungeon.hero != null ? Dungeon.hero : new Hero();
		pd.items.KindOfWeapon oldWeapon = nutHero.belongings.weapon;
		pd.items.KindOfWeapon oldThrown = nutHero.belongings.thrownWeapon;
		pd.items.equipment.weapon.melee.WornShortsword testWeapon =
				new pd.items.equipment.weapon.melee.WornShortsword();
		//强化到两成伤害明显不为 0，确保断言能区分“取到主武器”与“取到果实自身(0 伤害)”
		testWeapon.upgrade(20);
		nutHero.belongings.weapon = testWeapon;
		//模拟投掷中的状态：此时 attackingWeapon() 会返回果实自身
		nutHero.belongings.thrownWeapon = new NutFruit();
		TestMob nutVictim = new TestMob(100);
		int nutLo = Math.round(testWeapon.min() * 0.2f);
		int nutHi = Math.round(testWeapon.max() * 0.2f);
		check(nutLo > 0, "测试武器的两成伤害为 0，坚果果断言无效");
		new NutFruit().proc(nutHero, nutVictim, 0);
		check(nutVictim.HP <= 100 - nutLo && nutVictim.HP >= 100 - nutHi,
				"坚果果实没有按主武器两成伤害结算（实际伤害 " + (100 - nutVictim.HP)
						+ "，期望 " + nutLo + "~" + nutHi + "）");
		nutHero.belongings.thrownWeapon = oldThrown;
		nutHero.belongings.weapon = oldWeapon;

		new GroundNutFruit().land(60);
		Heap nuts = level.heaps.get(60);
		check(nuts != null && nuts.items.stream().anyMatch(item -> item instanceof pd.items.consum.food.fusion.Nut),
				"坚果果落地没有掉落坚果");

		//SPSEXPD: 竹背篓只收纳投掷果实与大型果实
		pd.items.equipment.bags.BambooBasket basket = new pd.items.equipment.bags.BambooBasket();
		check(basket.canHold(new NutFruit()), "竹背篓不能收纳投掷果实");
		check(basket.canHold(new LargeHealFruit()), "竹背篓不能收纳大型果实");
		check(!basket.canHold(new pd.items.consum.food.fusion.Nut()), "竹背篓收纳了非果实物品");
		//SPSEXPD: 包裹的快捷行为是「打开」，不可售卖但可以扔出
		check(basket.actions(new Hero()).contains(pd.items.equipment.bags.Bag.AC_OPEN),
				"竹背篓的快捷行为不是打开");
		check(basket.actions(new Hero()).contains(pd.items.Item.AC_DROP), "竹背篓不能扔出");
		check(!pd.actors.mobs.npcs.Shopkeeper.canSell(basket), "竹背篓不应该可以售卖");
		//SPSEXPD: 「打开」动作的文本键必须存在（测试环境无法初始化 Messages，改为直接查资源文件）
		for (String lang : new String[]{"zh", "en"}) {
			try {
				String text = new String(java.nio.file.Files.readAllBytes(
						java.nio.file.Paths.get("messages/items/" + lang + "/items.properties")),
						java.nio.charset.StandardCharsets.UTF_8);
				check(text.contains("items.equipment.bags.bag.ac_open="),
						"包裹「打开」动作缺少文本键（" + lang + "）");
			} catch (Exception e) {
				check(false, "读取包裹文本资源失败：" + e);
			}
		}
	}

	private static void checkStats(Item item, int min, int max) {
		pd.items.equipment.weapon.Weapon weapon =
				(pd.items.equipment.weapon.Weapon)item;
		check(weapon.min() == min && weapon.max() == max && weapon.STRReq() == 5,
				item.getClass().getSimpleName() + "基础数值错误");
		check(!item.isUpgradable() && item.isIdentified(), item.getClass().getSimpleName() + "识别属性错误");
	}

	private static void checkBuff(pd.items.equipment.weapon.Weapon fruit,
			TestMob attacker, Class<?> buff, String message) {
		TestMob target = new TestMob(100);
		fruit.proc(attacker, target, 10);
		check(target.buff((Class)buff) != null, message);
	}

	private static void land(TestLevel level, GroundFruit fruit, Class<? extends Blob> type, int perCell) {
		level.resetEffects();
		fruit.land(60);
		//SPSEXPD: 落点效果为周围 1 格（3x3），每格强度沿用原值
		Blob blob = level.blobs.get(type);
		String name = fruit.getClass().getSimpleName();
		check(blob != null && blob.cur[60] == perCell,
				name + "落地效果强度错误（实际 " + (blob == null ? "无" : String.valueOf(blob.cur[60])) + "，期望 " + perCell + "）");
		int covered = 0;
		for (int cell = 0; cell < level.length(); cell++) {
			if (blob.cur[cell] > 0) {
				check(level.insideMap(cell), name + "落地范围发生地图越界");
				covered++;
			}
		}
		check(covered <= 9, name + "落地范围超过了周围 1 格");
	}

	private static void checkBlob(TestLevel level, Class<? extends Blob> type, int volume, String message) {
		Blob blob = level.blobs.get(type);
		check(blob != null && blob.volume == volume, message);
		for (int cell = 0; cell < level.length(); cell++) {
			if (blob.cur[cell] > 0) check(level.insideMap(cell), message + "且发生地图越界");
		}
	}

	private static void testLegacyPixels() throws Exception {
		BufferedImage items = ImageIO.read(new File("sprites/items/items.png"));
		BufferedImage terrain = ImageIO.read(new File("environment/features/terrain_features.png"));
		check(items != null && items.getWidth() == 256 && items.getHeight() == 992, "物品图集尺寸错误");
		check(terrain != null && terrain.getWidth() == 256 && terrain.getHeight() == 288, "植物图集尺寸错误");
		for (int i = 0; i < SEED_HASHES.length; i++) {
			int x = (i % 16) * 16;
			int y = 864 + (i / 16) * 16;
			check(SEED_HASHES[i].equals(hash(items, x, y)), "旧版种子图标" + i + "像素错误");
		}
		for (int i = 0; i < PLANT_HASHES.length; i++) {
			int x = (i % 16) * 16;
			int y = 256 + (i / 16) * 16;
			check(PLANT_HASHES[i].equals(hash(terrain, x, y)), "旧版植物图像" + i + "像素错误");
		}
	}

	private static String hash(BufferedImage image, int left, int top) throws Exception {
		byte[] bytes = new byte[16 * 16 * 4];
		int index = 0;
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				int argb = image.getRGB(left + x, top + y);
				bytes[index++] = (byte)(argb >>> 24);
				bytes[index++] = (byte)(argb >>> 16);
				bytes[index++] = (byte)(argb >>> 8);
				bytes[index++] = (byte)argb;
			}
		}
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
		StringBuilder result = new StringBuilder(64);
		for (byte value : digest) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private interface GroundFruit { void land(int cell); }
	private static final class GroundHealFruit extends HealFruit implements GroundFruit { public void land(int c) { onThrow(c); } }
	private static final class GroundShockFruit extends ShockFruit implements GroundFruit { public void land(int c) { onThrow(c); } }
	private static final class GroundToxicFruit extends ToxicFruit implements GroundFruit { public void land(int c) { onThrow(c); } }
	private static final class GroundRootFruit extends RootFruit implements GroundFruit { public void land(int c) { onThrow(c); } }
	private static final class GroundFireFruit extends FireFruit implements GroundFruit { public void land(int c) { onThrow(c); } }
	private static final class GroundCharmFruit extends CharmFruit implements GroundFruit { public void land(int c) { onThrow(c); } }
	private static final class GroundSmokeFruit extends SmokeFruit implements GroundFruit { public void land(int c) { onThrow(c); } }
	private static final class GroundIceFruit extends IceFruit implements GroundFruit { public void land(int c) { onThrow(c); } }
	private static final class GroundNutFruit extends NutFruit implements GroundFruit { public void land(int c) { onThrow(c); } }
	private static final class GroundRotFruit extends RotFruit implements GroundFruit { public void land(int c) { onThrow(c); } }
	private static final class GroundBlindFruit extends BlindFruit implements GroundFruit { public void land(int c) { onThrow(c); } }
	private static final class GroundStarEaterFruit extends StarEaterFruit implements GroundFruit { public void land(int c) { onThrow(c); } }
	private static final class GroundStarFruit extends StarFruit implements GroundFruit { public void land(int c) { onThrow(c); } }

	private static final class TestMob extends Mob {
		TestMob(int health) { HP = HT = health; }
		@Override public int damageRoll() { return 1; }
		@Override public int attackSkill(Char target) { return 1; }
		@Override public int drRoll() { return 0; }
	}

	private static final class TestLevel extends Level {
		TestLevel(int width, int height) {
			setSize(width, height);
			Arrays.fill(map, Terrain.EMPTY);
			mobs().clear();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<>();
			traps = new SparseArray<Trap>();
			transitions = new ArrayList<LevelTransition>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			buildFlagMaps();
		}
		void resetEffects() {
			Actor.clear();
			blobs.clear();
			heaps.clear();
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

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsEnhancedPlantsTest() { }
}
