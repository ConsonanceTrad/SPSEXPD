/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs.randomone;

import java.util.ArrayList;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.consum.eggs.Egg;
import pd.atlas.items.ConsumSummorDict;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

/**
 * SPSXPD: 原「随机宠物灵魂」。
 * 魂石召唤的生物必须是固定的，所以它不再是魂石，而是**奖励包**：
 * 使用后掉落包里随机一颗固定魂石。
 */
public abstract class RandomPetEgg extends Item {

	public static final String AC_USE = "USE";

	private final Class<? extends Egg>[] eggs;

	@SafeVarargs
	protected RandomPetEgg(Class<? extends Egg>... eggs) {
		this.eggs = eggs;
		image = ConsumSummorDict.RANDOM_SOUL;
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
		dropContents(hero);
		detach(hero.belongings.backpack);
		hero.spendAndNext(1f);
	}

	/** SPSXPD: 把包里随机一颗魂石掉在英雄脚下（不消耗包自身；供初始/月份灵魂包一步到位） */
	public void dropContents(Hero hero) {
		if (eggs == null || eggs.length == 0) return;
		Egg stone = Reflection.newInstance(eggs[Random.Int(eggs.length)]);
		if (stone != null && Dungeon.level != null) {
			Dungeon.level.drop(stone, hero.pos).sprite.drop();
		}
	}

	/** 包里的候选魂石 */
	public Class<? extends Egg>[] possibleEggs() {
		return eggs.clone();
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 500 * quantity; }
}