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

import pd.Assets;
import pd.Dungeon;
import pd.GamesInProgress;
import pd.ShatteredPixelDungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ArtifactRecharge;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.buffs.CounterBuff;
import pd.actors.buffs.EnhancedRings;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.Haste;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.LostInventory;
import pd.actors.buffs.PhysicalEmpower;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.RevealedArea;
import pd.actors.buffs.Roots;
import pd.actors.buffs.ScrollEmpower;
import pd.actors.buffs.WandEmpower;
import pd.actors.hero.abilities.ArmorAbility;
import pd.actors.hero.abilities.Ratmogrify;
import pd.actors.hero.spells.DivineSense;
import pd.actors.hero.spells.RecallInscription;
import pd.actors.mobs.Mob;
import pd.effects.CellEmitter;
import pd.effects.Flare;
import pd.effects.FloatingText;
import pd.effects.SpellSprite;
import pd.effects.particles.LeafParticle;
import pd.items.BrokenSeal;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.armor.ClothArmor;
import pd.items.equipment.artifacts.CloakOfShadows;
import pd.items.equipment.artifacts.HolyTome;
import pd.items.equipment.artifacts.HornOfPlenty;
import pd.items.equipment.rings.Ring;
import pd.items.consum.scrolls.Scroll;
import pd.items.consum.scrolls.ScrollOfRecharging;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.items.consum.stones.Runestone;
import pd.items.consum.stones.StoneOfIntuition;
import pd.items.equipment.trinkets.ShardOfOblivion;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.SpiritBow;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.Gloves;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.noosa.Image;
import render.noosa.audio.Sample;
import render.utils.math.GameMath;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import pd.messages.InlineText;

public enum Talent {

	//Warrior T1
	HEARTY_MEAL(0), VETERANS_INTUITION(1), PROVOKED_ANGER(2), IRON_WILL(3),
	//Warrior T2
	IRON_STOMACH(4), LIQUID_WILLPOWER(5), RUNIC_TRANSFERENCE(6), LETHAL_MOMENTUM(7), IMPROVISED_PROJECTILES(8),
	//Warrior T3
	HOLD_FAST(9, 3), STRONGMAN(10, 3),
	//Berserker T3
	ENDLESS_RAGE(11, 3), DEATHLESS_FURY(12, 3), ENRAGED_CATALYST(13, 3),
	//Gladiator T3
	CLEAVE(14, 3), LETHAL_DEFENSE(15, 3), ENHANCED_COMBO(16, 3),
	//Heroic Leap T4
	BODY_SLAM(17, 4), IMPACT_WAVE(18, 4), DOUBLE_JUMP(19, 4),
	//Shockwave T4
	EXPANDING_WAVE(20, 4), STRIKING_WAVE(21, 4), SHOCK_FORCE(22, 4),
	//Endure T4
	SUSTAINED_RETRIBUTION(23, 4), SHRUG_IT_OFF(24, 4), EVEN_THE_ODDS(25, 4),

	//Mage T1
	EMPOWERING_MEAL(32), SCHOLARS_INTUITION(33), LINGERING_MAGIC(34), BACKUP_BARRIER(35),
	//Mage T2
	ENERGIZING_MEAL(36), INSCRIBED_POWER(37), WAND_PRESERVATION(38), ARCANE_VISION(39), SHIELD_BATTERY(40),
	//Mage T3
	DESPERATE_POWER(41, 3), ALLY_WARP(42, 3),
	//Battlemage T3
	EMPOWERED_STRIKE(43, 3), MYSTICAL_CHARGE(44, 3), EXCESS_CHARGE(45, 3),
	//Warlock T3
	SOUL_EATER(46, 3), SOUL_SIPHON(47, 3), NECROMANCERS_MINIONS(48, 3),
	//Elemental Blast T4
	BLAST_RADIUS(49, 4), ELEMENTAL_POWER(50, 4), REACTIVE_BARRIER(51, 4),
	//Wild Magic T4
	WILD_POWER(52, 4), FIRE_EVERYTHING(53, 4), CONSERVED_MAGIC(54, 4),
	//Warp Beacon T4
	TELEFRAG(55, 4), REMOTE_BEACON(56, 4), LONGRANGE_WARP(57, 4),

	//Rogue T1
	CACHED_RATIONS(64), THIEFS_INTUITION(65), SUCKER_PUNCH(66), PROTECTIVE_SHADOWS(67),
	//Rogue T2
	MYSTICAL_MEAL(68), INSCRIBED_STEALTH(69), WIDE_SEARCH(70), SILENT_STEPS(71), ROGUES_FORESIGHT(72),
	//Rogue T3
	ENHANCED_RINGS(73, 3), LIGHT_CLOAK(74, 3),
	//Assassin T3
	ENHANCED_LETHALITY(75, 3), ASSASSINS_REACH(76, 3), BOUNTY_HUNTER(77, 3),
	//Freerunner T3
	EVASIVE_ARMOR(78, 3), PROJECTILE_MOMENTUM(79, 3), SPEEDY_STEALTH(80, 3),
	//Smoke Bomb T4
	HASTY_RETREAT(81, 4), BODY_REPLACEMENT(82, 4), SHADOW_STEP(83, 4),
	//Death Mark T4
	FEAR_THE_REAPER(84, 4), DEATHLY_DURABILITY(85, 4), DOUBLE_MARK(86, 4),
	//Shadow Clone T4
	SHADOW_BLADE(87, 4), CLONED_ARMOR(88, 4), PERFECT_COPY(89, 4),

	//Huntress T1
	NATURES_BOUNTY(96), SURVIVALISTS_INTUITION(97), FOLLOWUP_STRIKE(98), NATURES_AID(99),
	//Huntress T2
	INVIGORATING_MEAL(100), LIQUID_NATURE(101), REJUVENATING_STEPS(102), HEIGHTENED_SENSES(103), DURABLE_PROJECTILES(104),
	//Huntress T3
	POINT_BLANK(105, 3), SEER_SHOT(106, 3),
	//Sniper T3
	FARSIGHT(107, 3), SHARED_ENCHANTMENT(108, 3), SHARED_UPGRADES(109, 3),
	//Warden T3
	DURABLE_TIPS(110, 3), BARKSKIN(111, 3), SHIELDING_DEW(112, 3),
	//Spectral Blades T4
	FAN_OF_BLADES(113, 4), PROJECTING_BLADES(114, 4), SPIRIT_BLADES(115, 4),
	//Natures Power T4
	GROWING_POWER(116, 4), NATURES_WRATH(117, 4), WILD_MOMENTUM(118, 4),
	//Spirit Hawk T4
	EAGLE_EYE(119, 4), GO_FOR_THE_EYES(120, 4), SWIFT_SPIRIT(121, 4),

	//Duelist T1
	STRENGTHENING_MEAL(128), ADVENTURERS_INTUITION(129), PATIENT_STRIKE(130), AGGRESSIVE_BARRIER(131),
	//Duelist T2
	FOCUSED_MEAL(132), LIQUID_AGILITY(133), WEAPON_RECHARGING(134), LETHAL_HASTE(135), SWIFT_EQUIP(136),
	//Duelist T3
	PRECISE_ASSAULT(137, 3), DEADLY_FOLLOWUP(138, 3),
	//Champion T3
	VARIED_CHARGE(139, 3), TWIN_UPGRADES(140, 3), COMBINED_LETHALITY(141, 3),
	//Monk T3
	UNENCUMBERED_SPIRIT(142, 3), MONASTIC_VIGOR(143, 3), COMBINED_ENERGY(144, 3),
	//Challenge T4
	CLOSE_THE_GAP(145, 4), INVIGORATING_VICTORY(146, 4), ELIMINATION_MATCH(147, 4),
	//Elemental Strike T4
	ELEMENTAL_REACH(148, 4), STRIKING_FORCE(149, 4), DIRECTED_POWER(150, 4),
	//Feint T4
	FEIGNED_RETREAT(151, 4), EXPOSE_WEAKNESS(152, 4), COUNTER_ABILITY(153, 4),

	//Cleric T1
	SATIATED_SPELLS(160), HOLY_INTUITION(161), SEARING_LIGHT(162), SHIELD_OF_LIGHT(163),
	//Cleric T2
	ENLIGHTENING_MEAL(164), RECALL_INSCRIPTION(165), SUNRAY(166), DIVINE_SENSE(167), BLESS(168),
	//Cleric T3
	CLEANSE(169, 3), LIGHT_READING(170, 3),
	//Priest T3
	HOLY_LANCE(171, 3), HALLOWED_GROUND(172, 3), MNEMONIC_PRAYER(173, 3),
	//Paladin T3
	LAY_ON_HANDS(174, 3), AURA_OF_PROTECTION(175, 3), WALL_OF_LIGHT(176, 3),
	//Ascended Form T4
	DIVINE_INTERVENTION(177, 4), JUDGEMENT(178, 4), FLASH(179, 4),
	//Trinity T4
	BODY_FORM(180, 4), MIND_FORM(181, 4), SPIRIT_FORM(182, 4),
	//Power of Many T4
	BEAMING_RAY(183, 4), LIFE_LINK(184, 4), STASIS(185, 4),

