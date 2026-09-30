package pd.items.weapon.enchantments;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Cold;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.DamageUp;
import pd.actors.buffs.Frost;
import pd.actors.buffs.FrostIce;
import pd.actors.buffs.GrowSeed;
import pd.actors.buffs.Hot;
import pd.actors.buffs.LightShootAttack;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Roots;
import pd.actors.buffs.ShadowCurse;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.Shocked;
import pd.actors.buffs.Tar;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.buffs.Wet;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Heap;
import pd.items.Item;
import pd.items.weapon.Weapon;
import pd.items.weapon.melee.normalweapon.Dagger;
import pd.levels.Level;
import pd.plants.Plant;
import watabou.noosa.Game;
import watabou.utils.Bundle;
import watabou.utils.Random;
import watabou.utils.SparseArray;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/** Headless regression checks for SPS-PD 0.9.8's 14 ordinary weapon enchantments. */
public final class SpsLegacyEnchantmentTest {

	private static final Class<?>[] CLASSES = {
		EnchantmentFire.class, EnchantmentEarth.class, EnchantmentDark.class,
		EnchantmentEnergy.class, EnchantmentIce.class, EnchantmentShock.class,
		EnchantmentLight.class, EnchantmentFire2.class, EnchantmentEarth2.class,
		EnchantmentDark2.class, EnchantmentEnergy2.class, EnchantmentIce2.class,
		EnchantmentShock2.class, EnchantmentLight2.class
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x535053454E43484CL);
		try {
			Dungeon.level = new TestLevel();
			Hero hero = new Hero();
			hero.HP = hero.HT = 100;
			hero.pos = 1;
			Dungeon.hero = hero;
			testPool();
			testDamageAndEffects();
			testSafeBounds();
			testDamageCharge();
			testSaveRestore();
			testLocalization();
			System.out.println("SPS旧版14种普通武器附魔测试通过：随机池、元素伤害、状态、边界、存档和中英文文本均正常。");
		} finally {
			Random.popGenerator();
		}
	}

	private static void testPool() {
		Set<Class<?>> expected = new HashSet<>(Arrays.asList(CLASSES));
		Set<Class<?>> configured = new HashSet<>();
		configured.addAll(Arrays.asList(Weapon.Enchantment.common));
		configured.addAll(Arrays.asList(Weapon.Enchantment.uncommon));
		configured.addAll(Arrays.asList(Weapon.Enchantment.rare));
		check(configured.equals(expected), "普通附魔随机池不是旧版14种");
		check(Weapon.Enchantment.common.length == 5 && Weapon.Enchantment.uncommon.length == 5
				&& Weapon.Enchantment.rare.length == 4, "旧版附魔分组适配错误");
		check(Arrays.equals(Weapon.Enchantment.typeChances, new float[]{5, 5, 4}), "旧版附魔等权概率错误");
		Set<Class<?>> generated = new HashSet<>();
		for (int i = 0; i < 10_000; i++) generated.add(Weapon.Enchantment.random().getClass());
		check(generated.equals(expected), "普通附魔随机入口无法生成全部14种");
	}

	private static void testDamageAndEffects() {
		Dagger weapon = new Dagger();
		TestMob attacker = mob(100);

		checkDamage(new EnchantmentFire(), weapon, attacker, 2);
		TestMob target = mob(100_000);
		until(new EnchantmentFire(), weapon, attacker, target, Burning.class);

		checkDamage(new EnchantmentFire2(), weapon, attacker, 7);
		target = mob(100_000);
		new EnchantmentFire2().proc(weapon, attacker, target, 0);
		check(target.buff(Hot.class) != null, "焦油附魔没有施加灼热");
		until(new EnchantmentFire2(), weapon, attacker, target, Tar.class);

		checkDamage(new EnchantmentEarth(), weapon, attacker, 7);
		until(new EnchantmentEarth(), weapon, attacker, mob(100_000), GrowSeed.class);
		checkDamage(new EnchantmentEarth2(), weapon, attacker, 2);
		target = mob(100_000);
		until(new EnchantmentEarth2(), weapon, attacker, target, Roots.class);
		check(target.buff(Ooze.class) != null, "酸蚀附魔没有同时施加腐酸");

		checkDamage(new EnchantmentDark(), weapon, attacker, 7);
		until(new EnchantmentDark(), weapon, attacker, mob(100_000), Terror.class);
		checkDamage(new EnchantmentDark2(), weapon, attacker, 2);
		target = mob(100_000);
		new EnchantmentDark2().proc(weapon, attacker, target, 0);
		check(target.buff(ShadowCurse.class) != null, "咒术附魔没有施加暗影诅咒");

		checkDamage(new EnchantmentEnergy(), weapon, attacker, 2);
		attacker = mob(100);
		target = mob(100_000);
		new EnchantmentEnergy().proc(weapon, attacker, target, 0);
		check(attacker.buff(DamageUp.class) != null && attacker.buff(DamageUp.class).level() == 10,
				"战意附魔没有积蓄固定伤害");
		new EnchantmentEnergy().proc(weapon, attacker, target, 0);
		check(target.buff(Cripple.class) != null, "已有战意时没有使目标残废");

		attacker = mob(100);
		checkDamage(new EnchantmentEnergy2(), weapon, attacker, 7);
		attacker = mob(100);
		target = mob(100_000);
		new EnchantmentEnergy2().proc(weapon, attacker, target, 0);
		check(attacker.buff(ShieldArmor.class) != null && attacker.buff(ShieldArmor.class).level() == 10,
				"剑舞附魔没有提供物理护盾");
		new EnchantmentEnergy2().proc(weapon, attacker, target, 0);
		check(target.buff(Vertigo.class) != null, "已有剑舞护盾时没有使目标眩晕");

		attacker = mob(100);
		checkDamage(new EnchantmentIce(), weapon, attacker, 7);
		target = mob(100_000);
		new EnchantmentIce().proc(weapon, attacker, target, 0);
		check(target.buff(Wet.class) != null && target.buff(Cold.class) != null, "寒潮附魔缺少潮湿或寒冷");
		until(new EnchantmentIce(), weapon, attacker, target, Frost.class);
		checkDamage(new EnchantmentIce2(), weapon, attacker, 2);
		until(new EnchantmentIce2(), weapon, attacker, mob(100_000), FrostIce.class);

		checkDamage(new EnchantmentLight(), weapon, attacker, 7);
		until(new EnchantmentLight(), weapon, attacker, mob(100_000), Blindness.class);
		checkDamage(new EnchantmentLight2(), weapon, attacker, 2);
		until(new EnchantmentLight2(), weapon, attacker, mob(100_000), LightShootAttack.class);

		target = mob(100_000);
		int hp = target.HP;
		for (int i = 0; i < 100 && target.HP == hp; i++) new EnchantmentShock().proc(weapon, attacker, target, 37);
		check(target.HP == hp - 7, "乱流附魔没有造成旧版重雷伤害");
		checkDamage(new EnchantmentShock2(), weapon, attacker, 2);
		until(new EnchantmentShock2(), weapon, attacker, mob(100_000), Shocked.class);
	}

	private static void testSafeBounds() {
		Dagger weapon = new Dagger();
		TestMob lowHealth = mob(1);
		check(SpsEnchantment.legacyRoll(weapon, lowHealth) == 0, "零级附魔伤害边界错误");
		weapon.level(-20);
		for (Class<?> type : CLASSES) {
			try {
				Weapon.Enchantment enchantment = (Weapon.Enchantment)type.getDeclaredConstructor().newInstance();
				enchantment.proc(weapon, lowHealth, mob(100_000), 0);
			} catch (ReflectiveOperationException exception) {
				throw new AssertionError("无法实例化附魔：" + type.getSimpleName(), exception);
			}
		}
	}

	private static void testDamageCharge() {
		TestMob attacker = mob(100);
		TestMob target = mob(100);
		attacker.pos = 9;
		target.pos = 10;
		Buff.affect(attacker, DamageUp.class).level(9);
		int hp = target.HP;
		check(attacker.attack(target), "伤害积蓄测试攻击未命中");
		check(hp - target.HP == 29, "伤害积蓄没有给下一次攻击固定增加9点伤害");
		check(attacker.buff(DamageUp.class) == null, "伤害积蓄没有在攻击后消耗");
	}

	private static void testSaveRestore() throws Exception {
		for (Class<?> type : CLASSES) {
			Dagger source = new Dagger();
			source.enchant((Weapon.Enchantment)type.getDeclaredConstructor().newInstance());
			Bundle bundle = new Bundle();
			source.storeInBundle(bundle);
			Dagger restored = new Dagger();
			restored.restoreFromBundle(bundle);
			check(restored.enchantment != null && restored.enchantment.getClass() == type,
					type.getSimpleName() + "没有随武器存档恢复");
		}
		DamageUp source = new DamageUp().level(17);
		Bundle bundle = new Bundle();
		source.storeInBundle(bundle);
		DamageUp restored = new DamageUp();
		restored.restoreFromBundle(bundle);
		check(restored.level() == 17, "伤害积蓄没有随存档恢复");
	}

	private static void testLocalization() throws Exception {
		String english = new String(Files.readAllBytes(Paths.get("messages/items/en/items.properties")), StandardCharsets.UTF_8);
		String chinese = new String(Files.readAllBytes(Paths.get("messages/items/zh/items.properties")), StandardCharsets.UTF_8);
		check(!english.contains("\uFFFD") && !chinese.contains("\uFFFD"), "附魔文本含有UTF-8替换字符");
		for (Class<?> type : CLASSES) {
			String key = "items.weapon.enchantments." + type.getSimpleName().toLowerCase() + ".";
			check(english.contains(key + "name=") && english.contains(key + "desc="), type.getSimpleName() + "缺少英文文本");
			check(chinese.contains(key + "name=") && chinese.contains(key + "desc="), type.getSimpleName() + "缺少中文文本");
		}
		check(chinese.contains("items.weapon.enchantments.enchantmentshock.name=乱流%s"), "中文附魔文本读取异常");
	}

	private static void checkDamage(Weapon.Enchantment enchantment, Dagger weapon, TestMob attacker, int expected) {
		TestMob target = mob(100_000);
		int hp = target.HP;
		check(enchantment.proc(weapon, attacker, target, 37) == 37, enchantment.getClass().getSimpleName() + "改变了主攻击伤害");
		check(hp - target.HP == expected, enchantment.getClass().getSimpleName() + "元素伤害倍率错误");
	}

	private static void until(Weapon.Enchantment enchantment, Dagger weapon, TestMob attacker,
			TestMob target, Class<? extends Buff> effect) {
		for (int i = 0; i < 200 && target.buff(effect) == null; i++) enchantment.proc(weapon, attacker, target, 0);
		check(target.buff(effect) != null, enchantment.getClass().getSimpleName() + "没有施加" + effect.getSimpleName());
	}

	private static TestMob mob(int health) { return new TestMob(health); }
	private static final class TestMob extends Mob {
		TestMob(int health) { HP = HT = health; }
		@Override public int damageRoll() { return 20; }
		@Override public int attackSkill(Char target) { return 1_000_000; }
		@Override public int drRoll() { return 0; }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(8, 8);
			mobs = new HashSet<>();
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
		@Override public Heap drop(Item item, int cell) { return null; }
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsLegacyEnchantmentTest() { }
}
