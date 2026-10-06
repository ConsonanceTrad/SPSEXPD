/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd;

import pd.actors.buffs.AscensionChallenge;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.items.Item;
import pd.items.equipment.artifacts.Artifact;
import pd.items.equipment.bags.MagicalHolster;
import pd.items.equipment.bags.PotionBandolier;
import pd.items.equipment.bags.ScrollHolder;
import pd.items.equipment.bags.VelvetPouch;
import pd.items.consum.potions.Potion;
import pd.items.quest.AdventureJournal;
import pd.items.quest.Pickaxe;
import pd.items.ground.remains.RemainsItem;
import pd.items.consum.scrolls.Scroll;
import pd.items.equipment.weapon.curses.Explosive;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.journal.Bestiary;
import pd.journal.Catalog;
import pd.journal.Document;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.utils.GLog;
import render.noosa.Game;
import render.utils.serialize.Bundle;
import render.utils.serialize.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import pd.messages.InlineText;

public class Badges {
	//SPSEXPD: inline Chinese text (generated from messages/misc/zh)
	static {
		InlineText.of(Badges.class)
			.t("endorsed", "取得徽章：%s")
			.t("new", "解锁徽章：%s")
			.t("$badge.monsters_slain_1.title", "新晋怪物猎人")
			.t("$badge.monsters_slain_1.desc", "在一局游戏中击败10个敌人")
			.t("$badge.monsters_slain_2.title", "进阶怪物猎人")
			.t("$badge.monsters_slain_2.desc", "在一局游戏中击败50个敌人")
			.t("$badge.monsters_slain_3.title", "专业怪物猎人")
			.t("$badge.monsters_slain_3.desc", "在一局游戏中击败100个敌人")
			.t("$badge.monsters_slain_4.title", "大师怪物猎人")
			.t("$badge.monsters_slain_4.desc", "在一局游戏中击败250个敌人")
			.t("$badge.monsters_slain_5.title", "宗师怪物猎人")
			.t("$badge.monsters_slain_5.desc", "在一局游戏中击败500个敌人")
			.t("$badge.gold_collected_1.title", "新晋财宝猎人")
			.t("$badge.gold_collected_1.desc", "在一局游戏中收集250金币")
			.t("$badge.gold_collected_2.title", "进阶财宝猎人")
			.t("$badge.gold_collected_2.desc", "在一局游戏中收集1000金币")
			.t("$badge.gold_collected_3.title", "专业财宝猎人")
			.t("$badge.gold_collected_3.desc", "在一局游戏中收集2500金币")
			.t("$badge.gold_collected_4.title", "大师财宝猎人")
			.t("$badge.gold_collected_4.desc", "在一局游戏中收集7500金币")
			.t("$badge.gold_collected_5.title", "宗师财宝猎人")
			.t("$badge.gold_collected_5.desc", "在一局游戏中收集15000金币")
			.t("$badge.level_reached_1.title", "新晋冒险家")
			.t("$badge.level_reached_1.desc", "升到6级")
			.t("$badge.level_reached_2.title", "进阶冒险家")
			.t("$badge.level_reached_2.desc", "升到12级")
			.t("$badge.level_reached_3.title", "专业冒险家")
			.t("$badge.level_reached_3.desc", "升到18级")
			.t("$badge.level_reached_4.title", "大师冒险家")
			.t("$badge.level_reached_4.desc", "升到24级")
			.t("$badge.level_reached_5.title", "宗师冒险家")
			.t("$badge.level_reached_5.desc", "升到30级")
			.t("$badge.all_weapons_identified.title", "武器研究员")
			.t("$badge.all_weapons_identified.desc", "鉴定手册中所有类型的武器")
			.t("$badge.all_armor_identified.title", "护甲研究员")
			.t("$badge.all_armor_identified.desc", "鉴定手册中所有类型的护甲")
			.t("$badge.all_wands_identified.title", "法杖研究员")
			.t("$badge.all_wands_identified.desc", "鉴定手册中所有类型的法杖")
			.t("$badge.all_rings_identified.title", "戒指研究员")
			.t("$badge.all_rings_identified.desc", "鉴定手册中所有类型的戒指")
			.t("$badge.all_artifacts_identified.title", "神器研究员")
			.t("$badge.all_artifacts_identified.desc", "鉴定手册中所有类型的神器")
			.t("$badge.all_potions_identified.title", "药剂研究员")
			.t("$badge.all_potions_identified.desc", "鉴定手册中所有类型的药剂")
			.t("$badge.all_scrolls_identified.title", "卷轴研究员")
			.t("$badge.all_scrolls_identified.desc", "鉴定手册中所有类型的卷轴")
			.t("$badge.all_items_identified.title", "道具专家")
			.t("$badge.all_items_identified.desc", "鉴定手册中所有的物品")
			.t("$badge.all_bags_bought.title", "背包客")
			.t("$badge.learn.title", "完成新手教程")
			.t("$badge.learn.desc", "完成特别惊喜像素地牢的新手教程。")
			.t("$badge.all_bags_bought.desc", "完全扩充你的背包")
			.t("$badge.death_from_fire.title", "燃尽")
			.t("$badge.death_from_fire.desc", "死于火焰")
			.t("$badge.death_from_poison.title", "中毒")
			.t("$badge.death_from_poison.desc", "死于中毒")
			.t("$badge.death_from_gas.title", "窒息")
			.t("$badge.death_from_gas.desc", "死于毒气")
			.t("$badge.death_from_hunger.title", "饿毙")
			.t("$badge.death_from_hunger.desc", "死于饥饿")
			.t("$badge.death_from_falling.title", "变成薄饼")
			.t("$badge.death_from_falling.desc", "死于坠落")
			.t("$badge.death_from_enemy_magic.title", "伏“法”")
			.t("$badge.death_from_enemy_magic.desc", "死于敌方法术攻击")
			.t("$badge.death_from_friendly_magic.title", "闹乌龙")
			.t("$badge.death_from_friendly_magic.desc", "死于你自己的魔法物品")
			.t("$badge.death_from_sacrifice.title", "上好祭品")
			.t("$badge.death_from_sacrifice.desc", "死在献祭之火中")
			.t("$badge.death_from_grim_trap.title", "致命失足")
			.t("$badge.death_from_grim_trap.desc", "死于即死陷阱或解离陷阱")
			.t("$badge.death_from_all.title", "另类死亡爱好者")
			.t("$badge.death_from_all.desc", "解锁所有关于死因的徽章")
			.t("$badge.boss_slain_1.title", "粘液保洁员")
			.t("$badge.boss_slain_1.desc", "击败下水道尽头的Boss")
			.t("$badge.boss_slain_2.title", "典狱长")
			.t("$badge.boss_slain_2.desc", "击败监狱尽头的Boss")
			.t("$badge.boss_slain_3.title", "五金回收工")
			.t("$badge.boss_slain_3.desc", "击败洞穴尽头的Boss")
			.t("$badge.boss_slain_4.title", "弑王者")
			.t("$badge.boss_slain_4.desc", "击败矮人都城尽头的Boss")
			.t("$badge.boss_slain_1_all_classes.title", "千面手")
			.t("$badge.boss_slain_1_all_classes.desc", "以所有职业击败第一个Boss")
			.t("$badge.boss_slain_3_all_subclasses.title", "万事通")
			.t("$badge.boss_slain_3_all_subclasses.desc", "以所有专精后的职业击败第三个Boss")
			.t("$badge.boss_slain_remains.title", "复仇的滋味！")
			.t("$badge.boss_slain_remains.desc", "在持有死于某Boss的英雄的遗物的情况下击败该Boss")
			.t("$badge.strength_attained_1.title", "健身菜鸟")
			.t("$badge.strength_attained_1.desc", "基础力量达到12点")
			.t("$badge.strength_attained_2.title", "健身学徒")
			.t("$badge.strength_attained_2.desc", "基础力量达到14点")
			.t("$badge.strength_attained_3.title", "健身专家")
			.t("$badge.strength_attained_3.desc", "基础力量达到16点")
			.t("$badge.strength_attained_4.title", "健身大佬")
			.t("$badge.strength_attained_4.desc", "基础力量达到18点")
			.t("$badge.strength_attained_5.title", "健身巨佬")
			.t("$badge.strength_attained_5.desc", "基础力量达到20点")
			.t("$badge.food_eaten_1.title", "新晋美食家")
			.t("$badge.food_eaten_1.desc", "在一场游戏中进食10次")
			.t("$badge.food_eaten_2.title", "进阶美食家")
			.t("$badge.food_eaten_2.desc", "在一场游戏中进食20次")
			.t("$badge.food_eaten_3.title", "专业美食家")
			.t("$badge.food_eaten_3.desc", "在一场游戏中进食30次")
			.t("$badge.food_eaten_4.title", "大师美食家")
			.t("$badge.food_eaten_4.desc", "在一场游戏中进食40次")
			.t("$badge.food_eaten_5.title", "宗师美食家")
			.t("$badge.food_eaten_5.desc", "在一场游戏中进食50次")
			.t("$badge.item_level_1.title", "新晋附魔师")
			.t("$badge.item_level_1.desc", "获得一件等级大于等于3的物品")
			.t("$badge.item_level_2.title", "进阶附魔师")
			.t("$badge.item_level_2.desc", "获得一件等级大于等于6的物品")
			.t("$badge.item_level_3.title", "专业附魔师")
			.t("$badge.item_level_3.desc", "获得一件等级大于等于9的物品")
			.t("$badge.item_level_4.title", "大师附魔师")
			.t("$badge.item_level_4.desc", "获得一件等级大于等于12的物品")
			.t("$badge.item_level_5.title", "宗师附魔师")
			.t("$badge.item_level_5.desc", "获得一件等级大于等于15的物品")
			.t("$badge.victory.title", "通关！")
			.t("$badge.victory.desc", "获得Yendor护符")
			.t("$badge.victory_all_classes.title", "全能宗师")
			.t("$badge.victory_all_classes.desc", "以所有职业取得Yendor护符")
			.t("$badge.mastery_combo.title", "角斗士之怒")
			.t("$badge.mastery_combo.desc", "达成十连击")
			.t("$badge.items_crafted_1.title", "炼金学徒")
			.t("$badge.items_crafted_1.desc", "在一局游戏中通过炼金术合成3个物品")
			.t("$badge.items_crafted_2.title", "炼金熟手")
			.t("$badge.items_crafted_2.desc", "在一局游戏中通过炼金术合成8个物品")
			.t("$badge.items_crafted_3.title", "炼金专家")
			.t("$badge.items_crafted_3.desc", "在一局游戏中通过炼金术合成15个物品")
			.t("$badge.items_crafted_4.title", "炼金大师")
			.t("$badge.items_crafted_4.desc", "在一局游戏中通过炼金术合成24个物品")
			.t("$badge.items_crafted_5.title", "炼金宗师")
			.t("$badge.items_crafted_5.desc", "在一局游戏中通过炼金术合成35个物品")
			.t("$badge.no_monsters_slain.title", "和平主义者")
			.t("$badge.no_monsters_slain.desc", "在不击败任何敌人的情况下通过一个楼层")
			.t("$badge.grim_weapon.title", "死神来了")
			.t("$badge.grim_weapon.desc", "使用死神附魔击败一个敌人")
			.t("$badge.piranhas.title", "非主流垂钓")
			.t("$badge.piranhas.desc", "在一场游戏中杀死6只食人鱼")
			.t("$badge.boss_challenge_1.title", "完杀无瑕")
			.t("$badge.boss_challenge_1.desc", "在未触发蓄力攻击与水面治疗的情况下，击败下水道区域Boss")
			.t("$badge.boss_challenge_2.title", "机巧无用")
			.t("$badge.boss_challenge_2.desc", "在不被任何机关陷阱击中的情况下，击败监狱区域Boss")
			.t("$badge.boss_challenge_3.title", "疾行无影")
			.t("$badge.boss_challenge_3.desc", "在过载期间完全规避所有攻击和伤害来源的情况下，击败洞穴区域Boss")
			.t("$badge.boss_challenge_4.title", "掣君无刃")
			.t("$badge.boss_challenge_4.desc", "在不使用武器、戒指、法杖或攻击性法术进行对其直接攻击的情况下，击败都城区域Boss")
			.t("$badge.boss_challenge_5.title", "制霸无敌")
			.t("$badge.boss_challenge_5.desc", "在开启“绝命头目”挑战、且所有恶魔血巢存活的情况下击败最终Boss")
			.t("$badge.games_played_1.title", "新晋地牢人")
			.t("$badge.games_played_1.desc", "进行10场游戏或通关1场游戏")
			.t("$badge.games_played_2.title", "进阶地牢人")
			.t("$badge.games_played_2.desc", "进行25场游戏或通关3场游戏")
			.t("$badge.games_played_3.title", "资深地牢人")
			.t("$badge.games_played_3.desc", "进行50场游戏或通关5场游戏")
			.t("$badge.games_played_4.title", "大师地牢人")
			.t("$badge.games_played_4.desc", "进行200场游戏或通关10场游戏")
			.t("$badge.games_played_5.title", "究极地牢人")
			.t("$badge.games_played_5.desc", "进行1000场游戏或通关25场游戏")
			.t("$badge.high_score_1.title", "新晋角逐者")
			.t("$badge.high_score_1.desc", "在结束游戏时达到5000分或更高")
			.t("$badge.high_score_2.title", "进阶角逐者")
			.t("$badge.high_score_2.desc", "在结束游戏时达到25,000分或更高")
			.t("$badge.high_score_3.title", "专业角逐者")
			.t("$badge.high_score_3.desc", "在结束游戏时达到100,000分或更高")
			.t("$badge.high_score_4.title", "大师角逐者")
			.t("$badge.high_score_4.desc", "在结束游戏时达到250,000分或更高")
			.t("$badge.high_score_5.title", "宗师角逐者")
			.t("$badge.high_score_5.desc", "在结束游戏时达到1,000,000分或更高")
			.t("$badge.researcher_1.title", "新晋调查员")
			.t("$badge.researcher_1.desc", "在日志中解锁40个图鉴条目")
			.t("$badge.researcher_2.title", "进阶调查员")
			.t("$badge.researcher_2.desc", "在日志中解锁80个图鉴条目")
			.t("$badge.researcher_3.title", "专业调查员")
			.t("$badge.researcher_3.desc", "在日志中解锁160个图鉴条目")
			.t("$badge.researcher_4.title", "高级调查员")
			.t("$badge.researcher_4.desc", "在日志中解锁320个图鉴条目")
			.t("$badge.researcher_5.title", "特级调查员")
			.t("$badge.researcher_5.desc", "在日志中解锁所有图鉴条目")
			.t("$badge.catalog_one_equipment.title", "百武精通")
			.t("$badge.catalog_one_equipment.desc", "在日志中解锁每种装备的任一图鉴条目")
			.t("$badge.catalog_potions_scrolls.title", "万物通识")
			.t("$badge.catalog_potions_scrolls.desc", "在一局游戏中鉴定每种药剂和卷轴")
			.t("$badge.all_rare_enemies.title", "王牌猎人")
			.t("$badge.all_rare_enemies.desc", "在日志中解锁十个稀有敌人的图鉴条目")
			.t("$badge.rodney.title", "泯然众人")
			.t("$badge.rodney.desc", "在背景故事的终幕中知晓矮人国王的真名")
			.t("$badge.happy_end.title", "幸福结局")
			.t("$badge.happy_end.desc", "将Yendor护符带出地牢")
			.t("$badge.happy_end_remains.title", "现在...你也...")
			.t("$badge.happy_end_remains.desc", "将一位英雄的遗物带回地表")
			.t("$badge.champion_1.title", "青铜斗士")
			.t("$badge.champion_1.desc", "在启用一个或更多挑战的情况下通关")
			.t("$badge.champion_2.title", "白银斗士")
			.t("$badge.champion_2.desc", "在启用三个或更多挑战的情况下通关")
			.t("$badge.champion_3.title", "黄金斗士")
			.t("$badge.champion_3.desc", "在启用六个或更多挑战的情况下通关")
			.t("$badge.unlock_mage.title", "解锁法师！")
			.t("$badge.unlock_mage.desc", "使用一张升级卷轴以解锁法师")
			.t("$badge.unlock_rogue.title", "解锁盗贼！")
			.t("$badge.unlock_rogue.desc", "在一场游戏中进行十次伏击以解锁盗贼")
			.t("$badge.unlock_huntress.title", "解锁女猎手！")
			.t("$badge.unlock_huntress.desc", "在一场游戏中使用投掷武器命中十次敌人以解锁女猎手")
			.t("$badge.unlock_duelist.title", "解锁决斗家！")
			.t("$badge.unlock_duelist.desc", "不受力量惩罚地装备一个2阶或更高阶的武器以解锁决斗家")
			.t("$badge.unlock_cleric.title", "解锁牧师！")
			.t("$badge.unlock_cleric.desc", "完全净化任何一件被诅咒装备的诅咒以解锁牧师")
			.t("$badge.enemy_hazards.title", "安全隐患")
			.t("$badge.enemy_hazards.desc", "在一局游戏中通过特殊地形(陷阱、植物或深渊)击败10个敌人")
			.t("$badge.many_buffs.title", "异彩缭乱")
			.t("$badge.many_buffs.desc", "同时获有至少10种增益/减益图标")
			.t("$badge.pacifist_ascent.title", "和平凯旋")
			.t("$badge.pacifist_ascent.desc", "将其诅咒未曾减弱的Yendor护符带出地牢")
			.t("$badge.taking_the_mick.title", "你在镐神么")
			.t("$badge.taking_the_mick.desc", "以一把至少20级的镐子对最终boss打出致命一击")
			.t("$badge.victory_random.title", "随机获胜！")
			.t("$badge.victory_random.desc", "在英雄、天赋、专精与护甲技能均为随机的条件下获得Yendor护符");
	}




