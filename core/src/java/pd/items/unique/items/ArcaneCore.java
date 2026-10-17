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
			.t("replace_title", "用%s替换掉哪一道已收录的法术？")
			.t("switch_title", "选择要使用的法术")
			.t("no_switch", "轴芯里只收录了一道法术，没什么可切换的。")
			.t("switched", "轴芯转向了%s的法术。")
			.t("desc", "一根空心的法师轴芯，可以像老法杖一样施法。\n\n「吞噬」：把一根法杖喂给它——法杖本体就此消融，只留下那道_法术_被轴芯收录。最多收录 _%1$d 道_法术。\n\n「切换法术」：在已收录的法术之间自由切换，想用哪道就用哪道。\n\n它的_能量上限不看等级_：基础 _2 点_，每收录一道法术 _+4 点_；_充能速度_也随收录数提高（每道 +50%%）。\n\n吞噬时与老法杖灌注同规则：会继承比自己等级更高的法杖等级，等级相等时额外获得一次_免费升级_。");
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

	/**
	 * SPSEXPD: 轴芯的能量上限不看等级，看_收录了几道法术_——基础上限 {@link #BASE_CAPACITY} 点，
	 * 每收录一道 +{@link #CAPACITY_PER_SPELL} 点。奥法古籍沿用同一套模型（只是能收更多道）。
	 */
	public static final int BASE_CAPACITY = 2;
	public static final int CAPACITY_PER_SPELL = 4;
	/** SPSEXPD: 每收录一道法术，充能速度 +50%。 */
	public static final float REGEN_PER_SPELL = 0.5f;

	/** SPSEXPD: 当前应有的能量上限。 */
	public int coreCapacity() {
		return BASE_CAPACITY + CAPACITY_PER_SPELL * spells.size();
	}

	/** SPSEXPD: 充能速度倍率（随收录数提高）。 */
	public float chargeRateMultiplier() {
		return 1f + REGEN_PER_SPELL * spells.size();
	}

	/** SPSEXPD: 把能量上限按「收录数」重新套到当前法术上（升级不提高上限）。 */
	protected void applyCoreCapacity() {
		Wand cur = currentWand();
		if (cur == null) return;
		cur.maxCharges = coreCapacity();
		cur.curCharges = Math.min(cur.curCharges, cur.maxCharges);
	}

	@Override
	public void updateWand(boolean levelled) {
		super.updateWand(levelled);
		//SPSEXPD: 覆盖掉父类按等级给的上限——轴芯的上限只由收录数决定
		applyCoreCapacity();
	}

	/** SPSEXPD: 充能速度随收录数提高（恢复效率）。 */
	@Override
	public void gainCharge(float amt, boolean notify) {
		super.gainCharge(amt * chargeRateMultiplier(), notify);
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

	/** SPSEXPD: 吞噬一根法杖——法杖本体消融，只收录其法术；已收满时可以选择替换掉其中一道。 */
	public boolean swallow(Wand source) {
		if (source == null || Dungeon.hero == null) return false;

		if (spells.size() >= maxSpells()) {
			//SPSEXPD: 已经收满——让玩家挑一道替换掉，而不是直接拒绝
			askReplace(source);
			return false;
		}
		return bind(source, -1);
	}

	/** SPSEXPD: 收满后弹出选择，把某一道已收录的法术替换成新的法杖。 */
	protected void askReplace(Wand source) {
		if (spells.isEmpty()) return;
		String[] opts = new String[spells.size()];
		for (int i = 0; i < spells.size(); i++) {
			opts[i] = spells.get(i).name();
		}
		GameScene.show(new WndOptions(new ItemSprite(this), Messages.titleCase(name()),
				Messages.get(this, "replace_title", source.name()), opts) {
			@Override
			protected void onSelect(int index) {
				if (index < 0 || index >= spells.size()) return;
				bind(source, index);
			}
		});
	}

	/** SPSEXPD: 真正收下这根法杖；replaceIndex >= 0 表示原地替换掉该位置的法术。 */
	protected boolean bind(Wand source, int replaceIndex) {
		Wand cur = currentWand();
		int keepCharges = cur != null ? cur.curCharges : -1;

		//SPSEXPD: 与老法杖灌注同规则——继承比自己高的法杖等级；
		//等级相等（且自身已有等级）时额外 +1，相当于一次免费升级
		int targetLevel = Math.max(trueLevel(), source.trueLevel());
		if (source.trueLevel() >= trueLevel() && trueLevel() > 0) targetLevel++;

		String srcName = source.name();
		source.detach(Dungeon.hero.belongings.backpack);
		level(targetLevel);

		Wand copy = Reflection.newInstance(source.getClass());
		if (copy == null) return false;
		copy.level(level());
		copy.identify();
		//SPSEXPD: 能量统一——新法术沿用轴芯当前的充能，而不是自己补满
		copy.curCharges = keepCharges >= 0 ? keepCharges : copy.maxCharges;

		if (replaceIndex >= 0 && replaceIndex < spells.size()) {
			//SPSEXPD: 替换指定位置（若替换的正是当前法术，就地换掉）
			boolean replacingCurrent = (replaceIndex == current);
			spells.set(replaceIndex, copy);
			if (replacingCurrent) current = replaceIndex;
		} else {
			spells.add(copy);
			if (current < 0 || cur == null) current = spells.size() - 1;
		}

		if (current < 0 || current >= spells.size()) current = 0;
		setWandDirect(spells.get(current));
		//SPSEXPD: 收录数变了，能量上限要跟着提高（并按新上限刷新当前法术）
		applyCoreCapacity();
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
