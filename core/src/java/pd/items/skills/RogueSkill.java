/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.skills;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Disarm;
import pd.actors.buffs.GoldTouch;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.HighAttack;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.ItemSteal;
import pd.actors.buffs.MoonFury;
import pd.actors.buffs.Silent;
import pd.actors.mobs.Mob;
import pd.effects.particles.ElmoParticle;
import pd.items.Generator;
import pd.items.Item;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The four rogue class skills from SPS-PD 0.9.8. */
public class RogueSkill extends ClassSkill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RogueSkill.class)
			.t("name", "盗贼技能")
			.t("ac_special", "暗影刺杀")
			.t("ac_special_two", "探云手")
			.t("ac_special_three", "宝石打磨")
			.t("ac_special_four", "信仰之跃")
			.t("desc", "盗贼可以施展四项职业技能。\n\n_暗影刺杀：_进入隐身，随机获得一项伤害增益，并压制附近视野内的敌人。达到56级后同时获得所有伤害增益。\n\n_探云手（21级）：_从敌人身上夺取特殊物品，并通过近战攻击获得金币。达到56级后两种效果的持续时间翻倍。\n\n_宝石打磨（31级）：_制作一枚已鉴定的+5戒指。达到56级后戒指同时破阶。\n\n_信仰之跃（41级）：_在障碍物旁为下一次攻击蓄力。达到56级后蓄力期间同时隐身。");
	}


	private static final float SKILL_TIME = 1f;

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public void doSpecial() {
		addCooldown(15);
		if (curUser.lvl > 55) {
			Buff.affect(curUser, MoonFury.class);
			Buff.affect(curUser, HasteBuff.class, 15f);
			Buff.affect(curUser, AttackUp.class, 15f).level(50);
		} else {
			switch (Random.Int(3)) {
				case 0: Buff.affect(curUser, MoonFury.class); break;
				case 1: Buff.affect(curUser, HasteBuff.class, 15f); break;
				default: Buff.affect(curUser, AttackUp.class, 15f).level(50); break;
			}
		}
		for (Mob mob : Dungeon.level.mobs()) {
			if (mob.pos >= 0 && mob.pos < Dungeon.level.heroFOV.length
					&& Dungeon.level.heroFOV[mob.pos]
					&& Dungeon.level.distance(curUser.pos, mob.pos) <= 10) {
				Buff.affect(mob, Silent.class, 9999f);
				Buff.affect(mob, Disarm.class, 5f);
				Buff.affect(mob, ArmorBreak.class, 10f).level(50);
				Buff.prolong(mob, Blindness.class, 3f);
			}
		}
		finishCast();
	}

	@Override
	public void doSpecial2() {
		addCooldown(20);
		float duration = curUser.lvl > 55 ? 30f : 15f;
		Buff.affect(curUser, ItemSteal.class, duration);
		Buff.affect(curUser, GoldTouch.class, duration);
		finishCast();
	}

	@Override
	public void doSpecial3() {
		Item ring = Generator.random(Generator.Category.RING);
		if (ring != null) {
			ring.identify().uncurse().upgrade(5);
			if (curUser.lvl > 55) ring.reinforce();
			Dungeon.level.drop(ring, curUser.pos).sprite.drop(curUser.pos);
		}
		addCooldown(20);
		finishCast();
	}

	@Override
	public void doSpecial4() {
		addCooldown(20);
		Buff.affect(curUser, HighAttack.class);
		finishCast();
	}

	private void finishCast() {
		if (curUser.sprite != null) {
			curUser.spend(SKILL_TIME);
			curUser.busy();
			curUser.sprite.centerEmitter().burst(ElmoParticle.FACTORY, 4);
			curUser.sprite.operate(curUser.pos);
		} else {
			curUser.spendAndNext(SKILL_TIME);
		}
		Sample.INSTANCE.play(Assets.Sounds.READ);
	}
}