	public static boolean checkCoconutRescued() {
		return local.contains(Badge.MONSTERS_SLAIN_4);
	}

	public static boolean checkSARRescued() {
		return Catalog.RINGS.totalItems() > 0
				&& Catalog.RINGS.totalSeen() == Catalog.RINGS.totalItems();
	}

	public static boolean checkMOSRescued() {
		return local.contains(Badge.FOOD_EATEN_4);
	}

	public static boolean checkItemRescued() {
		return local.contains(Badge.ITEM_LEVEL_4);
	}

	public static boolean checkFishRescued() {
		return local.contains(Badge.BOSS_SLAIN_3);
	}

	public static boolean checkEggRescued() {
		return Statistics.eggBreak >= 5;
	}

	public static boolean checkTombRescued() {
		return local.contains(Badge.BOSS_SLAIN_4);
	}

	public static boolean checkRainRescued() {
		return local.contains(Badge.LEVEL_REACHED_4);
	}

	public static boolean checkUncleRescued() {
		return local.contains(Badge.ITEMS_CRAFTED_1);
	}

	public static boolean checkOtilukeRescued() {
		if (Dungeon.hero == null) return false;
		AdventureJournal journal = Dungeon.hero.belongings.getItem(AdventureJournal.class);
		return journal != null && journal.isCompleted(7);
	}

