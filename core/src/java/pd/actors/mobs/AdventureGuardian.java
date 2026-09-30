/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Special Surprise adventure encounter adapted for the Shattered 4.0 ruleset.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Chill;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Haste;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Slow;
import pd.actors.buffs.Vertigo;
import pd.actors.buffs.Vulnerable;
import pd.actors.buffs.Weakness;
import pd.items.quest.AdventureJournal;
import pd.items.scrolls.ScrollOfTeleportation;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.BatSprite;
import pd.sprites.DM300Sprite;
import pd.sprites.EyeSprite;
import pd.sprites.GolemSprite;
import pd.sprites.GuardSprite;
import pd.sprites.MonkSprite;
import pd.sprites.ScorpioSprite;
import pd.sprites.SkeletonSprite;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

public class AdventureGuardian extends Mob {

	private int destination;
	private boolean guardian;
	private int phase;
	private int rushWave;

	{
		EXP = 0;
		maxLvl = -1;
		configure(AdventureJournal.destinationForBranch(Dungeon.branch), false);
	}

	public AdventureGuardian configure(int destination, boolean guardian) {
		this.destination = Math.max(0, destination);
		this.guardian = guardian;
		this.phase = 0;
		this.rushWave = 0;
		int stage = Math.max(0, (AdventureJournal.anchorDepth(this.destination) + 1) / 5 - 1);
		HT = guardian ? 24 + 20 * stage : 9 + 10 * stage;
		if (this.destination >= 21 && guardian) HT = Math.round(HT * 1.25f);
		if (this.destination == 21 && guardian) HT = 44 + 6 * stage;
		HP = HT;
		defenseSkill = (guardian ? 8 : 5) + 5 * stage;
		if (guardian) properties.add(Property.MINIBOSS);
		else properties.remove(Property.MINIBOSS);
		setSprite();
		return this;
	}

	private void setSprite() {
		switch (destination) {
			case 6: spriteClass = BatSprite.class; break;
			case 7: case 19: spriteClass = DM300Sprite.class; break;
			case 9: spriteClass = EyeSprite.class; break;
			case 10: case 18: spriteClass = MonkSprite.class; break;
			case 11: spriteClass = SkeletonSprite.class; break;
			case 12: spriteClass = GuardSprite.class; break;
			case 13: spriteClass = ScorpioSprite.class; break;
			case 14: spriteClass = GolemSprite.class; break;
			case 15: spriteClass = GuardSprite.class; break;
			case 16: case 20: case 22: case 23: case 24: spriteClass = EyeSprite.class; break;
			case 17: case 21: spriteClass = GolemSprite.class; break;
			default: spriteClass = GuardSprite.class;
		}
	}

	@Override
	public String name() {
		if (guardian && destination == 21) {
			return Messages.get(this, "boss_name_21_wave_" + rushWave);
		}
		if (guardian && destination >= 6) {
			return Messages.get(this, "boss_name_" + destination);
		}
		return Messages.get(this, guardian ? "guardian_name" : "echo_name",
				AdventureJournal.destinationName(destination));
	}

	@Override
	public String description() {
		if (guardian && destination >= 6) {
			return Messages.get(this, "boss_desc", AdventureJournal.destinationName(destination),
					Messages.get(this, "tactic_" + destination));
		}
		return Messages.get(this, guardian ? "guardian_desc" : "echo_desc",
				AdventureJournal.destinationName(destination));
	}

	private int stage() {
		return Math.max(0, (AdventureJournal.anchorDepth(destination) + 1) / 5 - 1);
	}

	@Override
	public int damageRoll() {
		int stage = stage();
		int min = (guardian ? 2 : 1) + 3 * stage;
		int max = (guardian ? 8 : 5) + 6 * stage;
		if (destination >= 21 && guardian) {
			min = Math.round(min * 1.15f);
			max = Math.round(max * 1.15f);
		}
		return Random.NormalIntRange(min, max);
	}

	@Override
	public int attackSkill(Char target) {
		int stage = stage();
		return (guardian ? 12 : 9) + 6 * stage;
	}

