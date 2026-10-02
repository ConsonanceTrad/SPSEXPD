package pd.items.specific.sellitem;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.mobs.npcs.Blacksmith;
import pd.actors.mobs.npcs.MirrorImage;
import pd.actors.mobs.npcs.Sheep;
import pd.actors.mobs.npcs.Shopkeeper;
import pd.actors.mobs.npcs.SpsSokobanSheep;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.Item;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;

import javax.imageio.ImageIO;

public final class SpsSellItemsTest {

	private static final Class<?>[] TYPES = {
			Apk931.class, BottleFlower.class, BrokenHammer.class, CrossPhoto.class,
			DwarfHammer.class, HummingTool.class, Mirror2.class, NouthSouth.class,
			SellPermit.class, SheepFur.class, Simple360.class, Tissue.class, UncleDumbbell.class
	};
	private static final int[] VALUES = {150, 1000, 30, 150, 150, 120, 112, 500, 50, 50, 80, 120, 100};
	private static final String[] HASHES = {
			"A669CE73B3B71F8EAB7C033B27FEBB25FA287431768E1B8CFDE0743A2385138B",
			"4BADBD2E9E535D64974FFCEF12E5BB0AB43E522C916F09E00D20B7E25A8AAFF3",
			"1F0C76B8272DB6E08A9518EA0C0970229894A74BAC653C870B5ED082F7E46617",
			"2BFDD279F34661535D0EA2A897DD002E03C33657AD77A27750A96362FC4946EE",
			"605F35F80B382AE6BFA47C62B2EAA5B8F39F998982DF8089CCED03EE758E80C5",
			"2699AF0894A8E742560B98D65DC42513D2A1038641B98A580024CC5E860F6E28",
			"DFC1FE0A7C0EB763139320889C1A3497E0F8BF571F8DAAAB4F55365F98CCC198",
			"5200D4C0D5DBA7EA8E80EB5ADE8AE7287DD8076F0C97DC843FF92E3C9B9EBA59",
			"823B5F9A04A03DD25B628302C08F86BBBC6E701FB84A7F907CAF0BD77A343EA8",
			"5D7D604F59E9880D01471A409BFC89E4822E3B095B3A9029DBAD055F48DF6FAA",
			"9D58C3DCB332C4E65918CBC32D349AD0D0AD5911DE6D12D09841385B71E9E65B",
			"6E83DFD28FA7FB8696FA2DEC242E97D2874B82A7D02DD208DCD1CA33D173F445",
			"7158CD5EE8D9B80C9546A14AA5E9B2059BBEF7A1B5D5EAE24B69B2AEAA529397"
	};
	private static final int[][] ICON_POSITIONS = {
			{112, 880}, {128, 880}, {144, 880}, {160, 880}, {176, 880},
			{192, 880}, {208, 880}, {224, 880}, {240, 880}, {160, 848},
			{176, 848}, {192, 848}, {208, 848}
	};

	public static void main(String[] args) throws Exception {
		testItems();
		testSources();
		testIcons();
		System.out.println("SPS纪念品测试通过：13件旧版物品、售价、堆叠、城镇NPC、商人、羊和原始图标均正常。");
	}

	private static void testItems() throws Exception {
		for (int i = 0; i < TYPES.length; i++) {
			Item item = (Item)TYPES[i].getDeclaredConstructor().newInstance();
			item.quantity(3);
			check(item.value() == VALUES[i] * 3, TYPES[i].getSimpleName() + "售价错误");
			check(item.stackable && item.isIdentified() && !item.isUpgradable(),
					TYPES[i].getSimpleName() + "基础属性错误");
		}
		HunterLens lens = new HunterLens();
		lens.quantity(2);
		check(lens.value() == 1000 && lens.stackable, "眼魔晶状体售价或堆叠错误");
	}

	private static void testSources() {
		TownNpc.Spec[] specs = {
				TownNpc.Spec.G2159687, TownNpc.Spec.A_REAL_MAN, TownNpc.Spec.JINKELOID,
				TownNpc.Spec.MEMORY_OF_SAND, TownNpc.Spec.MILLILITRE, TownNpc.Spec.GOBLIN_PLAYER,
				TownNpc.Spec.OTILUKE_NPC, TownNpc.Spec.SP931, TownNpc.Spec.STORM_AND_RAIN,
				TownNpc.Spec.UNCLE_S
		};
		Class<?>[] rewards = {
				Simple360.class, NouthSouth.class, BottleFlower.class, Tissue.class, CrossPhoto.class,
				HummingTool.class, DwarfHammer.class, Apk931.class, HunterLens.class, UncleDumbbell.class
		};
		for (int i = 0; i < specs.length; i++) {
			check(rewards[i].isInstance(new TownNpc().configure(specs[i]).SupercreateLoot()),
					specs[i] + "特殊掉落错误");
		}
		check(new Shopkeeper().SupercreateLoot() instanceof SellPermit, "商店老板没有经营许可证掉落");
		check(new Blacksmith().SupercreateLoot() instanceof BrokenHammer, "铁匠没有损坏铁锤掉落");
		check(new MirrorImage().SupercreateLoot() instanceof Mirror2, "镜像没有镜像碎片掉落");
		check(new Sheep().SupercreateLoot() instanceof SheepFur, "普通羊没有羊毛掉落");
		check(new SpsSokobanSheep().SupercreateLoot() instanceof SheepFur, "推箱羊没有羊毛掉落");

		Dungeon.hero = new Hero();
		Dungeon.depth = 1;
		Dungeon.branch = 0;
		BrokenHammer hammer = new BrokenHammer();
		check(Shopkeeper.sellPrice(hammer) == 150, "主线首层商店价格不符合旧版5倍公式");
		Dungeon.depth = pd.items.quest.AdventureJournal.anchorDepth(22);
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(22);
		check(Shopkeeper.sellPrice(hammer) == 750, "高深度商店价格没有按旧版25倍封顶");
		Dungeon.hero.heroClass = HeroClass.FOLLOWER;
		check(Shopkeeper.sellPrice(hammer) == 600, "追随者商店价格没有按旧版20倍封顶");
		Dungeon.depth = 1;
		Dungeon.branch = 0;
	}

	private static void testIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		for (int i = 0; i < HASHES.length; i++) {
			String actual = hash(sheet, ICON_POSITIONS[i][0], ICON_POSITIONS[i][1]);
			check(HASHES[i].equals(actual), TYPES[i].getSimpleName() + "图标与旧版像素不一致：" + actual);
		}
	}

	private static String hash(BufferedImage sheet, int left, int top) throws Exception {
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder out = new StringBuilder(64);
		for (byte value : digest) out.append(String.format("%02X", value & 0xFF));
		return out.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsSellItemsTest() { }
}
