/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Muscle;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.Rhythm;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class DemoScroll extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DemoScroll.class)
			.t("name", "恶魔契约")
			.t("ac_read", "鲜血交易")
			.t("ac_read2", "灵魂转换")
			.t("desc", "一张以鲜血达成的契约，可以用生命上限换取永久力量。英雄每升一级便可额外交易一次，击败敌人可以收集灵魂。")
			.t("hitup", "你感觉自己的命中能力提升了。")
			.t("evaup", "你感觉自己的闪避能力提升了。")
			.t("migup", "你感觉自己的魔法能力提升了。")
			.t("htdown", "你感觉自己的生命上限降低了。")
			.t("htup", "你感觉自己的生命上限提升了。")
			.t("charge", "灵魂数量：%d。")
			.t("charge2", "已使用鲜血交易次数：%d。");
	}



	public static final String AC_READ = "READ";
	public static final String AC_READ2 = "READ2";
	private static final String SOULS = "souls";
	private static final String TRADES = "trades";

	private int souls;
	private int trades;

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = false;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (canBloodTrade(hero)) actions.add(AC_READ);
		if (souls > 10) actions.add(AC_READ2);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_READ.equals(action)) {
			bloodTrade(hero);
		} else if (AC_READ2.equals(action)) {
			soulImbue(hero);
		} else {
			super.execute(hero, action);
		}
	}

	public boolean canBloodTrade(Hero hero) {
		return hero != null && trades < hero.lvl && hero.permanentHT() > hero.lvl;
	}

	/** @return the improved skill index, or -1 when the trade is unavailable. */
	public int bloodTrade(Hero hero) {
		if (!canBloodTrade(hero)) return -1;

		int result = Random.Int(3);
		switch (result) {
			case 0:
				hero.improveAttackSkill(1);
				Buff.affect(hero, Muscle.class, 50f);
				GLog.w(Messages.get(this, "hitup"));
				break;
			case 1:
				hero.improveDefenseSkill(1);
				Buff.affect(hero, Rhythm.class, 50f);
				GLog.w(Messages.get(this, "evaup"));
				break;
			default:
				hero.improveMagicSkill(1);
				Buff.affect(hero, Recharging.class, 50f);
				GLog.w(Messages.get(this, "migup"));
				break;
		}

		trades++;
		if (hero.sprite != null) hero.sprite.operate(hero.pos);
		hero.busy();
		hero.spend(2f);
		int cost = Math.max(1, hero.spp + 1);
		hero.damage(cost, this);
		int permanentCost = Math.min(cost, Math.max(0, hero.permanentHT() - 1));
		if (permanentCost > 0) hero.spendPermanentHT(permanentCost);
		GLog.w(Messages.get(this, "htdown"));
		updateQuickslot();
		return result;
	}

	public boolean soulImbue(Hero hero) {
		if (hero == null || souls <= 10) return false;
		souls -= 10;
		hero.HTBoost++;
		hero.updateHT(true);
		GLog.w(Messages.get(this, "htup"));
		updateQuickslot();
		return true;
	}

	public void gainSoul() {
		if (souls < Integer.MAX_VALUE) souls++;
		updateQuickslot();
	}

	public int souls() { return souls; }
	public int trades() { return trades; }

	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", souls) + "\n\n" + Messages.get(this, "charge2", trades); }
	@Override public String status() { return Integer.toString(souls); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SOULS, souls);
		bundle.put(TRADES, trades);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		souls = Math.max(0, bundle.getInt(SOULS));
		trades = Math.max(0, bundle.getInt(TRADES));
	}
}
