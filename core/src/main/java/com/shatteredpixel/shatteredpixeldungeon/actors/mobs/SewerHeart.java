/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GrowSeed;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RatKing;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.MissileShield;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SpsSkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode.Gleaf;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SewerHeartSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SewerLasherSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SewerHeart extends LegacyDualLootMob {
	private static final String BEAM_TARGET = "beam_target";
	private static final String BEAM_COOLDOWN = "beam_cooldown";
	private static final String BEAM_CHARGED = "beam_charged";
	private static final String BREAKS = "breaks";
	private static final String SPAWNED_LASHER = "spawned_lasher";

	private Ballistica beam;
	private int beamTarget = -1;
	private int beamCooldown;
	private int breaks;
	private boolean beamCharged;
	private boolean spawnedLasher;

	{
		spriteClass = SewerHeartSprite.class;
		HP = HT = 500;
		defenseSkill = 0;
		EXP = 30;
		maxLvl = 35;
		setupLegacyDualLoot(Rotberry.Seed.class, 0.2f, Generator.Category.BERRY, 1f);
		properties.add(Property.PLANT);
		properties.add(Property.BOSS);
		immunities.add(ToxicGas.class);
	}

	public boolean beamCharged() { return beamCharged; }

	@Override public Item SupercreateLoot() { return new MissileShield(); }

	@Override public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		Dungeon.level.seal();
		if (!spawnedLasher && Dungeon.hero != null) {
			Buff.affect(Dungeon.hero, LasherSpawner.class);
			spawnedLasher = true;
		}
		state = PASSIVE;
	}

	@Override protected boolean act() {
		if (5 - breaks > 6 * HP / HT) {
			breaks++;
			return true;
		}
		if (breaks == 3 && state == PASSIVE) state = HUNTING;
		if (beamCharged && state != HUNTING) beamCharged = false;
		if (beam == null && beamTarget != -1) {
			beam = new Ballistica(pos, beamTarget, Ballistica.STOP_SOLID);
			if (sprite != null) sprite.turnTo(pos, beamTarget);
		}
		if (beamCooldown > 0) beamCooldown--;
		return super.act();
	}

	@Override public void damage(int damage, Object source) {
		if (5 - breaks > 6 * HP / HT) teleportAndSeedLashers();
		super.damage(damage, source);
	}

	private void teleportAndSeedLashers() {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null
					&& (enemy == null || !Dungeon.level.adjacent(cell, enemy.pos))
					&& (Dungeon.level.heroFOV == null || Dungeon.level.heroFOV[cell])) candidates.add(cell);
		}
		if (candidates.isEmpty()) return;
		int oldPos = pos;
		int newPos = Random.element(candidates);
		move(newPos);
		if (sprite != null) sprite.move(oldPos, newPos);
		if (Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[newPos]) {
			CellEmitter.get(oldPos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
			Sample.INSTANCE.play(Assets.Sounds.PUFF);
		}
		GLog.n(Messages.get(this, "blink"));
		if (Dungeon.hero != null && Dungeon.level.mobs.size() < Dungeon.hero.lvl * 2) SewerLasher.spawnAroundChance(newPos);
	}

	@Override public int defenseProc(Char enemy, int damage) {
		GameScene.add(Blob.seed(pos, 20, ToxicGas.class));
		return super.defenseProc(enemy, damage);
	}

	@Override protected boolean getCloser(int target) { return false; }

	@Override protected boolean canAttack(Char enemy) {
		if (beamCooldown != 0) return super.canAttack(enemy);
		Ballistica aim = new Ballistica(pos, enemy.pos, Ballistica.STOP_SOLID);
		if (enemy.invisible == 0 && !isCharmedBy(enemy) && fieldOfView != null
				&& fieldOfView[enemy.pos] && aim.subPath(1, aim.dist).contains(enemy.pos)) {
			beam = aim;
			beamTarget = aim.collisionPos;
			return true;
		}
		return beamCharged;
	}

	@Override protected boolean doAttack(Char enemy) {
		if (beamCooldown > 0) return super.doAttack(enemy);
		if (!beamCharged) {
			if (sprite != null) ((SewerHeartSprite)sprite).charge(enemy.pos);
			spend(attackDelay() * 2f);
			beamCharged = true;
			return true;
		}
		spend(attackDelay());
		beam = new Ballistica(pos, beamTarget, Ballistica.STOP_SOLID);
		if (sprite != null && Dungeon.level.heroFOV != null
				&& (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[beam.collisionPos])) {
			sprite.zap(beam.collisionPos);
			return false;
		}
		deathGaze();
		return true;
	}

	public void deathGaze() {
		if (!beamCharged || beamCooldown > 0 || beam == null) return;
		beamCharged = false;
		beamCooldown = Random.IntRange(3, 6);
		boolean terrainAffected = false;
		for (int cell : beam.subPath(1, beam.dist)) {
			if (!Dungeon.level.insideMap(cell)) continue;
			if (Dungeon.level.flamable[cell]) {
				Dungeon.level.destroy(cell);
				GameScene.updateMap(cell);
				terrainAffected = true;
			}
			Char target = Actor.findChar(cell);
			if (target == null) continue;
			if (hit(this, target, true)) {
				target.damage(Random.NormalIntRange(20, 35), this);
				if (Dungeon.level.heroFOV[cell] && target.sprite != null) {
					target.sprite.flash();
					CellEmitter.center(cell).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
				}
			} else if (target.sprite != null) target.sprite.showStatus(CharSprite.NEUTRAL, target.defenseVerb());
		}
		if (terrainAffected) Dungeon.observe();
		beam = null;
		beamTarget = -1;
	}

	@Override public int damageRoll() { return Random.NormalIntRange(HP * 2 <= HT ? 5 : 2, HP * 2 <= HT ? 16 : 8); }
	@Override public int attackSkill(Char target) { return 30; }
	@Override public int drRoll() { return Random.NormalIntRange(0, 2); }

	@Override public void destroy() {
		super.destroy();
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) if (mob instanceof SewerLasher) mob.die(null);
	}

	@Override public void die(Object cause) {
		super.die(cause);
		if (Dungeon.hero != null) {
			Buff.detach(Dungeon.hero, LasherSpawner.class);
		}
		com.shatteredpixel.shatteredpixeldungeon.items.journalpages.JournalPage.dropAt(
				new com.shatteredpixel.shatteredpixeldungeon.items.journalpages.Sokoban1(), pos);
		Dungeon.level.drop(new Gold(1500), pos).sprite.drop();
		Dungeon.level.drop(new SpsSkeletonKey(Dungeon.depth), pos).sprite.drop();
		RatKing king = new RatKing();
		king.state = king.WANDERING;
		king.pos = pos;
		GameScene.add(king, 1f);
		ScrollOfTeleportation.appear(king, king.pos);
		if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.PERFORMER && Dungeon.hero.skin == 7) {
			Dungeon.level.drop(new Gleaf(), Dungeon.hero.pos).sprite.drop();
		}
		Dungeon.level.unseal();
		GameScene.bossSlain();
		Badges.validateBossSlain();
	}

	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BEAM_TARGET, beamTarget);
		bundle.put(BEAM_COOLDOWN, beamCooldown);
		bundle.put(BEAM_CHARGED, beamCharged);
		bundle.put(BREAKS, breaks);
		bundle.put(SPAWNED_LASHER, spawnedLasher);
	}

	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		beamTarget = bundle.contains(BEAM_TARGET) ? bundle.getInt(BEAM_TARGET) : -1;
		beamCooldown = bundle.getInt(BEAM_COOLDOWN);
		beamCharged = bundle.getBoolean(BEAM_CHARGED);
		breaks = bundle.getInt(BREAKS);
		spawnedLasher = bundle.getBoolean(SPAWNED_LASHER);
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}

	public static class LasherSpawner extends Buff {
		private static final String CHARGE = "charge";
		private int charge;

		@Override public boolean act() {
			charge++;
			int lashers = 1;
			for (Mob mob : Dungeon.level.mobs) if (mob instanceof SewerLasher) lashers++;
			int needed = Math.min(25, lashers);
			if (charge >= needed) {
				charge -= needed;
				int cell = randomFreeCell();
				if (cell >= 0) {
					SewerLasher.spawnAt(cell);
					Sample.INSTANCE.play(Assets.Sounds.BURNING);
				}
			}
			spend(TICK);
			return true;
		}

		@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
		@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = bundle.getInt(CHARGE); }
	}

	private static int randomFreeCell() {
		ArrayList<Integer> cells = new ArrayList<>();
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null) cells.add(cell);
		}
		return cells.isEmpty() ? -1 : Random.element(cells);
	}

	public static class SewerLasher extends Mob {
		{
			spriteClass = SewerLasherSprite.class;
			HP = HT = 60;
			defenseSkill = 0;
			EXP = 1;
			loot = Generator.Category.SEED;
			lootChance = 0.2f;
			state = HUNTING;
			properties.add(Property.PLANT);
			properties.add(Property.MINIBOSS);
			immunities.add(ToxicGas.class);
		}

		@Override protected boolean act() {
			if (enemy == null || !Dungeon.level.adjacent(pos, enemy.pos)) HP = Math.min(HT, HP + 3);
			return super.act();
		}
		@Override public void damage(int damage, Object source) {
			if (source instanceof Burning) { destroy(); if (sprite != null) sprite.die(); }
			else super.damage(damage, source);
		}
		@Override public int attackProc(Char enemy, int damage) {
			damage = super.attackProc(enemy, damage);
			if (Random.Int(5) == 0) Buff.affect(enemy, Cripple.class, 2f);
			else if (Random.Int(4) == 0) Buff.affect(enemy, GrowSeed.class).set(4f);
			else if (Random.Int(3) == 0) Buff.affect(enemy, Bleeding.class).set(damage);
			return super.attackProc(enemy, damage);
		}
		@Override protected boolean getCloser(int target) { return true; }
		@Override protected boolean getFurther(int target) { return true; }
		@Override public int damageRoll() { return Random.NormalIntRange(4, 12); }
		@Override public int attackSkill(Char target) { return 15; }
		@Override public int drRoll() { return Random.NormalIntRange(2, 8); }

		public static void spawnAroundChance(int center) {
			for (int offset : PathFinder.NEIGHBOURS4) {
				int cell = center + offset;
				if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
						&& Actor.findChar(cell) == null && Random.Float() < 0.75f) spawnAt(cell);
			}
		}
		public static SewerLasher spawnAt(int cell) {
			if (!Dungeon.level.insideMap(cell) || !Dungeon.level.passable[cell] || Actor.findChar(cell) != null) return null;
			SewerLasher lasher = new SewerLasher();
			lasher.pos = cell;
			lasher.state = lasher.HUNTING;
			GameScene.add(lasher, 2f);
			return lasher;
		}
	}
}
