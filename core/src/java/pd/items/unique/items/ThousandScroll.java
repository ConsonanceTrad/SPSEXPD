/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.buffs.BleedingRite;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.artifacts.Artifact;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: 小千录 —— 信徒一档（面具 A 路线）的神器。
 *
 * <p>「开启」后进入_持续累加的流血_状态，以血换命：每回合流血量递增，同时把血补回来，
 * 并在开启期间_挂起除流血外的所有负面状态_。关闭后要等冷却走完才能再次开启。</p>
 */
public class ThousandScroll extends Artifact implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ASCETIC_EARRING_T1_1_SCROLL;

	public static final String AC_OPEN = "OPEN";
	public static final String AC_CLOSE = "CLOSE";

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(ThousandScroll.class)
			.t("name", "小千录")
			.t("ac_open", "开启")
			.t("ac_close", "收起")
			.t("cooldown", "它还没晾干上次的血。")
			.t("opened", "书页自己翻开了，你的血开始沿着字迹往外渗。")
			.t("closed", "书页合上，血也慢慢收住了。")
			.t("desc", "一本册子，纸面永远潮乎乎的——没有下雨，它却湿漉漉的，这肯定很脏。\n\n「开启」：进入_持续累加的流血_状态。每回合你都会流血（出血量逐回合递增），但这些血会以_更高的比例补回_你的生命。\n\n开启期间，除流血外的_其它负面状态会被挂起_（既不生效，也不会消失；收起后原样回来）。\n\n「收起」后需要 _%1$d 回合_冷却才能再次开启。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
		chargeCap = cooldownTurns();
		charge = chargeCap;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", cooldownTurns());
	}

	/** 起手出血量。 */
	protected float bleedStart() { return 1f; }
	/** 每回合出血量的增量。 */
	protected float bleedGrowth() { return 0.5f; }
	/** 补血相对流血量的倍率。 */
	protected float healFactor() { return 1.5f; }
	/** 开启期间的移速加成。 */
	protected float speedBonus() { return 0f; }
	/** 开启期间的伤害加成。 */
	protected int damageBonus() { return 0; }
	/** 收起后的冷却回合。 */
	protected int cooldownTurns() { return 10; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(hero.buff(BleedingRite.class) != null ? AC_CLOSE : AC_OPEN);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_OPEN.equals(action)) {
			if (hero.buff(BleedingRite.class) != null) return;
			chargeCap = cooldownTurns();
			if (charge < chargeCap) {
				GLog.w(Messages.get(this, "cooldown"));
				return;
			}
			Buff.affect(hero, BleedingRite.class).setup(
					bleedStart(), bleedGrowth(), healFactor(), speedBonus(), damageBonus());
			charge = 0;
			GLog.p(Messages.get(this, "opened"));
			hero.spendAndNext(1f);
			return;
		}
		if (AC_CLOSE.equals(action)) {
			BleedingRite rite = hero.buff(BleedingRite.class);
			if (rite == null) return;
			rite.restoreSuspended(hero);
			rite.detach();
			charge = 0;
			GLog.i(Messages.get(this, "closed"));
			hero.spendAndNext(1f);
			return;
		}
		super.execute(hero, action);
	}

	/** SPSEXPD: 用神器充能充当「收起后的冷却」。 */
	@Override
	protected ArtifactBuff passiveBuff() {
		return new Cooldown();
	}

	public class Cooldown extends ArtifactBuff {
		@Override
		public boolean act() {
			chargeCap = cooldownTurns();
			if (charge < chargeCap) {
				charge++;
				updateQuickslot();
			}
			spend(TICK);
			return true;
		}
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