	public enum BadgeType {
		HIDDEN, //internal badges used for data tracking
		LOCAL,  //unlocked on a per-run basis and added to overall player profile
		GLOBAL, //unlocked for the save profile only, usually over multiple runs
		JOURNAL //profile-based and also tied to the journal, which means they even unlock in seeded runs
	}

	public enum Badge {
		MASTERY_WARRIOR,
		MASTERY_MAGE,
		MASTERY_ROGUE,
		MASTERY_HUNTRESS,
		MASTERY_DUELIST,
		MASTERY_CLERIC,
		FOUND_RATMOGRIFY,

		//bronze
		UNLOCK_MAGE                 ( 1 ),
		UNLOCK_ROGUE                ( 2 ),
		UNLOCK_HUNTRESS             ( 3 ),
		UNLOCK_DUELIST              ( 4 ),
		UNLOCK_CLERIC               ( 5 ),
		MONSTERS_SLAIN_1            ( 6 ),
		MONSTERS_SLAIN_2            ( 7 ),
		GOLD_COLLECTED_1            ( 8 ),
		GOLD_COLLECTED_2            ( 9 ),
		ITEM_LEVEL_1                ( 10 ),
		LEVEL_REACHED_1             ( 11 ),
		STRENGTH_ATTAINED_1         ( 12 ),
		FOOD_EATEN_1                ( 13 ),
		ITEMS_CRAFTED_1             ( 14 ),
		BOSS_SLAIN_1                ( 15 ),
		CATALOG_ONE_EQUIPMENT       ( 16, BadgeType.JOURNAL ),
		DEATH_FROM_FIRE             ( 17 ),
		DEATH_FROM_POISON           ( 18 ),
		DEATH_FROM_GAS              ( 19 ),
		DEATH_FROM_HUNGER           ( 20 ),
		DEATH_FROM_FALLING          ( 21 ),
		RESEARCHER_1                ( 22, BadgeType.JOURNAL ),
		GAMES_PLAYED_1              ( 23, BadgeType.GLOBAL ),
		HIGH_SCORE_1                ( 24 ),

		//silver
		NO_MONSTERS_SLAIN           ( 32 ),
		BOSS_SLAIN_REMAINS          ( 33 ),
		MONSTERS_SLAIN_3            ( 34 ),
		MONSTERS_SLAIN_4            ( 35 ),
		GOLD_COLLECTED_3            ( 36 ),
		GOLD_COLLECTED_4            ( 37 ),
		ITEM_LEVEL_2                ( 38 ),
		ITEM_LEVEL_3                ( 39 ),
		LEVEL_REACHED_2             ( 40 ),
		LEVEL_REACHED_3             ( 41 ),
		STRENGTH_ATTAINED_2         ( 42 ),
		STRENGTH_ATTAINED_3         ( 43 ),
		FOOD_EATEN_2                ( 44 ),
		FOOD_EATEN_3                ( 45 ),
		ITEMS_CRAFTED_2             ( 46 ),
		ITEMS_CRAFTED_3             ( 47 ),
		BOSS_SLAIN_2                ( 48 ),
		BOSS_SLAIN_3                ( 49 ),
		CATALOG_POTIONS_SCROLLS     ( 50 ),
		DEATH_FROM_ENEMY_MAGIC      ( 51 ),
		DEATH_FROM_FRIENDLY_MAGIC   ( 52 ),
		DEATH_FROM_SACRIFICE        ( 53 ),
		BOSS_SLAIN_1_WARRIOR,
		BOSS_SLAIN_1_MAGE,
		BOSS_SLAIN_1_ROGUE,
		BOSS_SLAIN_1_HUNTRESS,
		BOSS_SLAIN_1_DUELIST,
		BOSS_SLAIN_1_CLERIC,
		BOSS_SLAIN_1_ALL_CLASSES    ( 54, BadgeType.GLOBAL ),
		RESEARCHER_2                ( 55, BadgeType.JOURNAL ),
		GAMES_PLAYED_2              ( 56, BadgeType.GLOBAL ),
		HIGH_SCORE_2                ( 57 ),

		//gold
		ENEMY_HAZARDS               ( 64 ),
		PIRANHAS                    ( 65 ),
		GRIM_WEAPON                 ( 66 ),
		BAG_BOUGHT_VELVET_POUCH,
		BAG_BOUGHT_SCROLL_HOLDER,
		BAG_BOUGHT_POTION_BANDOLIER,
		BAG_BOUGHT_MAGICAL_HOLSTER,
		ALL_BAGS_BOUGHT             ( 67 ),
		MASTERY_COMBO               ( 68 ),
		MONSTERS_SLAIN_5            ( 69 ),
		GOLD_COLLECTED_5            ( 70 ),
		ITEM_LEVEL_4                ( 71 ),
		LEVEL_REACHED_4             ( 72 ),
		STRENGTH_ATTAINED_4         ( 73 ),
		STRENGTH_ATTAINED_5         ( 74 ),
		FOOD_EATEN_4                ( 75 ),
		FOOD_EATEN_5                ( 76 ),
		ITEMS_CRAFTED_4             ( 77 ),
		ITEMS_CRAFTED_5             ( 78 ),
		BOSS_SLAIN_4                ( 79 ),
		ALL_RARE_ENEMIES            ( 80, BadgeType.JOURNAL ), //no longer all, just 10 as of v3.1
		DEATH_FROM_GRIM_TRAP        ( 81 ), //also disintegration traps
		VICTORY                     ( 82 ),
		BOSS_CHALLENGE_1            ( 83 ),
		BOSS_CHALLENGE_2            ( 84 ),
		RESEARCHER_3                ( 85, BadgeType.JOURNAL ),
		GAMES_PLAYED_3              ( 86, BadgeType.GLOBAL ),
		HIGH_SCORE_3                ( 87 ),

		//platinum
		MANY_BUFFS                  ( 96 ),
		ITEM_LEVEL_5                ( 97 ),
		LEVEL_REACHED_5             ( 98 ),
		HAPPY_END                   ( 99 ),
		VICTORY_RANDOM              ( 100 ),
		HAPPY_END_REMAINS           ( 101 ),
		RODNEY                      ( 102, BadgeType.JOURNAL ),
		VICTORY_WARRIOR,
		VICTORY_MAGE,
		VICTORY_ROGUE,
		VICTORY_HUNTRESS,
		VICTORY_DUELIST,
		VICTORY_CLERIC,
		VICTORY_ALL_CLASSES         ( 103, BadgeType.GLOBAL ),
		DEATH_FROM_ALL              ( 104, BadgeType.GLOBAL ),
		BOSS_SLAIN_3_GLADIATOR,
		BOSS_SLAIN_3_BERSERKER,
		BOSS_SLAIN_3_WARLOCK,
		BOSS_SLAIN_3_BATTLEMAGE,
		BOSS_SLAIN_3_FREERUNNER,
		BOSS_SLAIN_3_ASSASSIN,
		BOSS_SLAIN_3_SNIPER,
		BOSS_SLAIN_3_WARDEN,
		BOSS_SLAIN_3_CHAMPION,
		BOSS_SLAIN_3_MONK,
		BOSS_SLAIN_3_PRIEST,
		BOSS_SLAIN_3_PALADIN,
		BOSS_SLAIN_3_ALL_SUBCLASSES ( 105, BadgeType.GLOBAL ),
		BOSS_CHALLENGE_3            ( 106 ),
		BOSS_CHALLENGE_4            ( 107 ),
		RESEARCHER_4                ( 108, BadgeType.JOURNAL ),
		GAMES_PLAYED_4              ( 109, BadgeType.GLOBAL ),
		HIGH_SCORE_4                ( 110 ),
		CHAMPION_1                  ( 111 ),

		//diamond
		PACIFIST_ASCENT             ( 120 ),
		TAKING_THE_MICK             ( 121 ), //This might be the most obscure game reference I've made
		BOSS_CHALLENGE_5            ( 122 ),
		RESEARCHER_5                ( 123, BadgeType.JOURNAL ),
		GAMES_PLAYED_5              ( 124, BadgeType.GLOBAL ),
		HIGH_SCORE_5                ( 125 ),
		CHAMPION_2                  ( 126 ),
		CHAMPION_3                  ( 127 ),
		LEARN                       ( 67, BadgeType.GLOBAL );

		public boolean meta;

		public int image;
		public BadgeType type;

		Badge(){
			this(-1, BadgeType.HIDDEN);
		}

		Badge( int image ) {
			this( image, BadgeType.LOCAL );
		}

		Badge( int image, BadgeType type ) {
			this.image = image;
			this.type = type;
		}

