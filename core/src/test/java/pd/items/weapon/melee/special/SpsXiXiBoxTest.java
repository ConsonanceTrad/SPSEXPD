package pd.items.weapon.melee.special;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.levels.Level;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.SparseArray;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

public final class SpsXiXiBoxTest {

	private static final String ICON_HASH =
			"7C6507135FBE0124D7B482DF0288A87623C4F18843609399EE482E95370F4503";

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		try {
			testSourceAndStats();
			testIndependentChargeAndSave();
			testBreakRewards();
			testIcon();
			System.out.println("SPS牢固箱测试通过：独立蓄力、存档、101次强击自毁、四类奖励、NPC来源和原始图标均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
			app.exit();
		}
	}

	private static void testSourceAndStats() {
		TownNpc npc = new TownNpc().configure(TownNpc.Spec.XIXI_ZERO);
		check(npc.SupercreateLoot() instanceof XiXiBox
				&& npc.properties().contains(Char.Property.BEAST), "XixiZero掉落或野兽属性错误");
		XiXiBox box = new XiXiBox();
		check(box.min(0) == 1 && box.max(0) == 10 && box.min(3) == 4 && box.max(3) == 13
				&& box.STRReq(0) == 10 && box.image == ItemSpriteSheet.XIXI_BOX,
				"牢固箱伤害成长、力量需求或图标错误");
	}

	private static void testIndependentChargeAndSave() {
		XiXiBox first = new XiXiBox();
		XiXiBox second = new XiXiBox();
		for (int i = 0; i < 100; i++) check(!first.chargeForDamage(8), "牢固箱在第101次强击前自毁");
		check(first.charge() == 100 && second.charge() == 0, "不同牢固箱错误地共享蓄力");
		check(!first.chargeForDamage(7) && first.charge() == 100, "7点伤害错误地增加蓄力");
		Bundle bundle = new Bundle();
		first.storeInBundle(bundle);
		XiXiBox restored = new XiXiBox();
		restored.restoreFromBundle(bundle);
		check(restored.charge() == 100 && restored.chargeForDamage(8), "牢固箱蓄力未存档或第101次强击未触发");
	}

	private static void testBreakRewards() {
		check(XiXiBox.REWARD_CATEGORIES.length == 4
				&& XiXiBox.REWARD_CATEGORIES[0] == Generator.Category.OLDWEAPON
				&& XiXiBox.REWARD_CATEGORIES[1] == Generator.Category.ARMOR
				&& XiXiBox.REWARD_CATEGORIES[2] == Generator.Category.ARTIFACT
				&& XiXiBox.REWARD_CATEGORIES[3] == Generator.Category.RING,
				"牢固箱四类奖励映射错误");
		Hero hero = new Hero();
		Dungeon.hero = hero;
		XiXiBox box = new XiXiBox();
		hero.belongings.weapon = box;
		RecordingLevel level = new RecordingLevel();
		Dungeon.level = level;
		Item[] rewards = {new TestItem(), new TestItem(), new TestItem(), new TestItem()};
		box.breakOpen(27, rewards);
		check(hero.belongings.weapon() == null && level.dropCount == 4 && level.lastCell == 27,
				"牢固箱自毁没有卸下自身或落下四件奖励");
	}

	private static void testIcon() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = 816; y < 832; y++) for (int x = 240; x < 256; x++) pixels.putInt(sheet.getRGB(x, y));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder actual = new StringBuilder(64);
		for (byte value : digest) actual.append(String.format("%02X", value & 0xFF));
		check(ICON_HASH.equals(actual.toString()), "牢固箱图标与旧版像素不一致：" + actual);
	}

	private static final class TestItem extends Item { }

	private static final class RecordingLevel extends Level {
		int dropCount;
		int lastCell;
		RecordingLevel() {
			setSize(8, 8);
			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<>();
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
			dropCount++;
			lastCell = cell;
			return null;
		}
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsXiXiBoxTest() { }
}
