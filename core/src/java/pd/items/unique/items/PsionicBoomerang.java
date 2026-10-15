/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.ConsumThrowsDict;
import pd.items.equipment.weapon.missiles.throwing.Boomerang;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 灵能回旋镖 —— 女猎手一档（面具 B 路线）的武器。
 *
 * <p>与普通回旋镖同源，但弹回时会在落点_一格范围内_寻找你——只要你在这圈里，就算接住。</p>
 */
public class PsionicBoomerang extends Boomerang implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.HUNTRESS_BOW_T1_2_BOOM;

	/** 接住判定放宽到落点 1 格内。 */
	public static final int CATCH_RADIUS = 1;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(PsionicBoomerang.class)
			.t("name", "灵能回旋镖")
			.t("desc", "一具泛着幽蓝微光的曲状投掷物，抛出后会自己找路回来。\n\n它会在弹回落点的_一格范围内_寻找你——只要你站在那圈里，就算是接住了。");
	}

	{
		//SPSEXPD: 暂借用普通回旋镖图标
		image = ConsumThrowsDict.BOOMERANG_0;
	}

	@Override
	protected int catchRadius() {
		return CATCH_RADIUS;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
