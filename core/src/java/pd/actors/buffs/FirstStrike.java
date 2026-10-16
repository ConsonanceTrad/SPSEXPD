/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

/**
 * SPSEXPD: 「先攻」标记——挂在你_先手命中过_的敌人身上。
 *
 * <p>当你带着神木圆盾 / 队长之盾时，命中敌人会给它留下这个标记；
 * 该敌人_第一次对你造成物理伤害_时会触发盾牌的吸收（队长之盾还会反射），标记随之消失。</p>
 */
public class FirstStrike extends Buff {

	{
		type = buffType.NEGATIVE;
	}

	//SPSEXPD: 纯标记 buff，不需要每回合做任何事
	@Override
	public boolean act() {
		spend(TICK);
		return true;
	}
}
