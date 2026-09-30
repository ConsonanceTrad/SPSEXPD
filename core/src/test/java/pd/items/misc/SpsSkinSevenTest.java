package pd.items.misc;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Badges;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.BunnyCombo;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.Talent;
import pd.actors.mobs.Mob;
import pd.effects.Pushing;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.armor.ClothArmor;
import pd.items.armor.normalarmor.VestArmor;
import pd.items.armor.normalarmor.WoodenArmor;
import pd.items.armor.specialarmor.PerformerArmor;
import pd.items.armor.specialarmor.RenBArmor;
import pd.items.armor.specialarmor.RogueArmor;
import pd.items.artifacts.AlienBag;
import pd.items.artifacts.DriedRose;
import pd.items.artifacts.Pylon;
import pd.items.eggs.AflyEgg;
import pd.items.eggs.EasterEgg;
import pd.items.food.AflyFood;
import pd.items.wands.WandOfFirebolt;
import pd.items.weapon.Weapon;
import pd.items.weapon.enchantments.Blazing;
import pd.items.weapon.melee.normalweapon.Dagger;
import pd.items.weapon.melee.normalweapon.Knuckles;
import pd.items.weapon.melee.normalweapon.MageBook;
import pd.items.weapon.melee.normalweapon.ShortSword;
import pd.items.weapon.melee.normalweapon.WoodenStaff;
import pd.items.weapon.melee.special.NinjaFan;
import pd.items.weapon.melee.start.BunnyDagger;
import pd.items.weapon.melee.start.BunnySpanner;
import pd.items.weapon.melee.start.XSaber;
import pd.items.weapon.missiles.MegaCannon;
import pd.items.weapon.rockcode.Dpotion;
import pd.items.weapon.rockcode.RockCode;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Dewcatcher;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.serialize.Bundle;
import render.utils.serialize.FileUtils;

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

public final class SpsSkinSevenTest {

	private static final int CENTER = 8 + 8 * 16;
	private static final String[] ICON_HASHES = {
			"738D34968273D8459C75FBCDF751DAEBA33E94468755315AD29081C021F358AF",
			"3A4167185D8CA958CF3D3CB14C6757436C3E39F74FCAE428A6AA567D4037659A",
			"4704519FF83EC641A82A9930E06761774A269798CDA84494D0AA5BA84E9414DA",
			"0F9EC46DE0E201281028F3EDE276283D86DBDFD3A1D5191227BF221553C4849B",
			"6DF34D68B0AF3BBDF7A279FE6B2E0B6C1458E5508A973AA3211C8680872D2D3A",
			"82452A3D7C1D9CE28179FA37272FDD947324CA4AAA77F7FB2A876D8B7F3855FD",
			"3706EC18AA879E22CC2C312DA6AE5138259D90EBB89F2480258A0F6D7B62004B",
			"643A5CC740150C6AB94164F27EF3FA3077C8D074B365CEF07BBE5FF9F74B6D65"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-skin-seven" + File.separator);
		Game.version = "test";
		try {
			pd.items.scrolls.Scroll.initLabels();
			pd.items.potions.Potion.initColors();
			pd.items.rings.Ring.initGems();
			Badges.loadGlobal();
			testStarts();
			testFuuraiWeaponChange();
			testMegaAndRockMan();
			testBunnyWeaponsAndCombo();
			testNinjaFanAndArmor();
			testIcons();
			System.out.println("SPS皮肤7测试通过：八职业开局、风来人、洛克人、TEVI、谋士装备、连击、存档和8个原始图标均正常。");
		} finally {
			Actor.clear(); Dungeon.level = null; Dungeon.hero = null; app.exit();
		}
	}

