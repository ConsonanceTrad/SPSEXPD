/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs.randomone;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.consum.eggs.RandomEasterEgg;
import pd.items.specific.sellitem.VIPcard;
import render.utils.math.Random;

import java.util.ArrayList;
import java.util.Calendar;
import pd.messages.InlineText;

/** Opens into the current month's pet soul pack, with the original Easter and VIP chances. */
public class RandomEgg extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg.class)
			.t("name", "随机灵魂")
			.t("ac_use", "使用")
			.t("desc", "获得一个对应月份的基础宠物包，有几率获得彩蛋宠物包。");
	}




	public static final String AC_USE = "USE";

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = true;
		defaultAction = AC_USE;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_USE.equals(action)) {
			super.execute(hero, action);
			return;
		}
		if (Random.Int(10) == 0) drop(new VIPcard(), hero);
		drop(Random.Int(10) == 0 ? new RandomEasterEgg()
				: monthEgg(Calendar.getInstance().get(Calendar.MONTH)), hero);
		detach(hero.belongings.backpack);
		hero.spendAndNext(1f);
	}

	protected void drop(Item item, Hero hero) {
		Dungeon.level.drop(item, hero.pos).sprite.drop();
	}

	public static RandomPetEgg monthEgg(int zeroBasedMonth) {
		switch (zeroBasedMonth) {
			case Calendar.JANUARY: return new RandomEgg1();
			case Calendar.FEBRUARY: return new RandomEgg2();
			case Calendar.MARCH: return new RandomEgg3();
			case Calendar.APRIL: return new RandomEgg4();
			case Calendar.MAY: return new RandomEgg5();
			case Calendar.JUNE: return new RandomEgg6();
			case Calendar.JULY: return new RandomEgg7();
			case Calendar.AUGUST: return new RandomEgg8();
			case Calendar.SEPTEMBER: return new RandomEgg9();
			case Calendar.OCTOBER: return new RandomEgg10();
			case Calendar.NOVEMBER: return new RandomEgg11();
			case Calendar.DECEMBER: return new RandomEgg12();
			default: throw new IllegalArgumentException("Invalid month: " + zeroBasedMonth);
		}
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 500 * quantity; }
}
