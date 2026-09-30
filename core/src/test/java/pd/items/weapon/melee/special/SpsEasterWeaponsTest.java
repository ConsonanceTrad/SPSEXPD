package pd.items.weapon.melee.special;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Dry;
import pd.actors.buffs.HolyStun;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.STRDown;
import pd.actors.buffs.Tar;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.YearBeast;
import pd.actors.mobs.npcs.TownNpc;
import pd.effects.Pushing;
import pd.items.EquipableItem;
import pd.items.Generator;
import pd.items.Item;
import pd.items.weapon.guns.ToyGun;
import pd.items.weapon.missiles.fusion.RocketMissile;
import pd.items.weapon.missiles.meleethrow.Brick;
import pd.items.weapon.missiles.meleethrow.DragonBoat;
import pd.items.weapon.missiles.meleethrow.MeleeThrowWeapon;
import pd.items.weapon.missiles.meleethrow.MiniMoai;
import pd.items.weapon.missiles.meleethrow.Tree;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

/** Runtime checks for SPS-PD 0.9.8's complete fifteen-item event weapon pool. */
public final class SpsEasterWeaponsTest {

	private static final Class<?>[] POOL = {
			Pumpkin.class, Tree.class, MiniMoai.class, TestWeapon.class, ToyGun.class,
			HookHam.class, Brick.class, Lollipop.class, FireCracker.class, SJRBMusic.class,
			RocketMissile.class, KeyWeapon.class, DragonBoat.class, PaperFan.class, MeleePan.class
	};
	private static final String[] ICON_HASHES = {
			"2F7DBCDD0EEBFE490D932829F06EB8B5454B37D0FF42A50194E6908A2761A3C2",
			"9837677E960B416925FB8B0D4D2C7BD104E392DEBDAAE555B0B7A298FBC20050",
			"5E9F7E3F7DE61A85584EE7F37DAE1AD954B71E147A149035482E93469B7D3D94",
			"D9FCD5ECAF5F8E7503946A890D01451C342CE606D4A80E93E5FF596FA774B986",
			"435783692D4D44537938B96D62CE7602E431CC210E91BC8BFE800449932D25CF",
			"08E1D51F06D560C83F72ED606B8876CC777E2EF031E63BEFB63B5B72070EB3DF",
			"2191D033DE7214F86D9813EDB9A16457A206A9D37E27AF8F26340B4FF01EE2C5",
			"B7AED8C96A421E90AE14D7809FE8B5E9D0766EBEBBA624E6807E1D9E3EB640D2"
	};
	private static final int[][] ICON_CELLS = {
			{208,928}, {224,928}, {240,928}, {0,944},
			{16,944}, {32,944}, {112,848}, {176,928}
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5350534541535445L);
		try {
			testPoolAndSources();
			testStatsAndActions();
			testMeleeEffectsAndPersistence();
			testFireCrackerAndMusic();
			testThrownEffectsAndSafety();
			testIcons();
			System.out.println("SPS节日武器池通过：15件等权武器、8件补回武器的数值、特效、投掷、存档、边界和原始图标均正常。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
		}
	}

	private static void testPoolAndSources() {
		check(Arrays.equals(Generator.Category.EASTERWEAPON.classes, POOL), "节日武器池顺序或成员错误");
		for (float probability : Generator.Category.EASTERWEAPON.probs) check(probability == 1f, "节日武器池不是等权");
		Set<Class<?>> generated = new HashSet<>();
		for (int i = 0; i < 10_000; i++) generated.add(Generator.random(Generator.Category.EASTERWEAPON).getClass());
		check(generated.size() == POOL.length, "节日武器池无法生成全部15件物品");
		TownNpc npc = new TownNpc().configure(TownNpc.Spec.OLD_NEW_STWIST);
		check(Arrays.asList(POOL).contains(npc.SupercreateLoot().getClass()), "城镇居民没有使用完整节日武器池");
	}

