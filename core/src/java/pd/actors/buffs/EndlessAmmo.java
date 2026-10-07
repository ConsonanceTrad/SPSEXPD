/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.equipment.artifacts.EndlessAmmoBag;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import pd.messages.InlineText;

/**
 * SPSEXPD: 无限弹药袋的激活状态。
 *
 * <p>存在的期间内所有射击都不消耗弹药（投掷武器用临时副本投出、枪械不消耗弹匣/备弹、
 * 投掷器不清空内仓）。每回合从弹药袋扣除充能，充能耗尽自动解除。</p>
 */
public class EndlessAmmo extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(EndlessAmmo.class)
			.t("name", "无限弹药")
			.t("desc", "弹药袋正在替你补上射出的每一发弹药。\n\n弹药袋剩余充能：%1$d");
	}

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	/** 是否正在生效（各处弹药消耗点用它决定是否跳过消耗）。 */
	public static boolean isActive(Char ch) {
		return ch != null && ch.buff(EndlessAmmo.class) != null;
	}

	private EndlessAmmoBag bag() {
		if (!(target instanceof Hero)) return null;
		return ((Hero) target).belongings.getItem(EndlessAmmoBag.class);
	}

	@Override
	public boolean act() {
		EndlessAmmoBag bag = bag();
		if (bag == null) {
			detach();
			return true;
		}

		bag.consumeCharge((int) EndlessAmmoBag.DRAIN_PER_TURN);
		if (bag.charge() <= 0) {
			detach();
			GLog.w(Messages.get(EndlessAmmoBag.class, "drained"));
			return true;
		}

		spend(TICK);
		return true;
	}

	@Override
	public int icon() {
		return BuffIndicator.RECHARGING;
	}

	@Override
	public String desc() {
		EndlessAmmoBag bag = bag();
		return Messages.get(this, "desc", bag == null ? 0 : bag.charge());
	}
}
