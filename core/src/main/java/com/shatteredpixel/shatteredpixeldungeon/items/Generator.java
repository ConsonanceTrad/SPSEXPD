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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClericArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.DuelistArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.HuntressArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MageArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.RogueArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.WarriorArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.BulletArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.CDArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.CeramicsArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.DiscArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.MachineArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.MailArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.MultiplelayerArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.PhantomArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.ProtectiveclothingArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.RubberArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.ScaleArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.StoneArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.StyrofoamArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.VestArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.WoodenArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlienBag;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CapeOfThorns;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.FlyChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.GlassTotem;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.RobotDMT;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SandalsOfNature;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimeOclock;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.fusion.EyeOfSkadi;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.BlueDragonEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.CocoCatEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.EasterEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.Egg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.GoldDragonEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.GreenDragonEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.LeryFireEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.LightDragonEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.RedDragonEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.ScorpionEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.ShadowDragonEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.VioletDragonEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomAtkEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomColEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomDefEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomEgg1;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomEgg10;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomEgg11;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomEgg12;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomEgg2;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomEgg3;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomEgg4;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomEgg5;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomEgg6;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomEgg7;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomEgg8;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomEgg9;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Chickennugget;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Chocolate;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.CompleteFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.FoodFans;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Frenchfries;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.FruitCandy;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Fruitsalad;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Gel;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Hamburger;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Herbmeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.HoneyGel;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.HoneyWater;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Honeymeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Honeyrice;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Icecream;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Kebab;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Meatroll;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.MixPizza;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.NutCookie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.PerfectFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Porksoup;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.RiceGruel;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Ricefood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Vegetablekebab;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Vegetableroll;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Vegetablesoup;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fusion.Nut;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Blackberry;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Blueberry;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Cherry;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Cloudberry;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Durian;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.FullMoonberry;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Fruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Moonberry;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Strawberry;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.BattleFlower;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.BrewLeft;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.DreamLeaf;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.HealGrass;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.NutVegetable;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.Truffles;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.Vegetable;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.staplefood.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.items.food.staplefood.NormalRation;
import com.shatteredpixel.shatteredpixeldungeon.items.food.staplefood.OverpricedRation;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.BlueMilk;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.DeathCap;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Earthstar;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Foamedbeverage;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.GoldenJelly;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Greaterpill;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.GreenSpore;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Hardpill;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.JackOLantern;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.LingPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.MagicPill;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.MendingTonic;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Musicpill;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Pill;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.PixieParasol;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Powerpill;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.RealgarWine;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Shootpill;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Smashpill;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.TimePill;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Timepill2;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.BlueNornStone;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.GreenNornStone;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.NornStone;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.OrangeNornStone;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.PurpleNornStone;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.YellowNornStone;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMending;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMixing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfOverHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfShield;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.SpsPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfArcana;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTenacity;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.sellitem.SellMushroom;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfDummy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlink;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfClairvoyance;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDeepSleep;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDetectMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFear;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFlock;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfShock;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ChaoticCenser;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.CrackedSpyglass;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.DimensionalSundial;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ExoticCrystals;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.EyeOfNewt;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.FerretTuft;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MimicTooth;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MossyClump;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ParchmentScrap;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.PetrifiedSeed;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.RatSkull;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.SaltCube;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ThirteenLeafClover;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrapMechanism;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.VialOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.WondrousResin;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfError;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTCloud;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfAcid;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCharm;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFreeze;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFirebolt;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFlock;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfSwamp;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMeteorite;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTransfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.fusion.WandOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.fusion.WandOfFlow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpsRangedWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunA;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunB;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunC;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunD;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunE;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.AlloyBowN;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.AlloyBowR;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.AlloyBowS;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.MetalBowN;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.MetalBowR;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.MetalBowS;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.PVCBowN;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.PVCBowR;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.PVCBowS;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.StoneBowN;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.StoneBowR;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.StoneBowS;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.WoodenBowN;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.WoodenBowR;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.WoodenBowS;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.AssassinsBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.BattleAxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.CurseBox;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Cudgel;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dirk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Flail;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gauntlet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Glaive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greataxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatshield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HandAxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HandLight;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HolyWater;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Katana;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Longsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MirrorDoll;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Quarterstaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Rapier;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scimitar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sickle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.StoneCross;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarScythe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Whip;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion.Harp;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion.Flute;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion.Nunchaku;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion.PrayerWheel;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion.ReedPipe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion.RitualBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion.Triangolo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion.Trumpet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion.VerdantGuard;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion.WarDrum;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion.WindBottle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.TrickSand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.WoodenStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.ToyGun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.FireCracker;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.HookHam;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.KeyWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.Lollipop;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.MeleePan;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.PaperFan;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.Pumpkin;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.SJRBMusic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.TestWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.meleethrow.Brick;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.meleethrow.DragonBoat;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.meleethrow.MiniMoai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.meleethrow.HugeShuriken;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.meleethrow.SmallChakram;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.meleethrow.Tamahawk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.meleethrow.Tree;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Bolas;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.FishingSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ForceCube;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Javelin;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Kunai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ShitBall;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingClub;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpike;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Tomahawk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Trident;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.fusion.RocketMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.fusion.TempestBoomerang;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.Arrows;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.BlindFruit;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.CharmFruit;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.FireFruit;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.GlassFruit;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.HealFruit;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.IceFruit;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.MagicHand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.NutFruit;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.RiceBall;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.RootFruit;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.ShockFruit;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.SmokeFruit;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.ToxicFruit;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing.EmpBola;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing.EscapeKnive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing.Skull;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing.Wave;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.fusion.RingOfKnowledge;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.fusion.RingOfMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush;
import com.shatteredpixel.shatteredpixeldungeon.plants.Dewcatcher;
import com.shatteredpixel.shatteredpixeldungeon.plants.Dreamfoil;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Freshberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Mageroyal;
import com.shatteredpixel.shatteredpixeldungeon.plants.NutPlant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.ReNepenth;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Seedpod;
import com.shatteredpixel.shatteredpixeldungeon.plants.SiOtwoFlower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.StarEater;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Generator {

	public enum Category {
		TRINKET ( 0, 0, Trinket.class),

		WEAPON	( 0, 0, Weapon.class),
		MELEEWEAPON( 2, 2, Weapon.class),
		OLDWEAPON( 0, 0, Weapon.class),
		WEP_T1	( 0, 0, MeleeWeapon.class),
		WEP_T2	( 0, 0, MeleeWeapon.class),
		WEP_T3	( 0, 0, MeleeWeapon.class),
		WEP_T4	( 0, 0, MeleeWeapon.class),
		WEP_T5	( 0, 0, MeleeWeapon.class),
		RANGED ( 1, 1, SpsRangedWeapon.class),
		
		ARMOR	( 2, 1, Armor.class ),
		
		MISSILE ( 1, 2, MissileWeapon.class ),
		MIS_T1  ( 0, 0, MissileWeapon.class ),
		MIS_T2  ( 0, 0, MissileWeapon.class ),
		MIS_T3  ( 0, 0, MissileWeapon.class ),
		MIS_T4  ( 0, 0, MissileWeapon.class ),
		MIS_T5  ( 0, 0, MissileWeapon.class ),
		
		WAND	( 1, 1, Wand.class ),
		RING	( 1, 0, Ring.class ),
		ARTIFACT( 0, 1, Artifact.class),
		
		FOOD	( 0, 0, Food.class ),
		HIGHFOOD( 0, 0, CompleteFood.class ),
		BERRY  ( 0, 0, Fruit.class ),
		SPS_BERRY( 0, 0, Fruit.class ),
		VEGETABLE( 0, 0, Vegetable.class ),
		MEDICINE( 1, 1, Pill.class ),
		MUSHROOM( 0, 0, Pill.class ),
		PILL    ( 0, 0, Pill.class ),
		SUMMONED( 0, 0, Item.class ),
		SHOES   ( 0, 0, Item.class ),
		
		POTION	( 8, 8, Potion.class ),
		SPS_POTION( 0, 0, SpsPotion.class ),
		SEED	( 1, 1, Plant.Seed.class ),
		SEED3	( 0, 0, Plant.Seed.class ),
		SEED4	( 0, 0, Plant.Seed.class ),
		SPS_SEED( 0, 0, Plant.Seed.class ),
		NORNSTONE( 0, 0, NornStone.class ),
		EGGS    ( 0, 0, Egg.class ),
		BASEPET ( 0, 0, Egg.class ),
		
		SCROLL	( 8, 8, Scroll.class ),
		STONE   ( 1, 1, Runestone.class),
		
		EASTERWEAPON( 0, 0, Item.class ),

		GOLD	( 10, 10, Gold.class ),

		// Appended for save compatibility with the existing Category ordinals.
		RANGEWEAPON( 0, 0, MissileWeapon.class ),
		ARROWS( 0, 0, Arrows.class ),
		MUSICWEAPON( 0, 0, Weapon.class ),
		GUNWEAPON( 0, 0, Weapon.class );
		
		public Class<?>[] classes;

		//some item types use a deck-based system, where the probs decrement as items are picked
		// until they are all 0, and then they reset. Those generator classes should define
		// defaultProbs. If defaultProbs is null then a deck system isn't used.
		//Artifacts in particular don't reset, no duplicates!
		public float[] probs;
		public float[] defaultProbs = null;

		//some items types have two decks and swap between them
		// this enforces more consistency while still allowing for better precision
		public float[] defaultProbs2 = null;
		public boolean using2ndProbs = false;
		//but in such cases we still need a reference to the full deck in case of non-deck generation
		public float[] defaultProbsTotal = null;

		//These variables are used as a part of the deck system, to ensure that drops are consistent
		// regardless of when they occur (either as part of seeded levelgen, or random item drops)
		public Long seed = null;
		public int dropped = 0;

		//game has two decks of 35 items for overall category probs
		//one deck has a ring and extra armor, the other has an artifact and extra thrown weapon
		//Note that pure random drops only happen as part of levelgen atm, so no seed is needed here
		public float firstProb;
		public float secondProb;
		public Class<? extends Item> superClass;
		
		private Category( float firstProb, float secondProb, Class<? extends Item> superClass ) {
			this.firstProb = firstProb;
			this.secondProb = secondProb;
			this.superClass = superClass;
		}

		//some generator categories can have ordering within that category as well
		// note that sub category ordering doesn't need to always include items that belong
		// to that categories superclass, e.g. bombs are ordered within thrown weapons
		private static HashMap<Class, ArrayList<Class>> subOrderings = new HashMap<>();
		static {
			subOrderings.put(Trinket.class, new ArrayList<>(Arrays.asList(Trinket.class, TrinketCatalyst.class)));
			subOrderings.put(MissileWeapon.class, new ArrayList<>(Arrays.asList(MissileWeapon.class, Bomb.class)));
			subOrderings.put(Potion.class, new ArrayList<>(Arrays.asList(Waterskin.class, Potion.class, ExoticPotion.class, Brew.class, Elixir.class, LiquidMetal.class)));
			subOrderings.put(Scroll.class, new ArrayList<>(Arrays.asList(Scroll.class, ExoticScroll.class, Spell.class, ArcaneResin.class)));
		}

		//in case there are multiple matches, this will return the latest match
		public static int order( Item item ) {
			int catResult = -1, subResult = 0;
			for (int i=0; i < values().length; i++) {
				// SUMMONED is a recipe-only generation pool whose entries do not share
				// a dedicated public base type. It must not classify every Item.
				if (values()[i] == SUMMONED || values()[i] == SHOES || values()[i] == EASTERWEAPON
						|| values()[i] == RANGEWEAPON || values()[i] == ARROWS
						|| values()[i] == MUSICWEAPON
						|| values()[i] == GUNWEAPON) continue;
				ArrayList<Class> subOrdering = subOrderings.get(values()[i].superClass);
				if (subOrdering != null){
					for (int j=0; j < subOrdering.size(); j++){
						if (subOrdering.get(j).isInstance(item)){
							catResult = i;
							subResult = j;
						}
					}
				} else {
					if (values()[i].superClass.isInstance(item)) {
						catResult = i;
						subResult = 0;
					}
				}
			}
			if (catResult != -1) return catResult*100 + subResult;

			//items without a category-defined order are sorted based on the spritesheet
			return Short.MAX_VALUE+item.image();
		}

		static {
			GOLD.classes = new Class<?>[]{
					Gold.class };
			GOLD.probs = new float[]{ 1 };
			
			POTION.classes = new Class<?>[]{
					PotionOfStrength.class, //2 drop every chapter, see Dungeon.posNeeded()
					PotionOfHealing.class,
					PotionOfMindVision.class,
					PotionOfFrost.class,
					PotionOfLiquidFlame.class,
					PotionOfToxicGas.class,
					PotionOfHaste.class,
					PotionOfInvisibility.class,
					PotionOfLevitation.class,
					PotionOfParalyticGas.class,
					PotionOfPurity.class,
					PotionOfExperience.class};
			POTION.defaultProbs  = new float[]{ 0, 3, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1 };
			POTION.defaultProbs2 = new float[]{ 0, 3, 2, 2, 1, 2, 1, 1, 1, 1, 1, 0 };
			POTION.probs = POTION.defaultProbs.clone();

			SPS_POTION.classes = new Class<?>[]{
					PotionOfMending.class, PotionOfMight.class, PotionOfMixing.class,
					PotionOfShield.class, PotionOfOverHealing.class };
			SPS_POTION.defaultProbs = new float[]{ 15, 4, 1, 5, 4 };
			SPS_POTION.probs = SPS_POTION.defaultProbs.clone();
			
			SEED.classes = new Class<?>[]{
					Firebloom.Seed.class, Icecap.Seed.class, Sorrowmoss.Seed.class,
					Blindweed.Seed.class, Sungrass.Seed.class, Earthroot.Seed.class,
					Fadeleaf.Seed.class, Rotberry.Seed.class, BlandfruitBush.Seed.class,
					Dreamfoil.Seed.class, Stormvine.Seed.class, NutPlant.Seed.class,
					Starflower.Seed.class, ReNepenth.Seed.class, StarEater.Seed.class,
					Dewcatcher.Seed.class, Seedpod.Seed.class, Freshberry.Seed.class,
					SiOtwoFlower.Seed.class};
			SEED.defaultProbs = new float[]{12, 12, 12, 12, 12, 12, 12, 0, 4,
					12, 12, 12, 3, 3, 4, 8, 2, 4, 3};
			SEED.probs = SEED.defaultProbs.clone();

			SEED3.classes = new Class<?>[]{
					Sungrass.Seed.class, Earthroot.Seed.class, BlandfruitBush.Seed.class,
					Dreamfoil.Seed.class, Starflower.Seed.class, Dewcatcher.Seed.class,
					Seedpod.Seed.class, SiOtwoFlower.Seed.class};
			SEED3.defaultProbs = new float[]{8, 4, 2, 4, 3, 1, 1, 1};
			SEED3.probs = SEED3.defaultProbs.clone();

			SEED4.classes = new Class<?>[]{
					Sungrass.Seed.class, StarEater.Seed.class, Dreamfoil.Seed.class,
					Starflower.Seed.class, ReNepenth.Seed.class, NutPlant.Seed.class,
					BlandfruitBush.Seed.class, Seedpod.Seed.class,
					Freshberry.Seed.class, SiOtwoFlower.Seed.class};
			SEED4.defaultProbs = new float[]{4, 1, 4, 2, 1, 3, 1, 1, 2, 1};
			SEED4.probs = SEED4.defaultProbs.clone();

			SPS_SEED.classes = new Class<?>[]{
					Firebloom.Seed.class, Icecap.Seed.class, Sorrowmoss.Seed.class,
					Blindweed.Seed.class, Sungrass.Seed.class, Earthroot.Seed.class,
					Fadeleaf.Seed.class, Rotberry.Seed.class, BlandfruitBush.Seed.class,
					Dreamfoil.Seed.class, Stormvine.Seed.class, NutPlant.Seed.class,
					Starflower.Seed.class, ReNepenth.Seed.class, StarEater.Seed.class,
					Dewcatcher.Seed.class, Seedpod.Seed.class, Freshberry.Seed.class,
					SiOtwoFlower.Seed.class};
			SPS_SEED.defaultProbs = new float[]{12, 12, 12, 12, 12, 12, 12, 0, 4,
					12, 12, 12, 3, 3, 4, 8, 2, 4, 3};
			SPS_SEED.probs = SPS_SEED.defaultProbs.clone();

			NORNSTONE.classes = new Class<?>[]{BlueNornStone.class, GreenNornStone.class,
					OrangeNornStone.class, PurpleNornStone.class, YellowNornStone.class};
			NORNSTONE.defaultProbs = new float[]{3, 3, 3, 3, 3};
			NORNSTONE.probs = NORNSTONE.defaultProbs.clone();

			// Original SPS-PD 0.9.8 pet-soul pools. These use independent weighted
			// rolls instead of Shattered's deck depletion.
			EGGS.classes = new Class<?>[]{
					BlueDragonEgg.class, CocoCatEgg.class, EasterEgg.class, Egg.class,
					LightDragonEgg.class, GreenDragonEgg.class, LeryFireEgg.class,
					RedDragonEgg.class, ScorpionEgg.class, ShadowDragonEgg.class,
					VioletDragonEgg.class, GoldDragonEgg.class, RandomEgg.class };
			EGGS.probs = new float[]{1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

			BASEPET.classes = new Class<?>[]{
					RandomAtkEgg.class, RandomDefEgg.class, RandomColEgg.class,
					com.shatteredpixel.shatteredpixeldungeon.items.eggs.RandomEasterEgg.class,
					RandomEgg1.class, RandomEgg2.class, RandomEgg3.class, RandomEgg4.class,
					RandomEgg5.class, RandomEgg6.class, RandomEgg7.class, RandomEgg8.class,
					RandomEgg9.class, RandomEgg10.class, RandomEgg11.class, RandomEgg12.class };
			BASEPET.probs = new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

			EASTERWEAPON.classes = new Class<?>[]{
					Pumpkin.class, Tree.class, MiniMoai.class, TestWeapon.class, ToyGun.class,
					HookHam.class, Brick.class, Lollipop.class, FireCracker.class, SJRBMusic.class,
					RocketMissile.class, KeyWeapon.class, DragonBoat.class, PaperFan.class, MeleePan.class};
			EASTERWEAPON.defaultProbs = new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
			EASTERWEAPON.probs = EASTERWEAPON.defaultProbs.clone();

			RANGEWEAPON.classes = new Class<?>[]{
					EmpBola.class, EscapeKnive.class, RocketMissile.class, Skull.class,
					Wave.class, ShitBall.class, MagicHand.class };
			RANGEWEAPON.probs = new float[]{1, 1, 1, 1, 1, 1, 1};

			ARROWS.classes = new Class<?>[]{
					BlindFruit.class, CharmFruit.class, CharmFruit.class,
					FireFruit.class, GlassFruit.class, HealFruit.class,
					IceFruit.class, MagicHand.class, NutFruit.class,
					RocketMissile.class, RootFruit.class, ShockFruit.class,
					SmokeFruit.class, ToxicFruit.class, RiceBall.class };
			ARROWS.probs = new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

			MUSICWEAPON.classes = new Class<?>[]{
					Triangolo.class, Flute.class, WarDrum.class, Trumpet.class, Harp.class};
			MUSICWEAPON.probs = new float[]{1, 1, 1, 1, 1};

			GUNWEAPON.classes = new Class<?>[]{GunA.class, GunB.class, GunC.class, GunD.class, GunE.class};
			GUNWEAPON.probs = new float[]{1, 1, 1, 1, 1};
			
			SCROLL.classes = new Class<?>[]{
					ScrollOfIdentify.class, ScrollOfTeleportation.class,
					ScrollOfRemoveCurse.class, ScrollOfUpgrade.class,
					ScrollOfRecharging.class, ScrollOfMagicMapping.class,
					ScrollOfRage.class, ScrollOfTerror.class,
					ScrollOfLullaby.class, ScrollOfMagicalInfusion.class,
					ScrollOfPsionicBlast.class, ScrollOfMirrorImage.class,
					ScrollOfRegrowth.class, ScrollOfDummy.class
			};
			SCROLL.defaultProbs = new float[]{30, 10, 15, 3, 10, 20, 10, 8, 8, 3, 3, 6, 6, 6};
			SCROLL.defaultProbs2 = null;
			SCROLL.probs = SCROLL.defaultProbs.clone();
			
			STONE.classes = new Class<?>[]{
					StoneOfEnchantment.class,   //1 is guaranteed to drop on floors 6-19
					StoneOfIntuition.class,     //1 additional stone is also dropped on floors 1-3
					StoneOfDetectMagic.class,
					StoneOfFlock.class,
					StoneOfShock.class,
					StoneOfBlink.class,
					StoneOfDeepSleep.class,
					StoneOfClairvoyance.class,
					StoneOfAggression.class,
					StoneOfBlast.class,
					StoneOfFear.class,
					StoneOfAugmentation.class  //1 is sold in each shop
			};
			STONE.defaultProbs = new float[]{ 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0 };
			STONE.probs = STONE.defaultProbs.clone();

			WAND.classes = new Class<?>[]{
					WandOfAcid.class,
					WandOfFreeze.class,
					WandOfFirebolt.class,
					WandOfLight.class,
					WandOfSwamp.class,
					WandOfBlood.class,
					WandOfLightning.class,
					WandOfCharm.class,
					WandOfFlow.class,
					WandOfFlock.class,
					WandOfMagicMissile.class,
					WandOfDisintegration.class,
					WandOfMeteorite.class,
					WandOfError.class,
					WandOfTCloud.class };
			WAND.defaultProbs = new float[]{ 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 5 };
			WAND.probs = WAND.defaultProbs.clone();
			
			// SPS-PD 0.9.8's complete 60-weapon pool. Shattered weapons remain compiled but hidden.
			WEAPON.classes = new Class<?>[]{
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Dagger.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Knuckles.class,
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.ShortSword.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.MageBook.class,
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Handaxe.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Spear.class,
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Dualknive.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.FightGloves.class,
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Nunchakus.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Scimitar.class,
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Whip.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Rapier.class,
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.AssassinsBlade.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.BattleAxe.class,
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Glaive.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Club.class,
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Gsword.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Halberd.class,
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.WarHammer.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Lance.class,
					Triangolo.class, Flute.class, WarDrum.class, Trumpet.class, Harp.class,
					WoodenStaff.class, Mace.class, HolyWater.class, PrayerWheel.class, StoneCross.class,
					TrickSand.class, MirrorDoll.class, WindBottle.class, HandLight.class, CurseBox.class,
					Kunai.class, SmallChakram.class, Javelin.class, HugeShuriken.class, Tamahawk.class,
					WoodenBowN.class, WoodenBowS.class, WoodenBowR.class, GunA.class,
					StoneBowN.class, StoneBowS.class, StoneBowR.class, GunB.class,
					MetalBowN.class, MetalBowS.class, MetalBowR.class, GunC.class,
					AlloyBowN.class, AlloyBowS.class, AlloyBowR.class, GunD.class,
					PVCBowN.class, PVCBowS.class, PVCBowR.class, GunE.class
			};
			WEAPON.probs = new float[]{
					1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
					1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
					1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
			};

			// Legacy randomWeapon() only draws from the first 40 entries, then picks
			// whichever of two candidates is closest to the requested strength.
			MELEEWEAPON.classes = Arrays.copyOf(WEAPON.classes, 40);
			MELEEWEAPON.probs = new float[]{
					1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
					1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
			};

			OLDWEAPON.classes = Arrays.copyOf(WEAPON.classes, 20);
			OLDWEAPON.probs = new float[]{
					1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
			};
			
			WEP_T1.classes = new Class<?>[]{
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Dagger.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Knuckles.class,
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.ShortSword.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.MageBook.class
			};
			WEP_T1.defaultProbs = new float[]{ 1, 1, 1, 1 };
			WEP_T1.probs = WEP_T1.defaultProbs.clone();
			
			WEP_T2.classes = new Class<?>[]{
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Handaxe.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Spear.class,
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Dualknive.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.FightGloves.class
			};
			WEP_T2.defaultProbs = new float[]{ 1, 1, 1, 1 };
			WEP_T2.probs = WEP_T2.defaultProbs.clone();
			
			WEP_T3.classes = new Class<?>[]{
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Nunchakus.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Scimitar.class,
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Whip.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Rapier.class
			};
			WEP_T3.defaultProbs = new float[]{ 1, 1, 1, 1 };
			WEP_T3.probs = WEP_T3.defaultProbs.clone();
			
			WEP_T4.classes = new Class<?>[]{
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.AssassinsBlade.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.BattleAxe.class,
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Glaive.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Club.class
			};
			WEP_T4.defaultProbs = new float[]{ 1, 1, 1, 1 };
			WEP_T4.probs = WEP_T4.defaultProbs.clone();
			
			WEP_T5.classes = new Class<?>[]{
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Gsword.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Halberd.class,
					com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.WarHammer.class, com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.Lance.class
			};
			WEP_T5.defaultProbs = new float[]{ 1, 1, 1, 1 };
			WEP_T5.probs = WEP_T5.defaultProbs.clone();

			RANGED.classes = new Class<?>[]{
					WoodenBowN.class, WoodenBowS.class, WoodenBowR.class, GunA.class,
					StoneBowN.class, StoneBowS.class, StoneBowR.class, GunB.class,
					MetalBowN.class, MetalBowS.class, MetalBowR.class, GunC.class,
					AlloyBowN.class, AlloyBowS.class, AlloyBowR.class, GunD.class,
					PVCBowN.class, PVCBowS.class, PVCBowR.class, GunE.class
			};
			RANGED.probs = new float[]{
					1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
					1, 1, 1, 1, 1, 1, 1, 1, 1, 1
			};
			
			//see Generator.randomArmor
			ARMOR.classes = new Class<?>[]{
					ClothArmor.class, WoodenArmor.class, VestArmor.class,
					LeatherArmor.class, CeramicsArmor.class, RubberArmor.class,
					DiscArmor.class, StoneArmor.class, CDArmor.class,
					MailArmor.class, MultiplelayerArmor.class, StyrofoamArmor.class,
					ScaleArmor.class, BulletArmor.class, ProtectiveclothingArmor.class,
					PlateArmor.class, MachineArmor.class, PhantomArmor.class
			};
			ARMOR.probs = new float[]{
					1, 1, 1, 1, 1, 1, 1, 1, 1,
					1, 1, 1, 1, 1, 1, 1, 1, 1
			};
			
			//see Generator.randomMissile
			MISSILE.classes = new Class<?>[]{};
			MISSILE.probs = new float[]{};
			
			MIS_T1.classes = new Class<?>[]{
					GlassFruit.class
			};
			MIS_T1.defaultProbs = new float[]{ 1 };
			MIS_T1.probs = MIS_T1.defaultProbs.clone();
			
			MIS_T2.classes = new Class<?>[]{
					GlassFruit.class
			};
			MIS_T2.defaultProbs = new float[]{ 1 };
			MIS_T2.probs = MIS_T2.defaultProbs.clone();
			
			MIS_T3.classes = new Class<?>[]{
					Kunai.class
			};
			MIS_T3.defaultProbs = new float[]{ 1 };
			MIS_T3.probs = MIS_T3.defaultProbs.clone();
			
			MIS_T4.classes = new Class<?>[]{
					Javelin.class,
					RocketMissile.class,
					TempestBoomerang.class
			};
			MIS_T4.defaultProbs = new float[]{ 3, 1, 1 };
			MIS_T4.probs = MIS_T4.defaultProbs.clone();
			
			MIS_T5.classes = new Class<?>[]{
					Javelin.class,
					RocketMissile.class,
					TempestBoomerang.class
			};
			MIS_T5.defaultProbs = new float[]{ 3, 1, 1 };
			MIS_T5.probs = MIS_T5.defaultProbs.clone();
			
			FOOD.classes = new Class<?>[]{
					NormalRation.class,
					Pasty.class,
					OverpricedRation.class };
			FOOD.defaultProbs = new float[]{ 8, 2, 5 };
			FOOD.probs = FOOD.defaultProbs.clone();

			HIGHFOOD.classes = new Class<?>[]{
					Chickennugget.class, Foamedbeverage.class, Fruitsalad.class,
					Hamburger.class, Herbmeat.class, Honeymeat.class, Honeyrice.class,
					Icecream.class, Kebab.class, PerfectFood.class, Porksoup.class,
					Ricefood.class, Vegetablekebab.class, Vegetablesoup.class,
					Meatroll.class, Vegetableroll.class, HoneyGel.class, Gel.class, HoneyWater.class,
					Chocolate.class, FoodFans.class, Frenchfries.class,
					FruitCandy.class, NutCookie.class, RiceGruel.class, MixPizza.class };
			HIGHFOOD.defaultProbs = new float[]{
					1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
					1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
			HIGHFOOD.probs = HIGHFOOD.defaultProbs.clone();

			BERRY.classes = new Class<?>[]{
					Blackberry.class, Blueberry.class, Cloudberry.class, Moonberry.class,
					FullMoonberry.class, Strawberry.class, Cherry.class, Durian.class };
			BERRY.defaultProbs = new float[]{ 6, 2, 2, 2, 1, 1, 1, 1 };
			BERRY.probs = BERRY.defaultProbs.clone();

			SPS_BERRY.classes = new Class<?>[]{Blackberry.class, Blueberry.class, Cloudberry.class, Moonberry.class};
			SPS_BERRY.defaultProbs = new float[]{6, 2, 2, 2};
			SPS_BERRY.probs = SPS_BERRY.defaultProbs.clone();

			VEGETABLE.classes = new Class<?>[]{
					NutVegetable.class, HealGrass.class, BattleFlower.class,
					DreamLeaf.class, Truffles.class, BrewLeft.class };
			VEGETABLE.defaultProbs = new float[]{ 4, 3, 2, 2, 1, 2 };
			VEGETABLE.probs = VEGETABLE.defaultProbs.clone();

			MEDICINE.classes = new Class<?>[]{
					BlueMilk.class, DeathCap.class, Earthstar.class, Foamedbeverage.class,
					GoldenJelly.class, Greaterpill.class, GreenSpore.class, Hardpill.class,
					JackOLantern.class, LingPotion.class, MagicPill.class, MendingTonic.class,
					Musicpill.class, PixieParasol.class, Powerpill.class, RealgarWine.class,
					Shootpill.class, Smashpill.class, TimePill.class, Timepill2.class };
			MEDICINE.defaultProbs = new float[]{
					1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1 };
			MEDICINE.probs = MEDICINE.defaultProbs.clone();

			MUSHROOM.classes = new Class<?>[]{BlueMilk.class, DeathCap.class, Earthstar.class,
					JackOLantern.class, PixieParasol.class, GoldenJelly.class, GreenSpore.class,
					SellMushroom.class};
			MUSHROOM.defaultProbs = new float[]{4, 3, 3, 3, 3, 3, 2, 1};
			MUSHROOM.probs = MUSHROOM.defaultProbs.clone();

			PILL.classes = new Class<?>[]{Hardpill.class, MagicPill.class, Musicpill.class,
					Powerpill.class, Shootpill.class, Smashpill.class};
			PILL.defaultProbs = new float[]{1, 1, 1, 1, 1, 1};
			PILL.probs = PILL.defaultProbs.clone();

			SUMMONED.classes = new Class<?>[]{
					com.shatteredpixel.shatteredpixeldungeon.items.summon.ActiveMrDestructo.class,
					com.shatteredpixel.shatteredpixeldungeon.items.summon.Mobile.class,
					com.shatteredpixel.shatteredpixeldungeon.items.summon.FairyCard.class,
					Honeypot.class
			};
			SUMMONED.defaultProbs = new float[]{1, 1, 1, 1};
			SUMMONED.probs = SUMMONED.defaultProbs.clone();

			SHOES.classes = new Class<?>[]{
					com.shatteredpixel.shatteredpixeldungeon.items.misc.JumpW.class,
					com.shatteredpixel.shatteredpixeldungeon.items.misc.JumpM.class,
					com.shatteredpixel.shatteredpixeldungeon.items.misc.JumpR.class,
					com.shatteredpixel.shatteredpixeldungeon.items.misc.JumpH.class,
					com.shatteredpixel.shatteredpixeldungeon.items.misc.JumpP.class,
					com.shatteredpixel.shatteredpixeldungeon.items.misc.JumpS.class,
					com.shatteredpixel.shatteredpixeldungeon.items.misc.JumpF.class,
					com.shatteredpixel.shatteredpixeldungeon.items.misc.JumpA.class
			};
			SHOES.defaultProbs = new float[]{1, 1, 1, 1, 1, 1, 1, 1};
			SHOES.probs = SHOES.defaultProbs.clone();
			
			RING.classes = new Class<?>[]{
					RingOfAccuracy.class,
					RingOfEvasion.class,
					RingOfElements.class,
					RingOfForce.class,
					RingOfFuror.class,
					RingOfHaste.class,
					RingOfMagic.class,
					RingOfMight.class,
					RingOfSharpshooting.class,
					RingOfTenacity.class,
					RingOfEnergy.class,
					RingOfKnowledge.class};
			RING.defaultProbs = new float[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
			RING.probs = RING.defaultProbs.clone();
			
			ARTIFACT.classes = new Class<?>[]{
					CapeOfThorns.class,
					ChaliceOfBlood.class,
					CloakOfShadows.class,
					HornOfPlenty.class,
					MasterThievesArmband.class,
					SandalsOfNature.class,
					TalismanOfForesight.class,
					TimekeepersHourglass.class,
					UnstableSpellbook.class,
					AlchemistsToolkit.class,
					RobotDMT.class,
					EyeOfSkadi.class,
					EtherealChains.class,
					DriedRose.class,
					GlassTotem.class,
					AlienBag.class,
					FlyChains.class,
					TimeOclock.class
			};
			ARTIFACT.defaultProbs = new float[]{1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0};
			ARTIFACT.probs = ARTIFACT.defaultProbs.clone();

			//Trinkets are unique like artifacts, but unlike them you can only have one at once
			//So we don't need the same enforcement of uniqueness
			TRINKET.classes = new Class<?>[]{
					RatSkull.class,
					ParchmentScrap.class,
					PetrifiedSeed.class,
					ExoticCrystals.class,
					MossyClump.class,
					DimensionalSundial.class,
					ThirteenLeafClover.class,
					TrapMechanism.class,
					MimicTooth.class,
					WondrousResin.class,
					EyeOfNewt.class,
					SaltCube.class,
					VialOfBlood.class,
					ShardOfOblivion.class,
					ChaoticCenser.class,
					FerretTuft.class,
					CrackedSpyglass.class
			};
			TRINKET.defaultProbs = new float[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
			TRINKET.probs = TRINKET.defaultProbs.clone();

			for (Category cat : Category.values()){
				if (cat.defaultProbs2 != null){
					cat.defaultProbsTotal = new float[cat.defaultProbs.length];
					for (int i = 0; i < cat.defaultProbs.length; i++){
						cat.defaultProbsTotal[i] = cat.defaultProbs[i] + cat.defaultProbs2[i];
					}
				} else if (cat.defaultProbs != null) {
					cat.defaultProbsTotal = cat.defaultProbs.clone();
				}
			}
		}
	}

	private static final float[][] floorSetTierProbs = new float[][] {
			{0, 75, 20,  4,  1},
			{0, 25, 50, 20,  5},
			{0,  0, 40, 50, 10},
			{0,  0, 20, 40, 40},
			{0,  0,  0, 20, 80}
	};

	private static boolean usingFirstDeck = false;
	private static HashMap<Category,Float> defaultCatProbs = new LinkedHashMap<>();
	private static HashMap<Category,Float> categoryProbs = new LinkedHashMap<>();

	public static void fullReset() {
		usingFirstDeck = Random.Int(2) == 0;
		generalReset();
		for (Category cat : Category.values()) {
			cat.using2ndProbs =  cat.defaultProbs2 != null && Random.Int(2) == 0;
			reset(cat);
			if (cat.defaultProbs != null) {
				cat.seed = Random.Long();
				cat.dropped = 0;
			}
		}
	}

	public static void generalReset(){
		for (Category cat : Category.values()) {
			categoryProbs.put( cat, usingFirstDeck ? cat.firstProb : cat.secondProb );
			defaultCatProbs.put( cat, cat.firstProb + cat.secondProb );
		}
	}

	public static void reset(Category cat){
		if (cat.defaultProbs != null) {
			if (cat.defaultProbs2 != null){
				cat.using2ndProbs = !cat.using2ndProbs;
				cat.probs = cat.using2ndProbs ? cat.defaultProbs2.clone() : cat.defaultProbs.clone();
			} else {
				cat.probs = cat.defaultProbs.clone();
			}
		}
	}

	//reverts changes to drop chances generates by this item
	//equivalent of shuffling the card back into the deck, does not preserve order!
	public static void undoDrop(Item item){
		undoDrop(item.getClass());
	}

	public static void undoDrop(Class cls){
		for (Category cat : Category.values()){
			if (cls.isAssignableFrom(cat.superClass)){
				if (cat.defaultProbs == null) continue;
				for (int i = 0; i < cat.classes.length; i++){
					if (cls == cat.classes[i]){
						cat.probs[i]++;
					}
				}
			}
		}
	}
	
	public static Item random() {
		Category cat = Random.chances( categoryProbs );
		if (cat == null){
			usingFirstDeck = !usingFirstDeck;
			generalReset();
			cat = Random.chances( categoryProbs );
		}
		categoryProbs.put( cat, categoryProbs.get( cat ) - 1);

		if (cat == Category.SEED) {
			//We specifically use defaults for seeds here because, unlike other item categories
			// their predominant source of drops is grass, not levelgen. This way the majority
			// of seed drops still use a deck, but the few that are spawned by levelgen are consistent
			return randomUsingDefaults(cat);
		} else {
			return random(cat);
		}
	}

	public static Item randomUsingDefaults(){
		return randomUsingDefaults(Random.chances( defaultCatProbs ));
	}
	
	public static Item random( Category cat ) {
		switch (cat) {
			case ARMOR:
				return randomArmor();
			case MELEEWEAPON:
				return randomWeapon();
			case MISSILE:
				return randomMissile();
			case ARTIFACT:
				Item item = randomArtifact();
				//if we're out of artifacts, return a ring instead.
				//do not use decks for that ring, as the # of artifacts genned can vary by gameplay
				return item != null ? item : randomUsingDefaults(Category.RING);
			default:
				if (cat.defaultProbs != null && cat.seed != null){
					Random.pushGenerator(cat.seed);
					for (int i = 0; i < cat.dropped; i++) Random.Long();
				}

				int i = Random.chances(cat.probs);
				if (i == -1) {
					reset(cat);
					i = Random.chances(cat.probs);
				}
				if (cat.defaultProbs != null) cat.probs[i]--;
				Class<?> itemCls = cat.classes[i];

				if (cat.defaultProbs != null && cat.seed != null){
					Random.popGenerator();
					cat.dropped++;
				}

				if (ExoticPotion.regToExo.containsKey(itemCls)){
					if (Random.Float() < ExoticCrystals.consumableExoticChance()){
						itemCls = ExoticPotion.regToExo.get(itemCls);
					}
				} else if (ExoticScroll.regToExo.containsKey(itemCls)){
					if (Random.Float() < ExoticCrystals.consumableExoticChance()){
						itemCls = ExoticScroll.regToExo.get(itemCls);
					}
				}

				return ((Item) Reflection.newInstance(itemCls)).random();
		}
	}

	//overrides any deck systems and always uses default probs
	// except for artifacts, which must always use a deck
	public static Item randomUsingDefaults( Category cat ){
		if (cat == Category.MISSILE){
			return randomMissile(true);
		} else if (cat.defaultProbs == null || cat == Category.ARTIFACT) {
			return random(cat);
		} else if (cat.defaultProbsTotal != null){
			return ((Item) Reflection.newInstance(cat.classes[Random.chances(cat.defaultProbsTotal)])).random();
		} else {
			Class<?> itemCls = cat.classes[Random.chances(cat.defaultProbs)];

			if (ExoticPotion.regToExo.containsKey(itemCls)){
				if (Random.Float() < ExoticCrystals.consumableExoticChance()){
					itemCls = ExoticPotion.regToExo.get(itemCls);
				}
			} else if (ExoticScroll.regToExo.containsKey(itemCls)){
				if (Random.Float() < ExoticCrystals.consumableExoticChance()){
					itemCls = ExoticScroll.regToExo.get(itemCls);
				}
			}

			return ((Item) Reflection.newInstance(itemCls)).random();
		}
	}
	
	public static Item random( Class<? extends Item> cl ) {
		return Reflection.newInstance(cl).random();
	}

	public static Armor randomArmor(){
		return randomArmorForStrength(10 + Dungeon.LimitedDrops.STRENGTH_POTIONS.count);
	}
	
	public static Armor randomArmor(int floorSet) {
		return randomArmorForStrength(10 + 2 * (int)GameMath.gate(0, floorSet, 5));
	}

	public static Armor randomArmorForStrength(int targetStr) {
		Armor first = (Armor)Reflection.newInstance(Category.ARMOR.classes[Random.chances(Category.ARMOR.probs)]);
		Armor second = (Armor)Reflection.newInstance(Category.ARMOR.classes[Random.chances(Category.ARMOR.probs)]);
		first.random();
		second.random();
		return Math.abs(targetStr - first.STRReq(0)) < Math.abs(targetStr - second.STRReq(0)) ? first : second;
	}

	public static final Category[] wepTiers = new Category[]{
			Category.WEP_T1,
			Category.WEP_T2,
			Category.WEP_T3,
			Category.WEP_T4,
			Category.WEP_T5
	};

	public static Weapon randomWeapon(){
		return randomWeaponForStrength(Hero.STARTING_STR + Dungeon.LimitedDrops.STRENGTH_POTIONS.count);
	}

	public static Weapon randomWeapon(int floorSet) {
		return randomWeapon(floorSet, false);
	}

	public static Weapon randomWeapon(boolean useDefaults) {
		return randomWeapon();
	}
	
	public static Weapon randomWeapon(int floorSet, boolean useDefaults) {
		return randomWeaponForStrength(10 + 2 * (int)GameMath.gate(0, floorSet, 5));
	}

	public static Weapon randomWeaponForStrength(int targetStr) {
		Weapon first = (Weapon)Reflection.newInstance(Category.MELEEWEAPON.classes[Random.chances(Category.MELEEWEAPON.probs)]);
		Weapon second = (Weapon)Reflection.newInstance(Category.MELEEWEAPON.classes[Random.chances(Category.MELEEWEAPON.probs)]);
		first.random();
		second.random();
		return Math.abs(targetStr - first.STRReq(0)) < Math.abs(targetStr - second.STRReq(0)) ? first : second;
	}
	
	public static final Category[] misTiers = new Category[]{
			Category.MIS_T1,
			Category.MIS_T2,
			Category.MIS_T3,
			Category.MIS_T4,
			Category.MIS_T5
	};
	
	public static MissileWeapon randomMissile(){
		return randomMissile(Dungeon.depth / 5);
	}

	public static MissileWeapon randomMissile(int floorSet) {
		return randomMissile(floorSet, false);
	}

	public static MissileWeapon randomMissile(boolean useDefaults) {
		return randomMissile(Dungeon.depth / 5, useDefaults);
	}

	public static MissileWeapon randomMissile(int floorSet, boolean useDefaults) {
		
		floorSet = (int)GameMath.gate(0, floorSet, floorSetTierProbs.length-1);

		MissileWeapon w;
		if (useDefaults){
			w = (MissileWeapon)randomUsingDefaults(misTiers[Random.chances(floorSetTierProbs[floorSet])]);
		} else {
			w = (MissileWeapon)random(misTiers[Random.chances(floorSetTierProbs[floorSet])]);
		}
		return w;
	}

	//enforces uniqueness of artifacts throughout a run.
	public static Artifact randomArtifact() {

		Category cat = Category.ARTIFACT;

		if (cat.defaultProbs != null && cat.seed != null){
			Random.pushGenerator(cat.seed);
			for (int i = 0; i < cat.dropped; i++) Random.Long();
		}

		int i = Random.chances( cat.probs );

		if (cat.defaultProbs != null && cat.seed != null){
			Random.popGenerator();
			cat.dropped++;
		}

		//if no artifacts are left, return null
		if (i == -1){
			return null;
		}

		cat.probs[i]--;
		return (Artifact) Reflection.newInstance((Class<? extends Artifact>) cat.classes[i]).random();

	}

	public static boolean removeArtifact(Class<?extends Artifact> artifact) {
		Category cat = Category.ARTIFACT;
		for (int i = 0; i < cat.classes.length; i++){
			if (cat.classes[i].equals(artifact) && cat.probs[i] > 0) {
				cat.probs[i] = 0;
				return true;
			}
		}
		return false;
	}

	private static final String FIRST_DECK = "first_deck";
	private static final String GENERAL_PROBS = "general_probs";
	private static final String CATEGORY_PROBS = "_probs";
	private static final String CATEGORY_USING_PROBS2 = "_using_probs2";
	private static final String CATEGORY_SEED = "_seed";
	private static final String CATEGORY_DROPPED = "_dropped";

	public static void storeInBundle(Bundle bundle) {
		bundle.put(FIRST_DECK, usingFirstDeck);

		Float[] genProbs = categoryProbs.values().toArray(new Float[0]);
		float[] storeProbs = new float[genProbs.length];
		for (int i = 0; i < storeProbs.length; i++){
			storeProbs[i] = genProbs[i];
		}
		bundle.put( GENERAL_PROBS, storeProbs);

		for (Category cat : Category.values()){
			if (cat.defaultProbs == null) continue;

			bundle.put(cat.name().toLowerCase() + CATEGORY_PROBS, cat.probs);

			if (cat.defaultProbs2 != null){
				bundle.put(cat.name().toLowerCase() + CATEGORY_USING_PROBS2, cat.using2ndProbs);
			}

			if (cat.seed != null) {
				bundle.put(cat.name().toLowerCase() + CATEGORY_SEED, cat.seed);
				bundle.put(cat.name().toLowerCase() + CATEGORY_DROPPED, cat.dropped);
			}
		}
	}

	public static void restoreFromBundle(Bundle bundle) {
		fullReset();

		usingFirstDeck = bundle.getBoolean(FIRST_DECK);

		if (bundle.contains(GENERAL_PROBS)){
			float[] probs = bundle.getFloatArray(GENERAL_PROBS);
			if (probs.length == Category.values().length) {
				for (int i = 0; i < probs.length; i++) {
					categoryProbs.put(Category.values()[i], probs[i]);
				}
			}
		}

		for (Category cat : Category.values()){
			if (bundle.contains(cat.name().toLowerCase() + CATEGORY_PROBS)){
				float[] probs = bundle.getFloatArray(cat.name().toLowerCase() + CATEGORY_PROBS);
				if (cat.defaultProbs != null && probs.length == cat.defaultProbs.length){
					cat.probs = probs;
				}
				if (bundle.contains(cat.name().toLowerCase() + CATEGORY_USING_PROBS2)){
					cat.using2ndProbs = bundle.getBoolean(cat.name().toLowerCase() + CATEGORY_USING_PROBS2);
				} else {
					cat.using2ndProbs = false;
				}
				if (bundle.contains(cat.name().toLowerCase() + CATEGORY_SEED)){
					cat.seed = bundle.getLong(cat.name().toLowerCase() + CATEGORY_SEED);
					cat.dropped = bundle.getInt(cat.name().toLowerCase() + CATEGORY_DROPPED);
				}

				//pre-v3.3.0 conversion for artifacts (addition of tome and key)
				if (cat == Category.ARTIFACT && probs.length != cat.defaultProbs.length){
					int keyIDX = 9;
					int j = 0;
					for (int i = 0; i < probs.length; i++){
						if (j == keyIDX){
							cat.probs[j] = 1;
							j++;
						}
						cat.probs[j] = probs[i];
						j++;
					}
				}

			}
		}
		
	}
}
