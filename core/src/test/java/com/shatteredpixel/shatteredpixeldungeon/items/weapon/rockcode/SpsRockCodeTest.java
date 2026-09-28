package com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnergyArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostIce;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.MiniBomb;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import com.watabou.utils.SparseArray;

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

public final class SpsRockCodeTest {

	private static final int CENTER = 8 + 8 * 16;
	private static final Class<?>[] CODES = {
			Gleaf.class, Dpotion.class, Obubble.class, Ichain.class, Nshuriken.class, Trush.class,
			Bmech.class, Sweb.class, Mlaser.class, Zshield.class, Alink.class, Lbox.class
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-rock-code" + File.separator);
		Game.version = "test";
		try {
			com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll.initLabels();
			com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion.initColors();
			com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring.initGems();
			Badges.loadGlobal();
			testAllCastsAndPersistence();
			testEnergyArmorLegacySave();
			testPerformerDropGate();
			testMiniBombAndIcon();
			System.out.println("SPS洛克芯片测试通过：12枚芯片施放、能量、状态、旧能量护盾存档、首领掉落门槛和迷你炸弹原始图标均正常。");
		} finally {
			Actor.clear(); Dungeon.level = null; Dungeon.hero = null; app.exit();
		}
	}

	private static void testAllCastsAndPersistence() throws Exception {
		for (Class<?> type : CODES) {
			TestLevel level = freshLevel(); TestHero hero = freshHero(level); TestMob target = mobAt(level, CENTER + 3);
			RockCode code = (RockCode)type.getDeclaredConstructor().newInstance();
			int oldHp = target.HP;
			check(code.zapAt(hero, target.pos), type.getSimpleName() + "无法施放");
			check(code.curEnergy == 3 && hero.cooldown() == 1f, type.getSimpleName() + "没有消耗1点能量和1回合");
			if (!(code instanceof Alink)) check(target.HP < oldHp, type.getSimpleName() + "没有造成技能伤害");
			if (code instanceof Trush) check(level.map[target.pos] == Terrain.EMPTY_DECO, "墓石冲击没有破坏目标地面");
			if (code instanceof Sweb) check(level.blobs.get(WebClass()) != null, "蛛网芯片没有生成蛛网");
			if (code instanceof Zshield) check(hero.buff(EnergyArmor.class) != null
					&& hero.buff(EnergyArmor.class).shielding() == hero.lvl * 5, "尸块护盾数值错误");
			if (code instanceof Lbox) check(target.buff(FrostIce.class) != null
					&& target.buff(FrostIce.class).level() == 5f, "巫妖冰盒没有施加5回合冻伤");
			if (code instanceof Alink) {
				int mirrors = 0;
				for (Actor actor : Actor.all()) if (actor instanceof MirrorImage) mirrors++;
				check(mirrors == 2, "长老链接没有召唤两个镜像");
			}
			Bundle saved = new Bundle(); code.storeInBundle(saved);
			RockCode restored = (RockCode)type.getDeclaredConstructor().newInstance(); restored.restoreFromBundle(saved);
			check(restored.curEnergy == 3, type.getSimpleName() + "能量没有存档");
		}
	}

	@SuppressWarnings("unchecked")
	private static Class<com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob> WebClass() {
		return (Class<com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob>)(Class<?>)com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web.class;
	}

	private static void testPerformerDropGate() {
		TestLevel level = freshLevel(); TestHero hero = freshHero(level);
		hero.heroClass = HeroClass.PERFORMER; hero.skin = 7;
		RockCode.dropForPerformer(new Obubble());
		check(level.heaps.get(hero.pos) != null && level.heaps.get(hero.pos).peek() instanceof Obubble,
				"皮肤7演员没有获得首领芯片");
		level.heaps.clear(); hero.skin = 6;
		RockCode.dropForPerformer(new Obubble());
		check(level.heaps.get(hero.pos) == null, "非皮肤7演员错误获得首领芯片");
		hero.skin = 7; hero.heroClass = HeroClass.WARRIOR;
		RockCode.dropForPerformer(new Obubble());
		check(level.heaps.get(hero.pos) == null, "非演员职业错误获得首领芯片");
	}

	private static void testEnergyArmorLegacySave() {
		Bundle legacy = new Bundle();
		legacy.put("level", 37);
		EnergyArmor restored = new EnergyArmor();
		restored.restoreFromBundle(legacy);
		check(restored.shielding() == 37, "能量护盾没有读取旧版level字段");

		Bundle mixed = new Bundle();
		mixed.put("level", 37);
		mixed.put("shielding", 42);
		restored.restoreFromBundle(mixed);
		check(restored.shielding() == 42, "能量护盾没有优先读取新存档字段");

		Bundle saved = new Bundle();
		restored.storeInBundle(saved);
		check(saved.getInt("shielding") == 42 && saved.getInt("level") == 42,
				"能量护盾没有双写新旧存档字段");
	}

	private static void testMiniBombAndIcon() throws Exception {
		TestLevel level = freshLevel(); TestHero hero = freshHero(level); TestMob target = mobAt(level, CENTER + 2);
		level.flamable[target.pos] = true; int hp = target.HP;
		new MiniBomb().explode(target.pos);
		check(target.HP < hp && level.map[target.pos] == Terrain.EMBERS, "迷你炸弹没有造成中心伤害或烧毁地形");
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check("4AB894760B4D2518A56D7F03E98F1C91DDB01B915A22EF9E09AB3071E1D5CB79".equals(hash(sheet, 128, 832)),
				"迷你炸弹原始图标错误");
	}

	private static TestLevel freshLevel() {
		Actor.clear(); Dungeon.depth = 1; Dungeon.branch = 0; Dungeon.quickslot = new QuickSlot();
		TestLevel level = new TestLevel(); Dungeon.level = level; return level;
	}
	private static TestHero freshHero(TestLevel level) {
		TestHero hero = new TestHero(); hero.heroClass = HeroClass.PERFORMER; hero.skin = 7; hero.lvl = 5;
		Talent.initClassTalents(hero); hero.HP = hero.HT = 1000; hero.pos = CENTER;
		Dungeon.hero = hero; Actor.add(hero); return hero;
	}
	private static TestMob mobAt(TestLevel level, int pos) {
		TestMob mob = new TestMob(); mob.HP = mob.HT = 1000; mob.pos = pos; level.mobs.add(mob); Actor.add(mob); return mob;
	}
	private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
	private static String hash(BufferedImage sheet, int left, int top) throws Exception {
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array()); StringBuilder out = new StringBuilder(64);
		for (byte value : digest) out.append(String.format("%02X", value & 0xFF)); return out.toString();
	}

	private static final class TestHero extends Hero {
		@Override public void damage(int damage, Object source) { HP = Math.max(0, HP - Math.max(0, damage)); }
	}
	private static final class TestMob extends Mob {
		@Override public int drRoll() { return 0; }
		@Override public void damage(int damage, Object source) { HP = Math.max(0, HP - Math.max(0, damage)); }
	}
	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(16, 16); Arrays.fill(map, Terrain.EMPTY); mobs = new HashSet<>(); heaps = new SparseArray<>(); blobs = new HashMap<>();
			plants = new SparseArray<Plant>(); traps = new SparseArray<Trap>(); transitions = new ArrayList<>(); customTiles = new ArrayList<>(); customTerrain = new ArrayList<>(); customWalls = new ArrayList<>();
			buildFlagMaps();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell); if (heap == null) { heap = new Heap(); heap.pos = cell; heaps.put(cell, heap); } heap.drop(item); return heap;
		}
	}

	private SpsRockCodeTest() { }
}
