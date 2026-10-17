/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.equipment.bags.Bag;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndOptions;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;

/**
 * SPSEXPD: 奥法轴芯 —— 法师一档（面具 A 路线）的特殊法杖。
 *
 * <p>它本身可以施法，并且能_吞噬_别的法杖：被吞掉的法杖会化作一道「法术」被它收录
 * （最多 {@link #MAX_SPELLS} 种），之后可以在收录的法术之间_自由切换_。</p>
 */
public class ArcaneCore extends MagesStaff implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.MAGE_STAFF_T1_1_CORE;

	public static final String AC_SWITCH = "SWITCH";

	public static final int MAX_SPELLS = 3;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(ArcaneCore.class)
			.t("name", "奥法轴芯")
			.t("ac_switch", "切换法术")
			.t("swallow_prompt", "选择要吞噬的法杖")
			.t("swallowed", "轴芯吞下了%s，收录了这道法术（%d/%d）。")
			.t("full", "轴芯已经收录了 %d 道法术，无法再吞下更多。")
			.t("switch_title", "选择要使用的法术")
			.t("no_switch", "轴芯里只收录了一道法术，没什么可切换的。")
			.t("switched", "轴芯转向了%s的法术。")
			.t("desc", "一根空心的法师轴芯，可以像老法杖一样施法。\n\n「吞噬」：把一根法杖喂给它——法杖本体就此消融，只留下那道_法术_被轴芯收录。最多收录 _%1$d 道_法术。\n\n「切换法术」：在已收录的法术之间自由切换，想用哪道就用哪道。");
	}

	/** SPSEXPD: 已收录的法术（法杖实例，只保留法术本身）。 */
	protected final ArrayList<Wand> spells = new ArrayList<>();
	/** SPSEXPD: 当前正在使用的法术在 spells 里的下标（-1 = 尚未收录任何法术）。 */
	protected int current = -1;

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", maxSpells());
	}

	/** 本轴芯最多收录几种法术（奥法古籍覆写为 9）。 */
	protected int maxSpells() {
		return MAX_SPELLS;
	}

	public int spellCount() {
		return spells.size();
	}

	/**
	 * SPSEXPD: 轴芯的「能量值」只有一份——收录的所有法术共用当前这道法术的充能与上限。
	 * 切换之前先把能量广播给全部法术，这样切到哪一道都用同一个池子（施法/自然充能都作用于当前那道，
	 * 下一次切换时再广播一次即可）。
	 */
	protected void syncCharges() {
		Wand cur = currentWand();
		if (cur == null) return;
		for (Wand w : spells) {
			w.curCharges = cur.curCharges;
			w.maxCharges = cur.maxCharges;
		}
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_SWITCH);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_IMBUE.equals(action)) {
			//SPSEXPD: 「灌注」在此被改写成「吞噬法杖」
			curUser = hero;
			curItem = this;
			GameScene.selectItem(swallowSelector);
			return;
		}
		if (AC_SWITCH.equals(action)) {
			curUser = hero;
			switchSpell(hero);
			return;
		}
		super.execute(hero, action);
	}

	/** SPSEXPD: 吞噬一根法杖——法杖本体消融，只收录其法术。 */
	public boolean swallow(Wand source) {
		if (source == null) return false;
		if (spells.size() >= maxSpells()) {
			GLog.w(Messages.get(this, "full", maxSpells()));
			return false;
		}
		if (Dungeon.hero == null) return false;

		String srcName = source.name();
		source.detach(Dungeon.hero.belongings.backpack);

		Wand copy = Reflection.newInstance(source.getClass());
		if (copy == null) return false;
		copy.level(level());
		copy.identify();
		//SPSEXPD: 能量统一——新收录的法术直接沿用轴芯当前的充能，而不是自己补满
		Wand cur = currentWand();
		copy.curCharges = cur != null ? cur.curCharges : copy.maxCharges;

		spells.add(copy);
		if (current < 0 || currentWand() == null) {
			current = spells.size() - 1;
			setWandDirect(copy);
		}
		GLog.p(Messages.get(this, "swallowed", srcName, spells.size(), maxSpells()));
		updateQuickslot();
		return true;
	}

	/** SPSEXPD: 在已收录的法术间切换。 */
	public void switchSpell(Hero hero) {
		if (spells.size() <= 1) {
			GLog.w(Messages.get(this, "no_switch"));
			return;
		}
		String[] opts = new String[spells.size()];
		for (int i = 0; i < spells.size(); i++) {
			opts[i] = spells.get(i).name();
		}
		GameScene.show(new WndOptions(new ItemSprite(this), Messages.titleCase(name()),
				Messages.get(this, "switch_title"), opts) {
			@Override
			protected void onSelect(int index) {
				if (index < 0 || index >= spells.size()) return;
				//SPSEXPD: 先把当前能量广播给全部法术（能量值只有一份）
				syncCharges();
				current = index;
				setWandDirect(spells.get(index));
				GLog.p(Messages.get(ArcaneCore.class, "switched", spells.get(index).name()));
				hero.spendAndNext(1f);
			}
		});
	}

	protected static WndBag.ItemSelector swallowSelector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(ArcaneCore.class, "swallow_prompt");
		}
		@Override
		public Class<? extends Bag> preferredBag() {
			return Belongings.Backpack.class;
		}
		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Wand && item != curItem;
		}
		@Override
		public void onSelect(Item item) {
			if (!(item instanceof Wand) || !(curItem instanceof ArcaneCore)) return;
			((ArcaneCore) curItem).swallow((Wand) item);
		}
	};

	private static final String SPELLS = "spells";
	private static final String CURRENT = "current";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SPELLS, spells);
		bundle.put(CURRENT, current);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		spells.clear();
		for (render.utils.serialize.Bundlable b : bundle.getCollection(SPELLS)) {
			if (b instanceof Wand) spells.add((Wand) b);
		}
		current = bundle.getInt(CURRENT);
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
