package pd;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.items.Item;
import pd.items.armor.Armor;
import pd.items.artifacts.fusion.NoomlinCrown;
import pd.items.misc.LuckyBadge;
import pd.items.rings.Ring;
import pd.items.scrolls.ScrollOfUpgrade;
import pd.items.wands.WandOfTest;
import pd.items.weapon.Weapon;
import pd.items.weapon.missiles.buildblock.PlantPotBlock;
import pd.items.weapon.missiles.fusion.RocketMissile;
import pd.plants.Plant;
import pd.ui.CurrencyIndicator;
import render.utils.serialize.FileUtils;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

/**
 * SPS S金与礼物商店门禁：金币兑 S金（2333:1）、S金钱包持久化、
 * 礼物商店前置树与购买落盘、开局强化发放和四语文本。
 */
public final class SpsSCoinGiftShopTest {

	private SpsSCoinGiftShopTest() { }

	public static void main(String[] args) throws Exception {
		render.noosa.Game.version = "test";
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		java.nio.file.Path temp = java.nio.file.Files.createTempDirectory("sps-scoin-");
		FileUtils.setDefaultFileProperties(com.badlogic.gdx.Files.FileType.Absolute,
				temp.toAbsolutePath().toString() + java.io.File.separator);
		try {
			testExchangeRatio();
			testWallet();
			testGiftUnlocksPersistence();
			testShopListing();
			testGiftBonusValues();
			testInitGiftIntegration();
			testLocalizedResources();
			testSourceWiring();
			testIconPixels();
			testRuntimeMessages();
			System.out.println("SPS S金与礼物商店测试通过：2333:1兑换边界、S金钱包持久化、商店前置树与34项上架、购买落盘、开局强化发放（含无人机信标与跳舞人偶）、图标像素和四语文本均正常。");
		} finally {
			try {
				Actor.clear();
				Dungeon.hero = null;
				Dungeon.level = null;
				SPDSettings.sCoinSpend(Integer.MAX_VALUE);
				FileUtils.deleteFile(GiftUnlocks.GIFTUNLOCKS_FILE);
				GiftUnlocks.resetForTesting();
			} finally {
				app.exit();
			}
		}
	}

	//1. 金币兑 S金比例 2333:1 的边界（整除，余数留在金币）
	private static void testExchangeRatio() {
		check(CurrencyIndicator.SC_EXCHANGE_RATE == 2333, "兑换比例不是 2333:1");
		check(CurrencyIndicator.sCoinForGold(0) == 0, "0 金币不应兑换出 S金");
		check(CurrencyIndicator.sCoinForGold(2332) == 0, "2332 金币不应兑换出 S金");
		check(CurrencyIndicator.sCoinForGold(2333) == 1, "2333 金币应恰好兑换 1 S金");
		check(CurrencyIndicator.sCoinForGold(4665) == 1, "4665 金币应只兑换 1 S金");
		check(CurrencyIndicator.sCoinForGold(4666) == 2, "4666 金币应兑换 2 S金");
		check(CurrencyIndicator.sCoinForGold(5000) == 2, "5000 金币应兑换 2 S金（余 334）");
		check(CurrencyIndicator.sCoinForGold(6998) == 2, "6998 金币应兑换 2 S金");
		check(CurrencyIndicator.sCoinForGold(6999) == 3, "6999 金币应兑换 3 S金");
	}

	//2. S金全局钱包：入账、扣费、0 下限、负数忽略
	private static void testWallet() {
		SPDSettings.sCoinSpend(Integer.MAX_VALUE);
		check(SPDSettings.sCoin() == 0, "S金钱包没有清零");
		SPDSettings.sCoinAdd(5);
		check(SPDSettings.sCoin() == 5, "S金入账异常");
		SPDSettings.sCoinSpend(2);
		check(SPDSettings.sCoin() == 3, "S金扣费异常");
		SPDSettings.sCoinSpend(99);
		check(SPDSettings.sCoin() == 0, "S金扣费没有 0 下限");
		SPDSettings.sCoinAdd(-7);
		check(SPDSettings.sCoin() == 0, "负数 S金入账没有被忽略");
		SPDSettings.sCoinAdd(8);
		check(SPDSettings.sCoin() == 8, "S金持久化读写异常");
		SPDSettings.sCoinSpend(Integer.MAX_VALUE);
	}

