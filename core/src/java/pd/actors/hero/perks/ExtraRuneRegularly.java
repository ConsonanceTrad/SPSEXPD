/*
 * 符文馈赠 —— 裁决：定时自动获得一枚「符石」（Runestone），不引入新物品。
 */

package pd.actors.hero.perks;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.consum.stones.Runestone;
import pd.items.consum.stones.StoneOfAggression;
import pd.items.consum.stones.StoneOfAugmentation;
import pd.items.consum.stones.StoneOfBlast;
import pd.items.consum.stones.StoneOfBlink;
import pd.items.consum.stones.StoneOfClairvoyance;
import pd.items.consum.stones.StoneOfDeepSleep;
import pd.items.consum.stones.StoneOfDetectMagic;
import pd.items.consum.stones.StoneOfEnchantment;
import pd.items.consum.stones.StoneOfFear;
import pd.items.consum.stones.StoneOfFlock;
import pd.items.consum.stones.StoneOfIntuition;
import pd.items.consum.stones.StoneOfShock;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

public class ExtraRuneRegularly extends TimingPerk {

	static {
		InlineText.of(ExtraRuneRegularly.class)
				.t("title", "符文馈赠")
				.t("desc", "每隔一段时间，你会自动获得一枚随机的符石。")
				.t("generated", "你获得了一枚符石：%s");
	}

	/** 可产出的符石种类 */
	private static final Class<? extends Runestone>[] STONES = new Class[]{
			StoneOfAggression.class, StoneOfAugmentation.class, StoneOfBlast.class,
			StoneOfBlink.class, StoneOfClairvoyance.class, StoneOfDeepSleep.class,
			StoneOfDetectMagic.class, StoneOfEnchantment.class, StoneOfFear.class,
			StoneOfFlock.class, StoneOfIntuition.class, StoneOfShock.class
	};

	public ExtraRuneRegularly() {
		super(GainRuneTiming.class);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.RUNE_EXTRA;
	}

	@Override
	public void trigger(Hero hero) {
		if (hero == null || hero.belongings == null) return;
		Runestone stone = randomStone();
		if (stone == null) return;
		stone.collect(hero.belongings.backpack);
		GLog.p(Messages.get(this, "generated", Messages.titleCase(stone.name())));
	}

	private static Runestone randomStone() {
		Class<? extends Runestone> cls = STONES[Random.Int(STONES.length)];
		return Reflection.newInstance(cls);
	}

	/** 周期触发（回合） */
	public static class GainRuneTiming extends Timing {
		public GainRuneTiming() {
			super(180f);
		}

		@Override
		public void trigger() {
			if (Dungeon.hero == null || Dungeon.hero.heroPerk == null) return;
			ExtraRuneRegularly p = Dungeon.hero.heroPerk.get(ExtraRuneRegularly.class);
			if (p != null) p.trigger(Dungeon.hero);
		}
	}
}
