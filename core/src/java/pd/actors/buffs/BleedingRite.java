/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.hero.Hero;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

/**
 * SPSEXPD: 小千录 / 巴虺断角「开启」后的状态。
 *
 * <p>每回合让使用者_流血_（出血量逐回合累加，且先回血再扣血，净收益为正），
 * 同时把_除流血外的其它负面状态挂起_（摘下并记下剩余时间，关闭时原样挂回）。</p>
 */
public class BleedingRite extends Buff {

	private static final String BLEED = "bleed";
	private static final String GROWTH = "growth";
	private static final String HEAL = "heal";
	private static final String SPEED = "speed";
	private static final String DAMAGE = "damage";
	private static final String SUSPENDED = "suspended";
	private static final String LEFT = "left";

	/** 当前每回合的出血量。 */
	private float bleed = 1f;
	private float growth = 0.5f;
	private float healFactor = 1.5f;
	private float speedBonus = 0f;
	private int damageBonus = 0;

	/** 被挂起的负面状态：类名 + 剩余时间。 */
	private final ArrayList<String> suspended = new ArrayList<>();
	private final ArrayList<Float> suspendedLeft = new ArrayList<>();

	{
		type = buffType.POSITIVE;
	}

	public BleedingRite setup(float bleed, float growth, float healFactor, float speedBonus, int damageBonus) {
		this.bleed = Math.max(this.bleed, bleed);
		this.growth = growth;
		this.healFactor = healFactor;
		this.speedBonus = speedBonus;
		this.damageBonus = damageBonus;
		return this;
	}

	public float bleedPerTurn() {
		return bleed;
	}

	public float speedMultiplier() {
		return 1f + speedBonus;
	}

	public int damageBonus() {
		return damageBonus;
	}

	@Override
	public boolean act() {
		if (target instanceof Hero) {
			Hero hero = (Hero) target;
			int dmg = Math.max(1, Math.round(bleed));
			int heal = Math.round(dmg * healFactor);
			//SPSEXPD: 仪式之血不会杀死你——先回血再流血，净收益为正
			hero.HP = Math.min(hero.HT, hero.HP + heal);
			hero.damage(dmg, this);
			bleed += growth;
			suspendNegatives(hero);
		}
		spend(TICK);
		return true;
	}

	/** SPSEXPD: 把除流血外的负面状态摘下来（不生效、也不消去），关闭时挂回。 */
	private void suspendNegatives(Hero hero) {
		ArrayList<Buff> negatives = new ArrayList<>();
		for (Buff b : hero.buffs()) {
			if (b == this) continue;
			if (b.type == buffType.NEGATIVE) negatives.add(b);
		}
		for (Buff b : negatives) {
			suspended.add(b.getClass().getName());
			suspendedLeft.add(Math.max(0.1f, b.visualcooldown()));
			b.detach();
		}
	}

	/** SPSEXPD: 关闭时把挂起的负面状态按原剩余时间重新挂上。 */
	public void restoreSuspended(Hero hero) {
		for (int i = 0; i < suspended.size(); i++) {
			try {
				Class<?> c = Class.forName(suspended.get(i));
				if (!Buff.class.isAssignableFrom(c)) continue;
				float left = Math.max(0.1f, suspendedLeft.get(i));
				//SPSEXPD: 只有 FlavourBuff 能带时长重新挂上，其它按默认时长
				if (FlavourBuff.class.isAssignableFrom(c)) {
					@SuppressWarnings("unchecked")
					Class<FlavourBuff> fc = (Class<FlavourBuff>) c;
					Buff.affect(hero, fc, left);
				} else {
					@SuppressWarnings("unchecked")
					Class<Buff> bc = (Class<Buff>) c;
					Buff.affect(hero, bc);
				}
			} catch (Exception ignored) {
				//SPSEXPD: 类已不存在（旧档）时直接丢弃这条挂起记录
			}
		}
		suspended.clear();
		suspendedLeft.clear();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BLEED, bleed);
		bundle.put(GROWTH, growth);
		bundle.put(HEAL, healFactor);
		bundle.put(SPEED, speedBonus);
		bundle.put(DAMAGE, damageBonus);
		StringBuilder types = new StringBuilder();
		for (String s : suspended) types.append(s).append(';');
		StringBuilder lefts = new StringBuilder();
		for (float f : suspendedLeft) lefts.append(f).append(';');
		bundle.put(SUSPENDED, types.toString());
		bundle.put(LEFT, lefts.toString());
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		bleed = bundle.getFloat(BLEED);
		growth = bundle.getFloat(GROWTH);
		healFactor = bundle.getFloat(HEAL);
		speedBonus = bundle.getFloat(SPEED);
		damageBonus = bundle.getInt(DAMAGE);
		suspended.clear();
		suspendedLeft.clear();
		for (String s : bundle.getString(SUSPENDED).split(";")) {
			if (!s.isEmpty()) suspended.add(s);
		}
		for (String s : bundle.getString(LEFT).split(";")) {
			if (!s.isEmpty()) suspendedLeft.add(Float.parseFloat(s));
		}
	}
}
