/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.ConfusionGas;
import pd.actors.blobs.Electricity;
import pd.actors.blobs.ToxicGas;
import pd.actors.blobs.TarGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Invisibility;
import pd.actors.damagetype.DamageType;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.effects.particles.BlastParticle;
import pd.effects.particles.ElmoParticle;
import pd.effects.particles.SmokeParticle;
import pd.effects.particles.SparkParticle;
import pd.items.Gold;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.Heap;
import pd.items.artifacts.CapeOfThorns;
import pd.items.nornstone.BlueNornStone;
import pd.items.nornstone.GreenNornStone;
import pd.items.nornstone.OrangeNornStone;
import pd.items.nornstone.PurpleNornStone;
import pd.items.nornstone.YellowNornStone;
import pd.levels.Terrain;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.BrokenRobotSprite;
import pd.sprites.CharSprite;
import pd.sprites.SpsDM300Sprite;
import pd.sprites.TowerSprite;
import pd.ui.BossHealthBar;
import pd.utils.GLog;
import render.noosa.Camera;
import render.noosa.audio.Sample;
import render.utils.Bundle;
import render.utils.PathFinder;
import render.utils.Random;

import java.util.ArrayList;

/** SPS-PD's tower-powered DM-300, separate from Shattered's arena-specific DM-300. */
public class SpsDM300 extends Mob {

	private boolean towersSpawned;

	{
		spriteClass = SpsDM300Sprite.class;
		HP = HT = 800;
		EXP = 50;
		defenseSkill = 24;
		properties.add(Property.MECH);
		properties.add(Property.INORGANIC);
		properties.add(Property.BOSS);
		immunities.add(ToxicGas.class);
		immunities.add(Terror.class);
	}

	private int towerPower() {
		// The source increments this power once when the pair is created; it is
		// not the number of surviving towers.
		return towersSpawned ? 1 : 0;
	}

	@Override public int damageRoll() { return Random.NormalIntRange(15, 19) * towerPower(); }
	@Override public int attackSkill(Char target) { return 35; }
	@Override public int drRoll() { return Random.NormalIntRange(10, 10 + 4 * towerPower()); }

	@Override
	protected boolean act() {
		if (!towersSpawned) {
			spawnTowers();
			towersSpawned = true;
		}
		GameScene.add(Blob.seed(pos, 30, ToxicGas.class));
		return super.act();
	}