	@Override
	public int drRoll() {
		int stage = stage();
		return super.drRoll() + Random.NormalIntRange(0, (guardian ? 3 : 1) + 2 * stage);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		if (Random.Int(guardian ? 3 : 8) == 0) {
			switch (destination) {
				case 6: case 17: Buff.affect(enemy, Burning.class).reignite(enemy, 3f); break;
				case 7: case 12: case 19: Buff.prolong(enemy, Cripple.class, 2f); break;
				case 9: case 16: Buff.affect(enemy, Poison.class).set(3f); break;
				case 10: case 13: case 18: Buff.prolong(enemy, Blindness.class, 2f); break;
				case 11: case 14: Buff.prolong(enemy, Weakness.class, 3f); break;
				case 15: Buff.prolong(enemy, Vertigo.class, 2f); break;
				case 20: case 23: Buff.prolong(enemy, Vulnerable.class, 2f); break;
				case 21:
					switch (rushWave) {
						case 0: Buff.prolong(enemy, Cripple.class, 2f); break;
						case 1: Buff.prolong(enemy, Blindness.class, 2f); break;
						case 2: Buff.prolong(enemy, Weakness.class, 2f); break;
						default: Buff.prolong(enemy, Slow.class, 2f); break;
					}
					break;
				case 22:
					if (Random.Int(2) == 0) Buff.prolong(enemy, Vertigo.class, 2f);
					else Buff.prolong(enemy, Weakness.class, 2f);
					break;
				case 24:
					if (Random.Int(2) == 0) Buff.affect(enemy, Poison.class).set(3f);
					else Buff.prolong(enemy, Chill.class, 3f);
					break;
				default: Buff.prolong(enemy, Vulnerable.class, 2f); break;
			}
		}
		return damage;
	}

	private boolean ranged() {
		return destination == 7 || destination == 10 || destination == 13
				|| destination == 16 || destination == 17 || destination == 19
				|| destination >= 20 && destination != 21
				|| destination == 21 && (rushWave == 1 || rushWave == 3);
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return super.canAttack(enemy) || ranged()
				&& new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override
	protected boolean doAttack(Char enemy) {
		if (!Dungeon.level.adjacent(pos, enemy.pos) && ranged()) {
			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap(enemy.pos);
				return false;
			}
			rangedAttack();
			return true;
		}
		return super.doAttack(enemy);
	}

	private void rangedAttack() {
		spend(TICK);
		if (enemy != null && enemy.isAlive() && hit(this, enemy, true)) {
			int damage = Math.max(1, Math.round(damageRoll() * 0.70f));
			enemy.damage(attackProc(enemy, damage), this);
		} else if (enemy != null && enemy.sprite != null) {
			enemy.sprite.showStatus(0xFFFFFF, enemy.defenseVerb());
		}
	}

	public void onZapComplete() {
		rangedAttack();
		next();
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		damage = super.defenseProc(enemy, damage);
		if (guardian) {
			int nextHP = HP - damage;
			if (phase == 0 && nextHP <= HT * 2 / 3) {
				phase = 1;
				activatePhase();
			} else if (phase == 1 && nextHP <= HT / 3) {
				phase = 2;
				activatePhase();
			}
		}
		return damage;
	}

	private void activatePhase() {
		Buff.prolong(this, Haste.class, destination >= 21 ? 4f : 3f);
		int shield = 2 + stage() + phase;
		if (destination == 12 || destination == 15 || destination == 19) shield += 2 + stage();
		Buff.affect(this, Barrier.class).incShield(shield);

		if (destination == 9 || destination == 11 || destination == 13
				|| destination == 14 || destination == 17
				|| destination == 23 || destination == 24) {
			summonEcho();
		}
		if (destination == 10 || destination == 16 || destination == 18
				|| destination == 20 || destination == 22 || destination == 24) {
			ScrollOfTeleportation.teleportChar(this);
		}
	}

	private void summonEcho() {
		ArrayList<Integer> cells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = pos + offset;
			if (cell >= 0 && cell < Dungeon.level.length()
					&& Dungeon.level.passable[cell] && Actor.findChar(cell) == null) {
				cells.add(cell);
			}
		}
		if (cells.isEmpty()) return;
		AdventureGuardian echo = new AdventureGuardian().configure(destination, false);
		echo.pos = Random.element(cells);
		echo.state = echo.HUNTING;
		GameScene.add(echo, 1f);
		Dungeon.level.occupyCell(echo);
	}

	@Override
	public void die(Object cause) {
		if (guardian && destination == 21 && rushWave < 3) {
			rushWave++;
			phase = 0;
			HP = HT;
			Buff.affect(this, Barrier.class).incShield(3 + stage());
			GLog.w(Messages.get(this, "rush_next", name()));
			return;
		}
		if (guardian) AdventureJournal.complete(destination);
		super.die(cause);
	}

	private static final String DESTINATION = "destination";
	private static final String GUARDIAN = "guardian";
	private static final String PHASE = "phase";
	private static final String RUSH_WAVE = "rush_wave";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DESTINATION, destination);
		bundle.put(GUARDIAN, guardian);
		bundle.put(PHASE, phase);
		bundle.put(RUSH_WAVE, rushWave);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		destination = bundle.getInt(DESTINATION);
		guardian = bundle.getBoolean(GUARDIAN);
		phase = bundle.getInt(PHASE);
		rushWave = bundle.getInt(RUSH_WAVE);
		if (guardian) properties.add(Property.MINIBOSS);
		else properties.remove(Property.MINIBOSS);
		setSprite();
	}
}
