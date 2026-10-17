/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.artifacts.Artifact;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: 临时义体的公共骨架。
 *
 * <p>义体沿用神器的充能机制当作_电量_：每使用一次扣 1 点，电量耗尽时义体直接损毁消失。
 * 具体效果由子类在 {@link #activateEffect(Hero)} 里实现。</p>
 */
public abstract class Cyberware extends Artifact {

	public static final String AC_USE = "USE";

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Cyberware.class)
			.t("ac_use", "启动")
			.t("no_charge", "这台义体的电量已经耗尽了。")
			.t("destroyed", "%s的电量耗尽，彻底报废了。")
			.t("desc", "一件临时义体，靠有限电量驱动。\n\n「启动」会消耗 1 点电量；电量耗尽时它就会_损毁消失_。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		chargeCap = capacity();
		charge = chargeCap;
	}

	/** 电量（可用次数）。 */
	protected int capacity() {
		return 5;
	}

	/** SPSEXPD: 提高电量上限（强化义体工具箱用）。 */
	public void boostCapacity(float factor) {
		int boosted = Math.max(chargeCap, Math.round(chargeCap * factor));
		chargeCap = boosted;
		charge = boosted;
	}

	/** SPSEXPD: 所有义体共用同一段描述。 */
	@Override
	public String desc() {
		return Messages.get(Cyberware.class, "desc");
	}

	/** 启动一次的效果。 */
	protected abstract void activateEffect(Hero hero);

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_USE.equals(action)) {
			if (charge <= 0) {
				GLog.w(Messages.get(this, "no_charge"));
				return;
			}
			charge--;
			updateQuickslot();
			activateEffect(hero);
			hero.spendAndNext(1f);
			if (charge <= 0) {
				GLog.w(Messages.get(Cyberware.class, "destroyed", name()));
				detach(hero.belongings.backpack);
			}
			return;
		}
		super.execute(hero, action);
	}

	/** SPSEXPD: 义体不会自己回电——每回合只保证电量不涨。 */
	@Override
	protected ArtifactBuff passiveBuff() {
		return new Idle();
	}

	public class Idle extends ArtifactBuff {
		@Override
		public boolean act() {
			chargeCap = capacity();
			if (charge > chargeCap) charge = chargeCap;
			spend(TICK);
			return true;
		}
	}
}
