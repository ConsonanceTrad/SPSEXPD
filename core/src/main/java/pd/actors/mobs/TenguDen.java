/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Slow;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.effects.particles.FlameParticle;
import pd.items.AdamantRing;
import pd.items.Gold;
import pd.items.quest.AdventureJournal;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.TenguSprite;
import pd.ui.BossHealthBar;
import render.noosa.audio.Sample;
import render.utils.Bundle;
import render.utils.Random;

import java.util.ArrayList;

public class TenguDen extends Mob {

	private static final int JUMP_DELAY = 5;
	private int timeToJump = JUMP_DELAY;

	{
		spriteClass = TenguSprite.class;
		baseSpeed = 2f;
		HP = HT = 2000;
		EXP = 20;
		defenseSkill = 30;
		properties.add(Property.BOSS);
		properties.add(Property.HUMAN);
		resistances.add(ToxicGas.class);
		resistances.add(Poison.class);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(15, 25); }
	@Override public int attackSkill(Char target) { return 28; }
	@Override public int drRoll() { return Random.NormalIntRange(10, 20); }

	@Override
	protected boolean getCloser(int target) {
		if (fieldOfView != null && target >= 0 && target < fieldOfView.length && fieldOfView[target]
				&& jump()) return true;
		return super.getCloser(target);
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
	}

	@Override
	protected boolean doAttack(Char enemy) {
		timeToJump--;
		if (timeToJump <= 0 && jump()) return true;
		return super.doAttack(enemy);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(15) == 0) {
			Buff.affect(enemy, Burning.class).reignite(enemy, 5f);
			if (enemy.sprite != null) enemy.sprite.emitter().burst(FlameParticle.FACTORY, 5);
		}
		if (Random.Int(20) == 0) Buff.prolong(enemy, Slow.class, 3f);
		if (Random.Int(30) == 0) Buff.prolong(enemy, Paralysis.class, 2f);
		return super.attackProc(enemy, damage);
	}

	private boolean jump() {
		timeToJump = JUMP_DELAY;
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null
					&& (Dungeon.hero == null || !Dungeon.level.adjacent(cell, Dungeon.hero.pos))) {
				candidates.add(cell);
			}
		}
		if (candidates.isEmpty()) return false;
		int oldPos = pos;
		int newPos = Random.element(candidates);
		if (sprite != null) sprite.move(oldPos, newPos);
		move(newPos);
		if (Dungeon.level.heroFOV[newPos]) {
			CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6);
			Sample.INSTANCE.play(pd.Assets.Sounds.PUFF);
		}
		spend(1f / speed());
		if (Dungeon.level.mobs.size() < 7) Assassin.spawnAt(oldPos);
		return true;
	}

	@Override
	public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		yell(Messages.get(this, "notice"));
	}

	@Override
	public void die(Object cause) {
		int deathPos = pos;
		super.die(cause);
		Dungeon.tenguDenKilled = true;
		AdventureJournal.complete(10);
		Dungeon.level.unseal();
		GameScene.bossSlain();
		Dungeon.level.drop(new AdamantRing(), deathPos).sprite.drop();
		Dungeon.level.drop(new Gold(Random.Int(1900, 4000)), deathPos).sprite.drop();
		yell(Messages.get(this, "die"));
	}

	private static final String TIME_TO_JUMP = "time_to_jump";
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(TIME_TO_JUMP, timeToJump); }
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		timeToJump = bundle.contains(TIME_TO_JUMP) ? bundle.getInt(TIME_TO_JUMP) : JUMP_DELAY;
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}
}
