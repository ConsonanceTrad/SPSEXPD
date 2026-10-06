/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.wish;

import pd.actors.hero.Hero;
import pd.items.consum.potions.PotionOfHealing;
import pd.messages.InlineText;

/** SPSEXPD: 许愿彩蛋——心愿之星：饮下后获得治疗与一笔经验。 */
public class WishStar extends WishOnlyItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WishStar.class)
			.t("name", "心愿之星")
			.t("desc", "一颗坠入瓶中的微小星辰，只有许愿才能得到。饮下它，伤口愈合，而你离强大又近了一步。");
	}

	@Override
	public void apply(Hero hero) {
		identify();
		PotionOfHealing.heal(hero);
		hero.earnExp(Math.max(1, (int)(hero.maxExp() * 0.2f)), WishStar.class);
	}
}