	//universal T4
	HEROIC_ENERGY(26, 4), //See icon() and title() for special logic for this one
	//Ratmogrify T4
	RATSISTANCE(215, 4), RATLOMACY(216, 4), RATFORCEMENTS(217, 4);
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Talent.class)
			.t("$provokedangertracker.name", "受衅怒火")
			.t("$provokedangertracker.desc", "战士刚刚失去护盾，他的下一次物理攻击将造成额外伤害。\n\n剩余回合数：%s")
			.t("$lingeringmagictracker.name", "波涌余威")
			.t("$lingeringmagictracker.desc", "法师刚刚使用了魔杖或法杖，他的下一次物理攻击将造成额外伤害。\n\n剩余回合数：%s")
			.t("$followupstriketracker.name", "追加打击")
			.t("$followupstriketracker.desc", "女猎手最近使用投掷武器发动过一次攻击，她对同一目标发动的下一次近战攻击将获得伤害加成。\n\n剩余回合数：%s")
			.t("$patientstriketracker.name", "沉稳一击")
			.t("$patientstriketracker.desc", "决斗家刚刚等待了一回合，她的下一次近战攻击将会造成额外伤害。")
			.t("$improvisedprojectilecooldown.name", "即兴投掷-冷却")
			.t("$improvisedprojectilecooldown.desc", "你刚刚发动过这个天赋，需要稍等才能再次发动。\n\n剩余冷却：%s回合")
			.t("$rejuvenatingstepscooldown.name", "复春步伐-冷却")
			.t("$rejuvenatingstepscooldown.desc", "你刚刚发动过这个天赋，需要稍等才能再次发动。\n\n剩余冷却：%s回合")
			.t("$seershotcooldown.name", "探地之矢-冷却")
			.t("$seershotcooldown.desc", "你刚刚发动过这个天赋，需要稍等才能再次发动。\n\n剩余冷却：%s回合")
			.t("$aggressivebarriercooldown.name", "烈气护盾-冷却")
			.t("$aggressivebarriercooldown.desc", "你刚刚发动过这个天赋，需要稍等才能再次发动。\n\n剩余冷却：%s回合")
			.t("$liquidagilacctracker.name", "液蕴机敏")
			.t("$liquidagilacctracker.desc", "决斗家的下一次普通近战攻击将获得额外精准。\n\n剩余回合数：%s")
			.t("$lethalhastecooldown.name", "夺命余势-冷却")
			.t("$lethalhastecooldown.desc", "你刚刚发动过这个天赋，需要稍等才能再次发动。\n\n剩余冷却：%s回合")
			.t("$swiftequipcooldown.name", "迅疾配装-冷却")
			.t("$swiftequipcooldown.desc", "你刚刚发动过这个天赋，需要稍等才能再次发动。\n\n剩余冷却：%s回合")
			.t("$preciseassaulttracker.name", "精准打击")
			.t("$preciseassaulttracker.desc", "决斗家的下一次普通近战攻击将获得精准加成。\n\n剩余回合数：%s")
			.t("$deadlyfollowuptracker.name", "夺命追击")
			.t("$deadlyfollowuptracker.desc", "决斗家最近使用投掷武器命中了一个敌人，她对同一目标发动的近战攻击将获得伤害加成。\n\n剩余回合数：%s")
			.t("$combinedlethalityabilitytracker.executed", "处决")
			.t("$satiatedspellstracker.name", "施法护盾")
			.t("$satiatedspellstracker.desc", "牧师下一次施法会为其提供少量护盾。")
			.t("$searinglightcooldown.name", "炽热之光")
			.t("$searinglightcooldown.desc", "你刚刚发动过这个天赋，需要稍等才能再次发动。\n\n剩余冷却：%s回合")
			.t("hearty_meal.title", "丰盛一餐")
			.t("hearty_meal.desc", "_+1：_ 战士在生命值不高于33%时进食将恢复_4点生命_。\n\n_+2：_ 战士在生命值不高于33%时进食将恢复_6点生命_。")
			.t("veterans_intuition.title", "老兵直觉")
			.t("veterans_intuition.desc", "_+1：_战士鉴定武器的速度提升至原来的_1.75倍_，鉴定护甲的速度提升至原来的_2.5倍_。\n\n_+2：_战士鉴定武器的速度提升至原来的_2.5倍_，且能在_装备护甲时_将其直接鉴定。")
			.t("provoked_anger.title", "受衅怒火")
			.t("provoked_anger.desc", "_+1：_当 战士所获的任何护盾效果被伤害击碎时，他的下一次物理攻击将造成_3点额外伤害_。\n\n_+2：_当战士所获的任何护盾效果被伤害击碎时，他的下一次物理攻击将造成_5点额外伤害_。")
			.t("iron_will.title", "钢铁意志")
			.t("iron_will.desc", "_+1：_战士的纹章所提供的护盾_增加1点_。\n\n_+2：_战士的纹章所提供的护盾_增加2点_。")
			.t("iron_will.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会提供1或2点最大护盾版本的战士的破损纹章效果。")
			.t("iron_stomach.title", "钢铁之胃")
			.t("iron_stomach.desc", "_+1：_战士进食只花费1回合，并在进食过程中获得_75%的伤害抗性_。\n\n_+2：_战士进食只花费1回合，并在进食过程中获得_100%的伤害抗性_。")
			.t("liquid_willpower.title", "液蕴意志")
			.t("liquid_willpower.desc", "_+1：_当战士饮用或投掷一瓶药剂、魔药或秘药时，他会获得他_最大生命值6.5%的护盾_。\n\n_+2：_当战士饮用或投掷一瓶药剂、魔药或秘药时，他会获得他_最大生命值10%的护盾_。\n\n如果使用的是力量药剂、经验药剂或须用前述药剂炼制的炼金物品，则获得的护盾量翻倍。\n\n对产量较高的炼金物品(如水爆魔药)，这项天赋会基于该物品的单次产出数量概率触发。")
			.t("runic_transference.title", "刻印转移")
			.t("runic_transference.desc", "_+1：_战士的破损纹章可以像携带一层升级一样携带_常见刻印_。\n\n_+2：_战士的破损纹章可以像携带一层升级一样携带_常见、强力或诅咒刻印_。\n\n破损纹章只能携带护甲贴附有纹章时刻在上面的刻印。")
			.t("runic_transference.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会使护甲刻印必定不会被升级卷轴消除的上限等级提高。天赋+1时，护甲在+6开始有概率消除刻印(原来在+4)，天赋+2时则为+7(原来在+4)。+8时仍然必定消除。")
			.t("lethal_momentum.title", "手起刀落")
			.t("lethal_momentum.desc", "_+1：_战士使用物理武器击杀敌人的一击有_67%的概率_不消耗回合数。\n\n_+2：_战士使用物理武器击杀敌人的一击有_100%的概率_不消耗回合数。")
			.t("improvised_projectiles.title", "即兴投掷")
			.t("improvised_projectiles.desc", "_+1：_战士向敌人扔出非投掷武器的物品时会对其造成_2回合_的致盲效果。这个天赋有50回合的冷却时间。\n\n_+2：_战士向敌人扔出非投掷武器的物品时会对其造成_3回合_的致盲效果。这个天赋有50回合的冷却时间。")
			.t("hold_fast.title", "不动如山")
			.t("hold_fast.desc", "_+1：_当战士装备着其纹章等待时，他可获得_1~2点护甲_并将连击和护盾的衰减速度减缓_50%_，直至他移动为止。\n\n_+2：_当战士装备着其纹章等待时，他可获得_2~4点护甲_并将连击和护盾的衰减速度减缓_75%_，直至他移动为止。\n\n_+3：_当战士装备着其纹章等待时，他可获得_3~6点护甲_并将连击和护盾的衰减速度减缓_100%_，直至他移动为止。")
			.t("hold_fast.meta_desc", "_如果这个天赋被其它英雄获得_，那么它不再需要战士纹章，且最多只能为你提供不超过你已装备的护甲的阶级加等级点护甲。")
			.t("strongman.title", "力大无穷")
			.t("strongman.desc", "_+1：_战士的力量提升_8%_，向下取整。\n\n_+2：_战士的力量提升_13%_，向下取整。\n\n_+3：_战士的力量提升_18%_，向下取整。")
			.t("endless_rage.title", "洪荒之怒")
			.t("endless_rage.desc", "_+1：_狂战士的怒气上限提升至_116%_。\n\n_+2：_狂战士的怒气上限提升至_133%_。\n\n_+3：_狂战士的怒气上限提升至_150%_。\n\n怒气值超过100%时，每1%额外怒气将会提升1%护盾，并降低1%的狂暴冷却，但伤害提升效果不会超过50%。")
			.t("deathless_fury.title", "不朽骤雨")
			.t("deathless_fury.desc", "_+1：_当狂战士即将死亡，且怒气值至少达到100%时，将自动狂暴化。触发后该效果需要_3个英雄等级_来冷却。\n\n_+2：_当狂战士即将死亡，且怒气值至少达到100%时，将自动狂暴化。触发后该效果需要_2个英雄等级_来冷却。\n\n_+3：_当狂战士即将死亡，且怒气值至少达到100%时，将自动狂暴化。触发后该效果需要_1个英雄等级_来冷却。\n\n注意，当狂暴结束时，若狂战士的生命为0，则仍会死亡。")
			.t("enraged_catalyst.title", "怒气导魔")
			.t("enraged_catalyst.desc", "_+1：_狂战士拥有越多怒气，他武器上的附魔或诅咒触发的概率越高，在100%怒气达到最大效果，触发概率_提升至1.15倍_。\n\n_+2：_狂战士拥有越多怒气，他武器上的附魔或诅咒触发的概率越高，在100%怒气达到最大效果，触发概率_提升至1.30倍_。\n\n_+3：_狂战士拥有越多怒气，他武器上的附魔或诅咒触发的概率越高，在100%怒气达到最大效果，触发概率_提升至1.45倍_。")
			.t("cleave.title", "连战热忱")
			.t("cleave.desc", "_+1：_当角斗士击杀一名敌人时，_30回合_内再次攻击仍继承连击数。\n\n_+2：_当角斗士击杀一名敌人时，_45回合_内再次攻击仍继承连击数。\n\n_+3：_当角斗士击杀一名敌人时，_60回合_内再次攻击仍继承连击数。")
			.t("lethal_defense.title", "以战养战")
			.t("lethal_defense.desc", "_+1：_当角斗士使用连击战技击杀一名敌人时，破损纹章护盾的剩余冷却时间会减少_50回合_。\n\n_+2：_当角斗士使用连击战技击杀一名敌人时，破损纹章护盾的剩余冷却时间会减少_100回合_。\n\n_+3：_当角斗士使用连击战技击杀一名敌人时，破损纹章护盾的剩余冷却时间会减少_150回合_。\n\n该天赋可将护盾剩余冷却时间减至最低-150回合，意味着护盾可在生效后立刻冷却完毕，以便再次激活护盾。")
			.t("enhanced_combo.title", "战技强化")
			.t("enhanced_combo.desc", "_+1：_当角斗士的连击数达到7或以上时，冲击的击退距离提升至3且附带眩晕，并可将敌人击落深渊。\n\n_+2：_除+1的增益外，当角斗士的连击数达到9或以上时，招架反击对一回合内的多次攻击有效。\n\n_+3：_除+1和+2的增益外，角斗士在使用撞击，横扫和暴雨时可跃过 连击数/3 格的地格来接近敌人。")
			.t("body_slam.title", "肉弹冲击")
			.t("body_slam.desc", "_+1：_战士英勇之跃落地时，对所有相邻敌人造成_1~4+25%护甲_的伤害。\n\n_+2：_战士英勇之跃落地时，对所有相邻敌人造成_2~8+50%护甲_的伤害。\n\n_+3：_战士英勇之跃落地时，对所有相邻敌人造成_3~12+75%护甲_的伤害。\n\n_+4：_战士英勇之跃落地时，对所有相邻敌人造成_4~16+100%护甲_的伤害。")
			.t("impact_wave.title", "堕天一击")
			.t("impact_wave.desc", "_+1：_战士英勇之跃落地时，击退所有相邻敌人_2格_，有_25%概率_使其获得5回合易伤。\n\n_+2：_战士英勇之跃落地时，击退所有相邻敌人_3格_，有_50%概率_使其获得5回合易伤。\n\n_+3：_战士英勇之跃落地时，击退所有相邻敌人_4格_，有_75%概率_使其获得5回合易伤。\n\n_+4：_战士英勇之跃落地时，击退所有相邻敌人_5格_，有_100%概率_使其获得5回合易伤。")
			.t("double_jump.title", "二段跳跃")
			.t("double_jump.desc", "_+1：_战士使用英勇跳跃后，3回合内下一次使用充能消耗减少_16%_。\n\n_+2：_战士使用英勇跳跃后，3回合内下一次使用充能消耗减少_30%_。\n\n_+3：_战士使用英勇跳跃后，3回合内下一次使用充能消耗减少_40%_。\n\n_+4：_战士使用英勇跳跃后，3回合内下一次使用充能消耗减少_50%_。")
			.t("expanding_wave.title", "广域冲击")
			.t("expanding_wave.desc", "_+1：_震地冲击的射程由5格扩大至_6格_，角度由60度展宽至_75度_。\n\n_+2：_震地冲击的射程由5格扩大至_7格_，角度由60度展宽至_90度_。\n\n_+3：_震地冲击的射程由5格扩大至_8格_，角度由60度展宽至_105度_。\n\n_+4：_震地冲击的射程由5格扩大至_9格_，角度由60度展宽至_120度_。")
			.t("striking_wave.title", "复合震波")
			.t("striking_wave.desc", "_+1：_使用震地冲击时，有_30%_概率使用附魔与连击等攻击效果。\n\n_+2：_使用震地冲击时，有_60%_概率使用附魔与连击等攻击效果。\n\n_+3：_使用震地冲击时，有_90%_概率使用附魔与连击等攻击效果。\n\n_+4：_使用震地冲击时，有_100%_概率使用附魔与连击等攻击效果，且获得_20%的附魔强化_。")
			.t("shock_force.title", "强力冲击")
			.t("shock_force.desc", "_+1：_震地冲击伤害增加_20%_，有_25%_概率将目标击晕而非残废。\n\n_+2：_震地冲击伤害增加_40%_，有_50%_概率将目标击晕而非残废。\n\n_+3：_震地冲击伤害增加_60%_，有_75%_概率将目标击晕而非残废。\n\n_+4：_震地冲击伤害增加_80%_，有_100%_概率将目标击晕而非残废。")
			.t("sustained_retribution.title", "持续反击")
			.t("sustained_retribution.desc", "_+1：_反击由一次额外伤害100%的攻击变为_2次_额外伤害_115%_的攻击。\n\n_+2：_反击由一次额外伤害100%的攻击变为_3次_额外伤害_130%_的攻击。\n\n_+3：_反击由一次额外伤害100%的攻击变为_4次_额外伤害_145%_的攻击。\n\n_+4：_反击由一次额外伤害100%的攻击变为_5次_额外伤害_160%_的攻击。")
			.t("shrug_it_off.title", "痛觉阈值")
			.t("shrug_it_off.desc", "_+1：_战士在苦痛坚忍中的减伤由50%提升至_60%_。\n\n_+2：_战士在苦痛坚忍中的减伤由50%提升至_68%_。\n\n_+3：_战士在苦痛坚忍中的减伤由50%提升至_74%_。\n\n_+4：_战士在苦痛坚忍中的减伤由50%提升至_80%_。")
			.t("even_the_odds.title", "情势反转")
			.t("even_the_odds.desc", "_+1：_战士在苦痛坚忍结束时，周围两格每有一名敌人，增加_5%额外伤害_。\n\n_+2：_战士在苦痛坚忍结束时，周围两格每有一名敌人，增加_10%额外伤害_。\n\n_+3：_战士在苦痛坚忍结束时，周围两格每有一名敌人，增加_15%额外伤害_。\n\n_+4：_战士在苦痛坚忍结束时，周围两格每有一名敌人，增加_20%额外伤害_。")
			.t("empowering_meal.title", "盈能一餐")
			.t("empowering_meal.desc", "_+1：_进食会为法师接下来3次法杖造成的伤害增加_2点额外伤害_。\n\n_+2：_进食会为法师接下来3次法杖造成的伤害增加_3点额外伤害_。")
			.t("scholars_intuition.title", "学者直觉")
			.t("scholars_intuition.desc", "_+1：_法师鉴定法杖的速度提升至原来的_3倍_。\n\n_+2：_法师能在_使用法杖时_将其直接鉴定。")
			.t("lingering_magic.title", "波涌余威")
			.t("lingering_magic.desc", "_+1：_当法师用魔杖或法杖施法后，他的下一次物理攻击将造成_1~2点额外伤害_。\n\n_+2：_当法师用魔杖或法杖施法后，他的下一次物理攻击将造成_2点额外伤害_。")
			.t("backup_barrier.title", "备用屏障")
			.t("backup_barrier.desc", "_+1：_每当法师释放魔杖最后一点充能，他获得_3点护盾_。\n\n_+2：_每当法师释放魔杖最后一点充能，他获得_5点护盾_。")
			.t("backup_barrier.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会由持有的等级最高的法杖触发。")
			.t("energizing_meal.title", "充能一餐")
			.t("energizing_meal.desc", "_+1：_法师进食只花费1回合，并获得_5回合的法杖充能_。\n\n_+2：_法师进食只花费1回合，并获得_8回合的法杖充能_。")
			.t("inscribed_power.title", "卷藏秘能")
			.t("inscribed_power.desc", "_+1：_当法师阅读卷轴或使用法术结晶后，他接下来的_2次法杖施法_提升2级。\n\n_+2：_当法师阅读卷轴或使用法术结晶后，他接下来的_3次法杖施法_提升2级。\n\n如果使用的是升级卷轴、嬗变卷轴或须用前述卷轴炼制的炼金物品，则法杖的强化施法次数翻倍。\n\n对产量较高的炼金物品(如大多数结晶)，这项天赋会基于该物品的单次产出数量概率触发。")
			.t("wand_preservation.title", "法杖回收")
			.t("wand_preservation.desc", "_+1：_当法师将新的法杖注入魔杖时，旧法杖会被回收为0级法杖，_但仅能执行一次法杖回收_。\n\n_+2：_当法师将新的法杖注入魔杖时，旧法杖会被回收为0级法杖，_英雄每升一级就能执行一次法杖回收_。")
			.t("wand_preservation.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会增加法杖于炼金釜中转化为奥术树脂的数量。+1时法杖转化的树脂数量增加1，+2时则增加2。")
			.t("arcane_vision.title", "奥术感知")
			.t("arcane_vision.desc", "_+1：_当法师对敌人施法时，获得对它们的灵视感知，持续_10回合_。\n\n_+2：_当法师对敌人施法时，获得对它们的灵视感知，持续_15回合_。")
			.t("shield_battery.title", "储能护盾")
			.t("shield_battery.desc", "_+1：_法师能以自身为目标施法，将法杖的每点充能转化为_4%最大生命值_的护盾。\n\n_+2：_法师能以自身为目标施法，将法杖的每点充能转化为_6%最大生命值_的护盾。")
			.t("desperate_power.title", "绝境迫能")
			.t("desperate_power.desc", "_+1：_法师的法杖和魔杖使用最后一点充能的施法效果获得_1级额外等级_。\n\n_+2：_法师的法杖和魔杖使用最后一点充能的施法效果获得_2级额外等级_。\n\n_+3：_法师的法杖和魔杖使用最后一点充能的施法效果获得_3级额外等级_。")
			.t("ally_warp.title", "移形换位")
			.t("ally_warp.desc", "_+1：_法师可以选取一名_2格范围内_的友方单位，与其瞬间交换位置。\n\n_+2：_法师可以选取一名_4格范围内_的友方单位，与其瞬间交换位置。\n\n_+3：_法师可以选取一名_6格范围内_的友方单位，与其瞬间交换位置。\n\n法师不能与无法移动的友方单位交换位置。")
			.t("empowered_strike.title", "蓄能打击")
			.t("empowered_strike.desc", "_+1：_战斗法师使用魔杖施法后，魔杖的首次近战攻击将造成_16%额外伤害_，且额外效果具有_50%强度提升_。\n\n_+2：_战斗法师使用魔杖施法后，魔杖的首次近战攻击将造成_33%额外伤害_，且额外效果具有_100%强度提升_。\n\n_+3：_战斗法师使用魔杖施法后，魔杖的首次近战攻击将造成_50%额外伤害_，且额外效果具有_150%强度提升_。")
			.t("mystical_charge.title", "充能秘术")
			.t("mystical_charge.desc", "_+1：_当战斗法师用魔杖近战击中敌人时，会获得相当于_0.5回合_的神器充能效果。\n\n_+2：_当战斗法师用魔杖近战击中敌人时，会获得相当于_1回合_的神器充能效果。\n\n_+3：_当战斗法师用魔杖近战击中敌人时，会获得相当于_1.5回合_的神器充能效果。")
			.t("excess_charge.title", "盈能屏障")
			.t("excess_charge.desc", "_+1：_战斗法师的魔杖满充能时，其上的每级升级都会在他下一次使用魔杖施法时使自身获得_0.67点护盾_。\n\n_+2：_战斗法师的魔杖满充能时，其上的每级升级都会在他下一次使用魔杖施法时使自身获得_1.33点护盾_。\n\n_+3：_战斗法师的魔杖满充能时，其上的每级升级都会在他下一次使用魔杖施法时使自身获得_2点护盾_。")
			.t("soul_siphon.title", "灵魂分食")
			.t("soul_siphon.desc", "_+1：_其他单位造成的物理伤害会以_13%效率_触发术士的灵魂标记。\n\n_+2：_其他单位造成的物理伤害会以_27%效率_触发术士的灵魂标记。\n\n_+3：_其他单位造成的物理伤害会以_40%效率_触发术士的灵魂标记。")
			.t("soul_eater.title", "噬魂秘法")
			.t("soul_eater.desc", "_+1：_灵魂标记单位受到的每点物理伤害都会提供_0.33回合_饥饿值；被灵魂标记的敌人死亡时，术士有_10%概率_触发进食特效。\n\n_+2：_灵魂标记单位受到的每点物理伤害都会提供_0.67回合_饥饿值；被灵魂标记的敌人死亡时，术士有_20%概率_触发进食特效。\n\n_+3：_灵魂标记单位受到的每点物理伤害都会提供_1回合_饥饿值；被灵魂标记的敌人死亡时，术士有_30%概率_触发进食特效。")
			.t("necromancers_minions.title", "怨灵爪牙")
			.t("necromancers_minions.desc", "_+1：_被灵魂标记的敌人死亡时，术士有_13%概率_将其唤起成为腐化的怨灵。\n\n_+2：_被灵魂标记的敌人死亡时，术士有_27%概率_将其唤起成为腐化的怨灵。\n\n_+3：_被灵魂标记的敌人死亡时，术士有_40%概率_将其唤起成为腐化的怨灵。")
			.t("blast_radius.title", "广域打击")
			.t("blast_radius.desc", "_+1：_元素风暴的半径由4格扩大至_5格_。\n\n_+2：_元素风暴的半径由4格扩大至_6格_。\n\n_+3：_元素风暴的半径由4格扩大至_7格_。\n\n_+4：_元素风暴的半径由4格扩大至_8格_。")
			.t("elemental_power.title", "元素之力")
			.t("elemental_power.desc", "_+1：_元素风暴的威力提升_25%_。\n\n_+2：_元素风暴的威力提升_50%_。\n\n_+3：_元素风暴的威力提升_75%_。\n\n_+4：_元素风暴的威力提升_100%_。")
			.t("reactive_barrier.title", "反应屏障")
			.t("reactive_barrier.desc", "_+1：_每有一个角色受元素风暴影响，法师获得_2.5点护盾_，最多计算5个角色。\n\n_+2：_每有一个角色受元素风暴影响，法师获得_5点护盾_，最多计算6个角色。\n\n_+3：_每有一个角色受元素风暴影响，法师获得_7.5点护盾_，最多计算7个角色。\n\n_+4：_每有一个角色受元素风暴影响，法师获得_10点护盾_，最多计算8个角色。")
			.t("wild_power.title", "狂野魔力")
			.t("wild_power.desc", "_+1：_释放狂野魔法时，法杖会被视为具有额外_2或3级_，最多可提升至_+4_。\n\n_+2：_释放狂野魔法时，法杖会被视为具有额外_3级_，最多可提升至_+5_。\n\n_+3：_释放狂野魔法时，法杖会被视为具有额外_3或4级_，最多可提升至_+6_。\n\n_+4：_释放狂野魔法时，法杖会被视为具有额外_4级_，最多可提升至_+7_。")
			.t("fire_everything.title", "法力倾泻")
			.t("fire_everything.desc", "_+1：_狂野魔法将释放法杖由4次提升至_5次_，并且每一根法杖有_25%的概率_可被释放3次。\n\n_+2：_狂野魔法将释放法杖由4次提升至_6次_，并且每一根法杖有_50%的概率_可被释放3次。\n\n_+3：_狂野魔法将释放法杖由4次提升至_7次_，并且每一根法杖有_75%的概率_可被释放3次。\n\n_+4：_狂野魔法将释放法杖由4次提升至_8次_，并且每一根法杖有_100%的概率_可被释放3次。")
			.t("conserved_magic.title", "节能施法")
			.t("conserved_magic.desc", "_+1：_狂野魔法每次释放法杖消耗的充能由0.5点下降至_0.33点_，并且狂野魔法有_25%概率_不花费时间。\n\n_+2：_狂野魔法每次释放法杖消耗的充能由0.5点下降至_0.225点_，并且狂野魔法有_50%概率_不花费时间。\n\n_+3：_狂野魔法每次释放法杖消耗的充能由0.5点下降至_0.15点_，并且狂野魔法有_75%概率_不花费时间。\n\n_+4：_狂野魔法每次释放法杖消耗的充能由0.5点下降至_0.1点_，并且狂野魔法有_100%概率_不花费时间。")
			.t("telefrag.title", "传送挤压")
			.t("telefrag.desc", "_+1：_当法师传送回信标与另一角色发生碰撞时，法师对其造成_10~15点_伤害，但自己会受到_5点伤害_。\n\n_+2：_当法师传送回信标与另一角色发生碰撞时，法师对其造成_20~30点_伤害，但自己会受到_10点伤害_。\n\n_+3：_当法师传送回信标与另一角色发生碰撞时，法师对其造成_30~45点_伤害，但自己会受到_15点伤害_。\n\n_+4：_当法师传送回信标与另一角色发生碰撞时，法师对其造成_40~60点_伤害，但自己会受到_20点伤害_。\n\n玩家不会死于自伤，且该伤害可由魔法抵抗效果减免。")
			.t("remote_beacon.title", "彼方信标")
			.t("remote_beacon.desc", "_+1：_法师可以在_4格_内的任意一处放置一个信标。\n\n_+2：_法师可以在_8格_内的任意一处放置一个信标。\n\n_+3：_法师可以在_12格_内的任意一处放置一个信标。\n\n_+4：_法师可以在_16格_内的任意一处放置一个信标。\n\n法师无法在其不可抵达的位置放置信标。")
			.t("longrange_warp.title", "长途传送")
			.t("longrange_warp.desc", "_+1：_法师能够跨层传送，但消耗_150%的充能_。\n\n_+2：_法师能够跨层传送，但消耗_117%的充能_。\n\n_+3：_法师能够跨层传送，且仅消耗_83%的充能_。\n\n_+4：_法师能够跨层传送，且仅消耗_50%的充能_。\n\n法师不能借助此能力离开封锁的楼层。")
			.t("cached_rations.title", "备用口粮")
			.t("cached_rations.desc", "_+1：_盗贼在探索后续几层地牢时，可以于箱子中找出_2包备用口粮_。\n\n_+2：_盗贼在探索后续几层地牢时，可以于箱子中找出_3包备用口粮_。\n\n备用口粮可快速食用，能回复中等饱食度、恢复5点生命值，并为暗影斗篷回复1点充能。")
			.t("thiefs_intuition.title", "窃贼直觉")
			.t("thiefs_intuition.desc", "_+1：_盗贼鉴定戒指的速度提升至原来的_2倍_，且能在_装备戒指时_鉴定其种类。\n\n_+2：_盗贼能在_装备戒指时_将其直接鉴定，且能在_拾取戒指时_鉴定其种类。")
			.t("sucker_punch.title", "阴险打击")
			.t("sucker_punch.desc", "_+1：_初次伏击一名敌人时盗贼将造成_1~2点额外伤害_。\n\n_+2：_初次伏击一名敌人时盗贼将造成_2点额外伤害_。")
			.t("protective_shadows.title", "暗影庇护")
			.t("protective_shadows.desc", "_+1：_盗贼在隐形时，每_2回合_获得一点护盾，最多积累_3点_。\n\n_+2：_盗贼在隐形时，每_1回合_获得一点护盾，最多积累_5点_。")
			.t("mystical_meal.title", "秘术祭食")
			.t("mystical_meal.desc", "_+1：_盗贼进食只花费1回合(食用备用口粮不花费回合)，并获得_3回合的神器充能_。\n\n_+2：_盗贼进食只花费1回合(食用备用口粮不花费回合)，并获得_5回合的神器充能_。\n\n这项天赋不能使丰饶之角对其本身充能。")
			.t("inscribed_stealth.title", "卷藏匿影")
			.t("inscribed_stealth.desc", "_+1：_盗贼阅读卷轴或使用法术结晶后会获得_3回合_的隐形。\n\n_+2：_盗贼阅读卷轴或使用法术结晶后会获得_5回合_的隐形。\n\n如果使用的是升级卷轴、嬗变卷轴或须用前述卷轴炼制的炼金物品，则获得的隐形效果时长翻倍。\n\n对产量较高的炼金物品(如大多数结晶)，这项天赋会基于该物品的单次产出数量概率触发。")
			.t("wide_search.title", "广域搜索")
			.t("wide_search.desc", "_+1：_盗贼的搜索范围由方形5x5增幅至_圆形7x7_。\n\n_+2：_盗贼的搜索范围由方形5x5增幅至_方形7x7_。")
			.t("wide_search.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会在+1时增幅搜索范围至5x5的圆形，+2时增幅至5x5的方形。")
			.t("silent_steps.title", "无声步伐")
			.t("silent_steps.desc", "_+1：_盗贼不会惊醒_距其3格或更远的敌人_。\n\n_+2：_盗贼不会惊醒_未与其相邻的敌人_。")
			.t("rogues_foresight.title", "盗贼直觉")
			.t("rogues_foresight.desc", "_+1：_当盗贼进入有隐藏房间的新楼层时，有_75%的概率察觉_该层有隐藏房间。\n\n_+2：_当盗贼进入有隐藏房间的新楼层时，有_100%的概率察觉_该层有隐藏房间。")
			.t("light_cloak.title", "轻便斗篷")
			.t("light_cloak.desc", "_+1：_盗贼不装备暗影斗篷也能使用其功能，但未装备时斗篷的充能速率会降至_25%_。\n\n_+2：_盗贼不装备暗影斗篷也能使用其功能，但未装备时斗篷的充能速率会降至_50%_。\n\n_+3：_盗贼不装备暗影斗篷也能使用其功能，但未装备时斗篷的充能速率会降至_75%_。")
			.t("light_cloak.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会在+1/+2/+3等级时提高7/13/20%的所有神器充能速度。")
			.t("enhanced_rings.title", "戒指强化")
			.t("enhanced_rings.desc", "_+1：_使用神器后的_3回合_内，盗贼的所有戒指都会提升1级。\n\n_+2：_使用神器后的_6回合_内，盗贼的所有戒指都会提升1级。\n\n_+3：_使用神器后的_9回合_内，盗贼的所有戒指都会提升1级。")
			.t("enhanced_lethality.title", "直击要害")
			.t("enhanced_lethality.desc", "_+1：_刺客每级阶段准备可以直接斩杀敌人的生命值阈值由3/10/20/50%提升至_4/13/27/67%_。\n\n_+2：_刺客每级阶段准备可以直接斩杀敌人的生命值阈值由3/10/20/50%提升至_5/17/33/83%_。\n\n_+3：_刺客每级阶段准备可以直接斩杀敌人的生命值阈值由3/10/20/50%提升至_6/20/40/100%_。")
			.t("assassins_reach.title", "超距索命")
			.t("assassins_reach.desc", "_+1：_刺客每级阶段准备的闪现距离由1/2/3/4格延长至_1/3/4/6格_。\n\n_+2：_刺客每级阶段准备的闪现距离由1/2/3/4格延长至_2/4/6/8格_。\n\n_+3：_刺客每级阶段准备的闪现距离由1/2/3/4格延长至_2/5/7/10格_。\n\n这种闪现可以越过危险地形或敌人，但不能穿透墙壁等坚固地形。")
			.t("bounty_hunter.title", "赏金猎手")
			.t("bounty_hunter.desc", "_+1：_被刺客用技能斩杀的敌人，依据准备阶段的等级，其战利品掉落概率提升_2/4/8/16%_。\n\n_+2：_被刺客用技能斩杀的敌人，依据准备阶段的等级，其战利品掉落概率提升_4/8/16/32%_。\n\n_+3：_被刺客用技能斩杀的敌人，依据准备阶段的等级，其战利品掉落概率提升_6/12/24/48%_。")
			.t("evasive_armor.title", "灵猴之护")
			.t("evasive_armor.desc", "_+1：_处于逸动状态时，每点超出护甲需求的力量使疾行者额外获得_1点闪避_。\n\n_+2：_处于逸动状态时，每点超出护甲需求的力量使疾行者额外获得_2点闪避_。\n\n_+3：_处于逸动状态时，每点超出护甲需求的力量使疾行者额外获得_3点闪避_。")
			.t("projectile_momentum.title", "飞速投掷")
			.t("projectile_momentum.desc", "_+1：_处于逸动状态时，疾行者的投掷武器具有额外的_50%精准与15%伤害_。\n\n_+2：_处于逸动状态时，疾行者的投掷武器具有额外的_100%精准与30%伤害_。\n\n_+3：_处于逸动状态时，疾行者的投掷武器具有额外的_150%精准与45%伤害_。")
			.t("speedy_stealth.title", "无人之境")
			.t("speedy_stealth.desc", "_+1：_疾行者在隐形时，每回合获得2层动量。\n\n_+2：_除获得+1的增益外，当疾行者隐形时，逸动效果持续的回合数不会下降。\n\n_+3：_除获得+1，+2的增益外，当疾行者隐形时，不论其是否处于逸动状态，他的移动速度都会翻倍。")
			.t("hasty_retreat.title", "猜我在哪")
			.t("hasty_retreat.desc", "_+1：_盗贼在闪现后获得_1回合_的极速与隐形。\n\n_+2：_盗贼在闪现后获得_2回合_的极速与隐形。\n\n_+3：_盗贼在闪现后获得_3回合_的极速与隐形。\n\n_+4：_盗贼在闪现后获得_4回合_的极速与隐形。")
			.t("body_replacement.title", "替身掩护")
			.t("body_replacement.desc", "_+1：_闪现时，盗贼会留下一个有_20点生命与1~3点护甲_的替身木桩。\n\n_+2：_闪现时，盗贼会留下一个有_40点生命与2~6点护甲_的替身木桩。\n\n_+3：_闪现时，盗贼会留下一个有_60点生命与3~9点护甲_的替身木桩。\n\n_+4：_闪现时，盗贼会留下一个有_80点生命与4~12点护甲_的替身木桩。\n\n同一时间只可存在一个替身木桩。")
			.t("shadow_step.title", "烟幕潜行")
			.t("shadow_step.desc", "_+1：_盗贼隐形时，烟幕爆炸可以瞬间发动，并且充能花费会_降低16%_。隐形时的烟幕爆炸不会致盲敌人，也不会触发其他天赋。\n\n_+2：_盗贼隐形时，烟幕爆炸可以瞬间发动，并且充能花费会_降低30%_。隐形时的烟幕爆炸不会致盲敌人，也不会触发其他天赋。\n\n_+3：_盗贼隐形时，烟幕爆炸可以瞬间发动，并且充能花费会_降低41%_。隐形时的烟幕爆炸不会致盲敌人，也不会触发其他天赋。\n\n_+4：_盗贼隐形时，烟幕爆炸可以瞬间发动，并且充能花费会_降低50%_。隐形时的烟幕爆炸不会致盲敌人，也不会触发其他天赋。")
			.t("fear_the_reaper.title", "恐惧蔓延")
			.t("fear_the_reaper.desc", "_+1：_具有夺命印记的敌人在生命值降为0时会获得_残废_效果。\n\n_+2：_具有夺命印记的敌人在生命值降为0时会获得_残废与恐惧_效果。\n\n_+3：_具有夺命印记的敌人在生命值降为0时会获得_残废与恐惧_效果，并且3格距离内的其他敌人也会获得_残废_效果。\n\n_+4：_具有夺命印记的敌人在生命值降为0时会获得_残废与恐惧_效果，并且3格距离内的其他敌人也会获得_残废与恐惧_效果。")
			.t("deathly_durability.title", "死神外衣")
			.t("deathly_durability.desc", "_+1：_死于夺命印记的敌人会赋予盗贼其被印记时生命值_13%_的护盾。\n\n_+2：_死于夺命印记的敌人会赋予盗贼其被印记时生命值_25%_的护盾。\n\n_+3：_死于夺命印记的敌人会赋予盗贼其被印记时生命值_38%_的护盾。\n\n_+4：_死于夺命印记的敌人会赋予盗贼其被印记时生命值_50%_的护盾。")
			.t("double_mark.title", "双重印记")
			.t("double_mark.desc", "_+1：_标记一个目标后立即标记另一目标时，标记的充能消耗降低_30%_。\n\n_+2：_标记一个目标后立即标记另一目标时，标记的充能消耗降低_50%_。\n\n_+3：_标记一个目标后立即标记另一目标时，标记的充能消耗降低_65%_。\n\n_+4：_标记一个目标后立即标记另一目标时，标记的充能消耗降低_75%_。")
			.t("shadow_blade.title", "影铸之刃")
			.t("shadow_blade.desc", "_+1：_暗影映像获得其主人每回合伤害的_8%_，有_25%_概率使用主人武器的附魔。\n\n_+2：_暗影映像获得其主人每回合伤害的_16%_，有_50%_概率使用主人武器的附魔。\n\n_+3：_暗影映像获得其主人每回合伤害的_24%_，有_75%_概率使用主人武器的附魔。\n\n_+4：_暗影映像获得其主人每回合伤害的_32%_，有_100%_概率使用主人武器的附魔。")
			.t("cloned_armor.title", "影塑之甲")
			.t("cloned_armor.desc", "_+1：_暗影映像获得其主人护甲值的_12%_，有_25%_概率使用主人护甲的刻印。\n\n_+2：_暗影映像获得其主人护甲值的_24%_，有_50%_概率使用主人护甲的刻印。\n\n_+3：_暗影映像获得其主人护甲值的_36%_，有_75%_概率使用主人护甲的刻印。\n\n_+4：_暗影映像获得其主人护甲值的_48%_，有_100%_概率使用主人护甲的刻印。")
			.t("perfect_copy.title", "完美克隆")
			.t("perfect_copy.desc", "_+1：_暗影映像额外获得其主人_10%_的最大生命值。并且盗贼可以瞬间与_1格_距离以内的映像交换位置。\n\n_+2：_暗影映像额外获得其主人_20%_的最大生命值，并且盗贼可以瞬间与_2格_距离以内的映像交换位置。\n\n_+3：_暗影映像额外获得其主人_30%_的最大生命值，并且盗贼可以瞬间与_3格_距离以内的映像交换位置。\n\n_+4：_暗影映像额外获得其主人_40%_的最大生命值，并且盗贼可以瞬间与_4格_距离以内的映像交换位置。")
			.t("natures_bounty.title", "自然馈赠")
			.t("natures_bounty.desc", "_+1：_女猎手在探索后续几层地牢时，可以于高草丛中找出隐藏的_4颗浆果_。\n\n_+2：_女猎手在探索后续几层地牢时，可以于高草丛中找出隐藏的_6颗浆果_。\n\n浆果可快速食用，能回复少量饱食度，还可能吃出一粒实用的种子。")
			.t("survivalists_intuition.title", "生存直觉")
			.t("survivalists_intuition.desc", "_+1：_女猎手鉴定投掷武器的速度提升至原来的_3倍_。\n\n_+2：_女猎手能在_使用投掷武器击中敌人时_将其直接鉴定。")
			.t("followup_strike.title", "追加打击")
			.t("followup_strike.desc", "_+1：_当女猎手用她的弓或其它投掷武器击中敌人时，她对该敌人的下一次近战攻击将造成_2点额外伤害_。\n\n_+2：_当女猎手用她的弓或其他投掷武器击中敌人时。她对该敌人的下一次近战攻击将造成_3点额外伤害_。")
			.t("natures_aid.title", "自然助力")
			.t("natures_aid.desc", "_+1：_视野内的植物的效果触发时，女猎手会获得_每3回合_衰减一次的0~2点树肤。\n\n_+2：_视野内的植物的效果触发时，女猎手会获得_每5回合_衰减一次的0~2点树肤。")
			.t("invigorating_meal.title", "活力一餐")
			.t("invigorating_meal.desc", "_+1：_女猎手进食只花费1回合(食用浆果不花费回合)，并获得_1回合的极速_。\n\n_+2：_女猎手进食只花费1回合(食用浆果不花费回合)，并获得_2回合的极速_。")
			.t("liquid_nature.title", "液蕴自然")
			.t("liquid_nature.desc", "_+1：_当女猎手饮用或投掷一瓶药剂、魔药或秘药时，周围会生出_最多4格高草丛_，且周围的敌人会被缠绕_1回合_。\n\n_+2：_当女猎手饮用或投掷一瓶药剂、魔药或秘药时，周围会生出_最多6格高草丛_，且周围的敌人会被缠绕_2回合_。\n\n如果使用的是力量药剂、经验药剂或须用前述药剂炼制的炼金物品，则高草丛的生长量和缠绕的时长均翻倍。\n\n对产量较高的炼金物品(如水爆魔药)，这项天赋会基于制成该物品的多少而有概率触发。")
			.t("rejuvenating_steps.title", "复春步伐")
			.t("rejuvenating_steps.desc", "_+1：_当女猎手踏上矮草或余烬时，它们会复生为高草，并立即被女猎手踩踏。这个天赋_有10回合的冷却时间_。\n\n_+2：_当女猎手踏上矮草或余烬时，它们会复生为高草，并立即被女猎手踩踏。这个天赋_有5回合的冷却时间_。\n\n若被动恢复效果被禁用或者英雄长时间内没有获得经验，此天赋只会生出枯草。")
			.t("heightened_senses.title", "敏锐感知")
			.t("heightened_senses.desc", "_+1：_女猎手能获得对_她周围2格范围内_所有单位的灵视感知。\n\n_+2：_女猎手能获得对_她周围3格范围内_所有单位的灵视感知。")
			.t("durable_projectiles.title", "矢石保养")
			.t("durable_projectiles.desc", "_+1：_女猎手手中的投掷武器获得_50%额外耐久_。\n\n_+2：_女猎手手中的投掷武器获得_75%额外耐久_。")
			.t("point_blank.title", "抵近射击")
			.t("point_blank.desc", "_+1：_当女猎手在近战距离使用投掷武器或灵能弓时，精准修正从-50%提升至_-25%_。\n\n_+2：_当女猎手在近战距离使用投掷武器或灵能弓时，精准修正从-50%提升至_正常精准_。\n\n_+3：_当女猎手在近战距离使用投掷武器或灵能弓时，精准修正从-50%提升至_+25%_。\n\n注意，非近战距离使用投掷武器或灵能弓时，总是获得+50%的精准修正。")
			.t("seer_shot.title", "探地之矢")
			.t("seer_shot.desc", "_+1：_当女猎手射箭击中地面时，会提供落点周围3x3的视野，_持续5回合_。这个天赋有20回合的冷却时间。\n\n_+2：_当女猎手射箭击中地面时，会提供落点周围3x3的视野，_持续10回合_。这个天赋有20回合的冷却时间。\n\n_+3：_当女猎手射箭击中地面时，会提供落点周围3x3的视野，_持续15回合_。这个天赋有20回合的冷却时间。")
			.t("seer_shot.meta_desc", "_如果这个天赋被其它英雄获得_，那么它可被任何投掷武器触发。")
			.t("farsight.title", "鹰眼远视")
			.t("farsight.desc", "_+1：_狙击手的视野范围_扩大25%_。\n\n_+2：_狙击手的视野范围_扩大50%_。\n\n_+3：_狙击手的视野范围_扩大75%_。")
			.t("shared_enchantment.title", "联动附魔")
			.t("shared_enchantment.desc", "_+1：_投掷武器_有33%概率_附带狙击手灵能弓上的附魔。\n\n_+2：_投掷武器_有67%概率_附带狙击手灵能弓上的附魔。\n\n_+3：_投掷武器_有100%概率_附带狙击手灵能弓上的附魔。\n\n无论投掷武器附有哪种附魔，均能正常触发灵能弓的附魔。")
			.t("shared_upgrades.title", "联动升级")
			.t("shared_upgrades.desc", "当狙击手以一件已升级的投掷武器攻击时，其每级升级都会延长1回合狙击标记持续时间并增加16%特殊攻击伤害。\n\n_+1：_狙击手的投掷武器被限制为最多提供_2级_加成，以延长_2回合_狙击标记持续时间并增加_33%_特殊攻击伤害。\n\n_+2：_狙击手的投掷武器被限制为最多提供_4级_加成，以延长_4回合_狙击标记持续时间并增加_67%_特殊攻击伤害。\n\n_+3：_狙击手的投掷武器被限制为最多提供_6级_加成，以延长_6回合_狙击标记持续时间并增加_100%_特殊攻击伤害。")
			.t("durable_tips.title", "持久药液")
			.t("durable_tips.desc", "_+1：_守望者的涂药飞镖拥有_2倍耐久_。\n\n_+2：_守望者的涂药飞镖拥有_3倍耐久_。\n\n_+3：_守望者的涂药飞镖拥有_4倍耐久_。")
			.t("barkskin.title", "树肤韧甲")
			.t("barkskin.desc", "_+1：_踏上未枯萎的高草或植物时，守望者获得_她等级0~33%_点，每回合衰减的树肤护甲。\n\n_+2：_踏上未枯萎的高草或植物时，守望者获得_她等级0~67%_点，每回合衰减的树肤护甲。\n\n_+3：_踏上未枯萎的高草或植物时，守望者获得_她等级0~100%_点，每回合衰减的树肤护甲。")
			.t("shielding_dew.title", "露水护体")
			.t("shielding_dew.desc", "_+1：_当守望者生命值满时，拾取露珠可以为守望者提供护盾，上限为守望者_20%的最大生命值_。\n\n_+2：_当守望者生命值满时，拾取露珠可以为守望者提供护盾，上限为守望者_40%的最大生命值_。\n\n_+3：_当守望者生命值满时，拾取露珠可以为守望者提供护盾，上限为守望者_60%的最大生命值_。")
			.t("fan_of_blades.title", "千叶刀扇")
			.t("fan_of_blades.desc", "_+1：_灵魂飞刃最多能命中_1个额外目标_，造成50%的伤害。目标须在视野内且与原目标同处于_30度_扇形内。\n\n_+2：_灵魂飞刃最多能命中_2个额外目标_，造成50%的伤害。目标须在视野内且与原目标同处于_60度_扇形内。\n\n_+3：_灵魂飞刃最多能命中_3个额外目标_，造成50%的伤害。目标须在视野内且与原目标同处于_90度_扇形内。\n\n_+4：_灵魂飞刃最多能命中_4个额外目标_，造成50%的伤害。目标须在视野内且与原目标同处于_120度_扇形内。")
			.t("projecting_blades.title", "索敌飞刃")
			.t("projecting_blades.desc", "_+1：_灵魂飞刃精准提升_25%_，最多能穿透_2格坚实地形_。\n\n_+2：_灵魂飞刃精准提升_50%_，最多能穿透_4格坚实地形_。\n\n_+3：_灵魂飞刃精准提升_75%_，最多能穿透_6格坚实地形_。\n\n_+4：_灵魂飞刃精准提升_100%_，最多能穿透_8格坚实地形_。")
			.t("spirit_blades.title", "灵能飞刃")
			.t("spirit_blades.desc", "_+1：_灵魂飞刃有_30%_的概率额外使用灵能弓的附魔。\n\n_+2：_灵魂飞刃有_60%_的概率额外使用灵能弓的附魔。\n\n_+3：_灵魂飞刃有_90%_的概率额外使用灵能弓的附魔。\n\n_+4：_灵魂飞刃有_100%_的概率额外使用灵能弓的附魔，且每种附魔都获得_10%的附魔强化_。")
			.t("growing_power.title", "茁壮之力")
			.t("growing_power.desc", "_+1：_自然之力对射击速度与移动速度的加成由33%与100%提升至_38%_与_125%_。\n\n_+2：_自然之力对射击速度与移动速度的加成由33%与100%提升至_42%_与_150%_。\n\n_+3：_自然之力对射击速度与移动速度的加成由33%与100%提升至_46%_与_175%_。\n\n_+4：_自然之力对射击速度与移动速度的加成由33%与100%提升至_50%_与_200%_。")
			.t("natures_wrath.title", "自然之怒")
			.t("natures_wrath.desc", "_+1：_在自然之力的加持下，灵能弓的射击有_8%的概率_触发一个随机的有害植物效果。\n\n_+2：_在自然之力的加持下，灵能弓的射击有_17%的概率_触发一个随机的有害植物效果。\n\n_+3：_在自然之力的加持下，灵能弓的射击有_25%的概率_触发一个随机的有害植物效果。\n\n_+4：_在自然之力的加持下，灵能弓的射击有_33%的概率_触发一个随机的有害植物效果。\n\n可能触发的植物有：致盲草、烈焰花、冰冠花、断肠苔、风暴藤。")
			.t("wild_momentum.title", "自然飞矢")
			.t("wild_momentum.desc", "_+1：_使用灵能弓射杀一名敌人时，延长_1回合_自然之力，最多延长_2回合_。\n\n_+2：_使用灵能弓射杀一名敌人时，延长_2回合_自然之力，最多延长_4回合_。\n\n_+3：_使用灵能弓射杀一名敌人时，延长_3回合_自然之力，最多延长_6回合_。\n\n_+4：_使用灵能弓射杀一名敌人时，延长_4回合_自然之力，最多延长_8回合_。")
			.t("eagle_eye.title", "鹰之瞭望")
			.t("eagle_eye.desc", "_+1：_飞鹰的视野范围由6格提升至_7格_。\n\n_+2：_飞鹰的视野范围由6格提升至_8格_。\n\n_+3：_飞鹰的视野范围由6格提升至_9格_，并获得_2格_的灵视感知。\n\n_+4：_飞鹰的视野范围由6格提升至_10格_，并获得_3格_的灵视感知。")
			.t("go_for_the_eyes.title", "夺目利爪")
			.t("go_for_the_eyes.desc", "_+1：_飞鹰在攻击时会扑抓敌人的眼睛并令其致盲_2回合_。\n\n_+2：_飞鹰在攻击时会扑抓敌人的眼睛并令其致盲_5回合_。\n\n_+3：_飞鹰在攻击时会扑抓敌人的眼睛并令其致盲_5回合_，并附加_2回合_的残废。\n\n_+4：_飞鹰在攻击时会扑抓敌人的眼睛并令其致盲_5回合_，并附加_5回合_的残废。")
			.t("swift_spirit.title", "疾风之魄")
			.t("swift_spirit.desc", "_+1：_飞鹰的移动速度由每回合2格提升至_2.5格_，必定闪避其遭到的前_2次攻击_。\n\n_+2：_飞鹰的移动速度由每回合2格提升至_3格_，必定闪避其遭到的前_4次攻击_。\n\n_+3：_飞鹰的移动速度由每回合2格提升至_3.5格_，必定闪避其遭到的前_6次攻击_。\n\n_+4：_飞鹰的移动速度由每回合2格提升至_4格_，必定闪避其遭到的前_8次攻击_。")
			.t("strengthening_meal.title", "强健一餐")
			.t("strengthening_meal.desc", "_+1：_进食会使决斗家接下来对敌人造成的_2次物理攻击_增加3点额外伤害。\n\n_+2：_进食会使决斗家接下来对敌人造成的_3次物理攻击_增加3点额外伤害。")
			.t("adventurers_intuition.title", "探险直觉")
			.t("adventurers_intuition.desc", "_+1：_决斗家鉴定武器的速度提升至原来的_2.5倍_，鉴定护甲的速度提升至原来的_1.75倍_。\n_+2：_决斗家能在_装备武器时_将其直接鉴定，且鉴定护甲的速度提升至原来的_2.5倍_。")
			.t("patient_strike.title", "沉稳一击")
			.t("patient_strike.desc", "_+1：_如果决斗家在进行一次近战攻击前进行等待，该次攻击将会造成_1~2点额外伤害_。\n_+2：_如果决斗家在进行一次近战攻击前进行等待，该次攻击将会造成_2点额外伤害_。")
			.t("aggressive_barrier.title", "烈气护盾")
			.t("aggressive_barrier.desc", "_+1：_决斗家在生命值不高于50%最大生命值时，使用武技会获得_3点护盾_。\n\n_+2：_决斗家在生命值不高于50%最大生命值时，使用武技会获得_5点护盾_。")
			.t("aggressive_barrier.meta_desc", "_如果这个天赋被其它英雄获得_，那么此天赋变为在低生命值时使用近战攻击获得此护盾。此时这个天赋有50回合的冷却时间。")
			.t("focused_meal.title", "专注一餐")
			.t("focused_meal.desc", "_+1：_决斗家进食只花费1回合，并获得_0.67点武技充能_。\n\n_+2：_决斗家进食只花费1回合，并获得_1点武技充能_。")
			.t("focused_meal.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会在+1等级时使英雄的下一次物理攻击额外造成英雄等级/3点伤害，+2等级时额外造成英雄等级/2点伤害。")
			.t("liquid_agility.title", "液蕴机敏")
			.t("liquid_agility.desc", "_+1：_决斗家在饮用或投掷一瓶药剂、魔药或秘药的期间拥有_4倍闪避_，并且她在5回合内的下一次近战攻击拥有_3倍精准_。\n\n_+2：_决斗家在饮用或投掷一瓶药剂、魔药或秘药的期间拥有_无限闪避_，并且她在5回合内的下一次近战攻击拥有_无限精准_。\n\n如果使用的是力量药剂、经验药剂或须用前述药剂炼制的炼金物品，则闪避加成会额外持续一回合，精准加成会额外持续一次攻击。\n\n对产量较高的炼金物品(如水爆魔药)，这项天赋会基于该物品的单次产出数量概率触发。")
			.t("weapon_recharging.title", "武器充能")
			.t("weapon_recharging.desc", "_+1：_决斗家在处于法杖或神器充能状态时，每_15回合_恢复1点武技充能。\n\n_+2：_决斗家在处于法杖或神器充能状态时，每_10回合_恢复1点武技充能。")
			.t("weapon_recharging.meta_desc", "_如果这个天赋被其它英雄获得_，那么处于任一充能状态的情况下，+1时造成额外5%近战伤害，+2时造成额外7.5%近战伤害。")
			.t("lethal_haste.title", "夺命余势")
			.t("lethal_haste.desc", "_+1：_决斗家用武技击杀一名敌人后，获得_3回合_瞬间移动效果。\n\n_+2：_决斗家用武技击杀一名敌人后，获得_5回合_瞬间移动效果。")
			.t("lethal_haste.meta_desc", "_如果这个天赋被其它英雄获得_，那么此天赋可以由常规的武器攻击触发，但会有100回合的冷却时间。")
			.t("swift_equip.title", "迅疾配装")
			.t("swift_equip.desc", "_+1：_决斗家每20回合可以瞬时更换_一次_装备的武器。\n\n_+2：_决斗家每20回合可以瞬时更换_两次_装备的武器。\n\n当决斗家获得此天赋，且其不处于冷却时间时，点击快捷栏中的未装备武器时会瞬时装备。")
			.t("precise_assault.title", "精准打击")
			.t("precise_assault.desc", "_+1：_当决斗家使用了一个武技，她在5回合内的下一次近战攻击将具有_2倍精准_。\n\n_+2：_当决斗家使用了一个武技，她在5回合内的下一次近战攻击将具有_5倍精准_。\n\n_+3：_当决斗家使用了一个武技，她在5回合内的下一次近战攻击将具有_无限精准_。")
			.t("precise_assault.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会在+1/+2/+3等级时提升10/20/30%的近战精准。")
			.t("deadly_followup.title", "夺命追击")
			.t("deadly_followup.desc", "_+1：_当决斗家使用投掷武器命中敌人，她在随后5回合对其造成的近战伤害增加_10%_。\n\n_+2：_当决斗家使用投掷武器命中敌人，她在随后5回合对其造成的近战伤害增加_20%_。\n\n_+3：_当决斗家使用投掷武器命中敌人，她在随后5回合对其造成的近战伤害增加_30%_。")
			.t("varied_charge.title", "异技充能")
			.t("varied_charge.desc", "_+1：_当勇士分别使用了两种不同的武技时，她将立即获得_0.17点武技充能_。使用两种武技间隔的回合数没有限制。\n\n_+2：_当勇士分别使用了两种不同的武技时，她将立即获得_0.33点武技充能_。使用两种武技间隔的回合数没有限制。\n\n_+3：_当勇士分别使用了两种不同的武技时，她将立即获得_0.5点武技充能_。使用两种武技间隔的回合数没有限制。")
			.t("twin_upgrades.title", "伴生强化")
			.t("twin_upgrades.desc", "_+1：_如果勇士所双持的武器，其中一把等级较低且阶数比另一把_低出至少2阶_，则将其等级加强至与另一把相同。\n\n_+2：_如果勇士所双持的武器，其中一把等级较低且阶数比另一把_低出至少1阶_，则将其等级加强至与另一把相同。\n\n_+3：_如果勇士所双持的武器，其中一把等级较低且阶数_不高于_另一把，则将其等级加强至与另一把相同。")
			.t("combined_lethality.title", "复合损伤")
			.t("combined_lethality.desc", "_+1：_如果勇士在使用一把武器的武技后立刻使用不同的近战武器进行攻击，则此次攻击将处决_生命值低于13%_的非Boss敌人。\n\n_+2：_如果勇士在使用一把武器的武技后立刻用不同的近战武器进行攻击，则此次攻击将处决_生命值低于27%_的非Boss敌人\n\n_+3：_如果勇士在使用一把武器的武技后立刻用不同的近战武器进行攻击，则此次攻击将处决_生命值低于40%_的非Boss敌人。\n\n此次攻击可以是一次普通的近战武器攻击，也可以是武技中的一部分。")
			.t("unencumbered_spirit.title", "无羁之魂")
			.t("unencumbered_spirit.desc", "_+1：_每使用一件_3阶及以下_的装备，武僧获取的内力就会_+50%_。\n\n_+2：_每使用一件_2阶及以下_的装备，武僧获取的内力就会_+75%_。3阶装备收益不变。\n\n_+3：_每使用一件_1阶_的装备，武僧获取的内力就会_+100%_。2，3阶装备收益不变。她还会免费获得一件布甲与一副镶钉手套。\n\n注意，使用赤手空拳或武力之戒攻击时该天赋无收益。")
			.t("monastic_vigor.title", "道心盎然")
			.t("monastic_vigor.desc", "_+1：_如果武僧拥有_100%的内力_，则各门武功会得到突破。\n\n_+2：_如果武僧拥有_80%的内力_，则各门武功会得到突破。\n\n_+3：_如果武僧拥有_60%的内力_，则各门武功会得到突破。\n\n当处于突破状态：\n-空振使用你的武器附魔。\n-凝神不消耗回合。\n-登云的冲刺距离+4。\n-盘龙造成的伤害+50%，可击退并击晕所有邻近的敌人。\n-冥思可缓慢回复20%的已损生命值并提供80%的伤害抗性。")
			.t("combined_energy.title", "阴阳调和")
			.t("combined_energy.desc", "_+1：_如果武僧在5回合内使用了一次武技与一门_4+内力消耗_的武功，她就会回复1点内力。\n\n_+2：_如果武僧在5回合内使用了一次武技与一门_3+内力消耗_的武功，她就会回复1点内力。\n\n_+3：_如果武僧在5回合内使用了一次武技与一门_2+内力消耗_的武功，她就会回复1点内力。")
			.t("close_the_gap.title", "跨越彼端")
			.t("close_the_gap.desc", "_+1：_发起决斗时，决斗家会向她的目标闪现_至多2格_距离。\n\n_+2：_发起决斗时，决斗家会向她的目标闪现_至多3格_距离。\n\n_+3：_发起决斗时，决斗家会向她的目标闪现_至多4格_距离。\n\n_+4：_发起决斗时，决斗家会向她的目标闪现_至多5格_距离。\n\n这种闪现可以越过危险地形或敌人，但不能穿透墙壁等坚固地形。闪现距离也会纳入决斗发起距离的判定。")
			.t("invigorating_victory.title", "凯旋复苏")
			.t("invigorating_victory.desc", "_+1：_如果决斗家在决斗结束前击败了目标，则恢复_5+30%决斗中承伤_的生命值。\n\n_+2：_如果决斗家在决斗结束前击败了目标，则恢复_10+50%决斗中承伤_的生命值。\n\n_+3：_如果决斗家在决斗结束前击败了目标，则恢复_15+65%决斗中承伤_的生命值。\n\n_+4：_如果决斗家在决斗结束前击败了目标，则恢复_20+75%决斗中承伤_的生命值。")
			.t("elimination_match.title", "连续淘汰")
			.t("elimination_match.desc", "_+1：_如果决斗家在决斗结束之后的3回合内再次发起决斗，那么该次决斗消耗的充能_减少16%_。\n\n_+2：_如果决斗家在决斗结束之后的3回合内再次发起决斗，那么该次决斗消耗的充能_减少30%_。\n\n_+3：_如果决斗家在决斗结束之后的3回合内再次发起决斗，那么该次决斗消耗的充能_减少40%_。\n\n_+4：_如果决斗家在决斗结束之后的3回合内再次发起决斗，那么该次决斗消耗的充能_减少50%_。")
			.t("elemental_reach.title", "元素延展")
			.t("elemental_reach.desc", "_+1：_元素打击的效果范围由4格扩大至_5格_，扩散角度由65度展宽至_75度_。\n\n_+2：_元素打击的效果范围由4格扩大至_6格_，扩散角度由65度展宽至_85度_。\n\n_+3：_元素打击的效果范围由4格扩大至_7格_，扩散角度由65度展宽至_95度_。\n\n_+4：_元素打击的效果范围由4格扩大至_8格_，扩散角度由65度展宽至_105度_。")
			.t("striking_force.title", "强力打击")
			.t("striking_force.desc", "_+1：_元素打击的效果强度提升_30%_。\n\n_+2：_元素打击的效果强度提升_60%_。\n\n_+3：_元素打击的效果强度提升_90%_。\n\n_+4：_元素打击的效果强度提升_120%_。")
			.t("directed_power.title", "能量导引")
			.t("directed_power.desc", "_+1：_元素打击效果范围内每有一个敌人，对主要目标的直接攻击就会获得_+30%附魔强度_。\n\n_+2：_元素打击效果范围内每有一个敌人，对主要目标的直接攻击就会获得_+60%附魔强度_。\n\n_+3：_元素打击效果范围内每有一个敌人，对主要目标的直接攻击就会获得_+90%附魔强度_。\n\n_+4：_元素打击效果范围内每有一个敌人，对主要目标的直接攻击就会获得_+120%附魔强度_。")
			.t("feigned_retreat.title", "佯装撤退")
			.t("feigned_retreat.desc", "_+1：_如果一个敌人攻击了决斗家的残影，那么决斗家会获得_2回合_极速。\n\n_+2：_如果一个敌人攻击了决斗家的残影，那么决斗家会获得_4回合_极速。\n\n_+3：_如果一个敌人攻击了决斗家的残影，那么决斗家会获得_6回合_极速。\n\n_+4：_如果一个敌人攻击了决斗家的残影，那么决斗家会获得_8回合_极速。")
			.t("expose_weakness.title", "弱点看破")
			.t("expose_weakness.desc", "_+1：_攻击了决斗家残影的敌人会获得_2回合_虚弱和易伤。\n\n_+2：_攻击了决斗家残影的敌人会获得_4回合_虚弱和易伤。\n\n_+3：_攻击了决斗家残影的敌人会获得_6回合_虚弱和易伤。\n\n_+4：_攻击了决斗家残影的敌人会获得_8回合_虚弱和易伤。")
			.t("counter_ability.title", "武技反击")
			.t("counter_ability.desc", "_+1：_如果决斗家在残影受到攻击后的3回合内使用了一次武技，那么她将立即回复_0.38点武技充能_。\n\n_+2：_如果决斗家在残影受到攻击后的3回合内使用了一次武技，那么她将立刻回复_0.77点武技充能_。\n\n_+3：_如果决斗家在残影受到攻击后的3回合内使用了一次武技，那么她将立刻回复_1.13点武技充能_。\n\n_+4：_如果决斗家在残影受到攻击后的3回合内使用了一次武技，那么她将立刻回复_1.5点武技充能_。")
			.t("satiated_spells.title", "圣餐礼文")
			.t("satiated_spells.desc", "_+1：_进食会使牧师在下一次施法时为其提供_3点护盾_。\n\n_+2：_进食会使牧师在下一次施法时为其提供_5点护盾_。")
			.t("satiated_spells.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会立即给予将在10回合后开始正常衰减的护盾。")
			.t("holy_intuition.title", "神圣预知")
			.t("holy_intuition.desc", "_+1：_牧师可以施放法术_神圣预知_，消耗_3点充能_以揭示物品有无诅咒。\n\n_+2：_牧师可以施放法术_神圣预知_，消耗_2点充能_以揭示物品有无诅咒。")
			.t("holy_intuition.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会在+1/+2等级时使英雄有15%/25%的概率在装备诅咒装备之前将其鉴定。")
			.t("searing_light.title", "灼热之光")
			.t("searing_light.desc", "_+1：_牧师对被_神导之光_施加光耀的敌人的物理攻击造成_3点额外伤害_。\n\n_+2：_牧师对被_神导之光_施加光耀的敌人的物理攻击造成_5点额外伤害_。")
			.t("searing_light.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会使攻击在对敌人使用法杖或神器后造成额外伤害，带有20回合的冷却时间。")
			.t("shield_of_light.title", "神圣护盾")
			.t("shield_of_light.desc", "_+1：_牧师可以施放法术_神圣护盾_，消耗1点充能以立即获得在5回合内仅对指定目标有效的_2~4点护甲_。\n\n_+2：_牧师可以施放法术_神圣护盾_，消耗1点充能以立即获得在5回合内仅对指定目标有效的_3~6点护甲_。")
			.t("shield_of_light.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会在+1/+2等级时有33/50%的概率减少作为你的攻击目标的敌人所造成的1点物理伤害。")
			.t("enlightening_meal.title", "启蒙圣餐")
			.t("enlightening_meal.desc", "_+1：_牧师进食只花费1回合，并获得_0.67点圣典充能_。\n\n_+2：_牧师进食只花费1回合，并获得_1点圣典充能_。")
			.t("enlightening_meal.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会在+1/+2等级时使英雄获得2/3回合的法杖充能和神器充能效果。这项天赋不能使丰饶之角对其本身充能。")
			.t("recall_inscription.title", "卷藏咒言")
			.t("recall_inscription.refunded", "你的物品已被返还！")
			.t("recall_inscription.desc", "_+1：_牧师可以施放法术_卷藏咒言_，以再次触发_10回合_内最近一次使用的符石或卷轴效果。\n\n_+2：_牧师可以施放法术_卷藏咒言_，以再次触发_300回合_内最近一次使用的符石或卷轴效果。\n\n卷藏咒言不能复制升级卷轴，充能消耗根据最近一次使用的物品变化而变化：符石2点充能、卷轴3点充能、秘卷4点充能。复制嬗变卷轴或嬗变/升级卷轴的符石/秘卷时，充能消耗还会翻倍。")
			.t("recall_inscription.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会在+1/+2等级时有10%/15%的概率返还升级卷轴除外的任何卷轴或符石。")
			.t("sunray.title", "阳炎射线")
			.t("sunray.desc", "_+1：_牧师可以施放法术_阳炎射线_，消耗1点充能造成_4~8_点伤害并使目标失明_4回合_。\n\n_+2：_牧师可以施放法术_阳炎射线_，消耗1点充能造成_6~12_点伤害并使目标失明_6回合_。\n\n阳炎射线只能致盲目标一次，但若目标已被阳炎射线致盲则会以麻痹代之。阳炎射线必定对恶魔和亡灵敌人造成最大伤害。")
			.t("sunray.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会在+1/+2等级时使法杖或神器有15%/25%的概率致盲敌人4回合。")
			.t("divine_sense.title", "神圣感知")
			.t("divine_sense.desc", "_+1：_牧师可以施放法术_神圣感知_，消耗2点充能以不耗时获得持续50回合，_8格范围_的灵视感知。\n\n_+2：_牧师可以施放法术_神圣感知_，消耗2点充能以不耗时获得持续50回合，_12格范围_的灵视感知。")
			.t("divine_sense.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会在+1/+2等级时使英雄在使用法杖或神器后暂时获得3/5格内的灵视感知。")
			.t("bless.title", "神圣祝福")
			.t("bless.desc", "_+1：_牧师可以施放法术_神圣祝福_，消耗1点充能以使自身获得_6回合赐福与10点护盾_或使其他单位获得_10回合赐福与10点治疗_。\n\n_+2：_牧师可以施放法术_神圣祝福_，消耗1点充能以使自身获得_10回合赐福与15点护盾_或使其他单位获得_15回合赐福与15点治疗_。\n\n该法术的溢出治疗将被转化为护盾。")
			.t("bless.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会在+1/+2等级时提升英雄和所有盟友3%/5%的精准和闪避。")
			.t("cleanse.title", "神圣净化")
			.t("cleanse.desc", "_+1：_牧师可以施放法术_神圣净化_，消耗2点充能以使自身和附近任何盟友_清除负面状态效果_并获得_10点护盾_。\n\n_+2：_牧师可以施放法术_神圣净化_，消耗2点充能以使自身和附近任何盟友获得_3回合全面净化_与_20点护盾_。\n\n_+3：_牧师可以施放法术_神圣净化_，消耗2点充能以使自身和附近任何盟友获得_5回合全面净化_与_30点护盾_。")
			.t("cleanse.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会在+1/+2/+3等级时使英雄在使用法杖或神器后有10/20/30%的概率净化负面状态效果。")
			.t("light_reading.title", "轻量阅读")
			.t("light_reading.desc", "_+1：_牧师不装备圣典也能使用其功能，但未装备时圣典的充能速率会降至_25%_。\n\n_+2：_牧师不装备圣典也能使用其功能，但未装备时圣典的充能速率会降至_50%_。\n\n_+3：_牧师不装备圣典也能使用其功能，但未装备时圣典的充能速率会降至_75%_。")
			.t("light_reading.meta_desc", "_如果这个天赋被其它英雄获得_，那么它会在+1/+2/+3等级时提升7/13/20%的所有法杖充能速度。")
			.t("holy_lance.title", "神圣标枪")
			.t("holy_lance.desc", "_+1：_祭司可以施放法术_神圣标枪_，消耗4点充能以造成致命的_30~55点伤害_。\n\n_+2：_祭司可以施放法术_神圣标枪_，消耗4点充能以造成致命的_45~83点伤害_。\n\n_+3：_祭司可以施放法术_神圣标枪_，消耗4点充能以造成致命的_60~110点伤害_。\n\n圣枪必定对恶魔和亡灵敌人造成最大伤害。圣枪有30回合的冷却，之后才能再次施放。")
			.t("hallowed_ground.title", "神圣领域")
			.t("hallowed_ground.desc", "_+1：_祭司可以施放法术_神圣领域_，消耗2点充能以创造_3x3范围_，持续20回合的神圣领域。\n\n_+2：_祭司可以施放法术_神圣领域_，消耗2点充能以创造_5x5范围_，持续20回合的神圣领域。\n\n_+3：_祭司可以施放法术_神圣领域_，消耗2点充能以创造_7x7范围_，持续20回合的神圣领域。\n\n施放时，法术会立即治疗盟友15点生命值，短暂缠绕敌人并扩散矮草。之后，神圣领域会缓慢治疗盟友、残疾敌人并随机催生高草。神圣领域会为祭司提供护盾而非治疗，并且神圣领域会被火焰摧毁。")
			.t("mnemonic_prayer.title", "祈愿诗篇")
			.t("mnemonic_prayer.desc", "_+1：_祭司可以施放法术_祈愿诗篇_，消耗1点充能以延长_3回合_盟友/敌人的增益/减益效果。\n\n_+2：_祭司可以施放法术_祈愿诗篇_，消耗1点充能以延长_4回合_盟友/敌人的增益/减益效果。\n\n_+3：_祭司可以施放法术_祈愿诗篇_，消耗1点充能以延长_5回合_盟友/敌人的增益/减益效果。\n\n祈愿诗篇施法不耗时。祈愿诗篇只能延长一次特定目标的增益或减益效果，并且不能延长来自护甲技能的增益效果。")
			.t("lay_on_hands.title", "圣疗之触")
			.t("lay_on_hands.desc", "_+1：_圣骑士可以施放法术_圣疗之触_，消耗1点充能以立即治疗附近一个单位_15点生命值_，或使圣骑士获得_15点护盾_。\n\n_+2：_圣骑士可以施放法术_圣疗之触_，消耗1点充能以立即治疗附近一个单位_20点生命值_，或使圣骑士获得_20点护盾_。\n\n_+3：_圣骑士可以施放法术_圣疗之触_，消耗1点充能以立即治疗附近一个单位_25点生命值_，或使圣骑士获得_25点护盾_。\n\n该法术的溢出治疗将被转化为护盾。圣疗可被重复施放，但无法立即施加超过三次施法数值的护盾。")
			.t("aura_of_protection.title", "守御灵光")
			.t("aura_of_protection.desc", "_+1：_圣骑士可以施放法术_守御灵光_，消耗2点充能以使圣骑士与2格范围内的所有盟友获得_20%伤害减免_与圣骑士护甲_50%刻印强化_。\n\n_+2：_圣骑士可以施放法术_守御灵光_，消耗2点充能以使圣骑士与2格范围内的所有盟友获得_30%伤害减免_与圣骑士护甲_75%刻印强化_。\n\n_+3：_圣骑士可以施放法术_守御灵光_，消耗2点充能以使圣骑士与2格范围内的所有盟友获得_40%伤害减免_与圣骑士护甲_100%刻印强化_。")
			.t("wall_of_light.title", "神圣屏障")
			.t("wall_of_light.desc", "_+1：_圣骑士可以施放法术_神圣屏障_，消耗3点充能以在圣骑士面前创造一面持续20回合，_3格宽_，可击退敌人的坚固墙壁。\n\n_+2：_圣骑士可以施放法术_神圣屏障_，消耗3点充能以在圣骑士面前创造一面持续20回合，_5格宽_，可击退敌人的坚固墙壁。\n\n_+3：_圣骑士可以施放法术_神圣屏障_，消耗3点充能以在圣骑士面前创造一面持续20回合，_7格宽_，可击退敌人的坚固墙壁。\n\n神圣屏障可被正向或斜向施放，同时只能存在一面屏障。")
			.t("divine_intervention.title", "神圣干预")
			.t("divine_intervention.desc", "_+1：_开启超凡升天时，牧师可以施放法术_神圣干预_，以强化牧师与所有盟友的护盾至_150点_并延长超凡升天_3回合_。\n\n_+2：_开启超凡升天时，牧师可以施放法术_神圣干预_，以强化牧师与所有盟友的护盾至_200点_并延长超凡升天_4回合_。\n\n_+3：_开启超凡升天时，牧师可以施放法术_神圣干预_，以强化牧师与所有盟友的护盾至_250点_并延长超凡升天_5回合_。\n\n_+4：_开启超凡升天时，牧师可以施放法术_神圣干预_，以强化牧师与所有盟友的护盾至_300点_并延长超凡升天_6回合_。\n\n神圣干预消耗高达5点圣典充能，并且在同次超凡升天中只能施放一次神圣干预。")
			.t("judgement.title", "终末天启")
			.t("judgement.desc", "_+1：_开启超凡升天时，牧师可以施放法术_终末天启_，以对视野内所有敌人造成_10~20点伤害_。\n\n_+2：_开启超凡升天时，牧师可以施放法术_终末天启_，以对视野内所有敌人造成_15~30点伤害_。\n\n_+3：_开启超凡升天时，牧师可以施放法术_终末天启_，以对视野内所有敌人造成_20~40点伤害_。\n\n_+4：_开启超凡升天时，牧师可以施放法术_终末天启_，以对视野内所有敌人造成_25~50点伤害_。\n\n终末天启消耗3点圣典充能。牧师自进入超凡升天以来或自上次施放终末天启以来的每次施法都会额外造成33%伤害。")
			.t("flash.title", "天堂阶梯")
			.t("flash.desc", "_+1：_开启超凡升天时，牧师可以施放法术_天堂阶梯_，以传送至最多_3格_外。\n\n_+2：_开启超凡升天时，牧师可以施放法术_天堂阶梯_，以传送至最多_4格_外。\n\n_+3：_开启超凡升天时，牧师可以施放法术_天堂阶梯_，以传送至最多_5格_外。\n\n_+4：_开启超凡升天时，牧师可以施放法术_天堂阶梯_，以传送至最多_6格_外。\n\n天堂阶梯初始消耗2点圣典充能，并且在同次开启超凡升天时每次施放都会+1充能消耗。")
			.t("body_form.title", "体之位格")
			.t("body_form.desc", "牧师可以施放法术_体之位格_，消耗2点充能以使三位一体模拟牧师本局已鉴定的附魔或刻印。\n\n_+1：_使用三位一体时，牧师会获得所选附魔或刻印的效果_20回合_。\n\n_+2：_使用三位一体时，牧师会获得所选附魔或刻印的效果_27回合_。\n\n_+3：_使用三位一体时，牧师会获得所选附魔或刻印的效果_33回合_。\n\n_+4：_使用三位一体时，牧师会获得所选附魔或刻印效果_40回合_。\n\n三位一体同时只能模拟一种体之位格效果。选择罕见而强力的刻印时，三位一体的护甲充能消耗更高。")
			.t("mind_form.title", "智之位格")
			.t("mind_form.desc", "牧师可以施放法术_智之位格_，消耗3点充能以使三位一体模拟牧师本局已使用的法杖或投武。\n\n_+1：_使用三位一体时，牧师会使用所选法杖或投武的_3级_效果。\n\n_+2：_使用三位一体时，牧师会使用所选法杖或投武的_4级_效果。\n\n_+3：_使用三位一体时，牧师会使用所选法杖或投武的_5级_效果。\n\n_+4：_使用三位一体时，牧师会使用所选法杖或投武的_6级_效果。\n\n三位一体同时只能模拟一种智之位格效果。")
			.t("spirit_form.title", "魂之位格")
			.t("spirit_form.desc", "牧师可以施放法术_魂之位格_，消耗4点充能以使三位一体模拟牧师本局已鉴定的戒指或神器(神圣法典除外)。\n\n_+1：_使用三位一体时，牧师会获得所选戒指的_1级_效果20回合或使用所选神器的_4级_效果。\n\n_+2：_使用三位一体时，牧师会获得所选戒指的_2级_效果20回合或使用所选神器的_6级_效果。\n\n_+3：_使用三位一体时，牧师会获得所选戒指的_3级_效果20回合或使用所选神器的_8级_效果。\n\n_+4：_使用三位一体时，牧师会获得所选戒指的_4级_效果20回合或使用所选神器的_10级_效果。\n\n三位一体同时只能模拟一种魂之位格效果。通过三位一体使用神器时效果与护甲充能消耗因神器而异。")
			.t("beaming_ray.title", "光灵召唤")
			.t("beaming_ray.desc", "牧师可以施放法术_光灵召唤_，消耗1点充能以使强化盟友释放传送光束。光束可穿透墙壁并传送盟友至指定位置。若该位置有敌人，盟友会出现在敌人附近并将其作为攻击目标。该法术还可传送通常情况下无法移动的盟友，但若如此做则传送光束范围减半。\n\n_+1：_传送光束的最大范围为_4格_，并使万物一心对最近敌人的伤害加成提升至_35%_，持续10回合。\n\n_+2：_传送光束的最大范围为_8格_，并使万物一心对最近敌人的伤害加成提升至_40%_，持续10回合。\n\n_+3：_传送光束的最大范围为_12格_，并使万物一心对最近敌人的伤害加成提升至_45%_，持续10回合。\n\n_+4：_传送光束的最大范围为_16格_，并使万物一心对最近敌人的伤害加成提升至_50%_，持续10回合。")
			.t("life_link.title", "血色羁绊")
			.t("life_link.desc", "牧师可以施放法术_血色羁绊_，消耗2点充能以使牧师自身与强化盟友之间建立生命联结。该法术会使牧师与其盟友共享所受伤害，并使3阶及以下的增益型牧师法术对任何一方施放时对双方均生效。\n\n_+1：_生命联结持续_10回合_并使万物一心的伤害减免提升至_35%_。\n\n_+2：_生命联结持续_13回合_并使万物一心的伤害减免提升至_40%_。\n\n_+3：_生命联结持续_17回合_并使万物一心的伤害减免提升至_45%_。\n\n_+4：_生命联结持续_20回合_并使万物一心的伤害减免提升至_50%_。")
			.t("stasis.title", "星界投射")
			.t("stasis.desc", "牧师可以施放法术_星界投射_，消耗2点充能以凝滞强化盟友。该法术会将盟友临时移出地牢，并保留其包括万物一心在内的所有增益效果的剩余时间。当法术效果结束时盟友会在你附近再次出现。该法术可被无消耗地立即再次施放，以提前结束法术效果。该法术对无法移动的盟友也会生效，但无法将其跨层移动。\n\n_+1：_凝滞状态最多持续_60回合_。\n\n_+2：_凝滞状态最多持续_90回合_。\n\n_+3：_凝滞状态最多持续_120回合_。\n\n_+4：_凝滞状态最多持续_150回合_。\n\n当有盟友处于凝滞状态时，牧师还可施放光灵召唤以再次召唤盟友，或施放血色羁绊以使盟友提前获得法术效果。然而，凝滞状态下的盟友无法从血色羁绊的法术效果中获得效益。")
			.t("heroic_energy.title", "英勇能量")
			.t("heroic_energy.rat_title", "阴勇能量")
			.t("heroic_energy.desc", "_+1：_发动英雄护甲能力的充能消耗_减少12%_。\n\n_+2：_发动英雄护甲能力的充能消耗_减少23%_。\n\n_+3：_发动英雄护甲能力的充能消耗_减少32%_。\n\n_+4：_发动英雄护甲能力的充能消耗_减少40%_。")
			.t("ratsistance.title", "鼠手鼠脚")
			.t("ratsistance.desc", "_+1：_被鼠化的敌人造成的伤害会降低_10%_。\n\n_+2：_被鼠化的敌人造成的伤害会降低_19%_。\n\n_+3：_被鼠化的敌人造成的伤害会降低_27%_。\n\n_+4：_被鼠化的敌人造成的伤害会降低_35%_。")
			.t("ratlomacy.title", "外交鼠段")
			.t("ratlomacy.desc", "_+1：_对被鼠化的敌人再次使用鼠化术会固化老鼠形态并将其永久转化为友军。\n\n_+2：_对被鼠化的敌人再次使用鼠化术会固化老鼠形态并将其永久转化为友军，并赋予其_2回合_的激素涌动。\n\n_+3：_对被鼠化的敌人再次使用鼠化术会固化老鼠形态并将其永久转化为友军，并赋予其_4回合_的激素涌动。\n\n_+4：_对被鼠化的敌人再次使用鼠化术会固化老鼠形态并将其永久转化为友军，并赋予其_6回合_的激素涌动。")
			.t("ratforcements.title", "吱援部队")
			.t("ratforcements.desc", "_+1：_以自身为目标使用鼠化术会在身边_召唤1只_友军啮齿小鼠。\n\n_+2：_以自身为目标使用鼠化术会在身边_召唤2只_友军啮齿小鼠。\n\n_+3：_以自身为目标使用鼠化术会在身边_召唤3只_友军啮齿小鼠。\n\n_+4：_以自身为目标使用鼠化术会在身边_召唤4只_友军啮齿小鼠。")
			.t("fusion_meta_prefix", "_如果这个天赋被其它英雄获得_，那么")
			.t("fusion_meta_prefix_alt", "_如果这个天赋被其他英雄获得_，那么")
			.t("fusion_meta_direct", "_%s的实际效果：_")
			.t("scholars_intuition.spellsword_title", "魔剑感应")
			.t("inscribed_power.spellsword_title", "铭文赋能")
			.t("scholars_intuition.ascetic_title", "修行感悟")
			.t("backup_barrier.ascetic_title", "余能护体")
			.t("energizing_meal.ascetic_title", "调息一餐")
			.t("inscribed_power.ascetic_title", "符文蓄势")
			.t("wand_preservation.ascetic_title", "灵材回收")
			.t("arcane_vision.ascetic_title", "灵息感知")
			.t("shield_battery.ascetic_title", "充能护体")
			.t("desperate_power.ascetic_title", "绝境聚气")
			.t("survivalists_intuition.performer_title", "舞台直觉")
			.t("mystical_meal.performer_title", "幕间充能")
			.t("inscribed_stealth.performer_title", "幕间匿影")
			.t("durable_projectiles.performer_title", "道具保养")
			.t("seer_shot.performer_title", "投掷探查")
			.t("adventurers_intuition.soldier_title", "战术直觉")
			.t("aggressive_barrier.soldier_title", "反击护盾")
			.t("focused_meal.soldier_title", "蓄势一餐")
			.t("liquid_agility.soldier_title", "药剂机动")
			.t("weapon_recharging.soldier_title", "充能锋芒")
			.t("lethal_haste.soldier_title", "乘胜疾行")
			.t("precise_assault.soldier_title", "校准打击")
			.t("satiated_spells.follower_title", "饱腹庇佑")
			.t("holy_intuition.follower_title", "谨慎鉴物")
			.t("shield_of_light.follower_title", "守望护盾")
			.t("enlightening_meal.follower_title", "启悟一餐")
			.t("recall_inscription.follower_title", "铭文回响")
			.t("sunray.follower_title", "耀光附术")
			.t("divine_sense.follower_title", "灵视余辉")
			.t("bless.follower_title", "同行祝福")
			.t("cleanse.follower_title", "净化共鸣")
			.t("light_reading.follower_title", "静心充能");
	}




	public static class ImprovisedProjectileCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.15f, 0.2f, 0.5f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 50); }
	};
	public static class LethalMomentumTracker extends FlavourBuff{};
	public static class StrikingWaveTracker extends FlavourBuff{};
	public static class WandPreservationCounter extends CounterBuff{{revivePersists = true;}};
	public static class EmpoweredStrikeTracker extends FlavourBuff{
		//blast wave on-hit doesn't resolve instantly, so we delay detaching for it
		public boolean delayedDetach = false;
	};
	public static class ProtectiveShadowsTracker extends Buff {
		float barrierInc = 0.5f;

		@Override
		public boolean act() {
			//barrier every 2/1 turns, to a max of 3/5
			if (((Hero)target).hasTalent(Talent.PROTECTIVE_SHADOWS) && target.invisible > 0){
				Barrier barrier = Buff.affect(target, Barrier.class);
				if (barrier.shielding() < 1 + 2*((Hero)target).pointsInTalent(Talent.PROTECTIVE_SHADOWS)) {
					barrierInc += 0.5f * ((Hero) target).pointsInTalent(Talent.PROTECTIVE_SHADOWS);
				}
				if (barrierInc >= 1){
					barrierInc = 0;
					barrier.incShield(1);
				} else {
					barrier.incShield(0); //resets barrier decay
				}
			} else {
				detach();
			}
			spend( TICK );
			return true;
		}

		private static final String BARRIER_INC = "barrier_inc";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( BARRIER_INC, barrierInc);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			barrierInc = bundle.getFloat( BARRIER_INC );
		}
	}
	public static class BountyHunterTracker extends FlavourBuff{};
	public static class RejuvenatingStepsCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0f, 0.35f, 0.15f); }
		public float iconFadePercent() { return GameMath.gate(0, visualcooldown() / (15 - 5*Dungeon.hero.pointsInTalent(REJUVENATING_STEPS)), 1); }
	};
	public static class RejuvenatingStepsFurrow extends CounterBuff{{revivePersists = true;}};
	public static class SeerShotCooldown extends FlavourBuff{
		public int icon() { return target.buff(RevealedArea.class) != null ? BuffIndicator.NONE : BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.7f, 0.4f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 20); }
	};
	public static class SpiritBladesTracker extends FlavourBuff{};
	public static class PatientStrikeTracker extends Buff {
		public int pos;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		@Override
		public boolean act() {
			if (pos != target.pos) {
				detach();
			} else {
				spend(TICK);
			}
			return true;
		}
		private static final String POS = "pos";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(POS, pos);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			pos = bundle.getInt(POS);
		}
	};
	public static class AggressiveBarrierCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.35f, 0f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 50); }
	};
	public static class LiquidAgilEVATracker extends FlavourBuff{
		{
			//detaches after hero acts, not after mobs act
			actPriority = HERO_PRIO+1;
		}
	};
	public static class LiquidAgilACCTracker extends FlavourBuff{
		public int uses;

		{ type = buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }

		private static final String USES = "uses";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(USES, uses);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			uses = bundle.getInt(USES);
		}
	};
	public static class LethalHasteCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.35f, 0f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 100); }
	};
	public static class SwiftEquipCooldown extends FlavourBuff{
		public boolean secondUse;
		public boolean hasSecondUse(){
			return secondUse;
		}

		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) {
			if (hasSecondUse()) icon.hardlight(0.85f, 0f, 1.0f);
			else                icon.hardlight(0.35f, 0f, 0.7f);
		}
		public float iconFadePercent() { return GameMath.gate(0, visualcooldown() / 20f, 1); }

		private static final String SECOND_USE = "second_use";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(SECOND_USE, secondUse);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			secondUse = bundle.getBoolean(SECOND_USE);
		}
	};
	public static class DeadlyFollowupTracker extends FlavourBuff{
		public int object;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
		private static final String OBJECT    = "object";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(OBJECT, object);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			object = bundle.getInt(OBJECT);
		}
	}
	public static class PreciseAssaultTracker extends FlavourBuff{
		{ type = buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(1f, 1f, 0.0f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
	};
	public static class VariedChargeTracker extends Buff{
		public Class weapon;

		private static final String WEAPON    = "weapon";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(WEAPON, weapon);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			weapon = bundle.getClass(WEAPON);
		}
	}
	public static class CombinedLethalityAbilityTracker extends FlavourBuff{
		public MeleeWeapon weapon;
	};
	public static class CombinedEnergyAbilityTracker extends FlavourBuff{
		public boolean monkAbilused = false;
		public boolean wepAbilUsed = false;

		private static final String MONK_ABIL_USED  = "monk_abil_used";
		private static final String WEP_ABIL_USED   = "wep_abil_used";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(MONK_ABIL_USED, monkAbilused);
			bundle.put(WEP_ABIL_USED, wepAbilUsed);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			monkAbilused = bundle.getBoolean(MONK_ABIL_USED);
			wepAbilUsed = bundle.getBoolean(WEP_ABIL_USED);
		}
	}
	public static class CounterAbilityTacker extends FlavourBuff{}
	public static class SatiatedSpellsTracker extends Buff{
		@Override
		public int icon() {
			return BuffIndicator.SPELL_FOOD;
		}
	}
	//used for metamorphed searing light
	public static class SearingLightCooldown extends FlavourBuff{
		@Override
		public int icon() {
			return BuffIndicator.TIME;
		}
		public void tintIcon(Image icon) { icon.hardlight(0f, 0f, 1f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 20); }
	}

	int icon;
	int maxPoints;

	// tiers 1/2/3/4 start at levels 2/7/13/21
	public static int[] tierLevelThresholds = new int[]{0, 2, 7, 13, 21, 31};

	Talent( int icon ){
		this(icon, 2);
	}

	Talent( int icon, int maxPoints ){
		this.icon = icon;
		this.maxPoints = maxPoints;
	}

	public int icon(){
		if (this == HEROIC_ENERGY){
			if (Ratmogrify.useRatroicEnergy){
				return 218;
			}
			HeroClass cls = Dungeon.hero != null ? Dungeon.hero.heroClass : GamesInProgress.selectedClass;
			switch (cls){
				case WARRIOR: default:
					return 26;
				case MAGE:
					return 58;
				case ROGUE:
					return 90;
				case HUNTRESS:
					return 122;
				case DUELIST:
					return 154;
				case CLERIC:
					return 186;
				case SPELLSWORD:
					return 58;
				case PERFORMER:
					return 90;
				case SOLDIER:
					return 154;
				case FOLLOWER:
					return 186;
				case ASCETIC:
					return 58;
			}
		} else {
			return icon;
		}
	}

	public int maxPoints(){
		return maxPoints;
	}

	public String title(){
		if (this == HEROIC_ENERGY && Ratmogrify.useRatroicEnergy){
			return Messages.get(this, name() + ".rat_title");
		}
		HeroClass heroClass = displayedHeroClass();
		if (isFusionClass(heroClass)) {
			String title = Messages.get(this, name() + "." + heroClass.name().toLowerCase(Locale.ENGLISH) + "_title");
			if (!title.equals(Messages.NO_TEXT_FOUND)) return adaptFusionNames(title, heroClass);
		}
		return adaptFusionNames(Messages.get(this, name() + ".title"), heroClass);
	}

	public final String desc(){
		return desc(false);
	}

	public String desc(boolean metamorphed){
		HeroClass heroClass = displayedHeroClass();
		if (isFusionClass(heroClass)) {
			String override = Messages.get(this, name() + "." + heroClass.name().toLowerCase(Locale.ENGLISH) + "_desc");
			if (!override.equals(Messages.NO_TEXT_FOUND)) return adaptFusionNames(override, heroClass);

			String metaDesc = Messages.get(this, name() + ".meta_desc");
			if (!metaDesc.equals(Messages.NO_TEXT_FOUND)) {
				String prefix = Messages.get(this, "fusion_meta_prefix");
				String alternatePrefix = Messages.get(this, "fusion_meta_prefix_alt");
				String directIntro = Messages.get(this, "fusion_meta_direct", heroClass.title());
				if (metaDesc.startsWith(prefix)) {
					metaDesc = directIntro + metaDesc.substring(prefix.length());
				} else if (metaDesc.startsWith(alternatePrefix)) {
					metaDesc = directIntro + metaDesc.substring(alternatePrefix.length());
				}
				return adaptFusionNames(metaDesc, heroClass);
			}
			return adaptFusionNames(Messages.get(this, name() + ".desc"), heroClass);
		}
		if (metamorphed){
			String metaDesc = Messages.get(this, name() + ".meta_desc");
			if (!metaDesc.equals(Messages.NO_TEXT_FOUND)){
				return Messages.get(this, name() + ".desc") + "\n\n" + metaDesc;
			}
		}
		return Messages.get(this, name() + ".desc");
	}

	private static HeroClass displayedHeroClass() {
		if (Dungeon.hero != null) return Dungeon.hero.heroClass;
		return GamesInProgress.selectedClass;
	}

	private static boolean isFusionClass(HeroClass heroClass) {
		return heroClass == HeroClass.SPELLSWORD || heroClass == HeroClass.PERFORMER
				|| heroClass == HeroClass.SOLDIER || heroClass == HeroClass.FOLLOWER
				|| heroClass == HeroClass.ASCETIC;
	}

	private String adaptFusionNames(String text, HeroClass heroClass) {
		if (!isFusionClass(heroClass) || text.equals(Messages.NO_TEXT_FOUND)) return text;

		for (HeroClass source : HeroClass.values()) {
			if (!isFusionClass(source)) text = text.replace(source.title(), heroClass.title());
		}

		HeroSubClass target = displayedFusionSubClass(heroClass);
		if (target != null) {
			for (HeroSubClass source : HeroSubClass.values()) {
				text = text.replace(source.title(), target.title());
			}
		}
		return text;
	}

	private HeroSubClass displayedFusionSubClass(HeroClass heroClass) {
		if (Dungeon.hero != null && Dungeon.hero.heroClass == heroClass
				&& Dungeon.hero.subClass != null && Dungeon.hero.subClass != HeroSubClass.NONE) {
			return Dungeon.hero.subClass;
		}

		switch (heroClass) {
			case SPELLSWORD:
				switch (this) {
					case SOUL_EATER: case SOUL_SIPHON: case NECROMANCERS_MINIONS: return HeroSubClass.WARLOCK;
					case UNENCUMBERED_SPIRIT: case MONASTIC_VIGOR: case COMBINED_ENERGY: return HeroSubClass.MONK;
				}
				break;
			case PERFORMER:
				switch (this) {
					case EVASIVE_ARMOR: case PROJECTILE_MOMENTUM: case SPEEDY_STEALTH: return HeroSubClass.SUPERSTAR;
					case FARSIGHT: case SHARED_ENCHANTMENT: case SHARED_UPGRADES: return HeroSubClass.JOKER;
				}
				break;
			case SOLDIER:
				switch (this) {
					case FARSIGHT: case PROJECTILE_MOMENTUM: case BOUNTY_HUNTER: return HeroSubClass.AGENT;
					case LAY_ON_HANDS: case AURA_OF_PROTECTION: case WALL_OF_LIGHT: return HeroSubClass.LEADER;
				}
				break;
			case FOLLOWER:
				switch (this) {
					case VARIED_CHARGE: case TWIN_UPGRADES: case COMBINED_LETHALITY: return HeroSubClass.ARTISAN;
					case HOLY_LANCE: case HALLOWED_GROUND: case MNEMONIC_PRAYER: return HeroSubClass.PASTOR;
				}
				break;
			case ASCETIC:
				switch (this) {
					case UNENCUMBERED_SPIRIT: case MONASTIC_VIGOR: case COMBINED_ENERGY: return HeroSubClass.ASCETIC_MONK;
					case EMPOWERED_STRIKE: case MYSTICAL_CHARGE: case EXCESS_CHARGE: return HeroSubClass.HACKER;
				}
				break;
		}
		return null;
	}

	public static void onTalentUpgraded( Hero hero, Talent talent ){
		//for metamorphosis
		if (talent == IRON_WILL && hero.heroClass != HeroClass.WARRIOR){
			Buff.affect(hero, BrokenSeal.WarriorShield.class);
		}

		if (talent == VETERANS_INTUITION && hero.pointsInTalent(VETERANS_INTUITION) == 2){
			if (hero.belongings.armor() != null && !ShardOfOblivion.passiveIDDisabled())  {
				hero.belongings.armor.identify();
			}
		}
		if (talent == THIEFS_INTUITION && hero.pointsInTalent(THIEFS_INTUITION) == 2){
			if (hero.belongings.ring instanceof Ring && !ShardOfOblivion.passiveIDDisabled()) {
				hero.belongings.ring.identify();
			}
			if (hero.belongings.misc instanceof Ring && !ShardOfOblivion.passiveIDDisabled()) {
				hero.belongings.misc.identify();
			}
			for (Item item : Dungeon.hero.belongings){
				if (item instanceof Ring){
					((Ring) item).setKnown();
				}
			}
		}
		if (talent == THIEFS_INTUITION && hero.pointsInTalent(THIEFS_INTUITION) == 1){
			if (hero.belongings.ring instanceof Ring) ((Ring) hero.belongings.ring).setKnown();
			if (hero.belongings.misc instanceof Ring) ((Ring) hero.belongings.misc).setKnown();
			//SPS: 新增饰品槽同样受益
			if (hero.belongings.accessory4 instanceof Ring) ((Ring) hero.belongings.accessory4).setKnown();
			if (hero.belongings.accessory5 instanceof Ring) ((Ring) hero.belongings.accessory5).setKnown();
		}
		if (talent == ADVENTURERS_INTUITION && hero.pointsInTalent(ADVENTURERS_INTUITION) == 2){
			if (hero.belongings.weapon() != null && !ShardOfOblivion.passiveIDDisabled()){
				hero.belongings.weapon().identify();
			}
			if (hero.belongings.secondWep() != null && !ShardOfOblivion.passiveIDDisabled()){
				hero.belongings.secondWep().identify();
			}
		}

		if (talent == PROTECTIVE_SHADOWS && hero.invisible > 0){
			Buff.affect(hero, Talent.ProtectiveShadowsTracker.class);
		}

		if (talent == LIGHT_CLOAK && hero.heroClass == HeroClass.ROGUE){
			for (Item item : Dungeon.hero.belongings.backpack){
				if (item instanceof CloakOfShadows){
					if (!hero.belongings.lostInventory() || item.keptThroughLostInventory()) {
						((CloakOfShadows) item).activate(Dungeon.hero);
					}
				}
			}
		}

		if (talent == HEIGHTENED_SENSES || talent == FARSIGHT || talent == DIVINE_SENSE){
			Dungeon.observe();
		}

		if (talent == TWIN_UPGRADES || talent == DESPERATE_POWER
				|| talent == STRONGMAN || talent == DURABLE_PROJECTILES){
			Item.updateQuickslot();
		}

		if (talent == UNENCUMBERED_SPIRIT && hero.pointsInTalent(talent) == 3){
			Item toGive = new ClothArmor().identify();
			if (!toGive.collect()){
				Dungeon.level.drop(toGive, hero.pos).sprite.drop();
			}
			toGive = new Gloves().identify();
			if (!toGive.collect()){
				Dungeon.level.drop(toGive, hero.pos).sprite.drop();
			}
		}

		if (talent == LIGHT_READING && hero.heroClass == HeroClass.CLERIC){
			for (Item item : Dungeon.hero.belongings.backpack){
				if (item instanceof HolyTome){
					if (!hero.belongings.lostInventory() || item.keptThroughLostInventory()) {
						((HolyTome) item).activate(Dungeon.hero);
					}
				}
			}
		}

		//if we happen to have spirit form applied with a ring of might
		if (talent == SPIRIT_FORM){
			Dungeon.hero.updateHT(false);
		}
	}

	public static class CachedRationsDropped extends CounterBuff{{revivePersists = true;}};
	public static class NatureBerriesDropped extends CounterBuff{{revivePersists = true;}};

	public static void onFoodEaten( Hero hero, float foodVal, Item foodSource ){
		if (hero.hasTalent(HEARTY_MEAL)){
			//4/6 HP healed, when hero is below 33% health (with a little rounding up)
			if (hero.HP/(float)hero.HT < 0.334f) {
				int healing = 2 + 2 * hero.pointsInTalent(HEARTY_MEAL);
				if (!pd.actors.hero.perks.BloodShield.convert(hero, healing)) hero.HP = Math.min(hero.HP + healing, hero.HT);
				hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healing), FloatingText.HEALING);

			}
		}
		if (hero.hasTalent(IRON_STOMACH)){
			if (hero.cooldown() > 0) {
				Buff.affect(hero, WarriorFoodImmunity.class, hero.cooldown());
			}
		}
		if (hero.hasTalent(EMPOWERING_MEAL)){
			//2/3 bonus wand damage for next 3 zaps
			Buff.affect( hero, WandEmpower.class).set(1 + hero.pointsInTalent(EMPOWERING_MEAL), 3);
			ScrollOfRecharging.charge( hero );
		}
		int wandChargeTurns = 0;
		if (hero.hasTalent(ENERGIZING_MEAL)){
			//5/8 turns of recharging
			wandChargeTurns += 2 + 3*hero.pointsInTalent(ENERGIZING_MEAL);
		}
		int artifactChargeTurns = 0;
		if (hero.hasTalent(MYSTICAL_MEAL)){
			//3/5 turns of recharging
			artifactChargeTurns += 1 + 2*hero.pointsInTalent(MYSTICAL_MEAL);
		}
		if (hero.hasTalent(INVIGORATING_MEAL)){
			//effectively 1/2 turns of haste
			Buff.prolong( hero, Haste.class, 0.67f+hero.pointsInTalent(INVIGORATING_MEAL));
		}
		if (hero.hasTalent(STRENGTHENING_MEAL)){
			//3 bonus physical damage for next 2/3 attacks
			Buff.affect( hero, PhysicalEmpower.class).set(3, 1 + hero.pointsInTalent(STRENGTHENING_MEAL));
		}
		if (hero.hasTalent(FOCUSED_MEAL)){
			if (hero.heroClass == HeroClass.DUELIST){
				//0.67/1 charge for the duelist
				Buff.affect( hero, MeleeWeapon.Charger.class ).gainCharge((hero.pointsInTalent(FOCUSED_MEAL)+1)/3f);
				ScrollOfRecharging.charge( hero );
			} else {
				// lvl/3 / lvl/2 bonus dmg on next hit for other classes
				Buff.affect( hero, PhysicalEmpower.class).set(Math.round(hero.lvl / (4f - hero.pointsInTalent(FOCUSED_MEAL))), 1);
			}
		}
		if (hero.hasTalent(SATIATED_SPELLS)){
			if (hero.heroClass == HeroClass.CLERIC) {
				Buff.affect(hero, SatiatedSpellsTracker.class);
			} else {
				//3/5 shielding, delayed up to 10 turns
				int amount = 1 + 2*hero.pointsInTalent(SATIATED_SPELLS);
				Barrier b = Buff.affect(hero, Barrier.class);
				if (b.shielding() <= amount){
					b.setShield(amount);
					b.delay(Math.max(10-b.cooldown(), 0));
				}
			}
		}
		if (hero.hasTalent(ENLIGHTENING_MEAL)){
			if (hero.heroClass == HeroClass.CLERIC) {
				HolyTome tome = hero.belongings.getItem(HolyTome.class);
				if (tome != null) {
					// 2/3 of a charge at +1, 1 full charge at +2
					tome.directCharge( (1+hero.pointsInTalent(ENLIGHTENING_MEAL))/3f );
					ScrollOfRecharging.charge(hero);
				}
			} else {
				//2/3 turns of recharging, both kinds
				wandChargeTurns += 1 + hero.pointsInTalent(ENLIGHTENING_MEAL);
				artifactChargeTurns += 1 + hero.pointsInTalent(ENLIGHTENING_MEAL);
			}
		}

		//we process these at the end as they can stack together from some talents
		if (wandChargeTurns > 0){
			Buff.prolong( hero, Recharging.class, wandChargeTurns );
			ScrollOfRecharging.charge( hero );
			SpellSprite.show(hero, SpellSprite.CHARGE);
		}
		if (artifactChargeTurns > 0){
			ArtifactRecharge buff = Buff.affect( hero, ArtifactRecharge.class);
			if (buff.left() < artifactChargeTurns){
				buff.set(artifactChargeTurns).ignoreHornOfPlenty = foodSource instanceof HornOfPlenty;
			}
			ScrollOfRecharging.charge( hero );
			SpellSprite.show(hero, SpellSprite.CHARGE, 0, 1, 1);
		}
	}

	public static class WarriorFoodImmunity extends FlavourBuff{
		{ actPriority = HERO_PRIO+1; }
	}

	public static float itemIDSpeedFactor( Hero hero, Item item ){
		float factor = 1f;

		// Affected by both Warrior(1.75x/2.5x) and Duelist(2.5x/inst.) talents
		if (item instanceof MeleeWeapon){
			factor *= 1f + 1.5f*hero.pointsInTalent(ADVENTURERS_INTUITION); //instant at +2 (see onItemEquipped)
			factor *= 1f + 0.75f*hero.pointsInTalent(VETERANS_INTUITION);
		}
		// Affected by both Warrior(2.5x/inst.) and Duelist(1.75x/2.5x) talents
		if (item instanceof Armor){
			factor *= 1f + 0.75f*hero.pointsInTalent(ADVENTURERS_INTUITION);
			factor *= 1f + hero.pointsInTalent(VETERANS_INTUITION); //instant at +2 (see onItemEquipped)
		}
		// 3x/instant for Mage (see Wand.wandUsed())
		if (item instanceof Wand){
			factor *= 1f + 2.0f*hero.pointsInTalent(SCHOLARS_INTUITION);
		}
		// 3x/instant speed with Huntress talent (see MissileWeapon.proc)
		if (item instanceof MissileWeapon){
			factor *= 1f + 2.0f*hero.pointsInTalent(SURVIVALISTS_INTUITION);
		}
		// 2x/instant for Rogue (see onItemEqupped), also id's type on equip/on pickup
		if (item instanceof Ring){
			factor *= 1f + hero.pointsInTalent(THIEFS_INTUITION);
		}
		return factor;
	}

	public static void onPotionUsed( Hero hero, int cell, float factor ){
		if (hero.hasTalent(LIQUID_WILLPOWER)){
			// 6.5/10% of max HP
			int shieldToGive = Math.round( factor * hero.HT * (0.030f + 0.035f*hero.pointsInTalent(LIQUID_WILLPOWER)));
			hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldToGive), FloatingText.SHIELDING);
			Buff.affect(hero, Barrier.class).setShield(shieldToGive);
		}
		if (hero.hasTalent(LIQUID_NATURE)){
			ArrayList<Integer> grassCells = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS9){
				grassCells.add(cell+i);
			}
			Random.shuffle(grassCells);
			for (int grassCell : grassCells){
				Char ch = Actor.findChar(grassCell);
				if (ch != null && ch.alignment == Char.Alignment.ENEMY){
					//1/2 turns of roots
					Buff.affect(ch, Roots.class, factor * hero.pointsInTalent(LIQUID_NATURE));
				}
				if (Dungeon.level.map[grassCell] == Terrain.EMPTY ||
						Dungeon.level.map[grassCell] == Terrain.EMBERS ||
						Dungeon.level.map[grassCell] == Terrain.EMPTY_DECO){
					Level.set(grassCell, Terrain.GRASS);
					GameScene.updateMap(grassCell);
				}
				CellEmitter.get(grassCell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
			}
			// 4/6 cells total
			int totalGrassCells = (int) (factor * (2 + 2 * hero.pointsInTalent(LIQUID_NATURE)));
			while (grassCells.size() > totalGrassCells){
				grassCells.remove(0);
			}
			for (int grassCell : grassCells){
				int t = Dungeon.level.map[grassCell];
				if ((t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
						|| t == Terrain.GRASS || t == Terrain.FURROWED_GRASS)
						&& Dungeon.level.plants.get(grassCell) == null){
					Level.set(grassCell, Terrain.HIGH_GRASS);
					GameScene.updateMap(grassCell);
				}
			}
			Dungeon.observe();
		}
		if (hero.hasTalent(LIQUID_AGILITY)){
			Buff.prolong(hero, LiquidAgilEVATracker.class, hero.cooldown() + Math.max(0, factor-1));
			if (factor >= 0.5f){
				Buff.prolong(hero, LiquidAgilACCTracker.class, 5f).uses = Math.round(factor);
			}
		}
	}

	public static void onScrollUsed( Hero hero, int pos, float factor, Class<?extends Item> cls ){
		if (hero.hasTalent(INSCRIBED_POWER)){
			// 2/3 empowered wand zaps
			Buff.affect(hero, ScrollEmpower.class).reset((int) (factor * (1 + hero.pointsInTalent(INSCRIBED_POWER))));
		}
		if (hero.hasTalent(INSCRIBED_STEALTH)){
			// 3/5 turns of stealth
			Buff.affect(hero, Invisibility.class, factor * (1 + 2*hero.pointsInTalent(INSCRIBED_STEALTH)));
			Sample.INSTANCE.play( Assets.Sounds.MELD );
		}
		if (hero.hasTalent(RECALL_INSCRIPTION) && Scroll.class.isAssignableFrom(cls) && cls != ScrollOfUpgrade.class){
			if (hero.heroClass == HeroClass.CLERIC){
				Buff.prolong(hero, RecallInscription.UsedItemTracker.class, hero.pointsInTalent(RECALL_INSCRIPTION) == 2 ? 300 : 10).item = cls;
			} else {
				// 10/15%
				if (Random.Int(20) < 1 + hero.pointsInTalent(RECALL_INSCRIPTION)){
					Reflection.newInstance(cls).collect();
					GLog.p(Messages.get(Talent.class, RECALL_INSCRIPTION.name() + ".refunded"));
				}
			}
		}
	}

	public static void onRunestoneUsed( Hero hero, int pos, Class<?extends Item> cls ){
		if (hero.hasTalent(RECALL_INSCRIPTION) && Runestone.class.isAssignableFrom(cls)){
			if (hero.heroClass == HeroClass.CLERIC){
				Buff.prolong(hero, RecallInscription.UsedItemTracker.class, hero.pointsInTalent(RECALL_INSCRIPTION) == 2 ? 300 : 10).item = cls;
			} else {

				//don't trigger on 1st intuition use
				if (cls.equals(StoneOfIntuition.class) && hero.buff(StoneOfIntuition.IntuitionUseTracker.class) != null){
					return;
				}
				// 10/15%
				if (Random.Int(20) < 1 + hero.pointsInTalent(RECALL_INSCRIPTION)){
					Reflection.newInstance(cls).collect();
					GLog.p(Messages.get(Talent.class, RECALL_INSCRIPTION.name() + ".refunded"));
				}
			}
		}
	}

	public static void onArtifactUsed( Hero hero ){
		if (hero.hasTalent(ENHANCED_RINGS)){
			Buff.prolong(hero, EnhancedRings.class, 3f*hero.pointsInTalent(ENHANCED_RINGS));
		}

		if (Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(Talent.DIVINE_SENSE)){
			Buff.prolong(Dungeon.hero, DivineSense.DivineSenseTracker.class, Dungeon.hero.cooldown()+1);
		}

		// 10/20/30%
		if (Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(Talent.CLEANSE)
				&& Random.Int(10) < Dungeon.hero.pointsInTalent(Talent.CLEANSE)){
			boolean removed = false;
			for (Buff b : Dungeon.hero.buffs()) {
				if (b.type == Buff.buffType.NEGATIVE
						&& !(b instanceof LostInventory)) {
					b.detach();
					removed = true;
				}
			}
			if (removed && Dungeon.hero.sprite != null) {
				new Flare( 6, 32 ).color(0xFF4CD2, true).show( Dungeon.hero.sprite, 2f );
			}
		}
	}

	public static void onItemEquipped( Hero hero, Item item ){
		boolean identify = false;
		if (hero.pointsInTalent(VETERANS_INTUITION) == 2 && item instanceof Armor){
			identify = true;
		}
		if (hero.hasTalent(THIEFS_INTUITION) && item instanceof Ring){
			if (hero.pointsInTalent(THIEFS_INTUITION) == 2){
				identify = true;
			}
			((Ring) item).setKnown();
		}
		if (hero.pointsInTalent(ADVENTURERS_INTUITION) == 2 && item instanceof Weapon){
			identify = true;
		}

		if (identify) {
			if (ShardOfOblivion.passiveIDDisabled()) {
				if (item instanceof Weapon){
					((Weapon) item).setIDReady();
				} else if (item instanceof Armor){
					((Armor) item).setIDReady();
				} else if (item instanceof Ring){
					((Ring) item).setIDReady();
				}
			} else {
				item.identify();
			}
		}
	}

	public static void onItemCollected( Hero hero, Item item ){
		if (hero.pointsInTalent(THIEFS_INTUITION) == 2){
			if (item instanceof Ring) ((Ring) item).setKnown();
		}
	}

	public static int onAttackProc( Hero hero, Char enemy, int dmg ){

		if (hero.hasTalent(Talent.PROVOKED_ANGER)
			&& hero.buff(ProvokedAngerTracker.class) != null){
			dmg += 1 + 2*hero.pointsInTalent(Talent.PROVOKED_ANGER);
			hero.buff(ProvokedAngerTracker.class).detach();
		}

		if (hero.hasTalent(Talent.LINGERING_MAGIC)
				&& hero.buff(LingeringMagicTracker.class) != null){
			dmg += Random.IntRange(hero.pointsInTalent(Talent.LINGERING_MAGIC) , 2);
			hero.buff(LingeringMagicTracker.class).detach();
		}

		if (hero.hasTalent(Talent.SUCKER_PUNCH)
				&& enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)
				&& enemy.buff(SuckerPunchTracker.class) == null){
			dmg += Random.IntRange(hero.pointsInTalent(Talent.SUCKER_PUNCH) , 2);
			Buff.affect(enemy, SuckerPunchTracker.class);
		}

		if (hero.hasTalent(Talent.FOLLOWUP_STRIKE) && enemy.isAlive() && enemy.alignment == Char.Alignment.ENEMY) {
			if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
				Buff.prolong(hero, FollowupStrikeTracker.class, 5f).object = enemy.id();
			} else if (hero.buff(FollowupStrikeTracker.class) != null
					&& hero.buff(FollowupStrikeTracker.class).object == enemy.id()){
				dmg += 1 + hero.pointsInTalent(FOLLOWUP_STRIKE);
				hero.buff(FollowupStrikeTracker.class).detach();
			}
		}

		if (hero.buff(Talent.SpiritBladesTracker.class) != null
				&& Random.Int(10) < 3*hero.pointsInTalent(Talent.SPIRIT_BLADES)){
			SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
			if (bow != null) dmg = bow.proc( hero, enemy, dmg );
			hero.buff(Talent.SpiritBladesTracker.class).detach();
		}

		if (hero.hasTalent(PATIENT_STRIKE)){
			if (hero.buff(PatientStrikeTracker.class) != null
					&& !(hero.belongings.attackingWeapon() instanceof MissileWeapon)){
				hero.buff(PatientStrikeTracker.class).detach();
				dmg += Random.IntRange(hero.pointsInTalent(Talent.PATIENT_STRIKE), 2);
			}
		}

		if (hero.hasTalent(DEADLY_FOLLOWUP) && enemy.alignment == Char.Alignment.ENEMY) {
			if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
				if (!(hero.belongings.attackingWeapon() instanceof SpiritBow.SpiritArrow)) {
					Buff.prolong(hero, DeadlyFollowupTracker.class, 5f).object = enemy.id();
				}
			} else if (hero.buff(DeadlyFollowupTracker.class) != null
					&& hero.buff(DeadlyFollowupTracker.class).object == enemy.id()){
				dmg = Math.round(dmg * (1.0f + .1f*hero.pointsInTalent(DEADLY_FOLLOWUP)));
			}
		}

		return dmg;
	}

	public static class ProvokedAngerTracker extends FlavourBuff{
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.WEAPON; }
		public void tintIcon(Image icon) { icon.hardlight(1.43f, 1.43f, 1.43f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
	}
	public static class LingeringMagicTracker extends FlavourBuff{
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.WEAPON; }
		public void tintIcon(Image icon) { icon.hardlight(1.43f, 1.43f, 0f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
	}
	public static class SuckerPunchTracker extends Buff{};
	public static class FollowupStrikeTracker extends FlavourBuff{
		public int object;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0f, 0.75f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
		private static final String OBJECT    = "object";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(OBJECT, object);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			object = bundle.getInt(OBJECT);
		}
	};

	public static final int MAX_TALENT_TIERS = 4;

	public static void initClassTalents( Hero hero ){
		initClassTalents( hero.heroClass, hero.talents, hero.metamorphedTalents );
	}

	public static void initClassTalents( HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents){
		initClassTalents( cls, talents, new LinkedHashMap<>());
	}

	public static void initClassTalents( HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents, LinkedHashMap<Talent, Talent> replacements ){
		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		ArrayList<Talent> tierTalents = new ArrayList<>();

		//tier 1
		switch (cls){
			case NEWPLAYER:
				break;
			case WARRIOR: default:
				Collections.addAll(tierTalents, HEARTY_MEAL, VETERANS_INTUITION, PROVOKED_ANGER, IRON_WILL);
				break;
			case MAGE:
				Collections.addAll(tierTalents, EMPOWERING_MEAL, SCHOLARS_INTUITION, LINGERING_MAGIC, BACKUP_BARRIER);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, CACHED_RATIONS, THIEFS_INTUITION, SUCKER_PUNCH, PROTECTIVE_SHADOWS);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, NATURES_BOUNTY, SURVIVALISTS_INTUITION, FOLLOWUP_STRIKE, NATURES_AID);
				break;
			case DUELIST:
				Collections.addAll(tierTalents, STRENGTHENING_MEAL, ADVENTURERS_INTUITION, PATIENT_STRIKE, AGGRESSIVE_BARRIER);
				break;
			case CLERIC:
				Collections.addAll(tierTalents, SATIATED_SPELLS, HOLY_INTUITION, SEARING_LIGHT, SHIELD_OF_LIGHT);
				break;
			case SPELLSWORD:
				Collections.addAll(tierTalents, EMPOWERING_MEAL, SCHOLARS_INTUITION, PATIENT_STRIKE, BACKUP_BARRIER);
				break;
			case PERFORMER:
				Collections.addAll(tierTalents, CACHED_RATIONS, SURVIVALISTS_INTUITION, SUCKER_PUNCH, NATURES_AID);
				break;
			case SOLDIER:
				Collections.addAll(tierTalents, STRENGTHENING_MEAL, ADVENTURERS_INTUITION, FOLLOWUP_STRIKE, AGGRESSIVE_BARRIER);
				break;
			case FOLLOWER:
				Collections.addAll(tierTalents, SATIATED_SPELLS, HOLY_INTUITION, PROVOKED_ANGER, SHIELD_OF_LIGHT);
				break;
			case ASCETIC:
				Collections.addAll(tierTalents, EMPOWERING_MEAL, SCHOLARS_INTUITION, PATIENT_STRIKE, BACKUP_BARRIER);
				break;
		}
		for (Talent talent : tierTalents){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(0).put(talent, 0);
		}
		tierTalents.clear();

		//tier 2
		switch (cls){
			case NEWPLAYER:
				break;
			case WARRIOR: default:
				Collections.addAll(tierTalents, IRON_STOMACH, LIQUID_WILLPOWER, RUNIC_TRANSFERENCE, LETHAL_MOMENTUM, IMPROVISED_PROJECTILES);
				break;
			case MAGE:
				Collections.addAll(tierTalents, ENERGIZING_MEAL, INSCRIBED_POWER, WAND_PRESERVATION, ARCANE_VISION, SHIELD_BATTERY);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, MYSTICAL_MEAL, INSCRIBED_STEALTH, WIDE_SEARCH, SILENT_STEPS, ROGUES_FORESIGHT);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, INVIGORATING_MEAL, LIQUID_NATURE, REJUVENATING_STEPS, HEIGHTENED_SENSES, DURABLE_PROJECTILES);
				break;
			case DUELIST:
				Collections.addAll(tierTalents, FOCUSED_MEAL, LIQUID_AGILITY, WEAPON_RECHARGING, LETHAL_HASTE, SWIFT_EQUIP);
				break;
			case CLERIC:
				Collections.addAll(tierTalents, ENLIGHTENING_MEAL, RECALL_INSCRIPTION, SUNRAY, DIVINE_SENSE, BLESS);
				break;
			case SPELLSWORD:
				Collections.addAll(tierTalents, ENERGIZING_MEAL, INSCRIBED_POWER, WAND_PRESERVATION, ARCANE_VISION, SHIELD_BATTERY);
				break;
			case PERFORMER:
				Collections.addAll(tierTalents, MYSTICAL_MEAL, INSCRIBED_STEALTH, HEIGHTENED_SENSES, SILENT_STEPS, DURABLE_PROJECTILES);
				break;
			case SOLDIER:
				Collections.addAll(tierTalents, FOCUSED_MEAL, LIQUID_AGILITY, WEAPON_RECHARGING, LETHAL_HASTE, SWIFT_EQUIP);
				break;
			case FOLLOWER:
				Collections.addAll(tierTalents, ENLIGHTENING_MEAL, RECALL_INSCRIPTION, SUNRAY, DIVINE_SENSE, BLESS);
				break;
			case ASCETIC:
				Collections.addAll(tierTalents, ENERGIZING_MEAL, INSCRIBED_POWER, WAND_PRESERVATION, ARCANE_VISION, SHIELD_BATTERY);
				break;
		}
		for (Talent talent : tierTalents){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(1).put(talent, 0);
		}
		tierTalents.clear();

		//tier 3
		switch (cls){
			case NEWPLAYER:
				break;
			case WARRIOR: default:
				Collections.addAll(tierTalents, HOLD_FAST, STRONGMAN);
				break;
			case MAGE:
				Collections.addAll(tierTalents, DESPERATE_POWER, ALLY_WARP);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, ENHANCED_RINGS, LIGHT_CLOAK);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, POINT_BLANK, SEER_SHOT);
				break;
			case DUELIST:
				Collections.addAll(tierTalents, PRECISE_ASSAULT, DEADLY_FOLLOWUP);
				break;
			case CLERIC:
				Collections.addAll(tierTalents, CLEANSE, LIGHT_READING);
				break;
			case SPELLSWORD:
				Collections.addAll(tierTalents, DESPERATE_POWER, ENHANCED_RINGS);
				break;
			case PERFORMER:
				Collections.addAll(tierTalents, ENHANCED_RINGS, SEER_SHOT);
				break;
			case SOLDIER:
				Collections.addAll(tierTalents, PRECISE_ASSAULT, HOLD_FAST);
				break;
			case FOLLOWER:
				Collections.addAll(tierTalents, CLEANSE, LIGHT_READING);
				break;
			case ASCETIC:
				Collections.addAll(tierTalents, DESPERATE_POWER, ALLY_WARP);
				break;
		}
		for (Talent talent : tierTalents){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(2).put(talent, 0);
		}
		tierTalents.clear();

		//tier4
		//TBD
	}

	public static void initSubclassTalents( Hero hero ){
		initSubclassTalents( hero.subClass, hero.talents );
	}

	public static void initSubclassTalents( HeroSubClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
		if (cls == HeroSubClass.NONE) return;

		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		ArrayList<Talent> tierTalents = new ArrayList<>();

		//tier 3
		switch (cls){
			case BERSERKER: default:
				Collections.addAll(tierTalents, ENDLESS_RAGE, DEATHLESS_FURY, ENRAGED_CATALYST);
				break;
			case GLADIATOR:
				Collections.addAll(tierTalents, CLEAVE, LETHAL_DEFENSE, ENHANCED_COMBO);
				break;
			case BATTLEMAGE:
				Collections.addAll(tierTalents, EMPOWERED_STRIKE, MYSTICAL_CHARGE, EXCESS_CHARGE);
				break;
			case WARLOCK:
				Collections.addAll(tierTalents, SOUL_EATER, SOUL_SIPHON, NECROMANCERS_MINIONS);
				break;
			case ASSASSIN:
				Collections.addAll(tierTalents, ENHANCED_LETHALITY, ASSASSINS_REACH, BOUNTY_HUNTER);
				break;
			case FREERUNNER:
				Collections.addAll(tierTalents, EVASIVE_ARMOR, PROJECTILE_MOMENTUM, SPEEDY_STEALTH);
				break;
			case SNIPER:
				Collections.addAll(tierTalents, FARSIGHT, SHARED_ENCHANTMENT, SHARED_UPGRADES);
				break;
			case WARDEN:
				Collections.addAll(tierTalents, DURABLE_TIPS, BARKSKIN, SHIELDING_DEW);
				break;
			case CHAMPION:
				Collections.addAll(tierTalents, VARIED_CHARGE, TWIN_UPGRADES, COMBINED_LETHALITY);
				break;
			case MONK:
				Collections.addAll(tierTalents, UNENCUMBERED_SPIRIT, MONASTIC_VIGOR, COMBINED_ENERGY);
				break;
			case PRIEST:
				Collections.addAll(tierTalents, HOLY_LANCE, HALLOWED_GROUND, MNEMONIC_PRAYER);
				break;
			case PALADIN:
				Collections.addAll(tierTalents, LAY_ON_HANDS, AURA_OF_PROTECTION, WALL_OF_LIGHT);
				break;
			case SUPERSTAR:
				Collections.addAll(tierTalents, EVASIVE_ARMOR, PROJECTILE_MOMENTUM, SPEEDY_STEALTH);
				break;
			case JOKER:
				Collections.addAll(tierTalents, FARSIGHT, SHARED_ENCHANTMENT, SHARED_UPGRADES);
				break;
			case AGENT:
				Collections.addAll(tierTalents, FARSIGHT, PROJECTILE_MOMENTUM, BOUNTY_HUNTER);
				break;
			case LEADER:
				Collections.addAll(tierTalents, LAY_ON_HANDS, AURA_OF_PROTECTION, WALL_OF_LIGHT);
				break;
			case ARTISAN:
				Collections.addAll(tierTalents, VARIED_CHARGE, TWIN_UPGRADES, COMBINED_LETHALITY);
				break;
			case PASTOR:
				Collections.addAll(tierTalents, HOLY_LANCE, HALLOWED_GROUND, MNEMONIC_PRAYER);
				break;
			case ASCETIC_MONK:
				Collections.addAll(tierTalents, UNENCUMBERED_SPIRIT, MONASTIC_VIGOR, COMBINED_ENERGY);
				break;
			case HACKER:
				Collections.addAll(tierTalents, EMPOWERED_STRIKE, MYSTICAL_CHARGE, EXCESS_CHARGE);
				break;
		}
		for (Talent talent : tierTalents){
			talents.get(2).put(talent, 0);
		}
		tierTalents.clear();

	}

	public static void initArmorTalents( Hero hero ){
		initArmorTalents( hero.armorAbility, hero.talents);
	}

	public static void initArmorTalents(ArmorAbility abil, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
		if (abil == null) return;

		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		for (Talent t : abil.talents()){
			talents.get(3).put(t, 0);
		}
	}

	private static final String TALENT_TIER = "talents_tier_";

	public static void storeTalentsInBundle( Bundle bundle, Hero hero ){
		for (int i = 0; i < MAX_TALENT_TIERS; i++){
			LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
			Bundle tierBundle = new Bundle();

			for (Talent talent : tier.keySet()){
				if (tier.get(talent) > 0){
					tierBundle.put(talent.name(), tier.get(talent));
				}
				if (tierBundle.contains(talent.name())){
					tier.put(talent, Math.min(tierBundle.getInt(talent.name()), talent.maxPoints()));
				}
			}
			bundle.put(TALENT_TIER+(i+1), tierBundle);
		}

		Bundle replacementsBundle = new Bundle();
		for (Talent t : hero.metamorphedTalents.keySet()){
			replacementsBundle.put(t.name(), hero.metamorphedTalents.get(t));
		}
		bundle.put("replacements", replacementsBundle);
	}

	private static final HashSet<String> removedTalents = new HashSet<>();
	static{
		//nothing atm
	}

	private static final HashMap<String, String> renamedTalents = new HashMap<>();
	static{
		//nothing atm
	}

	public static void restoreTalentsFromBundle( Bundle bundle, Hero hero ){
		if (bundle.contains("replacements")){
			Bundle replacements = bundle.getBundle("replacements");
			for (String key : replacements.getKeys()){
				String value = replacements.getString(key);
				if (renamedTalents.containsKey(key)) key = renamedTalents.get(key);
				if (renamedTalents.containsKey(value)) value = renamedTalents.get(value);
				if (!removedTalents.contains(key) && !removedTalents.contains(value)){
					try {
						hero.metamorphedTalents.put(Talent.valueOf(key), Talent.valueOf(value));
					} catch (Exception e) {
						ShatteredPixelDungeon.reportException(e);
					}
				}
			}
		}

		if (hero.heroClass != null)     initClassTalents(hero);
		if (hero.subClass != null)      initSubclassTalents(hero);
		if (hero.armorAbility != null)  initArmorTalents(hero);

		for (int i = 0; i < MAX_TALENT_TIERS; i++){
			LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
			Bundle tierBundle = bundle.contains(TALENT_TIER+(i+1)) ? bundle.getBundle(TALENT_TIER+(i+1)) : null;

			if (tierBundle != null){
				for (String tName : tierBundle.getKeys()){
					int points = tierBundle.getInt(tName);
					if (renamedTalents.containsKey(tName)) tName = renamedTalents.get(tName);
					if (!removedTalents.contains(tName)) {
						try {
							Talent talent = Talent.valueOf(tName);
							if (tier.containsKey(talent)) {
								tier.put(talent, Math.min(points, talent.maxPoints()));
							}
						} catch (Exception e) {
							ShatteredPixelDungeon.reportException(e);
						}
					}
				}
			}
		}
	}

}
