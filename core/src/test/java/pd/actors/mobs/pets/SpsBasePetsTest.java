package pd.actors.mobs.pets;

import pd.atlas.items.ConsumSummorDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.HolyStun;
import pd.actors.buffs.LightShootAttack;
import pd.actors.buffs.MagicWeak;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.Slow;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.Talent;
import pd.actors.mobs.Mob;
import pd.items.Garbage;
import pd.items.Item;
import pd.items.UpgradeBlobRed;
import pd.items.consum.eggs.ButterflypetEgg;
import pd.items.consum.eggs.ChocoboEgg;
import pd.items.consum.eggs.DaturaEgg;
import pd.items.consum.eggs.DogpetEgg;
import pd.items.consum.eggs.DwarfBoyEgg;
import pd.items.consum.eggs.Egg;
import pd.items.consum.eggs.FlyEgg;
import pd.items.consum.eggs.FoxHelperEgg;
import pd.items.consum.eggs.FrogpetEgg;
import pd.items.consum.eggs.GentleCrabEgg;
import pd.items.consum.eggs.KodoraEgg;
import pd.items.consum.eggs.LitDemonEgg;
import pd.items.consum.eggs.MonkeyEgg;
import pd.items.consum.eggs.RandomEasterEgg;
import pd.items.consum.eggs.RibbonRatEgg;
import pd.items.consum.eggs.SnakeEgg;
import pd.items.consum.eggs.SpiderpetEgg;
import pd.items.consum.eggs.StarKidEgg;
import pd.items.consum.eggs.StoneEgg;
import pd.items.consum.food.WaterItem;
import pd.items.consum.food.completefood.MoonCake;
import pd.items.consum.food.completefood.PetFood;
import pd.items.consum.food.fruit.Strawberry;
import pd.items.consum.food.meatfood.Meat;
import pd.items.consum.food.staplefood.NormalRation;
import pd.items.consum.food.vegetable.Truffles;
import pd.items.consum.potions.PotionOfLiquidFlame;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.consum.potions.PotionOfShield;
import pd.items.consum.scrolls.ScrollOfRage;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.items.equipment.weapon.melee.Whip;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.plants.Dewcatcher;
import pd.plants.Sungrass;
import render.noosa.Game;
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
import java.util.Set;

import javax.imageio.ImageIO;

