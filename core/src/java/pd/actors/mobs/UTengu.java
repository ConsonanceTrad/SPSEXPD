/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.wands.WandOfBlastWave;
import pd.items.weapon.melee.special.TenguSword;
import pd.levels.BossRushLevel;
import pd.mechanics.Ballistica;
import pd.sprites.UTenguSprite;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

/** Young Tengu, retaining the original ranged/melee phases and periodic jump. */
public class UTengu extends BossRushBoss {
	private static final int JUMP_DELAY = 10;
	private int timeToJump = JUMP_DELAY;

	{
		spriteClass = UTenguSprite.class;
		baseSpeed = 1f;
		loot = new TenguSword();
		lootChance = 0.5f;
		properties.add(Property.HUMAN);
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return Dungeon.level.distance(pos, enemy.pos) <= (breaks < 2 ? 4 : 1);
	}

	@Override
	protected boolean doAttack(Char enemy) {
		timeToJump--;
		if (timeToJump <= 0 && Dungeon.level.adjacent(pos, enemy.pos)) {
			jump(enemy);
			return true;
		}
		return super.doAttack(enemy);
	}

	private void jump(Char enemy) {
		timeToJump = JUMP_DELAY;
		int destination = -1;
		for (int tries = 0; tries < 200; tries++) {
			int cell = Random.Int(Dungeon.level.length());
			if (Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[cell]
					&& Dungeon.level.passable[cell] && Actor.findChar(cell) == null
					&& !Dungeon.level.adjacent(cell, enemy.pos)) {
				destination = cell;
				break;
			}
		}
		if (destination < 0 && Dungeon.level instanceof BossRushLevel) {
			destination = ((BossRushLevel) Dungeon.level).safeSpawnCell(pos);
		}
		if (destination >= 0 && destination != pos) {
			if (sprite != null) sprite.move(pos, destination);
			move(destination);
			CellEmitter.get(destination).burst(Speck.factory(Speck.WOOL), 6);
		}
		spend(1f / speed());
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Dungeon.level.adjacent(pos, enemy.pos) && Random.Int(5) >= 3) {
			int opposite = enemy.pos + (enemy.pos - pos);
			Ballistica trajectory = new Ballistica(enemy.pos, opposite, Ballistica.MAGIC_BOLT);
			WandOfBlastWave.throwChar(enemy, trajectory, 1, false, false, this);
		}
		return damage;
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (breaks == 2 && enemy != null) {
			int reflected = Random.IntRange(0, 20);
			if (reflected > 0 || Random.Int(3) == 0) enemy.damage(reflected, this);
		}
		return super.defenseProc(enemy, damage);
	}

	@Override protected Class<? extends BossRushBoss> nextBoss() { return UDM300.class; }

	private static final String JUMP = "jump";
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(JUMP, timeToJump); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); timeToJump = bundle.contains(JUMP) ? bundle.getInt(JUMP) : JUMP_DELAY; }
}