		public String title(){
			return Messages.get(this, name()+".title");
		}

		public String desc(){
			return Messages.get(this, name()+".desc");
		}
	}
	
	private static HashSet<Badge> global;
	private static HashSet<Badge> local = new HashSet<>();
	
	private static boolean saveNeeded = false;

	public static void reset() {
		local.clear();
		loadGlobal();
	}
	
	public static final String BADGES_FILE	= "badges.dat";
	private static final String BADGES		= "badges";
	
	private static final HashSet<String> removedBadges = new HashSet<>();
	static{
		//used only for save conversion since v2.5.0, actually removed in v4.0.0
		removedBadges.add("ALL_WEAPONS_IDENTIFIED");
		removedBadges.add("ALL_ARMOR_IDENTIFIED");
		removedBadges.add("ALL_WANDS_IDENTIFIED");
		removedBadges.add("ALL_RINGS_IDENTIFIED");
		removedBadges.add("ALL_ARTIFACTS_IDENTIFIED");
		removedBadges.add("ALL_POTIONS_IDENTIFIED");
		removedBadges.add("ALL_SCROLLS_IDENTIFIED");
		removedBadges.add("ALL_ITEMS_IDENTIFIED");
	}

	private static final HashMap<String, String> renamedBadges = new HashMap<>();
	static{
		//no renamed badges currently
	}

	public static HashSet<Badge> restore( Bundle bundle ) {
		HashSet<Badge> badges = new HashSet<>();
		if (bundle == null) return badges;
		
		String[] names = bundle.getStringArray( BADGES );
		if (names == null) return badges;

		for (int i=0; i < names.length; i++) {
			try {
				if (renamedBadges.containsKey(names[i])){
					names[i] = renamedBadges.get(names[i]);
				}
				if (!removedBadges.contains(names[i])){
					badges.add( Badge.valueOf( names[i] ) );
				}
			} catch (Exception e) {
				ShatteredPixelDungeon.reportException(e);
			}
		}

		addReplacedBadges(badges);
	
		return badges;
	}
	
	public static void store( Bundle bundle, HashSet<Badge> badges ) {
		addReplacedBadges(badges);

		int count = 0;
		String names[] = new String[badges.size()];
		
		for (Badge badge:badges) {
			names[count++] = badge.name();
		}
		bundle.put( BADGES, names );
	}
	
	public static void loadLocal( Bundle bundle ) {
		local = restore( bundle );
	}
	
	public static void saveLocal( Bundle bundle ) {
		store( bundle, local );
	}
	
	public static void loadGlobal() {
		if (global == null) {
			try {
				Bundle bundle = FileUtils.bundleFromFile( BADGES_FILE );
				global = restore( bundle );

			} catch (IOException e) {
				global = new HashSet<>();
			}
		}
	}

	public static void saveGlobal(){
		saveGlobal(false);
	}

	public static void saveGlobal(boolean force) {
		if (saveNeeded || force) {
			
			Bundle bundle = new Bundle();
			store( bundle, global );
			
			try {
				FileUtils.bundleToFile(BADGES_FILE, bundle);
				saveNeeded = false;
			} catch (IOException e) {
				ShatteredPixelDungeon.reportException(e);
			}
		}
	}

	public static int totalUnlocked(boolean global){
		if (global) return Badges.global.size();
		else        return Badges.local.size();
	}

	public static void validateMonstersSlain() {
		Badge badge = null;
		
		if (!local.contains( Badge.MONSTERS_SLAIN_1 ) && Statistics.enemiesSlain >= 10) {
			badge = Badge.MONSTERS_SLAIN_1;
			local.add( badge );
		}
		if (!local.contains( Badge.MONSTERS_SLAIN_2 ) && Statistics.enemiesSlain >= 50) {
			if (badge != null) unlock(badge);
			badge = Badge.MONSTERS_SLAIN_2;
			local.add( badge );
		}
		if (!local.contains( Badge.MONSTERS_SLAIN_3 ) && Statistics.enemiesSlain >= 100) {
			if (badge != null) unlock(badge);
			badge = Badge.MONSTERS_SLAIN_3;
			local.add( badge );
		}
		if (!local.contains( Badge.MONSTERS_SLAIN_4 ) && Statistics.enemiesSlain >= 250) {
			if (badge != null) unlock(badge);
			badge = Badge.MONSTERS_SLAIN_4;
			local.add( badge );
		}
		if (!local.contains( Badge.MONSTERS_SLAIN_5 ) && Statistics.enemiesSlain >= 500) {
			if (badge != null) unlock(badge);
			badge = Badge.MONSTERS_SLAIN_5;
			local.add( badge );
		}
		
		displayBadge( badge );
	}

	public static void validateLearn() {
		loadGlobal();
		unlock(Badge.LEARN);
		saveGlobal();
	}
	
	public static void validateGoldCollected() {
		Badge badge = null;
		
		if (!local.contains( Badge.GOLD_COLLECTED_1 ) && Statistics.goldCollected >= 250) {
			if (badge != null) unlock(badge);
			badge = Badge.GOLD_COLLECTED_1;
			local.add( badge );
		}
		if (!local.contains( Badge.GOLD_COLLECTED_2 ) && Statistics.goldCollected >= 1000) {
			if (badge != null) unlock(badge);
			badge = Badge.GOLD_COLLECTED_2;
			local.add( badge );
		}
		if (!local.contains( Badge.GOLD_COLLECTED_3 ) && Statistics.goldCollected >= 2500) {
			if (badge != null) unlock(badge);
			badge = Badge.GOLD_COLLECTED_3;
			local.add( badge );
		}
		if (!local.contains( Badge.GOLD_COLLECTED_4 ) && Statistics.goldCollected >= 7500) {
			if (badge != null) unlock(badge);
			badge = Badge.GOLD_COLLECTED_4;
			local.add( badge );
		}
		if (!local.contains( Badge.GOLD_COLLECTED_5 ) && Statistics.goldCollected >= 15_000) {
			if (badge != null) unlock(badge);
			badge = Badge.GOLD_COLLECTED_5;
			local.add( badge );
		}
		
		displayBadge( badge );
	}
	
	public static void validateLevelReached() {
		Badge badge = null;
		
		if (!local.contains( Badge.LEVEL_REACHED_1 ) && Dungeon.hero.lvl >= 6) {
			badge = Badge.LEVEL_REACHED_1;
			local.add( badge );
		}
		if (!local.contains( Badge.LEVEL_REACHED_2 ) && Dungeon.hero.lvl >= 12) {
			if (badge != null) unlock(badge);
			badge = Badge.LEVEL_REACHED_2;
			local.add( badge );
		}
		if (!local.contains( Badge.LEVEL_REACHED_3 ) && Dungeon.hero.lvl >= 18) {
			if (badge != null) unlock(badge);
			badge = Badge.LEVEL_REACHED_3;
			local.add( badge );
		}
		if (!local.contains( Badge.LEVEL_REACHED_4 ) && Dungeon.hero.lvl >= 24) {
			if (badge != null) unlock(badge);
			badge = Badge.LEVEL_REACHED_4;
			local.add( badge );
		}
		if (!local.contains( Badge.LEVEL_REACHED_5 ) && Dungeon.hero.lvl >= 30) {
			if (badge != null) unlock(badge);
			badge = Badge.LEVEL_REACHED_5;
			local.add( badge );
		}
		
		displayBadge( badge );
	}
	
	public static void validateStrengthAttained() {
		Badge badge = null;
		
		if (!local.contains( Badge.STRENGTH_ATTAINED_1 ) && Dungeon.hero.STR >= 12) {
			badge = Badge.STRENGTH_ATTAINED_1;
			local.add( badge );
		}
		if (!local.contains( Badge.STRENGTH_ATTAINED_2 ) && Dungeon.hero.STR >= 14) {
			if (badge != null) unlock(badge);
			badge = Badge.STRENGTH_ATTAINED_2;
			local.add( badge );
		}
		if (!local.contains( Badge.STRENGTH_ATTAINED_3 ) && Dungeon.hero.STR >= 16) {
			if (badge != null) unlock(badge);
			badge = Badge.STRENGTH_ATTAINED_3;
			local.add( badge );
		}
		if (!local.contains( Badge.STRENGTH_ATTAINED_4 ) && Dungeon.hero.STR >= 18) {
			if (badge != null) unlock(badge);
			badge = Badge.STRENGTH_ATTAINED_4;
			local.add( badge );
		}
		if (!local.contains( Badge.STRENGTH_ATTAINED_5 ) && Dungeon.hero.STR >= 20) {
			if (badge != null) unlock(badge);
			badge = Badge.STRENGTH_ATTAINED_5;
			local.add( badge );
		}
		
		displayBadge( badge );
	}
	
