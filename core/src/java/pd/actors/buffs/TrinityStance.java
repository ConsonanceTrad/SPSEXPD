/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.KindOfWeapon;
import pd.items.equipment.weapon.melee.special.TrinityForce;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 三相之力的「战舞」姿态。
 *
 * <p>冲锋姿态：移动速度被六刃拖慢（恒定为原先的四分之三），每次挥击命中都会叠加攻速；
 * 防御姿态：移动与攻击速度减半，但受到的伤害减少 50%。</p>
 */
public class TrinityStance extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(TrinityStance.class)
			.t("name", "距破之舞")
			.t("state_charge", "冲锋姿态")
			.t("state_defend", "防御姿态")
			.t("desc", "以战舞控制三相之刃防护。\n\n期间获得 50% 的伤害减免，但无法移动且攻速减半。");
	}

	public static final int MAX_LAYERS = 5;
	/** 未命中后的宽限回合数：这之后每回合衰减一层攻速叠加。 */
	public static final int GRACE_TURNS = 2;
	public static final float ATTACK_STEP = 0.2f;
	public static final float DEFEND_ATTACK = 0.5f;
	/** 战舞的移速代价恒定：无论何种姿态，都降低到原先的四分之三。 */
	public static final float CHARGE_SPEED = 0.75f;
	public static final float DEFEND_DAMAGE_FACTOR = 0.5f;

	private static final String LAYERS = "layers";
	private static final String IDLE = "idle";
	private static final String DEFENDING = "defending";

	private int layers;
	private int idleTurns;
	private boolean defending;

	{
		type = buffType.POSITIVE;
		announced = true;
		revivePersists = true;
	}

	public static TrinityStance of(Char ch) {
		return ch == null ? null : ch.buff(TrinityStance.class);
	}

	/** 当前装备着的三相之力；卸下后姿态会自动解除。 */
	public static TrinityForce weaponOf(Char ch) {
		if (!(ch instanceof Hero)) return null;
		Hero hero = (Hero) ch;
		KindOfWeapon main = hero.belongings.weapon;
		if (main instanceof TrinityForce) return (TrinityForce) main;
		KindOfWeapon second = hero.belongings.secondWep;
		if (second instanceof TrinityForce) return (TrinityForce) second;
		return null;
	}

	@Override
	public boolean act() {
		TrinityForce weapon = weaponOf(target);
		if (weapon == null) {
			detach();
			return true;
		}
		//SPSEXPD: 每回合同步一次，读档或其它途径改了姿态时也能纠正
		if (target != null) target.rooted = defending;
		idleTurns++;
		//SPSEXPD: 未命中先宽限 GRACE_TURNS 回合，之后逐层衰减（不再一次性清零）
		if (idleTurns > GRACE_TURNS && layers > 0) layers--;
		weapon.updateQuickslot();
		spend(TICK);
		return true;
	}

	/** 整次挥击（三次结算）只记一次。 */
	public void onAttack() {
		idleTurns = 0;
		//SPSEXPD: 防御姿态下不叠蓄势（层数在切进防御姿态时就已清零）
		if (defending) return;
		if (layers < MAX_LAYERS) layers++;
		if (target != null && target.buff(TrinityCharge.class) == null) {
			//SPSEXPD: 用一个独立 buff 显示当前层数（它自己跟随层数存活，这里只保证它存在）
			Buff.affect(target, TrinityCharge.class);
		}
	}

	public int layers() {
		return layers;
	}

	public boolean defending() {
		return defending;
	}

	/** SPSEXPD: 切换冲锋/防御姿态——随时可切，代价是切换这个动作本身花一个回合；切入防御姿态会打断战舞（蓄势层数清零）。 */
	public void toggleStance() {
		defending = !defending;
		if (defending) {
			layers = 0;
			//SPSEXPD: 蓄势的显示 buff 立刻消散，而不是等它下个回合自己发现层数为 0
			if (target != null) Buff.detach(target, TrinityCharge.class);
		}
		//SPSEXPD: 防御姿态无法移动（与束缚 Roots 一样走 Char.rooted，拦的是移动/位移而不是攻击）
		if (target != null) target.rooted = defending;
	}

	@Override
	public void detach() {
		//SPSEXPD: 只收回本姿态设的 rooted，别把束缚留下的那一个一起清掉
		if (target != null && defending) target.rooted = false;
		super.detach();
	}

	/** 战舞的移速代价恒定：无论何种姿态都降低到原先的四分之三。 */
	public float speedMultiplier() {
		return CHARGE_SPEED;
	}

	public float attackSpeedMultiplier(float base) {
		if (defending) return base * DEFEND_ATTACK;
		return base + ATTACK_STEP * layers;
	}

	/** 防御姿态的乘算减伤，与其它减伤叠乘。 */
	public int reduceDamage(int dmg) {
		if (!defending || dmg <= 0) return dmg;
		return (int) Math.ceil(dmg * DEFEND_DAMAGE_FACTOR);
	}

	@Override
	public int icon() {
		//SPSEXPD: 冲锋姿态是常态，不占状态栏；只有防御姿态才显示（图标沿用植物护甲）
		return defending ? BuffIndicator.ARMOR : BuffIndicator.NONE;
	}

	@Override
	public String iconTextDisplay() {
		//防御姿态没有可倒数的时间，图标上不需要数字
		return "";
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LAYERS, layers);
		bundle.put(IDLE, idleTurns);
		bundle.put(DEFENDING, defending);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		layers = bundle.getInt(LAYERS);
		idleTurns = bundle.getInt(IDLE);
		defending = bundle.getBoolean(DEFENDING);
	}
}