	private static void testStatsAndActions() {
		Hero hero = hero(27);
		SpsSpecialMeleeWeapon[] melee = {new HookHam(), new KeyWeapon(), new Lollipop(), new PaperFan()};
		int[][] meleeStats = {{1,5,4,8,10}, {1,10,4,13,10}, {50,50,53,53,10}, {1,15,4,21,12}};
		for (int i = 0; i < melee.length; i++) {
			check(melee[i].min(0) == meleeStats[i][0] && melee[i].max(0) == meleeStats[i][1]
					&& melee[i].min(3) == meleeStats[i][2] && melee[i].max(3) == meleeStats[i][3]
					&& melee[i].STRReq(0) == meleeStats[i][4], melee[i].getClass().getSimpleName() + "数值错误");
		}
		MeleeThrowWeapon[] thrown = {new Tree(), new MiniMoai(), new Brick(), new DragonBoat()};
		int[][] thrownStats = {{1,5,7,11}, {10,10,16,16}, {8,8,14,14}, {5,10,11,16}};
		for (int i = 0; i < thrown.length; i++) {
			check(thrown[i].min(0) == thrownStats[i][0] && thrown[i].max(0) == thrownStats[i][1]
					&& thrown[i].min(3) == thrownStats[i][2] && thrown[i].max(3) == thrownStats[i][3]
					&& thrown[i].STRReq(0) == 10, thrown[i].getClass().getSimpleName() + "数值错误");
			check(thrown[i].actions(hero).contains(Item.AC_THROW)
					&& thrown[i].actions(hero).contains(EquipableItem.AC_EQUIP), thrown[i].getClass().getSimpleName() + "缺少装备或投掷动作");
			check(thrown[i].defaultQuantity() == 1 && thrown[i].isUpgradable(), "旧版投掷武器数量或强化规则错误");
		}
		check(new MiniMoai().value() == 100, "迷你石雕售价错误");
	}

	private static void testMeleeEffectsAndPersistence() {
		level();
		Hero hero = hero(27);
		PlainMob target = mob(28, 100_000);

		HookHam hook = new HookHam();
		hero.HP = 95;
		for (int i = 0; i < 1_000 && hero.HP < hero.HT; i++) hook.proc(hero, target, 10);
		check(hero.HP == hero.HT, "钩子和火腿没有治疗或治疗越过上限");

		KeyWeapon key = new KeyWeapon();
		for (int i = 0; i < 2_000 && (target.buff(Paralysis.class) == null
				|| target.buff(Charm.class) == null || target.buff(Terror.class) == null
				|| target.buff(Amok.class) == null); i++) key.proc(hero, target, 10);
		check(target.buff(Paralysis.class) != null && target.buff(Charm.class) != null
				&& target.buff(Terror.class) != null && target.buff(Amok.class) != null,
				"钥匙武器没有恢复三组独立特效");

		PaperFan fan = new PaperFan();
		for (int i = 0; i < 3; i++) fan.proc(hero, target, 1);
		Bundle bundle = new Bundle();
		fan.storeInBundle(bundle);
		PaperFan restored = new PaperFan();
		restored.restoreFromBundle(bundle);
		check(restored.charge() == 3, "纸扇蓄力没有随实例存档");
		for (int i = 0; i < 6; i++) restored.proc(hero, target, 1);
		check(restored.charge() == 0 && target.buff(Vertigo.class) != null, "纸扇第九击没有释放强风");
		check(fan.charge() == 3, "两个纸扇实例错误共享蓄力");

		Lollipop lollipop = new Lollipop();
		hero.HP = hero.HT = 100;
		hero.HTBoost = 50;
		hero.belongings.weapon = lollipop;
		for (int i = 0; i < 10_000 && hero.belongings.weapon == lollipop; i++) lollipop.proc(hero, target, 50);
		check(hero.belongings.weapon == null && hero.HT == 85 && hero.HP == 85 && hero.HTBoost == 35,
				"棒棒糖破坏没有永久扣除生命或移除武器");
		check(hero.buff(HolyStun.class) != null && hero.buff(STRDown.class) != null
				&& target.buff(Tar.class) != null && target.buff(Charm.class) != null,
				"棒棒糖状态效果错误");
	}

	private static void testFireCrackerAndMusic() {
		TestLevel fireLevel = level();
		FixedDamageHero hero = fixedHero(18, 17);
		PlainMob target = mob(19, 100_000);
		PlainMob splash = mob(27, 100_000);
		CountingMob listener = new CountingMob();
		listener.pos = 45;
		listener.HP = listener.HT = 100_000;
		Actor.add(listener);
		fireLevel.mobs().add(listener);
		FireCracker cracker = new FireCracker();
		int splashHp = splash.HP;
		for (int i = 0; i < 1_000 && (listener.beckons == 0
				|| target.buff(Terror.class) == null || splash.HP == splashHp); i++) {
			cracker.proc(hero, target, 0);
		}
		check(listener.beckons > 0, "爆竹没有召来本层怪物");
		check(target.buff(Terror.class) != null && target.buff(Terror.class).object == hero.id(),
				"爆竹没有施加来源正确的恐惧");
		check(splash.HP < splashHp, "爆竹没有造成九宫格爆炸伤害");
		TrackingYearBeast beast = new TrackingYearBeast();
		beast.pos = 20;
		beast.HP = beast.HT = 100_000;
		Actor.add(beast);
		cracker.proc(hero, beast, 0);
		check(beast.receivedOne, "爆竹没有对年兽追加固定伤害");

		level();
		hero = fixedHero(9, 17);
		FixedDamageMob attacker = new FixedDamageMob();
		attacker.pos = 27;
		attacker.HP = attacker.HT = 100;
		Actor.add(attacker);
		PlainMob defender = mob(29, 100);
		PlainMob neighbour = mob(21, 100);
		new SJRBMusic().proc(attacker, defender, 0);
		check(attacker.pos == 28, "鸡乐器没有把两格外的攻击者推进一格");
		check(defender.HP == 83 && neighbour.HP == 83,
				"鸡乐器没有按英雄伤害骰造成目标追加伤害和邻格震波");
		boolean delayedPush = false;
		for (Actor actor : Actor.all()) {
			if (actor instanceof Pushing && Math.abs(actor.cooldown() + 1f) < 0.001f) delayedPush = true;
		}
		check(delayedPush, "鸡乐器突进没有按旧版延迟时序加入动画");
	}

