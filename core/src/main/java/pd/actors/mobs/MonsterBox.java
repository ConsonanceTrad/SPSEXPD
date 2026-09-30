/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.effects.Pushing;
import pd.items.Item;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.MonsterBoxSprite;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

import java.util.ArrayList;

/** A living copy-box which fights like the hero and drops one copied equipment type. */
public class MonsterBox extends Mob {

	{
		spriteClass = MonsterBoxSprite.class;
		EXP = 1;
		maxLvl = -1;
		properties.add(Property.OBJECT);
		properties.add(Property.UNKNOW);
	}

	public MonsterBox() {
		Hero hero = Dungeon.hero;
		HP = HT = hero == null ? 20 : hero.HT;
		defenseSkill = hero == null ? 5 : hero.defenseSkill(null);
	}

	@Override public int damageRoll() {
		return Dungeon.hero == null ? 1 : Dungeon.hero.damageRoll();
	}

	@Override public int attackSkill(Char target) {
		return Dungeon.hero == null ? 10 : Dungeon.hero.attackSkill(target);
	}

	@Override public boolean reset() {
		state = WANDERING;
		return true;
	}

	@Override public void die(Object cause) {
		super.die(cause);
		Hero hero = Dungeon.hero;
		if (hero == null || Dungeon.level == null) return;
		Item equipped;
		switch (Random.Int(5)) {
			case 0: equipped = hero.belongings.weapon; break;
			case 1: equipped = hero.belongings.armor; break;
			case 2: equipped = hero.belongings.artifact; break;
			case 3: equipped = hero.belongings.misc; break;
			default: equipped = hero.belongings.ring; break;
		}
		if (equipped != null) {
			Item copy = Reflection.newInstance(equipped.getClass());
			if (copy != null) Dungeon.level.drop(copy, pos);
		}
	}

	public static MonsterBox spawnAt(int pos) {
		if (Dungeon.level == null) return null;
		Char occupant = Actor.findChar(pos);
		if (occupant != null) {
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int offset : PathFinder.NEIGHBOURS8) {
				int cell = pos + offset;
				if (Dungeon.level.insideMap(cell) && Actor.findChar(cell) == null
						&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])) {
					candidates.add(cell);
				}
			}
			if (candidates.isEmpty()) return null;
			int destination = Random.element(candidates);
			Actor.addDelayed(new Pushing(occupant, occupant.pos, destination), -1f);
			occupant.move(destination);
		}

		MonsterBox box = new MonsterBox();
		box.pos = pos;
		box.state = box.HUNTING;
		GameScene.add(box, 1f);
		if (box.sprite != null && Dungeon.hero != null) box.sprite.turnTo(pos, Dungeon.hero.pos);
		return box;
	}

	@Override public float spawningWeight() { return 0f; }
}
