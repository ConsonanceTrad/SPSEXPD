/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.actors.Char;
import pd.sprites.SheepSprite;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 绵羊披风「闪烁」时留在原地的替身（移植自 Darkest PD 0.7.2 的 CloakOfSheep 绵羊）。
 *
 * <p>中立单位，不会攻击，但会被敌人视作目标，用来吸引火力；寿命耗尽后自行消失。</p>
 */
public class DecoySheep extends Mob {
	private static final String LIFESPAN = "lifespan";

	/** 剩余寿命（回合）。 */
	private float lifespan = 10f;

	{
		spriteClass = SheepSprite.class;
		alignment = Alignment.NEUTRAL;
		HP = HT = 15;
		defenseSkill = 5;
		EXP = 0;
		state = WANDERING;
	}

	public void initialize(float lifespan) {
		this.lifespan = lifespan;
	}

	public float lifespan() {
		return lifespan;
	}

	@Override
	public boolean act() {
		if (lifespan > 0) lifespan -= 1f;
		if (lifespan <= 0) {
			HP = 0;
			destroy();
			if (sprite != null) sprite.die();
			return true;
		}
		spend(TICK);
		return true;
	}

	/** 替身只会站着咩咩叫，既不会出手也不会被精神类效果影响。 */
	@Override
	public boolean canAttack(Char enemy) {
		return false;
	}

	@Override
	public int attackSkill(Char target) {
		return 0;
	}

	@Override
	public int defenseSkill(Char enemy) {
		return defenseSkill;
	}

	@Override
	public int damageRoll() {
		return 0;
	}

	@Override
	public int drRoll() {
		return 0;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LIFESPAN, lifespan);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		lifespan = bundle.getFloat(LIFESPAN);
	}
}
