package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BerryRegeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EarthImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnergyArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GlassShield;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HasteBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MechArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Notice;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.STRDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SuperArcane;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ToxicImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Foamedbeverage;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashSet;

import javax.imageio.ImageIO;

/** Headless regression checks for the eight default-skin SPS-PD prepared foods. */
public final class SpsCompleteFoodTest {

	private static final String[] ICON_HASHES = {
			"32F197AF1886D3288ED5524AD5F92D4778A39863D6B89EE91D42A5533ED8B978",
			"5F4D7E9F14B53EC526A0ECD2464395A67105CF8E90F706C0144D302D4451D173",
			"8EDAF2453AD38C9FD156CA3B6F4F06F0B4F2E43D611DA0E6B61EBC7E145549F0",
			"7B681CEDDEBC995E7E36C206C1EFAA501D8911258C87539FBE89281B8EC6359D",
			"C4E6EBF4A35B68A8F7D3A69D0D934ED44EC72324F6828D2DB49E40BB765963E8",
			"299D545CD6B7F09BB35B7F49B0EBF811F269921336D938285A51AC12B1AE8DFD",
			"A1FBB0E0F82046FD160BDE13C8AD0A9B2ED8F3CD53CF68E52B7D066EE8F73274",
			"3AAF2BAA275F149207F326F06C9E6ACE7EF25BE951A4C567E07943E8CA9F94CD"
	};

	private static final String[] HIGH_FOOD_ICON_HASHES = {
			"63BF28CDD6FF6D073690E0D969AA4E5A743DE5B2E897F13E086CB76C0B61973D",
			"893F1457FEE3B1AE52841A3CC77A3277C3ECF5B3E9FAF0E423928366882A430C",
			"5156F1B31BCBE63F37FAF13EAEBB4D778C79D53234BF4636516496E65FB14328",
			"47621EF29AC02ECB48039091EB13E42D3421A80C7F907C86FFEB7D115E7D5DC1",
			"614121588CDF2A565A088B84F72C204644CCE07802CC8D7CC1C0750335B3E0CB",
			"5B2124761E1975321A9A055EBED1DE4CA7833AC678E2C270B58671E15B178167",
			"B79A564152D1A3DFC042C3576E45D66E7833D22837732F58F88BE58F9E7EED16",
			"359F34DB718F5A847BB1B13AF30FD2A968708576A4CD5E6D32C8C7A4C4987B94",
			"025855B9E1CA51CA309AC60D86738776BCF08771B1079DF8D698E204A783B07C",
			"0DEEC0C8775CCA01DA2BF871FD8E2D738B14468B45D367B0E4703C0E94F5633E",
			"F07B98423949BADC2B32692CC1F379A9EBE71152011ECF03500279159EE6A9A3",
			"52BD31E1D1AA94475C4158265A290F4B69179E3FF078668772C0936F4712E9EF",
			"8F3E21770DCF9602E3EA1E5436ED8BBF4EAF9E40FC60B9472CB37364F94E82DE",
			"50761A42F80B73CE23388FD93A08745974E2276B5B5B39198C7ECC56822C715F",
			"5FD9209982DC0DE93DBC47281AA04F196E47CC9321B584806CA396473096726F",
			"891140E9366AF64CE6F4CA16D63CEF27FEAE46EC40CA6B34FCFEF188F925FD8A",
			"24BB99A56E48A039EE49238C2C4B2FE019B7DC6F10AC766A95733F71BC3736B3"
	};

