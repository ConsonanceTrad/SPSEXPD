/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.skills;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Badges;
import pd.Dungeon;
import pd.actors.buffs.*;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.NPC;
import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.bags.Bag;
import pd.scenes.GameScene;
import pd.windows.WndBag;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The four follower class skills from SPS-PD 0.9.8. */
public class FollowerSkill extends ClassSkill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FollowerSkill.class)
			.t("name", "信徒技能")
			.t("ac_special", "祈祷")
			.t("ac_special_two", "资金募集")
			.t("ac_special_three", "渎神")
			.t("ac_special_four", "祝福术")
			.t("desc", "_祈祷：_保持原地会逐回合提高攻击与伤害减免，强度过高后消耗金币。达到56级后蓄力速度翻倍。\n\n_资金募集（21级）：_召集全部生物，并根据其数量获得随机施舍。达到56级后同时获得金币。\n\n_渎神（31级）：_消耗40点永久生命上限，换取力量、攻击、闪避、魔力和永久伤害提升。达到56级后获得两层伤害提升。\n\n_祝福术（41级）：_强化一件选中的装备。达到56级后同时解除其诅咒。");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; }

	@Override public void doSpecial() {
		Buff.affect(curUser, ParyAttack.class);
		addCooldown(15);
		finishSkillCast();
	}

	@Override public void doSpecial2() {
		int people = 0;
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
			mob.beckon(curUser.pos);
			if (!(mob instanceof NPC)) people++;
		}
		if (curUser.lvl > 55) Dungeon.gold += people * (curUser.lvl / 10 + 100);
		for (int i = 0; i < people; i++) if (Random.Int(4) == 0) dropAtHero(Generator.random());
		addCooldown(20);
		finishSkillCast();
	}

	@Override public void doSpecial3() {
		if (curUser.spendPermanentHT(40)) {
			curUser.STR++;
			curUser.improveAttackSkill(1);
			curUser.improveDefenseSkill(1);
			curUser.improveMagicSkill(1);
			Buff.affect(curUser, Blasphemy.class).level(curUser.lvl > 55 ? 2 : 1);
		} else {
			Buff.prolong(curUser, AttackUp.class, 50f).level(50);
			Buff.prolong(curUser, ArmorBreak.class, 50f).level(50);
		}
		addCooldown(15);
		finishSkillCast();
	}

	@Override public void doSpecial4() { GameScene.selectItem(upgradeSelector); }

	private final WndBag.ItemSelector upgradeSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return name(); }
		@Override public Class<? extends Bag> preferredBag() { return pd.actors.hero.Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) { return item.isUpgradable(); }
		@Override public void onSelect(Item item) {
			if (item == null) return;
			item.upgrade();
			if (curUser.lvl > 55) item.uncurse();
			Badges.validateItemLevelAquired(item);
			addCooldown(40);
			finishSkillCast();
		}
	};
}
