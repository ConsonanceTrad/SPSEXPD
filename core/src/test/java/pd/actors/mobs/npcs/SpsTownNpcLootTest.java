package pd.actors.mobs.npcs;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Flag;
import pd.items.Item;
import pd.levels.Level;
import pd.sprites.ItemSpriteSheet;
import pd.windows.WndAscend;
import pd.windows.WndDream;
import pd.windows.WndHate;
import pd.windows.WndHotel;
import pd.windows.WndIssic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

import javax.imageio.ImageIO;

public final class SpsTownNpcLootTest {

	private static final TownNpc.Spec[] SPECS = {
			TownNpc.Spec.HBB, TownNpc.Spec.SHOWER, TownNpc.Spec.SAID_BY_SUN,
			TownNpc.Spec.G2159687, TownNpc.Spec.A_REAL_MAN, TownNpc.Spec.JINKELOID,
			TownNpc.Spec.MEMORY_OF_SAND, TownNpc.Spec.MILLILITRE, TownNpc.Spec.GOBLIN_PLAYER,
			TownNpc.Spec.OTILUKE_NPC, TownNpc.Spec.SP931, TownNpc.Spec.STORM_AND_RAIN,
			TownNpc.Spec.UNCLE_S, TownNpc.Spec.BILBOLDEV, TownNpc.Spec.DACHHACK,
			TownNpc.Spec.DREAM_PLAYER, TownNpc.Spec.FLY_LING, TownNpc.Spec.LAJI,
			TownNpc.Spec.NUT_PAINTER, TownNpc.Spec.TEMPEST102, TownNpc.Spec.APOSTLE,
			TownNpc.Spec.ALIVE_FISH, TownNpc.Spec.RAIN_TRAINER, TownNpc.Spec.RENNPC,
			TownNpc.Spec.WHITE_GHOST, TownNpc.Spec.A_FLY, TownNpc.Spec.COCONUT2,
			TownNpc.Spec.XIXI_ZERO, TownNpc.Spec.FRUIT_CAT, TownNpc.Spec.LERY,
			TownNpc.Spec.HONEY_POOOOT, TownNpc.Spec.LYNN, TownNpc.Spec.RUSTYBLADE,
			TownNpc.Spec.BLACK_MEOW, TownNpc.Spec.BONE_STAR, TownNpc.Spec.ICE13,
			TownNpc.Spec.SFB, TownNpc.Spec.ASH_WOLF, TownNpc.Spec.COCONUT,
			TownNpc.Spec.NEW_PLAYER
	};

	private static final String[] REWARDS = {
			"FishCracker", "FishPetFood", "TestCloak", "Simple360", "NouthSouth",
			"BottleFlower", "Tissue", "CrossPhoto", "HummingTool", "DwarfHammer",
			"Apk931", "HunterLens", "UncleDumbbell", "SkillOfAtk", "PlantPotBlock",
			"FunnyFood", "LingPotion", "CatSharkArmor", "FourClover", "TempestBoomerang",
			"ApostleBox", "FishBone", "RainShield", "CursePhone", "GhostGirlRose",
			"AFlySock", "MeleePan", "XiXiBox", "MoneyBook", "BottleFire", "HoneyArrow",
			"LynnDoll", "RustybladeCat", "WandOfBlackMeow", "CrystalVial", "WandOf13",
			"WandOfShatteredFireblast", "HaroEgg", "CocoCatEgg", "DevUpPlan"
	};

	public static void main(String[] args) throws Exception {
		testOriginalRuntimeIdentities();
		for (int i = 0; i < SPECS.length; i++) {
			Item reward = new TownNpc().configure(SPECS[i]).SupercreateLoot();
			check(reward != null && REWARDS[i].equals(reward.getClass().getSimpleName()),
					SPECS[i] + "特殊掉落错误：" + (reward == null ? "null" : reward.getClass().getSimpleName()));
		}
		testOtherLootSources();
		testHbbFlag();
		testResidentWindows();
		System.out.println("SPS城镇NPC物品测试通过：40名居民及4个独立NPC的特殊掉落、五名居民窗口交易、四语文本和原始图标均正常。");
	}