	private static void testThrownEffectsAndSafety() {
		level();
		Hero hero = hero(27);
		PlainMob target = mob(28, 100_000);
		PlainMob splash = mob(36, 100_000);
		int oldHp = splash.HP;
		Tree tree = new Tree();
		for (int i = 0; i < 1_000 && target.buff(Dry.class) == null; i++) tree.proc(hero, target, 1);
		check(target.buff(Dry.class) != null && splash.HP < oldHp, "圣诞树状态或邻格溅射错误");
		target.pos = 0;
		tree.proc(hero, target, 0);

		MiniMoai moai = new MiniMoai();
		for (int i = 0; i < 1_000 && target.buff(Charm.class) == null; i++) moai.proc(hero, target, 1);
		check(target.buff(Charm.class) != null, "迷你石雕没有魅惑目标");

		Brick brick = new Brick();
		for (int i = 0; i < 10_000 && !brick.destroyed(); i++) brick.proc(hero, target, 8);
		check(brick.destroyed() && target.buff(HolyStun.class) != null, "砖头没有按旧版破坏或眩晕");
		DragonBoat boat = new DragonBoat();
		for (int i = 0; i < 10_000 && !boat.destroyed(); i++) boat.proc(hero, target, 8);
		check(boat.destroyed() && target.buff(Paralysis.class) != null, "龙舟没有按旧版破坏或麻痹");
	}

	private static void testIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet != null && sheet.getWidth() == 256 && sheet.getHeight() == 992, "物品图集尺寸错误");
		for (int i = 0; i < ICON_HASHES.length; i++) {
			ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
			for (int y = ICON_CELLS[i][1]; y < ICON_CELLS[i][1] + 16; y++) {
				for (int x = ICON_CELLS[i][0]; x < ICON_CELLS[i][0] + 16; x++) pixels.putInt(sheet.getRGB(x, y));
			}
			check(ICON_HASHES[i].equals(hex(MessageDigest.getInstance("SHA-256").digest(pixels.array()))),
					"第" + (i + 1) + "件补回节日武器图标错误");
		}
		check(new HookHam().image == ItemSpriteSheet.SPS_HOOK_HAM
				&& new KeyWeapon().image == ItemSpriteSheet.SPS_KEY_WEAPON
				&& new DragonBoat().image == ItemSpriteSheet.SPS_DRAGON_BOAT, "节日武器图标槽绑定错误");
	}

	private static TestLevel level() {
		Actor.clear();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Arrays.fill(level.map, Terrain.EMPTY);
		Arrays.fill(level.passable, true);
		level.buildFlagMaps();
		return level;
	}

	private static Hero hero(int pos) {
		Hero hero = new Hero();
		hero.pos = pos;
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static FixedDamageHero fixedHero(int pos, int damage) {
		FixedDamageHero hero = new FixedDamageHero(damage);
		hero.pos = pos;
		hero.HP = hero.HT = 100_000;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static PlainMob mob(int pos, int health) {
		PlainMob mob = new PlainMob();
		mob.pos = pos;
		mob.HP = mob.HT = health;
		Actor.add(mob);
		return mob;
	}

	private static String hex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static final class PlainMob extends Mob {
		@Override public int damageRoll() { return 20; }
		@Override public int drRoll() { return 0; }
	}

	private static final class FixedDamageHero extends Hero {
		private final int damage;
		FixedDamageHero(int damage) { this.damage = damage; }
		@Override public int damageRoll() { return damage; }
	}

	private static final class FixedDamageMob extends Mob {
		@Override public int damageRoll() { return 3; }
		@Override public int drRoll() { return 0; }
	}

	private static final class CountingMob extends Mob {
		int beckons;
		@Override public void beckon(int cell) { beckons++; }
		@Override public int drRoll() { return 0; }
	}

	private static final class TrackingYearBeast extends YearBeast {
		boolean receivedOne;
		@Override public void damage(int damage, Object source) {
			if (source instanceof FireCracker && damage == 1) receivedOne = true;
			super.damage(damage, source);
		}
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(8, 8);
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
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsEasterWeaponsTest() { }
}
