package pd.items.equipment.weapon.melee;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.Silent;
import pd.actors.hero.Hero;
import pd.actors.mobs.Brute;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Piranha;
import pd.actors.mobs.Skeleton;
import pd.items.EquipableItem;
import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.guns.GunA;
import pd.items.equipment.weapon.guns.GunB;
import pd.items.equipment.weapon.guns.GunC;
import pd.items.equipment.weapon.guns.GunD;
import pd.items.equipment.weapon.guns.GunE;
import pd.items.equipment.weapon.melee.fusion.Flute;
import pd.items.equipment.weapon.melee.fusion.Harp;
import pd.items.equipment.weapon.melee.fusion.PrayerWheel;
import pd.items.equipment.weapon.melee.fusion.Triangolo;
import pd.items.equipment.weapon.melee.fusion.Trumpet;
import pd.items.equipment.weapon.melee.fusion.WarDrum;
import pd.items.equipment.weapon.melee.fusion.WindBottle;
import pd.items.equipment.weapon.melee.normalweapon.TrickSand;
import pd.items.equipment.weapon.melee.normalweapon.WoodenStaff;
import pd.items.equipment.weapon.missiles.Javelin;
import pd.items.equipment.weapon.missiles.Kunai;
import pd.items.equipment.weapon.missiles.meleethrow.HugeShuriken;
import pd.items.equipment.weapon.missiles.meleethrow.MeleeThrowWeapon;
import pd.items.equipment.weapon.missiles.meleethrow.SmallChakram;
import pd.items.equipment.weapon.missiles.meleethrow.Tamahawk;
import pd.items.equipment.weapon.ranges.AlloyBowN;
import pd.items.equipment.weapon.ranges.AlloyBowR;
import pd.items.equipment.weapon.ranges.AlloyBowS;
import pd.items.equipment.weapon.ranges.MetalBowN;
import pd.items.equipment.weapon.ranges.MetalBowR;
import pd.items.equipment.weapon.ranges.MetalBowS;
import pd.items.equipment.weapon.ranges.PVCBowN;
import pd.items.equipment.weapon.ranges.PVCBowR;
import pd.items.equipment.weapon.ranges.PVCBowS;
import pd.items.equipment.weapon.ranges.StoneBowN;
import pd.items.equipment.weapon.ranges.StoneBowR;
import pd.items.equipment.weapon.ranges.StoneBowS;
import pd.items.equipment.weapon.ranges.WoodenBowN;
import pd.items.equipment.weapon.ranges.WoodenBowR;
import pd.items.equipment.weapon.ranges.WoodenBowS;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.plants.Plant;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

