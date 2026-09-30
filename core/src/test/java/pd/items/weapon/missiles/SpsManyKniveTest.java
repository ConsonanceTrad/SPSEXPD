package pd.items.weapon.missiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HolyStun;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Heap;
import pd.items.Item;
import pd.items.weapon.missiles.throwing.EscapeKnive;
import pd.items.weapon.spammo.FireAmmo;
import pd.items.weapon.spammo.HeavyAmmo;
import pd.levels.Level;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.imageio.ImageIO;

/** Headless checks for the reusable knife set, escape knives, and unbreakable stun. */
public final class SpsManyKniveTest {
	private static final String MANY_HASH = "899F3FCEF89D29AD87D6C4E4348E879E326BDEC97691519AE022630E166360A9";
	private static final String KNIFE_HASH = "6A9221A1CD42F7CF3ED1D6DBF40F014AAAC33EF15AAA7222DC51EE70E0A0EEF4";

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5350534B4E495645L);
		try {
			testKnifeSet();
			testAmmoAndSave();
			testEscapeKnifeAndHolyStun();
			testRareDropBound();
			testLegacyIcons();
			System.out.println("SPS千支刀测试通过：数值、无限投掷、涂油、存档、逃脱小刀、护盾打击、稀有掉落和旧版图标均正常。");
		} finally {
			Random.popGenerator();
		}
	}

	private static void testKnifeSet() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		ManyKnive knives = new ManyKnive();
		check(knives.min(0) == 2 && knives.max(0) == 4 && knives.STRReq(0) == 10,
				"千支刀基础数值错误");
		check(knives.min(4) == 6 && knives.max(4) == 8 && knives.STRReq(4) == 10,
				"千支刀强化成长或固定力量需求错误");
		check(knives.unique && knives.isReinforced() && knives.isIdentified() && knives.isUpgradable(),
				"千支刀唯一、强化、鉴定或升级属性错误");
		check(knives.image == ItemSpriteSheet.MANY_KNIVE, "千支刀图标槽错误");
		check(knives.actions(hero).contains(ManyKnive.AC_SHOOT)
				&& knives.actions(hero).contains(ManyKnive.AC_AMMO)
				&& !knives.actions(hero).contains(Item.AC_DROP)
				&& !knives.actions(hero).contains(Item.AC_THROW), "千支刀背包动作错误");
		ManyKnive.KniveAmmo projectile = knives.new KniveAmmo();
		check(projectile.spawnedForEffect && projectile.image == ItemSpriteSheet.LEGACY_KNIFE,
				"千支刀投射物不是无限临时飞刀");
		check(Math.abs(projectile.DLY - 0.25f) < 0.00001f, "千支刀投掷耗时不是0.25回合");
	}

	private static void testAmmoAndSave() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		ManyKnive knives = new ManyKnive();
		knives.level(3);
		knives.loadAmmo(new HeavyAmmo());
		TestMob target = new TestMob(100);
		knives.new KniveAmmo().proc(hero, target, 20);
		check(target.HP == 85, "千支刀没有触发特殊弹药效果");

		Bundle bundle = new Bundle();
		knives.storeInBundle(bundle);
		ManyKnive restored = new ManyKnive();
		restored.restoreFromBundle(bundle);
		check(restored.level() == 3 && restored.loadedAmmo() instanceof HeavyAmmo,
				"千支刀等级或涂油没有随存档恢复");

		FireAmmo fire = new FireAmmo();
		hero.belongings.backpack.items.add(fire);
		check(restored.loadAmmoFromBackpack(hero, fire) && restored.loadedAmmo() instanceof FireAmmo,
				"千支刀不能消耗并替换背包中的特殊弹药");
		check(!hero.belongings.backpack.contains(fire), "千支刀涂油后背包仍保留弹药");
	}

	private static void testEscapeKnifeAndHolyStun() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		EscapeKnive knife = new EscapeKnive(5);
		check(knife.min(0) == 5 && knife.max(0) == 10 && knife.STRReq(0) == 10,
				"逃脱小刀基础数值错误");
		check(knife.quantity() == 5 && !knife.isUpgradable() && knife.isIdentified()
				&& knife.value() == 50 && Math.abs(knife.DLY - 0.5f) < 0.00001f,
				"逃脱小刀数量、价值或速度错误");
		TestMob target = new TestMob(100);
		knife.proc(hero, target, 7);
		HolyStun stun = target.buff(HolyStun.class);
		check(stun != null && target.paralysed == 1, "逃脱小刀没有施加护盾打击");
		target.damage(10, hero);
		check(target.buff(HolyStun.class) == stun && target.paralysed == 1,
				"护盾打击错误地被受伤提前解除");
		stun.detach();
		check(target.paralysed == 0, "护盾打击结束后没有解除麻痹计数");
	}

	private static void testRareDropBound() {
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		ManyKnive knives = new ManyKnive();
		TestMob target = new TestMob(100);
		target.pos = 27;
		int successes = 0;
		for (int i = 0; i < 1000; i++) if (knives.maybeDropEscapeKnife(target)) successes++;
		check(successes == level.escapeDrops && successes > 0 && successes < 100,
				"千支刀的1/50逃脱小刀掉落没有生效或概率边界异常");
	}

	private static void testLegacyIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(MANY_HASH.equals(hashSlot(sheet, 14)) && KNIFE_HASH.equals(hashSlot(sheet, 15)),
				"千支刀或飞刀图标与旧版像素不一致");
	}

	private static String hashSlot(BufferedImage sheet, int slot) throws Exception {
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = 624; y < 640; y++) {
			for (int x = slot * 16; x < (slot + 1) * 16; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		byte[] hash = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder result = new StringBuilder(hash.length * 2);
		for (byte value : hash) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static final class TestMob extends Mob {
		TestMob(int health) { HP = HT = health; }
		@Override public int damageRoll() { return 1; }
		@Override public int attackSkill(Char target) { return 1; }
		@Override public int drRoll() { return 0; }
	}

	private static final class TestLevel extends Level {
		int escapeDrops;
		TestLevel() {
			setSize(8, 8);
			mobs().clear();
			heaps = new render.utils.data.SparseArray<>();
			blobs = new HashMap<>();
			plants = new render.utils.data.SparseArray<Plant>();
			traps = new render.utils.data.SparseArray<>();
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
			if (item instanceof EscapeKnive) escapeDrops++;
			return null;
		}
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsManyKniveTest() { }
}
