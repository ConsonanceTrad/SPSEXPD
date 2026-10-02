package pd.tiles;

import pd.atlas.items.ConsumThrowsDict;

import pd.Dungeon;
import pd.items.Item;
import pd.items.quest.RatSkull;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;

/** Source-independent checks for legacy fog rules and the old-save rat skull. */
public final class SpsLegacyDisplayTest {

	public static void main(String[] args) throws Exception {
		testFogCornersAndColors();
		testTilemapCompatibility();
		testLegacyRatSkull();
		testRatSkullResources();
		System.out.println("SPS地图显示兼容通过：旧版战争迷雾、地图外点击、探索淡出色调及老存档巨鼠头骨均符合0.9.8。");
	}

	private static void testFogCornersAndColors() {
		int width = 3;
		boolean[] visible = new boolean[9];
		boolean[] visited = new boolean[9];
		boolean[] mapped = new boolean[9];
		check(FogOfWar.legacyFogColor(visible, visited, mapped, width, 1, 1)
				== FogOfWar.INVISIBLE, "未知四格交点不是纯黑迷雾");

		markCorner(mapped, width, 1, 1);
		check(FogOfWar.legacyFogColor(visible, visited, mapped, width, 1, 1)
				== 0xCC442211, "测绘迷雾颜色或四格判定不符合0.9.8");
		markCorner(visited, width, 1, 1);
		check(FogOfWar.legacyFogColor(visible, visited, mapped, width, 1, 1)
				== 0xCC111111, "已探索迷雾颜色或优先级不符合0.9.8");
		markCorner(visible, width, 1, 1);
		check(FogOfWar.legacyFogColor(visible, visited, mapped, width, 1, 1)
				== FogOfWar.VISIBLE, "可见四格交点仍被迷雾遮挡");

		visible[0] = false;
		check(FogOfWar.legacyFogColor(visible, new boolean[9], new boolean[9], width, 1, 1)
				== FogOfWar.INVISIBLE, "不足四格可见时错误清除了迷雾");
	}

	private static void markCorner(boolean[] cells, int width, int x, int y) {
		int pos = width * y + x;
		for (int index : new int[]{pos, pos - width, pos - 1, pos - width - 1}) {
			cells[index] = true;
		}
	}

	private static void testTilemapCompatibility() throws Exception {
		check(DungeonTilemap.SIZE == 16, "地图格尺寸不是0.9.8的16像素");
		check(DungeonTilemap.worldToTile(31.9f, 47.9f, 10) == 21,
				"世界坐标转地图格结果错误");
		String source = java.nio.file.Files.readString(Path.of("..", "java", "pd", "tiles", "DungeonTilemap.java"), StandardCharsets.UTF_8);
		check(source.contains("return -1;") && source.contains("tile.rm = tile.gm = tile.bm = rm;")
				&& source.contains("tile.ra = tile.ga = tile.ba = ra;"),
				"地图外点击或旧版探索淡出色调没有接入当前渲染器");
	}

	private static void testLegacyRatSkull() {
		RatSkull skull = new RatSkull();
		check(RatSkull.class.getSuperclass() == Item.class,
				"旧任务巨鼠头骨被同名破碎饰品替代");
		check(skull.unique && skull.image == ConsumThrowsDict.SKULL,
				"旧任务巨鼠头骨的唯一属性或原始图标错误");
		check(!skull.isUpgradable() && skull.isIdentified() && skull.value() == 100,
				"旧任务巨鼠头骨的鉴定、升级或价格行为错误");
		check(!RatSkull.class.equals(pd.items.equipment.trinkets.RatSkull.class),
				"旧任务物品与破碎饰品没有保持独立存档类型");
		check(!Dungeon.trinketCataNeeded(), "破碎饰品入口仍会出现在SPS正常流程");
	}

	private static void testRatSkullResources() throws Exception {
		String[] files = {"en/items.properties", "zh/items.properties",
				"zh-hant/items.properties", "ru/items.properties"};
		for (String file : files) {
			Properties items = new Properties();
			try (InputStreamReader reader = new InputStreamReader(java.nio.file.Files.newInputStream(
					Path.of("messages", "items", file)), StandardCharsets.UTF_8)) {
				items.load(reader);
			}
			for (String key : Arrays.asList("name", "desc")) {
				String value = items.getProperty("items.quest.ratskull." + key);
				check(value != null && !value.isEmpty() && !value.contains("\uFFFD"),
						"巨鼠头骨文本缺失或乱码：" + file + " / " + key);
			}
		}
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsLegacyDisplayTest() { }
}
