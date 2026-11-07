package pd.levels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import render.utils.math.Random;

/**
 * SPSEXPD: 特殊房间固定布局模板与尺寸参数表（由编辑器生成，工具链维护）。
 *
 * 维护方式：tools/fixed-levels-editor.html 的「房间模板」页编辑 → 导出 JSON →
 * tools/update-fixed-layouts.ps1 按锚点重写下面的 TEMPLATES_DATA / SIZES_DATA 两个块。
 * 手改也可，但会丢失编辑器侧同步。
 *
 * 模板编码：int[]{ w, h, cells... }，cells 顺排（行优先），结构为完整矩形（含边框墙）。
 * 包含边框墙与内部地形；门不编入模板（门位由引擎照常态流程定稿，模板写好后由
 * SpsRegularLevel.paintDoors 覆标门格）。
 *
 * 物品/怪物不编入模板：模板接管后该房不再走原 painter 的产物放置，房间为纯地形。
 *
 * 消费点（仅 SPS 普通层主线）：
 * - SpsRegularLevel.fitsLegacyRoom：准入尺寸 = SIZES 覆写 > painter 默认；
 * - SpsRegularLevel.paintLegacySpecialRoom：登记了模板的类型优先用模板绘制
 *   （门对齐居中展开，容不下则回退原 painter）；
 * - 破碎房间的尺寸收紧同样读取 SIZES 覆写。
 */
public final class SpsRoomTemplates {

	/** Type 名 -> 模板集合（int[]{ w, h, cells... }） */
	private static final Map<String, int[][]> TEMPLATES = new HashMap<>();
	/** Type 名 -> int[]{ minW, minH, maxW, maxH }（元素 <=0 表示该项不覆写） */
	private static final Map<String, int[]> SIZES = new HashMap<>();

	private SpsRoomTemplates() {
	}

	static {
		/*__TEMPLATES_DATA__*/
	}

	static {
		/*__SIZES_DATA__*/
	}

	/** 随机挑一张能容下 roomW×roomH 的模板；类型没登记或全都放不下返回 null。 */
	public static int[] pick(String type, int roomW, int roomH) {
		int[][] all = TEMPLATES.get(type);
		if (all == null) return null;
		List<int[]> fits = new ArrayList<>();
		for (int[] tpl : all) {
			if (tpl != null && tpl.length >= 2 && tpl[0] <= roomW && tpl[1] <= roomH) {
				fits.add(tpl);
			}
		}
		if (fits.isEmpty()) return null;
		return fits.get(Random.Int(fits.size()));
	}

	public static boolean hasTemplate(String type) {
		int[][] all = TEMPLATES.get(type);
		return all != null && all.length > 0;
	}

	/** 尺寸覆写：SIZES 登记 >0 才生效，否则回退调用方给的默认值。 */
	public static int minW(String type, int fallback) {
		int[] s = SIZES.get(type);
		return s == null || s.length < 1 || s[0] <= 0 ? fallback : s[0];
	}

	public static int minH(String type, int fallback) {
		int[] s = SIZES.get(type);
		return s == null || s.length < 2 || s[1] <= 0 ? fallback : s[1];
	}

	public static int maxW(String type, int fallback) {
		int[] s = SIZES.get(type);
		return s == null || s.length < 3 || s[2] <= 0 ? fallback : s[2];
	}

	public static int maxH(String type, int fallback) {
		int[] s = SIZES.get(type);
		return s == null || s.length < 4 || s[3] <= 0 ? fallback : s[3];
	}
}
