/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.materials;

import java.util.ArrayList;

import pd.Dungeon;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Drowsy;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Slow;
import pd.actors.buffs.Vertigo;
import pd.actors.buffs.Vulnerable;
import pd.actors.buffs.Weakness;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumGoodsMaterialsMaterialsExtraDict;
import pd.items.Item;
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.PotionForm;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.consum.potions.PotionOfPurity;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 魔法药瓶（docs/alchemy-rework.md §6.6，裁决 #5/#6）。
 *
 * <p>挂在包裹体系上的**自动喂食组件**：装填一种正面试剂后，
 * 英雄陷入对应危机时自动喂下一瓶（消耗 1 点炼金能量换 1 瓶）。
 *
 * <ul>
 *   <li>治疗类试剂：生命低于 60% 时自动喂食；</li>
 *   <li>净化类试剂：身上带负面效果时自动喂食；</li>
 *   <li>其它试剂：首批不自动喂食（后续按反馈调整触发条件）。</li>
 * </ul>
 */
public class MagicFeederBottle extends Item {

	public static final String AC_LOAD = "LOAD";

	/** 一次最多装填几瓶试剂。 */
	public static final int CAPACITY = 5;
	/** 每次自动喂食消耗的炼金能量。 */
	public static final int ENERGY_PER_FEED = 1;
	/** 治疗类试剂的触发阈值（占最大生命的比例）。 */
	public static final float HEAL_THRESHOLD = 0.6f;

