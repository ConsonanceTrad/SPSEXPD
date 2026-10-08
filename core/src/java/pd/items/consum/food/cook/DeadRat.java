/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.cook;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Slow;
import pd.actors.hero.Hero;
import pd.items.consum.food.Food;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;

/** SPSEXPD: 血源风烹饪食材——死鼠。生吃有风险，烹熟就没问题了。 */
public class DeadRat extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DeadRat.class)
			.t("name", "死鼠")
			.t("desc", "一只死老鼠。最好别生吃——用平底煎锅把它弄熟。")
			.t("legs", "你的双腿不听使唤了！")
			.t("not_well", "你的胃里一阵翻江倒海……")
			.t("stuffed", "你感觉肚子被塞住了。");
	}

	{
		image = ConsumFoodFoodDict.MONSTER_MEAT;
		energy = 40f;
		canBeCook = true;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_EAT.equals(action)) effect(hero);
	}

	/** 生吃死鼠的随机不适（移植自血源）。 */
	public static void effect(Hero hero) {
		if (hero == null) return;
		switch (Random.Int(5)) {
			case 0:
			case 1:
				GLog.w(Messages.get(DeadRat.class, "legs"));
				Buff.prolong(hero, Roots.class, 10f);
				break;
			case 2:
				GLog.w(Messages.get(DeadRat.class, "not_well"));
				Buff.affect(hero, Poison.class).set(Math.max(1, hero.HT / 5));
				break;
			case 3:
				GLog.w(Messages.get(DeadRat.class, "stuffed"));
				Buff.prolong(hero, Slow.class, 10f);
				break;
		}
	}

	@Override public int value() { return 1 * quantity; }
}