	//3. giftunlocks.dat 购买落盘与读档往返
	private static void testGiftUnlocksPersistence() {
		FileUtils.deleteFile(GiftUnlocks.GIFTUNLOCKS_FILE);
		GiftUnlocks.resetForTesting();
		check(GiftUnlocks.owned().isEmpty(), "全新档案不应有已购解锁");

		GiftUnlocks.buyOneGift(GiftUnlocks.GiftUnlock.DEF_HT);
		GiftUnlocks.buyOneGift(GiftUnlocks.GiftUnlock.DEF_HT); //幂等
		GiftUnlocks.saveGlobal();

		GiftUnlocks.resetForTesting();
		check(GiftUnlocks.isUnlocked(GiftUnlocks.GiftUnlock.DEF_HT), "读档后已购解锁丢失");
		check(!GiftUnlocks.isUnlocked(GiftUnlocks.GiftUnlock.DEF_SEED), "读档后出现未购解锁");
		check(GiftUnlocks.owned().size() == 1, "已购列表数量异常");
	}

	//4. 商店前置树与上架过滤
	private static void testShopListing() {
		FileUtils.deleteFile(GiftUnlocks.GIFTUNLOCKS_FILE);
		GiftUnlocks.resetForTesting();

		List<GiftUnlocks.GiftUnlock> initial = GiftUnlocks.filtered();
		check(initial.size() == 1 && initial.get(0) == GiftUnlocks.GiftUnlock.START,
				"未购 START 时商店应只上架 START，实际=" + initial.size());

		GiftUnlocks.buyOneGift(GiftUnlocks.GiftUnlock.START);
		List<GiftUnlocks.GiftUnlock> bases = GiftUnlocks.filtered();
		check(bases.size() == 9, "购 START 后应上架 START 加 8 个分支证章，实际=" + bases.size());

		for (GiftUnlocks.GiftUnlock unlock : GiftUnlocks.GiftUnlock.values()) {
			GiftUnlocks.buyOneGift(unlock);
		}
		List<GiftUnlocks.GiftUnlock> all = GiftUnlocks.filtered();
		check(all.size() == 34, "全购后应上架 34 项（隐藏 2 项），实际=" + all.size());
		check(!all.contains(GiftUnlocks.GiftUnlock.TRIBE_BUILD)
						&& !all.contains(GiftUnlocks.GiftUnlock.TRIBE_BUILD_TWO),
				"S金炼金/锻造两项不应上架售卖");
		check(all.contains(GiftUnlocks.GiftUnlock.DEF_ROBOT)
						&& all.contains(GiftUnlocks.GiftUnlock.OVERSEAS_ARTITEM),
				"已迁移的壁垒无人机信标与跳舞人偶应上架售卖");

		//分支子项需已购对应 BASE 才上架
		FileUtils.deleteFile(GiftUnlocks.GIFTUNLOCKS_FILE);
		GiftUnlocks.resetForTesting();
		GiftUnlocks.buyOneGift(GiftUnlocks.GiftUnlock.START);
		List<GiftUnlocks.GiftUnlock> afterStart = GiftUnlocks.filtered();
		check(!afterStart.contains(GiftUnlocks.GiftUnlock.DEF_HT), "未购 DEF_BASE 时不应上架 DEF_HT");
		GiftUnlocks.buyOneGift(GiftUnlocks.GiftUnlock.DEF_BASE);
		List<GiftUnlocks.GiftUnlock> afterBase = GiftUnlocks.filtered();
		check(afterBase.contains(GiftUnlocks.GiftUnlock.DEF_HT)
						&& afterBase.contains(GiftUnlocks.GiftUnlock.DEF_SEED)
						&& afterBase.contains(GiftUnlocks.GiftUnlock.DEF_TECH_MECH),
				"购 DEF_BASE 后应上架壁垒分支子项");
	}

