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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GiftUnlocks;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.actbuff.NmImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.AscendedForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.Trinity;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Challenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.ElementalStrike;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Feint;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpectralBlades;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.ElementalBlast;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WarpBeacon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.DeathMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.SmokeBomb;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.HeroicLeap;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Shockwave;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KnowledgeBook;
import com.shatteredpixel.shatteredpixeldungeon.items.StrBottle;
import com.shatteredpixel.shatteredpixeldungeon.items.TransmutationBall;
import com.shatteredpixel.shatteredpixeldungeon.items.UnBlessAnkh;
import com.shatteredpixel.shatteredpixeldungeon.items.Weightstone;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.DewVial;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.BaseArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.DiscArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.StyrofoamArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.WoodenArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.VestArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.specialarmor.LifeArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.specialarmor.PerformerArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.specialarmor.RenBArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.specialarmor.RogueArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.specialarmor.TestArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlienBag;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.FlyChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimeOclock;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Pylon;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ArrowCollecter;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.KeyRing;
import com.shatteredpixel.shatteredpixeldungeon.items.food.staplefood.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.FruitCandy;
import com.shatteredpixel.shatteredpixeldungeon.items.food.AflyFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Honey;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Meatroll;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.MixPizza;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.MoonCake;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.NutCookie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.NutCake;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Porksoup;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.RiceGruel;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Vegetablekebab;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.YearFood;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Hardpill;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Powerpill;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Smashpill;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.MissileShield;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.AttackShield;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.AttackShoes;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.BShovel;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.MKbox;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.PotionOfMage;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.Shovel;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.GunOfSoldier;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.BigBattery;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.DanceLion;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.HealBag;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.HorseTotem;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.RangeBag;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.SavageHelmet;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.RewardPaper;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.NeedPaper;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.PPC;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.PPC2;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.DiceTower;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.Ankhshield;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.SeriousPunch;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.LeaderFlag;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.NmHealBag;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.DemoScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.FaithSign;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.JumpW;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.JumpM;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.JumpR;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.JumpH;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.JumpP;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.CopyBall;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.JumpS;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.JumpF;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.JumpA;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.GrassBook;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.MechPocket;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.GnollMark;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.UndeadBook;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.AflyEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMending;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfShield;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.skills.ClassSkill;
import com.shatteredpixel.shatteredpixeldungeon.items.summon.ActiveMrDestructo;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFirebolt;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFreeze;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTest;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CannonOfMage;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Cudgel;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HolyWater;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Rapier;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spellblade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.MageBook;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.ShortSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.start.BraveBook;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.start.BeastKnive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.start.DiamondPickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.start.EleKatana;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.start.HolyMace;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.start.LinkSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.start.PixelTorch;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.start.Whisk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.TestWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.TrickSand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.WoodenStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion.Triangolo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ManyKnive;
import com.shatteredpixel.shatteredpixeldungeon.plants.Dewcatcher;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.TaurcenBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ElfBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ShootGun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpike;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing.EscapeKnive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.BlindFruit;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing.Boomerang;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing.EmpBola;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing.Skull;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.PoisonDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.Sling;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunA;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunB;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunC;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.BattleAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.GoldAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.HeavyAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.WoodenAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.SpsFireBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.IceBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.StormBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.DungeonBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion.ReedPipe;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.DeviceCompat;

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
			com.shatteredpixel.shatteredpixeldungeon.items.misc.LuckyBadge badge =
					new com.shatteredpixel.shatteredpixeldungeon.items.misc.LuckyBadge();
			badge.identify().upgrade( GiftUnlocks.luckyGiftBonus() );
			badge.collect();
		}

		for (int i = 0; i < GiftUnlocks.seedGiftCount(); i++) {
			Generator.random( Generator.Category.SEED ).collect();
		}
		if (GiftUnlocks.plantGiftCount() > 0) {
			new com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.buildblock.PlantPotBlock( 1 ).identify().collect();
		}
		if (GiftUnlocks.weaponGiftCount() > 0) {
			Generator.random( Generator.Category.MELEEWEAPON ).uncurse().identify().upgrade( 1 ).collect();
		}
		if (GiftUnlocks.armorGiftCount() > 0) {
			Generator.random( Generator.Category.ARMOR ).uncurse().identify().upgrade( 1 ).collect();
		}
		if (GiftUnlocks.rocketGiftCount() > 0) {
			new com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.fusion.RocketMissile().identify().collect();
		}
		if (GiftUnlocks.ringGiftCount() > 0) {
			Generator.random( Generator.Category.RING ).uncurse().identify().degrade( 10 ).collect();
		}
		if (GiftUnlocks.artifactGiftCount() > 0) {
			new com.shatteredpixel.shatteredpixeldungeon.items.artifacts.fusion.NoomlinCrown().identify().collect();
		}
		if (GiftUnlocks.wandGiftCount() > 0) {
			new WandOfTest().identify().collect();
		}
		if (GiftUnlocks.robotGiftCount() > 0) {
			new com.shatteredpixel.shatteredpixeldungeon.items.summon.ChinaMech().identify().collect();
		}
		if (GiftUnlocks.artItemGiftCount() > 0) {
			new com.shatteredpixel.shatteredpixeldungeon.items.sellitem.JumperDancer().identify().collect();
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
			new PotionOfMight().collect(hero.belongings.backpack);
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
			(hero.belongings.ring = ring).identify();
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
			com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce force = new com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce();
			(hero.belongings.misc = force).identify().upgrade(1); force.activate(hero);
			com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight might = new com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight();
			(hero.belongings.ring = might).identify().upgrade(1); might.activate(hero);
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
			(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Spear()).identify();
			(hero.belongings.armor = new DiscArmor()).identify();
			new MissileShield().identify().collect();
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
			com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce force = new com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce();
			(hero.belongings.misc = force).identify().upgrade(1); force.activate(hero);
			com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight might = new com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight();
			(hero.belongings.ring = might).identify().upgrade(1); might.activate(hero);
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
		new MissileShield().identify().collect();
		new Powerpill().identify().collect();
		new Smashpill().identify().collect();
		new Hardpill().identify().collect();
		new Porksoup().identify().collect();

		new PotionOfStrength().identify();
		new ScrollOfUpgrade().identify();
	}

	private static void initMage( Hero hero ) {
		if (hero.skin == 1) {
			hero.STR += 4; Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 4;
			(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Whip()).identify().upgrade(2);
			(hero.belongings.armor = new com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.LeatherArmor()).identify().upgrade(1);
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
			new com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRegrowth().identify().collect();
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
			(hero.belongings.armor = new com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.ClothArmor()).identify();
			new GnollMark().collect();
			new WandOfLight().identify().collect();
			new Powerpill().identify().collect();
			new Smashpill().identify().collect();
			new Hardpill().identify().collect();
			new JumpW().collect();
			hero.improveMagicSkill(3);
			new Meatroll().identify().collect();
			new ScrollOfIdentify().identify();
			new PotionOfLiquidFlame().identify();
			return;
		}
		(hero.belongings.weapon = new MageBook()).identify();
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
			(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Dagger()).identify();
			(hero.belongings.armor = new com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.ClothArmor()).identify();
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
			(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Glaive()).identify();
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
			(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Dagger()).identify();
			(hero.belongings.armor = new com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.ClothArmor()).identify();
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
			(hero.belongings.armor = new com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.ClothArmor()).identify();
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
			new com.shatteredpixel.shatteredpixeldungeon.items.Stylus().identify().collect();
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
			(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Dagger()).identify();
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
		(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Dagger()).identify();
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
			com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Dagger dagger = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Dagger();
			dagger.upgrade();
			(hero.belongings.weapon = dagger).identify();
			(hero.belongings.armor = new com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.RubberArmor()).identify().upgrade(1);
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
			(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Knuckles()).identify();
			(hero.belongings.armor = new com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.ClothArmor()).identify();
			new TaurcenBow().identify().collect();
			new JumpH().collect();
		} else if (hero.skin == 3) {
			(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Knuckles()).identify();
			(hero.belongings.armor = new com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.ClothArmor()).identify();
			new TaurcenBow().identify().collect();
			new RangeBag().identify().collect();
		} else if (hero.skin == 4) {
			(hero.belongings.weapon = new WoodenStaff()).identify();
			(hero.belongings.armor = new com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.ClothArmor()).identify();
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
			(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Knuckles()).identify();
			(hero.belongings.armor = new ClothArmor()).identify();
			Boomerang boomerang = new Boomerang();
			boomerang.identify().collect();
			EmpBola bola = new EmpBola(3);
			bola.identify().collect();
			Dungeon.quickslot.setSlot(0, boomerang);
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
			new PotionOfMending().identify().collect();
			new PotionOfHealing().identify().collect();
			new JumpP().collect();
			new NutCookie(6).identify().collect();
			new DungeonBomb().identify().collect();
			new ScrollOfLullaby().identify();
			new PotionOfPurity().identify();
			return;
		}
		if (hero.skin == 3) {
			(hero.belongings.weapon = new Triangolo()).identify();
			(hero.belongings.armor = new com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.ClothArmor()).identify();
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
			(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace()).identify();
			(hero.belongings.armor = new com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.LeatherArmor()).identify();
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
			(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace()).identify();
			(hero.belongings.armor = new com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.LeatherArmor()).identify();
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
			com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MegaCannon cannon =
					new com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MegaCannon();
			(hero.belongings.weapon = cannon).identify();
			(hero.belongings.armor = new PerformerArmor()).identify();
			(hero.belongings.secondWep = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.start.XSaber()).identify();
			new com.shatteredpixel.shatteredpixeldungeon.plants.Dewcatcher.Seed().quantity(2).collect();
			new com.shatteredpixel.shatteredpixeldungeon.items.misc.RockManJumpshoes().collect();
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
			(hero.belongings.armor = new com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.LeatherArmor()).identify().upgrade(3);
			AttackShoes shoes = new AttackShoes(); shoes.identify().collect(); MKbox box = new MKbox(); box.identify().collect();
			Dungeon.quickslot.setSlot(0, shoes); Dungeon.quickslot.setSlot(1, box);
			new MixPizza(4).identify().collect(); new ScrollOfRage().identify(); new PotionOfMending().identify(); hero.improveAttackSkill(4); hero.improveDefenseSkill(2);
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
			new PotionOfMending().identify();
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
			new PotionOfMending().identify();
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
			new PotionOfMending().identify();
			new MixPizza(4).identify().collect();
			return;
		}
		if (hero.skin == 5) {
			hero.improveAttackSkill(4);
			hero.improveDefenseSkill(2);
			new ScrollOfRage().identify();
			new PotionOfMending().identify();
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
			new PotionOfMending().identify();
			new MixPizza(4).identify().collect();
			Dungeon.quickslot.setSlot(0, shootGun);
			return;
		}
		if (hero.skin == 7) {
			(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.start.BunnyDagger()).identify();
			(hero.belongings.secondWep = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.start.BunnySpanner()).identify();
			(hero.belongings.armor = new RogueArmor()).identify();
			new JumpR().collect();
			AlienBag alienBag = new AlienBag();
			(hero.belongings.artifact = alienBag).identify();
			alienBag.activate(hero);
			hero.improveAttackSkill(4);
			hero.improveDefenseSkill(2);
			new ScrollOfRage().identify();
			new PotionOfMending().identify();
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
		new PotionOfMending().identify();
		new MixPizza(4).identify().collect();
	}

	private static void initFollower(Hero hero) {
		if (hero.skin == 1) {
			(hero.belongings.weapon = new DiamondPickaxe()).identify();
			(hero.belongings.armor = new com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.LeatherArmor()).identify();
			(hero.belongings.secondWep = new PixelTorch()).identify(); hero.belongings.secondWep.activate(hero);
			hero.STR += 4; Dungeon.LimitedDrops.STRENGTH_POTIONS.count += 4;
			new MoonCake().identify().collect(); new ScrollOfTerror().identify(); new PotionOfHealing().identify();
			Dungeon.quickslot.setSlot(0, hero.belongings.weapon); Dungeon.quickslot.setSlot(1, hero.belongings.secondWep); return;
		}
		if (hero.skin == 2) {
			(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Dagger()).identify().upgrade(2);
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
			(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Rapier()).identify();
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
			(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Knuckles()).identify();
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
			(hero.belongings.armor = new com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.LeatherArmor()).identify().upgrade(1);
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
			(hero.belongings.armor = new com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.LeatherArmor()).identify();
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
			(hero.belongings.weapon = new com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.NinjaFan()).identify();
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
		//always unlock on debug builds
		if (DeviceCompat.isDebug()) return true;

		switch (this){
			case WARRIOR: default:
				return true;
			case MAGE:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_MAGE);
			case ROGUE:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_ROGUE);
			case HUNTRESS:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_HUNTRESS);
			case DUELIST:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_DUELIST);
			case CLERIC:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_CLERIC);
			case SPELLSWORD:
			case PERFORMER:
			case SOLDIER:
			case FOLLOWER:
			case ASCETIC:
				return true;
		}
	}
	
	public String unlockMsg() {
		return shortDesc() + "\n\n" + Messages.get(HeroClass.class, name()+"_unlock");
	}

}
