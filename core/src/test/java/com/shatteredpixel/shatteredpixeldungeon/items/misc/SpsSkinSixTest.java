package com.shatteredpixel.shatteredpixeldungeon.items.misc;

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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HolyStun;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.OnePunch;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shocked2;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WatchOut;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.mindbuff.CrazyMind;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.Weightstone;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.BaseArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.VestArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Hardpill;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Powerpill;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Smashpill;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.ShortSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.TrickSand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.WoodenStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.start.BeastKnive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.start.EleKatana;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ShootGun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.BlindFruit;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.PoisonDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing.EmpBola;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing.Skull;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.Sling;
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

public final class SpsSkinSixTest {

	private static final int CENTER = 8 + 8 * 16;
	private static final String[] ICON_HASHES = {
			"B094391DC3A0EF40B9A8C16E1F2AFD8480F535783FAB2FA887D6EFDC582F0DF0",
			"4A493737EE7BF9A7954C0E0952D18993BFC3D8CA35DDA41053C50D00A1AB5BAB",
			"E299314ABD971E1D512177CF8B020977D7154889057AE9185EFEF83952E97DB2",
			"1C538667800E4A31E978178AEFEFE0D81F6852B6CF4B53925BD1AECAE1C1D9CC",
			"E2E5606935D359BC7725154C900BC0DAA29C9FE98A586ED8601E0CC9744A7562",
			"5591EC37215B364A4AA19140C4D2CEB94E279A4F4E2978D2F2A3AE950328CB65",
			"4704519FF83EC641A82A9930E06761774A269798CDA84494D0AA5BA84E9414DA",
			"0F9EC46DE0E201281028F3EDE276283D86DBDFD3A1D5191227BF221553C4849B"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-skin-six" + File.separator);
		Game.version = "test";
		try {
			com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll.initLabels();
			com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion.initColors();
			com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring.initGems();
			Badges.loadGlobal();
			testStarts();
			testPunchAndShield();
			testBladesAndShock();
			testShotgunWeightstoneAndPpc();
			testIcons();
			System.out.println("SPS皮肤6测试通过：八职业开局、拳套与护盾、双刃、电击、霰弹枪、磨刀石、调查仪、存档和8个原始图标均正常。");
		} finally {
			Actor.clear(); Dungeon.level = null; Dungeon.hero = null; app.exit();
		}
	}

	private static void testStarts() {
		Hero h = start(HeroClass.WARRIOR);
		check(h.belongings.armor instanceof VestArmor && h.belongings.armor.level() == 1
				&& h.belongings.misc instanceof RingOfForce && h.belongings.misc.level() == 1
				&& h.belongings.ring instanceof RingOfMight && h.belongings.ring.level() == 1
				&& has(h, SeriousPunch.class) && has(h, Ankhshield.class) && has(h, JumpW.class), "皮肤6战士开局错误");

		h = start(HeroClass.MAGE);
		check(h.belongings.weapon instanceof ShortSword
				&& h.belongings.armor instanceof com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.ClothArmor
				&& has(h, GnollMark.class) && has(h, WandOfLight.class) && has(h, Powerpill.class)
				&& has(h, Smashpill.class) && has(h, Hardpill.class) && has(h, JumpW.class)
				&& h.magicSkill() == 3, "皮肤6法师开局错误");

		h = start(HeroClass.ROGUE);
		check(h.STR == Hero.STARTING_STR + 2 && h.belongings.weapon instanceof EleKatana
				&& h.belongings.secondWep instanceof BeastKnive
				&& h.belongings.artifact instanceof CloakOfShadows && h.belongings.misc instanceof EtherealChains
				&& h.belongings.misc.level() == 3 && quantity(h, EmpBola.class) == 10
				&& quantity(h, PoisonDart.class) == 10 && quantity(h, BlindFruit.class) == 10
				&& has(h, Weightstone.class) && has(h, Stylus.class) && has(h, PotionOfHealing.class)
				&& has(h, ScrollOfMagicMapping.class) && has(h, WandOfLightning.class), "皮肤6盗贼开局错误");

		h = start(HeroClass.HUNTRESS);
		check(h.belongings.weapon instanceof Sling && h.belongings.armor instanceof VestArmor
				&& has(h, ShootGun.class) && has(h, JumpS.class) && quantity(h, EmpBola.class) == 3, "皮肤6女猎手开局错误");

		h = start(HeroClass.PERFORMER);
		check(h.STR == Hero.STARTING_STR + 2 && h.belongings.weapon instanceof Mace
				&& h.belongings.armor instanceof com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.LeatherArmor
				&& has(h, PPC2.class) && has(h, JumpP.class), "皮肤6演员开局错误");

		h = start(HeroClass.SOLDIER);
		check(h.HTBoost == 5 && h.STR == Hero.STARTING_STR + 6 && h.magicSkill() == 5
				&& h.belongings.weapon instanceof WoodenStaff && h.belongings.armor instanceof BaseArmor
				&& has(h, ShootGun.class) && has(h, JumpS.class), "皮肤6星兵开局错误");

		h = start(HeroClass.FOLLOWER);
		check(h.spp == 100 && h.belongings.weapon instanceof TrickSand && h.belongings.armor instanceof VestArmor
				&& quantity(h, Skull.class) == 5 && h.belongings.getAllItems(Ankh.class).size() == 2 && has(h, JumpF.class), "皮肤6信徒开局错误");

		h = start(HeroClass.ASCETIC);
		check(h.magicSkill() == 3 && has(h, JumpA.class) == false
				&& has(h, com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.FruitCandy.class), "皮肤6苦修者公共开局错误");
	}

