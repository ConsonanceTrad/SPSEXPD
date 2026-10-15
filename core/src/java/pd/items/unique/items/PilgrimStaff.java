/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Pilgrimage;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 禅杖 —— 信徒一档（面具 B 路线）的特殊武器。
 *
 * <p>拿着它_连续移动_、或者_周围没有怪物_时，赶路会明显更快。</p>
 */
public class PilgrimStaff extends MeleeWeapon implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ASCETIC_EARRING_T1_2_STAFF;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(PilgrimStaff.class)
			.t("name", "禅杖")
			.t("desc", "一截磨得发亮的木杖，杖头的铁环走一步响一声，走顺了就越响越轻快。\n\n只要_连续移动_，你赶路的速度就会越来越快（停下即清零）；如果_附近没有怪物_，也会另有提速。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		tier = 2;
		unique = true;
	}

	@Override public int min(int lvl) { return 2 + lvl; }
	@Override public int max(int lvl) { return 6 + 2 * lvl; }
	@Override public int STRReq(int lvl) { return 12; }

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		//SPSEXPD: 拿着禅杖期间维持「赶路」状态（卸下时由 Pilgrimage 自行结束）
		if (ch instanceof Hero) Buff.affect((Hero) ch, Pilgrimage.class);
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