	private static void testStarts() {
		Hero h = start(HeroClass.WARRIOR);
		check(h.belongings.weapon instanceof ShortSword && h.belongings.armor instanceof WoodenArmor,
				"皮肤7战士没有保持普通开局");
		h = start(HeroClass.MAGE);
		check(h.belongings.weapon instanceof MageBook, "皮肤7法师没有保持普通开局");
		h = start(HeroClass.HUNTRESS);
		check(h.belongings.weapon instanceof Knuckles && h.belongings.armor instanceof ClothArmor,
				"皮肤7女猎手没有保持普通开局：武器=" + h.belongings.weapon.getClass().getName()
						+ "，护甲=" + h.belongings.armor.getClass().getName());
		h = start(HeroClass.FOLLOWER);
		check(h.belongings.weapon instanceof WoodenStaff && h.belongings.armor instanceof ClothArmor,
				"皮肤7信徒没有保持普通开局");

		h = start(HeroClass.ROGUE);
		check(h.STR == Hero.STARTING_STR + 10 && h.belongings.weapon instanceof Dagger
				&& h.belongings.armor instanceof VestArmor && has(h, JumpR.class)
				&& quantity(h, AflyFood.class) == 3 && quantity(h, AflyEgg.class) == 3,
				"皮肤7风来人开局错误");
		h = start(HeroClass.PERFORMER);
		check(h.STR == Hero.STARTING_STR + 2 && h.belongings.weapon instanceof MegaCannon
				&& h.belongings.secondWep instanceof XSaber && h.belongings.armor instanceof PerformerArmor
				&& quantity(h, Dewcatcher.Seed.class) == 2 && has(h, RockManJumpshoes.class),
				"皮肤7洛克人开局错误");
		h = start(HeroClass.SOLDIER);
		check(h.belongings.weapon instanceof BunnyDagger && h.belongings.secondWep instanceof BunnySpanner
				&& h.belongings.armor instanceof RogueArmor && h.belongings.artifact instanceof AlienBag
				&& has(h, JumpR.class), "皮肤7 TEVI 开局错误");
		h = start(HeroClass.ASCETIC);
		Pylon pylon = h.belongings.getItem(Pylon.class);
		check(h.belongings.weapon instanceof NinjaFan && h.belongings.armor instanceof RenBArmor
				&& h.belongings.artifact instanceof DriedRose && pylon != null && pylon.isEquipped(h)
				&& has(h, WandOfFirebolt.class) && has(h, JumpW.class) && h.magicSkill() == 3,
				"皮肤7谋士开局错误");
	}

	private static void testFuuraiWeaponChange() {
		Hero hero = start(HeroClass.ROGUE);
		Weapon old = (Weapon)hero.belongings.weapon;
		old.level(3); old.levelKnown = false; old.cursedKnown = true; old.cursed = true;
		Blazing enchantment = new Blazing(); old.enchant(enchantment); old.reinforced = true;
		Dungeon.quickslot.setSlot(2, old);
		check(hero.advanceFuuraiWeapon(101), "风来人超过100点熟练度后没有更换武器");
		Weapon replacement = (Weapon)hero.belongings.weapon;
		check(replacement != old && replacement.getClass() != old.getClass() && replacement.trueLevel() == 3
				&& replacement.enchantment == enchantment && replacement.reinforced && replacement.cursed
				&& !replacement.levelKnown && replacement.cursedKnown && hero.spp == 0
				&& Dungeon.quickslot.getItem(2) == replacement, "风来人换武器没有完整继承状态或快捷栏");
	}

