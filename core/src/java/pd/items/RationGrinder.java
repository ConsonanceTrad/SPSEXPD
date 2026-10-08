/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.consum.food.BugMeat;
import pd.items.consum.food.Food;
import pd.items.consum.food.staplefood.NormalRation;
import pd.items.equipment.bags.ShoppingCart;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: 干粮碾制机——购物车的强化组件。
 *
 * 对购物车「安装」之后，购物车最后一格会变成碾制机；
 * 「碾制」会把车里所有食物按饱食度换算成等量干粮包（每包 300 饱食度，向下取整，
 * 换不出整包的散装食物会原样留下，不会被浪费）。
 */
public class RationGrinder extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RationGrinder.class)
			.t("name", "干粮碾制机")
			.t("ac_install", "安装")
			.t("ac_grind", "碾制")
			.t("prompt", "选择要安装碾制机的购物车")
			.t("no_cart", "你没有持有购物车。")
			.t("installed", "你把干粮碾制机装到了购物车上——最后一格现在就是碾制机。")
			.t("grind_none", "购物车里没有能碾出整包干粮的食物。")
			.t("grind_done", "碾制机把 %1$d 份食物碾成了 %2$d 包干粮。")
			.t("desc", "购物车的强化组件。安装后购物车的最后一格会变成碾制机，它能把车里的食物按饱食度换算成等量干粮包（每包 300 饱食度，向下取整）。");
	}

	/** 每包干粮的饱食度——向下取整换算的基准。 */
	public static final float RATION_ENERGY = 300f;

	public static final String AC_INSTALL = "INSTALL";
	public static final String AC_GRIND = "GRIND";

	{ image = SpecificPlaceHolderDict.SOMETHING_0; unique = true; defaultAction = AC_INSTALL; }

	/** 末格实例用：标记这是一台已装好的碾制机。 */
	protected boolean installed = false;
	/** 末格实例用：所属购物车。 */
	protected ShoppingCart host = null;

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (installed && host != null) {
			actions.add(AC_GRIND);
		} else if (cart(hero) != null) {
			actions.add(AC_INSTALL);
		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_INSTALL.equals(action)) {
			install(hero);
		} else if (AC_GRIND.equals(action)) {
			grind(hero);
		} else {
			super.execute(hero, action);
		}
	}

	/** 英雄持有的购物车，没有则为 null。 */
	public static ShoppingCart cart(Hero hero) {
		return hero == null || hero.belongings == null ? null
				: hero.belongings.getItem(ShoppingCart.class);
	}

	/** SPSEXPD: 生成一台「装在某购物车上」的碾制机（购物车末格用）。 */
	public static RationGrinder installedOn(ShoppingCart cart) {
		RationGrinder grinder = new RationGrinder();
		grinder.installed = true;
		grinder.host = cart;
		return grinder;
	}

	/** 把碾制机装到购物车上（消耗组件自身）。 */
	public boolean install(Hero hero) {
		ShoppingCart cart = cart(hero);
		if (cart == null) {
			GLog.w(Messages.get(this, "no_cart"));
			return false;
		}
		cart.grinderInstalled = true;
		GLog.p(Messages.get(this, "installed"));
		detachAll(hero.belongings.backpack);
		hero.spendAndNext(2f);
		return true;
	}

	/** 把购物车里的食物碾成干粮包。 */
	public boolean grind(Hero hero) {
		ShoppingCart cart = host != null ? host : cart(hero);
		if (cart == null) return false;

		ArrayList<Item> foods = new ArrayList<>();
		for (Item item : cart.items) {
			if (item instanceof Food && !(item instanceof BugMeat) && ((Food) item).energy > 0) {
				foods.add(item);
			}
		}
		if (foods.isEmpty()) {
			GLog.w(Messages.get(this, "grind_none"));
			return false;
		}

		int used = 0;
		int produced = 0;
		for (Item item : foods) {
			int pieces = (int)((int)(((Food) item).energy * item.quantity()) / RATION_ENERGY);
			if (pieces <= 0) continue;
			used += item.quantity();
			cart.items.remove(item);
			produced += pieces;
		}
		if (produced <= 0) {
			GLog.w(Messages.get(this, "grind_none"));
			return false;
		}

		NormalRation rations = new NormalRation();
		rations.quantity(produced);
		if (!rations.collect(cart) && Dungeon.level != null) {
			Dungeon.level.drop(rations, hero.pos);
		}

		GLog.p(Messages.get(this, "grind_done", used, produced));
		hero.spendAndNext(2f);
		return true;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 400 * quantity; }
}
