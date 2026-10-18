/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

/**
 * SPSEXPD: 临时义体的公共骨架。
 *
 * <p>义体是_背包里的消耗物_：不用装备，点「启动」就用掉一次电量；电量耗尽时它当场损毁消失。
 * 具体效果由子类在 {@link #activateEffect(Hero)} 里实现。</p>
 */
public abstract class Cyberware extends Item {

	public static final String AC_USE = "USE";

	private static final String CHARGES = "charges";

	/** 剩余电量（可用次数）。 */
	protected int charges;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Cyberware.class)
			.t("ac_use", "启动")
			.t("no_charge", "这台义体的电量已经耗尽了。")
			.t("used", "启动成功，剩余电量 %d。")
			.t("destroyed", "%s的电量耗尽，彻底报废了。")
			.t("desc", "一件临时义体，靠有限电量驱动。\n\n它不需要装备——点「启动」就会消耗 1 点电量；电量耗尽时它当场_损毁消失_。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		unique = false;
		charges = capacity();
		//SPSEXPD: 「启动」是默认动作——义体是背包消耗物，可直接放进快捷栏点一下就用
		defaultAction = AC_USE;
	}

	/** 电量（可用次数）。 */
	protected int capacity() {
		return 5;
	}

	/** 启动一次的效果。 */
	protected abstract void activateEffect(Hero hero);

	public int charges() {
		return charges;
	}

	/** SPSEXPD: 提高电量上限（强化义体工具箱用）。 */
	public void boostCapacity(float factor) {
		int boosted = Math.max(charges, Math.round(capacity() * factor));
		charges = boosted;
	}

	/** SPSEXPD: 所有义体共用同一段描述。 */
	@Override
	public String desc() {
		return Messages.get(Cyberware.class, "desc");
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_USE.equals(action)) {
			if (charges <= 0) {
				GLog.w(Messages.get(this, "no_charge"));
				detach(hero.belongings.backpack);
				return;
			}
			charges--;
			activateEffect(hero);
			hero.spendAndNext(1f);
			if (charges <= 0) {
				GLog.w(Messages.get(Cyberware.class, "destroyed", name()));
				detach(hero.belongings.backpack);
			} else {
				GLog.i(Messages.get(Cyberware.class, "used", charges));
			}
			return;
		}
		super.execute(hero, action);
	}

	@Override
	public String status() {
		return charges + "/" + capacity();
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
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGES, charges);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charges = bundle.getInt(CHARGES);
	}
}
