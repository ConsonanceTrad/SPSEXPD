/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MetronomeBeat;
import pd.actors.buffs.PhysicalEmpower;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.artifacts.Artifact;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

/**
 * SPSEXPD: 节拍器 —— 演员的初始神器（面具 B 路线）。
 *
 * <p>节拍<b>不由玩家指定</b>：英雄每行动一次，系统就在他身边随机标出一拍（脚下周围八格）。
 * 下一步踩上去就攒一拍，连续 {@link MetronomeBeat#BEATS_REQUIRED} 拍攒满后，
 * <b>接下来那次行动的类型</b>决定奖励——移动给护盾，攻击给强化攻击。</p>
 */
public class Metronome extends Artifact implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.PERFORMER_SHOVEL_BASE_B;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Metronome.class)
			.t("name", "节拍器")
			.t("on_beat", "四拍踩满——时间为你让了一步。")
			.t("desc", "一台给走路配乐的小机器，摆锤走到你脚上就响一下。\n\n你_不需要自己定拍_：每行动一次，它就会在你身边八格里随机标出一拍。下一步正好走到标出的那一格，就算踩中，连击 _+1_；没踩中则连击_清零_。\n\n连续踩满 _%1$d_ 拍后，_接下来那次行动的类型_决定奖励：\n· 是_移动_，就给你一层护盾；\n· 是_攻击_（或其它非移动动作），就把下一次攻击强化。\n\n奖励到手当次_不消耗回合_。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", MetronomeBeat.BEATS_REQUIRED);
	}

	/** SPSEXPD: 维持「踩拍」状态。 */
	@Override
	protected ArtifactBuff passiveBuff() {
		return new Beat();
	}

	public class Beat extends ArtifactBuff {
		@Override
		public boolean act() {
			if (target instanceof Hero) {
				MetronomeBeat beat = Buff.affect((Hero) target, MetronomeBeat.class);
				//SPSEXPD: 刚拿到节拍器（或读档后）先垫一拍，免得第一次行动时没有标出的格子
				if (beat.beatCell() < 0 && !beat.awaiting()) beat.autoBeat((Hero) target);
			}
			spend(TICK);
			return true;
		}
	}

	/**
	 * SPSEXPD: 发放踩满四拍的奖励。
	 * @param moved true = 下一次行动是移动（给护盾）；false = 其它动作（强化攻击）
	 */
	public static void grantReward(Hero hero, boolean moved) {
		if (hero == null) return;
		MetronomeBeat beat = hero.buff(MetronomeBeat.class);

		if (moved) {
			Buff.affect(hero, Barkskin.class).set(hero.HT / 10, 1);
		} else {
			PhysicalEmpower emp = Buff.affect(hero, PhysicalEmpower.class);
			emp.dmgBoost = Math.max(1, hero.HT / 12);
			emp.left = 1;
		}
		//SPSEXPD: 这一下不消耗回合
		hero.spend(-1f);

		//SPSEXPD: 闪电竖琴——踩满节拍时额外获得一次玻璃保护
		if (hero.belongings.weapon() instanceof StormHarmonica
				|| hero.belongings.secondWep instanceof StormHarmonica) {
			Buff.affect(hero, Barkskin.class).set(Math.max(1, hero.HT / 8), 1);
			GLog.p(Messages.get(StormHarmonica.class, "glass"));
		}

		if (beat != null) {
			beat.finishReward();
			//SPSEXPD: 只有这一次行为是移动，才重掷新节拍格；非移动（攻击等）结算后保持当前格，
			//保证高亮格只在移动后变化。
			if (moved) beat.autoBeat(hero);
		}
		GLog.p(Messages.get(Metronome.class, "on_beat"));
	}

	/** SPSEXPD: 英雄身上是否带着能驱动节拍体系的东西——节拍器（神器位或背包里带着），
	 *  或魔音贝斯 / 闪电竖琴（StormHarmonica 是 MagicBass 的子类，getItem 走 isInstance，一并命中）。 */
	public static boolean equipped(Hero hero) {
		if (hero == null) return false;
		if (hero.belongings.artifact instanceof Metronome) return true;
		if (hero.belongings.getItem(Metronome.class) != null) return true;
		return hero.belongings.getItem(MagicBass.class) != null;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}

	/** SPSEXPD: 供外部读取当前节拍格（渲染高亮用）。
	 *  节拍器在英雄身上时，即使 buff 还没挂上也强制挂一次并立刻定拍，
	 *  保证高亮第一帧就出现，不依赖 Beat.passiveBuff 的 act 时序。 */
	public static int currentBeatCell() {
		Hero hero = Dungeon.hero;
		if (hero == null) return -1;
		if (!equipped(hero)) return -1;
		MetronomeBeat beat = hero.buff(MetronomeBeat.class);
		if (beat == null) beat = Buff.affect(hero, MetronomeBeat.class);
		if (beat.beatCell() < 0 && !beat.awaiting()) beat.autoBeat(hero);
		return beat.beatCell();
	}
}
