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
import pd.Badges;
import pd.Challenges;
import pd.Dungeon;
import pd.GiftUnlocks;
import pd.QuickSlot;
import pd.SPDSettings;
import pd.actors.buffs.Buff;
import pd.actors.buffs.actbuff.NmImbue;
import pd.actors.hero.abilities.ArmorAbility;
import pd.actors.hero.abilities.cleric.AscendedForm;
import pd.actors.hero.abilities.cleric.PowerOfMany;
import pd.actors.hero.abilities.cleric.Trinity;
import pd.actors.hero.abilities.duelist.Challenge;
import pd.actors.hero.abilities.duelist.ElementalStrike;
import pd.actors.hero.abilities.duelist.Feint;
import pd.actors.hero.abilities.huntress.NaturesPower;
import pd.actors.hero.abilities.huntress.SpectralBlades;
import pd.actors.hero.abilities.huntress.SpiritHawk;
import pd.actors.hero.abilities.mage.ElementalBlast;
import pd.actors.hero.abilities.mage.WarpBeacon;
import pd.actors.hero.abilities.mage.WildMagic;
import pd.actors.hero.abilities.rogue.DeathMark;
import pd.actors.hero.abilities.rogue.ShadowClone;
import pd.actors.hero.abilities.rogue.SmokeBomb;
import pd.actors.hero.abilities.warrior.Endure;
import pd.actors.hero.abilities.warrior.HeroicLeap;
import pd.actors.hero.abilities.warrior.Shockwave;
import pd.items.Ankh;
import pd.items.BrokenSeal;
import pd.items.DewVial;
import pd.items.Generator;
import pd.items.Item;
import pd.items.KnowledgeBook;
import pd.items.StrBottle;
import pd.items.TransmutationBall;
import pd.items.UnBlessAnkh;
import pd.items.Waterskin;
import pd.items.Weightstone;
import pd.items.equipment.armor.ClothArmor;
import pd.items.equipment.armor.normalarmor.BaseArmor;
import pd.items.equipment.armor.normalarmor.DiscArmor;
import pd.items.equipment.armor.normalarmor.StyrofoamArmor;
import pd.items.equipment.armor.normalarmor.VestArmor;
import pd.items.equipment.armor.normalarmor.WoodenArmor;
import pd.items.equipment.armor.specialarmor.LifeArmor;
import pd.items.equipment.armor.specialarmor.PerformerArmor;
import pd.items.equipment.armor.specialarmor.RenBArmor;
import pd.items.equipment.armor.specialarmor.RogueArmor;
import pd.items.equipment.armor.specialarmor.TestArmor;
import pd.items.equipment.artifacts.AlienBag;
import pd.items.equipment.artifacts.Artifact;
import pd.items.equipment.artifacts.CloakOfShadows;
import pd.items.equipment.artifacts.DriedRose;
import pd.items.equipment.artifacts.EtherealChains;
import pd.items.equipment.artifacts.FlyChains;
import pd.items.equipment.artifacts.HolyTome;
import pd.items.equipment.artifacts.Pylon;
import pd.items.equipment.artifacts.TimeOclock;
import pd.items.equipment.artifacts.UnstableSpellbook;
import pd.items.equipment.bags.ArrowCollecter;
import pd.items.equipment.bags.KeyRing;
import pd.items.equipment.bags.VelvetPouch;
import pd.items.equipment.bombs.DungeonBomb;
import pd.items.equipment.bombs.IceBomb;
import pd.items.equipment.bombs.SpsFireBomb;
import pd.items.equipment.bombs.StormBomb;
import pd.items.consum.eggs.AflyEgg;
import pd.items.consum.food.AflyFood;
import pd.items.consum.food.Honey;
import pd.items.consum.food.completefood.FruitCandy;
import pd.items.consum.food.completefood.Meatroll;
import pd.items.consum.food.completefood.MixPizza;
import pd.items.consum.food.completefood.MoonCake;
import pd.items.consum.food.completefood.NutCake;
import pd.items.consum.food.completefood.NutCookie;
import pd.items.consum.food.completefood.Porksoup;
import pd.items.consum.food.completefood.RiceGruel;
import pd.items.consum.food.completefood.Vegetablekebab;
import pd.items.consum.food.completefood.YearFood;
import pd.items.consum.food.staplefood.Pasty;
import pd.items.misc.Ankhshield;
import pd.items.misc.AttackShield;
import pd.items.misc.AttackShoes;
import pd.items.misc.BShovel;
import pd.items.misc.BigBattery;
import pd.items.misc.CopyBall;
import pd.items.misc.DanceLion;
import pd.items.misc.DemoScroll;
import pd.items.misc.DiceTower;
import pd.items.misc.FaithSign;
import pd.items.misc.GnollMark;
import pd.items.misc.GrassBook;
import pd.items.misc.GunOfSoldier;
import pd.items.misc.HealBag;
import pd.items.misc.HorseTotem;
import pd.items.misc.JumpA;
import pd.items.misc.JumpF;
import pd.items.misc.JumpH;
import pd.items.misc.JumpM;
import pd.items.misc.JumpP;
import pd.items.misc.JumpR;
import pd.items.misc.JumpS;
import pd.items.misc.JumpW;
import pd.items.misc.LeaderFlag;
import pd.items.misc.MKbox;
import pd.items.misc.MechPocket;
import pd.items.misc.MissileShield;
import pd.items.misc.NeedPaper;
import pd.items.misc.NmHealBag;
import pd.items.misc.PPC2;
import pd.items.misc.PPC;
import pd.items.misc.PotionOfMage;
import pd.items.misc.RangeBag;
import pd.items.misc.RewardPaper;
import pd.items.misc.SavageHelmet;
import pd.items.misc.SeriousPunch;
import pd.items.misc.Shovel;
import pd.items.misc.UndeadBook;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.consum.potions.elixirs.ElixirOfMight;
import pd.items.consum.potions.PotionOfInvisibility;
import pd.items.consum.potions.PotionOfLiquidFlame;
import pd.items.consum.potions.PotionOfMindVision;
import pd.items.consum.potions.PotionOfPurity;
import pd.items.consum.potions.PotionOfShield;
import pd.items.consum.potions.PotionOfStrength;
import pd.items.equipment.rings.Ring;
import pd.items.consum.scrolls.ScrollOfIdentify;
import pd.items.consum.scrolls.ScrollOfLullaby;
import pd.items.consum.scrolls.ScrollOfMagicMapping;
import pd.items.consum.scrolls.ScrollOfMagicalInfusion;
import pd.items.consum.scrolls.ScrollOfMirrorImage;
import pd.items.consum.scrolls.ScrollOfPsionicBlast;
import pd.items.consum.scrolls.ScrollOfRage;
import pd.items.consum.scrolls.ScrollOfRecharging;
import pd.items.consum.scrolls.ScrollOfRemoveCurse;
import pd.items.consum.scrolls.ScrollOfTerror;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.items.skills.ClassSkill;
import pd.items.summon.ActiveMrDestructo;
import pd.items.equipment.wands.CannonOfMage;
import pd.items.equipment.wands.WandOfDisintegration;
import pd.items.equipment.wands.WandOfFirebolt;
import pd.items.equipment.wands.WandOfFreeze;
import pd.items.equipment.wands.WandOfLight;
import pd.items.equipment.wands.WandOfLightning;
import pd.items.equipment.wands.WandOfMagicMissile;
import pd.items.equipment.wands.WandOfTest;
import pd.items.equipment.weapon.SpiritBow;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.guns.GunA;
import pd.items.equipment.weapon.guns.GunB;
import pd.items.equipment.weapon.guns.GunC;
import pd.items.equipment.weapon.guns.Sling;
import pd.items.equipment.weapon.melee.Cudgel;
import pd.items.equipment.weapon.melee.Dagger;
import pd.items.equipment.weapon.melee.Gloves;
import pd.items.equipment.weapon.melee.HolyWater;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.items.equipment.weapon.melee.Rapier;
import pd.items.equipment.weapon.melee.Spellblade;
import pd.items.equipment.weapon.melee.WornShortsword;
import pd.items.equipment.weapon.melee.fusion.ReedPipe;
import pd.items.equipment.weapon.melee.fusion.Triangolo;
import pd.items.equipment.weapon.melee.normalweapon.MageBook;
import pd.items.equipment.weapon.melee.normalweapon.ShortSword;
import pd.items.equipment.weapon.melee.normalweapon.TrickSand;
import pd.items.equipment.weapon.melee.normalweapon.WoodenStaff;
import pd.items.equipment.weapon.melee.special.TestWeapon;
import pd.items.equipment.weapon.melee.start.BeastKnive;
import pd.items.equipment.weapon.melee.start.BraveBook;
import pd.items.equipment.weapon.melee.start.DiamondPickaxe;
import pd.items.equipment.weapon.melee.start.EleKatana;
import pd.items.equipment.weapon.melee.start.HolyMace;
import pd.items.equipment.weapon.melee.start.LinkSword;
import pd.items.equipment.weapon.melee.start.PixelTorch;
import pd.items.equipment.weapon.melee.start.Whisk;
import pd.items.equipment.weapon.missiles.ElfBow;
import pd.items.equipment.weapon.missiles.ManyKnive;
import pd.items.equipment.weapon.missiles.ShootGun;
import pd.items.equipment.weapon.missiles.TaurcenBow;
import pd.items.equipment.weapon.missiles.ThrowingKnife;
import pd.items.equipment.weapon.missiles.ThrowingSpike;
import pd.items.equipment.weapon.missiles.ThrowingStone;
import pd.items.equipment.weapon.missiles.arrows.BlindFruit;
import pd.items.equipment.weapon.missiles.darts.PoisonDart;
import pd.items.equipment.weapon.missiles.throwing.Boomerang;
import pd.items.equipment.weapon.missiles.throwing.EmpBola;
import pd.items.equipment.weapon.missiles.throwing.EscapeKnive;
import pd.items.equipment.weapon.missiles.throwing.Skull;
import pd.items.equipment.weapon.spammo.BattleAmmo;
import pd.items.equipment.weapon.spammo.GoldAmmo;
import pd.items.equipment.weapon.spammo.HeavyAmmo;
import pd.items.equipment.weapon.spammo.WoodenAmmo;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.plants.Dewcatcher;
import render.utils.platform.DeviceCompat;
import pd.messages.InlineText;

