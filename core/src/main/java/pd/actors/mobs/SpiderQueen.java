/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.ConfusionGas;
import pd.actors.blobs.DarkGas;
import pd.actors.blobs.SlowWeb;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Amok;
import pd.actors.buffs.BeOld;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Slow;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.effects.Pushing;
import pd.effects.Speck;
import pd.effects.particles.ElmoParticle;
import pd.items.Generator;
import pd.items.Item;
import pd.items.artifacts.RobotDMT;
import pd.items.food.MysteryMeat;
import pd.items.scrolls.ScrollOfTeleportation;
import pd.levels.Terrain;
import pd.levels.features.Door;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.SpiderEggSprite;
import pd.sprites.SpiderGoldSprite;
import pd.sprites.SpiderJumpSprite;
import pd.sprites.SpiderMindSprite;
import pd.sprites.SpiderNormalSprite;
import pd.sprites.SpiderQueenSprite;
import pd.ui.BossHealthBar;
import watabou.utils.BArray;
import watabou.utils.Bundle;
import watabou.utils.PathFinder;
import watabou.utils.Random;

import java.util.ArrayList;

/** SPS-PD's spider queen and all four original hatchling outcomes. */
public class SpiderQueen extends Mob {
	{
		spriteClass = SpiderQueenSprite.class;
		HP = HT = 1000;
		EXP = 50;
		defenseSkill = 35;
		baseSpeed = 0.8f;
		properties.add(Property.BEAST);
		properties.add(Property.BOSS);
		resistances.add(Poison.class);
		immunities.add(Slow.class);
		immunities.add(Roots.class);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(21, 36); }
	@Override public int attackSkill(Char target) { return 40; }
	@Override public int drRoll() { return Random.NormalIntRange(5, 20); }

