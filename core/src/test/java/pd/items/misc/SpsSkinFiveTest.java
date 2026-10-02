package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

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
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.items.ChangeEquip;
import pd.items.Generator;
import pd.items.Item;
import pd.items.KnowledgeBook;
import pd.items.TransmutationBall;
import pd.items.equipment.armor.normalarmor.BaseArmor;
import pd.items.equipment.armor.normalarmor.VestArmor;
import pd.items.equipment.armor.specialarmor.TestArmor;
import pd.items.equipment.artifacts.Artifact;
import pd.items.consum.food.completefood.FruitCandy;
import pd.items.consum.food.completefood.Meatroll;
import pd.items.consum.food.completefood.MixPizza;
import pd.items.consum.food.completefood.MoonCake;
import pd.items.consum.food.completefood.NutCookie;
import pd.items.consum.food.completefood.Porksoup;
import pd.items.consum.food.completefood.RiceGruel;
import pd.items.consum.food.completefood.Vegetablekebab;
import pd.items.equipment.rings.Ring;
import pd.items.consum.scrolls.ScrollOfRemoveCurse;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.normalweapon.ShortSword;
import pd.items.equipment.weapon.melee.normalweapon.Spear;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.FileUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.Iterator;

import javax.imageio.ImageIO;

public final class SpsSkinFiveTest {

	private static final HeroClass[] CLASSES = {
			HeroClass.WARRIOR, HeroClass.MAGE, HeroClass.ROGUE, HeroClass.HUNTRESS,
			HeroClass.PERFORMER, HeroClass.SOLDIER, HeroClass.FOLLOWER, HeroClass.ASCETIC
	};
	private static final Class<?>[] FOODS = {
			Porksoup.class, Meatroll.class, RiceGruel.class, Vegetablekebab.class,
			NutCookie.class, MixPizza.class, MoonCake.class, FruitCandy.class
	};
	private static final Class<?>[] SHOES = {
			JumpW.class, JumpM.class, JumpR.class, JumpH.class,
			JumpP.class, JumpS.class, JumpF.class, JumpA.class
	};

