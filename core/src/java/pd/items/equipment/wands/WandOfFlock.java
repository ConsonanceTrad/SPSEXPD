/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.wands;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.FlyingProtector;
import pd.actors.mobs.npcs.NPC;
import pd.effects.CellEmitter;
import pd.effects.MagicMissile;
import pd.effects.Speck;
import pd.items.Heap;
import pd.items.quest.AdventureJournal;
import pd.items.quest.ChallengeJournal;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.BaBaSprite;
import pd.sprites.SheepSprite;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.data.BArray;
import render.utils.data.Callback;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** The single-sheep obstruction wand from SPS-PD 0.9.8. */
public class WandOfFlock extends Wand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfFlock.class)
			.t("name", "招羊法杖")
			.t("desc", "这根暗属性法杖看起来就像普通木棍一样，但两头的金色饰物使它看起来并不普通。")
			.t("stats_desc", "该法杖会在目标地点召唤一只持续_2加法杖等级_回合的魔法绵羊。它能阻挡移动，并会在受到攻击时以暗属性伤害反击。")
			.t("guard", "魔法引起了智慧守卫的注意。")
			.t("magicsheep.name", "魔法绵羊")
			.t("magicsheep.desc", "这是一只杀不死的魔法绵羊。它只会站在那里直到消失，并在受到攻击时反击。")
			.t("magicbombsheep.name", "BABA")
			.t("magicbombsheep.desc", "LEVEL_TOO_HARD，BABA_IS_DONE。BETTER_LOOK_OUT，BABA_HAS_GUN。");
	}


	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
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
					pd.actors.damagetype.DamageType.DARK_DAMAGE);
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
					pd.actors.damagetype.DamageType.DARK_DAMAGE);
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
