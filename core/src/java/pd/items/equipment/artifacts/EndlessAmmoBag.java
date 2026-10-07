/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.artifacts;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.actors.buffs.Buff;
import pd.actors.buffs.EndlessAmmo;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: 原创神器——无限弹药袋。
 *
 * <p>装备后缓慢自行充能；充能达到门槛即可激活：激活期间按回合消耗充能维持，
 * 期间所有射击（投掷武器、枪械、投掷器的内仓）都不再消耗弹药。充能耗尽会自动关闭。</p>
 */
public class EndlessAmmoBag extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EndlessAmmoBag.class)
			.t("name", "无限弹药袋")
			.t("ac_activate", "激活")
			.t("activate", "弹药袋开始自行填充你的弹药。")
			.t("already", "弹药袋已经在生效了。")
			.t("no_charge", "充能不足，无法激活。")
			.t("drained", "弹药袋的充能耗尽了。")
			.t("desc", "一个内里远比外表大的弹药袋。装备后它会缓慢自行填充；充能足够时可以激活，让射出的每一发弹药都被袋子重新补上。\n\n激活期间按回合消耗充能，充能耗尽后自动关闭。")
			.t("desc_worn", "袋子正在缓慢充能，充能足够时可以激活。");
	}

	public static final String AC_ACTIVATE = "ACTIVATE";
	/** 激活后每回合消耗的充能。 */
	public static final float DRAIN_PER_TURN = 3f;
	/** 激活所需的最低充能。 */
	public static final int ACTIVATE_COST = 20;

	{
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
		levelCap = 0;
		charge = 0;
		partialCharge = 0;
		chargeCap = 100;
		defaultAction = AC_ACTIVATE;
	}

	public int charge() {
		return charge;
	}

	public int chargeCap() {
		return chargeCap;
	}

	/** 供激活状态每回合扣充能用。 */
	public void consumeCharge(int amount) {
		addCharge(-Math.max(0, amount));
	}

	/** 直接增减充能（受 0..chargeCap 限制）。 */
	public void addCharge(int amount) {
		charge = Math.max(0, Math.min(chargeCap, charge + amount));
		updateQuickslot();
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && hero.buff(EndlessAmmo.class) == null && charge >= ACTIVATE_COST) {
			actions.add(AC_ACTIVATE);
		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (!AC_ACTIVATE.equals(action)) return;

		if (!isEquipped(hero)) {
			GLog.i(Messages.get(Artifact.class, "need_to_equip"));
		} else if (hero.buff(EndlessAmmo.class) != null) {
			GLog.i(Messages.get(this, "already"));
		} else if (charge < ACTIVATE_COST) {
			GLog.i(Messages.get(this, "no_charge"));
		} else {
			Buff.affect(hero, EndlessAmmo.class);
			GLog.p(Messages.get(this, "activate"));
			if (com.badlogic.gdx.Gdx.audio != null) Sample.INSTANCE.play(Assets.Sounds.EVOKE);
			hero.spend(1f);
			hero.busy();
		}
	}

	@Override
	public String desc() {
		String desc = super.desc();
		if (isEquipped(pd.Dungeon.hero)) desc += "\n\n" + Messages.get(this, "desc_worn");
		return desc;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Feeding();
	}

	/** 装备期间自行充能：约每回合 2 点。 */
	public class Feeding extends ArtifactBuff {
		@Override
		public boolean act() {
			if (charge < chargeCap) {
				partialCharge += 2f;
				while (partialCharge >= 1f && charge < chargeCap) {
					partialCharge -= 1f;
					charge++;
				}
				if (charge >= chargeCap) partialCharge = 0f;
			} else {
				partialCharge = 0f;
			}
			updateQuickslot();
			spend(TICK);
			return true;
		}
	}
}
