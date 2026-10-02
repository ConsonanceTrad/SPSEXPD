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

package pd.actors.hero;

import pd.Dungeon;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.ui.HeroIcon;
import render.noosa.Game;
import pd.messages.InlineText;

public enum HeroSubClass {

	NONE(HeroIcon.NONE),

	BERSERKER(HeroIcon.BERSERKER),
	GLADIATOR(HeroIcon.GLADIATOR),

	BATTLEMAGE(HeroIcon.BATTLEMAGE),
	WARLOCK(HeroIcon.WARLOCK),
	
	ASSASSIN(HeroIcon.ASSASSIN),
	FREERUNNER(HeroIcon.FREERUNNER),
	
	SNIPER(HeroIcon.SNIPER),
	WARDEN(HeroIcon.WARDEN),

	CHAMPION(HeroIcon.CHAMPION),
	MONK(HeroIcon.MONK),

	PRIEST(HeroIcon.PRIEST),
	PALADIN(HeroIcon.PALADIN),

	SUPERSTAR(HeroIcon.FREERUNNER),
	JOKER(HeroIcon.SNIPER),
	AGENT(HeroIcon.SNIPER),
	LEADER(HeroIcon.PALADIN),
	ARTISAN(HeroIcon.CHAMPION),
	PASTOR(HeroIcon.PRIEST),
	ASCETIC_MONK(HeroIcon.MONK),
	HACKER(HeroIcon.BATTLEMAGE);
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HeroSubClass.class)
			.t("berserker", "狂战士")
			.t("berserker_short_desc", "_狂战士_在受到伤害的时候会积累怒气，怒气能提高狂战士的攻击力，并可在其达到100%时激活以获得额外护盾。")
			.t("berserker_desc", "狂战士会在他受到物理伤害时获得怒气，甚至包括被其护甲所格挡的伤害！怒气会随时间稳定流逝，但会在狂战士生命值较低时流逝更慢。\n\n怒气可使狂战士造成最多50%额外伤害。怒气为满时，狂战士可以进行狂暴，获得爆发性护盾，而只要狂战士仍有护盾剩余，其怒气也会保持不变。护盾会随狂战士护甲等级的增长而增长，并且在其生命值较低时护盾可被大幅强化。在他进行狂暴之后，狂战士需要时间稍作休息。")
			.t("gladiator", "角斗士")
			.t("gladiator_short_desc", "_角斗士_会在成功进行攻击时积累连击数。角斗士可以消耗连击数以使用特殊战技。")
			.t("gladiator_desc", "每当角斗士以近战武器或投掷武器成功击中目标时，他都会积累一点连击。如果角斗士在5回合之内(击杀后15回合之内)未能成功命中目标，那么其连击会被重置。\n\n随着角斗士积累连击，他可以使用一系列必中的连击战技：\n2连击：击退敌人，保留连击\n4连击：造成基于护甲的伤害\n6连击：招架攻击，保留连击\n8连击：攻击目标与附近敌人\n10连击：每连击一次就攻击一次")
			.t("battlemage", "战斗法师")
			.t("battlemage_short_desc", "_战斗法师_在使用魔杖近战攻击时会附带额外的法术效果。这些效果取决于魔杖内灌注的法杖种类。")
			.t("battlemage_desc", "战斗法师使用魔杖近战时会附带类似附魔的额外效果，效果取决于魔杖所灌注的法杖种类，每种法杖各不相同。用魔杖近战打击还会为其恢复0.5点充能。")
			.t("warlock", "术士")
			.t("warlock_short_desc", "_术士_在使用法杖时有概率标记敌人的灵魂。每当术士对被标记的敌人造成物理伤害，自身就会恢复生命值。")
			.t("warlock_desc", "术士在对角色使用法杖时有概率为其施加灵魂标记。施加灵魂标记的概率与标记的持续时间随法杖等级增加而上升。\n\n术士攻击被标记的敌人时，每造成5点伤害，就恢复2点生命值。此效果只对物理攻击有效，对法杖施法的伤害无效！")
			.t("assassin", "刺客")
			.t("assassin_short_desc", "隐形时，_刺客_会准备一次致命的攻击。耐心等得越久，这一击的威力越强。")
			.t("assassin_desc", "刺客会在隐形时准备强力的一击。他准备的时间越长，下次攻击就越致命。准备阶段最多累计9回合。\n\n准备充分后，刺客的下一次攻击会造成额外伤害，能闪现向目标，甚至还能直接斩杀足够虚弱的敌人。")
			.t("freerunner", "疾行者")
			.t("freerunner_short_desc", "_疾行者_在奔跑时会累积动量。动量可以用于开启逸动效果，使他在短时间内获得速度和闪避加成。")
			.t("freerunner_desc", "_疾行者_在奔跑时会累积动量。他每次移动获得1点动量，最多累积10点，在不移动时动量会迅速衰减。动量可用于开启逸动状态，每点动量获取2回合逸动。\n\n逸动状态下的疾行者以双倍速度跑动，并获得正比于自身等级的额外闪避。逸动状态结束后，疾行者需休息一会以重新积累动量。")
			.t("sniper", "狙击手")
			.t("sniper_short_desc", "_狙击手_的远程攻击能穿透护甲。用投掷武器击中目标后，狙击手能以灵能弓进行一次特殊追击。")
			.t("sniper_desc", "狙击手是远程战斗大师，她的远程攻击无视敌人护甲。使用投掷武器击中敌人后，女猎手能对目标施以狙击标记，以使用灵能弓对此敌人发射一次追击。追击的形式取决于弓的强化方式。\n\n未强化的弓会射出一支伤害稍低的速射箭矢，但射击不耗时。强化速度的弓会连射出三箭，每支箭的伤害较低但能触发附魔，射击耗时1回合。强化伤害的弓会射出一支必中的狙杀箭矢，造成基于距离的额外伤害，射击耗时2回合。")
			.t("warden", "守望者")
			.t("warden_short_desc", "_守望者_具有穿透高草的视野，而且在种植和踩踏植物时会获得额外效果。")
			.t("warden_desc", "守望者与自然之力有强大的联结，这赋予了她多项有关植物的能力。守望者具有穿透高草与枯草的视野。\n\n守望者种下的种子周围会生出草，在踩踏植物时会获得额外效果取代原本效果，使得所有植株对其完全无害。")
			.t("champion", "勇士")
			.t("champion_short_desc", "_勇士_可以双持武器，并拥有更多武技充能。她用主武器进行普通攻击，但她也可以自如地切换主、副武器，并施展两武器各自的武技。")
			.t("champion_desc", "勇士善使近战武器，除了主武器，她还能再装备一件副武器。勇士用主武器进行普通攻击，但她可以不耗时地切换主、副武器，也能施放两武器各自的武技。\n\n她还额外有2点武技充能上限，且武技充能回复速率提升50%。")
			.t("monk", "武僧")
			.t("monk_short_desc", "_武僧_在战斗时能够积蓄内力，这些内力可用于施展诸多武功。")
			.t("monk_desc", "武僧的武术造诣十分深厚。她在击败敌人时能够积蓄内力，并用于施放诸多防御性和功能性的能力。内力不会随时间流失，但具有基于武僧等级的存储上限。\n\n1点内力：疾速出拳，连续打击目标\n2点内力：聚精会神，闪避下次受击\n3点内力：瞬间位移，冲向附近位置\n4点内力：蓄力飞踢，击退附近敌人\n5点内力：清除状态，充能法杖神器")
			.t("priest", "祭司")
			.t("priest_short_desc", "_祭司_会获得全新的远程法术和强化版神导之光。")
			.t("priest_desc", "祭司获得一系列全新升级的法术，其主要强调远程攻击与魔法物品兼容。\n\n祭司每经50回合可以免费施放一次_神导之光_，可通过其任何直接作用于敌人的法术施加光耀，并可通过盟友、法杖和某些神器消耗光耀以造成等同于祭司等级+5的额外伤害。\n\n祭司还会获得法术_破晓辐光_，消耗2点充能以驱散黑暗，为视野内所有敌人触发并施加光耀，并将其短暂击晕。")
			.t("paladin", "圣骑士")
			.t("paladin_short_desc", "_圣骑士_会获得全新的近程法术和强化版神圣武器、神圣护甲。")
			.t("paladin_desc", "圣骑士获得一系列全新升级的法术，其主要强调近战攻击与武器护甲兼容。\n\n圣骑士的法术_神圣武器_与_神圣护甲_的攻防加成获得大幅强化，其不再覆盖已有的附魔与刻印，并可通过施放其他法术延长其时效。\n\n圣骑士还会获得法术_至圣斩击_，进行一次带有额外伤害与附魔强度的必中近战攻击。")
			.t("superstar", "巨星")
			.t("superstar_short_desc", "_巨星_能让演员的节奏祝福持续更久。")
			.t("superstar_desc", "巨星每次获得经验后，节奏祝福由3回合延长至5回合；三阶天赋侧重移动与战斗节奏。")
			.t("joker", "戏法师")
			.t("joker_short_desc", "_戏法师_能用投掷武器妨碍敌人行动。")
			.t("joker_desc", "投掷攻击有20%概率使目标残废2回合；三阶天赋侧重远程布置与强化共享。")
			.t("agent", "特工")
			.t("agent_short_desc", "_特工_使用投掷武器时拥有更高命中。")
			.t("agent_desc", "特工使用投掷武器时命中提高15%；三阶天赋强化射程、移动和悬赏作战。")
			.t("leader", "领袖")
			.t("leader_short_desc", "_领袖_在正面受击时更加沉着。")
			.t("leader_desc", "领袖在计算护甲前受到的伤害降低10%；三阶天赋侧重防护和支援。")
			.t("artisan", "工匠")
			.t("artisan_short_desc", "_工匠_能够更有效率地驾驭高要求装备。")
			.t("artisan_desc", "计算装备力量需求时，工匠视为额外拥有1点力量；三阶天赋侧重装备灵活性。")
			.t("pastor", "牧者")
			.t("pastor_short_desc", "_牧者_在饱腹时恢复生命的效率更高。")
			.t("pastor_desc", "牧者的自然恢复速度提高25%。与旧版机制不同，这项恢复不能超过生命上限。")
			.t("ascetic_monk", "戒律者")
			.t("ascetic_monk_short_desc", "_戒律者_专精于节制而稳定的拳套战斗。")
			.t("ascetic_monk_desc", "戒律者使用拳套时额外造成1点伤害。该路线沿用武僧天赋族，但与决斗家的武僧专精相互独立。")
			.t("hacker", "黑客")
			.t("hacker_short_desc", "_黑客_能将充能状态转换为近战力量。")
			.t("hacker_desc", "处于充能状态时，黑客造成的物理伤害提高10%；三阶天赋侧重法杖充能与强化打击。");
	}




	int icon;

	HeroSubClass(int icon){
		this.icon = icon;
	}
	
	public String title() {
		return Messages.get(this, name());
	}

	public String shortDesc() {
		return Messages.get(this, name()+"_short_desc");
	}

	public String desc() {
		//Include the staff effect description in the battlemage's desc if possible
		if (this == BATTLEMAGE){
			String desc = Messages.get(this, name() + "_desc");
			if (Game.scene() instanceof GameScene){
				MagesStaff staff = Dungeon.hero.belongings.getItem(MagesStaff.class);
				if (staff != null && staff.wandClass() != null){
					desc += "\n\n" + Messages.get(staff.wandClass(), "bmage_desc");
					desc = desc.replaceAll("_", "");
				}
			}
			return desc;
		} else {
			return Messages.get(this, name() + "_desc");
		}
	}

	public int icon(){
		return icon;
	}

}