public enum HeroClass {

	WARRIOR( HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR ),
	MAGE( HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK ),
	ROGUE( HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER ),
	HUNTRESS( HeroSubClass.SNIPER, HeroSubClass.WARDEN ),
	DUELIST( HeroSubClass.CHAMPION, HeroSubClass.MONK ),
	CLERIC( HeroSubClass.PRIEST, HeroSubClass.PALADIN ),
	SPELLSWORD( HeroSubClass.WARLOCK, HeroSubClass.MONK ),
	PERFORMER( HeroSubClass.SUPERSTAR, HeroSubClass.JOKER ),
	SOLDIER( HeroSubClass.AGENT, HeroSubClass.LEADER ),
	FOLLOWER( HeroSubClass.ARTISAN, HeroSubClass.PASTOR ),
	ASCETIC( HeroSubClass.ASCETIC_MONK, HeroSubClass.HACKER ),
	NEWPLAYER( HeroSubClass.NONE );
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HeroClass.class)
			.t("warrior", "战士")
			.t("warrior_desc_short", "战士用他的_破损纹章_生成护盾以额外减免伤害。纹章可以在护甲之间转移，并且可以_携带一级升级_。")
			.t("warrior_desc", "战士初始携带一枚_独特的破损纹章_，可使他在生命值半数以下时获得爆发性护盾。纹章可以在护甲之间转移，并且可以携带一级升级。\n\n同时，战士初始携带一柄_破旧的短剑_，_三块投石_，布甲，露珠瓶与绒布包。\n\n战士开局鉴定的物品有：\n_-_鉴定卷轴\n_-_治疗药剂\n_-_盛怒卷轴")
			.t("warrior_unlock", "战士开局即解锁。")
			.t("mage", "法师")
			.t("mage_desc_short", "法师是一位奥术专家，开局持有一根强于法杖的_魔杖_。魔杖可以灌注法师找到的_任何魔杖_。")
			.t("mage_desc", "法师初始携带一根_独特的魔杖_。魔杖的自动充能速度显著快于一般法杖，并且有1点额外最大充能数。法师可以将地牢中找到的各式法杖灌注到魔杖中。\n\n同时，法师初始携带一根已灌注于魔杖中的_魔弹法杖_、布甲、露珠瓶与绒布包。\n\n法师开局鉴定的物品有：\n_-_鉴定卷轴\n_-_液火药剂\n_-_升级卷轴")
			.t("mage_unlock", "_使用一张升级卷轴强化一个物品_以解锁法师。")
			.t("rogue", "盗贼")
			.t("rogue_desc_short", "盗贼能借助他的_暗影斗篷_来躲避和偷袭敌人。他还可以从更远的距离_探测秘密与陷阱_。")
			.t("rogue_desc", "盗贼初始携带一件_独特的暗影斗篷_。披上后，盗贼可以随时隐形。\n\n盗贼能从更远的距离_探测秘密与陷阱_。\n\n同时，盗贼初始携带_一柄匕首_，_三把飞刀_，布甲，露珠瓶与绒布包。\n\n盗贼开局鉴定的物品有：\n_-_鉴定卷轴\n_-_隐形药剂\n_-_探地卷轴")
			.t("rogue_unlock", "_在一场游戏中进行十次伏击_以解锁盗贼。")
			.t("huntress", "女猎手")
			.t("huntress_desc_short", "女猎手精通投掷武器，并且具有一把能无限射击的_灵能弓_。女猎手可以穿过高草丛而_不将其践踏成草地_。")
			.t("huntress_desc", "女猎手初始携带一把_独特的灵能弓_，可以无限发射魔法箭矢。\n\n女猎手可以穿过高草而_不会践踏高草_。\n\n同时，女猎手初始携带一副_镶钉手套_，布甲，露珠瓶与绒布包。\n\n女猎手开局鉴定的道具有：\n_-_鉴定卷轴\n_-_灵视药剂\n_-_催眠卷轴")
			.t("huntress_unlock", "_在一场游戏中使用投掷武器命中十次敌人_以解锁女猎手。")
			.t("duelist", "决斗家")
			.t("duelist_desc_short", "决斗家是一名武器大师，她能够用武器使出_独特的武技_。使出的武技_依不同武器而异_。")
			.t("duelist_desc", "决斗家初始携带一柄_独特的刺剑_，拥有随时间自然充能的特殊武技。\n\n决斗家持有地牢中的任何武器时都将拥有_不同的武技_。\n\n决斗家初始还携带有_两根飞刺_、布甲、露珠瓶以及绒布袋。\n\n决斗家开局鉴定的物品有：\n_-_鉴定卷轴\n_-_力量药剂\n_-_镜像卷轴")
			.t("duelist_unlock", "_在力量达到需求的情况下装备一把2阶或更高阶的武器_以解锁决斗家。")
			.t("cleric", "牧师")
			.t("cleric_desc_short", "牧师是一名使用其独特的_神圣法典_引导神圣魔法的神圣施法者。其能通过天赋_学习并升级法术_。")
			.t("cleric_desc", "牧师初始携带一本_独特的神圣法典_，可以使其施放各种各样的法术。\n\n牧师的大多数天赋专注于学习或升级法术上。\n\n牧师初始还携带有_一柄权杖_、布甲、露珠瓶以及绒布袋。\n\n牧师开局鉴定的物品有：\n_-_鉴定卷轴\n_-_净化药剂\n_-_祛邪卷轴")
			.t("cleric_unlock", "_完全净化任何一件被诅咒装备的诅咒_以解锁牧师")
			.t("spellsword", "魔剑士")
			.t("spellsword_desc_short", "魔剑士将_法杖魔法与近身战斗_结合，以较低的单项强度换取每回合灵活选择。")
			.t("spellsword_desc", "魔剑士初始携带一柄_独特的魔剑_和一根_魔弹法杖_。魔剑较为精准，但伤害低于破旧短剑，因此需要在近战与魔法之间合理切换。\n\n在使用专精之书时，魔剑士可以选择_术士_或_武僧_路线。\n\n魔剑士还初始携带布甲、露珠瓶与绒布包。\n\n魔剑士开局鉴定的物品有：\n_-_鉴定卷轴\n_-_升级卷轴\n_-_力量药剂")
			.t("spellsword_unlock", "融合版中魔剑士开局即解锁。")
			.t("performer", "演员")
			.t("performer_desc_short", "演员在获得经验后进入短暂的_节奏_，以长距离武器和战斗技巧灵活周旋。")
			.t("performer_desc", "演员每次获得经验时都会得到短暂的_节奏祝福_，升级时还会获得少量护盾。初始装备为匕首和芦苇管；芦苇管需要达到力量要求后，才能作为长距离武器正常发挥。\n\n演员可转职为_巨星_或_戏法师_。\n\n演员开局鉴定的物品有：\n_-_催眠卷轴\n_-_净化药剂")
			.t("performer_unlock", "融合版中演员开局即解锁。")
			.t("soldier", "星兵")
			.t("soldier_desc_short", "星兵拥有略高的_命中、闪避与生命成长_，但不会突破破碎装备的正常强度。")
			.t("soldier_desc", "星兵初始携带一柄刺剑和三枚投掷尖刺。训练使其命中与闪避提高5%，初始生命上限提高2点，每次升级额外提高1点生命上限。\n\n星兵可转职为_特工_或_领袖_。\n\n星兵开局鉴定的物品有：\n_-_盛怒卷轴\n_-_治疗药剂")
			.t("soldier_unlock", "融合版中星兵开局即解锁。")
			.t("follower", "信徒")
			.t("follower_desc_short", "信徒依靠_稳健的补给和更合理的价格_冒险，开局带有治疗物资和少量旅费。")
			.t("follower_desc", "信徒初始携带短棍、一瓶治疗药剂和50金币。商店购入价格降低10%，卖出价格与随机掉落不受影响。\n\n信徒可转职为_工匠_或_牧者_。\n\n信徒开局鉴定的物品有：\n_-_恐惧卷轴\n_-_治疗药剂")
			.t("follower_unlock", "融合版中信徒开局即解锁。")
			.t("ascetic", "苦修者")
			.t("ascetic_desc_short", "苦修者在_近身格斗与受控施法_之间切换，不依靠免费的高额强化。")
			.t("ascetic_desc", "苦修者初始携带拳套和魔弹法杖。两件装备均没有免费升级，因此开局强度与破碎的其他职业保持同一预算。\n\n苦修者可转职为_戒律者_或_黑客_。\n\n苦修者开局鉴定的物品有：\n_-_充能卷轴\n_-_镜像卷轴")
			.t("ascetic_unlock", "融合版中苦修者开局即解锁。")
			.t("newplayer", "新手")
			.t("newplayer_desc_short", "仅供 SPS-PD 新手教程使用的隐藏职业。")
			.t("newplayer_desc", "新手不携带任何装备进入教程，生命上限为50点，当前生命为10点。")
			.t("newplayer_unlock", "该职业只能通过新手教程进入。");
	}




	private static final HeroClass[] SPS_PLAYABLE = {
			WARRIOR, MAGE, ROGUE, HUNTRESS,
			DUELIST,
			PERFORMER, SOLDIER, FOLLOWER, ASCETIC
	};

	private HeroSubClass[] subClasses;

	HeroClass( HeroSubClass...subClasses ) {
		this.subClasses = subClasses;
	}

	/** Classes exposed by the character-selection flow: SPS-PD 0.9.8's roster plus the Duelist (SPSEXPD). */
	public static HeroClass[] playableClasses() {
		return SPS_PLAYABLE.clone();
	}

	public void initHero( Hero hero ) {

		hero.heroClass = this;
		if (this == NEWPLAYER) {
			hero.HTBoost = 20;
			hero.updateHT(false);
			hero.HP = 10;
			return;
		}
		Talent.initClassTalents(hero);

		Item i = new ClothArmor().identify();
		if (!Challenges.isItemBlocked(i)) hero.belongings.armor = (ClothArmor)i;

		new VelvetPouch().collect();
		Dungeon.LimitedDrops.VELVET_POUCH.drop();
		new ArrowCollecter().collect();
		new KeyRing().collect();

		Waterskin waterskin = new DewVial();
		waterskin.collect();

		new Pasty().identify().collect();
		new NutCake().identify().collect();
		if (hero.skin == 3) new YearFood().identify().collect();

		new ScrollOfIdentify().identify();

		if (hero.skin == 5) initChaoticSkin(hero);

		switch (this) {
			case WARRIOR:
				initWarrior( hero );
				break;

			case MAGE:
				initMage( hero );
				break;

			case ROGUE:
				initRogue( hero );
				break;

			case HUNTRESS:
				initHuntress( hero );
				break;

			case DUELIST:
				initDuelist( hero );
				break;

			case CLERIC:
				initCleric( hero );
				break;

			case SPELLSWORD:
				initSpellsword( hero );
				break;
			case PERFORMER:
				initPerformer( hero );
				break;
			case SOLDIER:
				initSoldier( hero );
				break;
			case FOLLOWER:
				initFollower( hero );
				break;
			case ASCETIC:
				initAscetic( hero );
				break;
		}
		ClassSkill classSkill = ClassSkill.createFor(this);
		if (classSkill != null) classSkill.collect(hero.belongings.backpack);

		//SPSXPD: 发放职业初始特质（把各角色的特殊点以特质形式呈现）
		for (pd.actors.hero.perks.Perk perk : pd.actors.hero.perks.PerkGrants.initialPerks(hero)) {
			hero.heroPerk.add(perk);
		}
		if (hero.skin == 3 && hero.belongings.misc == null) {
			FlyChains chains = new FlyChains();
			chains.identify().upgrade(3);
			hero.belongings.misc = chains;
			chains.activate(hero);
		}
		hero.updateHT(true);
		//SPS: 礼物商店开局强化（对照 SPS 0.9.9 HeroClass.initGift，需已购总开关 START）
		if (GiftUnlocks.isUnlocked( GiftUnlocks.GiftUnlock.START )) {
			initGift( hero );
		}
		applySpsChallengeStarts(hero);
		SpsTestTimeLoadout.apply(hero);

		if (SPDSettings.quickslotWaterskin()) {
			for (int s = 0; s < QuickSlot.SIZE; s++) {
				if (Dungeon.quickslot.getItem(s) == null) {
					Dungeon.quickslot.setSlot(s, waterskin);
					break;
				}
			}
		}

	}

	/**
	 * SPS 礼物商店开局强化（对照 SPS 0.9.9 HeroClass.initGift）。
	 * 属性与初始物品按已购解锁逐项发放；HT 计入 HTBoost 后由 initHero 统一 updateHT。
	 */
	private static void initGift( Hero hero ) {
		hero.HTBoost += GiftUnlocks.htGiftBonus();
		hero.improveAttackSkill( GiftUnlocks.hitGiftBonus() );
		hero.improveDefenseSkill( GiftUnlocks.evadeGiftBonus() );
		hero.improveMagicSkill( GiftUnlocks.magicGiftBonus() );
		Dungeon.gold += GiftUnlocks.goldGiftBonus();
		hero.exp += GiftUnlocks.expGiftBonus();
		SPDSettings.sCoinAdd( GiftUnlocks.sCoinGiftBonus() );

		//初始幸运：以 1 级幸运徽章计数（LuckyBadge.luckBonus 按等级计入）
		if (GiftUnlocks.luckyGiftBonus() > 0) {
			pd.items.misc.LuckyBadge badge =
					new pd.items.misc.LuckyBadge();
			badge.identify().upgrade( GiftUnlocks.luckyGiftBonus() );
			badge.collect();
		}

		for (int i = 0; i < GiftUnlocks.seedGiftCount(); i++) {
			Generator.random( Generator.Category.SEED ).collect();
		}
		if (GiftUnlocks.plantGiftCount() > 0) {
			new pd.items.equipment.weapon.missiles.buildblock.PlantPotBlock( 1 ).identify().collect();
		}
		if (GiftUnlocks.weaponGiftCount() > 0) {
			Generator.random( Generator.Category.MELEEWEAPON ).uncurse().identify().upgrade( 1 ).collect();
		}
		if (GiftUnlocks.armorGiftCount() > 0) {
			Generator.random( Generator.Category.ARMOR ).uncurse().identify().upgrade( 1 ).collect();
		}
		if (GiftUnlocks.ringGiftCount() > 0) {
			Generator.random( Generator.Category.RING ).uncurse().identify().degrade( 10 ).collect();
		}
		if (GiftUnlocks.artifactGiftCount() > 0) {
			new pd.items.equipment.artifacts.fusion.NoomlinCrown().identify().collect();
		}
		if (GiftUnlocks.wandGiftCount() > 0) {
			new WandOfTest().identify().collect();
		}
		if (GiftUnlocks.robotGiftCount() > 0) {
			new pd.items.summon.ChinaMech().identify().collect();
		}
		if (GiftUnlocks.artItemGiftCount() > 0) {
			new pd.items.specific.sellitem.JumperDancer().identify().collect();
		}
		for (int i = 0; i < GiftUnlocks.upgradeGiftCount(); i++) {
			new ScrollOfUpgrade().collect();
		}
	}

	private static void applySpsChallengeStarts(Hero hero) {
		if (Dungeon.isChallenged(Challenges.ITEM_PHOBIA)) {
			Dungeon.gold += 1000;
		}
		if (Dungeon.isChallenged(Challenges.LISTLESS)) {
			new ElixirOfMight().collect(hero.belongings.backpack);
			new Honey().collect(hero.belongings.backpack);
		}
		if (Dungeon.isChallenged(Challenges.NIGHTMARE_VIRUS)) {
			new UnBlessAnkh().collect(hero.belongings.backpack);
		}
		if (Dungeon.isChallenged(Challenges.ENERGY_LOST)) {
			new Pasty().collect(hero.belongings.backpack);
		}
		if (Dungeon.isChallenged(Challenges.DEW_REJECTION)) {
			new Dewcatcher.Seed().collect(hero.belongings.backpack);
			new Dewcatcher.Seed().collect(hero.belongings.backpack);
		}
		if (Dungeon.isChallenged(Challenges.SPS_DARKNESS)) {
			new ScrollOfMagicMapping().collect(hero.belongings.backpack);
			new ScrollOfMagicMapping().collect(hero.belongings.backpack);
			new ScrollOfMagicMapping().setKnown();
		}
		if (Dungeon.isChallenged(Challenges.ABRASION)) {
			new ScrollOfUpgrade().collect(hero.belongings.backpack);
			new ScrollOfUpgrade().setKnown();
			new ScrollOfMagicalInfusion().collect(hero.belongings.backpack);
			new ScrollOfMagicalInfusion().setKnown();
		}
		if (Dungeon.isChallenged(Challenges.ELE_STOME)) {
			new ScrollOfPsionicBlast().collect(hero.belongings.backpack);
			new ScrollOfPsionicBlast().setKnown();
			new PotionOfShield().collect(hero.belongings.backpack);
			new PotionOfShield().setKnown();
		}
	}

	private static void initChaoticSkin(Hero hero) {
		Weapon primary = (Weapon)Generator.random(Generator.Category.MELEEWEAPON);
		Weapon secondary = (Weapon)Generator.random(Generator.Category.MELEEWEAPON);
		Artifact artifact = (Artifact)Generator.random(Generator.Category.ARTIFACT);
		Ring ring = (Ring)Generator.random(Generator.Category.RING);
		if (primary != null) (hero.belongings.weapon = primary).identify();
		if (secondary != null) (hero.belongings.secondWep = secondary).identify();
		(hero.belongings.armor = new BaseArmor()).identify();
		(hero.belongings.secondArmor = new BaseArmor()).identify();
		if (artifact != null) {
			(hero.belongings.artifact = artifact).identify();
			artifact.activate(hero);
		}
		if (ring != null) {
			(hero.belongings.accessory4 = ring).identify();
			ring.activate(hero);
		}
		hero.STR += 10;
		Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 10;
		Item shoes = Generator.random(Generator.Category.SHOES);
		if (shoes != null) shoes.identify().collect();
		new KnowledgeBook().collect();
		new ScrollOfRemoveCurse().identify().collect();
		new TransmutationBall().collect();
		new TransmutationBall().collect();
	}

	public Badges.Badge masteryBadge() {
		switch (this) {
			case WARRIOR:
				return Badges.Badge.MASTERY_WARRIOR;
			case MAGE:
				return Badges.Badge.MASTERY_MAGE;
			case ROGUE:
				return Badges.Badge.MASTERY_ROGUE;
			case HUNTRESS:
				return Badges.Badge.MASTERY_HUNTRESS;
			case DUELIST:
				return Badges.Badge.MASTERY_DUELIST;
			case CLERIC:
				return Badges.Badge.MASTERY_CLERIC;
			case SPELLSWORD:
				return Badges.Badge.MASTERY_MAGE;
			case PERFORMER:
				return Badges.Badge.MASTERY_ROGUE;
			case SOLDIER:
				return Badges.Badge.MASTERY_DUELIST;
			case FOLLOWER:
				return Badges.Badge.MASTERY_CLERIC;
			case ASCETIC:
				return Badges.Badge.MASTERY_MAGE;
		}
		return null;
	}

	private static void initWarrior( Hero hero ) {
		if (hero.skin == 1) {
			(hero.belongings.armor = new VestArmor()).identify().upgrade(1);
			pd.items.equipment.rings.RingOfForce force = new pd.items.equipment.rings.RingOfForce();
			(hero.belongings.accessory4 = force).identify().upgrade(1); force.activate(hero);
			pd.items.equipment.rings.RingOfMight might = new pd.items.equipment.rings.RingOfMight();
			(hero.belongings.accessory5 = might).identify().upgrade(1); might.activate(hero);
			AttackShield shield = new AttackShield(); shield.identify().collect(); Dungeon.quickslot.setSlot(0, shield);
			new Porksoup().identify().collect(); new PotionOfStrength().identify(); new ScrollOfUpgrade().identify();
			return;
		}
		if (hero.skin == 2) {
			hero.HTBoost += 36;
			hero.updateHT(true);
			hero.STR -= 4;
			hero.improveAttackSkill(-4);
			hero.improveDefenseSkill(1);
			hero.improveMagicSkill(6);
			(hero.belongings.armor = new BaseArmor()).upgrade(6).identify();
			new WandOfFirebolt().upgrade(6).identify().collect();
			new DemoScroll().collect();
			new StrBottle().quantity(4).collect();
			Dungeon.gold += 666;
			new JumpW().collect();
			new Porksoup().identify().collect();
			new PotionOfStrength().identify();
			new ScrollOfUpgrade().identify();
			return;
		}
		if (hero.skin == 3) {
			hero.STR += 2;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 2;
			(hero.belongings.weapon = new pd.items.equipment.weapon.melee.normalweapon.Spear()).identify();
			(hero.belongings.armor = new DiscArmor()).identify();
			//SPSEXPD: 神木圆盾开局直接装备在副武器栏
			(hero.belongings.secondWep = new MissileShield()).identify();
			new SavageHelmet().identify().collect();
			new Porksoup().identify().collect();
			new PotionOfStrength().identify();
			new ScrollOfUpgrade().identify();
			return;
		}
		if (hero.skin == 4) {
			hero.STR += 3;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 3;
			(hero.belongings.weapon = new GunB()).identify();
			hero.belongings.weapon.activate(hero);
			(hero.belongings.armor = new StyrofoamArmor()).identify();
			new TestWeapon().identify().collect();
			new WandOfTest().identify().collect();
			new TestArmor().identify().collect();
			new RewardPaper().identify().collect();
			new JumpW().collect();
			new Porksoup().identify().collect();
			new PotionOfStrength().identify();
			new ScrollOfUpgrade().identify();
			return;
		}
		if (hero.skin == 5) {
			new Porksoup().identify().collect();
			new PotionOfStrength().identify();
			new ScrollOfUpgrade().identify();
			return;
		}
		if (hero.skin == 6) {
			(hero.belongings.armor = new VestArmor()).identify().upgrade(1);
			pd.items.equipment.rings.RingOfForce force = new pd.items.equipment.rings.RingOfForce();
			(hero.belongings.accessory4 = force).identify().upgrade(1); force.activate(hero);
			pd.items.equipment.rings.RingOfMight might = new pd.items.equipment.rings.RingOfMight();
			(hero.belongings.accessory5 = might).identify().upgrade(1); might.activate(hero);
			SeriousPunch punch = new SeriousPunch(); punch.identify().collect();
			Ankhshield shield = new Ankhshield(); shield.identify().collect();
			new JumpW().collect();
			new Porksoup().identify().collect();
			new PotionOfStrength().identify();
			new ScrollOfUpgrade().identify();
			Dungeon.quickslot.setSlot(0, punch); Dungeon.quickslot.setSlot(1, shield);
			return;
		}
		(hero.belongings.weapon = new ShortSword()).identify();
		(hero.belongings.armor = new WoodenArmor()).identify();
		//SPSXPD: 战士开局携带破碎纹章（护盾与刻印转移的载体）
		BrokenSeal seal = new BrokenSeal();
		seal.identify();
		seal.affixToArmor(hero.belongings.armor, null);
		//SPSEXPD: 神木圆盾开局直接装备在副武器栏
		(hero.belongings.secondWep = new MissileShield()).identify();
		new Porksoup().identify().collect();

		new PotionOfStrength().identify();
		new ScrollOfUpgrade().identify();
	}

	private static void initMage( Hero hero ) {
		if (hero.skin == 1) {
			hero.STR += 4; Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 4;
			(hero.belongings.weapon = new pd.items.equipment.weapon.melee.normalweapon.Whip()).identify().upgrade(2);
			(hero.belongings.armor = new pd.items.equipment.armor.normalarmor.LeatherArmor()).identify().upgrade(1);
			CannonOfMage cannon = new CannonOfMage(); cannon.identify().collect(); Dungeon.quickslot.setSlot(0, cannon);
			new Meatroll().identify().collect(); hero.improveMagicSkill(3); new ScrollOfIdentify().identify(); new PotionOfLiquidFlame().identify();
			return;
		}
		if (hero.skin == 2) {
			hero.HTBoost -= 10;
			hero.updateHT(false);
			hero.improveAttackSkill(-5);
			hero.improveDefenseSkill(2);
			hero.improveMagicSkill(6);
			(hero.belongings.weapon = new Dagger()).identify();
			(hero.belongings.armor = new VestArmor()).identify();
			new WandOfFirebolt().upgrade(1).identify().collect();
			new WandOfFreeze().upgrade(1).identify().collect();
			new WandOfLightning().upgrade(1).identify().collect();
			new GnollMark().collect();
			new JumpM().collect();
			new Meatroll().identify().collect();
			new ScrollOfIdentify().identify();
			new PotionOfLiquidFlame().identify();
			return;
		}
		if (hero.skin == 3) {
			(hero.belongings.weapon = new WoodenStaff()).identify();
			(hero.belongings.armor = new VestArmor()).identify();
			new WandOfFirebolt().identify().collect();
			new WandOfFreeze().identify().collect();
			new GnollMark().collect();
			new PotionOfMage().identify().collect();
			hero.improveMagicSkill(3);
			new Meatroll().identify().collect();
			new ScrollOfIdentify().identify();
			new PotionOfLiquidFlame().identify();
			return;
		}
		if (hero.skin == 4) {
			(hero.belongings.weapon = new ElfBow()).identify();
			(hero.belongings.armor = new VestArmor()).identify();
			new JumpM().collect();
			new pd.items.consum.scrolls.ScrollOfRegrowth().identify().collect();
			hero.improveMagicSkill(3);
			new Meatroll().identify().collect();
			new ScrollOfIdentify().identify();
			new PotionOfLiquidFlame().identify();
			return;
		}
		if (hero.skin == 5) {
			hero.improveMagicSkill(3);
			new Meatroll().identify().collect();
			new ScrollOfIdentify().identify();
			new PotionOfLiquidFlame().identify();
			return;
		}
		if (hero.skin == 6) {
			(hero.belongings.weapon = new ShortSword()).identify();
			(hero.belongings.armor = new pd.items.equipment.armor.normalarmor.ClothArmor()).identify();
			new GnollMark().collect();
			new WandOfLight().identify().collect();
			new JumpW().collect();
			hero.improveMagicSkill(3);
			new Meatroll().identify().collect();
			new ScrollOfIdentify().identify();
			new PotionOfLiquidFlame().identify();
			return;
		}
		//SPSXPD: 法师开局使用「老法杖」（MagesStaff，法杖系特质的载体），替代法术书
		MagesStaff staff = new MagesStaff();
		(hero.belongings.weapon = staff).identify();
		staff.activate(hero);
		staff.imbueWand(new WandOfMagicMissile(), hero);
		WandOfMagicMissile missile = new WandOfMagicMissile();
		missile.identify().collect();
		WandOfDisintegration disintegration = new WandOfDisintegration();
		disintegration.identify().collect();
		new PotionOfMage().identify().collect();
		new Meatroll().identify().collect();
		hero.improveMagicSkill(3);

		Dungeon.quickslot.setSlot(0, missile);
		Dungeon.quickslot.setSlot(1, disintegration);

		new ScrollOfIdentify().identify();
		new PotionOfLiquidFlame().identify();
	}

	private static void initRogue( Hero hero ) {
		if (hero.skin == 1) {
			LinkSword sword = new LinkSword(); (hero.belongings.weapon = sword).identify(); sword.activate(hero);
			(hero.belongings.armor = new WoodenArmor()).identify(); hero.STR++; Dungeon.LimitedDrops.STRENGTH_POTIONS.count++;
			EtherealChains chains = new EtherealChains(); (hero.belongings.artifact = chains).identify(); chains.activate(hero);
			Dungeon.quickslot.setSlot(0, chains); Dungeon.quickslot.setSlot(1, sword);
			new RiceGruel().identify().collect(); new ScrollOfMagicMapping().identify(); new PotionOfInvisibility().identify();
			return;
		}
		if (hero.skin == 2) {
			hero.HTBoost -= 20;
			hero.updateHT(false);
			hero.improveDefenseSkill(3);
			hero.improveMagicSkill(3);
			(hero.belongings.weapon = new pd.items.equipment.weapon.melee.normalweapon.Dagger()).identify();
			(hero.belongings.armor = new pd.items.equipment.armor.normalarmor.ClothArmor()).identify();
			new UndeadBook().collect();
			new Skull(5).collect();
			new JumpR().collect();
			new RiceGruel().identify().collect();
			new ScrollOfMagicMapping().identify();
			new PotionOfInvisibility().identify();
			return;
		}
		if (hero.skin == 3) {
			hero.STR += 4;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 4;
			(hero.belongings.weapon = new pd.items.equipment.weapon.melee.normalweapon.Glaive()).identify();
			(hero.belongings.armor = new DiscArmor()).identify();
			CloakOfShadows cloak = new CloakOfShadows();
			(hero.belongings.artifact = cloak).identify();
			cloak.activate(hero);
			new HorseTotem().identify().collect();
			Dungeon.quickslot.setSlot(0, cloak);
			new RiceGruel().identify().collect();
			new ScrollOfMagicMapping().identify();
			new PotionOfInvisibility().identify();
			return;
		}
		if (hero.skin == 4) {
			(hero.belongings.weapon = new pd.items.equipment.weapon.melee.normalweapon.Dagger()).identify();
			(hero.belongings.armor = new pd.items.equipment.armor.normalarmor.ClothArmor()).identify();
			new JumpR().collect();
			new NeedPaper().identify().collect();
			new RiceGruel().identify().collect();
			new ScrollOfMagicMapping().identify();
			new PotionOfInvisibility().identify();
			return;
		}
		if (hero.skin == 5) {
			new RiceGruel().identify().collect();
			new ScrollOfMagicMapping().identify();
			new PotionOfInvisibility().identify();
			return;
		}
		if (hero.skin == 6) {
			EleKatana katana = new EleKatana();
			(hero.belongings.weapon = katana).identify();
			(hero.belongings.armor = new pd.items.equipment.armor.normalarmor.ClothArmor()).identify();
			BeastKnive knife = new BeastKnive();
			(hero.belongings.secondWep = knife).identify(); knife.activate(hero);
			CloakOfShadows cloak = new CloakOfShadows();
			(hero.belongings.artifact = cloak).identify(); cloak.activate(hero);
			EtherealChains chains = new EtherealChains();
			chains.identify().upgrade(3); hero.belongings.misc = chains; chains.activate(hero);
			new EmpBola(10).identify().collect();
			new PoisonDart().quantity(10).identify().collect();
			new BlindFruit(10).identify().collect();
			new Weightstone().identify().collect();
			new pd.items.Stylus().identify().collect();
			new PotionOfHealing().identify().collect();
			new ScrollOfMagicMapping().identify().collect();
			new WandOfLightning().upgrade(3).identify().collect();
			hero.STR += 2;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 2;
			new RiceGruel().identify().collect();
			new ScrollOfMagicMapping().identify();
			new PotionOfInvisibility().identify();
			Dungeon.quickslot.setSlot(0, katana); Dungeon.quickslot.setSlot(1, knife);
			return;
		}
		if (hero.skin == 7) {
			(hero.belongings.weapon = new pd.items.equipment.weapon.melee.normalweapon.Dagger()).identify();
			(hero.belongings.armor = new VestArmor()).identify();
			new JumpR().collect();
			new AflyFood().quantity(3).identify().collect();
			new AflyEgg().quantity(3).identify().collect();
			hero.STR += 10;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 10;
			new RiceGruel().identify().collect();
			new ScrollOfMagicMapping().identify();
			new PotionOfInvisibility().identify();
			return;
		}
		(hero.belongings.weapon = new pd.items.equipment.weapon.melee.normalweapon.Dagger()).identify();
		(hero.belongings.armor = new VestArmor()).identify();

		CloakOfShadows cloak = new CloakOfShadows();
		(hero.belongings.artifact = cloak).identify();
		hero.belongings.artifact.activate( hero );

		BlindFruit fruit = new BlindFruit(3);
		fruit.identify().collect();

		Dungeon.quickslot.setSlot(0, cloak);
		Dungeon.quickslot.setSlot(1, fruit);

		new ScrollOfMagicMapping().identify();
		new PotionOfInvisibility().identify();
		new RiceGruel().identify().collect();
	}

	private static void initHuntress( Hero hero ) {
		if (hero.skin == 1) {
			hero.STR++;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count++;
			pd.items.equipment.weapon.melee.normalweapon.Dagger dagger = new pd.items.equipment.weapon.melee.normalweapon.Dagger();
			dagger.upgrade();
			(hero.belongings.weapon = dagger).identify();
			(hero.belongings.armor = new pd.items.equipment.armor.normalarmor.RubberArmor()).identify().upgrade(1);
			TimeOclock clock = new TimeOclock(); clock.upgrade(5); (hero.belongings.artifact = clock).identify(); clock.activate(hero);
			ManyKnive knives = (ManyKnive)new ManyKnive().upgrade();
			knives.identify().collect();
			EscapeKnive escapeKnives = new EscapeKnive(5);
			escapeKnives.identify().collect();
			Dungeon.quickslot.setSlot(0, clock);
			Dungeon.quickslot.setSlot(1, escapeKnives);
		} else if (hero.skin == 2) {
			hero.HTBoost -= 10;
			hero.updateHT(false);
			hero.improveAttackSkill(5);
			hero.improveDefenseSkill(3);
			(hero.belongings.weapon = new pd.items.equipment.weapon.melee.normalweapon.Knuckles()).identify();
			(hero.belongings.armor = new pd.items.equipment.armor.normalarmor.ClothArmor()).identify();
			new TaurcenBow().identify().collect();
			new JumpH().collect();
		} else if (hero.skin == 3) {
			(hero.belongings.weapon = new pd.items.equipment.weapon.melee.normalweapon.Knuckles()).identify();
			(hero.belongings.armor = new pd.items.equipment.armor.normalarmor.ClothArmor()).identify();
			new TaurcenBow().identify().collect();
			new RangeBag().identify().collect();
		} else if (hero.skin == 4) {
			(hero.belongings.weapon = new WoodenStaff()).identify();
			(hero.belongings.armor = new pd.items.equipment.armor.normalarmor.ClothArmor()).identify();
			new PPC().identify().collect();
			new JumpH().collect();
		} else if (hero.skin == 5) {
			// The chaotic skin's shared loadout is installed before the class branch.
		} else if (hero.skin == 6) {
			(hero.belongings.weapon = new Sling()).identify();
			hero.belongings.weapon.activate(hero);
			(hero.belongings.armor = new VestArmor()).identify();
			ShootGun shootGun = new ShootGun(); shootGun.identify().collect();
			new JumpS().collect();
			new EmpBola(3).identify().collect();
			hero.improveAttackSkill(4);
			hero.improveDefenseSkill(2);
			Dungeon.quickslot.setSlot(0, shootGun);
		} else {
			(hero.belongings.weapon = new pd.items.equipment.weapon.melee.normalweapon.Knuckles()).identify();
			(hero.belongings.armor = new ClothArmor()).identify();
			//SPSXPD: 女猎手开局携带灵能弓（替换原回旋镖）
			SpiritBow bow = new SpiritBow();
			bow.identify().collect();
			EmpBola bola = new EmpBola(3);
			bola.identify().collect();
			Dungeon.quickslot.setSlot(0, bow);
			Dungeon.quickslot.setSlot(1, bola);
		}

		new PotionOfMindVision().identify();
		new ScrollOfRemoveCurse().identify();
		new Vegetablekebab().identify().collect();
	}

	private static void initDuelist( Hero hero ) {

		(hero.belongings.weapon = new Rapier()).identify();
		hero.belongings.weapon.activate(hero);

		ThrowingSpike spikes = new ThrowingSpike();
		spikes.quantity(2).identify().collect(); //set quantity is 3, but Duelist starts with 2

		Dungeon.quickslot.setSlot(0, hero.belongings.weapon);
		Dungeon.quickslot.setSlot(1, spikes);

		new PotionOfStrength().identify();
		new ScrollOfMirrorImage().identify();
	}

	private static void initCleric( Hero hero ) {

		(hero.belongings.weapon = new Cudgel()).identify();
		hero.belongings.weapon.activate(hero);

		HolyTome tome = new HolyTome();
		(hero.belongings.artifact = tome).identify();
		hero.belongings.artifact.activate( hero );

		Dungeon.quickslot.setSlot(0, tome);

		new PotionOfPurity().identify();
		new ScrollOfRemoveCurse().identify();
	}

	private static void initSpellsword( Hero hero ) {

		(hero.belongings.weapon = new Spellblade()).identify();
		hero.belongings.weapon.activate(hero);

		WandOfMagicMissile wand = new WandOfMagicMissile();
		wand.identify().collect();

		Dungeon.quickslot.setSlot(0, wand);
		Dungeon.quickslot.setSlot(1, hero.belongings.weapon);

		new ScrollOfUpgrade().identify();
		new PotionOfStrength().identify();
	}

	private static void initPerformer(Hero hero) {
		if (hero.skin == 1) {
			(hero.belongings.weapon = new GunA()).identify().upgrade(2);
			(hero.belongings.armor = new VestArmor()).identify().upgrade(1);
			new GoldAmmo().identify().collect(); new WoodenAmmo().identify().collect(); new BattleAmmo().identify().collect();
			AlienBag bag = new AlienBag(); (hero.belongings.artifact = bag).identify(); bag.activate(hero);
			BShovel button = new BShovel(); button.identify().collect(); Dungeon.quickslot.setSlot(0, button); Dungeon.quickslot.setSlot(1, hero.belongings.weapon);
			new NutCookie(6).identify().collect(); new DungeonBomb().identify().collect(); new ScrollOfLullaby().identify(); new PotionOfPurity().identify();
			return;
		}
		if (hero.skin == 2) {
			hero.HTBoost -= 10;
			hero.updateHT(false);
			hero.improveMagicSkill(3);
			hero.improveDefenseSkill(5);
			hero.STR += 4;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 4;
			(hero.belongings.weapon = new HolyWater()).identify();
			(hero.belongings.armor = new BaseArmor()).identify();
			new CopyBall().collect();
			new PotionOfHealing().quantity(2).identify().collect();
			new JumpP().collect();
			new NutCookie(6).identify().collect();
			new DungeonBomb().identify().collect();
			new ScrollOfLullaby().identify();
			new PotionOfPurity().identify();
			return;
		}
		if (hero.skin == 3) {
			(hero.belongings.weapon = new Triangolo()).identify();
			(hero.belongings.armor = new pd.items.equipment.armor.normalarmor.ClothArmor()).identify();
			Shovel shovel = new Shovel();
			shovel.identify().collect();
			DanceLion danceLion = new DanceLion();
			danceLion.identify().collect();
			new SpsFireBomb().identify().collect();
			new IceBomb().identify().collect();
			new StormBomb().identify().collect();
			new DungeonBomb().identify().collect();
			new DungeonBomb().identify().collect();
			Dungeon.quickslot.setSlot(0, shovel);
			Dungeon.quickslot.setSlot(1, danceLion);
			new ScrollOfLullaby().identify();
			new ScrollOfLullaby().identify().collect();
			new PotionOfPurity().identify();
			new NutCookie(6).identify().collect();
			return;
		}
		if (hero.skin == 4) {
			hero.STR += 2;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 2;
			(hero.belongings.weapon = new pd.items.equipment.weapon.melee.Mace()).identify();
			(hero.belongings.armor = new pd.items.equipment.armor.normalarmor.LeatherArmor()).identify();
			new LeaderFlag().identify().collect();
			new JumpP().collect();
			Dungeon.gold += 1000;
			new NutCookie(6).identify().collect();
			new DungeonBomb().identify().collect();
			new ScrollOfLullaby().identify();
			new PotionOfPurity().identify();
			return;
		}
		if (hero.skin == 5) {
			new NutCookie(6).identify().collect();
			new DungeonBomb().identify().collect();
			new ScrollOfLullaby().identify();
			new PotionOfPurity().identify();
			return;
		}
		if (hero.skin == 6) {
			hero.STR += 2;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 2;
			(hero.belongings.weapon = new pd.items.equipment.weapon.melee.Mace()).identify();
			(hero.belongings.armor = new pd.items.equipment.armor.normalarmor.LeatherArmor()).identify();
			PPC2 computer = new PPC2(); computer.identify().collect();
			new JumpP().collect();
			new NutCookie(6).identify().collect();
			new DungeonBomb().identify().collect();
			new ScrollOfLullaby().identify();
			new PotionOfPurity().identify();
			Dungeon.quickslot.setSlot(0, computer);
			return;
		}
		if (hero.skin == 7) {
			pd.items.equipment.weapon.missiles.MegaCannon cannon =
					new pd.items.equipment.weapon.missiles.MegaCannon();
			(hero.belongings.weapon = cannon).identify();
			(hero.belongings.armor = new PerformerArmor()).identify();
			(hero.belongings.secondWep = new pd.items.equipment.weapon.melee.start.XSaber()).identify();
			new pd.plants.Dewcatcher.Seed().quantity(2).collect();
			new pd.items.misc.RockManJumpshoes().collect();
			hero.STR += 2;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 2;
			new NutCookie(6).identify().collect();
			new DungeonBomb().identify().collect();
			new ScrollOfLullaby().identify();
			new PotionOfPurity().identify();
			Dungeon.quickslot.setSlot(0, cannon);
			Dungeon.quickslot.setSlot(1, hero.belongings.secondWep);
			return;
		}
		(hero.belongings.weapon = new Triangolo()).identify();
		(hero.belongings.armor = new ClothArmor()).identify();
		Shovel shovel = new Shovel();
		shovel.identify().collect();
		new SpsFireBomb().identify().collect();
		new IceBomb().identify().collect();
		new StormBomb().identify().collect();
		new DungeonBomb().identify().collect();
		new DungeonBomb().identify().collect();
		Dungeon.quickslot.setSlot(0, shovel);
		new ScrollOfLullaby().identify();
		new ScrollOfLullaby().identify().collect();
		new PotionOfPurity().identify();
		new NutCookie(6).identify().collect();
	}

	private static void initSoldier(Hero hero) {
		if (hero.skin == 1) {
			hero.STR += 2; Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 2;
			(hero.belongings.armor = new pd.items.equipment.armor.normalarmor.LeatherArmor()).identify().upgrade(3);
			AttackShoes shoes = new AttackShoes(); shoes.identify().collect(); MKbox box = new MKbox(); box.identify().collect();
			Dungeon.quickslot.setSlot(0, shoes); Dungeon.quickslot.setSlot(1, box);
			new MixPizza(4).identify().collect(); new ScrollOfRage().identify(); new PotionOfHealing().identify(); hero.improveAttackSkill(4); hero.improveDefenseSkill(2);
			return;
		}
		if (hero.skin == 2) {
			hero.HTBoost += 5;
			hero.updateHT(true);
			hero.STR += 6;
			hero.improveMagicSkill(5);
			hero.improveAttackSkill(-6);
			hero.improveDefenseSkill(-33);
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 6;
			(hero.belongings.weapon = new GunC()).identify();
			hero.belongings.weapon.activate(hero);
			(hero.belongings.armor = new BaseArmor()).identify();
			new MechPocket().collect();
			new JumpS().collect();
			new MixPizza(4).identify().collect();
			new ScrollOfRage().identify();
			new PotionOfHealing().identify();
			return;
		}
		if (hero.skin == 3) {
			(hero.belongings.weapon = new GunA()).identify();
			hero.belongings.weapon.activate(hero);
			(hero.belongings.armor = new VestArmor()).identify();
			new HeavyAmmo().identify().collect();
			GunOfSoldier pistol = new GunOfSoldier();
			pistol.identify().collect();
			HealBag healBag = new HealBag();
			healBag.identify().collect();
			Dungeon.quickslot.setSlot(0, pistol);
			Dungeon.quickslot.setSlot(1, healBag);
			hero.improveAttackSkill(4);
			hero.improveDefenseSkill(2);
			new ScrollOfRage().identify();
			new PotionOfHealing().identify();
			new MixPizza(4).identify().collect();
			return;
		}
		if (hero.skin == 4) {
			Buff.affect(hero, NmImbue.class);
			(hero.belongings.weapon = new GunA()).identify();
			hero.belongings.weapon.activate(hero);
			(hero.belongings.armor = new VestArmor()).identify();
			new JumpS().collect();
			new NmHealBag().identify().collect();
			new EscapeKnive(10).identify().collect();
			hero.improveAttackSkill(4);
			hero.improveDefenseSkill(2);
			new ScrollOfRage().identify();
			new PotionOfHealing().identify();
			new MixPizza(4).identify().collect();
			return;
		}
		if (hero.skin == 5) {
			hero.improveAttackSkill(4);
			hero.improveDefenseSkill(2);
			new ScrollOfRage().identify();
			new PotionOfHealing().identify();
			new MixPizza(4).identify().collect();
			return;
		}
		if (hero.skin == 6) {
			hero.HTBoost += 5;
			hero.updateHT(true);
			hero.STR += 6;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 6;
			hero.improveMagicSkill(5);
			hero.improveAttackSkill(-6);
			hero.improveDefenseSkill(-33);
			(hero.belongings.weapon = new WoodenStaff()).identify();
			(hero.belongings.armor = new BaseArmor()).identify();
			ShootGun shootGun = new ShootGun(); shootGun.identify().collect();
			new JumpS().collect();
			new ScrollOfRage().identify();
			new PotionOfHealing().identify();
			new MixPizza(4).identify().collect();
			Dungeon.quickslot.setSlot(0, shootGun);
			return;
		}
		if (hero.skin == 7) {
			(hero.belongings.weapon = new pd.items.equipment.weapon.melee.start.BunnyDagger()).identify();
			(hero.belongings.secondWep = new pd.items.equipment.weapon.melee.start.BunnySpanner()).identify();
			(hero.belongings.armor = new RogueArmor()).identify();
			new JumpR().collect();
			AlienBag alienBag = new AlienBag();
			(hero.belongings.artifact = alienBag).identify();
			alienBag.activate(hero);
			hero.improveAttackSkill(4);
			hero.improveDefenseSkill(2);
			new ScrollOfRage().identify();
			new PotionOfHealing().identify();
			new MixPizza(4).identify().collect();
			Dungeon.quickslot.setSlot(0, hero.belongings.weapon);
			Dungeon.quickslot.setSlot(1, hero.belongings.secondWep);
			return;
		}
		(hero.belongings.weapon = new Sling()).identify();
		(hero.belongings.armor = new VestArmor()).identify();
		hero.belongings.weapon.activate(hero);
		GunOfSoldier pistol = new GunOfSoldier();
		pistol.identify().collect();
		EscapeKnive knives = new EscapeKnive(3);
		knives.identify().collect();
		Dungeon.quickslot.setSlot(0, pistol);
		Dungeon.quickslot.setSlot(1, knives);
		hero.improveAttackSkill(4);
		hero.improveDefenseSkill(2);
		new ScrollOfRage().identify();
		new PotionOfHealing().identify();
		new MixPizza(4).identify().collect();
	}

	private static void initFollower(Hero hero) {
		if (hero.skin == 1) {
			(hero.belongings.weapon = new DiamondPickaxe()).identify();
			(hero.belongings.armor = new pd.items.equipment.armor.normalarmor.LeatherArmor()).identify();
			(hero.belongings.secondWep = new PixelTorch()).identify(); hero.belongings.secondWep.activate(hero);
			hero.STR += 4; Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 4;
			new MoonCake().identify().collect(); new ScrollOfTerror().identify(); new PotionOfHealing().identify();
			Dungeon.quickslot.setSlot(0, hero.belongings.weapon); Dungeon.quickslot.setSlot(1, hero.belongings.secondWep); return;
		}
		if (hero.skin == 2) {
			(hero.belongings.weapon = new pd.items.equipment.weapon.melee.normalweapon.Dagger()).identify().upgrade(2);
			(hero.belongings.armor = new VestArmor()).identify();
			Pylon pylon = new Pylon();
			pylon.identify().upgrade(2);
			hero.belongings.artifact = pylon;
			pylon.activate(hero);
			new JumpF().collect();
			new MoonCake().identify().collect();
			new ScrollOfTerror().identify();
			new PotionOfHealing().identify();
			Dungeon.quickslot.setSlot(0, hero.belongings.weapon);
			Dungeon.quickslot.setSlot(1, pylon);
			return;
		}
		if (hero.skin == 3) {
			hero.STR += 4;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 4;
			(hero.belongings.weapon = new pd.items.equipment.weapon.melee.normalweapon.Rapier()).identify();
			(hero.belongings.armor = new VestArmor()).identify();
			new FaithSign().identify().collect();
			Pylon pylon = new Pylon();
			(hero.belongings.artifact = pylon).identify();
			pylon.activate(hero);
			Dungeon.quickslot.setSlot(0, pylon);
			new PotionOfHealing().identify().collect();
			new ScrollOfTerror().identify();
			new PotionOfHealing().identify();
			new MoonCake().identify().collect();
			return;
		}
		if (hero.skin == 4) {
			(hero.belongings.weapon = new pd.items.equipment.weapon.melee.normalweapon.Knuckles()).identify();
			(hero.belongings.armor = new VestArmor()).identify();
			new DiceTower().identify().collect();
			new JumpF().collect();
			Dungeon.gold += 1000;
			new ScrollOfTerror().identify();
			new PotionOfHealing().identify();
			new MoonCake().identify().collect();
			return;
		}
		if (hero.skin == 5) {
			new ScrollOfTerror().identify();
			new PotionOfHealing().identify();
			new MoonCake().identify().collect();
			return;
		}
		if (hero.skin == 6) {
			(hero.belongings.weapon = new TrickSand()).identify();
			(hero.belongings.armor = new VestArmor()).identify();
			hero.spp = 100;
			new Skull(5).identify().collect();
			new Ankh().identify().collect();
			new Ankh().identify().collect();
			new JumpF().collect();
			new ScrollOfTerror().identify();
			new PotionOfHealing().identify();
			new MoonCake().identify().collect();
			return;
		}
		(hero.belongings.weapon = new WoodenStaff()).identify();
		(hero.belongings.armor = new ClothArmor()).identify();
		new PotionOfHealing().identify().collect();
		new FaithSign().identify().collect();
		new ScrollOfTerror().identify();
		new PotionOfHealing().identify();
		new MoonCake().identify().collect();
	}

	private static void initAscetic(Hero hero) {
		if (hero.skin == 1) {
			hero.STR += 4; Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 4;
			(hero.belongings.weapon = new HolyMace()).identify().upgrade(1);
			(hero.belongings.armor = new pd.items.equipment.armor.normalarmor.LeatherArmor()).identify().upgrade(1);
			(hero.belongings.secondWep = new BraveBook()).identify().upgrade(1); hero.belongings.secondWep.activate(hero);
			hero.improveMagicSkill(3); new FruitCandy(3).identify().collect(); new ScrollOfMirrorImage().identify(); new PotionOfShield().identify();
			Dungeon.quickslot.setSlot(0, hero.belongings.weapon); Dungeon.quickslot.setSlot(1, hero.belongings.secondWep); return;
		}
		if (hero.skin == 2) {
			hero.HTBoost += 10;
			hero.updateHT(false);
			hero.improveAttackSkill(4);
			hero.improveDefenseSkill(2);
			hero.improveMagicSkill(-3);
			(hero.belongings.weapon = new WoodenStaff()).identify();
			LifeArmor armor = new LifeArmor();
			(hero.belongings.armor = armor).identify();
			armor.activate(hero);
			new GrassBook().collect();
			new JumpA().collect();
			hero.improveMagicSkill(3);
			new FruitCandy(3).identify().collect();
			new ScrollOfMirrorImage().identify();
			new PotionOfShield().identify();
			return;
		}
		if (hero.skin == 3) {
			hero.STR += 4;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 4;
			(hero.belongings.weapon = new Whisk()).identify();
			(hero.belongings.armor = new pd.items.equipment.armor.normalarmor.LeatherArmor()).identify();
			BigBattery battery = new BigBattery();
			battery.identify().collect();
			UnstableSpellbook spellbook = new UnstableSpellbook();
			(hero.belongings.artifact = spellbook).identify();
			spellbook.activate(hero);
			Dungeon.quickslot.setSlot(0, battery);
			Dungeon.quickslot.setSlot(1, spellbook);
			hero.improveMagicSkill(3);
			new ScrollOfRecharging().identify();
			new ScrollOfMirrorImage().identify();
			new FruitCandy(3).identify().collect();
			return;
		}
		if (hero.skin == 4 || hero.skin == 5 || hero.skin == 6) {
			hero.improveMagicSkill(3);
			new ScrollOfMirrorImage().identify();
			new PotionOfShield().identify();
			new FruitCandy(3).identify().collect();
			return;
		}
		if (hero.skin == 7) {
			(hero.belongings.weapon = new pd.items.equipment.weapon.melee.special.NinjaFan()).identify();
			(hero.belongings.armor = new RenBArmor()).identify();
			DriedRose rose = new DriedRose();
			(hero.belongings.artifact = rose).identify();
			rose.activate(hero);
			Pylon pylon = new Pylon();
			pylon.identify().collect();
			pylon.activate(hero);
			new WandOfFirebolt().identify().collect();
			new JumpW().collect();
			hero.improveMagicSkill(3);
			new FruitCandy(3).identify().collect();
			new ScrollOfMirrorImage().identify();
			new PotionOfShield().identify();
			Dungeon.quickslot.setSlot(0, pylon);
			Dungeon.quickslot.setSlot(1, hero.belongings.weapon);
			return;
		}
		(hero.belongings.weapon = new TrickSand()).identify();
		(hero.belongings.armor = new VestArmor()).identify();
		BigBattery battery = new BigBattery();
		battery.identify().collect();
		new ActiveMrDestructo().identify().collect();
		Dungeon.quickslot.setSlot(0, battery);
		hero.improveMagicSkill(3);
		new ScrollOfRecharging().identify();
		new ScrollOfMirrorImage().identify();
		new FruitCandy(3).identify().collect();
	}

	public String title() {
		return Messages.get(HeroClass.class, name());
	}

	public String desc(){
		return Messages.get(HeroClass.class, name()+"_desc");
	}

	public String shortDesc(){
		return Messages.get(HeroClass.class, name()+"_desc_short");
	}

	public HeroSubClass[] subClasses() {
		return subClasses;
	}

	public ArmorAbility[] armorAbilities(){
		switch (this) {
			case WARRIOR: default:
				return new ArmorAbility[]{new HeroicLeap(), new Shockwave(), new Endure()};
			case MAGE:
				return new ArmorAbility[]{new ElementalBlast(), new WildMagic(), new WarpBeacon()};
			case ROGUE:
				return new ArmorAbility[]{new SmokeBomb(), new DeathMark(), new ShadowClone()};
			case HUNTRESS:
				return new ArmorAbility[]{new SpectralBlades(), new NaturesPower(), new SpiritHawk()};
			case DUELIST:
				return new ArmorAbility[]{new Challenge(), new ElementalStrike(), new Feint()};
			case CLERIC:
				return new ArmorAbility[]{new AscendedForm(), new Trinity(), new PowerOfMany()};
			case SPELLSWORD:
				return new ArmorAbility[]{new ElementalBlast(), new ElementalStrike(), new Feint()};
			case PERFORMER:
				return new ArmorAbility[]{new SmokeBomb(), new NaturesPower(), new Feint()};
			case SOLDIER:
				return new ArmorAbility[]{new Shockwave(), new Challenge(), new Endure()};
			case FOLLOWER:
				return new ArmorAbility[]{new Trinity(), new PowerOfMany(), new AscendedForm()};
			case ASCETIC:
				return new ArmorAbility[]{new ElementalBlast(), new WarpBeacon(), new ElementalStrike()};
		}
	}

	public String spritesheet() {
		switch (this) {
			case WARRIOR: default:
				return Assets.Sprites.WARRIOR;
			case MAGE:
				return Assets.Sprites.MAGE;
			case ROGUE:
				return Assets.Sprites.ROGUE;
			case HUNTRESS:
				return Assets.Sprites.HUNTRESS;
			case DUELIST:
				return Assets.Sprites.DUELIST;
			case CLERIC:
				return Assets.Sprites.CLERIC;
			case SPELLSWORD:
				return Assets.Sprites.SPELLSWORD;
			case PERFORMER:
				return Assets.Sprites.PERFORMER;
			case SOLDIER:
				return Assets.Sprites.SOLDIER;
			case FOLLOWER:
				return Assets.Sprites.FOLLOWER;
			case ASCETIC:
				return Assets.Sprites.ASCETIC;
		}
	}

	public String spritesheet(int skin) {
		if (skin <= 0) return spritesheet();
		switch (this) {
			case WARRIOR: return Assets.Sprites.SPS_WARRIOR;
			case MAGE: return Assets.Sprites.SPS_MAGE;
			case ROGUE: return Assets.Sprites.SPS_ROGUE;
			case HUNTRESS: return Assets.Sprites.SPS_HUNTRESS;
			default: return spritesheet();
		}
	}

	public boolean supportsSkins() {
		return this == WARRIOR || this == MAGE || this == ROGUE || this == HUNTRESS
				|| this == PERFORMER || this == SOLDIER || this == FOLLOWER || this == ASCETIC;
	}

	public String splashArt(){
		switch (this) {
			case WARRIOR: default:
				return Assets.Splashes.WARRIOR;
			case MAGE:
				return Assets.Splashes.MAGE;
			case ROGUE:
				return Assets.Splashes.ROGUE;
			case HUNTRESS:
				return Assets.Splashes.HUNTRESS;
			case DUELIST:
				return Assets.Splashes.DUELIST;
			case CLERIC:
				return Assets.Splashes.CLERIC;
			case SPELLSWORD:
				return Assets.Splashes.SPELLSWORD;
			case PERFORMER:
				return Assets.Splashes.PERFORMER;
			case SOLDIER:
				return Assets.Splashes.SOLDIER;
			case FOLLOWER:
				return Assets.Splashes.FOLLOWER;
			case ASCETIC:
				return Assets.Splashes.ASCETIC;
		}
	}
	
	public boolean isUnlocked(){
		//SPSEXPD: every class is available from the start
		return true;
	}
	
	public String unlockMsg() {
		return shortDesc() + "\n\n" + Messages.get(HeroClass.class, name()+"_unlock");
	}

}