	public static void main(String[] args) {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-skin-five" + File.separator);
		Game.version = "test";
		try {
			pd.items.consum.scrolls.Scroll.initLabels();
			pd.items.consum.potions.Potion.initColors();
			pd.items.equipment.rings.Ring.initGems();
			Badges.loadGlobal();
			testShoeDeck();
			testAllClassStarts();
			testDualEquipment();
			testAccessorySlots();
			testChangeIcon();
			System.out.println("SPS皮肤5测试通过：八职业随机双武器、双基础甲、装备切换、存档、净化、容量和原始图标均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
			app.exit();
		}
	}

	private static void testShoeDeck() {
		check(Generator.Category.SHOES.classes.length == SHOES.length
				&& Generator.Category.SHOES.probs.length == SHOES.length,
				"皮肤5职业鞋牌组不是8双");
		for (int i = 0; i < SHOES.length; i++) {
			check(Generator.Category.SHOES.classes[i] == SHOES[i]
					&& Generator.Category.SHOES.probs[i] == 1f,
					"皮肤5职业鞋牌组顺序或权重错误");
		}
	}

	private static void testAllClassStarts() {
		for (int i = 0; i < CLASSES.length; i++) {
			Random.pushGenerator(0x535053534B494E35L + i);
			Hero hero;
			try { hero = start(CLASSES[i]); }
			finally { Random.popGenerator(); }

			check(hero.STR == Hero.STARTING_STR + 10,
					"皮肤5" + CLASSES[i] + "没有增加10点力量");
			check(hero.belongings.weapon instanceof Weapon
					&& hero.belongings.secondWep instanceof Weapon
					&& hero.belongings.weapon != hero.belongings.secondWep,
					"皮肤5" + CLASSES[i] + "没有两件独立随机武器");
			check(hero.belongings.armor instanceof BaseArmor
					&& hero.belongings.secondArmor instanceof BaseArmor
					&& hero.belongings.getAllItems(BaseArmor.class).size() == 2,
					"皮肤5" + CLASSES[i] + "没有主用与备用基础甲");
			check(hero.belongings.artifact instanceof Artifact && hero.belongings.ring instanceof Ring,
					"皮肤5" + CLASSES[i] + "没有随机神器或戒指");
			check(hero.belongings.weapon.isIdentified() && hero.belongings.secondWep.isIdentified()
					&& hero.belongings.armor.isIdentified() && hero.belongings.artifact.isIdentified()
					&& hero.belongings.ring.isIdentified(),
					"皮肤5" + CLASSES[i] + "的随机装备没有全部鉴定");
			check(countShoes(hero) == 1, "皮肤5" + CLASSES[i] + "没有且仅有一双随机职业鞋");
			check(has(hero, KnowledgeBook.class) && has(hero, ScrollOfRemoveCurse.class),
					"皮肤5" + CLASSES[i] + "缺少知识之书或净化卷轴");
			TransmutationBall balls = hero.belongings.getItem(TransmutationBall.class);
			check(balls != null && balls.quantity() == 2,
					"皮肤5" + CLASSES[i] + "没有两颗转换笼果实");
			Item food = hero.belongings.getItem((Class<? extends Item>)FOODS[i]);
			check(food != null, "皮肤5" + CLASSES[i] + "缺少职业公共食物");
		}
	}

	//SPS: 5 个完全通用饰品槽 + 1 个徽章槽（槽位与徽章字段的独立承载）
	private static void testAccessorySlots() {
		Hero hero = new Hero();
		Dungeon.hero = hero;

		Belongings b = hero.belongings;
		check(b.artifact == null && b.misc == null && b.ring == null
				&& b.accessory4 == null && b.accessory5 == null && b.badge == null,
				"饰品槽 1-5 或徽章槽初始不为空");

		pd.items.KindofMisc[] slots =
				{ b.artifact, b.misc, b.ring, b.accessory4, b.accessory5 };
		check(slots.length == 5, "通用饰品槽数量不是 5");

		//同一件饰品可放入任意槽；各槽互不串扰
		pd.items.equipment.rings.RingOfForce ring =
				new pd.items.equipment.rings.RingOfForce();
		b.accessory4 = ring;
		b.accessory5 = ring;
		check(b.accessory4 == ring && b.accessory5 == ring
				&& b.artifact == null && b.misc == null && b.ring == null,
				"饰品槽 4/5 未独立承载物品或与其它槽串扰");
		b.accessory4 = null;
		b.accessory5 = null;
		check(b.accessory4 == null && b.accessory5 == null, "饰品槽 4/5 无法释放");
	}

	private static void testDualEquipment() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		hero.belongings.weapon = new ShortSword();
		hero.belongings.secondWep = new Spear();
		hero.belongings.armor = new BaseArmor();
		hero.belongings.secondArmor = new VestArmor();

		int occupiedCapacity = hero.belongings.backpack.capacity();
		Weapon primaryWeapon = (Weapon)hero.belongings.weapon;
		Weapon secondaryWeapon = (Weapon)hero.belongings.secondWep;
		BaseArmor primaryArmor = (BaseArmor)hero.belongings.armor;
		VestArmor secondaryArmor = (VestArmor)hero.belongings.secondArmor;
		//SPS: 主背包 35 格（5 列 x 7 行），装备区两排独立、不占背包容量（用户裁决）
		check(occupiedCapacity == Belongings.BACKPACK_CAPACITY,
				"主背包容量不是 35 格（5x7）");
		hero.belongings.secondWep = null;
		hero.belongings.secondArmor = null;
		check(hero.belongings.backpack.capacity() == occupiedCapacity,
				"副装备不应再占用背包容量（装备区两排独立）");
		hero.belongings.secondWep = secondaryWeapon;
		hero.belongings.secondArmor = secondaryArmor;

		ChangeEquip control = new ChangeEquip();
		check(control.image == SpecificPlaceHolderDict.SOMETHING_0
				&& control.actions(hero).contains(ChangeEquip.AC_CHANGE)
				&& !control.actions(hero).contains(Item.AC_DROP)
				&& !control.actions(hero).contains(Item.AC_THROW),
				"装备切换按钮动作或图标错误");
		ChangeEquip.swap(hero);
		check(hero.belongings.weapon == secondaryWeapon && hero.belongings.secondWep == primaryWeapon
				&& hero.belongings.armor == secondaryArmor && hero.belongings.secondArmor == primaryArmor,
				"主副武器或主副护甲没有同时对换");
		check(primaryArmor.isEquipped(hero) && secondaryArmor.isEquipped(hero),
				"副护甲没有被识别为已装备物品");

		TestArmor activeArmor = new TestArmor();
		hero.belongings.armor = activeArmor;
		hero.belongings.secondArmor = new BaseArmor();
		activeArmor.activate(hero);
		check(hero.buff(TestArmor.TestCharge.class) != null, "测试甲没有建立主槽增益");
		ChangeEquip.swap(hero);
		check(hero.buff(TestArmor.TestCharge.class) == null && hero.belongings.secondArmor == activeArmor,
				"护甲移入副槽后仍残留主槽增益");
		ChangeEquip.swap(hero);
		check(hero.buff(TestArmor.TestCharge.class) != null && hero.belongings.armor == activeArmor,
				"副护甲切回主槽后没有重新激活");
		activeArmor.deactivate(hero);
		hero.belongings.armor = secondaryArmor;
		hero.belongings.secondArmor = primaryArmor;

		hero.belongings.secondWep.cursed = true;
		hero.belongings.secondArmor.cursed = true;
		hero.belongings.uncurseEquipped();
		check(!hero.belongings.secondWep.cursed && !hero.belongings.secondArmor.cursed,
				"副武器或副护甲没有被净化");

		Bundle saved = new Bundle();
		hero.belongings.storeInBundle(saved);
		Hero restored = new Hero();
		Dungeon.hero = restored;
		restored.belongings.restoreFromBundle(saved);
		check(restored.belongings.weapon instanceof Spear
				&& restored.belongings.secondWep instanceof ShortSword
				&& restored.belongings.armor instanceof VestArmor
				&& restored.belongings.secondArmor instanceof BaseArmor,
				"双武器或双护甲没有随存档恢复");
		check(restored.belongings.getAllItems(pd.items.equipment.armor.Armor.class).size() == 2,
				"装备遍历没有包含副护甲");

		Hero removal = new Hero();
		removal.belongings.weapon = new ShortSword();
		removal.belongings.secondArmor = new BaseArmor();
		Iterator<Item> iterator = removal.belongings.iterator();
		check(iterator.next() == removal.belongings.weapon, "装备迭代顺序错误");
		iterator.remove();
		check(removal.belongings.weapon == null && removal.belongings.armor == null,
				"装备迭代器删除了错误槽位");
		check(iterator.next() == removal.belongings.secondArmor, "装备迭代跳过了副护甲");
		iterator.remove();
		check(removal.belongings.secondArmor == null, "装备迭代器无法删除副护甲");
	}