	public static void validateFoodEaten() {
		Badge badge = null;
		
		if (!local.contains( Badge.FOOD_EATEN_1 ) && Statistics.foodEaten >= 10) {
			badge = Badge.FOOD_EATEN_1;
			local.add( badge );
		}
		if (!local.contains( Badge.FOOD_EATEN_2 ) && Statistics.foodEaten >= 20) {
			if (badge != null) unlock(badge);
			badge = Badge.FOOD_EATEN_2;
			local.add( badge );
		}
		if (!local.contains( Badge.FOOD_EATEN_3 ) && Statistics.foodEaten >= 30) {
			if (badge != null) unlock(badge);
			badge = Badge.FOOD_EATEN_3;
			local.add( badge );
		}
		if (!local.contains( Badge.FOOD_EATEN_4 ) && Statistics.foodEaten >= 40) {
			if (badge != null) unlock(badge);
			badge = Badge.FOOD_EATEN_4;
			local.add( badge );
		}
		if (!local.contains( Badge.FOOD_EATEN_5 ) && Statistics.foodEaten >= 50) {
			if (badge != null) unlock(badge);
			badge = Badge.FOOD_EATEN_5;
			local.add( badge );
		}
		
		displayBadge( badge );
	}
	
	public static void validateItemsCrafted() {
		Badge badge = null;
		
		if (!local.contains( Badge.ITEMS_CRAFTED_1 ) && Statistics.itemsCrafted >= 3) {
			badge = Badge.ITEMS_CRAFTED_1;
			local.add( badge );
		}
		if (!local.contains( Badge.ITEMS_CRAFTED_2 ) && Statistics.itemsCrafted >= 8) {
			if (badge != null) unlock(badge);
			badge = Badge.ITEMS_CRAFTED_2;
			local.add( badge );
		}
		if (!local.contains( Badge.ITEMS_CRAFTED_3 ) && Statistics.itemsCrafted >= 15) {
			if (badge != null) unlock(badge);
			badge = Badge.ITEMS_CRAFTED_3;
			local.add( badge );
		}
		if (!local.contains( Badge.ITEMS_CRAFTED_4 ) && Statistics.itemsCrafted >= 24) {
			if (badge != null) unlock(badge);
			badge = Badge.ITEMS_CRAFTED_4;
			local.add( badge );
		}
		if (!local.contains( Badge.ITEMS_CRAFTED_5 ) && Statistics.itemsCrafted >= 35) {
			if (badge != null) unlock(badge);
			badge = Badge.ITEMS_CRAFTED_5;
			local.add( badge );
		}
		
		displayBadge( badge );
	}

	public static void validateHazardAssists() {
		if (!local.contains( Badge.ENEMY_HAZARDS ) && Statistics.hazardAssistedKills >= 10) {
			local.add( Badge.ENEMY_HAZARDS );
			displayBadge( Badge.ENEMY_HAZARDS );
		}
	}
	
	public static void validatePiranhasKilled() {
		Badge badge = null;
		
		if (!local.contains( Badge.PIRANHAS ) && Statistics.piranhasKilled >= 6) {
			badge = Badge.PIRANHAS;
			local.add( badge );
		}
		
		displayBadge( badge );
	}
	
	public static void validateItemLevelAquired( Item item ) {
		
		// This method should be called:
		// 1) When an item is obtained (Item.collect)
		// 2) When an item is upgraded (ScrollOfUpgrade, ScrollOfWeaponUpgrade, ShortSword, WandOfMagicMissile)
		// 3) When an item is identified

		// Note that artifacts should never trigger this badge as they are alternatively upgraded
		if (!item.levelKnown || item instanceof Artifact) {
			return;
		}

		if (item instanceof MeleeWeapon){
			validateDuelistUnlock();
		}
		
		Badge badge = null;
		if (!local.contains( Badge.ITEM_LEVEL_1 ) && item.level() >= 3) {
			badge = Badge.ITEM_LEVEL_1;
			local.add( badge );
		}
		if (!local.contains( Badge.ITEM_LEVEL_2 ) && item.level() >= 6) {
			if (badge != null) unlock(badge);
			badge = Badge.ITEM_LEVEL_2;
			local.add( badge );
		}
		if (!local.contains( Badge.ITEM_LEVEL_3 ) && item.level() >= 9) {
			if (badge != null) unlock(badge);
			badge = Badge.ITEM_LEVEL_3;
			local.add( badge );
		}
		if (!local.contains( Badge.ITEM_LEVEL_4 ) && item.level() >= 12) {
			if (badge != null) unlock(badge);
			badge = Badge.ITEM_LEVEL_4;
			local.add( badge );
		}
		if (!local.contains( Badge.ITEM_LEVEL_5 ) && item.level() >= 15) {
			if (badge != null) unlock(badge);
			badge = Badge.ITEM_LEVEL_5;
			local.add( badge );
		}
		
		displayBadge( badge );
	}
	
	public static void validateAllBagsBought( Item bag ) {
		
		Badge badge = null;
		if (bag instanceof VelvetPouch) {
			badge = Badge.BAG_BOUGHT_VELVET_POUCH;
		} else if (bag instanceof ScrollHolder) {
			badge = Badge.BAG_BOUGHT_SCROLL_HOLDER;
		} else if (bag instanceof PotionBandolier) {
			badge = Badge.BAG_BOUGHT_POTION_BANDOLIER;
		} else if (bag instanceof MagicalHolster) {
			badge = Badge.BAG_BOUGHT_MAGICAL_HOLSTER;
		}
		
		if (badge != null) {
			
			local.add( badge );
			
			if (!local.contains( Badge.ALL_BAGS_BOUGHT ) &&
				local.contains( Badge.BAG_BOUGHT_VELVET_POUCH ) &&
				local.contains( Badge.BAG_BOUGHT_SCROLL_HOLDER ) &&
				local.contains( Badge.BAG_BOUGHT_POTION_BANDOLIER ) &&
				local.contains( Badge.BAG_BOUGHT_MAGICAL_HOLSTER )) {
						
					badge = Badge.ALL_BAGS_BOUGHT;
					local.add( badge );
					displayBadge( badge );
			}
		}
	}

	//several badges all tie into catalog completion
	public static void validateCatalogBadges(){
		//Item and mob construction can update catalogs during headless generation or
		//save restoration, before the profile-wide badge set has been loaded.
		if (global == null) return;

		int totalSeen = 0;
		int totalThings = 0;

		for (Catalog cat : Catalog.values()){
			totalSeen += cat.totalSeen();
			totalThings += cat.totalItems();
		}

		for (Bestiary cat : Bestiary.values()){
			totalSeen += cat.totalSeen();
			totalThings += cat.totalEntities();
		}

		for (Document doc : Document.values()){
			if (!doc.isLoreDoc()) {
				for (String page : doc.pageNames()){
					if (doc.isPageFound(page)) totalSeen++;
					totalThings++;
				}
			}
		}

		//overall unlock badges
		Badge badge = null;
		if (totalSeen >= 40) {
			badge = Badge.RESEARCHER_1;
		}
		if (totalSeen >= 80) {
			unlock(badge);
			badge = Badge.RESEARCHER_2;
		}
		if (totalSeen >= 160) {
			unlock(badge);
			badge = Badge.RESEARCHER_3;
		}
		if (totalSeen >= 320) {
			unlock(badge);
			badge = Badge.RESEARCHER_4;
		}
		if (totalSeen == totalThings) {
			unlock(badge);
			badge = Badge.RESEARCHER_5;
		}
		displayBadge( badge );

		//specific task badges

		boolean qualified = true;
		for (Catalog cat : Catalog.equipmentCatalogs) {
			if (cat != Catalog.ENCHANTMENTS && cat != Catalog.GLYPHS) {
				if (cat.totalSeen() == 0) {
					qualified = false;
					break;
				}
			}
		}
		if (qualified) {
			displayBadge(Badge.CATALOG_ONE_EQUIPMENT);
		}

		//doesn't actually use catalogs, but triggers at the same time effectively
		if (!local.contains(Badge.CATALOG_POTIONS_SCROLLS)
				&& Potion.allKnown() && Scroll.allKnown()
				&& Dungeon.hero != null && Dungeon.hero.isAlive()){
			local.add(Badge.CATALOG_POTIONS_SCROLLS);
			displayBadge(Badge.CATALOG_POTIONS_SCROLLS);
		}

		if (Bestiary.RARE.totalSeen() >= 10){
			displayBadge(Badge.ALL_RARE_ENEMIES);
		}

		if (Document.HALLS_KING.isPageRead(Document.KING_ATTRITION)){
			displayBadge(Badge.RODNEY);
		}

	}
	
	public static void validateDeathFromFire() {
		Badge badge = Badge.DEATH_FROM_FIRE;
		local.add( badge );
		displayBadge( badge );
		
		validateDeathFromAll();
	}
	
	public static void validateDeathFromPoison() {
		Badge badge = Badge.DEATH_FROM_POISON;
		local.add( badge );
		displayBadge( badge );
		
		validateDeathFromAll();
	}
	
	public static void validateDeathFromGas() {
		Badge badge = Badge.DEATH_FROM_GAS;
		local.add( badge );
		displayBadge( badge );
		
		validateDeathFromAll();
	}
	
	public static void validateDeathFromHunger() {
		Badge badge = Badge.DEATH_FROM_HUNGER;
		local.add( badge );
		displayBadge( badge );
		
		validateDeathFromAll();
	}

	public static void validateDeathFromFalling() {
		Badge badge = Badge.DEATH_FROM_FALLING;
		local.add( badge );
		displayBadge( badge );

		validateDeathFromAll();
	}

	public static void validateDeathFromEnemyMagic() {
		Badge badge = Badge.DEATH_FROM_ENEMY_MAGIC;
		local.add( badge );
		displayBadge( badge );

		validateDeathFromAll();
	}
	