	private static void testMegaAndRockMan() {
		TestLevel level = freshLevel(); TestHero hero = freshHero(level); TestMob mob = mobAt(level, CENTER + 1);
		MegaCannon cannon = new MegaCannon();
		for (int i = 0; i < 5; i++) cannon.proc(hero, mob, 0);
		check(cannon.charge() == MegaCannon.FULL_CHARGE && new MegaCannon().charge() == 0,
				"洛克手炮充能没有封顶或不同实例共享状态");
		int shot = cannon.shotDamage(hero, cannon.charge());
		check(shot >= 3 && shot <= 15 && cannon.consumeCharge() == 3 && cannon.charge() == 0,
				"洛克手炮伤害倍率或射击清空错误");
		cannon.charge(1); check(cannon.ammo().image == ItemSpriteSheet.SPS_MEGA_AMMO_SMALL, "洛克手炮小弹图标错误");
		cannon.charge(2); check(cannon.ammo().image == ItemSpriteSheet.SPS_MEGA_AMMO_MEDIUM, "洛克手炮中弹图标错误");
		cannon.charge(3); check(cannon.ammo().image == ItemSpriteSheet.SPS_MEGA_AMMO_LARGE, "洛克手炮大弹图标错误");
		Bundle saved = new Bundle(); cannon.storeInBundle(saved); MegaCannon restored = new MegaCannon(); restored.restoreFromBundle(saved);
		check(restored.charge() == 3, "洛克手炮充能没有存档");

		Actor.remove(mob); level.mobs.remove(mob);
		RockManJumpshoes shoes = new RockManJumpshoes(); int origin = hero.pos;
		boolean jumped = shoes.jumpTo(hero, origin + 3);
		check(jumped && hero.pos == origin + 3 && hero.cooldown() == 3f,
				"洛克之鞋三格跳跃距离或耗时错误：jumped=" + jumped + "，位置=" + hero.pos
						+ "，目标=" + (origin + 3) + "，耗时=" + hero.cooldown());

		mob = mobAt(level, hero.pos + 1);
		XSaber saber = new XSaber(); Dpotion code = new Dpotion(); code.curEnergy = 2; code.collect(hero.belongings.backpack);
		check(saber.learn(hero, code) && !hero.belongings.backpack.contains(code)
				&& saber.rockCode() == code, "X能量剑没有消耗并学习技能芯片");
		int hp = mob.HP;
		for (int i = 0; i < 1000 && mob.HP == hp; i++) saber.proc(hero, mob, 5);
		check(mob.HP < hp, "X能量剑学习的芯片没有产生近战效果");
		saved = new Bundle(); saber.storeInBundle(saved); XSaber restoredSaber = new XSaber(); restoredSaber.restoreFromBundle(saved);
		check(restoredSaber.rockCode() instanceof Dpotion && restoredSaber.rockCode().curEnergy == 2,
				"X能量剑技能芯片没有存档");
	}

	private static void testBunnyWeaponsAndCombo() {
		TestLevel level = freshLevel(); TestHero hero = freshHero(level); hero.heroClass = HeroClass.SOLDIER; hero.skin = 7;
		TestMob mob = mobAt(level, CENTER + 1); BunnyDagger dagger = new BunnyDagger();
		int hp = mob.HP; dagger.proc(hero, mob, 5);
		check(mob.HP <= hp - 5 && mob.HP >= hp - 10, "兔人小刀额外伤害范围错误");
		BunnySpanner spanner = new BunnySpanner();
		RandomLoop: for (int i = 0; i < 1000; i++) {
			spanner.proc(hero, mob, 5);
			if (mob.buff(Paralysis.class) != null) break RandomLoop;
		}
		check(mob.buff(Paralysis.class) != null, "兔人扳手没有触发30%麻痹");
		mob.buff(Paralysis.class).detach();

		BunnyCombo combo = pd.actors.buffs.Buff.affect(hero, BunnyCombo.class);
		combo.hit(); combo.hit();
		check(combo.finish(hero, mob) && combo.count() == 3 && mob.buff(Cripple.class) != null,
				"兔兔连击2连击残效果错误");
		combo.hit(); check(combo.finish(hero, mob) && combo.count() == 5 && mob.buff(Blindness.class) != null,
				"兔兔连击4连抛沙效果错误");
		combo.hit(); check(combo.finish(hero, mob) && combo.count() == 7, "兔兔连击6连割裂阈值错误");
		combo.hit(); int oldPos = mob.pos;
		check(combo.finish(hero, mob) && combo.count() == 9 && mob.pos != oldPos && mob.buff(Vertigo.class) != null,
				"兔兔连击8连击退效果错误");
		mob.pos = hero.pos + 1; level.occupyCell(mob);
		combo.hit(); mob.HP = 100; check(combo.finish(hero, mob) && mob.HP == 80 && hero.buff(BunnyCombo.class) == null,
				"兔兔连击10连伤害倍率或清空错误");

		combo = pd.actors.buffs.Buff.affect(hero, BunnyCombo.class);
		combo.hit(); combo.hit(); combo.miss();
		Bundle saved = new Bundle(); combo.storeInBundle(saved); BunnyCombo restored = new BunnyCombo(); restored.restoreFromBundle(saved);
		check(restored.count() == 2 && restored.misses() == 1 && restored.comboTime() == 4f, "兔兔连击没有存档");
		combo.miss(); check(hero.buff(BunnyCombo.class) == null, "兔兔连击连续两次未命中没有清空");
		combo = pd.actors.buffs.Buff.affect(hero, BunnyCombo.class); combo.hit();
		for (int i = 0; i < 4; i++) combo.act();
		check(hero.buff(BunnyCombo.class) == null, "兔兔连击4回合超时没有清空");
	}