	private static void testPunchAndShield() {
		TestLevel level = freshLevel();
		Hero hero = freshHero(level);
		SeriousPunch punch = new SeriousPunch(); punch.collect(hero.belongings.backpack); punch.charge(4);
		check(punch.cast(hero) && punch.charge() == 0 && hero.buff(OnePunch.class).level() == 4, "认真拳套没有生成一拳状态");
		int damage = hero.attackProc(mobAt(level, CENTER + 1), 10);
		check(damage == 14 && hero.buff(OnePunch.class) == null && punch.charge() == 1, "认真一拳倍率或命中充能错误");
		Bundle saved = new Bundle(); punch.charge(17); punch.storeInBundle(saved);
		SeriousPunch restoredPunch = new SeriousPunch(); restoredPunch.restoreFromBundle(saved);
		check(restoredPunch.charge() == 17, "认真拳套充能未存档");

		Ankhshield shield = new Ankhshield(); shield.collect(hero.belongings.backpack); shield.charge(29);
		check(!shield.defend(hero), "神圣护盾在30充能前错误发动");
		TestMob close = mobAt(level, CENTER + 2); TestMob far = mobAt(level, CENTER + 5);
		shield.charge(30);
		check(shield.defend(hero) && shield.charge() == 0 && close.HP == 95
				&& close.buff(HolyStun.class) != null && far.buff(WatchOut.class) != null, "神圣护盾范围效果错误");
		shield.charge(99); shield.gainCharge(); shield.gainCharge();
		check(shield.charge() == Ankhshield.FULL_CHARGE, "神圣护盾充能未在100封顶");
	}

	private static void testBladesAndShock() {
		TestLevel level = freshLevel(); Hero hero = freshHero(level); TestMob mob = mobAt(level, CENTER + 1);
		EleKatana katana = new EleKatana();
		int first = katana.proc(hero, mob, 20);
		check(first == 20 && katana.charge() == 1 && mob.buff(Shocked2.class) != null && mob.HP < 100, "武士雷刀首次命中错误");
		int second = katana.proc(hero, mob, 20);
		check(second == 30 && katana.charge() == 2, "武士雷刀没有利用环绕乱流增伤");
		Shocked2 turbulence = mob.buff(Shocked2.class); int hp = mob.HP; mob.pos++;
		turbulence.act();
		check(mob.HP == hp - mob.HT / 20 && mob.buff(Roots.class) != null && mob.buff(Shocked2.class) == null, "环绕乱流移动惩罚错误");

		Actor.remove(mob); level.mobs.remove(mob); katana.charge(10); int oldPos = hero.pos;
		check(katana.zap(hero, oldPos + 3) && hero.pos == oldPos + 3 && katana.charge() == 0, "武士雷刀一闪边界错误");

		BeastKnive knife = new BeastKnive(); mob = mobAt(level, hero.pos + 1);
		knife.proc(hero, mob, 20);
		check(knife.charge() == 1 && mob.HP < 100, "兽性匕首命中效果错误");
		knife.charge(10);
		check(knife.empower(hero) && knife.charge() == 0, "兽性匕首恰好10充能时无法发动");
	}

