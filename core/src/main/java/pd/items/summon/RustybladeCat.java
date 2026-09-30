/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.summon;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.Mob;
import pd.items.Item;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.sprites.ErrorSprite;
import render.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

/** Rustyblade's pager, which summons one of two Zero butter cats. */
public class RustybladeCat extends Item {

	private static final String AC_ACTIVE = "ACTIVE";
	private boolean summonOnThrow;

	{
		image = ItemSpriteSheet.RUSTY_CAT;
		defaultAction = AC_ACTIVE;
		stackable = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_ACTIVE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_ACTIVE.equals(action)) {
			summonOnThrow = true;
			super.execute(hero, AC_THROW);
		} else {
			summonOnThrow = false;
			super.execute(hero, action);
		}
	}

	@Override
	protected void onThrow(int cell) {
		if (!summonOnThrow || Dungeon.level == null || Dungeon.level.pit[cell]) {
			summonOnThrow = false;
			super.onThrow(cell);
			return;
		}
		int destination = openDestination(cell);
		if (destination < 0) {
			summonOnThrow = false;
			super.onThrow(cell);
			return;
		}
		summonAt(destination);
		summonOnThrow = false;
	}

	int openDestination(int cell) {
		if (Actor.findChar(cell) == null && Dungeon.level.insideMap(cell)
				&& Dungeon.level.passable[cell]) return cell;
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int offset : pd.mechanics.pathfind.PathFinder.NEIGHBOURS8) {
			int candidate = cell + offset;
			if (Dungeon.level.insideMap(candidate) && Dungeon.level.passable[candidate]
					&& !Dungeon.level.pit[candidate] && Actor.findChar(candidate) == null) {
				candidates.add(candidate);
			}
		}
		return candidates.isEmpty() ? -1 : Random.element(candidates);
	}

	public Mob summonAt(int cell) {
		Mob cat = Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.LEADER
				? new ButterCat2() : new ButterCat();
		cat.pos = cell;
		cat.state = cat.HUNTING;
		GameScene.add(cat, 1f);
		Dungeon.level.occupyCell(cat);
		return cat;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }

	public static class ButterCat extends Mob {
		{
		spriteClass = ErrorSprite.class;
			alignment = Alignment.ALLY;
			state = HUNTING;
			HP = HT = 200;
			defenseSkill = 0;
			intelligentAlly = true;
			properties.add(Property.BEAST);
		}

		@Override public int drRoll() { return 0; }

		@Override
		protected boolean act() {
			damage(1, this);
			supportHero();
			return super.act();
		}

		public void supportHero() {
			if (Dungeon.hero != null && Dungeon.level.adjacent(pos, Dungeon.hero.pos)) {
				Dungeon.hero.belongings.reloadGuns();
				Buff.prolong(Dungeon.hero, Recharging.class, 2f);
			}
		}

		@Override
		protected boolean getCloser(int target) {
			return super.getCloser(Dungeon.hero == null ? target : Dungeon.hero.pos);
		}

		@Override protected Char chooseEnemy() { return null; }
		@Override public boolean add(Buff buff) { return false; }
	}

	public static class ButterCat2 extends Mob {
		{
			spriteClass = ErrorSprite.class;
			alignment = Alignment.ALLY;
			state = HUNTING;
			HP = HT = 400;
			defenseSkill = 20;
			intelligentAlly = true;
			properties.add(Property.BEAST);
		}

		@Override public int drRoll() { return 0; }

		@Override
		protected boolean act() {
			damage(1, this);
			supportHero();
			return super.act();
		}

		public void supportHero() {
			if (Dungeon.hero != null && Dungeon.level.adjacent(pos, Dungeon.hero.pos)) {
				Dungeon.hero.belongings.reloadGuns();
				Buff.prolong(Dungeon.hero, Recharging.class, 2f);
				Buff.affect(Dungeon.hero, ShieldArmor.class).level(30);
			}
		}

		@Override
		protected boolean getCloser(int target) {
			if (Dungeon.hero != null && (state == WANDERING
					|| Dungeon.level.distance(target, Dungeon.hero.pos) > 6)) {
				this.target = target = Dungeon.hero.pos;
			}
			return super.getCloser(target);
		}

		@Override
		protected Char chooseEnemy() {
			if (enemy == null || !enemy.isAlive()) {
				HashSet<Mob> enemies = new HashSet<>();
				for (Mob mob : Dungeon.level.mobs) {
					if (mob.alignment == Alignment.ENEMY && fieldOfView != null && fieldOfView[mob.pos]) {
						enemies.add(mob);
					}
				}
				enemy = enemies.isEmpty() ? null : Random.element(enemies);
			}
			return enemy;
		}

		@Override protected boolean canAttack(Char enemy) { return Dungeon.level.distance(pos, enemy.pos) <= 4; }
		@Override public int attackSkill(Char target) { return 60 + Dungeon.legacyDepth(); }
		@Override public int damageRoll() { return Random.NormalIntRange(Dungeon.legacyDepth() + 12, Dungeon.legacyDepth() + 25); }
		@Override public boolean add(Buff buff) { return false; }
	}
}