	private static void testNinjaFanAndArmor() {
		TestLevel level = freshLevel(); TestHero hero = freshHero(level); TestMob mob = mobAt(level, CENTER + 1);
		NinjaFan fan = new NinjaFan();
		for (int i = 0; i < 6; i++) fan.proc(hero, mob, 1);
		check(fan.charge() == 6 && new NinjaFan().charge() == 0, "忍者蒲扇蓄风或实例隔离错误");
		fan.proc(hero, mob, 1);
		check(fan.charge() == 0 && mob.buff(Vertigo.class) != null && Pushing.pushingExistsForChar(mob),
				"忍者蒲扇第7击没有眩晕并创建击退动作");
		fan.proc(hero, mob, 1); Bundle saved = new Bundle(); fan.storeInBundle(saved); NinjaFan restored = new NinjaFan(); restored.restoreFromBundle(saved);
		check(restored.charge() == 1, "忍者蒲扇蓄风没有存档");

		RenBArmor armor = new RenBArmor(); hero.belongings.armor = armor;
		for (int i = 0; i < 99; i++) armor.proc(mob, hero, 1);
		check(armor.durability() == 1 && hero.belongings.armor == armor, "兔女郎制服提前破碎");
		armor.proc(mob, hero, 1);
		check(armor.durability() == 0 && hero.belongings.armor == null
				&& level.heaps.get(hero.pos).peek() instanceof EasterEgg, "兔女郎制服第100击没有破碎并掉落彩蛋");
		armor.durability(37); saved = new Bundle(); armor.storeInBundle(saved); RenBArmor restoredArmor = new RenBArmor(); restoredArmor.restoreFromBundle(saved);
		check(restoredArmor.durability() == 37, "兔女郎制服耐久没有存档");
	}

	private static void testIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet.getWidth() == 256 && sheet.getHeight() == 992, "物品图集不是256x992");
		for (int i = 0; i < ICON_HASHES.length; i++)
			check(ICON_HASHES[i].equals(hash(sheet, i * 16, 832)), "皮肤7第" + (i + 1) + "个原始图标错误");
	}

	private static Hero start(HeroClass heroClass) {
		Actor.clear(); Dungeon.level = null; Dungeon.gold = 0; Dungeon.LimitedDrops.reset();
		Dungeon.quickslot = new QuickSlot(); Generator.fullReset();
		Hero hero = new Hero(); hero.skin = 7; Dungeon.hero = hero; heroClass.initHero(hero); return hero;
	}
	private static TestLevel freshLevel() {
		Actor.clear(); Dungeon.depth = 1; Dungeon.branch = 0; Dungeon.quickslot = new QuickSlot();
		TestLevel level = new TestLevel(); Dungeon.level = level; return level;
	}
	private static TestHero freshHero(TestLevel level) {
		TestHero hero = new TestHero(); hero.heroClass = HeroClass.WARRIOR; Talent.initClassTalents(hero);
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

	private static final class TestHero extends Hero {
		@Override public int damageRoll() { return 10; }
		@Override public int attackProc(pd.actors.Char enemy, int damage) { return damage; }
		@Override public void damage(int damage, Object source) { HP = Math.max(0, HP - Math.max(0, damage)); }
	}
	private static final class TestMob extends Mob {
		@Override public int drRoll() { return 0; }
		@Override public int defenseProc(pd.actors.Char enemy, int damage) { return damage; }
	}
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
		@Override public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell); if (heap == null) { heap = new Heap(); heap.pos = cell; heaps.put(cell, heap); } heap.drop(item); return heap;
		}
	}

	private SpsSkinSevenTest() { }
}
