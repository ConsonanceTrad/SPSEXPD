package pd.items.summon;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Slow;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.Mob;
import pd.items.bags.ScrollHolder;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.sprites.CocoCatSprite;
import pd.sprites.ItemSpriteSheet;
import com.watabou.utils.SparseArray;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

public final class SpsCallCoconutTest {
	private static final String ITEM_HASH = "B7AED8C96A421E90AE14D7809FE8B5E9D0766EBEBBA624E6807E1D9E3EB640D2";
	private static final String SPRITE_HASH = "B9E59C6BED0E3BEEEF8765DA059845F45285C35906E4D282E7FC979B19AE143F";

	public static void main(String[] args) throws Exception {
		try {
			testItemAndPlacement();
			testNormalCoconut();
			testLeaderCoconut();
			testResourcesAndPixels();
			System.out.println("SPS召唤钥匙通过：安全落点、普通与领袖椰子猫、战斗、自损、爆炸概率、状态免疫和原始素材均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
		}
	}

	private static void testItemAndPlacement() {
		Actor.clear();
		TestLevel level = level();
		hero(9, HeroSubClass.NONE);
		CallCoconut key = new CallCoconut();
		key.quantity(3);
		check(key.stackable && key.value() == 300 && key.isIdentified() && !key.isUpgradable(),
				"召唤钥匙基础属性错误");
		check(key.image == ItemSpriteSheet.SPS_CALL_COCONUT && key.defaultAction().equals("ACTIVE"),
				"召唤钥匙图标或默认动作错误");
		check(new ScrollHolder().canHold(key), "卷轴筒无法收纳召唤钥匙");

		PlainMob blocker = new PlainMob();
		blocker.pos = 0;
		Actor.add(blocker);
		int destination = CallCoconut.summonCell(0);
		check(destination >= 0 && destination < level.length() && destination != 0,
				"地图边缘或占位目标没有找到安全相邻格");
		Arrays.fill(level.passable, false);
		check(CallCoconut.summonCell(0) == -1, "没有合法落点时仍返回地图位置");
	}

	private static void testNormalCoconut() {
		Actor.clear();
		level();
		Dungeon.depth = 10;
		hero(9, HeroSubClass.NONE);
		CallCoconut.Scococat cat = (CallCoconut.Scococat)new CallCoconut().summonAt(18);
		check(cat.HT == 200 && cat.HP == 200 && cat.defenseSkill == 0 && cat.attackSkill(Dungeon.hero) == 50,
				"普通椰子猫生命、防御或命中错误");
		check(cat.alignment == Char.Alignment.ALLY && cat.properties().contains(Char.Property.BEAST)
				&& cat.spriteClass == CocoCatSprite.class && cat.bombDenominator() == 10,
				"普通椰子猫阵营、属性、动画或爆炸概率错误");
		for (int i = 0; i < 50; i++) {
			int damage = cat.damageRoll();
			check(damage >= 20 && damage <= 26, "普通椰子猫伤害越界：" + damage);
		}
		cat.decayTurn();
		check(cat.HP == 199, "普通椰子猫没有逐回合损失1点生命");
		check(!cat.add(new Slow()), "普通椰子猫错误接受了状态效果");
	}

	private static void testLeaderCoconut() {
		Actor.clear();
		level();
		Dungeon.depth = 10;
		hero(9, HeroSubClass.LEADER);
		CallCoconut.EXcococat cat = (CallCoconut.EXcococat)new CallCoconut().summonAt(18);
		check(cat.HT == 400 && cat.HP == 400 && cat.defenseSkill == 20 && cat.attackSkill(Dungeon.hero) == 70,
				"领袖椰子猫生命、防御或命中错误");
		check(cat.bombDenominator() == 5, "领袖椰子猫爆炸概率不是20%线性判定");
		PlainMob near = new PlainMob(); near.pos = 22;
		PlainMob far = new PlainMob(); far.pos = 23;
		check(cat.inRange(near) && !cat.inRange(far), "椰子猫四格攻击距离错误");
		for (int i = 0; i < 50; i++) {
			int damage = cat.damageRoll();
			check(damage >= 30 && damage <= 42, "领袖椰子猫伤害越界：" + damage);
		}
	}

	private static void testResourcesAndPixels() throws Exception {
		String zh = Files.readString(Paths.get("messages/items/items_zh.properties"), StandardCharsets.UTF_8);
		String en = Files.readString(Paths.get("messages/items/items.properties"), StandardCharsets.UTF_8);
		String[] keys = {"items.summon.callcoconut.name=", "items.summon.callcoconut.ac_active=",
				"items.summon.callcoconut$scococat.name=", "items.summon.callcoconut$excococat.name="};
		for (String key : keys) check(zh.contains(key) && en.contains(key), "中英文资源缺少键：" + key);
		check(zh.contains("召唤钥匙") && zh.contains("EX椰子猫") && !zh.contains("�"), "召唤钥匙中文乱码或缺失");

		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(ITEM_HASH.equals(hashItem(sheet, ItemSpriteSheet.SPS_CALL_COCONUT)), "召唤钥匙不是旧版原始图标");
		byte[] sprite = Files.readAllBytes(Paths.get("sprites/npcs/sps_town_coconut.png"));
		check(SPRITE_HASH.equals(hex(MessageDigest.getInstance("SHA-256").digest(sprite))),
				"椰子猫角色图不是旧版原始素材");
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

	private static String hashItem(BufferedImage sheet, int itemIndex) throws Exception {
		int left = itemIndex % 16 * 16, top = itemIndex / 16 * 16;
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
		return hex(MessageDigest.getInstance("SHA-256").digest(pixels.array()));
	}

	private static String hex(byte[] bytes) {
		StringBuilder out = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) out.append(String.format("%02X", value & 0xFF));
		return out.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
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
			traps = new SparseArray<Trap>();
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

	private SpsCallCoconutTest() { }
}
