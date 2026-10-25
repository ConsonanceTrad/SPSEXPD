package pd.items.equipment.bags;

import pd.messages.InlineText;
import pd.messages.Messages;

/**
 * SPSEXPD: 强化组件——给目标包裹提供功能（产炼金能量 / 转化食物 / 处理种子）。
 * 同容器内的固定顺序：炼药工具台 10 -> 丰饶号角 20 -> 绿色小瓶 30，永远排在拓展组件之前。
 */
public abstract class BagUpgrade extends pd.items.Item implements BagComponent {

	static {
		InlineText.of(BagUpgrade.class)
				.t("inactive", "还没有对应的包裹可以装配，暂时没有效果。");
	}

	{
		stackable = false;
		unique = true;
	}

	@Override
	public int componentOrder() {
		return 10;
	}

	@Override
	public boolean collect(Bag container) {
		//SPSEXPD: 强化组件不走普通拾取——直接归位到目标包裹的组件区段（没有对应包裹则留在主背包且不生效）
		return BagComponent.Components.collectComponent(this, container);
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 0;
	}

	@Override
	public String desc() {
		String text = super.desc();
		if (!active()) {
			text += "\n\n" + Messages.get(BagUpgrade.class, "inactive");
		}
		return text;
	}
}