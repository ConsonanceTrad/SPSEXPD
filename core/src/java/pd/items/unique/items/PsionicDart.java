/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.atlas.items.EquipmentEquipWeaponUniqueWeaponDict;
import pd.items.equipment.weapon.SpiritBow;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: 灵能袖箭 —— 女猎手一档（面具 A 路线）的武器。
 *
 * <p>以灵能弓为骨架，另有一种「预装填」：花 _0.5 回合_先把一次射击压进袖箭里，
 * 之后那一发打出去_不消耗回合_。</p>
 */
public class PsionicDart extends SpiritBow implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.HUNTRESS_BOW_T1_1_DART;

	public static final String AC_PRELOAD = "PRELOAD";
	/** 预装填消耗的回合数。 */
	public static final float PRELOAD_TIME = 0.5f;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(PsionicDart.class)
			.t("name", "灵能袖箭")
			.t("ac_preload", "预装填")
			.t("preloaded", "你把一次射击提前压进了袖箭（可预装填 %d 发）。")
			.t("preload_full", "袖箭已经预装填满了。")
			.t("preload_used", "预装填的一发打了出去，没有耗费回合。")
			.t("desc", "一架能藏进袖口的轻型弩，弦上覆着一层灵能。\n\n「预装填」：花 _0.5 回合_把一次射击提前压进袖箭里，之后那一发打出去_不消耗回合_。最多预装填 _%1$d 发_。");
	}

	/** SPSEXPD: 已预装填的发数。 */
	protected int preloaded = 0;

	{
		//SPSEXPD: 暂借用灵能弓图标
		image = EquipmentEquipWeaponUniqueWeaponDict.SPIRIT_BOW_0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", maxPreload());
	}

	/** 最多预装填几发（袖箭·改覆写为整个弹匣）。 */
	protected int maxPreload() {
		return 1;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_PRELOAD);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_PRELOAD.equals(action)) {
			if (preloaded >= maxPreload()) {
				GLog.w(Messages.get(this, "preload_full"));
				return;
			}
			preloaded++;
			GLog.i(Messages.get(this, "preloaded", preloaded));
			hero.spend(PRELOAD_TIME);
			return;
		}
		super.execute(hero, action);
	}

	/** SPSEXPD: 预装填的那一发不消耗回合。 */
	@Override
	protected float baseDelay(Char owner) {
		if (preloaded > 0) {
			preloaded--;
			GLog.i(Messages.get(this, "preload_used"));
			return 0f;
		}
		return super.baseDelay(owner);
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