	private static final Class<?>[] HIGH_FOOD_CLASSES = {
			Chickennugget.class, Foamedbeverage.class, Fruitsalad.class, Hamburger.class,
			Herbmeat.class, Honeymeat.class, Honeyrice.class, Icecream.class, Kebab.class,
			PerfectFood.class, Porksoup.class, Ricefood.class, Vegetablekebab.class,
			Vegetablesoup.class, Meatroll.class, Vegetableroll.class, HoneyGel.class,
			Gel.class, HoneyWater.class, Chocolate.class, FoodFans.class, Frenchfries.class,
			FruitCandy.class, NutCookie.class, RiceGruel.class, MixPizza.class
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x535053464F4F4453L);
		try {
			testDefinitions();
			testHighFoodDefinitions();
			testFixedEffects();
			testHighFoodEffects();
			testRandomEffects();
			testHighFoodDeck();
			testMagicSkillAndSave();
			testLegacyIcons();
			System.out.println("SPS完整食物测试通过：8种开局食物、26项高级食物池、饱食、状态、永久生命、存档和原始图标均正常。");
		} finally {
			Random.popGenerator();
		}
	}

	private static void testDefinitions() {
		CompleteFood[] foods = {new Porksoup(), new Meatroll(), new Vegetablekebab(), new NutCookie(),
				new MixPizza(), new RiceGruel(), new FruitCandy(), new MoonCake()};
		int[] images = {ItemSpriteSheet.MEAT_SOUP, ItemSpriteSheet.HOTDOG, ItemSpriteSheet.KEBAB,
				ItemSpriteSheet.NUT_COOKIE, ItemSpriteSheet.MIX_PIZZA, ItemSpriteSheet.RICE_GRUEL,
				ItemSpriteSheet.FRUIT_CANDY, ItemSpriteSheet.MOON_CAKE};
		float[] energy = {200f, 250f, 150f, 10f, 50f, 250f, 20f, 360f};
		int[] quantity = {1, 1, 1, 6, 4, 2, 2, 1};
		int[] value = {3, 3, 2, 60, 40, 20, 20, 3};
		for (int i = 0; i < foods.length; i++) {
			check(foods[i].image == images[i], foods[i].getClass().getSimpleName() + "图标槽错误");
			check(foods[i].energy == energy[i], foods[i].getClass().getSimpleName() + "饱食值错误");
			check(foods[i].quantity() == quantity[i], foods[i].getClass().getSimpleName() + "默认数量错误");
			check(foods[i].value() == value[i], foods[i].getClass().getSimpleName() + "价格错误");
		}
	}

	private static void testHighFoodDefinitions() {
		CompleteFood[] foods = {new Chickennugget(), new Fruitsalad(), new Hamburger(), new Herbmeat(),
				new Honeymeat(), new Honeyrice(), new Icecream(), new Kebab(), new PerfectFood(),
				new Ricefood(), new Vegetablesoup(), new Vegetableroll(), new HoneyGel(), new Gel(),
				new HoneyWater(), new Chocolate(), new FoodFans(), new Frenchfries()};
		int[] images = {ItemSpriteSheet.CHICKENNUGGET, ItemSpriteSheet.FRUIT_SALAD,
				ItemSpriteSheet.HAMBURGER, ItemSpriteSheet.HERB_MEAT, ItemSpriteSheet.HONEY_MEAT,
				ItemSpriteSheet.HONEY_RICE, ItemSpriteSheet.ICECREAM, ItemSpriteSheet.KEBAB,
				ItemSpriteSheet.PERFECT_FOOD, ItemSpriteSheet.RICE_FOOD, ItemSpriteSheet.VEGETABLE_SOUP,
				ItemSpriteSheet.HOTDOG, ItemSpriteSheet.HONEY_GEL, ItemSpriteSheet.GEL,
				ItemSpriteSheet.HONEY_WATER, ItemSpriteSheet.CHOCOLATE, ItemSpriteSheet.FOOD_FANS,
				ItemSpriteSheet.FRENCH_FRIES};
		float[] energy = {170, 130, 770, 180, 150, 500, 90, 330, 600, 450, 90, 170,
				20, 10, 10, 300, 150, 150};
		int[] value = {2, 2, 10, 2, 400, 400, 300, 5, 50, 3, 1, 6, 400, 50,
				200, 60, 20, 20};
		for (int i = 0; i < foods.length; i++) {
			check(foods[i].image == images[i], foods[i].getClass().getSimpleName() + "图标槽错误");
			check(foods[i].energy == energy[i], foods[i].getClass().getSimpleName() + "饱食值错误");
			check(foods[i].value() == value[i], foods[i].getClass().getSimpleName() + "价格错误");
		}
		check(new Foamedbeverage().image == ItemSpriteSheet.FOAMED, "泡沫饮料没有使用旧版图标");
		check(Arrays.equals(Generator.Category.HIGHFOOD.classes, HIGH_FOOD_CLASSES), "高级食物池类型或顺序与旧版不一致");
		for (float probability : Generator.Category.HIGHFOOD.defaultProbs) {
			check(probability == 1f, "高级食物池不是等权生成");
		}
	}

	private static void testFixedEffects() {
		Hero hero = hero(120);
		Buff.affect(hero, Poison.class);
		Buff.affect(hero, Cripple.class);
		Buff.affect(hero, STRDown.class);
		Buff.affect(hero, Bleeding.class);
		new Porksoup().doEat(hero);
		check(hero.buff(Poison.class) == null && hero.buff(Cripple.class) == null
				&& hero.buff(STRDown.class) == null && hero.buff(Bleeding.class) == null, "排骨汤没有清除四种负面状态");
		check(hero.buff(MagicArmor.class).level() == 30, "排骨汤魔法护盾错误");
		check(hero.buff(AttackUp.class).level() == 20, "排骨汤攻击提升错误");

		hero = hero(120);
		new Meatroll().doEat(hero);
		check(hero.buff(Recharging.class) != null && hero.buff(SuperArcane.class).level() == 5
				&& hero.buff(AttackUp.class).level() == 20, "肉卷的三种状态错误");

		hero = hero(120);
		new Vegetablekebab().doEat(hero);
		check(hero.buff(MagicArmor.class).level() == 60 && hero.buff(AttackUp.class).level() == 20, "大菜串状态错误");

		hero = hero(120);
		new MixPizza().doEat(hero);
		check(hero.buff(Bless.class) != null && hero.buff(Light.class) != null
				&& hero.buff(HasteBuff.class) != null && hero.buff(Levitation.class) != null, "混合披萨状态错误");

		hero = hero(120);
		new MoonCake().doEat(hero);
		check(hero.buff(MagicArmor.class).level() == 40 && hero.buff(ShieldArmor.class).level() == 40, "月饼护盾错误");
	}

	private static void testHighFoodEffects() {
		Hero hero = hero(120);
		new Chickennugget().doEat(hero);
		check(hero.buff(AttackUp.class).level() == 20, "椒盐鸡块攻击增益错误");

		hero = hero(120);
		new Chocolate().doEat(hero);
		check(hero.buff(ShieldArmor.class).level() == 120, "巧克力物理护盾错误");

		hero = hero(120);
		new FoodFans().doEat(hero);
		check(hero.buff(ShieldArmor.class).level() == 60 && hero.buff(Bless.class) != null, "粉丝效果错误");

		hero = hero(120);
		new Frenchfries().doEat(hero);
		check(hero.buff(ShieldArmor.class).level() == 60 && hero.buff(Recharging.class) != null
				&& hero.buff(SuperArcane.class).level() == 5, "薯条效果错误");

		hero = hero(120);
		hero.HP = 40;
		new Fruitsalad().doEat(hero);
		check(hero.HP == 80 && hero.buff(BerryRegeneration.class) != null, "水果沙拉治疗或再生错误");

		hero = hero(120);
		hero.HP = 40;
		new Hamburger().doEat(hero);
		check(hero.HP == 64 && hero.buff(MagicArmor.class).level() == 40
				&& hero.buff(AttackUp.class).level() == 70, "巨无霸汉堡效果错误");

		hero = hero(120);
		new Herbmeat().doEat(hero);
		check(hero.buff(AttackUp.class).level() == 30, "药草酱肉攻击增益错误");

		hero = hero(120);
		int oldHT = hero.HT;
		new HoneyGel().doEat(hero);
		check(hero.HT >= oldHT + 3 && hero.HT <= oldHT + 5, "蜂蜜布丁永久生命错误");

		hero = hero(120);
		oldHT = hero.HT;
		new Honeymeat().doEat(hero);
		check(hero.HT >= oldHT + 3 && hero.HT <= oldHT + 5
				&& hero.buff(AttackUp.class).level() == 20, "蜜汁肉排效果错误");

		hero = hero(120);
		oldHT = hero.HT;
		new Honeyrice().doEat(hero);
		check(hero.HT >= oldHT + 3 && hero.HT <= oldHT + 5, "蜂蜜拌饭永久生命错误");

		hero = hero(120);
		hero.HP = 40;
		Buff.affect(hero, Poison.class);
		Buff.affect(hero, Burning.class);
		Buff.affect(hero, STRDown.class);
		oldHT = hero.HT;
		new Icecream().doEat(hero);
		check(hero.HT >= oldHT + 3 && hero.HT <= oldHT + 5 && hero.HP > 40
				&& hero.buff(Poison.class) == null && hero.buff(Burning.class) == null
				&& hero.buff(STRDown.class) == null, "冰雪盛宴的生命、治疗或净化错误");

		hero = hero(120);
		new Kebab().doEat(hero);
		check(hero.buff(MagicArmor.class).level() == 30 && hero.buff(AttackUp.class).level() == 40, "大肉串效果错误");

		hero = hero(120);
		oldHT = hero.HT;
		new PerfectFood().doEat(hero);
		check(hero.HT >= oldHT + 3 && hero.HT <= oldHT + 6 && hero.buff(Bless.class) != null
				&& hero.buff(Light.class) != null && hero.buff(HasteBuff.class) != null
				&& hero.buff(Levitation.class) != null, "完美便当效果错误");

		hero = hero(120);
		new Vegetableroll().doEat(hero);
		check(hero.buff(MagicArmor.class).level() == 30 && hero.buff(Recharging.class) != null
				&& hero.buff(SuperArcane.class).level() == 5, "菜卷效果错误");

		hero = hero(120);
		Buff.affect(hero, Poison.class);
		Buff.affect(hero, Cripple.class);
		Buff.affect(hero, STRDown.class);
		Buff.affect(hero, Bleeding.class);
		new Vegetablesoup().doEat(hero);
		check(hero.buff(Poison.class) == null && hero.buff(Cripple.class) == null
				&& hero.buff(STRDown.class) == null && hero.buff(Bleeding.class) == null
				&& hero.buff(MagicArmor.class).level() == 60, "菜汤净化或魔法护盾错误");

		hero = hero(120);
		Buff.affect(hero, Poison.class);
		Buff.affect(hero, Cripple.class);
		Buff.affect(hero, STRDown.class);
		Buff.affect(hero, Bleeding.class);
		oldHT = hero.HT;
		new HoneyWater().doEat(hero);
		check(hero.HT >= oldHT + 3 && hero.HT <= oldHT + 5 && hero.buff(Poison.class) == null
				&& hero.buff(Cripple.class) == null && hero.buff(STRDown.class) == null
				&& hero.buff(Bleeding.class) == null, "蜂糖水永久生命或净化错误");

		class TestDrink extends Foamedbeverage {
			void apply(Hero target) { onUse(target); }
		}
		boolean fire = false, frost = false, toxic = false, earth = false;
		for (int i = 0; i < 100 && !(fire && frost && toxic && earth); i++) {
			hero = hero(120);
			Buff.affect(hero, Poison.class);
			Buff.affect(hero, Cripple.class);
			new TestDrink().apply(hero);
			check(hero.buff(Poison.class) == null && hero.buff(Cripple.class) == null
					&& hero.buff(Bless.class) != null && hero.buff(BerryRegeneration.class) != null,
					"泡沫饮料的净化、祝福或再生错误");
			fire |= hero.buff(FireImbue.class) != null;
			frost |= hero.buff(FrostImbue.class) != null;
			toxic |= hero.buff(ToxicImbue.class) != null;
			earth |= hero.buff(EarthImbue.class) != null;
		}
		check(fire && frost && toxic && earth, "泡沫饮料无法产生全部四种元素防护");
	}

	private static void testRandomEffects() {
		boolean glass = false, mech = false, energy = false, physical = false, magic = false;
		for (int i = 0; i < 500 && !(glass && mech && energy && physical && magic); i++) {
			Hero hero = hero(100);
			new NutCookie().doEat(hero);
			if (hero.buff(GlassShield.class) != null) {
				glass = true;
				check(hero.buff(GlassShield.class).turns() == 6, "坚果饼干玻璃护盾未修复为6次");
			}
			mech |= hero.buff(MechArmor.class) != null;
			energy |= hero.buff(EnergyArmor.class) != null;
			physical |= hero.buff(ShieldArmor.class) != null;
			magic |= hero.buff(MagicArmor.class) != null;
		}
		check(glass && mech && energy && physical && magic, "坚果饼干无法产生全部五种结果");

		boolean movement = false, notice = false, healing = false;
		for (int i = 0; i < 200 && !(movement && notice && healing); i++) {
			Hero hero = hero(100);
			new FruitCandy().doEat(hero);
			movement |= hero.buff(HasteBuff.class) != null && hero.buff(Levitation.class) != null;
			notice |= hero.buff(Notice.class) != null;
			healing |= hero.buff(BerryRegeneration.class) != null;
		}
		check(movement && notice && healing, "水果硬糖无法产生全部三种结果");
	}

	private static void testHighFoodDeck() {
		Generator.reset(Generator.Category.HIGHFOOD);
		HashSet<Class<?>> generated = new HashSet<>();
		for (int i = 0; i < HIGH_FOOD_CLASSES.length; i++) {
			generated.add(Generator.random(Generator.Category.HIGHFOOD).getClass());
		}
		check(generated.size() == HIGH_FOOD_CLASSES.length
				&& generated.containsAll(Arrays.asList(HIGH_FOOD_CLASSES)), "高级食物完整牌组无法逐项生成");
	}

	private static void testMagicSkillAndSave() {
		Hero hero = hero(100);
		hero.improveMagicSkill(2);
		SuperArcane arcane = Buff.affect(hero, SuperArcane.class, 40f).level(1);
		check(hero.magicSkill() == 7, "低等级奥术灌注未提供至少5点法强");
		arcane.level(9);
		check(hero.magicSkill() == 11, "奥术灌注等级未接入英雄法强");

		Bundle bundle = new Bundle();
		arcane.storeInBundle(bundle);
		SuperArcane restored = new SuperArcane();
		restored.restoreFromBundle(bundle);
		check(restored.level() == 9, "奥术灌注等级未随存档恢复");
	}

	private static void testLegacyIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet != null && sheet.getWidth() == 256 && sheet.getHeight() == 992, "物品图集不是256x992像素");
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int slot = 0; slot < ICON_HASHES.length; slot++) {
			pixels.clear();
			int left = 64 + slot * 16;
			for (int y = 736; y < 752; y++) {
				for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
			}
			check(ICON_HASHES[slot].equals(toHex(digest.digest(pixels.array()))), "第" + (slot + 1) + "种开局食物原始图标错误");
		}
		for (int slot = 0; slot < HIGH_FOOD_ICON_HASHES.length; slot++) {
			pixels.clear();
			int index = 4 + slot;
			int left = index % 16 * 16;
			int top = 752 + index / 16 * 16;
			for (int y = top; y < top + 16; y++) {
				for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
			}
			check(HIGH_FOOD_ICON_HASHES[slot].equals(toHex(digest.digest(pixels.array()))),
					"第" + (slot + 1) + "种高级食物原始图标错误");
		}
	}

	private static Hero hero(int health) {
		Hero hero = new Hero();
		hero.HTBoost = health - Hero.STARTING_HT;
		hero.updateHT(false);
		hero.HP = hero.HT;
		Dungeon.hero = hero;
		return hero;
	}

	private static String toHex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