/** Runtime checks for the missing faith and melee-throw section of the legacy weapon deck. */
public final class SpsExtendedLegacyWeaponsTest {
	private static final Class<?>[] ALL_WEAPONS = {
			pd.items.equipment.weapon.melee.normalweapon.Dagger.class,
			pd.items.equipment.weapon.melee.normalweapon.Knuckles.class,
			pd.items.equipment.weapon.melee.normalweapon.ShortSword.class,
			pd.items.equipment.weapon.melee.normalweapon.MageBook.class,
			pd.items.equipment.weapon.melee.normalweapon.Handaxe.class,
			pd.items.equipment.weapon.melee.normalweapon.Spear.class,
			pd.items.equipment.weapon.melee.normalweapon.Dualknive.class,
			pd.items.equipment.weapon.melee.normalweapon.FightGloves.class,
			pd.items.equipment.weapon.melee.normalweapon.Nunchakus.class,
			pd.items.equipment.weapon.melee.normalweapon.Scimitar.class,
			pd.items.equipment.weapon.melee.normalweapon.Whip.class,
			pd.items.equipment.weapon.melee.normalweapon.Rapier.class,
			pd.items.equipment.weapon.melee.normalweapon.AssassinsBlade.class,
			pd.items.equipment.weapon.melee.normalweapon.BattleAxe.class,
			pd.items.equipment.weapon.melee.normalweapon.Glaive.class,
			pd.items.equipment.weapon.melee.normalweapon.Club.class,
			pd.items.equipment.weapon.melee.normalweapon.Gsword.class,
			pd.items.equipment.weapon.melee.normalweapon.Halberd.class,
			pd.items.equipment.weapon.melee.normalweapon.WarHammer.class,
			pd.items.equipment.weapon.melee.normalweapon.Lance.class,
			Triangolo.class, Flute.class, WarDrum.class, Trumpet.class, Harp.class,
			WoodenStaff.class, Mace.class, HolyWater.class, PrayerWheel.class, StoneCross.class,
			TrickSand.class, MirrorDoll.class, WindBottle.class, HandLight.class, CurseBox.class,
			Kunai.class, SmallChakram.class, Javelin.class, HugeShuriken.class, Tamahawk.class,
			WoodenBowN.class, WoodenBowS.class, WoodenBowR.class, GunA.class,
			StoneBowN.class, StoneBowS.class, StoneBowR.class, GunB.class,
			MetalBowN.class, MetalBowS.class, MetalBowR.class, GunC.class,
			AlloyBowN.class, AlloyBowS.class, AlloyBowR.class, GunD.class,
			PVCBowN.class, PVCBowS.class, PVCBowR.class, GunE.class,
			pd.items.equipment.weapon.melee.special.TrinityForce.class
	};
	private static final int[][] ICON_CELLS = {
			{96, 944}, {112, 944}, {128, 944}, {144, 944},
			{160, 944}, {176, 944}, {192, 944}
	};
	private static final String[] ICON_HASHES = {
			"66DBD8A8C08B536C93CCBBF369E4FF6557C3EC392843B472A322CFE10321AF77",
			"E2B6D33A816EAFDBD96E5E439C8E278ED4013249F95F22F9160741FF6EB431A7",
			"5B697C3C434139D977B521A5D44F3A8625D839BC875630E08FEAAC44FF4B4656",
			"639EE4DE15CA01D8B75047B53126A5DE1F4C7834D01C27B91FD86151D518827B",
			"84C345E551A1208420A87751C13A43AE240FDBE4A260A79E4BBBA834A2F55559",
			"7580F7593A6C9ED7193BE9E1906121F58DC0548E64C70DA635DEB0CEDD80AAB1",
			"C2254E3C8E6BECF1B20E2F4563A477D9374A966DF2708A29EDE33D3B2C8C921C"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5350534558545745L);
		try {
			testStoneCross();
			testMagicBreakWeapons();
			testMeleeThrowWeapons();
			testLegacyWeaponDecks();
			testSources();
			testIconsAndMessages();
			System.out.println("SPS扩展旧版武器测试通过：十碑、三件破法武器和三件近战投掷武器的数值、效果、来源、存档、双语文本与原始图标均正常。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
		}
	}

	private static void testLegacyWeaponDecks() throws Exception {
		check(Generator.Category.WEAPON.superClass == Weapon.class, "旧版总武器池类型不是Weapon");
		//SPSEXPD: 旧版 60 件之后追加了三相之力（原创武器，追加在末尾不影响前 40/20 项双抽池）
		check(Generator.Category.WEAPON.classes.length == 61, "旧版总武器池不是61件");
		check(Generator.Category.MELEEWEAPON.classes.length == 40, "旧版近战双抽池不是40件");
		check(Generator.Category.OLDWEAPON.classes.length == 20, "旧版基础武器池不是20件");
		for (int i = 0; i < ALL_WEAPONS.length; i++) {
			check(Generator.Category.WEAPON.classes[i] == ALL_WEAPONS[i], "旧版总武器池顺序错误：" + i);
			check(Generator.Category.WEAPON.probs[i] == 1f, "旧版总武器池权重错误：" + i);
			check(ALL_WEAPONS[i].getDeclaredConstructor().newInstance() instanceof Weapon,
					"旧版武器无法实例化为Weapon：" + ALL_WEAPONS[i].getSimpleName());
			if (i < 40) {
				check(Generator.Category.MELEEWEAPON.classes[i] == ALL_WEAPONS[i], "旧版近战双抽池顺序错误：" + i);
				check(Generator.Category.MELEEWEAPON.probs[i] == 1f, "旧版近战双抽池权重错误：" + i);
			}
			if (i < 20) {
				check(Generator.Category.OLDWEAPON.classes[i] == ALL_WEAPONS[i], "旧版基础武器池顺序错误：" + i);
				check(Generator.Category.OLDWEAPON.probs[i] == 1f, "旧版基础武器池权重错误：" + i);
			}
		}

		float[] allWeights = Generator.Category.WEAPON.probs.clone();
		float[] meleeWeights = Generator.Category.MELEEWEAPON.probs.clone();
		try {
			Arrays.fill(Generator.Category.WEAPON.probs, 0f);
			Generator.Category.WEAPON.probs[40] = 1f;
			check(Generator.random(Generator.Category.WEAPON) instanceof WoodenBowN,
					"总武器池仍错误地转入近战双抽流程");

			Arrays.fill(Generator.Category.MELEEWEAPON.probs, 0f);
			Generator.Category.MELEEWEAPON.probs[39] = 1f;
			check(Generator.randomWeaponForStrength(18) instanceof Tamahawk,
					"近战双抽池没有使用第40件旧版武器");
		} finally {
			System.arraycopy(allWeights, 0, Generator.Category.WEAPON.probs, 0, allWeights.length);
			System.arraycopy(meleeWeights, 0, Generator.Category.MELEEWEAPON.probs, 0, meleeWeights.length);
		}
	}

