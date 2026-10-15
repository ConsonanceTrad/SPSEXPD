/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;

import java.util.ArrayList;
import java.util.Collections;

/**
 * SPSEXPD: 秘银手杖 —— 法师一档（面具 B 路线）的近战特殊武器。
 *
 * <p>_无法施法_，只是一根近战武器：每次近战命中会为_随机 2 支_法杖少量补充能量，
 * 并且带上你_上次施放的法术_的完整命中效果。</p>
 */
public class MythrilStaff extends MagesStaff implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.MAGE_STAFF_T1_2_MITHRIL;

	/** 近战一次为多少支法杖回能。 */
	public static final int RECHARGE_COUNT = 2;
	/** 每支法杖补充的充能点数。 */
	public static final float RECHARGE_AMOUNT = 0.34f;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(MythrilStaff.class)
			.t("name", "秘银手杖")
			.t("desc", "一根封了银的实心手杖——它_没法施法_，只能拿来打人。\n\n每次近战命中，它会为_随机 %1$d 支_法杖各补上一点能量；同时，你_上次施放的法术_会顺着杖身涌出，让这一击带上那道法术的完整命中效果。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", RECHARGE_COUNT);
	}

	/** SPSEXPD: 无法施法——移除「释放」与「灌注」动作。 */
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_ZAP);
		actions.remove(AC_IMBUE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		//SPSEXPD: 没法施法——这两个动作直接忽略
		if (AC_ZAP.equals(action) || AC_IMBUE.equals(action)) return;
		super.execute(hero, action);
	}

	/** 本次近战为多少支法杖回能（精金手杖覆写为「全部」）。 */
	protected int rechargeCount(int total) {
		return Math.min(RECHARGE_COUNT, total);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (attacker instanceof Hero) {
			onMeleeRecharge((Hero) attacker, defender, damage);
		}
		return super.proc(attacker, defender, damage);
	}

	/** SPSEXPD: 近战命中 → 给若干支法杖回能 + 重放上次施法的完整命中效果。 */
	protected void onMeleeRecharge(Hero hero, Char defender, int damage) {
		ArrayList<Wand> wands = new ArrayList<>();
		for (Item it : hero.belongings) {
			if (it instanceof Wand) wands.add((Wand) it);
		}
		if (!wands.isEmpty()) {
			int n = rechargeCount(wands.size());
			if (n < wands.size()) Collections.shuffle(wands);
			for (int i = 0; i < n; i++) {
				wands.get(i).gainCharge(RECHARGE_AMOUNT);
			}
		}

		Class<? extends Wand> lastSpell = SpsSpellMemory.last();
		if (lastSpell != null && defender != null) {
			Wand echo = render.utils.serialize.Reflection.newInstance(lastSpell);
			if (echo != null) {
				echo.onHit(this, hero, defender, damage);
			}
		}
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
