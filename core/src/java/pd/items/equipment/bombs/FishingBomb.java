/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bombs;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.NPC;
import pd.effects.CellEmitter;
import pd.effects.particles.SmokeParticle;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.data.BArray;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBombDict;

public class FishingBomb extends Bomb {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FishingBomb.class)
			.t("name", "鱼饵炸弹")
			.t("desc", "这枚炸弹会把附近的敌对生物转移到干燥地面。")
			.t("no_tp", "这里已经没有可用的干燥地面了。")
			.t("tp", "有什么东西被拉到了干燥地面。");
	}




	{
		image = EquipmentEquipWeaponBombDict.SPS_FISHING_BOMB;
	}

	public FishingBomb() { this(1); }
	public FishingBomb(int quantity) { this.quantity = quantity; }

	@Override public void explode(int cell) {
		super.explode(cell);
		PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 2);
		for (int target = 0; target < PathFinder.distance.length; target++) {
			if (PathFinder.distance[target] == Integer.MAX_VALUE) continue;
			if (Dungeon.level.heroFOV[target]) {
				CellEmitter.get(target).burst(SmokeParticle.FACTORY, 4);
			}
			Char ch = Actor.findChar(target);
			if (!(ch instanceof Mob) || ch instanceof NPC) continue;
			int destination = randomDryCell();
			if (destination == -1) {
				GLog.w(Messages.get(this, "no_tp"));
				continue;
			}
			ch.pos = destination;
			if (ch.sprite != null) {
				ch.sprite.place(destination);
				ch.sprite.visible = Dungeon.level.heroFOV[destination];
			}
			GLog.i(Messages.get(this, "tp"));
		}
		Dungeon.observe();
	}

	private int randomDryCell() {
		for (int tries = 0; tries < 200; tries++) {
			int cell = Random.Int(Dungeon.level.length());
			int terrain = Dungeon.level.map[cell];
			if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null
					&& (terrain == Terrain.EMPTY || terrain == Terrain.EMPTY_SP
					|| terrain == Terrain.GRASS || terrain == Terrain.HIGH_GRASS)) return cell;
		}
		return -1;
	}

	@Override public FishingBomb random() { return this; }
	@Override public int value() { return 20 * quantity; }
}
