/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.wish;

import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.LuckyMoment;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

/** SPSEXPD: 许愿彩蛋——许愿残片：饮下后获得祝福与一段幸运时刻。 */
public class WishFragment extends WishOnlyItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WishFragment.class)
			.t("name", "许愿残片")
			.t("desc", "一片被愿望灼烧过的水晶残片，只有许愿才能得到。饮下它，祝福与好运短暂地包裹住你。");
	}

	@Override
	public void apply(Hero hero) {
		identify();
		Buff.prolong(hero, Bless.class, 60f);
		Buff.prolong(hero, LuckyMoment.class, 300f);
	}
}
