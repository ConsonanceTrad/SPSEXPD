package pd.items.summon;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.weapon.guns.GunA;
import pd.items.weapon.missiles.ShootGun;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import render.utils.data.SparseArray;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

public final class SpsRustybladeCatTest {

	private static final String ICON_HASH =
			"08B3E21DDD83CAA56F381002B13CE88B6CDBE5A57CDFE523EF7716398F83BC0D";

	public static void main(String[] args) throws Exception {
		int oldDepth = Dungeon.depth;
		try {
			testItemAndNpcSource();
			testNormalCatSupport();
			testLeaderCatSupportAndCombat();
			testPlacementAndIcon();
			System.out.println("SPS零式呼机测试通过：普通与领袖黄油猫、枪械补弹、充能、护盾、战斗和原始图标均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
			Dungeon.depth = oldDepth;
		}
	}

	private static void testItemAndNpcSource() {
		TownNpc rusty = new TownNpc().configure(TownNpc.Spec.RUSTYBLADE);
		RustybladeCat pager = (RustybladeCat)rusty.SupercreateLoot();
		pager.quantity(3);
		check(rusty.properties().contains(Char.Property.HUMAN), "Rustyblade缺少人类属性");
		check(pager.stackable && pager.value() == 300 && !pager.isUpgradable() && pager.isIdentified()
				&& pager.image == ItemSpriteSheet.RUSTY_CAT && pager.defaultAction().equals("ACTIVE"),
				"零式呼机基础属性、售价、动作或图标错误");
	}

	private static void testNormalCatSupport() {
		Actor.clear();
		level();
		Hero hero = hero(27, HeroSubClass.NONE);
		GunA gun = new GunA();
		gun.charge(0);
		ShootGun shootGun = new ShootGun();
		shootGun.charge(0);
		hero.belongings.backpack.items.add(gun);
		hero.belongings.backpack.items.add(shootGun);
		RustybladeCat.ButterCat cat = new RustybladeCat.ButterCat();
		cat.pos = 28;
		cat.supportHero();
		check(gun.charge() == 1 && shootGun.charge() == 1, "普通黄油猫没有给两类枪械各补1发");
		check(hero.buff(Recharging.class) != null && cat.HT == 200 && cat.defenseSkill(hero) == 0
				&& cat.alignment == Char.Alignment.ALLY && cat.properties().contains(Char.Property.BEAST),
				"普通黄油猫充能、生命、防御或阵营错误");
	}

	private static void testLeaderCatSupportAndCombat() {
		Actor.clear();
		level();
		Dungeon.depth = 10;
		Hero hero = hero(27, HeroSubClass.LEADER);
		RustybladeCat pager = new RustybladeCat();
		Mob summoned = pager.summonAt(28);
		check(summoned instanceof RustybladeCat.ButterCat2, "领袖没有召唤黄油猫2.0");
		RustybladeCat.ButterCat2 cat = (RustybladeCat.ButterCat2)summoned;
		cat.supportHero();
		check(hero.buff(Recharging.class) != null && hero.buff(ShieldArmor.class) != null
				&& hero.buff(ShieldArmor.class).level() == 30, "领袖黄油猫没有提供充能和30点护盾");
		check(cat.HT == 400 && cat.defenseSkill == 20 && cat.attackSkill(hero) == 70,
				"领袖黄油猫生命、防御或命中成长错误: " + cat.HT + "/"
						+ cat.defenseSkill + "/" + cat.attackSkill(hero));
		for (int i = 0; i < 20; i++) {
			int damage = cat.damageRoll();
			check(damage >= 22 && damage <= 35, "领袖黄油猫伤害超出深度成长区间");
		}
	}

	private static void testPlacementAndIcon() throws Exception {
		Actor.clear();
		level();
		hero(27, HeroSubClass.NONE);
		PlainMob blocker = new PlainMob();
		blocker.pos = 28;
		Actor.add(blocker);
		RustybladeCat pager = new RustybladeCat();
		int destination = pager.openDestination(28);
		check(destination >= 0 && destination != 28 && Dungeon.level.insideMap(destination),
				"零式呼机没有避开被占据位置或地图边界");

		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = 608; y < 624; y++) for (int x = 64; x < 80; x++) pixels.putInt(sheet.getRGB(x, y));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder actual = new StringBuilder(64);
		for (byte value : digest) actual.append(String.format("%02X", value & 0xFF));
		check(ICON_HASH.equals(actual.toString()), "零式呼机图标与旧版像素不一致：" + actual);
	}

	private static TestLevel level() {
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Arrays.fill(level.map, Terrain.EMPTY);
		Arrays.fill(level.passable, true);
		return level;
	}

	private static Hero hero(int pos, HeroSubClass subClass) {
		Hero hero = new Hero();
		hero.pos = pos;
		hero.HP = hero.HT = 100;
		hero.subClass = subClass;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static final class PlainMob extends Mob {
		{ alignment = Alignment.ENEMY; }
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
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsRustybladeCatTest() { }
}