	private void spawnTowers() {
		if (Dungeon.level == null) return;
		ArrayList<Integer> cells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = pos + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
					&& Actor.findChar(cell) == null) cells.add(cell);
		}
		if (cells.size() < 2) {
			for (int cell = 0; cell < Dungeon.level.length() && cells.size() < 2; cell++) {
				if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null
						&& Dungeon.level.distance(pos, cell) <= 3 && !cells.contains(cell)) cells.add(cell);
			}
		}
		for (int i = 0; i < Math.min(2, cells.size()); i++) {
			int pick = Random.Int(cells.size());
			Tower tower = new Tower();
			tower.pos = cells.remove(pick);
			GameScene.add(tower);
		}
	}

	@Override
	public void move(int step, boolean travelling) {
		super.move(step, travelling);
		if (Dungeon.level == null) return;
		if (Dungeon.level.map[step] == Terrain.INACTIVE_TRAP && HP < HT) {
			HP = Math.min(HT, HP + Random.Int(1, HT - HP));
			if (sprite != null) sprite.emitter().burst(ElmoParticle.FACTORY, 5);
			if (Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[step]) GLog.n(Messages.get(this, "heal"));
		}

		ArrayList<Integer> cells = adjacentCells(step, false);
		if (cells.isEmpty()) return;
		int cell = Random.element(cells);
		if (Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[cell]) {
			CellEmitter.get(cell).start(Speck.factory(Speck.ROCK), 0.07f, 10);
			Camera.main.shake(3, 0.7f);
			Sample.INSTANCE.play(Assets.Sounds.ROCKS);
			if (Dungeon.level.water[cell]) GameScene.ripple(cell);
			else if (Dungeon.level.map[cell] == Terrain.EMPTY) {
				Dungeon.level.set(cell, Terrain.EMPTY_DECO);
				GameScene.updateMap(cell);
			}
		}
		Char ch = Actor.findChar(cell);
		if (ch != null && ch != this) Buff.prolong(ch, Paralysis.class, 2f);
	}

	private static ArrayList<Integer> adjacentCells(int center, boolean includeCenter) {
		ArrayList<Integer> cells = new ArrayList<>();
		if (Dungeon.level == null) return cells;
		if (includeCenter) cells.add(center);
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = center + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.distance(center, cell) <= 1) cells.add(cell);
		}
		return cells;
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
				new pd.items.weapon.rockcode.Bmech());
		finishFight(pos, false);
		yell(Messages.get(this, "die"));
	}

	private static void finishFight(int pos, boolean towerWasKilled) {
		if (Dungeon.level == null) return;
		for (Mob mob : Dungeon.level.mobs) {
			if ((mob instanceof SpsDM300 || mob instanceof Tower) && mob.isAlive()) return;
		}
		if (towerWasKilled) Dungeon.level.drop(new Gold(Random.IntRange(3000, 6000)), pos).sprite.drop();
		Item common = Random.oneOf(new BlueNornStone(), new GreenNornStone(), new OrangeNornStone(),
				new PurpleNornStone(), new YellowNornStone());
		SpsCavesBossRewards.grant(pos, new CapeOfThorns().identify(), common);
	}

	private static final String TOWERS_SPAWNED = "towers_spawned";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TOWERS_SPAWNED, towersSpawned);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		towersSpawned = bundle.getBoolean(TOWERS_SPAWNED);
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}

	public static class Tower extends Mob {
		{
			spriteClass = TowerSprite.class;
			HP = HT = 500 + Math.max(1, Dungeon.legacyDepth()) * Random.NormalIntRange(2, 5);
			defenseSkill = 0;
			EXP = 25;
			state = PASSIVE;
			alignment = Alignment.NEUTRAL;
			loot = StoneOre.class;
			lootChance = 1f;
			properties.add(Property.MECH);
			properties.add(Property.BOSS);
			properties.add(Property.INORGANIC);
			properties.add(Property.IMMOVABLE);
			properties.add(Property.BOSS_MINION);
			resistances.add(Electricity.class);
			immunities.add(ToxicGas.class);
			immunities.add(Terror.class);
			immunities.add(ConfusionGas.class);
		}

		@Override public void beckon(int cell) { }
		@Override public int damageRoll() { return 0; }
		@Override public int attackSkill(Char target) { return 0; }
		@Override public int drRoll() { return 0; }

		@Override
		public void damage(int damage, Object src) {
			if (Dungeon.level != null) {
				for (Mob mob : Dungeon.level.mobs) mob.beckon(Dungeon.hero == null ? pos : Dungeon.hero.pos);
			}
			if (sprite != null) {
				sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3);
				Sample.INSTANCE.play(Assets.Sounds.CHALLENGE);
			}
			GLog.w(Messages.get(this, "alert"));
			super.damage(damage, src);
		}

		@Override
		protected boolean act() {
			switch (Random.Int(4)) {
				case 1:
					if (Dungeon.level != null) for (Mob mob : Dungeon.level.mobs) {
						if (mob instanceof Tower && mob != this && mob.sprite != null) {
							mob.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
							mob.sprite.flash();
						}
					}
					break;
				case 2:
					if (Dungeon.level != null && Dungeon.level.mobs.size() < 10) {
						pd.actors.mobs.BrokenRobot.spawnAround(pos);
						GLog.n(Messages.get(this, "robots"));
					}
					break;
			}
			spend(TICK);
			return true;
		}

		@Override
		public boolean add(pd.actors.buffs.Buff buff) {
			return false;
		}

		@Override
		public void die(Object cause) {
			super.die(cause);
			explode(pos, this);
			finishFight(pos, true);
			dropLegacyDew(pos);
		}
	}

	public static class BrokenRobot extends Mob {
		private Ballistica beam;

		{
			spriteClass = BrokenRobotSprite.class;
			HP = HT = 120 + legacyDepthAdjustment(0) * Random.NormalIntRange(4, 7);
			defenseSkill = 20 + legacyDepthAdjustment(1);
			EXP = 13;
			maxLvl = 25;
			loot = StoneOre.class;
			lootChance = 0.25f;
			properties.add(Property.INORGANIC);
			immunities.add(Terror.class);
			immunities.add(ToxicGas.class);
		}

		@Override public int damageRoll() { return 0; }
		@Override public int attackSkill(Char target) { return 20 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return Random.NormalIntRange(0, 5); }
		@Override public float attackDelay() { return 3f; }

		@Override
		protected boolean act() {
			GameScene.add(Blob.seed(pos, 30, TarGas.class));
			if (enemySeen && Random.Int(50) == 1) {
				GLog.n(Messages.get(this, "explode"));
				explode(pos, this);
				if (!isAlive()) return true;
			}
			return super.act();
		}

		@Override
		protected boolean canAttack(Char enemy) {
			beam = new Ballistica(pos, enemy.pos, Ballistica.STOP_SOLID);
			return beam.subPath(1, beam.dist).contains(enemy.pos);
		}

		@Override
		protected boolean doAttack(Char enemy) {
			beam = new Ballistica(pos, enemy.pos, Ballistica.STOP_SOLID);
			if (sprite != null && enemy.sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.attack(enemy.pos);
				return false;
			}
			fireBeam();
			Invisibility.dispel(this);
			spend(attackDelay());
			return true;
		}

		private void fireBeam() {
			if (beam == null) return;
			for (int cell : beam.subPath(1, beam.dist)) {
				Char ch = Actor.findChar(cell);
				if (ch == null) continue;
				if (hit(this, ch, true)) {
					ch.damage(Random.NormalIntRange(2, 8 + legacyDepthAdjustment(0)), DamageType.LIGHT_DAMAGE);
					if (ch.sprite != null) {
						ch.sprite.flash();
						ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, Random.IntRange(1, 2));
					}
				} else if (ch.sprite != null) ch.sprite.showStatus(CharSprite.NEUTRAL, ch.defenseVerb());
			}
		}

		@Override
		public void onAttackComplete() {
			fireBeam();
			Invisibility.dispel(this);
			spend(attackDelay());
			next();
		}

		static void spawnAround(int center) {
			if (Dungeon.level == null) return;
			for (int offset : PathFinder.NEIGHBOURS4) {
				int cell = center + offset;
				if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
						&& Actor.findChar(cell) == null) {
					BrokenRobot robot = new BrokenRobot();
					robot.pos = cell;
					robot.state = robot.HUNTING;
					GameScene.add(robot, 2f);
				}
			}
		}

		public static final class LightDamage { }
	}

	private static void explode(int center, Object source) {
		if (Dungeon.level == null) return;
		Sample.INSTANCE.play(Assets.Sounds.BLAST, 2f);
		if (Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[center]) {
			CellEmitter.center(center).burst(BlastParticle.FACTORY, 30);
		}
		boolean terrainAffected = false;
		for (int cell : adjacentCells(center, true)) {
			if (Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[cell]) {
				CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 4);
			}
			if (Dungeon.level.flamable[cell]) {
				Dungeon.level.set(cell, Terrain.EMBERS);
				GameScene.updateMap(cell);
				terrainAffected = true;
			}
			Heap heap = Dungeon.level.heaps.get(cell);
			if (heap != null) heap.explode();
			Char ch = Actor.findChar(cell);
			if (ch != null && ch.isAlive()) {
				int min = cell == center ? legacyDungeonDepth() + 5 : 1;
				int max = 10 + legacyDepthAdjustment(3);
				int damage = Random.NormalIntRange(min, max) - Math.max(ch.drRoll(), 0);
				if (damage > 0) ch.damage(damage, source);
			}
		}
		if (terrainAffected) Dungeon.observe();
	}
}
