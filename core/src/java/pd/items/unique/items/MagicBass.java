/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 魔音贝斯 —— 演员一档（面具 A 路线）的武器。
 *
 * <p>_阶位比同类武器高一档_；每次命中都会把音浪荡开，对目标_附近一格_的单位造成溅射伤害。</p>
 */
public class MagicBass extends MeleeWeapon implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.PERFORMER_SHOVEL_T1_1_BASS;

	/** 溅射伤害相对主伤害的比例。 */
	public static final float SPLASH_FACTOR = 0.5f;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(MagicBass.class)
			.t("name", "魔音贝斯")
			.t("desc", "一把弦上缠着魔力的低音贝斯，拨一下能让整层楼听见。\n\n它的_阶位比同类武器高一档_——挥起来也确实是那个分量。\n\n每次命中，音浪都会荡开，对目标_附近一格_的所有敌对单位造成溅射伤害。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		//SPSEXPD: 阶位 +1
		tier = 3;
		unique = true;
	}

	@Override public int min(int lvl) { return 3 + lvl; }
	@Override public int max(int lvl) { return 9 + 2 * lvl; }
	@Override public int STRReq(int lvl) { return 12; }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int dealt = super.proc(attacker, defender, damage);
		splash(attacker, defender, damage);
		return dealt;
	}

	/** SPSEXPD: 对目标附近一格的敌对单位造成溅射。 */
	protected void splash(Char attacker, Char defender, int damage) {
		if (Dungeon.level == null || defender == null) return;
		int splash = Math.max(1, Math.round(damage * SPLASH_FACTOR));
		int w = Dungeon.level.width();
		int dx0 = defender.pos % w;
		int dy0 = defender.pos / w;
		for (int dy = -1; dy <= 1; dy++) {
			for (int dx = -1; dx <= 1; dx++) {
				if (dx == 0 && dy == 0) continue;
				int x = dx0 + dx, y = dy0 + dy;
				if (x < 0 || y < 0 || x >= w) continue;
				int cell = y * w + x;
				if (!Dungeon.level.insideMap(cell)) continue;
				Char ch = Actor.findChar(cell);
				if (ch == null || ch == attacker || ch == defender) continue;
				if (ch.alignment != Char.Alignment.ENEMY) continue;
				ch.damage(splash, this);
			}
		}
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
