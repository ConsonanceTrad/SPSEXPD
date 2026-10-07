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
 * <p>冲锋姿态：移动速度被七刃拖慢（随击杀进度减轻），每次挥击命中都会叠加攻速；
 * 防御姿态：移动与攻击速度减半，但受到的伤害减少 50%。</p>
 */
public class TrinityStance extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(TrinityStance.class)
			.t("name", "战舞姿态")
			.t("state_charge", "冲锋姿态")
			.t("state_defend", "防御姿态")
			.t("desc", "以战舞驾驭七刃的架势。\n\n_冲锋姿态：_移动速度降低，每次命中使攻速提高，连续 5 回合未命中则加成清零。\n_防御姿态：_移动与攻击速度减半，但受到的伤害减少 50%%。\n\n无论何种姿态，命中两格开外的敌人时都会顺势向对方冲进一格。\n\n当前姿态：%1$s");
	}

	public static final int MAX_LAYERS = 5;
	/** 连续这么多回合没有命中就清空攻速叠加。 */
	public static final int IDLE_RESET = 5;
	public static final int DEFEND_TURNS = 5;
	public static final int DEFEND_COOLDOWN = 10;
	public static final float ATTACK_STEP = 0.2f;
	public static final float DEFEND_ATTACK = 0.5f;
	public static final float DEFEND_SPEED = 0.5f;
	public static final float CHARGE_SPEED = 0.8f;
	public static final float CHARGE_SPEED_TRAINED = 0.9f;
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
		if (idleTurns >= IDLE_RESET && layers > 0) layers = 0;
		weapon.updateQuickslot();
		spend(TICK);
		return true;
	}

	/** 整次挥击（七片飞刃）只记一次。 */
	public void onAttack() {
		idleTurns = 0;
		if (!defending && layers < MAX_LAYERS) layers++;
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

	/** 先锋之刃施放后强制回到冲锋姿态。 */
	public void leaveDefend() {
		if (!defending) return;
		defending = false;
		defendTurns = 0;
		defendCooldown = DEFEND_COOLDOWN;
	}

	public float speedMultiplier(TrinityForce weapon) {
		if (defending) return DEFEND_SPEED;
		return chargeSpeedMultiplier(weapon == null ? 0 : weapon.kills());
	}

	/** 冲锋姿态的移速惩罚随累计击杀减轻，150 次后完全取消。 */
	public static float chargeSpeedMultiplier(int kills) {
		if (kills >= TrinityForce.VANGUARD_KILLS) return 1f;
		if (kills >= TrinityForce.TRAINED_KILLS) return CHARGE_SPEED_TRAINED;
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
