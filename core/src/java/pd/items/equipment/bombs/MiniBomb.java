/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bombs;

import pd.atlas.items.EquipmentEquipWeaponBombDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.scenes.GameScene;
import render.utils.math.Random;
import pd.messages.InlineText;

public class MiniBomb extends Bomb {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MiniBomb.class)
			.t("name", "迷你炸弹")
			.t("desc", "一枚紧凑的小型炸弹，会在爆炸中心额外造成集中伤害。");
	}



	{ image = EquipmentEquipWeaponBombDict.SPS_MINI_BOMB; }

	@Override public void explode(int cell) {
		super.explode(cell);
		if (Dungeon.level == null || !Dungeon.level.insideMap(cell)) return;
		boolean terrainAffected = false;
		if (Dungeon.level.flamable[cell]) {
			Level.set(cell, Terrain.EMBERS, Dungeon.level);
			GameScene.updateMap(cell);
			terrainAffected = true;
		}
		Char target = Actor.findChar(cell);
		if (target != null && target.isAlive()) {
			int min = Math.max(0, target.HT / 15);
			int max = Math.max(min, target.HT / 6);
			int damage = Random.NormalIntRange(min, max) - Math.max(0, target.drRoll());
			if (damage > 0) target.damage(damage, this);
		}
		if (terrainAffected) Dungeon.observe();
	}

	@Override public int value() { return 10 * quantity; }
}
