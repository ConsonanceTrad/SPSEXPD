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
			.t("name", "战舞姿态")
			.t("state_charge", "冲锋姿态")
			.t("state_defend", "防御姿态")
			.t("charge_layer", "战舞蓄势：攻速加成叠加至 %d 层。")
			.t("desc", "以战舞驾驭六刃的架势。\n\n_冲锋姿态：_移动速度降低到原先的四分之三，每次命中使攻速提高，连续 2 回合未命中后层数每回合衰减 1 层；命中两格或更远的敌人时，你会顺势朝对方冲进一格。\n_防御姿态：_攻击速度减半，但受到的伤害减少 50%%。\n\n当前姿态：%1$s");
	}

	public static final int MAX_LAYERS = 5;
	/** 未命中后的宽限回合数：这之后每回合衰减一层攻速叠加。 */
	public static final int GRACE_TURNS = 2;
	public static final int DEFEND_TURNS = 5;
	public static final int DEFEND_COOLDOWN = 10;
	public static final float ATTACK_STEP = 0.2f;
	public static final float DEFEND_ATTACK = 0.5f;
	/** 战舞的移速代价恒定：无论何种姿态，都降低到原先的四分之三。 */
	public static final float CHARGE_SPEED = 0.75f;
	public static final float DEFEND_DAMAGE_FACTOR = 0.5f;

	private static final String LAYERS = "layers";
	private static final String IDLE = "idle";
	private static final String DEFENDING = "defending";
	private static final String DEFEND_LEFT = "defend_left";
	private static final String DEFEND_CD = "defend_cd";

	private int layers;
	private int idleTurns;
	private boolean defending;
	private int defendTurns;
	private int defendCooldown;

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
		if (defending) {
			defendTurns--;
			if (defendTurns <= 0) {
				defending = false;
				defendCooldown = DEFEND_COOLDOWN;
			}
		} else if (defendCooldown > 0) {
			defendCooldown--;
		}
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
		if (!defending && layers < MAX_LAYERS) layers++;
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

	public int defendTurnsLeft() {
		return defending ? defendTurns : 0;
	}

	public int defendCooldownLeft() {
		return defending ? 0 : Math.max(0, defendCooldown);
	}

	public boolean canDefend() {
		return !defending && defendCooldown <= 0;
	}

	public boolean enterDefend() {
		if (!canDefend()) return false;
		defending = true;
		defendTurns = DEFEND_TURNS;
		return true;
	}

	/** 战舞的移速代价恒定：不再随击杀改善，冲锋姿态与防御姿态都是四分之三。 */
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
		return BuffIndicator.FURY;
	}

	@Override
	public String iconTextDisplay() {
		return defending ? Integer.toString(defendTurns) : Integer.toString(layers);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", Messages.get(this, defending ? "state_defend" : "state_charge"));
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LAYERS, layers);
		bundle.put(IDLE, idleTurns);
		bundle.put(DEFENDING, defending);
		bundle.put(DEFEND_LEFT, defendTurns);
		bundle.put(DEFEND_CD, defendCooldown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		layers = bundle.getInt(LAYERS);
		idleTurns = bundle.getInt(IDLE);
		defending = bundle.getBoolean(DEFENDING);
		defendTurns = bundle.getInt(DEFEND_LEFT);
		defendCooldown = bundle.getInt(DEFEND_CD);
	}
}