	private static void testStoneCross() {
		level();
		Hero hero = hero(27);
		StoneCross cross = new StoneCross();
		check(cross.tier == 5 && cross.min(0) == 50 && cross.max(0) == 66
				&& cross.min(3) == 53 && cross.max(3) == 78 && cross.STRReq(0) == 18,
				"十碑基础数值或强化成长错误");
		PlainMob target = mob(28);
		for (int i = 0; i < StoneCross.FULL_CHARGE; i++) cross.proc(hero, target, 1);
		check(cross.charge() == 20, "十碑没有积蓄20次攻击");
		int chargedDamage = cross.damageRoll(hero);
		check(chargedDamage >= 250 && chargedDamage <= 330, "十碑满充没有造成五倍伤害：" + chargedDamage);
		Bundle saved = new Bundle();
		cross.storeInBundle(saved);
		StoneCross restored = new StoneCross();
		restored.restoreFromBundle(saved);
		check(restored.charge() == 20 && new StoneCross().charge() == 0, "十碑充能存档或实例隔离失败");
		restored.proc(hero, target, 1);
		check(restored.charge() == 1, "十碑满充攻击后没有重置计数");
	}

	private static void testMagicBreakWeapons() {
		MirrorDoll mirror = new MirrorDoll();
		HandLight light = new HandLight();
		CurseBox box = new CurseBox();
		check(mirror.tier == 2 && mirror.min(0) == 12 && mirror.max(0) == 17
				&& mirror.min(3) == 18 && mirror.max(3) == 26 && mirror.STRReq(0) == 12,
				"持镜人偶数值错误");
		check(light.tier == 4 && light.min(0) == 29 && light.max(0) == 38
				&& light.min(3) == 35 && light.max(3) == 50 && light.STRReq(0) == 16,
				"神拳灯数值错误");
		check(box.tier == 5 && box.min(0) == 40 && box.max(0) == 50
				&& box.min(3) == 46 && box.max(3) == 62 && box.STRReq(0) == 18,
				"魔箱数值错误");
		checkMagicBreak(mirror, 5f, 30);
		checkMagicBreak(light, 3f, 31);
		checkMagicBreak(box, 2f, 32);
	}

	private static void checkMagicBreak(MeleeWeapon weapon, float duration, int pos) {
		PlainMob target = mob(pos);
		Buff.affect(target, ShieldArmor.class).level(10);
		int before = target.HP;
		weapon.proc(Dungeon.hero, target, 20);
		check(target.HP < before && target.buff(Silent.class) != null
				&& target.buff(Silent.class).cooldown() == duration,
				weapon.getClass().getSimpleName() + "没有破盾或施加正确时长的沉默");
		before = target.HP;
		weapon.proc(Dungeon.hero, target, 20);
		check(target.HP <= before - 10, weapon.getClass().getSimpleName() + "没有对已沉默目标追加半额伤害");
	}

