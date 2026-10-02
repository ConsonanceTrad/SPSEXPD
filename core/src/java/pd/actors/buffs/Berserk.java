/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.actors.buffs;

import pd.Assets;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.effects.FloatingText;
import pd.effects.SpellSprite;
import pd.items.BrokenSeal.WarriorShield;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.CharSprite;
import pd.ui.ActionIndicator;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import pd.utils.GLog;
import render.noosa.BitmapText;
import render.noosa.Image;
import render.noosa.Visual;
import render.noosa.audio.Sample;
import render.utils.math.GameMath;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Berserk extends ShieldBuff implements ActionIndicator.Action {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Berserk.class)
			.t("angered", "愤怒")
			.t("berserk", "狂暴")
			.t("exhausted", "力竭")
			.t("recovering", "恢复")
			.t("angered_desc", "狂战士受伤的严重程度会增强他的攻击。每当狂战士受到物理伤害时，他会积累怒气并获得额外伤害，被护甲格挡的伤害仍可作为怒气积累。\n\n怒气会随时间消逝，狂战士生命值越低，怒气留存时间越长。\n\n狂战士可以100%%的怒气进入狂暴，使他获得基于护甲等级与已损生命值的护盾。狂暴时，他的护盾会随时间衰减，而且其一旦耗尽则他必须稍作休息之后才能再次积累怒气。\n\n当前怒气：_%1$.0f%%_\n_+%2$.0f%%_伤害\n\n当前护甲与生命值的狂暴护盾：_%3$d_")
			.t("berserk_desc", "恐惧与疑虑已然随流出的鲜血一齐消散，此身惟余满腔怒火。在这种狂暴状态下，狂战士将无比强大，_造成50%%额外伤害并获得额外护盾_，但每回合都会损失部分护盾。\n\n当其所有护盾归零时，狂暴将会结束。\n\n狂暴过后，狂战士需要稍作休息才能再次积累怒气。\n\n剩余护盾值：%d")
			.t("recovering_desc", "内在潜力是有限的。狂战士必须充分休息才能再次释放他的怒火。\n\n恢复状态下的狂战士无法从伤痛中积累怒气。")
			.t("recovering_desc_turns", "恢复所需回合数：%d")
			.t("recovering_desc_levels", "恢复所需等级：%.2f")
			.t("rankings_desc", "暴走至死")
			.t("action_name", "狂暴")
			.t("no_seal", "你需要破损纹章才能进入狂暴！");
	}


	{
		type = buffType.POSITIVE;

		detachesAtZero = false;
		shieldUsePriority = -1; //other shielding buffs are always consumed first
	}

	private enum State{
		NORMAL, BERSERK, RECOVERING
	}
	private State state = State.NORMAL;

	private static final float LEVEL_RECOVER_START = 4f;
	private float levelRecovery;

	private static final int TURN_RECOVERY_START = 100;
	private int turnRecovery;

	public int powerLossBuffer = 0;
	private float power = 0;

	private static final String STATE = "state";
	private static final String LEVEL_RECOVERY = "levelrecovery";
	private static final String TURN_RECOVERY = "turn_recovery";
	private static final String POWER = "power";
	private static final String POWER_BUFFER = "power_buffer";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(STATE, state);
		bundle.put(POWER, power);
		bundle.put(POWER_BUFFER, powerLossBuffer);
		bundle.put(LEVEL_RECOVERY, levelRecovery);
		bundle.put(TURN_RECOVERY, turnRecovery);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);

		state = bundle.getEnum(STATE, State.class);
		power = bundle.getFloat(POWER);
		powerLossBuffer = bundle.getInt(POWER_BUFFER);
		levelRecovery = bundle.getFloat(LEVEL_RECOVERY);
		turnRecovery = bundle.getInt(TURN_RECOVERY);

		if (power >= 1f && state == State.NORMAL){
			ActionIndicator.setAction(this);
		}
	}

	@Override
	public boolean act() {
		if (state == State.BERSERK){
			if (target.shielding() > 0) {
				//lose 2.5% of shielding per turn, but no less than 1
				float dmg = (float)Math.ceil(target.shielding() * 0.025f) * HoldFast.buffDecayFactor(target);
				if (Random.Float() < dmg % 1){
					dmg++;
				}

				ShieldBuff.processDamage(target, (int)dmg, this);

				if (target.shielding() <= 0){
					state = State.RECOVERING;
					power = 0f;
					BuffIndicator.refreshHero();
					if (!target.isAlive()){
						target.die(this);
						if (!target.isAlive()) Dungeon.fail(this);
					}
				}

			} else {
				state = State.RECOVERING;
				power = 0f;
				if (!target.isAlive()){
					target.die(this);
					if (!target.isAlive()) Dungeon.fail(this);
				}

			}
		} else if (state == State.NORMAL) {
			if (powerLossBuffer > 0){
				powerLossBuffer--;
			} else {
				power -= GameMath.gate(0.1f, power, 1f) * 0.05f * Math.pow((target.HP / (float) target.HT), 2);

				if (power < 1f){
					ActionIndicator.clearAction(this);
				} else {
					ActionIndicator.refresh();
				}

				if (power <= 0) {
					detach();
				}
			}
		} else if (state == State.RECOVERING && levelRecovery == 0 && Regeneration.regenOn()){
			turnRecovery--;
			if (turnRecovery <= 0){
				turnRecovery = 0;
				state = State.NORMAL;
			}
		}
		spend(TICK);
		return true;
	}

	@Override
	public void detach() {
		super.detach();
		if (state == State.BERSERK) {
			state = State.RECOVERING;
		}
		ActionIndicator.clearAction(this);
	}

	public float enchantFactor(float chance){
		return chance + ((Math.min(1f, power) * 0.15f) * ((Hero) target).pointsInTalent(Talent.ENRAGED_CATALYST));
	}

	public float damageFactor(float dmg){
		return dmg * Math.min(1.5f, 1f + (power / 2f));
	}

	public boolean berserking(){
		if (target.HP == 0
				&& state == State.NORMAL
				&& power >= 1f
				&& ((Hero)target).hasTalent(Talent.DEATHLESS_FURY)){
			startBerserking();
			ActionIndicator.clearAction(this);
		}

		return state == State.BERSERK && target.shielding() > 0;
	}

	private void startBerserking(){
		state = State.BERSERK;
		SpellSprite.show(target, SpellSprite.BERSERK);
		Sample.INSTANCE.play( Assets.Sounds.CHALLENGE );
		GameScene.flash(0xFF0000);

		if (target.HP > 0) {
			turnRecovery = TURN_RECOVERY_START;
			levelRecovery = 0;
		} else {
			levelRecovery = LEVEL_RECOVER_START - ((Hero)target).pointsInTalent(Talent.DEATHLESS_FURY);
			turnRecovery = 0;
		}

		int shieldAmount = currentShieldBoost();
		setShield(shieldAmount);
		target.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(shieldAmount), FloatingText.SHIELDING );

		BuffIndicator.refreshHero();
	}

	public int currentShieldBoost(){
		//base multiplier scales at 1/1.5/2/2.5/3x at 100/37/20/9/0% HP
		float shieldMultiplier = 1f + 2*(float)Math.pow((1f-(target.HP/(float)target.HT)), 3);

		//Endless rage effect on shield and cooldown
		if (power > 1f){
			shieldMultiplier *= power;
			levelRecovery *= 2f - power;
			turnRecovery *= 2f - power;
		}

		int baseShield = 8;
		if (target instanceof Hero && ((Hero) target).belongings.armor() != null){
			baseShield += 2*((Hero) target).belongings.armor().buffedLvl();
		}
		return Math.round(baseShield * shieldMultiplier);
	}

	//not accounting for talents
	public int maxShieldBoost(){
		int baseShield = 8;
		if (target instanceof Hero && ((Hero) target).belongings.armor() != null){
			baseShield += 2*((Hero) target).belongings.armor().buffedLvl();
		}
		return baseShield*3;
	}
	
	public void damage(int damage){
		if (state != State.NORMAL) return;
		float maxPower = 1f + 0.1667f*((Hero)target).pointsInTalent(Talent.ENDLESS_RAGE);
		power = Math.min(maxPower, power + (damage/(float)target.HT)/4f );
		BuffIndicator.refreshHero(); //show new power immediately
		powerLossBuffer = 3; //2 turns until rage starts dropping
		if (power >= 1f){
			ActionIndicator.setAction(this);
		}
	}

	public void recover(float percent){
		if (state == State.RECOVERING && levelRecovery > 0){
			levelRecovery -= percent;
			if (levelRecovery <= 0) {
				levelRecovery = 0;
				if (turnRecovery == 0){
					state = State.NORMAL;
				}
			}
		}
	}

	@Override
	public String actionName() {
		return Messages.get(this, "action_name");
	}

	@Override
	public int actionIcon() {
		return HeroIcon.BERSERK;
	}

	@Override
	public Visual secondaryVisual() {
		BitmapText txt = new BitmapText(PixelScene.pixelFont);
		txt.text((int) (power * 100) + "%");
		txt.hardlight(CharSprite.POSITIVE);
		txt.measure();
		return txt;
	}

	@Override
	public int indicatorColor() {
		return 0x660000;
	}

	@Override
	public void doAction() {
		WarriorShield shield = target.buff(WarriorShield.class);
		if (shield != null && shield.maxShield() > 0) {
			startBerserking();
			ActionIndicator.clearAction(this);
		} else {
			GLog.w(Messages.get(this, "no_seal"));
		}
	}

	@Override
	public int icon() {
		return BuffIndicator.BERSERK;
	}
	
	@Override
	public void tintIcon(Image icon) {
		switch (state){
			case NORMAL: default:
				if (power < 1f) icon.hardlight(1f, 0.5f, 0f);
				else            icon.hardlight(1f, 0f, 0f);
				break;
			case BERSERK:
				icon.hardlight(1f, 0f, 0f);
				break;
			case RECOVERING:
				icon.hardlight(0, 0, 1f);
				break;
		}
	}
	
	@Override
	public float iconFadePercent() {
		switch (state){
			case NORMAL: default:
				float maxPower = 1f + 0.1667f*((Hero)target).pointsInTalent(Talent.ENDLESS_RAGE);
				return (maxPower - power)/maxPower;
			case BERSERK:
				return 1f - shielding() / (float)maxShieldBoost();
			case RECOVERING:
				if (levelRecovery > 0) {
					return levelRecovery/(LEVEL_RECOVER_START-Dungeon.hero.pointsInTalent(Talent.DEATHLESS_FURY));
				} else {
					return turnRecovery/(float)TURN_RECOVERY_START;
				}
		}
	}

	public String iconTextDisplay(){
		switch (state){
			case NORMAL: default:
				return (int)(power*100) + "%";
			case BERSERK:
				return Integer.toString(shielding());
			case RECOVERING:
				if (levelRecovery > 0) {
					return Messages.decimalFormat("#.##", levelRecovery);
				} else {
					return Integer.toString(turnRecovery);
				}
		}
	}

	@Override
	public String name() {
		switch (state){
			case NORMAL: default:
				return Messages.get(this, "angered");
			case BERSERK:
				return Messages.get(this, "berserk");
			case RECOVERING:
				return Messages.get(this, "recovering");
		}
	}

	@Override
	public String desc() {
		float dispDamage = ((int)damageFactor(10000) / 100f) - 100f;
		switch (state){
			case NORMAL: default:
				return Messages.get(this, "angered_desc", Math.floor(power * 100f), dispDamage, currentShieldBoost());
			case BERSERK:
				return Messages.get(this, "berserk_desc", shielding());
			case RECOVERING:
				if (levelRecovery > 0){
					return Messages.get(this, "recovering_desc") + "\n\n" + Messages.get(this, "recovering_desc_levels", levelRecovery);
				} else {
					return Messages.get(this, "recovering_desc") + "\n\n" + Messages.get(this, "recovering_desc_turns", turnRecovery);
				}
		}
		
	}
}