public final class SpsBasePetsTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		try {
			testStatsFoodAndRewards();
			testCombatAndSupport();
			testSecondPetBatch();
			testRemainingPets();
			testEggsAndPersistence();
			testAssets();
			System.out.println("SPS普通宠物测试通过：17种普通宠物的成长、食物、奖励、战斗、孵化、存档和原始素材均正常。");
		} finally {
			Actor.clear(); Dungeon.level = null; Dungeon.hero = null;
		}
	}

	private static void testRemainingPets() throws Exception {
		Hero hero = heroAndLevel(10);
		MonkeyProbe monkey = new MonkeyProbe(); SnakeProbe snake = new SnakeProbe(); Spider spider = new Spider();
		StarProbe star = new StarProbe(); StoneProbe stone = new StoneProbe(); FlyProbe fly = new FlyProbe(); RibbonProbe ribbon = new RibbonProbe();
		for (LegacyPet pet : new LegacyPet[]{monkey, snake, spider, star, stone, fly, ribbon}) check(pet.HT == 170, pet.getClass().getSimpleName() + "生命成长错误");
		check(monkey.legacyType() == 302 && monkey.lovefood(new Strawberry()) && monkey.reaches(hero.pos, hero.pos + 2), "绿皮猴属性、食物或攻击距离错误");
		Dummy enemy = new Dummy(); enemy.pos = hero.pos + 2; Actor.add(enemy);
		snake.setCooldown(0); int hp = enemy.HP; snake.attackProc(enemy, 10); check(enemy.HP <= hp - hp / 3, "毒蛇没有削减目标三分之一生命");
		check(spider.legacyType() == 204 && spider.defenseSkill == 15, "植蛛类型或闪避成长错误");
		star.setCooldown(0); hp = enemy.HP; check(star.attackProc(enemy, 20) == 0 && enemy.HP < hp && enemy.buff(LightShootAttack.class) != null,
				"星芒没有转为光属性伤害或施加光击");
		stone.setCooldown(0); stone.defenseProc(enemy, 1); check(enemy.buff(HolyStun.class) != null, "石拳石没有在冷却结束时施加神圣眩晕");
		for (int i = 0; i < 200 && enemy.buff(Slow.class) == null; i++) fly.attackProc(enemy, 1);
		check(enemy.buff(Slow.class) != null && fly.legacyType() == 203, "飞蝇没有10%减速效果");
		hero.subClass = HeroSubClass.NONE; check(fly.minion().HT == 1 && ribbon.minion().HT == 1, "普通职业的宠物召唤物生命不是1");
		hero.subClass = HeroSubClass.LEADER; check(fly.minion().HT == 50 && ribbon.minion().HT == 50, "领袖职业没有强化宠物召唤物生命");
		check(ribbon.legacyType() == 103, "缎带鼠旧版类型错误");
		check(hatch(new MonkeyEgg()) instanceof Monkey && hatch(new SnakeEgg()) instanceof Snake
				&& hatch(new SpiderpetEgg()) instanceof Spider && hatch(new StarKidEgg()) instanceof StarKid
				&& hatch(new StoneEgg()) instanceof Stone && hatch(new FlyEgg()) instanceof Fly
				&& hatch(new RibbonRatEgg()) instanceof RibbonRat, "最后七种普通蛋孵化类型错误");
		check(new MonkeyEgg().image == ConsumSummorDict.MONKEY_EGG_0 && new SnakeEgg().image == ConsumSummorDict.SNAKE_PET_EGG_0
				&& new SpiderpetEgg().image == ConsumSummorDict.SPIDER_PET_EGG_0 && new StarKidEgg().image == ConsumSummorDict.STAR_KID_EGG_0
				&& new StoneEgg().image == ConsumSummorDict.STONE_PET_EGG_0 && new FlyEgg().image == ConsumSummorDict.FLY_EGG_0
				&& new RibbonRatEgg().image == ConsumSummorDict.RIBBON_RAT_EGG_0, "最后七种普通蛋图标索引错误");
	}

	private static void testSecondPetBatch() throws Exception {
		Hero hero = heroAndLevel(10);
		FoxProbe fox = new FoxProbe(); FrogProbe frog = new FrogProbe(); GentleProbe crab = new GentleProbe();
		KodoraProbe kodora = new KodoraProbe(); LitProbe demon = new LitProbe();
		for (LegacyPet pet : new LegacyPet[]{fox, frog, crab, kodora, demon}) check(pet.HT == 170, pet.getClass().getSimpleName() + "生命成长错误");
		check(fox.legacyType() == 305 && fox.lovefood(new Strawberry()) && fox.SupercreateLoot() instanceof UpgradeBlobRed,
				"狐女仆属性、食物或奖励错误");
		check(fox.reward() instanceof ScrollOfUpgrade, "狐女仆的相邻支援奖励不是升级卷轴");
		check(frog.legacyType() == 306 && frog.lovefood(new Truffles()) && frog.SupercreateLoot() instanceof Whip
				&& frog.reaches(hero.pos, hero.pos + 2), "呆头蛙属性、食物、奖励或攻击距离错误");
		check(crab.legacyType() == 102 && Math.abs(crab.speed() - 1.5f) < 0.001f
				&& crab.lovefood(new Truffles()) && crab.SupercreateLoot() instanceof PotionOfShield,
				"绅士蟹速度、食物或奖励错误");
		Dummy enemy = new Dummy(); enemy.pos = hero.pos + 2; Actor.add(enemy); crab.setCooldown(0); crab.attackProc(enemy, 20);
		check(enemy.buff(ArmorBreak.class) != null && enemy.buff(ArmorBreak.class).level() == 20, "绅士蟹没有施加旧版20级破甲");
		check(kodora.legacyType() == 101 && kodora.lovefood(new Meat()) && kodora.SupercreateLoot() instanceof PotionOfLiquidFlame,
				"柯多拉属性、食物或奖励错误");
		kodora.setCooldown(0); int hp = enemy.HP; int physical = kodora.attackProc(enemy, 99);
		check(physical == 0 && enemy.HP < hp && enemy.buff(MagicWeak.class) != null, "柯多拉没有将攻击转成魔法伤害并施加魔法易伤");
		check(demon.legacyType() == 105 && demon.lovefood(new PotionOfHealing()) && demon.SupercreateLoot() instanceof ScrollOfRage,
				"链锯魔属性、食物或奖励错误");
		demon.setCooldown(0); hp = enemy.HP; demon.attackProc(enemy, 50);
		check(enemy.HP <= hp - 6, "链锯魔冷却结束时没有完成六段切割");
		check(hatch(new FoxHelperEgg()) instanceof FoxHelper && hatch(new FrogpetEgg()) instanceof FrogPet
				&& hatch(new GentleCrabEgg()) instanceof GentleCrab && hatch(new KodoraEgg()) instanceof Kodora
				&& hatch(new LitDemonEgg()) instanceof LitDemon, "第二批五种普通蛋孵化类型错误");
		check(new FoxHelperEgg().image == ConsumSummorDict.FOX_HELPER_EGG_0 && new FrogpetEgg().image == ConsumSummorDict.FROG_PET_EGG_0
				&& new GentleCrabEgg().image == ConsumSummorDict.GENTLE_CRAB_EGG_0 && new KodoraEgg().image == ConsumSummorDict.KODORA_EGG_0
				&& new LitDemonEgg().image == ConsumSummorDict.LIT_DEMON_EGG_0, "第二批五种普通蛋图标索引错误");
	}

	private static void testStatsFoodAndRewards() {
		heroAndLevel(10);
		ButterflyPet butterfly = new ButterflyPet(); Chocobo chocobo = new Chocobo();
		Datura datura = new Datura(); DogPet dog = new DogPet(); DwarfBoy dwarf = new DwarfBoy();
		for (LegacyPet pet : new LegacyPet[]{butterfly, chocobo, datura, dog, dwarf}) {
			check(pet.HT == 170 && pet.attackSkill(null) == 15, pet.getClass().getSimpleName() + "成长错误");
		}
		check(butterfly.defenseSkill == 10 && butterfly.legacyType() == 304
				&& butterfly.lovefood(new PetFood()) && butterfly.lovefood(new Strawberry())
				&& butterfly.SupercreateLoot() instanceof Garbage, "萤石粉蝶属性、食物或奖励错误");
		check(chocobo.defenseSkill == 15 && chocobo.legacyType() == 202
				&& chocobo.lovefood(new Truffles()) && chocobo.SupercreateLoot() instanceof RandomEasterEgg,
				"陆行鸟属性、食物或奖励错误");
		check(datura.defenseSkill == 10 && datura.legacyType() == 301
				&& datura.lovefood(new WaterItem()) && datura.lovefood(new Sungrass.Seed())
				&& datura.SupercreateLoot() instanceof Dewcatcher.Seed, "曼陀罗属性、食物或奖励错误");
		check(dog.defenseSkill == 15 && dog.legacyType() == 201
				&& dog.lovefood(new Meat()) && dog.SupercreateLoot() instanceof MoonCake,
				"忠犬属性、食物或奖励错误");
		check(dwarf.defenseSkill == 15 && dwarf.legacyType() == 206
				&& dwarf.lovefood(new MoonCake()) && dwarf.SupercreateLoot() instanceof NormalRation,
				"矮人学徒属性、食物或奖励错误");
	}

	private static void testCombatAndSupport() {
		Hero hero = heroAndLevel(10); Dummy enemy = new Dummy(); enemy.pos = hero.pos + 2; Actor.add(enemy);
		ButterflyProbe butterfly = new ButterflyProbe(); butterfly.pos = hero.pos + 1; butterfly.setCooldown(0);
		hero.HP = 50; butterfly.supportHero(); check(hero.HP == 55, "萤石粉蝶没有相邻治疗5点生命");
		DogProbe dog = new DogProbe(); dog.pos = hero.pos + 1; dog.setCooldown(0); dog.supportHero();
		check(hero.buff(ShieldArmor.class) != null && hero.buff(ShieldArmor.class).level() == 20
				&& dog.buff(ShieldArmor.class) != null && dog.buff(ShieldArmor.class).level() == 20,
				"忠犬没有为双方提供双倍宠物等级护盾");
		ChocoboProbe chocobo = new ChocoboProbe(); chocobo.setCooldown(0); chocobo.defenseProc(enemy, 1);
		check(hero.buff(HasteBuff.class) != null, "陆行鸟没有在冷却结束后为英雄加速");
		ButterflyPet blind = new ButterflyPet();
		for (int i = 0; i < 200 && enemy.buff(Blindness.class) == null; i++) blind.attackProc(enemy, 1);
		check(enemy.buff(Blindness.class) != null, "萤石粉蝶没有20%致盲效果"); enemy.remove(enemy.buff(Blindness.class));
		Datura datura = new Datura();
		for (int i = 0; i < 200 && enemy.buff(Terror.class) == null; i++) datura.attackProc(enemy, 1);
		check(enemy.buff(Terror.class) != null && enemy.buff(Terror.class).object == datura.id(), "曼陀罗没有20%恐吓效果");
		enemy.remove(enemy.buff(Terror.class)); DwarfBoy dwarf = new DwarfBoy();
		for (int i = 0; i < 200 && enemy.buff(Vertigo.class) == null; i++) dwarf.attackProc(enemy, 1);
		check(enemy.buff(Vertigo.class) != null, "矮人学徒没有20%眩晕效果");
		int hp = enemy.HP; new DwarfProbe().retaliate(enemy); check(enemy.HP == hp - 30, "矮人学徒冷却反伤数值错误");
	}

	private static void testEggsAndPersistence() throws Exception {
		heroAndLevel(10);
		check(hatch(new ButterflypetEgg()) instanceof ButterflyPet && hatch(new ChocoboEgg()) instanceof Chocobo
				&& hatch(new DaturaEgg()) instanceof Datura && hatch(new DogpetEgg()) instanceof DogPet
				&& hatch(new DwarfBoyEgg()) instanceof DwarfBoy, "五种普通蛋孵化类型错误");
		check(new ButterflypetEgg().image == SpecificPlaceHolderDict.SOMETHING_0
				&& new ChocoboEgg().image == ConsumSummorDict.CHOCOBO_EGG_0 && new DaturaEgg().image == ConsumSummorDict.DATURA_EGG_0
				&& new DogpetEgg().image == ConsumSummorDict.DOG_PET_EGG_0 && new DwarfBoyEgg().image == ConsumSummorDict.DWARF_BOY_EGG_0,
				"五种普通蛋图标索引错误");
		Set<Class<?>> easter = new HashSet<>();
		for (int seed = 0; seed < 100; seed++) {
			Random.pushGenerator(seed); try { easter.add(hatch(new RandomEasterEgg()).getClass()); } finally { Random.popGenerator(); }
		}
		check(easter.contains(Bunny.class) && easter.contains(CocoCat.class) && easter.contains(Velocirooster.class),
				"随机复活节之魂没有覆盖原版三种宠物");
		ButterflyProbe original = new ButterflyProbe(); original.setCooldown(17); Bundle bundle = new Bundle(); original.storeInBundle(bundle);
		ButterflyProbe restored = new ButterflyProbe(); restored.restoreFromBundle(bundle);
		check(restored.petCooldown() == 17 && restored.legacyType() == 304, "普通宠物冷却或类型存档错误");
	}

	private static void testAssets() throws Exception {
		checkFile("sprites/mobs/sps_butterfly.png", "0B98A819C4D13FF724BDD7B97E40968B7285C6EDFD583E7CD1E91992D84B96B7");
		checkFile("sprites/pets/sps_chocobo.png", "094F733BB54FE30A519ABBBD7DBA98DB903AA73C0A5535E40259B0A9D3FD6715");
		checkFile("sprites/mobs/sps_datura.png", "0C61D31D6F734575F56D50B44805C8CC39A17E112D1B82C38D8179568D09741C");
		checkFile("sprites/pets/sps_dog.png", "A56037870E320CAA6C40D4C44384A87C11F607C94CA886A2021F4FD4DF7C1846");
		checkFile("sprites/mobs/sps_dwarfboy.png", "A83AC2159BBBEE4DD930A95B9EDC25588CCE5BF6ADB869AA2525D99C6DCC32BE");
		checkFile("sprites/pets/sps_foxhelper.png", "CFF40E54621F777D017188BC296E2BB4CB600540E7CEB8CD558683365A0C12EA");
		checkFile("sprites/pets/sps_frogpet.png", "C767BA6E9D6FA58A8E3BC0B255D7347FF399EA0AE1D972DDB258AD7FABE09891");
		checkFile("sprites/mobs/sps_gentlecrab.png", "1A2F64F307035B2AD6CFDE74E52E432571C79ACF239414EF10128AC653A1936B");
		checkFile("sprites/mobs/sps_kodora.png", "B5E47A3CD2270D9ABC40BF9308A0237FFC5C57B528A94466003E0D565C97BD72");
		checkFile("sprites/mobs/sps_litdemon.png", "FC9C938357299A158497405CFBF6BBFE22B27C51C86F31C85C084295D6027ED8");
		checkFile("sprites/mobs/sps_kliks.png", "9CB33F2B20852985BFFF31AF794BA4A3B8629B26E3BCC7CBDAD323C11CBD9402");
		checkFile("sprites/mobs/sps_newsnake.png", "917911B6757DB204AFC856FAE62CBB2BB1B3542AA683D7B7F8B1BCE770EE6945");
		checkFile("sprites/mobs/sps_newspinner.png", "2AF385BA707DE6E604BACD4E618A9B1E9E9ED10B488B158A80775668D491F540");
		checkFile("sprites/mobs/sps_starkid.png", "D5DD8105C8721C3FE9870F5A1FC4E53921B2B511F2173E03334114126EE31B9D");
		checkFile("sprites/mobs/sps_swarm.png", "48F99550B08C1DA7BBA06596A6D1EC0D5F0410438F5D4017BEFD93E73AF31D5F");
		checkFile("sprites/mobs/sps_ribbon_rat.png", "7B7580B84402C02D3E895891611087E4B363C1826E4FF22BA37F0233C866DC4E");
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		String[] hashes = {"6DF88387870BBE837269B556507A74A1BC0366AA9A924FDAACC40EC4ECB99D26",
				"EC544C3533E11ACFBC20ECF85E2204D81EF94C86C758601AEC3796781D6FD160",
				"C532E4F9960CEE0F1C4892F9899D4571C37FA44E818C929BF5BB9F0E509057DE",
				"5ACC23D81D5B32DA6FCE7E8702E36D006A120DBB51742EE4083B292C7D028EC3",
				"DF13A55048F4F23A0E85A1A1842EFF7CA3F378C735BC77E2572EB2A48A855EBD"};
		for (int i = 0; i < hashes.length; i++) checkIcon(sheet, 112 + i * 16, hashes[i]);
		checkIcon(sheet, 208, 896, "590F296E73D33F6087A5D69F023B5F19C8B9D76862B1B1F8E0DF0BAFC098F821");
		checkIcon(sheet, 224, 896, "A3E2890976B0ABF36EA9AF77584B98AD99B1CB5C4DB1FF262DC49E8CBDCC1C68");
		checkIcon(sheet, 240, 896, "ED7FF191935EF8D98785596DD4D07D179E2AD21CDB43557B4655BCF6A964D381");
		checkIcon(sheet, 0, 912, "46DE051C906F62C9620EC498F3746AA6E1FBAB5AB63AFB1235F06348403551EB");
		checkIcon(sheet, 16, 912, "871C5F6D113E16FD0D2E5BC000DC2046C34F8B6F8145E8114F98860062E4D736");
		checkIcon(sheet, 32, 912, "14902408BECA8B70067DEE7E1023ABF393161CE0C9DB9FB81EB86CC176260B4C");
		checkIcon(sheet, 48, 912, "8DFD221B4C942E8C484E559174480137A3481A58BBDAA07EECEB47CF6E83339F");
		checkIcon(sheet, 64, 912, "B2F1702639C20901D767A38B11EC5B69BF47DF0BE34D6C218EDF5417CDBD7977");
		checkIcon(sheet, 80, 912, "A1B783F63C2711D918B22C1D1973705B94A2775613EC4560C7C09BF5ED17ECE2");
		checkIcon(sheet, 96, 912, "252A77BDD1C6CBF9C3FBDE523B07AC7A610319CFC61A1D58D708F2315AB6E194");
		checkIcon(sheet, 112, 912, "1B5F025067644CF1A9D965C8C31A51D8DDDDA61821934ABDDB9C43DD59414F23");
		checkIcon(sheet, 192, 896, "870D8888D5D71901155B5DEC6C1E597CE76E98A6665594C76BD190793C995C3B");
	}

	private static Hero heroAndLevel(int level) {
		Actor.clear(); Hero hero = new Hero(); Talent.initClassTalents(hero); hero.HP = hero.HT = 100; hero.petLevel = level;
		hero.pos = 100; Dungeon.hero = hero; Dungeon.level = new TestLevel(); Actor.add(hero); return hero;
	}
	private static LegacyPet hatch(Egg egg) throws Exception { Method method = Egg.class.getDeclaredMethod("hatchling"); method.setAccessible(true); return (LegacyPet)method.invoke(egg); }
	private static void checkFile(String path, String expected) throws Exception { check(expected.equals(hex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(new File(path).toPath())))), path + "原始动画不一致"); }
	private static void checkIcon(BufferedImage sheet, int x0, String expected) throws Exception {
		checkIcon(sheet, x0, 896, expected);
	}
	private static void checkIcon(BufferedImage sheet, int x0, int y0, String expected) throws Exception {
		ByteBuffer data = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = y0; y < y0 + 16; y++) for (int x = x0; x < x0 + 16; x++) data.putInt(sheet.getRGB(x, y));
		check(expected.equals(hex(MessageDigest.getInstance("SHA-256").digest(data.array()))), x0 + "位置的宠物蛋图标不一致");
	}
	private static String hex(byte[] bytes) { StringBuilder result = new StringBuilder(); for (byte value : bytes) result.append(String.format("%02X", value & 0xFF)); return result.toString(); }
	private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

	private static class ButterflyProbe extends ButterflyPet { void setCooldown(int value) { cooldown = value; } int petCooldown() { return cooldown; } }
	private static class DogProbe extends DogPet { void setCooldown(int value) { cooldown = value; } }
	private static class ChocoboProbe extends Chocobo { void setCooldown(int value) { cooldown = value; } }
	private static class DwarfProbe extends DwarfBoy { DwarfProbe() { cooldown = 0; } int retaliate(Char enemy) { return defenseProc(enemy, 0); } }
	private static class FoxProbe extends FoxHelper { Item reward() { return supportReward(); } }
	private static class FrogProbe extends FrogPet { boolean reaches(int from, int to) { pos = from; return canAttack(new DummyAt(to)); } }
	private static class GentleProbe extends GentleCrab { void setCooldown(int value) { cooldown = value; } }
	private static class KodoraProbe extends Kodora { void setCooldown(int value) { cooldown = value; } }
	private static class LitProbe extends LitDemon { void setCooldown(int value) { cooldown = value; } }
	private static class MonkeyProbe extends Monkey { boolean reaches(int from,int to){pos=from;return canAttack(new DummyAt(to));} }
	private static class SnakeProbe extends Snake { void setCooldown(int value){cooldown=value;} }
	private static class StarProbe extends StarKid { void setCooldown(int value){cooldown=value;} }
	private static class StoneProbe extends Stone { void setCooldown(int value){cooldown=value;} }
	private static class FlyProbe extends Fly { FlyTwo minion(){return createMinion();} }
	private static class RibbonProbe extends RibbonRat { RibbonRatTwo minion(){return createMinion();} }
	private static class Dummy extends Mob { Dummy() { HP = HT = 1000; defenseSkill = 0; } @Override public int attackSkill(Char target) { return 0; } @Override public int damageRoll() { return 0; } }
	private static class DummyAt extends Dummy { DummyAt(int position) { pos = position; } }
	private static class TestLevel extends Level {
		TestLevel() { setSize(32, 32); mobs().clear(); heaps = new SparseArray<>(); blobs = new HashMap<>(); plants = new SparseArray<>(); traps = new SparseArray<>(); transitions = new ArrayList<>(); customTiles = new ArrayList<>(); customTerrain = new ArrayList<>(); customWalls = new ArrayList<>(); Arrays.fill(map, Terrain.EMPTY); Arrays.fill(passable, true); buildFlagMaps(); }
		@Override protected boolean build() { return true; } @Override protected void createMobs() { } @Override protected void createItems() { } @Override public String tilesTex() { return null; } @Override public String waterTex() { return null; }
	}
	private SpsBasePetsTest() { }
}
