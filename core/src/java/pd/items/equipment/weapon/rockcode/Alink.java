/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.MirrorImage;
import pd.effects.MagicMissile;
import pd.mechanics.Ballistica;
import pd.scenes.GameScene;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class Alink extends RockCode {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Alink.class)
			.t("name", "长老链接")
			.t("desc", "来自星灵长老的技能芯片，在目标附近制造强力镜像。")
			.t("stats_desc", "消耗4点能量中的1点，最多召唤两个镜像。");
	}



	{ collisionProperties = Ballistica.PROJECTILE; sname = "A.l"; }
	@Override protected int missileType() { return MagicMissile.WOOL; }
	@Override protected void onZap(Ballistica bolt) {
		if (curUser != null) spawnImages(curUser, bolt.collisionPos, 2);
	}
	@Override public void onMeleeHit(pd.items.equipment.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (attacker instanceof Hero && Dungeon.level != null && Random.Int(10) == 1)
			spawnImages((Hero)attacker, attacker.pos, 1);
	}
	private static int spawnImages(Hero hero, int center, int count) {
		ArrayList<Integer> cells = new ArrayList<>();
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (Dungeon.level.insideMap(cell) && Dungeon.level.distance(center, cell) <= 2
					&& Dungeon.level.passable[cell] && Actor.findChar(cell) == null) cells.add(cell);
		}
		int spawned = 0;
		while (spawned < count && !cells.isEmpty()) {
			int index = Random.Int(cells.size());
			MirrorImage image = new MirrorImage();
			image.duplicate(hero);
			image.pos = cells.remove(index);
			GameScene.add(image);
			if (!Actor.all().contains(image)) Actor.add(image);
			Dungeon.level.occupyCell(image);
			spawned++;
		}
		return spawned;
	}
}