	public static void validateDeathFromFriendlyMagic() {
		Badge badge = Badge.DEATH_FROM_FRIENDLY_MAGIC;
		local.add( badge );
		displayBadge( badge );

		validateDeathFromAll();
	}

	public static void validateDeathFromSacrifice() {
		Badge badge = Badge.DEATH_FROM_SACRIFICE;
		local.add( badge );
		displayBadge( badge );

		validateDeathFromAll();
	}

	public static void validateDeathFromGrimOrDisintTrap() {
		Badge badge = Badge.DEATH_FROM_GRIM_TRAP;
		local.add( badge );
		displayBadge( badge );

		validateDeathFromAll();
	}
	
	private static void validateDeathFromAll() {
		if (isUnlocked( Badge.DEATH_FROM_FIRE ) &&
				isUnlocked( Badge.DEATH_FROM_POISON ) &&
				isUnlocked( Badge.DEATH_FROM_GAS ) &&
				isUnlocked( Badge.DEATH_FROM_HUNGER) &&
				isUnlocked( Badge.DEATH_FROM_FALLING) &&
				isUnlocked( Badge.DEATH_FROM_ENEMY_MAGIC) &&
				isUnlocked( Badge.DEATH_FROM_FRIENDLY_MAGIC) &&
				isUnlocked( Badge.DEATH_FROM_SACRIFICE) &&
				isUnlocked( Badge.DEATH_FROM_GRIM_TRAP)) {

			Badge badge = Badge.DEATH_FROM_ALL;
			if (!isUnlocked( badge )) {
				displayBadge( badge );
			}
		}
	}

	private static LinkedHashMap<HeroClass, Badge> firstBossClassBadges = new LinkedHashMap<>();
	static {
		firstBossClassBadges.put(HeroClass.WARRIOR, Badge.BOSS_SLAIN_1_WARRIOR);
		firstBossClassBadges.put(HeroClass.MAGE, Badge.BOSS_SLAIN_1_MAGE);
		firstBossClassBadges.put(HeroClass.ROGUE, Badge.BOSS_SLAIN_1_ROGUE);
		firstBossClassBadges.put(HeroClass.HUNTRESS, Badge.BOSS_SLAIN_1_HUNTRESS);
		firstBossClassBadges.put(HeroClass.DUELIST, Badge.BOSS_SLAIN_1_DUELIST);
		firstBossClassBadges.put(HeroClass.CLERIC, Badge.BOSS_SLAIN_1_CLERIC);
		firstBossClassBadges.put(HeroClass.SPELLSWORD, Badge.BOSS_SLAIN_1_MAGE);
		firstBossClassBadges.put(HeroClass.PERFORMER, Badge.BOSS_SLAIN_1_ROGUE);
		firstBossClassBadges.put(HeroClass.SOLDIER, Badge.BOSS_SLAIN_1_DUELIST);
		firstBossClassBadges.put(HeroClass.FOLLOWER, Badge.BOSS_SLAIN_1_CLERIC);
		firstBossClassBadges.put(HeroClass.ASCETIC, Badge.BOSS_SLAIN_1_MAGE);
	}

	private static LinkedHashMap<HeroClass, Badge> victoryClassBadges = new LinkedHashMap<>();
	static {
		victoryClassBadges.put(HeroClass.WARRIOR, Badge.VICTORY_WARRIOR);
		victoryClassBadges.put(HeroClass.MAGE, Badge.VICTORY_MAGE);
		victoryClassBadges.put(HeroClass.ROGUE, Badge.VICTORY_ROGUE);
		victoryClassBadges.put(HeroClass.HUNTRESS, Badge.VICTORY_HUNTRESS);
		victoryClassBadges.put(HeroClass.DUELIST, Badge.VICTORY_DUELIST);
		victoryClassBadges.put(HeroClass.CLERIC, Badge.VICTORY_CLERIC);
		victoryClassBadges.put(HeroClass.SPELLSWORD, Badge.VICTORY_MAGE);
		victoryClassBadges.put(HeroClass.PERFORMER, Badge.VICTORY_ROGUE);
		victoryClassBadges.put(HeroClass.SOLDIER, Badge.VICTORY_DUELIST);
		victoryClassBadges.put(HeroClass.FOLLOWER, Badge.VICTORY_CLERIC);
		victoryClassBadges.put(HeroClass.ASCETIC, Badge.VICTORY_MAGE);
	}

	private static LinkedHashMap<HeroSubClass, Badge> thirdBossSubclassBadges = new LinkedHashMap<>();
	static {
		thirdBossSubclassBadges.put(HeroSubClass.BERSERKER, Badge.BOSS_SLAIN_3_BERSERKER);
		thirdBossSubclassBadges.put(HeroSubClass.GLADIATOR, Badge.BOSS_SLAIN_3_GLADIATOR);
		thirdBossSubclassBadges.put(HeroSubClass.BATTLEMAGE, Badge.BOSS_SLAIN_3_BATTLEMAGE);
		thirdBossSubclassBadges.put(HeroSubClass.WARLOCK, Badge.BOSS_SLAIN_3_WARLOCK);
		thirdBossSubclassBadges.put(HeroSubClass.ASSASSIN, Badge.BOSS_SLAIN_3_ASSASSIN);
		thirdBossSubclassBadges.put(HeroSubClass.FREERUNNER, Badge.BOSS_SLAIN_3_FREERUNNER);
		thirdBossSubclassBadges.put(HeroSubClass.SNIPER, Badge.BOSS_SLAIN_3_SNIPER);
		thirdBossSubclassBadges.put(HeroSubClass.WARDEN, Badge.BOSS_SLAIN_3_WARDEN);
		thirdBossSubclassBadges.put(HeroSubClass.CHAMPION, Badge.BOSS_SLAIN_3_CHAMPION);
		thirdBossSubclassBadges.put(HeroSubClass.MONK, Badge.BOSS_SLAIN_3_MONK);
		thirdBossSubclassBadges.put(HeroSubClass.SUPERSTAR, Badge.BOSS_SLAIN_3_FREERUNNER);
		thirdBossSubclassBadges.put(HeroSubClass.JOKER, Badge.BOSS_SLAIN_3_SNIPER);
		thirdBossSubclassBadges.put(HeroSubClass.AGENT, Badge.BOSS_SLAIN_3_SNIPER);
		thirdBossSubclassBadges.put(HeroSubClass.LEADER, Badge.BOSS_SLAIN_3_PALADIN);
		thirdBossSubclassBadges.put(HeroSubClass.ARTISAN, Badge.BOSS_SLAIN_3_CHAMPION);
		thirdBossSubclassBadges.put(HeroSubClass.PASTOR, Badge.BOSS_SLAIN_3_PRIEST);
		thirdBossSubclassBadges.put(HeroSubClass.ASCETIC_MONK, Badge.BOSS_SLAIN_3_MONK);
		thirdBossSubclassBadges.put(HeroSubClass.HACKER, Badge.BOSS_SLAIN_3_BATTLEMAGE);
		thirdBossSubclassBadges.put(HeroSubClass.PRIEST, Badge.BOSS_SLAIN_3_PRIEST);
		thirdBossSubclassBadges.put(HeroSubClass.PALADIN, Badge.BOSS_SLAIN_3_PALADIN);
	}
	
	public static void validateBossSlain() {
		Badge badge = null;
		switch (Dungeon.legacyDepth()) {
		case 7:
			badge = Badge.BOSS_SLAIN_1;
			break;
		case 15:
			badge = Badge.BOSS_SLAIN_2;
			break;
		case 23:
			badge = Badge.BOSS_SLAIN_3;
			break;
		case 31:
			badge = Badge.BOSS_SLAIN_4;
			break;
		}
		
		if (badge != null) {
			local.add( badge );
			displayBadge( badge );
			
			if (badge == Badge.BOSS_SLAIN_1) {
				badge = firstBossClassBadges.get(Dungeon.hero.heroClass);
				if (badge == null) return;
				local.add( badge );
				unlock(badge);

				boolean allUnlocked = true;
				for (Badge b : firstBossClassBadges.values()){
					if (!isUnlocked(b)){
						allUnlocked = false;
						break;
					}
				}
				if (allUnlocked) {
					
					badge = Badge.BOSS_SLAIN_1_ALL_CLASSES;
					if (!isUnlocked( badge )) {
						displayBadge( badge );
					}
				}
			} else if (badge == Badge.BOSS_SLAIN_3) {

				badge = thirdBossSubclassBadges.get(Dungeon.hero.subClass);
				if (badge == null) return;
				local.add( badge );
				unlock(badge);

				boolean allUnlocked = true;
				for (Badge b : thirdBossSubclassBadges.values()){
					if (!isUnlocked(b)){
						allUnlocked = false;
						break;
					}
				}
				if (allUnlocked) {
					badge = Badge.BOSS_SLAIN_3_ALL_SUBCLASSES;
					if (!isUnlocked( badge )) {
						displayBadge( badge );
					}
				}
			}

			if (Statistics.qualifiedForBossRemainsBadge && Dungeon.hero.belongings.getItem(RemainsItem.class) != null){
				badge = Badge.BOSS_SLAIN_REMAINS;
				local.add( badge );
				displayBadge( badge );
			}

		}
	}