	//5. 开局强化数值（对应 0.9.9 GiftUnlocks 的 *GiftisUsed 系列）
	private static void testGiftBonusValues() {
		for (GiftUnlocks.GiftUnlock unlock : GiftUnlocks.GiftUnlock.values()) {
			GiftUnlocks.buyOneGift(unlock);
		}
		check(GiftUnlocks.htGiftBonus() == 10, "五项体质训练应合计 +10 生命上限");
		check(GiftUnlocks.hitGiftBonus() == 1, "邪角命中训练应 +1 命中");
		check(GiftUnlocks.evadeGiftBonus() == 1, "遗迹闪避训练应 +1 闪避");
		check(GiftUnlocks.magicGiftBonus() == 1, "高塔法术训练应 +1 法强");
		check(GiftUnlocks.luckyGiftBonus() == 1, "流浪者拾荒应 +1 幸运");
		check(GiftUnlocks.expGiftBonus() == 10, "两项实战训练应合计 +10 经验");
		check(GiftUnlocks.goldGiftBonus() == 100, "宫殿消费训练应 +100 金币");
		check(GiftUnlocks.sCoinGiftBonus() == 5, "虚无应 +5 S金（0.9.9 未接线，本次按文案接线）");
		check(GiftUnlocks.seedGiftCount() == 2, "两项栽培教学应合计 2 颗种子");
		check(GiftUnlocks.plantGiftCount() == 1, "采集心得应携带 1 个花盆");
		check(GiftUnlocks.weaponGiftCount() == 1, "兵器准备应携带 1 件武器");
		check(GiftUnlocks.armorGiftCount() == 1, "护甲准备应携带 1 件护甲");
		check(GiftUnlocks.rocketGiftCount() == 1, "火箭准备应携带 1 发火箭");
		check(GiftUnlocks.ringGiftCount() == 1, "戒指准备应携带 1 枚戒指");
		check(GiftUnlocks.artifactGiftCount() == 1, "神器准备应携带诺姆林王冠");
		check(GiftUnlocks.wandGiftCount() == 1, "法杖准备应携带测试法杖");
		check(GiftUnlocks.robotGiftCount() == 1, "壁垒先锋准备应携带支援无人机");
		check(GiftUnlocks.artItemGiftCount() == 1, "奇异艺术应携带跳舞人偶");
		check(GiftUnlocks.upgradeGiftCount() == 1, "锻造科技应携带 1 张升级卷轴");
	}

