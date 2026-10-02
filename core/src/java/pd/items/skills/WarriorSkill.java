/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.skills;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.*;
import pd.actors.mobs.Mob;
import pd.actors.mobs.pets.LegacyPet;
import pd.items.Generator;
import pd.items.Item;
import pd.mechanics.pathfind.PathFinder;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentNonEquipDict;

/** The four warrior class skills from SPS-PD 0.9.8. */
public class WarriorSkill extends ClassSkill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WarriorSkill.class)
			.t("name", "战士技能")
			.t("ac_special", "武装")
			.t("ac_special_two", "决斗")
			.t("ac_special_three", "圣盾术")
			.t("ac_special_four", "奇袭战术")
			.t("desc", "_武装：_生成一件+5武器或防具，并暂时提高2点力量。达到56级后同时生成两件并将其破阶。\n\n_决斗（21级）：_削弱附近敌人，并缴械、沉默视野内的远处敌人。达到56级后大幅强化自身攻防。\n\n_圣盾术（31级）：_治疗伙伴，获得物理护盾，伤害相邻敌人并清除部分负面状态。达到56级后额外获得魔法与能量护盾。\n\n_奇袭战术（41级）：_获得鲜血灌注，并对满血或濒死敌人造成额外伤害。达到56级后永久获得1点生命上限，并暂时提高20%%生命上限。");
	}



	{ image = EquipmentNonEquipDict.HERO_SKILL_WARRIOR; }

	@Override public void doSpecial() {
		Buff.prolong(curUser, Muscle.class, 160f);
		if (curUser.lvl > 55) {
			dropEquipment(Generator.Category.MELEEWEAPON, true);
			dropEquipment(Generator.Category.ARMOR, true);
		} else {
			dropEquipment(Random.Int(2) == 0 ? Generator.Category.MELEEWEAPON : Generator.Category.ARMOR, false);
		}
		addCooldown(20);
		finishSkillCast();
	}

	private void dropEquipment(Generator.Category category, boolean reinforce) {
		Item item = Generator.random(category);
		if (item == null) return;
		item.upgrade(5).uncurse().identify();
		if (reinforce) item.reinforce();
		dropAtHero(item);
	}

	@Override public void doSpecial2() {
		int seen = 0;
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
			if (!visibleMob(mob, Integer.MAX_VALUE)) continue;
			seen++;
			if (Dungeon.level.distance(curUser.pos, mob.pos) <= 3) {
				Buff.prolong(mob, AttackDown.class, 20f).level(10);
			} else {
				Buff.prolong(mob, Disarm.class, curUser.STR());
				Buff.prolong(mob, Silent.class, curUser.STR());
			}
		}
		Buff.prolong(curUser, DefenceUp.class, Math.max(1, seen) * 5f).level(50);
		if (curUser.lvl > 55) {
			Buff.prolong(curUser, AttackUp.class, 80f).level(75);
			Buff.prolong(curUser, DefenceUp.class, 80f).level(75);
		}
		addCooldown(15);
		finishSkillCast();
	}

	@Override public void doSpecial3() {
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
			if (mob instanceof LegacyPet) mob.HP = Math.min(mob.HT, mob.HP + mob.HT / 2);
		}
		Buff.affect(curUser, ShieldArmor.class).level(curUser.HT / 2);
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = curUser.pos + offset;
			if (cell < 0 || cell >= Dungeon.level.length()) continue;
			Char ch = Actor.findChar(cell);
			if (ch != null && ch != curUser && ch.alignment != Char.Alignment.ALLY) ch.damage(curUser.HT / 2, this);
		}
		Buff.detach(curUser, Poison.class);
		Buff.detach(curUser, Cripple.class);
		Buff.detach(curUser, STRDown.class);
		Buff.detach(curUser, BeOld.class);
		if (curUser.lvl > 55) {
			Buff.affect(curUser, MagicArmor.class).level(curUser.HT / 2);
			Buff.affect(curUser, EnergyArmor.class).level(curUser.HT / 2);
		}
		addCooldown(25);
		finishSkillCast();
	}

	@Override public void doSpecial4() {
		Buff.prolong(curUser, BloodImbue.class, 50f);
		Buff.prolong(curUser, SpAttack.class, 50f);
		if (curUser.lvl > 55) {
			curUser.HTBoost++;
			Buff.prolong(curUser, HTimprove.class, 50f);
			curUser.updateHT(true);
		}
		addCooldown(10);
		finishSkillCast();
	}
}