	@Override
	protected boolean act() {
		boolean result = super.act();
		if (state == FLEEING && buff(Terror.class) == null && enemy != null
				&& enemySeen && enemy.buff(Poison.class) == null) state = HUNTING;
		return result;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(2) == 0) {
			Buff.affect(enemy, Poison.class).set(Random.Int(7, 9));
			state = FLEEING;
		}
		return super.attackProc(enemy, damage);
	}

	@Override
	public void move(int step, boolean travelling) {
		super.move(step, travelling);
		if (Dungeon.level == null || Dungeon.level.map[step] != Terrain.INACTIVE_TRAP) return;
		if (sprite != null) sprite.emitter().burst(ElmoParticle.FACTORY, 5);
		ArrayList<Integer> cells = freeNeighbours(pos);
		if (!cells.isEmpty()) {
			SpiderEgg egg = new SpiderEgg();
			egg.pos = Random.element(cells);
			if (Dungeon.level.map[egg.pos] == Terrain.DOOR) Door.enter(egg.pos);
			GameScene.add(egg, 1f);
			Actor.addDelayed(new SourceTimedPushing(egg, pos, egg.pos), 0f);
			damage(1, this);
		}
		if (Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[step]) yell(Messages.get(this, "egg"));
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
				new pd.items.weapon.rockcode.Sweb());
		Item common = Generator.random(Generator.Category.ARMOR);
		SpsCavesBossRewards.grant(pos, rareLoot(), common);
		yell(Messages.get(this, "die"));
	}

	public static Item rareLoot() {
		return new RobotDMT().identify();
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}

	private static ArrayList<Integer> freeNeighbours(int center) {
		ArrayList<Integer> result = new ArrayList<>();
		if (Dungeon.level == null) return result;
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = center + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.distance(center, cell) == 1
					&& Dungeon.level.passable[cell] && Actor.findChar(cell) == null) result.add(cell);
		}
		return result;
	}

	private static final class SourceTimedPushing extends Pushing {
		SourceTimedPushing(Char ch, int from, int to) {
			super(ch, from, to);
			spend(-1f);
		}
	}

	public static class SpiderEgg extends Mob {
		private int life;

		{
			spriteClass = SpiderEggSprite.class;
			HP = HT = 25;
			defenseSkill = 0;
			EXP = 0;
			state = PASSIVE;
			properties.add(Property.UNKNOW);
			properties.add(Property.BOSS);
			properties.add(Property.BOSS_MINION);
			immunities.add(Amok.class);
			immunities.add(Sleep.class);
			immunities.add(Terror.class);
			immunities.add(Poison.class);
			immunities.add(Vertigo.class);
			immunities.add(ToxicGas.class);
			immunities.add(Slow.class);
		}

		@Override public void beckon(int cell) { }
		@Override public int damageRoll() { return Random.NormalIntRange(0, 1); }
		@Override public int attackSkill(Char target) { return 0; }
		@Override public int drRoll() { return 0; }

		@Override
		protected boolean act() {
			if (Dungeon.level != null) {
				PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), 2);
				for (int cell = 0; cell < PathFinder.distance.length; cell++) {
					if (PathFinder.distance[cell] < Integer.MAX_VALUE) GameScene.add(Blob.seed(cell, 2, SlowWeb.class));
				}
			}
			life++;
			damage(1, this);
			if (isAlive()) return super.act();
			return true;
		}

		@Override
		public void die(Object cause) {
			int hatchCell = pos;
			super.die(cause);
			SpiderWorker hatchling;
			if (life > 20) hatchling = new SpiderGold();
			else if (life > 10) hatchling = new SpiderJumper();
			else if (life > 5) hatchling = new SpiderMind();
			else hatchling = new SpiderWorker();
			hatchling.pos = hatchCell;
			hatchling.state = hatchling.HUNTING;
			GameScene.add(hatchling, 1f);
		}

		private static final String LIFE = "life";
		@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(LIFE, life); }
		@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); life = bundle.getInt(LIFE); }
	}

	public static class SpiderWorker extends Mob {
		{
			spriteClass = SpiderNormalSprite.class;
			HP = HT = 150;
			defenseSkill = 10;
			EXP = 9;
			maxLvl = 16;
			loot = MysteryMeat.class;
			lootChance = 0.15f;
			properties.add(Property.BEAST);
			resistances.add(Poison.class);
			immunities.add(Roots.class);
			immunities.add(SlowWeb.class);
		}

		@Override public int damageRoll() { return Random.NormalIntRange(12, 26 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 20 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return Random.NormalIntRange(6, 10); }
	}

	public static class SpiderMind extends SpiderWorker {
		{
			spriteClass = SpiderMindSprite.class;
			HP = HT = 100;
			defenseSkill = 20;
		}

		@Override public int damageRoll() { return Random.NormalIntRange(5, 10 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 20 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return 0; }

		@Override
		public int attackProc(Char enemy, int damage) {
			int heal = damage > 0 ? Random.Int(damage) : 0;
			if (heal > 0 && buff(BeOld.class) == null) {
				HP += heal;
				if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
			}
			return super.attackProc(enemy, damage);
		}

		@Override protected boolean act() {
			GameScene.add(Blob.seed(pos, 15, DarkGas.class));
			return super.act();
		}
	}

	public static class SpiderJumper extends SpiderWorker {
		private static final int BLINK_DELAY = 3;
		private int delay;

		{
			spriteClass = SpiderJumpSprite.class;
			HP = HT = 150;
			defenseSkill = 10;
		}

		@Override public int damageRoll() { return Random.NormalIntRange(12, 26 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 20 + legacyDepthAdjustment(0); }

		@Override
		protected boolean getCloser(int target) {
			if (fieldOfView != null && fieldOfView[target] && Dungeon.level.distance(pos, target) > 2 && delay <= 0) {
				blink(target);
				spend(-1f / speed());
				return true;
			}
			delay--;
			return super.getCloser(target);
		}

		private void blink(int target) {
			Ballistica route = new Ballistica(pos, target, Ballistica.PROJECTILE);
			int cell = route.collisionPos;
			if (Actor.findChar(cell) != null && cell != pos && route.dist > 0) cell = route.path.get(route.dist - 1);
			if (!Dungeon.level.insideMap(cell)) { delay = BLINK_DELAY; return; }
			if (Dungeon.level.avoid[cell] || Actor.findChar(cell) != null) {
				ArrayList<Integer> candidates = freeNeighbours(route.collisionPos);
				if (candidates.isEmpty()) { delay = BLINK_DELAY; return; }
				cell = Random.element(candidates);
			}
			ScrollOfTeleportation.appear(this, cell);
			delay = BLINK_DELAY;
		}

		private static final String DELAY = "delay";
		@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(DELAY, delay); }
		@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); delay = bundle.getInt(DELAY); }
	}

	public static class SpiderGold extends SpiderWorker {
		{
			spriteClass = SpiderGoldSprite.class;
			HP = HT = 300;
			defenseSkill = 5;
			baseSpeed = 0.75f;
			immunities.add(ConfusionGas.class);
		}

		@Override public int damageRoll() { return Random.NormalIntRange(20, 30 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 40 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return Random.NormalIntRange(0, 10); }

		@Override protected boolean act() {
			GameScene.add(Blob.seed(pos, 15, ConfusionGas.class));
			return super.act();
		}

		@Override public int defenseProc(Char enemy, int damage) {
			Buff.prolong(this, DefenceUp.class, 3f).level(20);
			return super.defenseProc(enemy, damage);
		}
	}
}
