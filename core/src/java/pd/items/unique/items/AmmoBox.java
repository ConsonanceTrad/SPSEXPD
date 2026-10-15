/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.Magazine;
import pd.items.StoneOre;
import pd.items.equipment.bags.Bag;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

/**
 * SPSEXPD: 弹药箱 —— 星兵一档（面具 B 路线）的道具。
 *
 * <p>两种产弹方式：塞进一块_原石_立刻压出一个通用弹匣；或者放着不管，
 * 它每 {@link #CHARGE_TURNS} 回合_自然充能_出一个通用弹匣（最多存 {@link #MAX_STORED} 个）。</p>
 */
public class AmmoBox extends Item implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.SOLDIER_GUN_T1_2_AMMOBOX;

	public static final String AC_LOAD = "LOAD";
	/** 自然充能出一个弹匣所需的回合数。 */
	public static final int CHARGE_TURNS = 40;
	/** 最多囤积几个弹匣。 */
	public static final int MAX_STORED = 5;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(AmmoBox.class)
			.t("name", "弹药箱")
			.t("ac_load", "填入原石")
			.t("no_ore", "你没有原石可以填进去。")
			.t("loaded", "你把原石塞进弹药箱，它咔嗒作响，吐出一个通用弹匣。")
			.t("full", "弹药箱已经装满了。")
			.t("produced", "弹药箱又压好了一个通用弹匣。")
			.t("desc", "一只带压柄的铁皮弹药箱。\n\n「填入原石」：塞进一块_原石_，立刻压出 _1 个通用弹匣_。\n\n就算不管它，它也会自己慢慢工作——每 _%1$d 回合_自然充能出 1 个通用弹匣，最多囤 _%2$d 个_。");
	}

	private static final String PROGRESS = "progress";
	private static final String STORED = "stored";

	private int progress = 0;
	private int stored = 0;

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", CHARGE_TURNS, MAX_STORED);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_LOAD);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (!AC_LOAD.equals(action)) return;

		if (stored >= MAX_STORED) {
			GLog.w(Messages.get(this, "full"));
			return;
		}
		StoneOre ore = hero.belongings.getItem(StoneOre.class);
		if (ore == null) {
			GLog.w(Messages.get(this, "no_ore"));
			return;
		}
		ore.detach(hero.belongings.backpack);
		produce(hero, 1);
		GLog.p(Messages.get(this, "loaded"));
		hero.spendAndNext(1f);
	}

	/** SPSEXPD: 产出一个弹匣（优先塞进背包，背包满则掉地上）。 */
	private void produce(Hero hero, int count) {
		for (int i = 0; i < count; i++) {
			Magazine mag = new Magazine();
			if (!mag.collect(hero.belongings.backpack) && Dungeon.level != null) {
				Dungeon.level.drop(mag, hero.pos);
			}
		}
	}

	@Override
	public boolean collect(Bag container) {
		boolean ok = super.collect(container);
		if (ok && Dungeon.hero != null) {
			Buff.affect(Dungeon.hero, Feeder.class).box = this;
		}
		return ok;
	}

	/** SPSEXPD: 每回合推进自然充能（挂在英雄身上，指向背包里的弹药箱）。 */
	public static class Feeder extends Buff {

		private AmmoBox box;

		@Override
		public boolean act() {
			Hero hero = Dungeon.hero;
			if (box == null || hero == null || hero.belongings == null || !hero.belongings.contains(box)) {
				detach();
				return false;
			}
			if (box.stored < MAX_STORED) {
				box.progress++;
				if (box.progress >= CHARGE_TURNS) {
					box.progress = 0;
					box.stored++;
					box.produce(hero, 1);
					if (Dungeon.level != null && Dungeon.level.heroFOV != null
							&& hero.pos >= 0 && hero.pos < Dungeon.level.heroFOV.length
							&& Dungeon.level.heroFOV[hero.pos]) {
						GLog.i(Messages.get(AmmoBox.class, "produced"));
					}
				}
			}
			spend(Actor.TICK);
			return true;
		}

		private static final String BOX = "box";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(BOX, box);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			box = (AmmoBox) bundle.get(BOX);
		}
	}

	@Override
	public boolean isUpgradable() { return false; }

	@Override
	public boolean isIdentified() { return true; }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PROGRESS, progress);
		bundle.put(STORED, stored);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		progress = bundle.getInt(PROGRESS);
		stored = bundle.getInt(STORED);
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
