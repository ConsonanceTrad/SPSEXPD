package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;
import pd.messages.InlineText;
import pd.messages.Messages;

/**
 * SPSEXPD: 拓展组件——本身就是一个小背包（15 格）。
 * 装进目标包裹后点击即可打开这个小背包；对上层的搜索与取物而言等同于上级包裹的一部分。
 */
public abstract class BagExpand extends Bag implements BagComponent {

	static {
		InlineText.of(BagExpand.class)
				.t("name", "拓展组件")
				.t("ac_open", "打开")
				.t("desc_suffix", "装进%1$s后，可以点开一个 %2$d 格的内部空间。")
				.t("inactive", "还没有对应的包裹可以装配，暂时没有效果。");
	}

	{
		image = EquipmentBagsDict.POUCH;
		defaultAction = AC_OPEN;
	}

	@Override
	public int capacity() {
		return SLOT_BONUS;
	}

	@Override
	public int componentOrder() {
		return COMPONENT_ORDER_EXPAND;
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

	/** 供子类复用的「装进 X 后多出 15 格」说明。 */
	protected String bonusSuffix() {
		String bagName = "";
		try {
			Bag target = render.utils.serialize.Reflection.newInstance(targetBag());
			if (target != null) bagName = Messages.titleCase(target.name());
		} catch (Exception ignored) {
		}
		return Messages.get(BagExpand.class, "desc_suffix", bagName, SLOT_BONUS);
	}

	@Override
	public String actionName(String action, pd.actors.hero.Hero hero) {
		if (AC_OPEN.equals(action)) return Messages.get(BagExpand.class, "ac_open");
		return super.actionName(action, hero);
	}
}