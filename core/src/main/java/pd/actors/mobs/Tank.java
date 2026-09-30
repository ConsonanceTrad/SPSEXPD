/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.ToxicGas;
import pd.actors.blobs.damageblobs.DarkEffectDamage;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Vertigo;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.artifacts.DriedRose;
import pd.items.potions.PotionOfToxicGas;
import pd.items.wands.WandOfBlastWave;
import pd.items.wands.WandOfLight;
import pd.items.weapon.enchantments.EnchantmentDark;
import pd.items.weapon.enchantments.EnchantmentLight;
import pd.levels.Terrain;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.TankSprite;
import pd.ui.BossHealthBar;
import pd.utils.GLog;
import render.noosa.Camera;
import render.noosa.audio.Sample;
import render.utils.Bundle;
import render.utils.Random;

import java.util.ArrayList;

/** SPS-PD's slow prison tank boss with jumps, corrupt trails and rock walls. */
public class Tank extends Mob {

	private static final int JUMP_DELAY = 20;
	private int timeToJump = JUMP_DELAY;
	private boolean rock = true;

	{
		spriteClass = TankSprite.class;
		HP = HT = 1000;
		EXP = 40;
		defenseSkill = 5;
		viewDistance = 4;
		properties.add(Property.UNDEAD);
		properties.add(Property.BOSS);
		resistances.add(ToxicGas.class);
		resistances.add(Poison.class);
		resistances.add(EnchantmentDark.class);
		weaknesses.add(WandOfLight.class);
		weaknesses.add(EnchantmentLight.class);
		immunities.add(DarkEffectDamage.class);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(40, 65); }
	@Override public int attackSkill(Char target) { return 60; }
	@Override public int drRoll() { return 0; }
	@Override public float attackDelay() { return 3f; }

	@Override
	protected boolean act() {
		timeToJump--;
		if (timeToJump <= 0) {
			jump();
			rock = false;
		}
		if (!rock && timeToJump > 5 && timeToJump < 15 && state == HUNTING
				&& paralysed <= 0 && enemy != null && enemy.invisible == 0
				&& fieldOfView != null && fieldOfView[enemy.pos]
				&& Dungeon.level.distance(pos, enemy.pos) < 5
				&& !Dungeon.level.adjacent(pos, enemy.pos)
				&& Random.Int(3) == 0 && HP > 0) {
			rockAttack();
		}
		return super.act();
	}

	@Override
	public void move(int step, boolean travelling) {
		super.move(step, travelling);
		if (Dungeon.level == null) return;
		GameScene.add(Blob.seed(pos, Random.IntRange(5, 6), DarkEffectDamage.class));

		ArrayList<Integer> neighbours = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = step + offset;
			if (Dungeon.level.insideMap(cell)) neighbours.add(cell);
		}
		if (neighbours.isEmpty()) return;
		int cell = Random.element(neighbours);
		if (sprite != null && Camera.main != null && Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[cell]) {
			CellEmitter.get(cell).start(Speck.factory(Speck.ROCK), 0.07f, 10);
			Camera.main.shake(3, 0.7f);
			Sample.INSTANCE.play(Assets.Sounds.ROCKS);
		}
		Char ch = Actor.findChar(cell);
		if (ch != null && ch != this) Buff.prolong(ch, AttackDown.class, 3f).level(10);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		int opposite = enemy.pos + enemy.pos - pos;
		Ballistica trajectory = new Ballistica(enemy.pos, opposite, Ballistica.MAGIC_BOLT);
		WandOfBlastWave.throwChar(enemy, trajectory, 1, false, false, this);
		Buff.prolong(enemy, Vertigo.class, 3f);
		return super.attackProc(enemy, damage);
	}

	private void jump() {
		timeToJump = JUMP_DELAY;
		if (Dungeon.hero == null || Dungeon.level == null) return;
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = Dungeon.hero.pos + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
					&& Actor.findChar(cell) == null) candidates.add(cell);
		}
		if (candidates.isEmpty()) {
			spend(2f);
			return;
		}

		int oldPos = pos;
		int newPos = Random.element(candidates);
		if (sprite != null) sprite.move(oldPos, newPos);
		move(newPos, false);

		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = newPos + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.map[cell] == Terrain.WALL) {
				Dungeon.level.set(cell, Terrain.EMBERS);
				GameScene.updateMap(cell);
			}
		}
		spend(2f);
	}

	private void rockAttack() {
		rock = true;
		if (Dungeon.hero == null || Dungeon.level == null) return;
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = Dungeon.hero.pos + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
					&& Actor.findChar(cell) == null) candidates.add(cell);
		}
		if (candidates.isEmpty()) return;
		int rockPos = Random.element(candidates);
		Dungeon.level.set(rockPos, Terrain.WALL);
		GameScene.updateMap(rockPos);
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = rockPos + offset;
			if (!Dungeon.level.insideMap(cell)) continue;
			Char ch = Actor.findChar(cell);
			if (ch != null && ch.isAlive()) ch.damage(15, this);
		}
		GLog.n(Messages.get(this, "rock"));
	}

	@Override
	public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		yell(Messages.get(this, "notice"));
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		pd.items.weapon.rockcode.RockCode.dropForPerformer(
				new pd.items.weapon.rockcode.Trush());
		SpsPrisonBossRewards.grant(pos, new DriedRose().identify(), new PotionOfToxicGas());
		yell(Messages.get(this, "die"));
	}

	private static final String TIME_TO_JUMP = "time_to_jump";
	private static final String ROCK = "rock";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TIME_TO_JUMP, timeToJump);
		bundle.put(ROCK, rock);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		timeToJump = bundle.contains(TIME_TO_JUMP) ? bundle.getInt(TIME_TO_JUMP) : JUMP_DELAY;
		rock = !bundle.contains(ROCK) || bundle.getBoolean(ROCK);
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}
}
