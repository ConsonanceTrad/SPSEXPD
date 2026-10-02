/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.summon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.Mob;
import pd.items.Heap;
import pd.items.equipment.bombs.BuildBomb;
import pd.scenes.GameScene;
import pd.sprites.CocoCatSprite;
import render.utils.math.Random;

public class CallCoconut extends SpsSummonItem {
	private boolean summonOnThrow;

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_ACTIVE.equals(action)) {
			summonOnThrow = true;
			beginActivation(hero);
		} else {
			summonOnThrow = false;
			super.execute(hero, action);
		}
	}

	@Override
	protected void onThrow(int cell) {
		if (!summonOnThrow || Dungeon.level == null) {
			summonOnThrow = false;
			super.onThrow(cell);
			return;
		}
		int destination = summonCell(cell);
		if (destination < 0) {
			int recovery = Dungeon.hero == null ? cell : Dungeon.hero.pos;
			Heap heap = Dungeon.level.drop(this, recovery);
			if (heap.sprite != null) heap.sprite.drop(cell);
		} else {
			summonAt(destination);
		}
		summonOnThrow = false;
	}

	public Mob summonAt(int cell) {
		CoconutAlly coconut = Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.LEADER
				? new EXcococat() : new Scococat();
		coconut.pos = cell;
		coconut.state = coconut.HUNTING;
		GameScene.add(coconut, 1f);
		Dungeon.level.occupyCell(coconut);
		return coconut;
	}

	@Override public int value() { return 100 * quantity; }

	public abstract static class CoconutAlly extends Mob {
		protected int bombDenominator;
		{
			spriteClass = CocoCatSprite.class;
			alignment = Alignment.ALLY;
			state = HUNTING;
			intelligentAlly = false;
			properties.add(Property.BEAST);
		}

		@Override public int drRoll() { return 0; }

		@Override protected boolean act() {
			decayTurn();
			return super.act();
		}

		public void decayTurn() {
			damage(1, this);
		}

		@Override protected boolean canAttack(Char enemy) {
			return Dungeon.level != null && enemy != null && Dungeon.level.distance(pos, enemy.pos) <= 4;
		}

		public boolean inRange(Char enemy) { return canAttack(enemy); }
		public int bombDenominator() { return bombDenominator; }

		@Override public int attackProc(Char enemy, int damage) {
			if (enemy != null && Random.Int(bombDenominator) == 1) new BuildBomb().explode(enemy.pos);
			return damage;
		}

		@Override public boolean add(Buff buff) { return false; }
	}

	public static class Scococat extends CoconutAlly {
		{
			HP = HT = 200;
			defenseSkill = 0;
			bombDenominator = 10;
		}
		@Override public int attackSkill(Char target) { return 40 + Dungeon.legacyDepth(); }
		@Override public int damageRoll() {
			return Random.NormalIntRange(Dungeon.legacyDepth() + 10, Dungeon.legacyDepth() + 16);
		}
	}

	public static class EXcococat extends CoconutAlly {
		{
			HP = HT = 400;
			defenseSkill = 20;
			bombDenominator = 5;
		}
		@Override public int attackSkill(Char target) { return 60 + Dungeon.legacyDepth(); }
		@Override public int damageRoll() {
			return Random.NormalIntRange(Dungeon.legacyDepth() + 20, Dungeon.legacyDepth() + 32);
		}
	}
}