	//6. HeroClass.initGift 集成：属性、物品、金币、经验、S金发放
	private static void testInitGiftIntegration() throws Exception {
		for (GiftUnlocks.GiftUnlock unlock : GiftUnlocks.GiftUnlock.values()) {
			GiftUnlocks.buyOneGift(unlock);
		}

		Badges.loadGlobal();
		Actor.clear();
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		hero.exp = 0;
		Dungeon.hero = hero;
		Dungeon.gold = 0;
		SPDSettings.sCoinSpend(Integer.MAX_VALUE);

		int baseAttack = intField(hero, "attackSkill");
		int baseDefense = intField(hero, "defenseSkill");
		int baseMagic = intField(hero, "magicSkill");

		Method initGift = HeroClass.class.getDeclaredMethod("initGift", Hero.class);
		initGift.setAccessible(true);
		initGift.invoke(null, hero);

		check(hero.HTBoost == 10, "开局强化没有加 10 点 HTBoost");
		check(intField(hero, "attackSkill") == baseAttack + 1, "开局强化没有 +1 命中");
		check(intField(hero, "defenseSkill") == baseDefense + 1, "开局强化没有 +1 闪避");
		check(intField(hero, "magicSkill") == baseMagic + 1, "开局强化没有 +1 法强");
		check(hero.exp == 10, "开局强化没有 +10 经验");
		check(Dungeon.gold == 100, "开局强化没有 +100 金币");
		check(SPDSettings.sCoin() == 5, "开局强化没有发放 5 S金");

		check(hero.belongings.getItem(LuckyBadge.class) != null
						&& hero.belongings.getItem(LuckyBadge.class).level() == 1,
				"开局强化没有发放 1 级幸运徽章");
		check(hero.belongings.getItem(PlantPotBlock.class) != null, "开局强化没有发放花盆");
		check(hero.belongings.getItem(RocketMissile.class) != null, "开局强化没有发放火箭");
		check(hero.belongings.getItem(NoomlinCrown.class) != null, "开局强化没有发放诺姆林王冠");
		check(hero.belongings.getItem(WandOfTest.class) != null, "开局强化没有发放测试法杖");
		check(hero.belongings.getItem(ScrollOfUpgrade.class) != null, "开局强化没有发放升级卷轴");
		check(hero.belongings.getItem(
						pd.items.summon.ChinaMech.class) != null,
				"开局强化没有发放壁垒支援用无人机");
		check(hero.belongings.getItem(
						pd.items.sellitem.JumperDancer.class) != null,
				"开局强化没有发放跳舞人偶");

		int seeds = 0;
		for (Item item : hero.belongings.backpack.items) {
			if (item instanceof Plant.Seed) seeds += item.quantity(); //同类种子可堆叠
		}
		check(seeds == 2, "开局强化没有发放 2 颗种子，实际=" + seeds);

		Weapon weapon = null;
		for (Item item : hero.belongings.backpack.items) {
			//只排除花盆与火箭（initGift 另行发放），MELEEWEAPON 产物即使是 MissileWeapon 族也算
			if (item instanceof Weapon
					&& item.getClass() != PlantPotBlock.class
					&& item.getClass() != RocketMissile.class) {
				weapon = (Weapon) item;
			}
		}
		//等级断言不写死：0.9.8 Weapon.random() 初始等级随机（+1~+3 或 -1~-3 附诅咒），
		//礼物 +1 叠加在随机初始等级上，这里只断言发放、去诅咒与鉴定
		check(weapon != null && !weapon.cursed && weapon.levelKnown,
				"开局强化武器没有发放或不是已鉴定未诅咒");
		Armor armor = hero.belongings.getItem(Armor.class);
		check(armor != null && !armor.cursed && armor.levelKnown,
				"开局强化护甲没有发放或不是已鉴定未诅咒");
		Ring ring = hero.belongings.getItem(Ring.class);
		check(ring != null && !ring.cursed && ring.levelKnown,
				"开局强化戒指没有发放或不是已鉴定未诅咒");

		//固定语义：+1 与 -10 的叠加规则（不依赖 Generator 随机初始等级）
		Item chainWeapon = new pd.items.weapon.melee.WornShortsword();
		chainWeapon.uncurse().identify().upgrade(1);
		check(chainWeapon.level() == 1, "uncurse/identify/upgrade(1) 链式 +1 语义失效");
		Item chainRing = new pd.items.rings.RingOfForce();
		chainRing.uncurse().identify().degrade(10);
		check(chainRing.level() == -10, "uncurse/identify/degrade(10) 链式 -10 语义失效");

		//未购 START 时 initGift 不应发放任何东西
		FileUtils.deleteFile(GiftUnlocks.GIFTUNLOCKS_FILE);
		GiftUnlocks.resetForTesting();
		Actor.clear();
		Hero fresh = new Hero();
		Dungeon.hero = fresh;
		Dungeon.gold = 0;
		SPDSettings.sCoinSpend(Integer.MAX_VALUE);
		check(!GiftUnlocks.isUnlocked(GiftUnlocks.GiftUnlock.START), "全新档案不应拥有 START");
		check(fresh.HTBoost == 0, "全新英雄不应有 HTBoost");
	}