	private static void testOtherLootSources() {
		checkLoot(new Tinkerer1().SupercreateLoot(), "SellMushroom", "第一层工匠");
		checkLoot(new Leadercn().SupercreateLoot(), "DevUpPlan", "教程向导");
		checkLoot(new RatKing().SupercreateLoot(), "SewerReward", "鼠王");
		Item conditional = new TownNpc().configure(TownNpc.Spec.HMDZL001).SupercreateLoot();
		check(conditional != null && (conditional.getClass().getSimpleName().equals("DevUpPlan")
				|| conditional.getClass().getSimpleName().equals("SaveYourLife")),
				"Hmdzl001条件礼物映射错误");
	}

	private static void checkLoot(Item item, String expected, String source) {
		check(item != null && expected.equals(item.getClass().getSimpleName()),
				source + "特殊掉落错误：" + (item == null ? "null" : item.getClass().getSimpleName()));
	}

	private static void testResidentWindows() throws Exception {
		Hero hero = new Hero();
		hero.lvl = 12;
		hero.improveCombatSkills(14);
		hero.HTBoost = 7;
		WndDream.applyReset(hero);
		check(hero.lvl == 1 && hero.HTBoost == 20 && hero.HT == 50,
				"DreamPlayer没有把英雄重置为旧版的1级、50基础生命");
		java.lang.reflect.Field attack = Hero.class.getDeclaredField("attackSkill");
		java.lang.reflect.Field defense = Hero.class.getDeclaredField("defenseSkill");
		attack.setAccessible(true);
		defense.setAccessible(true);
		check(attack.getInt(hero) == 13 && defense.getInt(hero) == 8,
				"DreamPlayer没有按旧版规则扣除等级带来的攻防");
		check(!WndHotel.canBuy(100) && WndHotel.canBuy(101), "旅馆钥匙的严格100金币边界错误");
		check(!WndHate.canBuy(3000) && WndHate.canBuy(3001), "羊群法杖的严格3000金币边界错误");
		check(!WndIssic.canSellBlood(150) && WndIssic.canSellBlood(151), "卖血的严格150生命边界错误");
		Level surface = new Level() {
			@Override protected boolean build() { return true; }
			@Override protected void createMobs() { }
			@Override protected void createItems() { }
			@Override public String tilesTex() { return null; }
			@Override public String waterTex() { return null; }
		};
		check(!surface.forceDone, "地表离开确认状态初始值错误");
		WndAscend.confirmDeparture(surface);
		check(surface.forceDone, "WndAscend没有记录旧版地表离开确认");

		String[] files = {"en/windows.properties", "zh/windows.properties",
				"zh-hant/windows.properties", "ru/windows.properties"};
		String[] required = {"windows.wndegoalinfo.title=", "windows.wndhotel.message=",
				"windows.wnddream.message=", "windows.wndissic.message=", "windows.wndhate.message=",
				"windows.wndascend.message="};
		for (String file : files) {
			String text = Files.readString(Paths.get("messages/windows/" + file), StandardCharsets.UTF_8);
			check(!text.contains("\uFFFD"), file + "包含乱码替换字符");
			for (String key : required) check(text.contains(key), file + "缺少城镇居民窗口文本：" + key);
		}
	}

