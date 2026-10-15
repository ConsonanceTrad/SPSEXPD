/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 巴虺断角 —— 信徒二档（王冠 A 路线）的神器。
 *
 * <p>与小千录同源的流血仪式，但出血更猛、补血更多，并且开启期间还额外获得
 * _伤害提升_与_移速提升_。</p>
 */
public class BaHuiHorn extends ThousandScroll {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ASCETIC_EARRING_T2_1_HORN;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(BaHuiHorn.class)
			.t("name", "巴虺断角")
			.t("ac_open", "开启")
			.t("ac_close", "收起")
			.t("cooldown", "断角还没晾干上次的血。")
			.t("opened", "断角里的东西醒了过来，你的血开始朝它涌去。")
			.t("closed", "断角安静下来，血也慢慢收住了。")
			.t("desc", "一截断角，断面上的花纹像在缓慢地呼吸。\n\n「开启」：进入_持续累加的流血_状态。出血比小千录更猛，补回来的血也更多，并且期间你的_伤害与移动速度都会提升_。\n\n开启期间，除流血外的_其它负面状态会被挂起_（既不生效，也不会消失；收起后原样回来）。\n\n「收起」后需要 _%1$d 回合_冷却才能再次开启。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
	}

	@Override protected float bleedStart() { return 2f; }
	@Override protected float bleedGrowth() { return 1f; }
	@Override protected float healFactor() { return 1.75f; }
	@Override protected float speedBonus() { return 0.15f; }
	@Override protected int damageBonus() { return 3; }
	@Override protected int cooldownTurns() { return 12; }

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
