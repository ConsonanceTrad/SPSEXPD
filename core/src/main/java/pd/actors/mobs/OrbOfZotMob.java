/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Terror;
import pd.effects.CellEmitter;
import pd.effects.particles.PurpleParticle;
import pd.items.Heap;
import pd.items.OrbOfZot;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.sprites.OrbOfZotSprite;
import render.utils.Random;
import render.noosa.particles.Emitter;

import java.util.ArrayList;

/** Immobile allied beam turret produced by a fully charged Orb of Zot. */
public class OrbOfZotMob extends Mob {

	private static final float SPAWN_DELAY = 1f;
	private Ballistica beam;

	{
		spriteClass = OrbOfZotSprite.class;
		alignment = Alignment.ALLY;
		state = HUNTING;
		HP = HT = 500;
		defenseSkill = 35;
		EXP = 0;
		properties.add(Property.MECH);
		properties.add(Property.IMMOVABLE);
		immunities.add(Terror.class);
		immunities.add(ToxicGas.class);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(100, 200); }
	@Override public int attackSkill(Char target) { return 70 + Math.max(0, Dungeon.legacyDepth()); }
	@Override public int drRoll() { return 0; }
	@Override public void beckon(int cell) { }
	@Override protected boolean getCloser(int target) { return false; }

	@Override
	protected Char chooseEnemy() {
		if (Dungeon.level == null) return null;
		if (enemy != null && enemy.isAlive() && enemy.alignment == Alignment.ENEMY
				&& visible(enemy.pos)) return enemy;

		ArrayList<Mob> enemies = new ArrayList<>();
		for (Mob mob : Dungeon.level.mobs) {
			if (mob != this && mob.isAlive() && mob.alignment == Alignment.ENEMY
					&& mob.invisible <= 0 && visible(mob.pos)) enemies.add(mob);
		}
		enemy = enemies.isEmpty() ? null : Random.element(enemies);
		return enemy;
	}

	private boolean visible(int cell) {
		return cell >= 0 && Dungeon.level != null && cell < Dungeon.level.length()
				&& fieldOfView != null && fieldOfView.length == Dungeon.level.length()
				&& fieldOfView[cell];
	}

	@Override
	protected boolean canAttack(Char target) {
		if (target == null || target.alignment != Alignment.ENEMY || Dungeon.level == null) return false;
		beam = new Ballistica(pos, target.pos, Ballistica.STOP_SOLID);
		return beam.subPath(1, beam.dist).contains(target.pos);
	}

	@Override
	protected boolean doAttack(Char target) {
		if (target == null || target.alignment != Alignment.ENEMY) {
			spend(TICK);
			return true;
		}
		beam = new Ballistica(pos, target.pos, Ballistica.STOP_SOLID);
		if (sprite != null && (sprite.visible || target.sprite != null && target.sprite.visible)) {
			sprite.attack(beam.collisionPos);
			return false;
		}
		fireBeam();
		Invisibility.dispel(this);
		spend(attackDelay());
		return true;
	}

	@Override
	public void onAttackComplete() {
		fireBeam();
		Invisibility.dispel(this);
		spend(attackDelay());
		next();
	}

	/** Applies one legacy laser pulse; exposed for deterministic no-graphics checks. */
	public void fireBeamAt(Char target) {
		if (target == null || target.alignment != Alignment.ENEMY || Dungeon.level == null) return;
		enemy = target;
		beam = new Ballistica(pos, target.pos, Ballistica.STOP_SOLID);
		fireBeam();
	}

	private void fireBeam() {
		if (beam == null || Dungeon.level == null) return;
		for (int cell : beam.subPath(1, beam.dist)) {
			Char target = Actor.findChar(cell);
			if (target == null || target == this || target.alignment != Alignment.ENEMY) continue;
			if (hit(this, target, true)) {
				target.damage(Random.NormalIntRange(100, 200), this);
				damage(Random.NormalIntRange(10, 20), this);
				if (target.sprite != null) target.sprite.flash();
				if (target.sprite != null && Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[cell]) {
					Emitter emitter = CellEmitter.get(cell);
					if (emitter != null) emitter.burst(PurpleParticle.BURST, Random.IntRange(1, 2));
				}
				if (!isAlive()) break;
			} else if (target.sprite != null) {
				target.sprite.showStatus(CharSprite.NEUTRAL, target.defenseVerb());
			}
		}
	}

	@Override
	public void die(Object cause) {
		int deathPos = pos;
		if (sprite != null) yell(Messages.get(this, "die"));
		// Runtime mobs always have a sprite. The fallback keeps save repair and
		// headless verification from dereferencing an absent rendering object.
		if (sprite == null || Dungeon.hero == null) destroy();
		else super.die(cause);
		if (Dungeon.level != null && Dungeon.level.insideMap(deathPos)) {
			Heap heap = Dungeon.level.drop(new OrbOfZot(), deathPos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}
	}

	public static OrbOfZotMob spawnAt(int cell) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(cell)
				|| !Dungeon.level.passable[cell] || Dungeon.level.pit[cell]
				|| Actor.findChar(cell) != null) return null;
		OrbOfZotMob orb = new OrbOfZotMob();
		orb.pos = cell;
		orb.state = orb.HUNTING;
		GameScene.add(orb, SPAWN_DELAY);
		return orb;
	}

	@Override public float spawningWeight() { return 0f; }
}