	private static void testShotgunWeightstoneAndPpc() {
		TestLevel level = freshLevel(); Hero hero = freshHero(level); TestMob mob = mobAt(level, CENTER + 1);
		ShootGun gun = new ShootGun();
		check(gun.reload(hero) && gun.charge() == ShootGun.FULL_CHARGE && !gun.reload(hero), "科技霰弹枪装填边界错误");
		gun.ammo().proc(hero, mob, 10);
		check(mob.buff(com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak.class) != null, "科技霰弹枪没有施加破甲");
		Bundle saved = new Bundle(); gun.charge(2); gun.storeInBundle(saved); ShootGun restored = new ShootGun(); restored.restoreFromBundle(saved);
		check(restored.charge() == 2, "科技霰弹枪弹仓未存档");

		ShortSword sword = new ShortSword(); hero.belongings.weapon = sword; sword.identify();
		Weightstone stone = new Weightstone(); stone.collect(hero.belongings.backpack);
		check(stone.apply(hero, sword) && sword.enchantment != null && !sword.cursed && sword.isIdentified(), "磨刀石没有消耗并附魔净化武器");

		PPC2 ppc = new PPC2();
		Level.set(hero.pos + 1, Terrain.WALL, level);
		check(ppc.mine(hero, hero.pos + 1) && level.map[hero.pos + 1] == Terrain.EMBERS, "深渊调查仪没有拆取相邻墙体");
		hero.spp = hero.lvl * 2;
		check(!ppc.randomMind(hero), "深渊调查仪在费用边界错误允许实验");
		hero.spp++;
		check(ppc.randomMind(hero) && hero.spp == 1, "深渊调查仪没有正确扣除实验费用");
		com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff.affect(hero, CrazyMind.class);
		hero.HP = 50; hero.spp = 0;
		check(ppc.clearMind(hero) && hero.buff(CrazyMind.class) == null && hero.HP == 70, "深渊调查仪精神丰收错误");
	}

	private static void testIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet.getWidth() == 256 && sheet.getHeight() == 992, "物品图集不是256x992");
		for (int i = 0; i < ICON_HASHES.length; i++) check(ICON_HASHES[i].equals(hash(sheet, i * 16, 816)), "皮肤6第" + (i + 1) + "个原始图标错误");
	}

	private static Hero start(HeroClass heroClass) {
		Actor.clear(); Dungeon.level = null; Dungeon.gold = 0; Dungeon.LimitedDrops.reset(); Dungeon.quickslot = new QuickSlot(); Generator.fullReset();
		Hero hero = new Hero(); hero.skin = 6; Dungeon.hero = hero; heroClass.initHero(hero); return hero;
	}

	private static TestLevel freshLevel() {
		Actor.clear(); Dungeon.depth = 1; Dungeon.branch = 0; Dungeon.quickslot = new QuickSlot();
		TestLevel level = new TestLevel(); Dungeon.level = level; return level;
	}

	private static Hero freshHero(TestLevel level) {
		Hero hero = new TestHero(); hero.heroClass = HeroClass.WARRIOR; Talent.initClassTalents(hero);
		hero.HP = hero.HT = 100; hero.pos = CENTER; Dungeon.hero = hero; Actor.add(hero); return hero;
	}

	private static TestMob mobAt(TestLevel level, int pos) {
		TestMob mob = new TestMob(); mob.HP = mob.HT = 100; mob.pos = pos; level.mobs.add(mob); Actor.add(mob); return mob;
	}

	private static boolean has(Hero hero, Class<? extends Item> type) { return hero.belongings.getItem(type) != null; }
	private static int quantity(Hero hero, Class<? extends Item> type) { Item item = hero.belongings.getItem(type); return item == null ? 0 : item.quantity(); }
	private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }

	private static String hash(BufferedImage sheet, int left, int top) throws Exception {
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array()); StringBuilder out = new StringBuilder(64);
		for (byte value : digest) out.append(String.format("%02X", value & 0xFF)); return out.toString();
	}

	private static final class TestMob extends Mob { @Override public int drRoll() { return 0; } }
	private static final class TestHero extends Hero { @Override public void damage(int damage, Object source) { HP = Math.max(0, HP - Math.max(0, damage)); } }
	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(16, 16); Arrays.fill(map, Terrain.EMPTY); mobs = new HashSet<>(); heaps = new SparseArray<>(); blobs = new HashMap<>();
			plants = new SparseArray<Plant>(); traps = new SparseArray<Trap>(); transitions = new ArrayList<>(); customTiles = new ArrayList<>(); customTerrain = new ArrayList<>(); customWalls = new ArrayList<>();
			buildFlagMaps(); Arrays.fill(heroFOV, true);
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public Heap drop(Item item, int cell) { Heap heap = heaps.get(cell); if (heap == null) { heap = new Heap(); heap.pos = cell; heaps.put(cell, heap); } heap.drop(item); return heap; }
	}

	private SpsSkinSixTest() { }
}
