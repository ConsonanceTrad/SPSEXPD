/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.Char;
import pd.actors.buffs.Pilgrimage;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 九环锡杖 —— 信徒二档（王冠 B 路线）的特殊武器。
 *
 * <p>拥有禅杖的全部「赶路」效果；此外，在_加速状态下打出的第一击_
 * 会额外重随一次伤害，取其中较高的一次。</p>
 */
public class NineRingStaff extends PilgrimStaff {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ASCETIC_EARRING_T2_2_RING;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(NineRingStaff.class)
			.t("name", "九环锡杖")
			.t("desc", "杖头串着九只铁环的锡杖，走起来一路叮当。\n\n它拥有禅杖的全部本事：_连续移动_（停下即清零）或_附近没有怪物_时都会赶路更快。\n\n此外，在_加速状态下打出的第一击_会额外重随一次伤害，取其中较高的那次。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		tier = 3;
		unique = true;
	}

	@Override public int min(int lvl) { return 3 + lvl; }
	@Override public int max(int lvl) { return 8 + 2 * lvl; }

	@Override
	public int damageRoll(Char owner) {
		int dmg = super.damageRoll(owner);
		if (owner == null) return dmg;
		Pilgrimage p = owner.buff(Pilgrimage.class);
		//SPSEXPD: 加速状态下的第一击重随一次，取高
		if (p != null && p.consumeReroll()) {
			dmg = Math.max(dmg, super.damageRoll(owner));
		}
		return dmg;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
