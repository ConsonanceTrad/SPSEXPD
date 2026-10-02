/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs.randomone;

import pd.actors.hero.Hero;
import pd.items.consum.eggs.RandomEasterEgg;
import pd.items.specific.sellitem.VIPcard;
import render.utils.math.Random;
import pd.messages.InlineText;

/** Opens into one of all twelve monthly pet soul packs. */
public class RandomMonthEgg extends RandomEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomMonthEgg.class)
			.t("name", "随机月份灵魂")
			.t("ac_use", "使用")
			.t("desc", "获得一个随机月份的基础宠物包，有几率获得彩蛋宠物包。");
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_USE.equals(action)) {
			super.execute(hero, action);
			return;
		}
		if (Random.Int(10) == 0) drop(new VIPcard(), hero);
		drop(Random.Int(10) == 0 ? new RandomEasterEgg() : monthEgg(Random.Int(12)), hero);
		detach(hero.belongings.backpack);
		hero.spendAndNext(1f);
	}
}
