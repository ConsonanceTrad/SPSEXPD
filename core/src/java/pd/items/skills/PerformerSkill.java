/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.skills;

import pd.atlas.items.EquipmentJewelleryArtifactDict;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.actors.damagetype.SpsMagicDamage;
import pd.actors.mobs.Mob;
import pd.items.*;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.artifacts.Artifact;
import pd.items.equipment.bags.Bag;
import pd.items.equipment.bombs.DungeonBomb;
import pd.items.equipment.rings.Ring;
import pd.items.consum.scrolls.ScrollOfTransmutation;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.Weapon;
import pd.levels.GroundItems;
import pd.plants.Plant;
import pd.scenes.GameScene;
import pd.windows.WndBag;
import pd.messages.InlineText;

/** The four performer class skills from SPS-PD 0.9.8. */
public class PerformerSkill extends ClassSkill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PerformerSkill.class)
			.t("name", "演员技能")
			.t("ac_special", "奇异舞步")
			.t("ac_special_two", "谢幕礼花")
			.t("ac_special_three", "魔术手法")
			.t("ac_special_four", "市场研习")
			.t("desc", "_奇异舞步：_魅惑并狂乱视野内的全部敌人，同时强化自身。达到56级后冷却减半。\n\n_谢幕礼花（21级）：_伤害、致盲并减速视野内的全部敌人，在其脚下种下种子。达到56级后额外获得一枚炸弹。\n\n_魔术手法（31级）：_转换一件选中的装备。达到56级后额外获得一个转换球。\n\n_市场研习（41级）：_击杀50个敌对单位后，永久提高攻击、闪避、魔力和生命上限。达到56级后提升翻倍。");
	}

	{ image = EquipmentJewelleryArtifactDict.ARTIFACT_HORN1; }

	@Override public void doSpecial() {
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
			if (!visibleMob(mob, Integer.MAX_VALUE)) continue;
			Buff.affect(mob, Charm.class, 10f).object = curUser.id();
			Buff.prolong(mob, Amok.class, 10f);
			Buff.prolong(mob, HasteBuff.class, 5f);
			Buff.prolong(mob, ArmorBreak.class, 20f).level(50);
		}
		Buff.prolong(curUser, DefenceUp.class, 10f).level(25);
		Buff.prolong(curUser, AttackUp.class, 10f).level(25);
		Buff.prolong(curUser, HighVoice.class, 100f);
		addCooldown(curUser.lvl > 55 ? 10 : 20);
		finishSkillCast();
	}

	@Override public void doSpecial2() {
		Buff.prolong(curUser, HighVoice.class, 100f);
		if (curUser.lvl > 55) dropAtHero(new DungeonBomb());
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
			if (!visibleMob(mob, Integer.MAX_VALUE)) continue;
			int damage = Math.max(1, Math.round(curUser.lvl * (1f + 0.1f * curUser.magicSkill())));
			mob.damage(damage, SpsMagicDamage.ENERGY);
			Item seed = Generator.random(Generator.Category.SEED);
			if (seed instanceof Plant.Seed && Dungeon.level.insideMap(mob.pos)) GroundItems.plant( Dungeon.level, (Plant.Seed) seed, mob.pos);
			if (mob.isAlive()) {
				Buff.prolong(mob, Blindness.class, 10f);
				Buff.prolong(mob, Slow.class, 10f);
			}
		}
		addCooldown(10);
		finishSkillCast();
	}

	@Override public void doSpecial3() { GameScene.selectItem(transmutationSelector); }

	private final WndBag.ItemSelector transmutationSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return name(); }
		@Override public Class<? extends Bag> preferredBag() { return pd.actors.hero.Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) {
			return !item.isEquipped(Dungeon.hero) && (item instanceof Weapon || item instanceof Armor
					|| item instanceof Ring || item instanceof Wand || item instanceof Artifact);
		}
		@Override public void onSelect(Item item) {
			if (item == null) return;
			Item result = ScrollOfTransmutation.changeItem(item);
			if (result == null) return;
			item.detach(Dungeon.hero.belongings.backpack);
			if (!result.collect()) dropAtHero(result);
			Buff.prolong(curUser, HighVoice.class, 100f);
			if (curUser.lvl > 55) dropAtHero(new TransmutationBall());
			addCooldown(20);
			finishSkillCast();
		}
	};

	@Override public void doSpecial4() {
		Buff.prolong(curUser, HighVoice.class, 100f);
		Buff.affect(curUser, LearnSkill.class).set(50);
		addCooldown(20);
		finishSkillCast();
	}
}
