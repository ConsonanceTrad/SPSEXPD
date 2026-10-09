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

import pd.items.Dewdrop;
import pd.items.Item;
import pd.messages.InlineText;

public class Challenges {
	//SPSEXPD: inline Chinese text (generated from messages/misc/zh)
	static {
		InlineText.of(Challenges.class)
			.t("no_food", "缩餐节食")
			.t("no_food_desc", "食物本就稀缺，但你还需要注意节食！\n\n-使用各类食物与丰饶之角的饱腹效果为原本的三分之一\n-其他恢复饥饿的机制不受影响")
			.t("no_armor", "信念护体")
			.t("no_armor_desc", "如今护甲已难堪大用，你只能相信你自己的肉身了！\n\n-布甲之外所有护甲的基本防御力降低\n-所有护甲升级提供的防御力成长大幅降低\n-灵壤守卫的防御力大幅降低")
			.t("no_healing", "恐药异症")
			.t("no_healing_desc", "治疗药剂真是种好东西，可惜你对它过敏！\n\n-治疗药剂、以治疗药剂为原料炼制的道具与阳光果将无法治愈英雄，反而会使英雄中毒\n-紊乱魔药不会随机到治愈英雄或使英雄中毒的效果\n-这些道具对其他单位依然发挥正常效果。")
			.t("no_herbalism", "荒芜之地")
			.t("no_herbalism_desc", "这个晦气的地牢里连干净的水都没有了...\n\n-移除露珠\n-移除所有特殊植物\n-高草依然会掉落种子，但种子不会生根发芽")
			.t("swarm_intelligence", "集群智能")
			.t("swarm_intelligence_desc", "要小心了，地牢里的怪物们也在学习进步！\n\n-每当有敌人发现你或你的盟友时，它们将会吸引附近其他敌人至你所在的位置。\n-初始吸引范围仅有2格，但你每回合暴露于敌方视野中，都会使吸引范围成长2格，且上限为12格。\n-阻断视线可以重置吸引范围，即使是门这类的即时阻断也算数。")
			.t("darkness", "没入黑暗")
			.t("darkness_desc", "地牢毕竟是在地下嘛！\n\n-正常视野范围显著缩小\n-每层都会出现一根火把\n-其他光源有效性降至1/5")
			.t("no_scrolls", "禁忌咒文")
			.t("no_scrolls_desc", "一种卷轴会变得更稀有。不幸的是，正好还是最有用的那一种。\n\n-移除地牢中半数的升级卷轴与魔力灌注卷轴")
			.t("item_phobia", "恐物幻觉")
			.t("item_phobia_desc", "你感觉你的卷轴和药水都十分危险。\n\n-饮用药水需要5回合\n-阅读卷轴会受到最大生命值10%的伤害并沉默5回合\n-开局额外获得1000金币")
			.t("listless", "精神萎靡")
			.t("listless_desc", "你感觉浑身无力。\n\n-升级只增加2点生命上限并恢复1点生命\n-开局获得一瓶根骨药水和一份蜂皇浆")
			.t("nightmare_virus", "梦魇病毒")
			.t("nightmare_virus_desc", "恐怖的病毒感染了所有生物。\n\n-怪物死亡时生成病毒体\n-开局获得一个复活十字架")
			.t("energy_lost", "能量流失")
			.t("energy_lost_desc", "大量能量转化为热能散发。\n\n-正向饱食收益降低至40%\n-法杖每5级仅增加1点最大充能，且上限降为6\n-开局额外获得一份干粮")
			.t("dew_rejection", "排异露珠")
			.t("dew_rejection_desc", "露珠之神并不喜欢你。\n\n-充能露珠爆发仅覆盖四个正方向格子\n-露珠瓶能力额外消耗10点露珠\n-开局获得两颗集露草种子")
			.t("sps_darkness", "没入黑暗")
			.t("sps_darkness_desc", "黑暗吞噬了周围的环境。\n\n-无法主动记录地图。\n-夜影在周围徘徊。\n-开始时获得探地卷轴")
			.t("abrasion", "严重磨损")
			.t("abrasion_desc", "武器上的锈迹明显了起来。\n\n-常规武器有耐久度限制。用完即刻报废。\n-开始时获得升级卷轴和注魔卷轴。")
			.t("ele_stome", "元素风暴")
			.t("ele_stome_desc", "元素在地牢里面涌动。\n\n-敌人额外造成属性法术伤害。\n-法杖造成伤害降低。\n-开始时获得护盾药水和灵能汲取卷轴。")
			.t("champion_enemies", "精英强敌")
			.t("champion_enemies_desc", "道高一尺，魔高一丈！\n\n普通敌人生成时会有概率获得特殊的精英强敌效果，概率会从1层的1/8增长至21层的1/6。\n\n-精英强敌不以沉睡状态生成\n-精英强敌生成时英雄会察觉\n-精英强敌无法被转化为盟友\n\n一共有六种精英强敌效果：\n_烈焰(橙)：_近战伤害+25%，攻击时点燃，死亡时起火(不点燃水体)\n_索敌(紫)：_近战伤害+25%，近战范围+3\n_敌法(绿)：_所受伤害-50%，免疫魔法效果\n_巨型(蓝)：_所受伤害-80%，近战范围+1，无法进入门道\n_天佑(黄)：_精准、闪避x4\n_成长(红)：_精准、闪避、攻击伤害与有效生命+20%，每4回合以上属性成长1%")
			.t("stronger_bosses", "绝命头目")
			.t("stronger_bosses_desc", "这项挑战会让所有Boss都更具挑战性！\n\n_粘咕：_生命值+20%\n_-_持续处于水中时，生命回复量递增，最高至每回合3点\n_-_爆发攻击蓄力时间由2回合缩短至1回合\n_天狗：_生命+25%\n_-_第一阶段：陷阱更加致命\n_-_第二阶段：技能频率更高\n_DM-300：_生命+33%\n_-_能量塔更坚固，并且需要击毁3座\n_-_技能频率更高，威力也更强大\n_-_超载时速度更快\n_-_地表导线数量为原先的两倍\n_矮人国王：_生命+50%\n_-_所有阶段中都会召唤更多强力随从\n_-_第一阶段：更高的技能与召唤频率\n_-_第二阶段：每轮额外召唤两名随从\n_-_第三阶段：生命值+100%，进一步提高召唤频率\n_Yog-Dzewa：_\n_-_同时召唤两个古神之拳！\n_-_激光攻击伤害+60%\n_-_召唤更强大的随从")
			.t("test_time", "测试时间")
			.t("test_time_desc", "特别惊喜像素地牢原版的测试挑战，会启用测试专用捷径与奖励。\n\n-玩得开心。");
	}




