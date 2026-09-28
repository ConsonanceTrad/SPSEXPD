/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FlyingProtector;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.AdventureJournal;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.ChallengeJournal;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BaBaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SheepSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

/** The single-sheep obstruction wand from SPS-PD 0.9.8. */
public class WandOfFlock extends Wand {

	{
		image = ItemSpriteSheet.WAND_FLOCK;
		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override
	public void onZap(Ballistica bolt) {
		int spawnCell = findSpawnCell(bolt.collisionPos);
		if (spawnCell >= 0) {
			if (Dungeon.hero.subClass == HeroSubClass.LEADER && !inProtectedPuzzle()) {
				MagicBombSheep sheep = new MagicBombSheep();
				sheep.pos = spawnCell;
				sheep.wandLevel = level();
				GameScene.add(sheep);
				Dungeon.level.occupyCell(sheep);
			} else {
				MagicSheep sheep = new MagicSheep();
				sheep.pos = spawnCell;
				sheep.initialize(2f + level(), level());
				GameScene.add(sheep);
				Dungeon.level.occupyCell(sheep);
			}
			CellEmitter.get(spawnCell).burst(Speck.factory(Speck.WOOL), 4);
		}

		if (inProtectedPuzzle()) {
			FlyingProtector protector = new FlyingProtector();
			protector.adjustStats(50 + AdventureJournal.destinationForBranch(Dungeon.branch));
			int guardCell = Dungeon.level.randomRespawnCell(protector);
			if (guardCell >= 0) {
				protector.pos = guardCell;
				GameScene.add(protector);
				Dungeon.level.occupyCell(protector);
				GLog.w(Messages.get(this, "guard"));
			}
		}

		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) heap.darkhit();
	}

	private int findSpawnCell(int target) {
		boolean[] passable = BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null);
		for (Char ch : Actor.chars()) passable[ch.pos] = false;
		PathFinder.buildDistanceMap(target, passable, 1);

		int distance = Actor.findChar(target) == null ? 0 : 1;
		if (distance == 1) PathFinder.distance[target] = Integer.MAX_VALUE;
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (PathFinder.distance[cell] == distance && Dungeon.level.insideMap(cell)
					&& !Dungeon.level.pit[cell] && Actor.findChar(cell) == null) return cell;
		}
		return -1;
	}

	public static boolean protectedPuzzleDestination(int destination) {
		return destination >= 1 && destination <= 4;
	}

	private static boolean inProtectedPuzzle() {
		return protectedPuzzleDestination(AdventureJournal.destinationForBranch(Dungeon.branch));
	}

	public static int legacyDepth() {
		return Math.max(1, Dungeon.legacyDepth());
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.WOOL,
				curUser.sprite, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}

	public static class MagicSheep extends NPC {

		private static final String LIFESPAN = "lifespan";
		private static final String INITIALIZED = "initialized";
		private static final String WAND_LEVEL = "wand_level";

		private float lifespan;
		private boolean initialized;
		private int wandLevel;

		{
			spriteClass = SheepSprite.class;
			flying = true;
			alignment = Alignment.ALLY;
		}

		public void initialize(float lifespan, int wandLevel) {
			this.lifespan = lifespan;
			this.wandLevel = wandLevel;
		}

		@Override
		protected boolean act() {
			if (initialized) {
				HP = 0;
				destroy();
				if (sprite != null) sprite.die();
			} else {
				initialized = true;
				spend(lifespan + Random.Float(2f));
			}
			return true;
		}

		@Override public void damage(int dmg, Object src) { }
		@Override public boolean add(Buff buff) { return false; }
		@Override public boolean interact(Char ch) { return false; }

		@Override
		public int defenseProc(Char enemy, int damage) {
			enemy.damage(Random.IntRange(1, Math.max(1, wandLevel * 2)),
					com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType.DARK_DAMAGE);
			return super.defenseProc(enemy, damage);
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(LIFESPAN, lifespan);
			bundle.put(INITIALIZED, initialized);
			bundle.put(WAND_LEVEL, wandLevel);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			lifespan = bundle.getFloat(LIFESPAN);
			initialized = bundle.getBoolean(INITIALIZED);
			wandLevel = bundle.getInt(WAND_LEVEL);
		}
	}

	public static class MagicBombSheep extends NPC {

		private static final String WAND_LEVEL = "wand_level";
		private int wandLevel;

		{
			spriteClass = BaBaSprite.class;
			HP = HT = 20;
			state = HUNTING;
			defenseSkill = 10;
			alignment = Alignment.ALLY;
		}

		@Override
		protected boolean act() {
			damage(1, this);
			return super.act();
		}

		@Override public int attackSkill(Char target) { return 100; }

		@Override
		public int damageRoll() {
			return Random.NormalIntRange(legacyDepth() + 10, legacyDepth() + 20);
		}

		@Override
		public int defenseProc(Char enemy, int damage) {
			enemy.damage(Random.IntRange(1, Math.max(1, wandLevel * 4)),
					com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType.DARK_DAMAGE);
			return super.defenseProc(enemy, damage);
		}

		@Override
		public boolean interact(Char ch) {
			if (!(ch instanceof Hero) || !Dungeon.level.adjacent(pos, ch.pos)) return false;
			if (state == SLEEPING) state = HUNTING;
			int sheepFrom = pos;
			int heroFrom = ch.pos;
			move(heroFrom);
			ch.move(sheepFrom);
			if (sprite != null) sprite.move(sheepFrom, heroFrom);
			if (ch.sprite != null) ch.sprite.move(heroFrom, sheepFrom);
			((Hero) ch).spendAndNext(1f / ch.speed());
			return true;
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(WAND_LEVEL, wandLevel);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			wandLevel = bundle.getInt(WAND_LEVEL);
		}
	}
}