	private static void testOriginalRuntimeIdentities() {
		Class<?>[] expectedClasses = {
			Udawos.class,
			TypedScroll.class,
			G2159687.class,
			ConsideredHamster.class,
			Bilboldev.class,
			XixiZero.class,
			Millilitre.class,
			NYRDS.class,
			HBB.class,
			SFB.class,
			FlyLing.class,
			Omicronrg9.class,
			HoneyPoooot.class,
			Jinkeloid.class,
			ATV9.class,
			SP931.class,
			DreamPlayer.class,
			Evan.class,
			Ice13.class,
			HeXA.class,
			Coconut.class,
			Locastan.class,
			GoblinPlayer.class,
			Dachhack.class,
			OldNewStwist.class,
			HateSokoban.class,
			LaJi.class,
			Kostis12345.class,
			Apostle.class,
			NutPainter.class,
			Juh9870.class,
			SadSaltan.class,
			Shower.class,
			RENnpc.class,
			OtilukeNPC.class,
			Watabou.class,
			WhiteGhost.class,
			UncleS.class,
			ARealMan.class,
			Lyn.class,
			SaidbySun.class,
			Lery.class,
			BlackMeow.class,
			CatSheep.class,
			FruitCat.class,
			MemoryOfSand.class,
			AFly.class,
			BoneStar.class,
			StormAndRain.class,
			Ravenwolf.class,
			Lynn.class,
			RainTrainer.class,
			Rustyblade.class,
			Tempest102.class,
			AliveFish.class,
			AshWolf.class,
			Coconut2.class,
			Hmdzl001.class,
			NewPlayer.class,
			ThankList.class,
			Tinkerer4.class,
			Tinkerer5.class
		};
		Class<?>[] expectedSprites = {
			pd.sprites.UdawosSprite.class,
			pd.sprites.TypedScrollSprite.class,
			pd.sprites.G2159687Sprite.class,
			pd.sprites.MimicSprite.class,
			pd.sprites.BilboldevSprite.class,
			pd.sprites.XixiZeroSprite.class,
			pd.sprites.MillilitreSprite.class,
			pd.sprites.NYRDSSprite.class,
			pd.sprites.HBBSprite.class,
			pd.sprites.SFBSprite.class,
			pd.sprites.WhiteLingSprite.class,
			pd.sprites.Omicronrg9Sprite.class,
			pd.sprites.HoneyPooootSprite.class,
			pd.sprites.JinkeloidSprite.class,
			pd.sprites.ATV9Sprite.class,
			pd.sprites.SP931Sprite.class,
			pd.sprites.DreamPlayerSprite.class,
			pd.sprites.EvanSprite.class,
			pd.sprites.Ice13Sprite.class,
			pd.sprites.HeXASprite.class,
			pd.sprites.CoconutSprite.class,
			pd.sprites.LocastanSprite.class,
			pd.sprites.GoblinPlayerSprite.class,
			pd.sprites.DachhackSprite.class,
			pd.sprites.OldNewStwistSprite.class,
			pd.sprites.HateSokobanSprite.class,
			pd.sprites.LaJiSprite.class,
			pd.sprites.Kostis12345Sprite.class,
			pd.sprites.ApostleSprite.class,
			pd.sprites.PainterSprite.class,
			pd.sprites.Juh9870Sprite.class,
			pd.sprites.SadSaltanSprite.class,
			pd.sprites.ShowerSprite.class,
			pd.sprites.RENSprite.class,
			pd.sprites.OtilukeNPCSprite.class,
			pd.sprites.WatabouSprite.class,
			pd.sprites.WhiteGhostSprite.class,
			pd.sprites.UncleSSprite.class,
			pd.sprites.ARealManSprite.class,
			pd.sprites.LynSprite.class,
			pd.sprites.SaidbySunSprite.class,
			pd.sprites.LerySprite.class,
			pd.sprites.BlackMeowSprite.class,
			pd.sprites.CatSheepSprite.class,
			pd.sprites.FruitCatSprite.class,
			pd.sprites.MemoryOfSandSprite.class,
			pd.sprites.AFlySprite.class,
			pd.sprites.BoneStarSprite.class,
			pd.sprites.StormAndRainSprite.class,
			pd.sprites.RavenwolfSprite.class,
			pd.sprites.LynnSprite.class,
			pd.sprites.RainSprite.class,
			pd.sprites.RustybladeSprite.class,
			pd.sprites.Tempest102Sprite.class,
			pd.sprites.PiranhaSprite.class,
			pd.sprites.AshWolfSprite.class,
			pd.sprites.CoconutSprite.class,
			pd.sprites.Hmdzl001Sprite.class,
			pd.sprites.NewPlayerSprite.class,
			pd.sprites.ThankListSprite.class,
			pd.sprites.NoodlemireSprite.class,
			pd.sprites.Xavier251998Sprite.class
		};
		TownNpc.Spec[] specs = TownNpc.Spec.values();
		check(specs.length == expectedClasses.length, "城镇居民类型映射数量错误");
		for (int i = 0; i < specs.length; i++) {
			TownNpc npc = TownNpc.create(specs[i]);
			check(npc.getClass() == expectedClasses[i], specs[i] + "没有生成旧版顶层NPC类");
			check(npc.spriteClass == expectedSprites[i], specs[i] + "没有使用旧版精灵身份");
		}

		TownNpc apostle = TownNpc.create(TownNpc.Spec.APOSTLE);
		check(apostle.properties().contains(Char.Property.MECH)
				&& apostle.properties().contains(Char.Property.ELEMENT)
				&& !apostle.properties().contains(Char.Property.IMMOVABLE), "Apostle阵营错误");
		TownNpc afly = TownNpc.create(TownNpc.Spec.A_FLY);
		check(afly.properties().contains(Char.Property.ELF)
				&& afly.properties().contains(Char.Property.DEMONIC)
				&& afly.state == afly.WANDERING, "AFly阵营或游荡状态错误");
		TownNpc typed = TownNpc.create(TownNpc.Spec.TYPED_SCROLL);
		check(typed.properties().contains(Char.Property.DEMONIC)
				&& typed.properties().contains(Char.Property.UNKNOW), "TypedScroll阵营错误");
		TownNpc ash = TownNpc.create(TownNpc.Spec.ASH_WOLF);
		check(ash.properties().contains(Char.Property.ORC)
				&& ash.properties().contains(Char.Property.IMMOVABLE), "AshWolf阵营或固定状态错误");
		check(TownNpc.create(TownNpc.Spec.UDAWOS).properties().contains(Char.Property.DWARF),
				"Udawos矮人阵营缺失");
		check(TownNpc.create(TownNpc.Spec.DACHHACK).properties().contains(Char.Property.PLANT),
				"Dachhack植物阵营缺失");
		TownNpc oldNew = TownNpc.create(TownNpc.Spec.OLD_NEW_STWIST);
		check(oldNew.state == oldNew.SLEEPING, "OldNewStwist没有以沉睡状态生成");
	}

