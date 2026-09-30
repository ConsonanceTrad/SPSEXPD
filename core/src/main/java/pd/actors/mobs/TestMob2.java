/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HiddenShadow;
import pd.actors.buffs.Locked;
import pd.mechanics.Ballistica;
import pd.items.bags.HeartOfScarecrow;
import pd.sprites.ScarecrowSprite;
import com.watabou.utils.Bundle;

/** The original clockwork scarecrow, dormant until struck and capable of ranged attacks. */
public class TestMob2 extends Mob {

	private static final String SKILL = "skill";
	private boolean skill;

	{
		spriteClass = ScarecrowSprite.class;
		HP = HT = 100000;
		defenseSkill = 0;
		state = PASSIVE;
		properties.add(Property.INORGANIC);
		properties.add(Property.MECH);
	}

	@Override
	public void damage(int damage, Object source) {
		if (state == PASSIVE) state = HUNTING;
		super.damage(damage, source);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		Buff.prolong(this, HiddenShadow.class, 2f);
		return damage;
	}

	@Override
	public int damageRoll() {
		return 5;
	}

	@Override
	public int attackSkill(Char target) {
		return 100;
	}

	@Override
	public int drRoll() {
		return 0;
	}

	@Override
	protected boolean canAttack(Char enemy) {
		if (buff(Locked.class) != null) return Dungeon.level.adjacent(pos, enemy.pos);
		return new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		Dungeon.level.drop(new HeartOfScarecrow(), pos).sprite.drop();
		dropLegacyDew(pos);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SKILL, skill);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		skill = bundle.getBoolean(SKILL);
	}
}
