/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Alink extends RockCode {
	{ collisionProperties = Ballistica.PROJECTILE; sname = "A.l"; }
	@Override protected int missileType() { return MagicMissile.WOOL; }
	@Override protected void onZap(Ballistica bolt) {
		if (curUser != null) spawnImages(curUser, bolt.collisionPos, 2);
	}
	@Override public void onMeleeHit(com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
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
