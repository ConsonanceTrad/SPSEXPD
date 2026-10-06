/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.wish;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

/** SPSEXPD: 许愿彩蛋——心愿硬币：饮下后获得护盾与短暂的攻防提升。 */
public class WishCoin extends WishOnlyItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WishCoin.class)
			.t("name", "心愿硬币")
			.t("desc", "一枚边缘被磨圆的硬币，只有许愿才能得到。饮下它，一笔无形的力量会撑起你的攻与守。");
	}

	@Override
	public void apply(Hero hero) {
		identify();
		Buff.affect(hero, Barrier.class).incShield(Math.max(2, hero.HT / 5));
		Buff.affect(hero, AttackUp.class, 60f).level(10);
		Buff.affect(hero, DefenceUp.class, 60f).level(10);
	}
}
