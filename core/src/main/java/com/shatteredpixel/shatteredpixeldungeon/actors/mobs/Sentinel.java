/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SentinelSprite;
import com.watabou.utils.Random;

/** The passive enchanted-weapon guardian from the legacy wisdom trial. */
public class Sentinel extends Statue {
	private static final int LEGACY_DEPTH = 33;

	public Sentinel() {
		super();
		spriteClass = SentinelSprite.class;
		properties.add(Property.MECH);
		HP = HT = 15 + LEGACY_DEPTH * 8;
		defenseSkill = 4 + LEGACY_DEPTH * 2;
		EXP = 18;
		state = PASSIVE;
		createWeapon(false);
		weapon.identify();
		weapon.enchant(Weapon.Enchantment.random());
		for (int i = 0; i < 3; i++) weapon.upgrade(true);
	}

	@Override public int attackSkill(Char target) {
		return (int)((9 + LEGACY_DEPTH) * weapon.accuracyFactor(this, target));
	}

	@Override public int drRoll() {
		return Random.NormalIntRange(0, LEGACY_DEPTH);
	}

	@Override public void beckon(int cell) { }

	@Override public void die(Object cause) {
		dropLegacyDew(pos);
		super.die(cause);
	}

	@Override public String description() {
		return Messages.get(this, "desc", weapon.name());
	}
}
