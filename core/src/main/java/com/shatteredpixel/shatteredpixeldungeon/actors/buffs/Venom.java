package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class Venom extends Poison {
	private static final String DAMAGE = "damage";

	private int damage = 1;

	public void set(float duration, int damage) {
		set(duration);
		this.damage = Math.max(this.damage, damage);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DAMAGE, damage);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		damage = Math.max(1, bundle.getInt(DAMAGE));
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0.3f, 0.8f, 0.2f);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns(left), damage);
	}

	@Override
	public boolean act() {
		if (target.isAlive()) {
			target.damage(damage, this);
			damage = Math.min(damage + 1, (Dungeon.legacyDepth() + 1) / 2 + 1);
			spend(TICK + 0.1f);
			if ((left -= TICK) <= 0) detach();
			target.needsIncomingDOTUpdate = true;
		} else {
			detach();
		}
		return true;
	}

	@Override
	public int totalIncomingDMG() {
		int total = 0;
		int nextDamage = damage;
		int cap = (Dungeon.legacyDepth() + 1) / 2 + 1;
		for (int turns = (int) Math.ceil(left); turns > 0; turns--) {
			total += nextDamage;
			nextDamage = Math.min(nextDamage + 1, cap);
		}
		return total;
	}
}