	/** 装填的试剂原型（只记录类型）。 */
	private Potion stored = null;
	/** 剩余瓶数。 */
	private int amount = 0;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(MagicFeederBottle.class)
			.t("name", "魔法药瓶")
			.t("desc", "一个会自己动手的小瓶子。装填一种正面试剂后，"
					+ "它会在你陷入对应的麻烦时替你喂下去（每次花费 1 点炼金能量）。"
					+ "治疗类试剂在生命低于六成时触发，净化类在你身上带负面效果时触发。")
			.t("ac_load", "装填试剂")
			.t("prompt", "选择一种试剂装入魔法药瓶")
			.t("empty", "瓶里还没有装填任何试剂。")
			.t("loaded", "装填完成：%1$s ×%2$d")
			.t("full", "瓶里已经装满了。")
			.t("no_reagent", "你身上没有试剂（先用炼金釜把药剂做成试剂）。")
			.t("fed", "魔法药瓶自动喂下了一瓶%1$s。")
			.t("no_energy", "炼金能量不足，魔法药瓶没有动作。");
	}

	{
		image = ConsumGoodsMaterialsMaterialsExtraDict.DRY_TWIG;
		stackable = false;
		defaultAction = AC_LOAD;
		bones = false;
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

		curUser = hero;
		boolean any = false;
		for (Item item : hero.belongings.backpack.items) {
			if (isReagent(item)) { any = true; break; }
		}
		if (!any) {
			GLog.w(Messages.get(this, "no_reagent"));
			return;
		}
		if (amount >= CAPACITY) {
			GLog.w(Messages.get(this, "full"));
			return;
		}
		GameScene.selectItem(selector);
	}

	private static boolean isReagent(Item item) {
		return item instanceof Potion && ((Potion) item).form == PotionForm.REAGENT;
	}

	private static final WndBag.ItemSelector selector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(MagicFeederBottle.class, "prompt");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return isReagent(item);
		}

		@Override
		public void onSelect(Item item) {
			if (!isReagent(item)) return;
			Potion reagent = (Potion) item;

			MagicFeederBottle bottle = curUser.belongings.getItem(MagicFeederBottle.class);
			if (bottle == null) return;

			//换了别的试剂就重新装：旧试剂原样退回背包
			if (bottle.stored != null && bottle.stored.getClass() != reagent.getClass()) {
				bottle.dropStored(curUser);
			}

			int loaded = Math.min(reagent.quantity(), CAPACITY - bottle.amount);
			if (loaded <= 0) {
				GLog.w(Messages.get(MagicFeederBottle.class, "full"));
				return;
			}

			bottle.stored = Reflection.newInstance(reagent.getClass());
			if (bottle.stored != null) bottle.stored.form = PotionForm.REAGENT;
			bottle.amount += loaded;

			reagent.quantity(reagent.quantity() - loaded);
			if (reagent.quantity() <= 0) reagent.detach(curUser.belongings.backpack);

			GLog.p(Messages.get(MagicFeederBottle.class, "loaded",
					Messages.titleCase(bottle.stored == null ? "" : bottle.stored.name()), bottle.amount));
			curUser.spendAndNext(1f);
		}
	};

	/** 把瓶里剩的试剂退回背包/地面。 */
	private void dropStored(Hero hero) {
		if (stored == null || amount <= 0) {
			stored = null;
			amount = 0;
			return;
		}
		Potion back = Reflection.newInstance(stored.getClass());
		if (back != null) {
			back.form = PotionForm.REAGENT;
			back.quantity(amount);
			if (!back.collect()) Dungeon.level.drop(back, hero.pos);
		}
		stored = null;
		amount = 0;
	}

	/** 每回合由英雄行动触发：满足条件就自动喂一瓶。 */
	public static void autofeed(Hero hero) {
		if (hero == null || hero.belongings == null) return;
		MagicFeederBottle bottle = hero.belongings.getItem(MagicFeederBottle.class);
		if (bottle != null) bottle.tryFeed(hero);
	}

	private void tryFeed(Hero hero) {
		if (stored == null || amount <= 0) return;
		if (!shouldFeed(hero)) return;

		if (Dungeon.energy < ENERGY_PER_FEED) {
			return;
		}
		Dungeon.energy -= ENERGY_PER_FEED;
		amount--;

		Potion dose = Reflection.newInstance(stored.getClass());
		if (dose != null) {
			dose.form = PotionForm.REAGENT;
			//自动服用不应揭示图鉴/计天赋
			dose.anonymize();
			dose.apply(hero);
			GLog.i(Messages.get(this, "fed", Messages.titleCase(dose.name())));
		}

		if (amount <= 0) {
			stored = null;
			amount = 0;
		}
	}

	private boolean shouldFeed(Hero hero) {
		if (stored instanceof PotionOfHealing) {
			return hero.HP < hero.HT * HEAL_THRESHOLD;
		}
		if (stored instanceof PotionOfPurity) {
			return hasDebuff(hero);
		}
		return false;
	}

	/** 身上是否带常见负面效果。 */
	private static boolean hasDebuff(Hero hero) {
		return hero.buff(Poison.class) != null
				|| hero.buff(Burning.class) != null
				|| hero.buff(Bleeding.class) != null
				|| hero.buff(Ooze.class) != null
				|| hero.buff(Paralysis.class) != null
				|| hero.buff(Slow.class) != null
				|| hero.buff(Weakness.class) != null
				|| hero.buff(Vulnerable.class) != null
				|| hero.buff(Vertigo.class) != null
				|| hero.buff(Blindness.class) != null
				|| hero.buff(Cripple.class) != null
				|| hero.buff(Drowsy.class) != null;
	}

	//*** 存档

	private static final String STORED = "stored";
	private static final String AMOUNT = "amount";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (stored != null) bundle.put(STORED, stored.getClass().getName());
		bundle.put(AMOUNT, amount);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		amount = bundle.getInt(AMOUNT);
		if (bundle.contains(STORED)) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends Potion> type =
						(Class<? extends Potion>) Class.forName(bundle.getString(STORED), true, getClass().getClassLoader());
				stored = Reflection.newInstance(type);
				if (stored != null) stored.form = PotionForm.REAGENT;
			} catch (Exception e) {
				stored = null;
				amount = 0;
			}
		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public String name() {
		if (stored == null || amount <= 0) return super.name();
		return super.name() + "(" + Messages.titleCase(stored.name()) + " ×" + amount + ")";
	}

	@Override
	public int value() {
		return 120 * quantity;
	}
}
