/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
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
			.t("desc", "一根空心的法师轴芯，可以像老法杖一样施法。\n\n「吞噬」：把一根法杖喂给它——法杖本体就此消融，只留下那道_法术_被轴芯收录。最多收录 _%1$d 道_法术。\n\n「切换法术」：在已收录的法术之间自由切换，想用哪道就用哪道。\n\n它的_能量上限不看等级_：基础 _2 点_，每收录一道法术 _+4 点_。_充能速度_也随收录数提高：空着时约 _20 回合_回 1 点，每多收录一道快 _50%%_。\n\n吞噬时与老法杖灌注同规则：会继承比自己等级更高的法杖等级，等级相等时额外获得一次_免费升级_。");
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

	/** SPSEXPD: 轴芯自己的能量（唯一真源；内嵌法术只是被同步的壳）。 */
	protected int coreCharges;

	/** SPSEXPD: 自然充能的「零头」，攒满 1 就涨一点能量。 */
	protected float corePartial;

	/** SPSEXPD: 0 收录时的自然充能回合数（每收录一道快 50%）。 */
	public static final float BASE_TURNS_TO_CHARGE = 20f;

	/**
	 * SPSEXPD: 轴芯的自然充能——由自己身上的 {@link CoreCharger} 每回合驱动，
	 * <b>不再向内嵌法杖的 Charger 借力</b>。速率随收录数提高。
	 */
	protected void coreRecharge() {
		//SPSEXPD: 先把 Wand 上的最新值（含施法扣除）收回来，再累加，避免把扣除盖掉
		pullCharges();
		int cap = coreCapacity();
		if (coreCharges >= cap) {
			corePartial = 0f;
			return;
		}
		corePartial += chargeRateMultiplier() / BASE_TURNS_TO_CHARGE;
		while (corePartial >= 1f && coreCharges < cap) {
			corePartial--;
			coreCharges++;
			updateQuickslot();
		}
		if (coreCharges >= cap) corePartial = 0f;
		pushCharges();
	}

	/**
	 * SPSEXPD: 轴芯自己的充能 buff（照 Reloading.bind 的先例绑定物品）。
	 * 只负责每回合调一次 {@link ArcaneCore#coreRecharge()}。
	 */
	public static class CoreCharger extends Buff {
		{
			actPriority = Actor.MOB_PRIO;
		}

		private ArcaneCore core;

		public void bind(ArcaneCore core) {
			this.core = core;
		}

		@Override
		public boolean act() {
			if (core == null || core != equippedCore()) core = equippedCore();
			if (core != null) core.coreRecharge();
			spend(TICK);
			return true;
		}

		@Override
		public void detach() {
			core = null;
			super.detach();
		}

		private ArcaneCore equippedCore() {
			if (!(target instanceof Hero)) return null;
			Belongings b = ((Hero) target).belongings;
			if (b.weapon instanceof ArcaneCore) return (ArcaneCore) b.weapon;
			if (b.secondWep instanceof ArcaneCore) return (ArcaneCore) b.secondWep;
			return null;
		}
	}

	/** SPSEXPD: 改挂轴芯自己的充能 buff——不再给内嵌法杖挂 Wand.Charger。 */
	@Override
	public void applyWandChargeBuff(Char owner) {
		if (owner == null) return;
		Buff.affect(owner, CoreCharger.class).bind(this);
	}

	/** SPSEXPD: 把轴芯的能量推给当前法术（让 Wand 用它施法/显示）。 */
	protected void pushCharges() {
		Wand cur = currentWand();
		if (cur == null) return;
		cur.maxCharges = coreCapacity();
		cur.curCharges = Math.min(coreCharges, cur.maxCharges);
	}

	/** SPSEXPD: 把当前法术里剩下的能量收回轴芯。 */
	protected void pullCharges() {
		Wand cur = currentWand();
		if (cur != null) coreCharges = Math.min(cur.curCharges, coreCapacity());
	}

	/** SPSEXPD: 能量上限按「收录数」刷新，并把轴芯能量推给当前法术（升级不提高上限）。 */
	protected void applyCoreCapacity() {
		pullCharges();
		coreCharges = Math.min(coreCharges, coreCapacity());
		pushCharges();
	}

	/** SPSEXPD: 显示的是轴芯自己的能量，而不是收录法术的。 */
	@Override
	public String status() {
		//SPSEXPD: 施法的扣除发生在 Wand 上（wandUsed 在 onZap 回调里），读之前先收回来
		pullCharges();
		return coreCharges + "/" + coreCapacity();
	}

	@Override
	public void updateWand(boolean levelled) {
		super.updateWand(levelled);
		//SPSEXPD: 覆盖掉父类按等级给的上限——轴芯的上限只由收录数决定
		applyCoreCapacity();
	}

	/** SPSEXPD: 充能速度随收录数提高（恢复效率）；能量加在_轴芯自己_身上。 */
	@Override
	public void gainCharge(float amt, boolean notify) {
		coreCharges = Math.min(coreCharges + Math.round(amt * chargeRateMultiplier()), coreCapacity());
		pushCharges();
		updateQuickslot();
	}

	/** SPSEXPD: 动作列表按轴芯能量判断能否施法。 */
	@Override
	public ArrayList<String> actions(Hero hero) {
		//SPSEXPD: 只读不写——这里绝不能 pushCharges，否则会把 Wand 刚扣掉的能量又盖回去
		pullCharges();
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
		if (AC_ZAP.equals(action)) {
			//SPSEXPD: 施法前把轴芯能量交给 Wand（Wand 按 wand.curCharges 扣）。
			//注意 wandUsed() 里的扣除是在 onZap 回调里才执行的，所以这里不能立刻 pull；
			//扣除结果留在 wand 上，由下一次 pullCharges()（status / actions）收回轴芯
			pushCharges();
			super.execute(hero, action);
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
	private static final String CORE_CHARGES = "core_charges";
	private static final String CORE_PARTIAL = "core_partial";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SPELLS, spells);
		bundle.put(CURRENT, current);
		//SPSEXPD: 轴芯能量是唯一真源，必须一起存档（含充能零头）
		bundle.put(CORE_CHARGES, coreCharges);
		bundle.put(CORE_PARTIAL, corePartial);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		spells.clear();
		for (render.utils.serialize.Bundlable b : bundle.getCollection(SPELLS)) {
			if (b instanceof Wand) spells.add((Wand) b);
		}
		current = bundle.getInt(CURRENT);
		coreCharges = bundle.contains(CORE_CHARGES)
				? bundle.getInt(CORE_CHARGES)
				: (currentWand() != null ? currentWand().curCharges : 0);
		corePartial = bundle.contains(CORE_PARTIAL) ? bundle.getFloat(CORE_PARTIAL) : 0f;
		pushCharges();
	}

	/** SPSEXPD: 始终显示轴芯/古籍自己的名字，不显示当前收录法术的名字。 */
	@Override
	public String name() {
		String name = trueName();
		return enchantment != null && (cursedKnown || !enchantment.curse()) ? enchantment.name(name) : name;
	}

	/** SPSEXPD: 轴芯不显示力量需求（角标与详情都不显示）。 */
	@Override
	public boolean showStrength() {
		return false;
	}

	/**
	 * SPSEXPD: 升级时继承「灌注」——
	 * 来源是老法杖（MagesStaff）时，把它灌注的那根法杖收为第一道法术；
	 * 来源是奥法轴芯（升为奥法古籍）时，连收录的法术、当前选择与能量一起搬过来。
	 */
	public void inheritInfusion(Item source) {
		if (source == null || source == this) return;

		if (source instanceof ArcaneCore) {
			ArcaneCore old = (ArcaneCore) source;
			spells.clear();
			spells.addAll(old.spells);
			current = old.current;
			coreCharges = old.coreCharges;
			if (current >= 0 && current < spells.size()) {
				setWandDirect(spells.get(current));
			}
			applyCoreCapacity();
		} else if (source instanceof MagesStaff) {
			Wand w = ((MagesStaff) source).infusedWand();
			if (w != null) {
				spells.clear();
				spells.add(w);
				current = 0;
				coreCharges = w.curCharges;
				setWandDirect(w);
				applyCoreCapacity();
			}
		}
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