	public static void validateBossChallengeCompleted(){
		Badge badge = null;
		switch (Dungeon.legacyDepth()) {
			case 7:
				badge = Badge.BOSS_CHALLENGE_1;
				break;
			case 15:
				badge = Badge.BOSS_CHALLENGE_2;
				break;
			case 23:
				badge = Badge.BOSS_CHALLENGE_3;
				break;
			case 31:
				badge = Badge.BOSS_CHALLENGE_4;
				break;
			case 39:
				badge = Badge.BOSS_CHALLENGE_5;
				break;
		}

		if (badge != null) {
			local.add(badge);
			displayBadge(badge);
		}
	}
	
	public static void validateMastery() {
		
		Badge badge = null;
		switch (Dungeon.hero.heroClass) {
			case WARRIOR:
				badge = Badge.MASTERY_WARRIOR;
				break;
			case MAGE:
				badge = Badge.MASTERY_MAGE;
				break;
			case ROGUE:
				badge = Badge.MASTERY_ROGUE;
				break;
			case HUNTRESS:
				badge = Badge.MASTERY_HUNTRESS;
				break;
			case DUELIST:
				badge = Badge.MASTERY_DUELIST;
				break;
			case CLERIC:
				badge = Badge.MASTERY_CLERIC;
				break;
			case SPELLSWORD:
				badge = Badge.MASTERY_MAGE;
				break;
			case PERFORMER:
				badge = Badge.MASTERY_ROGUE;
				break;
			case SOLDIER:
				badge = Badge.MASTERY_DUELIST;
				break;
			case FOLLOWER:
				badge = Badge.MASTERY_CLERIC;
				break;
			case ASCETIC:
				badge = Badge.MASTERY_MAGE;
				break;
		}
		
		unlock(badge);
	}

	public static void validateRatmogrify(){
		unlock(Badge.FOUND_RATMOGRIFY);
	}
	
	public static void validateMageUnlock(){
		if (Statistics.upgradesUsed >= 1 && !isUnlocked(Badge.UNLOCK_MAGE)){
			displayBadge( Badge.UNLOCK_MAGE );
		}
	}
	
	public static void validateRogueUnlock(){
		if (Statistics.sneakAttacks >= 10 && !isUnlocked(Badge.UNLOCK_ROGUE)){
			displayBadge( Badge.UNLOCK_ROGUE );
		}
	}
	
	public static void validateHuntressUnlock(){
		if (Statistics.thrownAttacks >= 10 && !isUnlocked(Badge.UNLOCK_HUNTRESS)){
			displayBadge( Badge.UNLOCK_HUNTRESS );
		}
	}

	public static void validateDuelistUnlock(){
		if (!isUnlocked(Badge.UNLOCK_DUELIST) && Dungeon.hero != null
				&& Dungeon.hero.belongings.weapon instanceof MeleeWeapon
				&& ((MeleeWeapon) Dungeon.hero.belongings.weapon).tier >= 2
				&& ((MeleeWeapon) Dungeon.hero.belongings.weapon).STRReq() <= Dungeon.hero.STR()){

			if (Dungeon.hero.belongings.weapon.isIdentified() &&
					((MeleeWeapon) Dungeon.hero.belongings.weapon).STRReq() <= Dungeon.hero.STR()) {
				displayBadge(Badge.UNLOCK_DUELIST);

			} else if (!Dungeon.hero.belongings.weapon.isIdentified() &&
					((MeleeWeapon) Dungeon.hero.belongings.weapon).STRReq(0) <= Dungeon.hero.STR()){
				displayBadge(Badge.UNLOCK_DUELIST);
			}
		}
	}

	public static void validateClericUnlock(){
		if (!isUnlocked(Badge.UNLOCK_CLERIC)){
			displayBadge( Badge.UNLOCK_CLERIC );
		}
	}
	
	public static void validateMasteryCombo( int n ) {
		if (!local.contains( Badge.MASTERY_COMBO ) && n == 10) {
			Badge badge = Badge.MASTERY_COMBO;
			local.add( badge );
			displayBadge( badge );
		}
	}
	
	public static void validateVictory() {

		Badge badge = Badge.VICTORY;
		local.add( badge );
		displayBadge( badge );

		//technically player can also not spend talent points if they want for some reason
		if (Statistics.qualifiedForRandomVictoryBadge
				&& Dungeon.hero.subClass != null
				&& Dungeon.hero.armorAbility != null){
			badge = Badge.VICTORY_RANDOM;
			local.add( badge );
			displayBadge( badge );
		}

		badge = victoryClassBadges.get(Dungeon.hero.heroClass);
		if (badge == null) return;
		local.add( badge );
		unlock(badge);

		boolean allUnlocked = true;
		for (Badge b : victoryClassBadges.values()){
			if (!isUnlocked(b)){
				allUnlocked = false;
				break;
			}
		}
		if (allUnlocked){
			badge = Badge.VICTORY_ALL_CLASSES;
			displayBadge( badge );
		}
	}

	public static void validateTakingTheMick(Object cause){
		if ((cause == Dungeon.hero || cause instanceof Explosive.ExplosiveCurseBomb)
				&& Dungeon.hero.belongings.attackingWeapon() instanceof Pickaxe
				&& Dungeon.hero.belongings.attackingWeapon().level() >= 20){
			local.add( Badge.TAKING_THE_MICK );
			displayBadge(Badge.TAKING_THE_MICK);
		}
	}

	public static void validateNoKilling() {
		if (!local.contains( Badge.NO_MONSTERS_SLAIN ) && Statistics.completedWithNoKilling) {
			Badge badge = Badge.NO_MONSTERS_SLAIN;
			local.add( badge );
			displayBadge( badge );
			Statistics.completedWithNoKilling = false;
		}
	}
	
	public static void validateGrimWeapon() {
		if (!local.contains( Badge.GRIM_WEAPON )) {
			Badge badge = Badge.GRIM_WEAPON;
			local.add( badge );
			displayBadge( badge );
		}
	}

	public static void validateManyBuffs(){
		if (!local.contains( Badge.MANY_BUFFS )) {
			Badge badge = Badge.MANY_BUFFS;
			local.add( badge );
			displayBadge( badge );
		}
	}
	
	public static void validateGamesPlayed() {
		Badge badge = null;
		if (Rankings.INSTANCE.totalNumber >= 10 || Rankings.INSTANCE.wonNumber >= 1) {
			badge = Badge.GAMES_PLAYED_1;
		}
		if (Rankings.INSTANCE.totalNumber >= 25 || Rankings.INSTANCE.wonNumber >= 3) {
			unlock(badge);
			badge = Badge.GAMES_PLAYED_2;
		}
		if (Rankings.INSTANCE.totalNumber >= 50 || Rankings.INSTANCE.wonNumber >= 5) {
			unlock(badge);
			badge = Badge.GAMES_PLAYED_3;
		}
		if (Rankings.INSTANCE.totalNumber >= 200 || Rankings.INSTANCE.wonNumber >= 10) {
			unlock(badge);
			badge = Badge.GAMES_PLAYED_4;
		}
		if (Rankings.INSTANCE.totalNumber >= 1000 || Rankings.INSTANCE.wonNumber >= 25) {
			unlock(badge);
			badge = Badge.GAMES_PLAYED_5;
		}
		
		displayBadge( badge );
	}

	public static void validateHighScore( int score ){
		Badge badge = null;
		if (score >= 5000) {
			badge = Badge.HIGH_SCORE_1;
			local.add( badge );
		}
		if (score >= 25_000) {
			unlock(badge);
			badge = Badge.HIGH_SCORE_2;
			local.add( badge );
		}
		if (score >= 100_000) {
			unlock(badge);
			badge = Badge.HIGH_SCORE_3;
			local.add( badge );
		}
		if (score >= 250_000) {
			unlock(badge);
			badge = Badge.HIGH_SCORE_4;
			local.add( badge );
		}
		if (score >= 1_000_000) {
			unlock(badge);
			badge = Badge.HIGH_SCORE_5;
			local.add( badge );
		}

		displayBadge( badge );
	}
	
	public static void validateHappyEnd() {
		local.add( Badge.HAPPY_END );
		displayBadge( Badge.HAPPY_END );

		if( Dungeon.hero.belongings.getItem(RemainsItem.class) != null ){
			local.add( Badge.HAPPY_END_REMAINS );
			displayBadge( Badge.HAPPY_END_REMAINS );
		}

		if (AscensionChallenge.qualifiedForPacifist()) {
			local.add( Badge.PACIFIST_ASCENT );
			displayBadge( Badge.PACIFIST_ASCENT );
		}
	}

	public static void validateChampion( int challenges ) {
		if (challenges == 0) return;
		Badge badge = null;
		if (challenges >= 1) {
			badge = Badge.CHAMPION_1;
		}
		if (challenges >= 3){
			unlock(badge);
			badge = Badge.CHAMPION_2;
		}
		if (challenges >= 6){
			unlock(badge);
			badge = Badge.CHAMPION_3;
		}
		local.add(badge);
		displayBadge( badge );
	}
	
	private static void displayBadge( Badge badge ) {

		if (badge == null || (badge.type != BadgeType.JOURNAL && !Dungeon.customSeedText.isEmpty())) {
			return;
		}
		
		if (isUnlocked( badge )) {
			
			if (badge.type == BadgeType.LOCAL) {
				GLog.h( Messages.get(Badges.class, "endorsed", badge.title()) );
				GLog.newLine();
			}
			
		} else {
			
			unlock(badge);
			
			GLog.h( Messages.get(Badges.class, "new", badge.title() + " (" + badge.desc() + ")") );
			GLog.newLine();
			if (Game.instance != null) PixelScene.showBadge( badge );
		}
	}
	
