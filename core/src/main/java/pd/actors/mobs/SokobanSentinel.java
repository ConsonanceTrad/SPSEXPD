/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Poison;
import pd.items.Generator;
import pd.items.weapon.Weapon;
import pd.items.weapon.melee.MeleeWeapon;
import pd.messages.Messages;
import pd.sprites.SentinelSprite;
import render.utils.Bundle;
import render.utils.Random;

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
