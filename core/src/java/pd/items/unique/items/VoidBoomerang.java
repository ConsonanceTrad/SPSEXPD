/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.ConsumThrowsDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 虚空回旋镖 —— 女猎手二档（王冠 B 路线）的武器。
 *
 * <p>在灵能回旋镖的基础上，投掷弹道_可以穿透墙体_（不再被墙挡住落点）。</p>
 */
public class VoidBoomerang extends PsionicBoomerang {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.HUNTRESS_BOW_T2_2_BOOM;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(VoidBoomerang.class)
			.t("name", "虚空回旋镖")
			.t("desc", "一具边缘啃掉了光线的曲状投掷物。\n\n它的弹道_可以穿过墙壁_，而且在弹回落点的_一格范围内_就能找到你，把你当作接住。");
	}

	{
		//SPSEXPD: 暂借用普通回旋镖图标
		image = ConsumThrowsDict.BOOMERANG_0;
	}

	/** SPSEXPD: 弹道无视墙壁。 */
	@Override
	protected boolean piercesWalls() {
		return true;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
