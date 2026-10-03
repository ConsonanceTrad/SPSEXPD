/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.skills;

import pd.atlas.items.EquipmentNonEquipDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.*;
import pd.actors.damagetype.SpsMagicDamage;
import pd.items.Generator;
import pd.items.GreatRune;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The four ascetic class skills from SPS-PD 0.9.8. */
public class AsceticSkill extends ClassSkill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AsceticSkill.class)
			.t("name", "修士技能")
			.t("ac_special", "超频")
			.t("ac_special_two", "能量灌注")
			.t("ac_special_three", "重编程")
			.t("ac_special_four", "地震")
			.t("desc", "_超频：_速度翻倍并提高攻击伤害，但受到的伤害略微增加。达到56级后没有冷却。\n\n_能量灌注（21级）：_生成一个高级符文。达到56级后额外生成随机物品。\n\n_重编程（31级）：_消耗1点攻击和闪避，换取2点魔力。达到56级后额外随机获得1点战斗属性。\n\n_地震（41级）：_摧毁附近墙壁、伤害敌人，并在自身周围建造门。达到56级后额外致盲并扰乱敌人。");
	}



	{ image = EquipmentNonEquipDict.HERO_SKILL_ASCETIC; }

	@Override public void doSpecial() {
		Buff.prolong(curUser, SpeedImbue.class, 40f);
		if (curUser.lvl < 56) addCooldown(10);
		finishSkillCast();
	}

	@Override public void doSpecial2() {
		dropAtHero(new GreatRune());
		if (curUser.lvl > 55) dropAtHero(Generator.random());
		addCooldown(20);
		finishSkillCast();
	}

	@Override public void doSpecial3() {
		curUser.improveAttackSkill(-1);
		curUser.improveDefenseSkill(-1);
		curUser.improveMagicSkill(2);
		if (curUser.lvl > 55) {
			switch (Random.Int(3)) {
				case 0: curUser.improveMagicSkill(1); break;
				case 1: curUser.improveAttackSkill(1); break;
				default: curUser.improveDefenseSkill(1); break;
			}
		}
		addCooldown(30);
		finishSkillCast();
	}

	@Override public void doSpecial4() {
		int damage = Math.round(Dungeon.legacyDepth() * (1f + 0.1f * curUser.magicSkill()));
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (!Dungeon.level.insideMap(cell) || Dungeon.level.distance(curUser.pos, cell) > 2) continue;
			int terrain = Dungeon.level.map[cell];
			if (terrain == Terrain.WALL || terrain == Terrain.WALL_DECO || terrain == Terrain.GLASS_WALL) {
				Level.set(cell, Terrain.EMBERS, Dungeon.level);
				GameScene.updateMap(cell);
			}
			Char ch = Actor.findChar(cell);
			if (ch != null && ch != curUser && ch.alignment != Char.Alignment.ALLY && damage > 0) {
				if (curUser.lvl > 55) {
					Buff.prolong(ch, Vertigo.class, 10f);
					Buff.prolong(ch, Blindness.class, 10f);
				}
				ch.damage(damage, SpsMagicDamage.ENERGY);
			}
		}
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = curUser.pos + offset;
			if (!Dungeon.level.insideMap(cell) || isTransition(Dungeon.level.map[cell])) continue;
			Level.set(cell, Terrain.DOOR, Dungeon.level);
			GameScene.updateMap(cell);
		}
		Dungeon.observe();
		addCooldown(20);
		finishSkillCast();
	}

	private boolean isTransition(int terrain) {
		return terrain == Terrain.ENTRANCE || terrain == Terrain.ENTRANCE_SP || terrain == Terrain.EXIT
				|| terrain == Terrain.LOCKED_EXIT || terrain == Terrain.UNLOCKED_EXIT;
	}
}