	public static boolean isUnlocked( Badge badge ) {
		return global.contains( badge );
	}
	
	public static HashSet<Badge> allUnlocked(){
		loadGlobal();
		return new HashSet<>(global);
	}
	
	public static void disown( Badge badge ) {
		loadGlobal();
		global.remove( badge );
		saveNeeded = true;
	}
	
	public static void unlock( Badge badge ){
		if (!isUnlocked(badge) && (badge.type == BadgeType.JOURNAL || Dungeon.customSeedText.isEmpty())){
			global.add( badge );
			saveNeeded = true;
		}
	}

	public static List<Badge> filterReplacedBadges( boolean global ) {

		ArrayList<Badge> badges = new ArrayList<>(global ? Badges.global : Badges.local);

		Iterator<Badge> iterator = badges.iterator();
		while (iterator.hasNext()) {
			Badge badge = iterator.next();
			if ((!global && badge.type != BadgeType.LOCAL) || badge.type == BadgeType.HIDDEN) {
				iterator.remove();
			}
		}

		Collections.sort(badges);

		return filterReplacedBadges(badges);

	}

	//only show the highest unlocked and the lowest locked
	private static final Badge[][] tierBadgeReplacements = new Badge[][]{
			{Badge.MONSTERS_SLAIN_1, Badge.MONSTERS_SLAIN_2, Badge.MONSTERS_SLAIN_3, Badge.MONSTERS_SLAIN_4, Badge.MONSTERS_SLAIN_5},
			{Badge.GOLD_COLLECTED_1, Badge.GOLD_COLLECTED_2, Badge.GOLD_COLLECTED_3, Badge.GOLD_COLLECTED_4, Badge.GOLD_COLLECTED_5},
			{Badge.ITEM_LEVEL_1, Badge.ITEM_LEVEL_2, Badge.ITEM_LEVEL_3, Badge.ITEM_LEVEL_4, Badge.ITEM_LEVEL_5},
			{Badge.LEVEL_REACHED_1, Badge.LEVEL_REACHED_2, Badge.LEVEL_REACHED_3, Badge.LEVEL_REACHED_4, Badge.LEVEL_REACHED_5},
			{Badge.STRENGTH_ATTAINED_1, Badge.STRENGTH_ATTAINED_2, Badge.STRENGTH_ATTAINED_3, Badge.STRENGTH_ATTAINED_4, Badge.STRENGTH_ATTAINED_5},
			{Badge.FOOD_EATEN_1, Badge.FOOD_EATEN_2, Badge.FOOD_EATEN_3, Badge.FOOD_EATEN_4, Badge.FOOD_EATEN_5},
			{Badge.ITEMS_CRAFTED_1, Badge.ITEMS_CRAFTED_2, Badge.ITEMS_CRAFTED_3, Badge.ITEMS_CRAFTED_4, Badge.ITEMS_CRAFTED_5},
			{Badge.BOSS_SLAIN_1, Badge.BOSS_SLAIN_2, Badge.BOSS_SLAIN_3, Badge.BOSS_SLAIN_4},
			{Badge.RESEARCHER_1, Badge.RESEARCHER_2, Badge.RESEARCHER_3, Badge.RESEARCHER_4, Badge.RESEARCHER_5},
			{Badge.HIGH_SCORE_1, Badge.HIGH_SCORE_2, Badge.HIGH_SCORE_3, Badge.HIGH_SCORE_4, Badge.HIGH_SCORE_5},
			{Badge.GAMES_PLAYED_1, Badge.GAMES_PLAYED_2, Badge.GAMES_PLAYED_3, Badge.GAMES_PLAYED_4, Badge.GAMES_PLAYED_5},
			{Badge.CHAMPION_1, Badge.CHAMPION_2, Badge.CHAMPION_3}
	};

	//don't show the later badge if the earlier one isn't unlocked
	//we aren't too aggressive with this, mainly just want to prevent boss spoilers,
	// and all diamond tier badges must have a gold/plat prerequisite
	private static final Badge[][] prerequisiteBadges = new Badge[][]{
			{Badge.BOSS_SLAIN_1, Badge.BOSS_CHALLENGE_1},
			{Badge.BOSS_SLAIN_2, Badge.BOSS_CHALLENGE_2},
			{Badge.BOSS_SLAIN_3, Badge.BOSS_CHALLENGE_3},
			{Badge.BOSS_SLAIN_4, Badge.BOSS_CHALLENGE_4},
			{Badge.VICTORY,      Badge.BOSS_CHALLENGE_5},
			{Badge.HAPPY_END,    Badge.PACIFIST_ASCENT},
			{Badge.VICTORY,      Badge.TAKING_THE_MICK}
	};

	//If the summary badge is unlocked, don't show the component badges
	private static final Badge[][] summaryBadgeReplacements = new Badge[][]{
			{Badge.DEATH_FROM_FIRE, Badge.DEATH_FROM_ALL},
			{Badge.DEATH_FROM_GAS, Badge.DEATH_FROM_ALL},
			{Badge.DEATH_FROM_HUNGER, Badge.DEATH_FROM_ALL},
			{Badge.DEATH_FROM_POISON, Badge.DEATH_FROM_ALL},
			{Badge.DEATH_FROM_FALLING, Badge.DEATH_FROM_ALL},
			{Badge.DEATH_FROM_ENEMY_MAGIC, Badge.DEATH_FROM_ALL},
			{Badge.DEATH_FROM_FRIENDLY_MAGIC, Badge.DEATH_FROM_ALL},
			{Badge.DEATH_FROM_SACRIFICE, Badge.DEATH_FROM_ALL},
			{Badge.DEATH_FROM_GRIM_TRAP, Badge.DEATH_FROM_ALL}
	};
	
	public static List<Badge> filterReplacedBadges( List<Badge> badges ) {

		for (Badge[] tierReplace : tierBadgeReplacements){
			leaveBest( badges, tierReplace );
		}

		for (Badge[] metaReplace : summaryBadgeReplacements){
			leaveBest( badges, metaReplace );
		}
		
		return badges;
	}
	
	private static void leaveBest( Collection<Badge> list, Badge...badges ) {
		for (int i=badges.length-1; i > 0; i--) {
			if (list.contains( badges[i])) {
				for (int j=0; j < i; j++) {
					list.remove( badges[j] );
				}
				break;
			}
		}
	}

	public static List<Badge> filterBadgesWithoutPrerequisites(List<Badges.Badge> badges ) {

		for (Badge[] prereqReplace : prerequisiteBadges){
			leaveWorst( badges, prereqReplace );
		}

		for (Badge[] tierReplace : tierBadgeReplacements){
			leaveWorst( badges, tierReplace );
		}

		Collections.sort( badges );

		return badges;
	}

	private static void leaveWorst( Collection<Badge> list, Badge...badges ) {
		for (int i=0; i < badges.length; i++) {
			if (list.contains( badges[i])) {
				for (int j=i+1; j < badges.length; j++) {
					list.remove( badges[j] );
				}
				break;
			}
		}
	}

	public static Collection<Badge> addReplacedBadges(Collection<Badges.Badge> badges ) {

		for (Badge[] tierReplace : tierBadgeReplacements){
			addLower( badges, tierReplace );
		}

		for (Badge[] metaReplace : summaryBadgeReplacements){
			addLower( badges, metaReplace );
		}

		return badges;
	}

	private static void addLower( Collection<Badge> list, Badge...badges ) {
		for (int i=badges.length-1; i > 0; i--) {
			if (list.contains( badges[i])) {
				for (int j=0; j < i; j++) {
					list.add( badges[j] );
				}
				break;
			}
		}
	}

	//used for badges with completion progress that would otherwise be hard to track
	public static String showCompletionProgress( Badge badge ){
		if (isUnlocked(badge)) return null;

		String result = "\n";

		if (badge == Badge.BOSS_SLAIN_1_ALL_CLASSES){
			for (HeroClass cls : HeroClass.playableClasses()){
				result += "\n";
				if (isUnlocked(firstBossClassBadges.get(cls)))  result += "_" + Messages.titleCase(cls.title()) + "_";
				else                                            result += Messages.titleCase(cls.title());
			}

			return result;

		} else if (badge == Badge.VICTORY_ALL_CLASSES) {

			for (HeroClass cls : HeroClass.playableClasses()){
				result += "\n";
				if (isUnlocked(victoryClassBadges.get(cls)))    result += "_" + Messages.titleCase(cls.title()) + "_";
				else                                            result += Messages.titleCase(cls.title());
			}

			return result;

		} else if (badge == Badge.BOSS_SLAIN_3_ALL_SUBCLASSES){

			for (HeroSubClass cls : HeroSubClass.values()){
				if (cls == HeroSubClass.NONE) continue;
				result += "\n";
				if (isUnlocked(thirdBossSubclassBadges.get(cls)))   result += "_" + Messages.titleCase(cls.title()) + "_";
				else                                                result += Messages.titleCase(cls.title()) ;
			}

			return result;
		}

		return null;
	}
}
