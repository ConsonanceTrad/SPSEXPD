/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SentinelSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class SokobanSentinel extends Mob {
	private Weapon weapon;

	{
		spriteClass = SentinelSprite.class;
		baseSpeed = 0.5f;
		HP = HT = 500;
		defenseSkill = 40;
		EXP = 18;
		maxLvl = -1;
		state = PASSIVE;
		properties.add(Property.INORGANIC);
		properties.add(Property.MECH);
		resistances.add(ToxicGas.class);
		resistances.add(Poison.class);
	}

	public SokobanSentinel() {
		do {
			weapon = (Weapon) Generator.random(Generator.Category.OLDWEAPON);
		} while (!(weapon instanceof MeleeWeapon) || ((MeleeWeapon) weapon).tier < 3);
		weapon.identify();
		weapon.enchant(Weapon.Enchantment.random());
		weapon.upgrade(5);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(weapon.min(weapon.buffedLvl()), weapon.max(weapon.buffedLvl()));
	}

	@Override
	public int attackSkill(Char target) {
		return 40;
	}

	@Override
	public float attackDelay() {
		return weapon.delayFactor(this);
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, Dungeon.legacyDepth());
	}

	@Override
	public void damage(int damage, Object source) {
		if (state == PASSIVE) state = HUNTING;
		super.damage(damage, source);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		return weapon.proc(this, enemy, damage);
	}

	@Override
	public void beckon(int cell) {
	}

	@Override
	public String description() {
		return Messages.get(this, "desc", weapon.name());
	}

	private static final String WEAPON = "weapon";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(WEAPON, weapon);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		weapon = (Weapon) bundle.get(WEAPON);
	}
}