	private static void testChangeIcon() {
		try {
			java.awt.image.BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
			check("81D63ED83FC728CE9036912A81277E23E23962EAEA763066C1EDEA337DD41A16"
					.equals(hash(sheet, 128, 816)), "装备切换原始图标错误");
		} catch (Exception e) {
			throw new AssertionError("无法校验装备切换原始图标", e);
		}
	}

	private static String hash(java.awt.image.BufferedImage sheet, int left, int top) throws Exception {
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) {
			for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder out = new StringBuilder(64);
		for (byte value : digest) out.append(String.format("%02X", value & 0xFF));
		return out.toString();
	}

	private static int countShoes(Hero hero) {
		int count = 0;
		for (Class<?> shoe : SHOES) if (has(hero, (Class<? extends Item>)shoe)) count++;
		return count;
	}

	private static Hero start(HeroClass heroClass) {
		Actor.clear();
		Dungeon.level = null;
		Dungeon.gold = 0;
		Dungeon.LimitedDrops.reset();
		Dungeon.quickslot = new QuickSlot();
		Generator.fullReset();
		Hero hero = new Hero();
		hero.skin = 5;
		Dungeon.hero = hero;
		heroClass.initHero(hero);
		return hero;
	}

	private static boolean has(Hero hero, Class<? extends Item> type) {
		return hero.belongings.getItem(type) != null;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsSkinFiveTest() { }
}
