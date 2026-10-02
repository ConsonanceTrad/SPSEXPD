/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bombs;

import pd.atlas.items.EquipmentEquipWeaponBombDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.CellEmitter;
import pd.effects.particles.SmokeParticle;
import pd.items.Item;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.utils.math.Random;
import pd.messages.InlineText;

public class DungeonBomb extends Bomb {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DungeonBomb.class)
			.t("name", "地牢炸弹")
			.t("desc", "装填大量黑火药的炸弹，会伤害附近所有目标并摧毁相邻墙壁。")
			.t("doublebomb.name", "一对地牢炸弹")
			.t("doublebomb.desc", "两枚重型地牢炸弹，看起来第二枚是免费赠送的。");
	}


	{ image = EquipmentEquipWeaponBombDict.BOMB_0; }

	@Override
	public void explode(int cell) {
		super.explode(cell);
		boolean terrainAffected = false;
		for (int offset : PathFinder.NEIGHBOURS9) {
			int target = cell + offset;
			if (target < 0 || target >= Dungeon.level.length() || !Dungeon.level.insideMap(target)) continue;
			if (GameScene.hasActiveScene() && Dungeon.level.heroFOV[target]) CellEmitter.get(target).burst(SmokeParticle.FACTORY, 4);
			if (Dungeon.level.flamable[target]) {
				Level.set(target, Terrain.EMBERS, Dungeon.level);
				GameScene.updateMap(target);
				terrainAffected = true;
			} else if (Dungeon.level.map[target] == Terrain.WALL || Dungeon.level.map[target] == Terrain.WALL_DECO) {
				Level.set(target, Terrain.EMPTY, Dungeon.level);
				GameScene.updateMap(target);
				terrainAffected = true;
			}
			Char ch = Actor.findChar(target);
			if (ch == null || !ch.isAlive()) continue;
			int min = Char.hasProp(ch, Char.Property.BOSS) || Char.hasProp(ch, Char.Property.MINIBOSS) ? ch.HT / 10 : ch.HT / 5;
			int max = Char.hasProp(ch, Char.Property.BOSS) || Char.hasProp(ch, Char.Property.MINIBOSS) ? ch.HT / 5 : ch.HT / 4;
			int damage = Random.NormalIntRange(min, Math.max(min, max));
			if (Char.hasProp(ch, Char.Property.BOSS) || Char.hasProp(ch, Char.Property.MINIBOSS)) damage -= Math.max(ch.drRoll(), 0);
			if (damage > 0) ch.damage(damage, this);
		}
		if (terrainAffected) Dungeon.observe();
	}

	@Override public Item random() { return Random.Int(2) == 0 ? this : new DoubleBomb(); }
	@Override public int value() { return 10 * quantity; }

	public static class DoubleBomb extends DungeonBomb {
		{ image = EquipmentEquipWeaponBombDict.DBL_BOMB_0; stackable = false; }
		@Override public boolean doPickUp(pd.actors.hero.Hero hero, int pos) {
			DungeonBomb bomb = new DungeonBomb();
			bomb.quantity(2);
			return bomb.doPickUp(hero, pos);
		}
	}
}
