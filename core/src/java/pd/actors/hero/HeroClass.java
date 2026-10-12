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
			.t("newplayer_unlock", "该职业只能通过新手教程进入。")

			//SPSEXPD: 职业背景故事（WndHeroInfo 的「背景故事」页展示）
			.t("warrior_lore", "城邦的卫队早已解散，铠甲锈蚀，旗帜褪色。他把最后一枚破损纹章别在胸前，独自走向地牢深处——那里或许还有值得守护的东西。")
			.t("mage_lore", "他被逐出高塔，理由只有一个：他不肯停止追问。如今他握着自己的魔杖，在废墟里收集别人弃置的咒文，想弄清世界究竟由什么写成。")
			.t("rogue_lore", "没有门能拦住他，只有上了锁的门才勉强算数。盗贼披上暗影斗篷，把它当作最体面的借口——地牢里堆满的秘密，比金子更让他心痒。")
			.t("huntress_lore", "她来自被荒草重新吞没的边村，弓是父亲留下的，箭袋里却只剩风声。灵能弓认得她的沉默，便替她射出无尽的光矢——她要替村子守住最后一片高草。")
			.t("duelist_lore", "在贵族学院的决斗场上，她赢过太多场，也看穿了太多花招。刺剑离鞘之后，她只忠于一件事：真正的武技，应当在地牢里也站得住脚。")
			.t("performer_lore", "掌声曾是他的全部，如今空荡的台下只剩回声。他带着芦苇管走入地牢，把每一场战斗当作新的节拍——只要还在演出，演员就不算谢幕。")
			.t("soldier_lore", "一艘坠毁的星舰是她的摇篮，残骸里的训练舱是她的学校。她按军规整理装备、按战术推进——即使整支小队只剩她一人，任务仍未结束。")
			.t("follower_lore", "他跟随过太多个英雄，也埋葬过太多个。信徒学会了替活下来的人精打细算：省下的每一枚金币，都是下一次活下去的可能。")
			.t("ascetic_lore", "他放弃过一切能让人变强的东西——宝石、附魔、馈赠。苦修者的拳套与法杖都保持粗粝的原样，因为力量若来得太容易，就不配被信任。")
			.t("cleric_lore", "教会烧掉了他所有的注释，只留下法典本身。牧师携着那本书走入黑暗，既不布道也不说教——他要亲眼看看，神圣究竟能不能照亮这里。")
			.t("spellsword_lore", "剑与杖本不该同时握在手里，他偏要试试。魔剑士在近战与法术之间来回寻找那条窄缝，代价是样样不精，收获是从不重样。")
			.t("newplayer_lore", "他什么也没有携带——没有铠甲，没有武器，甚至没有名字。这是留给第一次踏入地牢的人的空白，剩下的故事由他自己写。");
	}




	private static final HeroClass[] SPS_PLAYABLE = {
			WARRIOR, MAGE, ROGUE, HUNTRESS,
			DUELIST,
			PERFORMER, SOLDIER, FOLLOWER, ASCETIC
	};

	//SPSEXPD: 主题（= 开局拓展包）总数。索引 0 = 经典皮肤，1~7 = 已有内容的皮肤，
	//8 = 「传奇征途」（留空占位，表现与经典一致，待填充）。
	public static final int THEME_COUNT = 9;

	//SPSEXPD: 已实装专属内容的主题数量（skin 0 ~ IMPLEMENTED_SKINS-1）。
	//skin >= 该值的主题对任何职业都还没有内容，一律按经典处理。
	public static final int IMPLEMENTED_SKINS = 8;

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

		//SPSEXPD: 所有角色开局携带一枚满充能的神圣护盾
		Ankhshield.fullCharge().collect();

		Waterskin waterskin = new DewVial();
		waterskin.collect();

		new Pasty().identify().collect();
		new NutCake().identify().collect();
		if (hero.skin == 3) new YearFood().identify().collect();

		//SPSEXPD: 原本这里有一句 new ScrollOfIdentify().identify()（游离实例），
		//但 Scroll.identify() 会 setKnown() 全局标记该类型已知 —— 开局不该自带这种鉴定状态，故删除。

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

		//初始幸运：改为发放「幸运儿」特质（幸运 +1）——幸运胸章现已固定 +3 且不可强化
		if (GiftUnlocks.luckyGiftBonus() > 0) {
			pd.actors.hero.perks.PerkGrants.grant(hero, new pd.actors.hero.perks.BornLucky());
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
			//SPSEXPD: 安卡不再有「未祝福」形态
			new Ankh().collect(hero.belongings.backpack);
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
		//SPSEXPD: 原本是 identify().collect()，但 Scroll.identify() 会 setKnown() 全局标记该类型已知，
		//开局不该自带这种鉴定状态 —— 保留物品、去掉鉴定
		new ScrollOfRemoveCurse().collect();
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
		//SPSEXPD: 物品部分已抽到 startingItems()（单一真源），这里只留属性与全局副作用
		if (hero.skin == 1) {
			applyStartingItems(hero, startingItems(WARRIOR, 1));
			return;
		}
		if (hero.skin == 2) {
			hero.HTBoost += 36;
			hero.updateHT(true);
			hero.STR -= 4;
			hero.improveAttackSkill(-4);
			hero.improveDefenseSkill(1);
			hero.improveMagicSkill(6);
			Dungeon.gold += 666;
			applyStartingItems(hero, startingItems(WARRIOR, 2));
			return;
		}
		if (hero.skin == 3) {
			hero.STR += 2;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 2;
			applyStartingItems(hero, startingItems(WARRIOR, 3));
			return;
		}
		if (hero.skin == 4) {
			hero.STR += 3;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 3;
			applyStartingItems(hero, startingItems(WARRIOR, 4));
			return;
		}
		if (hero.skin == 5) {
			applyStartingItems(hero, startingItems(WARRIOR, 5));
			return;
		}
		if (hero.skin == 6) {
			applyStartingItems(hero, startingItems(WARRIOR, 6));
			return;
		}
		//经典（skin 0）与留空主题（skin >= IMPLEMENTED_SKINS）都走默认清单
		applyStartingItems(hero, startingItems(WARRIOR, 0));
	}

	private static void initMage( Hero hero ) {
		//SPSEXPD: 物品部分已抽到 startingItems()（单一真源），这里只留属性与全局副作用
		if (hero.skin == 1) {
			hero.STR += 4; Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 4;
			hero.improveMagicSkill(3);
			applyStartingItems(hero, startingItems(MAGE, 1));
			return;
		}
		if (hero.skin == 2) {
			hero.HTBoost -= 10;
			hero.updateHT(false);
			hero.improveAttackSkill(-5);
			hero.improveDefenseSkill(2);
			hero.improveMagicSkill(6);
			applyStartingItems(hero, startingItems(MAGE, 2));
			return;
		}
		if (hero.skin == 3) {
			hero.improveMagicSkill(3);
			applyStartingItems(hero, startingItems(MAGE, 3));
			return;
		}
		if (hero.skin == 4) {
			hero.improveMagicSkill(3);
			applyStartingItems(hero, startingItems(MAGE, 4));
			return;
		}
		if (hero.skin == 5) {
			hero.improveMagicSkill(3);
			applyStartingItems(hero, startingItems(MAGE, 5));
			return;
		}
		if (hero.skin == 6) {
			hero.improveMagicSkill(3);
			applyStartingItems(hero, startingItems(MAGE, 6));
			return;
		}
		//经典（skin 0）与留空主题都走默认清单
		hero.improveMagicSkill(3);
		applyStartingItems(hero, startingItems(MAGE, 0));
	}

	private static void initRogue( Hero hero ) {
		//SPSEXPD: 物品部分已抽到 startingItems()（单一真源），这里只留属性与全局副作用
		if (hero.skin == 1) {
			hero.STR++; Dungeon.LimitedDrops.STRENGTH_POTIONS.count++;
			applyStartingItems(hero, startingItems(ROGUE, 1));
			return;
		}
		if (hero.skin == 2) {
			hero.HTBoost -= 20;
			hero.updateHT(false);
			hero.improveDefenseSkill(3);
			hero.improveMagicSkill(3);
			applyStartingItems(hero, startingItems(ROGUE, 2));
			return;
		}
		if (hero.skin == 3) {
			hero.STR += 4;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 4;
			applyStartingItems(hero, startingItems(ROGUE, 3));
			return;
		}
		if (hero.skin == 4) {
			applyStartingItems(hero, startingItems(ROGUE, 4));
			return;
		}
		if (hero.skin == 5) {
			applyStartingItems(hero, startingItems(ROGUE, 5));
			return;
		}
		if (hero.skin == 6) {
			hero.STR += 2;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 2;
			applyStartingItems(hero, startingItems(ROGUE, 6));
			return;
		}
		if (hero.skin == 7) {
			hero.STR += 10;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 10;
			applyStartingItems(hero, startingItems(ROGUE, 7));
			return;
		}
		//经典（skin 0）与留空主题都走默认清单
		applyStartingItems(hero, startingItems(ROGUE, 0));
	}

	private static void initHuntress( Hero hero ) {
		//SPSEXPD: 物品部分已抽到 startingItems()（单一真源），这里只留属性与全局副作用
		if (hero.skin == 1) {
			hero.STR++;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count++;
		} else if (hero.skin == 2) {
			hero.HTBoost -= 10;
			hero.updateHT(false);
			hero.improveAttackSkill(5);
			hero.improveDefenseSkill(3);
		} else if (hero.skin == 6) {
			hero.improveAttackSkill(4);
			hero.improveDefenseSkill(2);
		}
		//skin 5（混沌）的整套装备由 initChaoticSkin() 在 initHero 通用段发放
		applyStartingItems(hero, startingItems(HUNTRESS, hero.skin));
	}

	private static void initDuelist( Hero hero ) {
		//SPSEXPD: 物品部分已抽到 startingItems()（单一真源）
		applyStartingItems(hero, startingItems(DUELIST, hero.skin));
	}

	private static void initCleric( Hero hero ) {
		//SPSEXPD: 物品部分已抽到 startingItems()（单一真源）
		applyStartingItems(hero, startingItems(CLERIC, hero.skin));
	}

	private static void initSpellsword( Hero hero ) {
		//SPSEXPD: 物品部分已抽到 startingItems()（单一真源）
		applyStartingItems(hero, startingItems(SPELLSWORD, hero.skin));
	}

	private static void initPerformer(Hero hero) {
		//SPSEXPD: 物品部分已抽到 startingItems()（单一真源），这里只留属性与全局副作用
		if (hero.skin == 2) {
			hero.HTBoost -= 10;
			hero.updateHT(false);
			hero.improveMagicSkill(3);
			hero.improveDefenseSkill(5);
			hero.STR += 4;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 4;
		} else if (hero.skin == 4) {
			hero.STR += 2;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 2;
			Dungeon.gold += 1000;
		} else if (hero.skin == 6) {
			hero.STR += 2;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 2;
		} else if (hero.skin == 7) {
			hero.STR += 2;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 2;
		}
		applyStartingItems(hero, startingItems(PERFORMER, hero.skin));
	}

	private static void initSoldier(Hero hero) {
		//SPSEXPD: 物品部分已抽到 startingItems()（单一真源），这里只留属性与全局副作用
		if (hero.skin == 1) {
			hero.STR += 2; Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 2;
			hero.improveAttackSkill(4);
			hero.improveDefenseSkill(2);
		} else if (hero.skin == 2) {
			hero.HTBoost += 5;
			hero.updateHT(true);
			hero.STR += 6;
			hero.improveMagicSkill(5);
			hero.improveAttackSkill(-6);
			hero.improveDefenseSkill(-33);
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 6;
		} else if (hero.skin == 3) {
			hero.improveAttackSkill(4);
			hero.improveDefenseSkill(2);
		} else if (hero.skin == 4) {
			Buff.affect(hero, NmImbue.class);
			hero.improveAttackSkill(4);
			hero.improveDefenseSkill(2);
		} else if (hero.skin == 5) {
			hero.improveAttackSkill(4);
			hero.improveDefenseSkill(2);
		} else if (hero.skin == 6) {
			hero.HTBoost += 5;
			hero.updateHT(true);
			hero.STR += 6;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 6;
			hero.improveMagicSkill(5);
			hero.improveAttackSkill(-6);
			hero.improveDefenseSkill(-33);
		} else {
			hero.improveAttackSkill(4);
			hero.improveDefenseSkill(2);
		}
		applyStartingItems(hero, startingItems(SOLDIER, hero.skin));
	}

	private static void initFollower(Hero hero) {
		//SPSEXPD: 物品部分已抽到 startingItems()（单一真源），这里只留属性与全局副作用
		if (hero.skin == 1) {
			hero.STR += 4; Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 4;
		} else if (hero.skin == 3) {
			hero.STR += 4;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 4;
		} else if (hero.skin == 4) {
			Dungeon.gold += 1000;
		} else if (hero.skin == 6) {
			hero.spp = 100;
		}
		applyStartingItems(hero, startingItems(FOLLOWER, hero.skin));
	}

	private static void initAscetic(Hero hero) {
		//SPSEXPD: 物品部分已抽到 startingItems()（单一真源），这里只留属性与全局副作用
		if (hero.skin == 1) {
			hero.STR += 4; Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 4;
			hero.improveMagicSkill(3);
		} else if (hero.skin == 2) {
			hero.HTBoost += 10;
			hero.updateHT(false);
			hero.improveAttackSkill(4);
			hero.improveDefenseSkill(2);
			hero.improveMagicSkill(-3);
			hero.improveMagicSkill(3);
		} else if (hero.skin == 3) {
			hero.STR += 4;
			Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 4;
			hero.improveMagicSkill(3);
		} else {
			hero.improveMagicSkill(3);
		}
		applyStartingItems(hero, startingItems(ASCETIC, hero.skin));
	}

	public String title() {
		return Messages.get(HeroClass.class, name());
	}

	/**
	 * SPSEXPD: 角色显示名 —— 按主题取名的预留接口。
	 *
	 * 现在一律回退到职业名；以后某个主题有专属名字时，只需在 themedName() 里登记，
	 * 选角界面与详情页的调用点都不用改。
	 */
	public String displayName( int skin ) {
		String themed = themedName( skin );
		return themed != null ? themed : Messages.titleCase( title() );
	}

	/**
	 * SPSEXPD: 主题专属角色名的登记入口（当前为空，一律返回 null）。
	 *
	 * 填法：按 (职业, 主题索引) 返回名字，文本走项目的内联简体 + en properties 机制，例如
	 *   case WARRIOR: if (skin == 3) return Messages.get(HeroClass.class, "warrior_name_3");
	 * 并在 HeroClass 的 InlineText 静态块里登记 .t("warrior_name_3", "年兽猎人")。
	 */
	private String themedName( int skin ) {
		return null;
	}

	public String desc(){
		return Messages.get(HeroClass.class, name()+"_desc");
	}

	public String shortDesc(){
		return Messages.get(HeroClass.class, name()+"_desc_short");
	}

	//SPSEXPD: 职业背景故事（供 WndHeroInfo 的「背景故事」页展示）
	public String lore(){
		return Messages.get(HeroClass.class, name()+"_lore");
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
		//SPSEXPD: 经典（0）与留空主题（>= IMPLEMENTED_SKINS）都用默认（经典）贴图
		if (skin <= 0 || skin >= IMPLEMENTED_SKINS) return spritesheet();
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

	//SPSEXPD: 选角界面顶部的特写横幅。不可玩职业没有对应素材，返回 null 由调用方回退到 splashArt()
	public String closeupArt(){
		switch (this) {
			case WARRIOR:
				return Assets.Splashes.Closeup.WARRIOR;
			case MAGE:
				return Assets.Splashes.Closeup.MAGE;
			case ROGUE:
				return Assets.Splashes.Closeup.ROGUE;
			case HUNTRESS:
				return Assets.Splashes.Closeup.HUNTRESS;
			case DUELIST:
				return Assets.Splashes.Closeup.DUELIST;
			case SPELLSWORD:
				return Assets.Splashes.Closeup.SPELLSWORD;
			case PERFORMER:
				return Assets.Splashes.Closeup.PERFORMER;
			case SOLDIER:
				return Assets.Splashes.Closeup.SOLDIER;
			case FOLLOWER:
				return Assets.Splashes.Closeup.FOLLOWER;
			case ASCETIC:
				return Assets.Splashes.Closeup.ASCETIC;
			default:
				return null;
		}
	}

	//SPSEXPD: 在选角头像图集（splashes/avatars.png，28x36 一格）中的格号；不在可玩列表的职业返回 -1
	public int avatarIndex(){
		for (int i = 0; i < SPS_PLAYABLE.length; i++){
			if (SPS_PLAYABLE[i] == this) return i;
		}
		return -1;
	}

	public boolean isUnlocked(){
		//SPSEXPD: every class is available from the start
		return true;
	}
	
	public String unlockMsg() {
		return shortDesc() + "\n\n" + Messages.get(HeroClass.class, name()+"_unlock");
	}

	//SPSEXPD: ---- 开局物品的声明式清单（单一真源）-----------------------------------
	//initXxx 用它发放物品；选角详情页用它展示「开局携带的物品」（每行图标 + 名称）。
	//只描述物品本身（去向 / 升级 / 数量 / 快捷栏 / 收集后处理）；属性调整与全局副作用
	//（STR、HTBoost、技能等级、LimitedDrops、gold 等）仍留在 initXxx 里。

	/** 物品去向 */
	public enum ItemSlot { BACKPACK, WEAPON, ARMOR, ARTIFACT, SECOND_WEP, ACCESSORY4, ACCESSORY5, MISC }

	/** 一条物品声明：存工厂而不是实例，避免共享实例被 identify/upgrade 污染 */
	public static final class LoadoutItem {
		final java.util.function.Supplier<Item> factory;
		final ItemSlot slot;
		final int upgrade;
		final int quantity;
		final int quickSlot;                                    // -1 = 不设快捷栏
		final java.util.function.BiConsumer<Hero, Item> after;  // 装备/收集后的额外处理，可空
		/** SPSEXPD: 开局是否鉴定；null = 按物品类型默认（见 identifyOnStart） */
		Boolean identify = null;

		LoadoutItem(java.util.function.Supplier<Item> factory, ItemSlot slot, int upgrade, int quantity,
					int quickSlot, java.util.function.BiConsumer<Hero, Item> after) {
			this.factory = factory;
			this.slot = slot;
			this.upgrade = upgrade;
			this.quantity = quantity;
			this.quickSlot = quickSlot;
			this.after = after;
		}
	}

	/**
	 * SPSEXPD: 开局默认不鉴定的物品类型 —— 照原实现，这些物品开场本来就是未鉴定的。
	 * 另外药水/卷轴一律不鉴定：它们的 identify() 会调用 setKnown() 全局标记该类型为已知，
	 * 开局不该自带这种「鉴定状态」（玩家自己鉴定）。
	 * 个别同类型但确实要鉴定的条目可在 startingItems 里用 Loadout.identified() 覆盖。
	 */
	private static boolean identifyOnStart(Item item) {
		if (item == null) return false;
		if (item instanceof pd.items.consum.potions.Potion) return false;
		if (item instanceof pd.items.consum.scrolls.Scroll) return false;
		return !(item instanceof JumpW || item instanceof JumpS || item instanceof JumpR
				|| item instanceof JumpM || item instanceof JumpP || item instanceof JumpA
				|| item instanceof JumpH
				|| item instanceof GnollMark || item instanceof DemoScroll || item instanceof StrBottle
				|| item instanceof UndeadBook || item instanceof Skull || item instanceof CopyBall
				|| item instanceof GrassBook || item instanceof MechPocket
				|| item instanceof pd.items.misc.RockManJumpshoes
				|| item instanceof pd.plants.Dewcatcher.Seed);
	}

	/** 一份开局物品清单 */
	public static final class Loadout {
		final java.util.ArrayList<LoadoutItem> items = new java.util.ArrayList<>();

		public Loadout backpack(java.util.function.Supplier<Item> factory) {
			return backpack(factory, 0, 1, -1, null);
		}

		public Loadout backpack(java.util.function.Supplier<Item> factory, int upgrade) {
			return backpack(factory, upgrade, 1, -1, null);
		}

		public Loadout backpack(java.util.function.Supplier<Item> factory, int upgrade, int quantity, int quickSlot,
								java.util.function.BiConsumer<Hero, Item> after) {
			items.add(new LoadoutItem(factory, ItemSlot.BACKPACK, upgrade, quantity, quickSlot, after));
			return this;
		}

		public Loadout equip(ItemSlot slot, java.util.function.Supplier<Item> factory) {
			return equip(slot, factory, 0, null);
		}

		public Loadout equip(ItemSlot slot, java.util.function.Supplier<Item> factory, int upgrade,
							 java.util.function.BiConsumer<Hero, Item> after) {
			items.add(new LoadoutItem(factory, slot, upgrade, 1, -1, after));
			return this;
		}

		public boolean isEmpty() {
			return items.isEmpty();
		}

		/** SPSEXPD: 让「最后加入的那一条」开局不鉴定 */
		public Loadout unidentified() {
			if (!items.isEmpty()) items.get(items.size() - 1).identify = Boolean.FALSE;
			return this;
		}

		/** SPSEXPD: 让「最后加入的那一条」开局照样鉴定（覆盖 identifyOnStart 的默认） */
		public Loadout identified() {
			if (!items.isEmpty()) items.get(items.size() - 1).identify = Boolean.TRUE;
			return this;
		}

		/** 供 UI 展示：按发放顺序新建物品实例（只读名字与图标，不做装备/收集） */
		public java.util.ArrayList<Item> preview() {
			java.util.ArrayList<Item> out = new java.util.ArrayList<>();
			for (LoadoutItem it : items) {
				Item item = it.factory.get();
				if (item == null) continue;
				//SPSEXPD: 预览要带上数量，否则 UI 的数量角标不会显示
				if (it.quantity > 1) item.quantity(it.quantity);
				out.add(item);
			}
			return out;
		}
	}

	/** 通用开局物品（展示用；实际发放仍在 initHero 通用段，那里还有挑战屏蔽等判定） */
	public static Loadout baseStartingItems() {
		Loadout l = new Loadout();
		l.equip(ItemSlot.ARMOR, () -> new ClothArmor());
		l.backpack(() -> new VelvetPouch());
		l.backpack(() -> new ArrowCollecter());
		l.backpack(() -> new KeyRing());
		//SPSEXPD: 所有角色开局携带一枚满充能的神圣护盾（与 initHero 通用段一致）
		l.backpack(() -> Ankhshield.fullCharge());
		l.backpack(() -> new DewVial());
		l.backpack(() -> new Pasty());
		l.backpack(() -> new NutCake());
		return l;
	}

	/**
	 * 「职业 × 主题」的开局物品。
	 * 还没迁移过来的职业返回空清单（它们的发放暂时仍在各自的 initXxx 里）。
	 */
	public static Loadout startingItems(HeroClass cls, int skin) {
		Loadout l = new Loadout();
		if (cls == null) return l;

		if (cls == WARRIOR) {
			switch (skin) {
				case 1:
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 1, null);
					l.equip(ItemSlot.ACCESSORY4, () -> new pd.items.equipment.rings.RingOfForce(), 1,
							(h, i) -> ((pd.items.equipment.rings.RingOfForce) i).activate(h));
					l.equip(ItemSlot.ACCESSORY5, () -> new pd.items.equipment.rings.RingOfMight(), 1,
							(h, i) -> ((pd.items.equipment.rings.RingOfMight) i).activate(h));
					l.backpack(() -> new AttackShield(), 0, 1, 0, null);
					l.backpack(() -> new Porksoup());
					break;
				case 2:
					l.equip(ItemSlot.ARMOR, () -> new BaseArmor(), 6, null);
					l.backpack(() -> new WandOfFirebolt(), 6, 1, -1, null);
					l.backpack(() -> new DemoScroll());
					l.backpack(() -> new StrBottle(), 0, 4, -1, null);
					l.backpack(() -> new JumpW());
					l.backpack(() -> new Porksoup());
					break;
				case 3:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.normalweapon.Spear(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new DiscArmor(), 0, null);
					l.equip(ItemSlot.SECOND_WEP, () -> new MissileShield(), 0, null);
					l.backpack(() -> new SavageHelmet());
					l.backpack(() -> new Porksoup());
					break;
				case 4:
					l.equip(ItemSlot.WEAPON, () -> new GunB(), 0,
							(h, i) -> ((pd.items.KindOfWeapon) i).activate(h));
					l.equip(ItemSlot.ARMOR, () -> new StyrofoamArmor(), 0, null);
					l.backpack(() -> new TestWeapon());
					l.backpack(() -> new WandOfTest());
					l.backpack(() -> new TestArmor());
					l.backpack(() -> new RewardPaper());
					l.backpack(() -> new JumpW());
					l.backpack(() -> new Porksoup());
					break;
				case 5:
					l.backpack(() -> new Porksoup());
					break;
				case 6:
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 1, null);
					l.equip(ItemSlot.ACCESSORY4, () -> new pd.items.equipment.rings.RingOfForce(), 1,
							(h, i) -> ((pd.items.equipment.rings.RingOfForce) i).activate(h));
					l.equip(ItemSlot.ACCESSORY5, () -> new pd.items.equipment.rings.RingOfMight(), 1,
							(h, i) -> ((pd.items.equipment.rings.RingOfMight) i).activate(h));
					l.backpack(() -> new SeriousPunch(), 0, 1, 0, null);
					l.backpack(() -> new JumpW());
					l.backpack(() -> new Porksoup());
					break;
				default:
					l.equip(ItemSlot.WEAPON, () -> new ShortSword(), 0, null);
					//SPSEXPD: 战士不再自带木甲与神木圆盾（护甲回落到 initHero 通用段的布甲）
					//SPSEXPD: 破损纹章改为以物品形式发放到背包，不再开局贴附在护甲上（由玩家自行贴附）
					l.backpack(() -> new BrokenSeal());
					l.backpack(() -> new Porksoup());
					//SPSEXPD: 战士另外携带一枚纹章碎片（只放背包，不贴护甲——护甲上只能挂一个纹章）
					l.backpack(() -> new pd.items.SealShard());
					break;
			}
		}

		if (cls == MAGE) {
			switch (skin) {
				case 1:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.normalweapon.Whip(), 2, null);
					l.equip(ItemSlot.ARMOR, () -> new pd.items.equipment.armor.normalarmor.LeatherArmor(), 1, null);
					l.backpack(() -> new CannonOfMage(), 0, 1, 0, null);
					l.backpack(() -> new Meatroll());
					break;
				case 2:
					l.equip(ItemSlot.WEAPON, () -> new Dagger(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 0, null);
					l.backpack(() -> new WandOfFirebolt(), 1, 1, -1, null);
					l.backpack(() -> new WandOfFreeze(), 1, 1, -1, null);
					l.backpack(() -> new WandOfLightning(), 1, 1, -1, null);
					l.backpack(() -> new GnollMark());
					l.backpack(() -> new JumpM());
					l.backpack(() -> new Meatroll());
					break;
				case 3:
					l.equip(ItemSlot.WEAPON, () -> new WoodenStaff(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 0, null);
					l.backpack(() -> new WandOfFirebolt());
					l.backpack(() -> new WandOfFreeze());
					l.backpack(() -> new GnollMark());
					l.backpack(() -> new PotionOfMage());
					l.backpack(() -> new Meatroll());
					break;
				case 4:
					l.equip(ItemSlot.WEAPON, () -> new ElfBow(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 0, null);
					l.backpack(() -> new JumpM());
					l.backpack(() -> new pd.items.consum.scrolls.ScrollOfRegrowth());
					l.backpack(() -> new Meatroll());
					break;
				case 5:
					l.backpack(() -> new Meatroll());
					break;
				case 6:
					l.equip(ItemSlot.WEAPON, () -> new ShortSword(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new pd.items.equipment.armor.normalarmor.ClothArmor(), 0, null);
					l.backpack(() -> new GnollMark());
					l.backpack(() -> new WandOfLight());
					l.backpack(() -> new JumpW());
					l.backpack(() -> new Meatroll());
					break;
				default:
					//SPSXPD: 法师开局用「老法杖」（法杖系特质的载体），激活并灌注魔法飞弹
					l.equip(ItemSlot.WEAPON, () -> new MagesStaff(), 0, (h, i) -> {
						MagesStaff staff = (MagesStaff) i;
						staff.activate(h);
						staff.imbueWand(new WandOfMagicMissile(), h);
					});
					l.backpack(() -> new WandOfMagicMissile(), 0, 1, 0, null);
					l.backpack(() -> new WandOfDisintegration(), 0, 1, 1, null);
					l.backpack(() -> new PotionOfMage());
					l.backpack(() -> new Meatroll());
					break;
			}
		}

		if (cls == ROGUE) {
			switch (skin) {
				case 1:
					l.equip(ItemSlot.WEAPON, () -> new LinkSword(), 0, (h, i) -> {
						((LinkSword) i).activate(h);
						Dungeon.quickslot.setSlot(1, i);
					});
					l.equip(ItemSlot.ARMOR, () -> new WoodenArmor(), 0, null);
					l.equip(ItemSlot.ARTIFACT, () -> new EtherealChains(), 0, (h, i) -> {
						((EtherealChains) i).activate(h);
						Dungeon.quickslot.setSlot(0, i);
					});
					l.backpack(() -> new RiceGruel());
					break;
				case 2:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.normalweapon.Dagger(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new pd.items.equipment.armor.normalarmor.ClothArmor(), 0, null);
					l.backpack(() -> new UndeadBook());
					l.backpack(() -> new Skull(5));
					l.backpack(() -> new JumpR());
					l.backpack(() -> new RiceGruel());
					break;
				case 3:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.normalweapon.Glaive(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new DiscArmor(), 0, null);
					l.equip(ItemSlot.ARTIFACT, () -> new CloakOfShadows(), 0, (h, i) -> {
						((CloakOfShadows) i).activate(h);
						Dungeon.quickslot.setSlot(0, i);
					});
					l.backpack(() -> new HorseTotem());
					l.backpack(() -> new RiceGruel());
					break;
				case 4:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.normalweapon.Dagger(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new pd.items.equipment.armor.normalarmor.ClothArmor(), 0, null);
					l.backpack(() -> new JumpR());
					l.backpack(() -> new NeedPaper());
					l.backpack(() -> new RiceGruel());
					break;
				case 5:
					l.backpack(() -> new RiceGruel());
					break;
				case 6:
					l.equip(ItemSlot.WEAPON, () -> new EleKatana(), 0, (h, i) -> Dungeon.quickslot.setSlot(0, i));
					l.equip(ItemSlot.ARMOR, () -> new pd.items.equipment.armor.normalarmor.ClothArmor(), 0, null);
					l.equip(ItemSlot.SECOND_WEP, () -> new BeastKnive(), 0, (h, i) -> {
						((BeastKnive) i).activate(h);
						Dungeon.quickslot.setSlot(1, i);
					});
					l.equip(ItemSlot.ARTIFACT, () -> new CloakOfShadows(), 0,
							(h, i) -> ((CloakOfShadows) i).activate(h));
					l.equip(ItemSlot.MISC, () -> new EtherealChains(), 3,
							(h, i) -> ((EtherealChains) i).activate(h));
					l.backpack(() -> new EmpBola(10));
					l.backpack(() -> new PoisonDart(), 0, 10, -1, null);
					l.backpack(() -> new BlindFruit(10));
					l.backpack(() -> new Weightstone());
					l.backpack(() -> new pd.items.Stylus());
					l.backpack(() -> new PotionOfHealing());
					l.backpack(() -> new ScrollOfMagicMapping());
					l.backpack(() -> new WandOfLightning(), 3, 1, -1, null);
					l.backpack(() -> new RiceGruel());
					break;
				case 7:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.normalweapon.Dagger(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 0, null);
					l.backpack(() -> new JumpR());
					l.backpack(() -> new AflyFood(), 0, 3, -1, null);
					l.backpack(() -> new AflyEgg(), 0, 3, -1, null);
					l.backpack(() -> new RiceGruel());
					break;
				default:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.normalweapon.Dagger(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 0, null);
					l.equip(ItemSlot.ARTIFACT, () -> new CloakOfShadows(), 0, (h, i) -> {
						((CloakOfShadows) i).activate(h);
						Dungeon.quickslot.setSlot(0, i);
					});
					//SPSEXPD: 盗贼开局携带白银之手（魔术之手法杖强化版，击杀充能）
					l.backpack(() -> new pd.items.equipment.wands.SilverHand());
					l.backpack(() -> new BlindFruit(3), 0, 1, 1, null);
					l.backpack(() -> new RiceGruel());
					break;
			}
		}

		if (cls == HUNTRESS) {
			switch (skin) {
				case 1:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.normalweapon.Dagger(), 1, null);
					l.equip(ItemSlot.ARMOR, () -> new pd.items.equipment.armor.normalarmor.RubberArmor(), 1, null);
					l.equip(ItemSlot.ARTIFACT, () -> new TimeOclock(), 5, (h, i) -> {
						((TimeOclock) i).activate(h);
						Dungeon.quickslot.setSlot(0, i);
					});
					l.backpack(() -> new ManyKnive(), 1, 1, -1, null);
					l.backpack(() -> new EscapeKnive(5), 0, 1, 1, null);
					break;
				case 2:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.normalweapon.Knuckles(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new pd.items.equipment.armor.normalarmor.ClothArmor(), 0, null);
					l.backpack(() -> new TaurcenBow());
					l.backpack(() -> new JumpH());
					break;
				case 3:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.normalweapon.Knuckles(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new pd.items.equipment.armor.normalarmor.ClothArmor(), 0, null);
					l.backpack(() -> new TaurcenBow());
					l.backpack(() -> new RangeBag());
					break;
				case 4:
					l.equip(ItemSlot.WEAPON, () -> new WoodenStaff(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new pd.items.equipment.armor.normalarmor.ClothArmor(), 0, null);
					l.backpack(() -> new PPC());
					l.backpack(() -> new JumpH());
					break;
				case 5:
					//skin 5（混沌）的整套装备由 initChaoticSkin() 在 initHero 通用段发放
					break;
				case 6:
					l.equip(ItemSlot.WEAPON, () -> new Sling(), 0,
							(h, i) -> ((pd.items.KindOfWeapon) i).activate(h));
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 0, null);
					l.backpack(() -> new ShootGun(), 0, 1, 0, null);
					l.backpack(() -> new JumpS());
					l.backpack(() -> new EmpBola(3));
					break;
				default:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.normalweapon.Knuckles(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new ClothArmor(), 0, null);
					//SPSXPD: 女猎手开局携带灵能弓（替换原回旋镖）
					l.backpack(() -> new SpiritBow(), 0, 1, 0, null);
					//SPSEXPD: 女猎手开局另携带一枚近战化的回旋镖（充能投掷/命中麻痹/接住重置）
					l.backpack(() -> new Boomerang());
					l.backpack(() -> new EmpBola(3), 0, 1, 1, null);
					break;
			}
			//所有主题共通
			l.backpack(() -> new Vegetablekebab());
		}

		if (cls == DUELIST) {
			l.equip(ItemSlot.WEAPON, () -> new Rapier(), 0, (h, i) -> {
				((pd.items.KindOfWeapon) i).activate(h);
				Dungeon.quickslot.setSlot(0, i);
			});
			//SPSEXPD: 决斗家开局携带护手盾（副手，只提供格挡）
			l.equip(ItemSlot.SECOND_WEP, () -> new pd.items.misc.GauntletShield(), 0, null);
			l.backpack(() -> new ThrowingSpike(), 0, 2, 1, null);
		}

		if (cls == CLERIC) {
			l.equip(ItemSlot.WEAPON, () -> new Cudgel(), 0,
					(h, i) -> ((pd.items.KindOfWeapon) i).activate(h));
			l.equip(ItemSlot.ARTIFACT, () -> new HolyTome(), 0, (h, i) -> {
				((HolyTome) i).activate(h);
				Dungeon.quickslot.setSlot(0, i);
			});
		}

		if (cls == SPELLSWORD) {
			l.equip(ItemSlot.WEAPON, () -> new Spellblade(), 0, (h, i) -> {
				((pd.items.KindOfWeapon) i).activate(h);
				Dungeon.quickslot.setSlot(1, i);
			});
			l.backpack(() -> new WandOfMagicMissile(), 0, 1, 0, null);
		}

		if (cls == PERFORMER) {
			switch (skin) {
				case 1:
					l.equip(ItemSlot.WEAPON, () -> new GunA(), 2, null);
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 1, null);
					l.backpack(() -> new GoldAmmo());
					l.backpack(() -> new WoodenAmmo());
					l.backpack(() -> new BattleAmmo());
					l.equip(ItemSlot.ARTIFACT, () -> new AlienBag(), 0, (h, i) -> ((AlienBag) i).activate(h));
					l.backpack(() -> new BShovel(), 0, 1, 0, null);
					l.backpack(() -> new NutCookie(6));
					l.backpack(() -> new DungeonBomb());
					break;
				case 2:
					l.equip(ItemSlot.WEAPON, () -> new HolyWater(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new BaseArmor(), 0, null);
					l.backpack(() -> new CopyBall());
					l.backpack(() -> new PotionOfHealing(), 0, 2, -1, null);
					l.backpack(() -> new JumpP());
					l.backpack(() -> new NutCookie(6));
					l.backpack(() -> new DungeonBomb());
					break;
				case 3:
					l.equip(ItemSlot.WEAPON, () -> new Triangolo(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new pd.items.equipment.armor.normalarmor.ClothArmor(), 0, null);
					l.backpack(() -> new Shovel(), 0, 1, 0, null);
					l.backpack(() -> new DanceLion(), 0, 1, 1, null);
					l.backpack(() -> new SpsFireBomb());
					l.backpack(() -> new IceBomb());
					l.backpack(() -> new StormBomb());
					l.backpack(() -> new DungeonBomb());
					l.backpack(() -> new DungeonBomb());
					l.backpack(() -> new ScrollOfLullaby());
					l.backpack(() -> new NutCookie(6));
					break;
				case 4:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.Mace(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new pd.items.equipment.armor.normalarmor.LeatherArmor(), 0, null);
					l.backpack(() -> new LeaderFlag());
					l.backpack(() -> new JumpP());
					l.backpack(() -> new NutCookie(6));
					l.backpack(() -> new DungeonBomb());
					break;
				case 5:
					l.backpack(() -> new NutCookie(6));
					l.backpack(() -> new DungeonBomb());
					break;
				case 6:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.Mace(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new pd.items.equipment.armor.normalarmor.LeatherArmor(), 0, null);
					l.backpack(() -> new PPC2(), 0, 1, 0, null);
					l.backpack(() -> new JumpP());
					l.backpack(() -> new NutCookie(6));
					l.backpack(() -> new DungeonBomb());
					break;
				case 7:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.missiles.MegaCannon(), 0,
							(h, i) -> Dungeon.quickslot.setSlot(0, i));
					l.equip(ItemSlot.ARMOR, () -> new PerformerArmor(), 0, null);
					l.equip(ItemSlot.SECOND_WEP, () -> new pd.items.equipment.weapon.melee.start.XSaber(), 0,
							(h, i) -> Dungeon.quickslot.setSlot(1, i));
					l.backpack(() -> new pd.plants.Dewcatcher.Seed(), 0, 2, -1, null);
					l.backpack(() -> new pd.items.misc.RockManJumpshoes());
					l.backpack(() -> new NutCookie(6));
					l.backpack(() -> new DungeonBomb());
					break;
				default:
					l.equip(ItemSlot.WEAPON, () -> new Triangolo(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new ClothArmor(), 0, null);
					l.backpack(() -> new Shovel(), 0, 1, 0, null);
					l.backpack(() -> new SpsFireBomb());
					l.backpack(() -> new IceBomb());
					l.backpack(() -> new StormBomb());
					l.backpack(() -> new DungeonBomb());
					l.backpack(() -> new DungeonBomb());
					l.backpack(() -> new ScrollOfLullaby());
					l.backpack(() -> new NutCookie(6));
					break;
			}
		}

		if (cls == SOLDIER) {
			switch (skin) {
				case 1:
					l.equip(ItemSlot.ARMOR, () -> new pd.items.equipment.armor.normalarmor.LeatherArmor(), 3, null);
					l.backpack(() -> new AttackShoes(), 0, 1, 0, null);
					l.backpack(() -> new MKbox(), 0, 1, 1, null);
					l.backpack(() -> new MixPizza(4));
					break;
				case 2:
					l.equip(ItemSlot.WEAPON, () -> new GunC(), 0,
							(h, i) -> ((pd.items.KindOfWeapon) i).activate(h));
					l.equip(ItemSlot.ARMOR, () -> new BaseArmor(), 0, null);
					l.backpack(() -> new MechPocket());
					l.backpack(() -> new JumpS());
					l.backpack(() -> new MixPizza(4));
					break;
				case 3:
					l.equip(ItemSlot.WEAPON, () -> new GunA(), 0,
							(h, i) -> ((pd.items.KindOfWeapon) i).activate(h));
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 0, null);
					l.backpack(() -> new HeavyAmmo());
					l.backpack(() -> new GunOfSoldier(), 0, 1, 0, null);
					l.backpack(() -> new HealBag(), 0, 1, 1, null);
					l.backpack(() -> new MixPizza(4));
					break;
				case 4:
					l.equip(ItemSlot.WEAPON, () -> new GunA(), 0,
							(h, i) -> ((pd.items.KindOfWeapon) i).activate(h));
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 0, null);
					l.backpack(() -> new JumpS());
					l.backpack(() -> new NmHealBag());
					l.backpack(() -> new EscapeKnive(10));
					l.backpack(() -> new MixPizza(4));
					break;
				case 5:
					l.backpack(() -> new MixPizza(4));
					break;
				case 6:
					l.equip(ItemSlot.WEAPON, () -> new WoodenStaff(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new BaseArmor(), 0, null);
					l.backpack(() -> new ShootGun(), 0, 1, 0, null);
					l.backpack(() -> new JumpS());
					l.backpack(() -> new MixPizza(4));
					break;
				case 7:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.start.BunnyDagger(), 0,
							(h, i) -> Dungeon.quickslot.setSlot(0, i));
					l.equip(ItemSlot.SECOND_WEP, () -> new pd.items.equipment.weapon.melee.start.BunnySpanner(), 0,
							(h, i) -> Dungeon.quickslot.setSlot(1, i));
					l.equip(ItemSlot.ARMOR, () -> new RogueArmor(), 0, null);
					l.backpack(() -> new JumpR());
					l.equip(ItemSlot.ARTIFACT, () -> new AlienBag(), 0, (h, i) -> ((AlienBag) i).activate(h));
					l.backpack(() -> new MixPizza(4));
					break;
				default:
					l.equip(ItemSlot.WEAPON, () -> new Sling(), 0,
							(h, i) -> ((pd.items.KindOfWeapon) i).activate(h));
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 0, null);
					//SPSEXPD: 星兵开局携带无尽弹药袋（该物品已移出常规神器池）
					l.equip(ItemSlot.ARTIFACT, () -> new pd.items.equipment.artifacts.EndlessAmmoBag(), 0,
							(h, i) -> ((pd.items.equipment.artifacts.EndlessAmmoBag) i).activate(h));
					l.backpack(() -> new GunOfSoldier(), 0, 1, 0, null);
					l.backpack(() -> new EscapeKnive(3), 0, 1, 1, null);
					l.backpack(() -> new MixPizza(4));
					break;
			}
		}

		if (cls == FOLLOWER) {
			switch (skin) {
				case 1:
					l.equip(ItemSlot.WEAPON, () -> new DiamondPickaxe(), 0, (h, i) -> Dungeon.quickslot.setSlot(0, i));
					l.equip(ItemSlot.ARMOR, () -> new pd.items.equipment.armor.normalarmor.LeatherArmor(), 0, null);
					l.equip(ItemSlot.SECOND_WEP, () -> new PixelTorch(), 0, (h, i) -> {
						((pd.items.KindOfWeapon) i).activate(h);
						Dungeon.quickslot.setSlot(1, i);
					});
					l.backpack(() -> new MoonCake());
					break;
				case 2:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.normalweapon.Dagger(), 2,
							(h, i) -> Dungeon.quickslot.setSlot(0, i));
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 0, null);
					l.equip(ItemSlot.ARTIFACT, () -> new Pylon(), 2, (h, i) -> {
						((Pylon) i).activate(h);
						Dungeon.quickslot.setSlot(1, i);
					});
					l.backpack(() -> new JumpF());
					l.backpack(() -> new MoonCake());
					break;
				case 3:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.normalweapon.Rapier(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 0, null);
					l.backpack(() -> new FaithSign());
					l.equip(ItemSlot.ARTIFACT, () -> new Pylon(), 0, (h, i) -> {
						((Pylon) i).activate(h);
						Dungeon.quickslot.setSlot(0, i);
					});
					l.backpack(() -> new PotionOfHealing());
					l.backpack(() -> new MoonCake());
					break;
				case 4:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.normalweapon.Knuckles(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 0, null);
					l.backpack(() -> new DiceTower());
					l.backpack(() -> new JumpF());
					l.backpack(() -> new MoonCake());
					break;
				case 5:
					l.backpack(() -> new MoonCake());
					break;
				case 6:
					l.equip(ItemSlot.WEAPON, () -> new TrickSand(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 0, null);
					l.backpack(() -> new Skull(5)).identified();
					l.backpack(() -> new Ankh());
					l.backpack(() -> new Ankh());
					l.backpack(() -> new JumpF());
					l.backpack(() -> new MoonCake());
					break;
				default:
					l.equip(ItemSlot.WEAPON, () -> new WoodenStaff(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new ClothArmor(), 0, null);
					//SPSEXPD: 信徒开局携带蒲团（占位道具，暂无效果）
					l.backpack(() -> new pd.items.misc.Futon());
					l.backpack(() -> new PotionOfHealing());
					l.backpack(() -> new FaithSign());
					l.backpack(() -> new MoonCake());
					break;
			}
		}

		if (cls == ASCETIC) {
			switch (skin) {
				case 1:
					l.equip(ItemSlot.WEAPON, () -> new HolyMace(), 1, (h, i) -> Dungeon.quickslot.setSlot(0, i));
					l.equip(ItemSlot.ARMOR, () -> new pd.items.equipment.armor.normalarmor.LeatherArmor(), 1, null);
					l.equip(ItemSlot.SECOND_WEP, () -> new BraveBook(), 1, (h, i) -> {
						((pd.items.KindOfWeapon) i).activate(h);
						Dungeon.quickslot.setSlot(1, i);
					});
					l.backpack(() -> new FruitCandy(3));
					break;
				case 2:
					l.equip(ItemSlot.WEAPON, () -> new WoodenStaff(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new LifeArmor(), 0, (h, i) -> ((LifeArmor) i).activate(h));
					l.backpack(() -> new GrassBook());
					l.backpack(() -> new JumpA());
					l.backpack(() -> new FruitCandy(3));
					break;
				case 3:
					l.equip(ItemSlot.WEAPON, () -> new Whisk(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new pd.items.equipment.armor.normalarmor.LeatherArmor(), 0, null);
					l.backpack(() -> new BigBattery(), 0, 1, 0, null);
					l.equip(ItemSlot.ARTIFACT, () -> new UnstableSpellbook(), 0, (h, i) -> {
						((UnstableSpellbook) i).activate(h);
						Dungeon.quickslot.setSlot(1, i);
					});
					l.backpack(() -> new FruitCandy(3));
					break;
				case 4:
				case 5:
				case 6:
					l.backpack(() -> new FruitCandy(3));
					break;
				case 7:
					l.equip(ItemSlot.WEAPON, () -> new pd.items.equipment.weapon.melee.special.NinjaFan(), 0,
							(h, i) -> Dungeon.quickslot.setSlot(1, i));
					l.equip(ItemSlot.ARMOR, () -> new RenBArmor(), 0, null);
					l.equip(ItemSlot.ARTIFACT, () -> new DriedRose(), 0, (h, i) -> ((DriedRose) i).activate(h));
					l.backpack(() -> new Pylon(), 0, 1, 0, (h, i) -> ((Pylon) i).activate(h));
					l.backpack(() -> new WandOfFirebolt());
					l.backpack(() -> new JumpW());
					l.backpack(() -> new FruitCandy(3));
					break;
				default:
					l.equip(ItemSlot.WEAPON, () -> new TrickSand(), 0, null);
					l.equip(ItemSlot.ARMOR, () -> new VestArmor(), 0, null);
					//SPSEXPD: 苦修者开局携带经卷耳环（占位神器，暂不提供效果）
					l.equip(ItemSlot.ARTIFACT, () -> new pd.items.equipment.artifacts.ScrollEarring(), 0,
							(h, i) -> ((pd.items.equipment.artifacts.ScrollEarring) i).activate(h));
					l.backpack(() -> new BigBattery(), 0, 1, 0, null);
					l.backpack(() -> new ActiveMrDestructo());
					l.backpack(() -> new FruitCandy(3));
					break;
			}
		}

		return l;
	}

	/** 唯一发放实现：identify → 升级/数量 → 装备或收集 → 快捷栏 → 额外处理 */
	public static void applyStartingItems(Hero hero, Loadout loadout) {
		if (hero == null || loadout == null) return;

		for (LoadoutItem it : loadout.items) {
			Item item = it.factory.get();
			if (item == null) continue;

			//SPSEXPD: 照原实现 —— 部分物品开局本就是未鉴定的
			boolean identify = (it.identify != null) ? it.identify : identifyOnStart(item);
			if (identify) item.identify();
			if (it.quantity > 1) item.quantity(it.quantity);
			if (it.upgrade > 0) item.upgrade(it.upgrade);

			switch (it.slot) {
				case WEAPON:
					if (item instanceof pd.items.KindOfWeapon) hero.belongings.weapon = (pd.items.KindOfWeapon) item;
					else item.collect();
					break;
				case ARMOR:
					if (item instanceof pd.items.equipment.armor.Armor) hero.belongings.armor = (pd.items.equipment.armor.Armor) item;
					else item.collect();
					break;
				case ARTIFACT:
					if (item instanceof pd.items.KindofMisc) hero.belongings.artifact = (pd.items.KindofMisc) item;
					else item.collect();
					break;
				case SECOND_WEP:
					if (item instanceof pd.items.KindOfWeapon) hero.belongings.secondWep = (pd.items.KindOfWeapon) item;
					else item.collect();
					break;
				case ACCESSORY4:
					if (item instanceof pd.items.KindofMisc) hero.belongings.accessory4 = (pd.items.KindofMisc) item;
					else item.collect();
					break;
				case ACCESSORY5:
					if (item instanceof pd.items.KindofMisc) hero.belongings.accessory5 = (pd.items.KindofMisc) item;
					else item.collect();
					break;
				case MISC:
					if (item instanceof pd.items.KindofMisc) hero.belongings.misc = (pd.items.KindofMisc) item;
					else item.collect();
					break;
				default:
					item.collect();
					break;
			}

			if (it.quickSlot >= 0) Dungeon.quickslot.setSlot(it.quickSlot, item);
			if (it.after != null) it.after.accept(hero, item);
		}
	}

	/**
	 * SPSEXPD: 展示用的「开局物品」清单 —— 按实际发放语义合并通用清单与职业清单：
	 * 装备类槽位（非 BACKPACK）同槽位时，职业条目会在 applyStartingItems 里覆盖通用条目，
	 * 因此展示时也跳过被覆盖的通用条目；背包条目则全部保留。
	 */
	public static java.util.ArrayList<Item> previewStartingItems(HeroClass cls, int skin) {
		Loadout base = baseStartingItems();
		Loadout spec = startingItems(cls, skin);

		java.util.ArrayList<Item> out = new java.util.ArrayList<>();
		for (LoadoutItem it : base.items) {
			if (it.slot != ItemSlot.BACKPACK && hasSlot(spec, it.slot)) continue;
			addPreview(out, it);
		}
		for (LoadoutItem it : spec.items) addPreview(out, it);
		return out;
	}

	private static boolean hasSlot(Loadout loadout, ItemSlot slot) {
		for (LoadoutItem it : loadout.items) {
			if (it.slot == slot) return true;
		}
		return false;
	}

	private static void addPreview(java.util.ArrayList<Item> out, LoadoutItem it) {
		Item item = it.factory.get();
		if (item == null) return;
		//SPSEXPD: 预览要带上数量，否则 UI 的数量角标不会显示
		if (it.quantity > 1) item.quantity(it.quantity);
		out.add(item);
	}

	//SPSEXPD: ---- 开局物品在详情页里的「按用途分组」（用户后续可自行调整这里的规则）-------
	public enum ItemGroup {
		EQUIP,    // 装备套装（武器 / 护甲 / 副手 / 饰品）
		BAG,      // 包裹袋
		SPECIAL,  // 特殊装备（破损纹章、刻印笔… 不属于上面任何一类的本体物品）
		WAND,     // 法杖
		FOOD,     // 食品
		POTION,   // 药水
		SCROLL,   // 卷轴
		MISSILE,  // 投掷与弹药
		BOMB,     // 炸弹
		OTHER,    // 其它
		INNATE    // 固有物（所有角色开局固定携带物：露珠瓶、神圣护盾；显示在最下）
	}

	/** 判定某个物品属于哪一组（顺序有意义：炸弹先于投掷物、包袋先于食物类容器） */
	public static ItemGroup groupOf(Item item) {
		if (item == null) return ItemGroup.OTHER;
		if (item instanceof pd.items.equipment.bags.Bag) return ItemGroup.BAG;
		if (item instanceof pd.items.consum.food.Food) return ItemGroup.FOOD;
		if (item instanceof pd.items.consum.potions.Potion) return ItemGroup.POTION;
		if (item instanceof pd.items.consum.scrolls.Scroll) return ItemGroup.SCROLL;
		if (item instanceof pd.items.equipment.wands.Wand) return ItemGroup.WAND;
		if (item instanceof pd.items.equipment.bombs.Bomb) return ItemGroup.BOMB;
		if (item instanceof pd.items.equipment.weapon.missiles.MissileWeapon) return ItemGroup.MISSILE;
		if (item instanceof pd.items.KindOfWeapon
				|| item instanceof pd.items.equipment.armor.Armor
				|| item instanceof pd.items.KindofMisc) return ItemGroup.EQUIP;
		//SPSEXPD: 固有物——所有角色开局固定携带的露珠瓶与神圣护盾，单独成组并显示在最下
		if (item instanceof pd.items.DewVial || item instanceof Ankhshield) return ItemGroup.INNATE;
		return ItemGroup.SPECIAL;
	}

}