	private static void testMeleeThrowWeapons() {
		Hero hero = Dungeon.hero;
		MeleeThrowWeapon[] weapons = {new SmallChakram(), new HugeShuriken(), new Tamahawk()};
		int[][] stats = {{2, 11, 23, 17, 32, 12}, {4, 38, 52, 44, 67, 16}, {5, 53, 68, 59, 86, 18}};
		for (int i = 0; i < weapons.length; i++) {
			MeleeThrowWeapon weapon = weapons[i];
			check(weapon.tier == stats[i][0] && weapon.min(0) == stats[i][1] && weapon.max(0) == stats[i][2]
					&& weapon.min(3) == stats[i][3] && weapon.max(3) == stats[i][4]
					&& weapon.STRReq(0) == stats[i][5], weapon.getClass().getSimpleName() + "数值错误");
			check(weapon.actions(hero).contains(Item.AC_THROW)
					&& weapon.actions(hero).contains(EquipableItem.AC_EQUIP)
					&& weapon.defaultQuantity() == 1 && weapon.isUpgradable(),
					weapon.getClass().getSimpleName() + "缺少装备、投掷或回收规则");
		}
	}

	private static void testSources() throws Exception {
		check(new Skeleton().SupercreateLoot() instanceof StoneCross, "骷髅特殊掉落不是十碑");
		check(new Brute().SupercreateLoot() instanceof Tamahawk, "豺狼暴徒特殊掉落不是飞斧");
		check(new Piranha().SupercreateLoot() instanceof HugeShuriken, "食人鱼特殊掉落不是巨型手里剑");
		String tengu = Files.readString(Paths.get("../java/pd/actors/mobs/SpsTengu.java"), StandardCharsets.UTF_8);
		check(tengu.contains("new HugeShuriken()"), "SPS天狗普通首领奖励没有接入巨型手里剑");
	}

	private static void testIconsAndMessages() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet != null && sheet.getWidth() == 256 && sheet.getHeight() == 992, "物品图集尺寸错误");
		for (int i = 0; i < ICON_CELLS.length; i++) {
			ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
			for (int y = ICON_CELLS[i][1]; y < ICON_CELLS[i][1] + 16; y++) {
				for (int x = ICON_CELLS[i][0]; x < ICON_CELLS[i][0] + 16; x++) pixels.putInt(sheet.getRGB(x, y));
			}
			check(ICON_HASHES[i].equals(hex(MessageDigest.getInstance("SHA-256").digest(pixels.array()))),
					"第" + (i + 1) + "件扩展旧版武器图标错误");
		}
		check(new StoneCross().image == SpecificPlaceHolderDict.SOMETHING_0
				&& new MirrorDoll().image == EquipmentEquipWeaponBasicWeaponDict.MIRROR_DOLL
				&& new HandLight().image == SpecificPlaceHolderDict.SOMETHING_0
				&& new CurseBox().image == SpecificPlaceHolderDict.SOMETHING_0
				&& new SmallChakram().image == SpecificPlaceHolderDict.SOMETHING_0
				&& new HugeShuriken().image == SpecificPlaceHolderDict.SOMETHING_0
				&& new Tamahawk().image == SpecificPlaceHolderDict.SOMETHING_0, "扩展旧版武器图标槽绑定错误");
		String zh = Files.readString(Paths.get("messages/items/zh/items.properties"), StandardCharsets.UTF_8);
		String en = Files.readString(Paths.get("messages/items/en/items.properties"), StandardCharsets.UTF_8);
		check(!zh.contains("\uFFFD") && !en.contains("\uFFFD"), "扩展旧版武器文本含UTF-8替换字符");
		for (String key : Arrays.asList("stonecross", "mirrordoll", "handlight", "cursebox")) {
			check(zh.contains("items.equipment.weapon.melee." + key + ".name=")
					&& en.contains("items.equipment.weapon.melee." + key + ".name="), "破法武器缺少双语键：" + key);
		}
		for (String key : Arrays.asList("smallchakram", "hugeshuriken", "tamahawk")) {
			check(zh.contains("items.equipment.weapon.missiles.meleethrow." + key + ".name=")
					&& en.contains("items.equipment.weapon.missiles.meleethrow." + key + ".name="), "近战投掷武器缺少双语键：" + key);
		}
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

	private static PlainMob mob(int pos) {
		PlainMob mob = new PlainMob();
		mob.pos = pos;
		mob.HP = mob.HT = 1_000;
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

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(8, 8);
			mobs().clear(); heaps = new SparseArray<>(); blobs = new HashMap<>();
			plants = new SparseArray<Plant>(); traps = new SparseArray<>(); transitions = new ArrayList<>();
			customTiles = new ArrayList<>(); customTerrain = new ArrayList<>(); customWalls = new ArrayList<>();
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

	private SpsExtendedLegacyWeaponsTest() { }
}