	private static void testHbbFlag() throws Exception {
		TownNpc hbb = new TownNpc().configure(TownNpc.Spec.HBB);
		check(hbb.hbbReward(3, true) instanceof Flag, "营救奥蒂卢克后HBB没有发放军旗");
		check(hbb.hbbReward(0, true) == null && hbb.hbbReward(3, false) == null,
				"HBB在错误的交互分支或营救前发放军旗");
		Flag flag = new Flag();
		check(flag.image == ItemSpriteSheet.SPS_FLAG && flag.unique && !flag.stackable,
				"军旗图标或唯一属性错误");
		check(!flag.actions(new Hero()).contains(Item.AC_DROP)
				&& !flag.actions(new Hero()).contains(Item.AC_THROW), "军旗可以被丢弃或投掷");

		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check("7B6AC7FDBC6FA7F86AA8746705E179B442EA7D7AF364649DAC486FB63FA9A735"
				.equals(iconHash(sheet, 240, 944)), "军旗原始图标错误");
		String zh = Files.readString(Paths.get("messages/items/zh/items.properties"), StandardCharsets.UTF_8);
		String en = Files.readString(Paths.get("messages/items/en/items.properties"), StandardCharsets.UTF_8);
		check(zh.contains("items.flag.name=军旗") && en.contains("items.flag.name=flag")
				&& !zh.contains("\uFFFD") && !en.contains("\uFFFD"), "军旗双语文本缺失或乱码");
	}

	private static String iconHash(BufferedImage image, int left, int top) throws Exception {
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) {
			for (int x = left; x < left + 16; x++) pixels.putInt(image.getRGB(x, y));
		}
		byte[] hash = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder result = new StringBuilder(hash.length * 2);
		for (byte value : hash) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsTownNpcLootTest() { }
}