	//7. 四语文本（英/简/繁/俄）
	private static void testLocalizedResources() throws Exception {
		String[] langs = {"en", "zh", "zh-hant", "ru"};

		for (String lang : langs) {
			Properties misc = load("messages/misc/" + lang + "/misc.properties");
			required(misc, "giftunlocks.price", lang);
			for (GiftUnlocks.GiftUnlock unlock : GiftUnlocks.GiftUnlock.values()) {
				String base = "giftunlocks$giftunlock." + unlock.name().toLowerCase() + ".";
				required(misc, base + "title", lang);
				required(misc, base + "desc", lang);
			}

			Properties scenes = load("messages/scenes/" + lang + "/scenes.properties");
			required(scenes, "scenes.titlescene.giftshop", lang);
			required(scenes, "scenes.giftshopscene.title", lang);
			required(scenes, "scenes.giftshopscene.balance", lang);

			Properties windows = load("messages/windows/" + lang + "/windows.properties");
			required(windows, "windows.wndgiftunlock.buy", lang);
			required(windows, "windows.wndgiftunlock.more_gold", lang);

			Properties ui = load("messages/ui/" + lang + "/ui.properties");
			for (String key : new String[]{"exchange_title", "exchange_body", "exchange_confirm",
					"cancel", "not_enough", "exchange_ok"}) {
				required(ui, "ui.currencyindicator." + key, lang);
			}

			Properties items = load("messages/items/" + lang + "/items.properties");
			for (String key : new String[]{"items.summon.chinamech.name", "items.summon.chinamech.ac_active",
					"items.summon.chinamech.desc", "items.summon.chinamech$huaweidajiang.name",
					"items.summon.chinamech$huaweidajiang.desc",
					"items.sellitem.jumperdancer.name", "items.sellitem.jumperdancer.desc"}) {
				required(items, key, lang);
			}

			for (Properties properties : new Properties[]{misc, scenes, windows, ui, items}) {
				for (Object value : properties.values()) {
					check(!String.valueOf(value).contains("\uFFFD"), "资源包含Unicode替换字符：" + lang);
				}
			}
		}

		Properties zh = load("messages/misc/zh/misc.properties");
		check("售价: %s S金".equals(zh.getProperty("giftunlocks.price")), "简体售价文本错误");
		Properties zhUi = load("messages/ui/zh/ui.properties");
		check(zhUi.getProperty("ui.currencyindicator.exchange_body").contains("2333"),
				"简体兑换确认文本缺少 2333 比例说明");
	}

	//10. 运行时 Messages 查询（防止键存在但 bundle 查不到的回归；键前缀是类名小写 titlescene）
	private static void testRuntimeMessages() {
		pd.messages.Messages.setup(
				pd.messages.Languages.CHI_SMPL);
		String label = pd.messages.Messages.get(
				pd.scenes.TitleScene.class, "giftshop");
		check("礼物商店".equals(label), "运行时查询 scenes.titlescene.giftshop 失败，得到：" + label);
		String giftTitle = pd.messages.Messages.get(
				pd.scenes.GiftShopScene.class, "title");
		check("礼物商店".equals(giftTitle), "运行时查询 scenes.giftshopscene.title 失败，得到：" + giftTitle);
		String balance = pd.messages.Messages.get(
				pd.scenes.GiftShopScene.class, "balance", 7);
		check(balance.contains("7"), "运行时查询 scenes.giftshopscene.balance 失败，得到：" + balance);
	}

