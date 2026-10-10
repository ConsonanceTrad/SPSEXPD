/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Dewdrop;
import pd.items.RedDewdrop;
import pd.items.VioletDewdrop;
import pd.items.YellowDewdrop;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/**
 * SPSEXPD: 露珠菌孢——下一次攻击命中时，按造成的伤害散落黄、红、紫三色露珠（总量 10~120）。
 */
public class DewScatter extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DewScatter.class)
			.t("name", "高能蓄势")
			.t("desc", "下一次攻击命中时，蓄积的能量会凝成黄、红、紫三色露珠散落在地上。\n\n剩余回合：%s");
	}

	public static final float DURATION = 20f;
	/** SPSEXPD: 单次散落的能量下限与上限。 */
	public static final int MIN_ENERGY = 10;
	public static final int MAX_ENERGY = 120;

	/** SPSEXPD: 英雄攻击结算时调用——触发并消耗本 buff。 */
	public static void onHeroAttack(Hero hero, Char enemy, int damage) {
		if (hero == null || enemy == null || damage <= 0) return;
		DewScatter buff = hero.buff(DewScatter.class);
		if (buff == null) return;

		if (Dungeon.level != null) {
			scatter(enemy.pos, Math.max(MIN_ENERGY, Math.min(MAX_ENERGY, damage)));
		}
		buff.detach();
	}

	/** SPSEXPD: 按紫(30)/红(15)/黄(5)面额从大到小切分散落。 */
	private static void scatter(int pos, int energy) {
		int remaining = energy;
		while (remaining >= 30) {
			Dewdrop.dropAt(new VioletDewdrop(), pos, pos);
			remaining -= 30;
		}
		while (remaining >= 15) {
			Dewdrop.dropAt(new RedDewdrop(), pos, pos);
			remaining -= 15;
		}
		while (remaining >= 5) {
			Dewdrop.dropAt(new YellowDewdrop(), pos, pos);
			remaining -= 5;
		}
	}

	@Override
	public int icon() {
		return BuffIndicator.LIGHT;
	}
}
