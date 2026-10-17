/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.Actor;
import pd.actors.Char;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

/**
 * SPSEXPD: 闪电口琴 —— 演员二档（王冠 A 路线）的武器。
 *
 * <p>阶位再高一档，保留魔音贝斯的_溅射_，并且每次命中都会_射出一道闪电能量_。</p>
 */
public class StormHarmonica extends MagicBass {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.PERFORMER_SHOVEL_T2_1_HARM;

	/** 闪电附加伤害。 */
	public static final int BOLT_BONUS = 4;
	/** 闪电再向外弹跳的格数。 */
	public static final int BOLT_JUMPS = 2;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(StormHarmonica.class)
			.t("name", "闪电竖琴")
			.t("chained", "竖琴的余音顺着空气劈到%s身上。")
			.t("glass", "竖琴的共鸣在你身周结成一层薄壳。")
			.t("desc", "一架黄铜小竖琴，拨弦时有细小的电弧在弦上跳。电属性的魔法会随着声音一起溢出来。\n\n它的_阶位比同类武器再高一档_，并保留魔音贝斯的_附近一格溅射_与_节拍_功能。\n\n此外，每次命中都会_射出一道闪电能量_；而当你_选择移动_（踩满节拍）时，还会额外获得一次_玻璃保护_。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		//SPSEXPD: 阶位再 +1
		tier = 4;
		unique = true;
	}

	@Override public int min(int lvl) { return 4 + lvl; }
	@Override public int max(int lvl) { return 12 + 3 * lvl; }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int dealt = super.proc(attacker, defender, damage);
		if (defender != null && defender.isAlive()) {
			//SPSEXPD: 命中目标额外吃一道闪电
			defender.damage(BOLT_BONUS, this);
		}
		chainBolt(attacker, defender);
		return dealt;
	}

	/** SPSEXPD: 闪电从目标向外劈过最多 BOLT_JUMPS 个单位。 */
	protected void chainBolt(Char attacker, Char defender) {
		if (pd.Dungeon.level == null || defender == null) return;
		int w = pd.Dungeon.level.width();
		int jumped = 0;
		int dx0 = defender.pos % w;
		int dy0 = defender.pos / w;
		for (int dy = -1; dy <= 1 && jumped < BOLT_JUMPS; dy++) {
			for (int dx = -1; dx <= 1 && jumped < BOLT_JUMPS; dx++) {
				if (dx == 0 && dy == 0) continue;
				int x = dx0 + dx, y = dy0 + dy;
				if (x < 0 || y < 0 || x >= w) continue;
				int cell = y * w + x;
				if (!pd.Dungeon.level.insideMap(cell)) continue;
				Char ch = Actor.findChar(cell);
				if (ch == null || ch == attacker || ch == defender) continue;
				if (ch.alignment != Char.Alignment.ENEMY) continue;
				GLog.i(Messages.get(this, "chained", ch.name()));
				ch.damage(BOLT_BONUS, this);
				jumped++;
			}
		}
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