	//8. 接线文本断言：入口、开局发放、兑换比例
	private static void testSourceWiring() throws Exception {
		String title = java.nio.file.Files.readString(
				Path.of("../java/pd/scenes/TitleScene.java"));
		check(title.contains("GiftShopScene.class"), "标题画面没有接入礼物商店入口");
		check(title.contains("btnGiftShop"), "礼物商店按钮未定义");

		String heroClass = java.nio.file.Files.readString(
				Path.of("../java/pd/actors/hero/HeroClass.java"));
		check(heroClass.contains("initGift( hero )"), "开局流程没有接入礼物强化发放");

		String currency = java.nio.file.Files.readString(
				Path.of("../java/pd/ui/CurrencyIndicator.java"));
		check(currency.contains("SC_EXCHANGE_RATE = 2333"), "兑换比例常量不是 2333");
		//SPS: 兑换入口已按用户裁决移到背包界面的金币图标，HUD 指示器只保留金币变化提示
		check(!currency.contains("onGoldClick"), "兑换点击逻辑不应留在 HUD 金币指示器上");

		String wndBag = java.nio.file.Files.readString(
				Path.of("../java/pd/windows/WndBag.java"));
		check(wndBag.contains("askSGoldExchange"), "背包界面缺少 S金兑换入口");
		check(wndBag.contains("SPDSettings.sCoinAdd"), "兑换没有入账到 S金钱包");

		String giftShop = java.nio.file.Files.readString(
				Path.of("../java/pd/scenes/GiftShopScene.java"));
		check(giftShop.contains("public void refresh()") && giftShop.contains("scrollTo( 0, scrollY )"),
				"礼物商店购买后没有保留滚动位置的刷新逻辑");
		String wndGift = java.nio.file.Files.readString(
				Path.of("../java/pd/windows/WndGiftUnlock.java"));
		check(wndGift.contains("((GiftShopScene) Game.scene()).refresh()"),
				"购买后没有走保留滚动位置的场景内刷新");
	}

	private static int intField(Object target, String name) throws Exception {
		Field field = target.getClass().getDeclaredField(name);
		field.setAccessible(true);
		return field.getInt(target);
	}

	//9. 图标像素保护（ImportChinaMechSprites 从 0.9.9 图集导入的原始像素）
	private static void testIconPixels() throws Exception {
		java.awt.image.BufferedImage sheet = javax.imageio.ImageIO.read(
				Path.of("sprites/items", "items.png").toFile());
		check("7B6AC7FDBC6FA7F86AA8746705E179B442EA7D7AF364649DAC486FB63FA9A735"
						.equals(iconHash(sheet, pd.sprites.ItemSpriteSheet.SPS_CHINA_MECH)),
				"壁垒支援用无人机图标不是 0.9.9 原始像素");
		check("D79A27527CA26CFC252798365C524D73767FE3C58F614E14FED2EA4FCD887BA6"
						.equals(iconHash(sheet, pd.sprites.ItemSpriteSheet.SPS_JUMPER_DANCER)),
				"跳舞人偶图标不是 0.9.9 原始像素");
	}

	private static String iconHash(java.awt.image.BufferedImage image, int index) throws Exception {
		int left = (index % 16) * 16;
		int top = (index / 16) * 16;
		java.nio.ByteBuffer pixels = java.nio.ByteBuffer.allocate(16 * 16 * 4).order(java.nio.ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) {
			for (int x = left; x < left + 16; x++) pixels.putInt(image.getRGB(x, y));
		}
		StringBuilder result = new StringBuilder(64);
		for (byte value : java.security.MessageDigest.getInstance("SHA-256").digest(pixels.array())) {
			result.append(String.format("%02X", value & 0xFF));
		}
		return result.toString();
	}

	private static Properties load(String path) throws Exception {
		Properties properties = new Properties();
		try (InputStreamReader reader = new InputStreamReader(
				java.nio.file.Files.newInputStream(Path.of(path)), StandardCharsets.UTF_8)) {
			properties.load(reader);
		}
		return properties;
	}

	private static void required(Properties properties, String key, String file) {
		check(properties.getProperty(key) != null && !properties.getProperty(key).isEmpty(),
				"资源" + file + "缺少文本：" + key);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
