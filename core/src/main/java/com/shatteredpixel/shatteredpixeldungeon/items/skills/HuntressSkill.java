/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.skills;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Mtree;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.reward.BoundReward;
import com.shatteredpixel.shatteredpixeldungeon.items.summon.FairyCard;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** The four huntress class skills from SPS-PD 0.9.8. */
public class HuntressSkill extends ClassSkill {
	{ image = ItemSpriteSheet.ARMOR_HUNTRESS; }

	@Override public void doSpecial() {
		Buff.prolong(curUser, TargetShoot.class, 50f);
		Buff.prolong(curUser, Needling.class, 50f);
		if (curUser.lvl > 55) {
			Buff.affect(curUser, FireImbue.class).set(50f);
			Buff.prolong(curUser, EarthImbue.class, 50f);
			Buff.prolong(curUser, FrostImbue.class, 50f);
		} else {
			switch (Random.Int(3)) {
				case 0: Buff.affect(curUser, FireImbue.class).set(50f); break;
				case 1: Buff.prolong(curUser, EarthImbue.class, 50f); break;
				default: Buff.prolong(curUser, FrostImbue.class, 50f); break;
			}
		}
		addCooldown(15);
		finishSkillCast();
	}

	@Override public void doSpecial2() {
		if (curUser.lvl > 55) {
			Item[] equipped = {curUser.belongings.weapon, curUser.belongings.armor, curUser.belongings.misc};
			for (Item item : equipped) {
				if (item != null && !item.isReinforced()) { item.reinforce(); break; }
			}
		}
		dropAtHero(new BoundReward());
		addCooldown(10);
		finishSkillCast();
	}

	@Override public void doSpecial3() {
		for (int i = 0; i < 3; i++) dropAtHero(Generator.random(Generator.Category.ARROWS));
		ArrayList<Integer> spawnPoints = adjacentSpawnPoints();
		if (!spawnPoints.isEmpty()) {
			Mob fairy = curUser.lvl > 55 ? new FairyCard.SugarplumFairy() : new FairyCard.Fairy();
			fairy.pos = Random.element(spawnPoints);
			GameScene.add(fairy);
		}
		addCooldown(15);
		finishSkillCast();
	}

	private ArrayList<Integer> adjacentSpawnPoints() {
		ArrayList<Integer> result = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = curUser.pos + offset;
			if (Dungeon.level.insideMap(cell) && Actor.findChar(cell) == null
					&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])) result.add(cell);
		}
		return result;
	}

	@Override public void doSpecial4() {
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (visibleMob(mob, 10)) {
				Buff.prolong(mob, Roots.class, 8f);
				Buff.affect(mob, GrowSeed.class).set(10f);
			}
		}
		ArrayList<Integer> spawnPoints = adjacentSpawnPoints();
		if (!spawnPoints.isEmpty()) {
			Mtree tree = new Mtree();
			tree.pos = Random.element(spawnPoints);
			GameScene.add(tree);
		}
		addCooldown(20);
		finishSkillCast();
	}
}