	//Some of these internal IDs are outdated and don't represent what these challenges do
	public static final int NO_FOOD				= 1;
	public static final int NO_ARMOR			= 2;
	public static final int NO_HEALING			= 4;
	public static final int NO_HERBALISM		= 8;
	public static final int SWARM_INTELLIGENCE	= 16;
	public static final int DARKNESS			= 32;
	public static final int NO_SCROLLS		    = 64;
	public static final int CHAMPION_ENEMIES	= 128;
	public static final int STRONGER_BOSSES 	= 256;
	public static final int TEST_TIME          = 512;
	// Legacy SPS challenges use new bits so old mechanics cannot alias Shattered challenges.
	public static final int DEW_REJECTION      = 1024;
	public static final int ITEM_PHOBIA        = 2048;
	public static final int LISTLESS           = 4096;
	public static final int NIGHTMARE_VIRUS    = 8192;
	public static final int ENERGY_LOST        = 16384;
	public static final int SPS_DARKNESS       = 32768;
	public static final int ABRASION           = 65536;
	public static final int ELE_STOME          = 131072;

	public static final int MAX_VALUE           = 262143;
	public static final int MAX_CHALS           = 18;

	public static final String[] NAME_IDS = {
			"champion_enemies",
			"stronger_bosses",
			"no_food",
			"no_armor",
			"no_healing",
			"no_herbalism",
			"swarm_intelligence",
			"darkness",
			"no_scrolls",
			"item_phobia",
			"listless",
			"nightmare_virus",
			"energy_lost",
			"dew_rejection",
			"sps_darkness",
			"abrasion",
			"ele_stome",
			"test_time"
	};

	public static final int[] MASKS = {
			CHAMPION_ENEMIES, STRONGER_BOSSES, NO_FOOD, NO_ARMOR, NO_HEALING, NO_HERBALISM,
			SWARM_INTELLIGENCE, DARKNESS, NO_SCROLLS, ITEM_PHOBIA, LISTLESS, NIGHTMARE_VIRUS, ENERGY_LOST,
			DEW_REJECTION, SPS_DARKNESS, ABRASION, ELE_STOME, TEST_TIME
	};

	public static int activeChallenges(){
		return activeChallenges(Dungeon.challenges);
	}

	public static int activeChallenges(int mask){
		int chCount = 0;
		for (int ch : Challenges.MASKS){
			if ((mask & ch) != 0) chCount++;
		}
		return chCount;
	}

	public static boolean isItemBlocked( Item item ){

		if (Dungeon.isChallenged(NO_HERBALISM) && item instanceof Dewdrop){
			return true;
		}

		return false;

	}

}
