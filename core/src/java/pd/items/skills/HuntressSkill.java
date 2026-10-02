/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.skills;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.*;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.Mtree;
import pd.items.Generator;
import pd.items.Item;
import pd.items.specific.reward.BoundReward;
import pd.items.summon.FairyCard;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

/** The four huntress class skills from SPS-PD 0.9.8. */
public class HuntressSkill extends ClassSkill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HuntressSkill.class)
			.t("name", "猎手技能")
			.t("ac_special", "狩猎本能")
			.t("ac_special_two", "遗迹之光")
			.t("ac_special_three", "自然之助")
			.t("ac_special_four", "战争古树")
			.t("desc", "_狩猎本能：_获得瞄准射击、针刺和一种随机元素灌注。达到56级后同时获得全部元素灌注。\n\n_遗迹之光（21级）：_获得一个露珠奖励包。达到56级后破阶首件符合条件的装备。\n\n_自然之助（31级）：_获得三件投射武器并召唤一只精灵。达到56级后召唤更强的糖梅精灵。\n\n_战争古树（41级）：_缠绕附近视野内的敌人并使其寄生，随后召唤战争古树。");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }

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
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
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
