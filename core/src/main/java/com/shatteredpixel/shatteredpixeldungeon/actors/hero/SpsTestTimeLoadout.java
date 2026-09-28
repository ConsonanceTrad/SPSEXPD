/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Elevator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Palantir;
import com.shatteredpixel.shatteredpixeldungeon.items.PocketBall;
import com.shatteredpixel.shatteredpixeldungeon.items.PowerHand;
import com.shatteredpixel.shatteredpixeldungeon.items.SaveYourLife;
import com.shatteredpixel.shatteredpixeldungeon.items.SkillBook;
import com.shatteredpixel.shatteredpixeldungeon.items.SoulCollect;
import com.shatteredpixel.shatteredpixeldungeon.items.TomeOfMastery;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ShoppingCart;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.AflyEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.EasterEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.GoldDragonEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomMonthEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Honey;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Hamburger;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.MoonCake;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.FourClover;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.BlueNornStone;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.GreenNornStone;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.OrangeNornStone;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.PurpleNornStone;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.YellowNornStone;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMending;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.AdventureJournal;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.ChallengeJournal;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTenacity;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.fusion.RingOfKnowledge;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.fusion.RingOfMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfDummy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.TestWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.plants.Dewcatcher;
import com.shatteredpixel.shatteredpixeldungeon.plants.Seedpod;

import java.util.Collections;

/** Restores the complete SPS-PD 0.9.8 TEST_TIME starting inventory. */
public final class SpsTestTimeLoadout {

	public static void apply(Hero hero) {
		if (hero == null || !Dungeon.isChallenged(Challenges.TEST_TIME)) return;

		AdventureJournal adventures = AdventureJournal.ensureFor(hero);
		for (int destination = 0; destination < AdventureJournal.DESTINATION_COUNT; destination++) {
			adventures.unlock(destination);
		}
		adventures.fillCharge();
		ChallengeJournal challenges = ChallengeJournal.ensureFor(hero);
		for (int challenge = 0; challenge < ChallengeJournal.CHALLENGE_COUNT; challenge++) {
			challenges.unlock(challenge);
		}

		collect(hero, new Elevator());
		collect(hero, new SkillBook());
		collect(hero, new ScrollHolder());
		//SPS: 种子包已取消（绒布袋 VelvetPouch 为其替代品，用户裁决 2026-09-28）
		collect(hero, new PotionBandolier());
		collect(hero, new ShoppingCart());
		collect(hero, new MagicalHolster());
		collect(hero, new Palantir());
		collect(hero, new SoulCollect());
		collect(hero, new PowerHand());
		collect(hero, new TomeOfMastery());
		collect(hero, identified(new TestWeapon()));
		collect(hero, new EasterEgg());
		collect(hero, new AflyEgg());
		collect(hero, new GoldDragonEgg());
		collect(hero, new PocketBall(10));

		collect(hero, identified(new ScrollOfIdentify().quantity(199)));
		collect(hero, identified(new ScrollOfMagicMapping().quantity(199)));
		collect(hero, new MoonCake().quantity(199));
		collect(hero, identified(new PotionOfMindVision().quantity(199)));
		collect(hero, new YellowNornStone().quantity(199));
		collect(hero, new BlueNornStone().quantity(199));
		collect(hero, new OrangeNornStone().quantity(199));
		collect(hero, new PurpleNornStone().quantity(199));
		collect(hero, new GreenNornStone().quantity(199));

		collect(hero, new Seedpod.Seed().quantity(10));
		collect(hero, new Dewcatcher.Seed().quantity(10));
		collect(hero, new ScrollOfDummy().quantity(10));
		collect(hero, new PotionOfMending().quantity(10));
		collect(hero, new ScrollOfPsionicBlast().quantity(10));
		collect(hero, new Hamburger().quantity(10));
		collect(hero, new RandomMonthEgg().quantity(10));
		collect(hero, new Honey().quantity(10));

		Ring[] rings = {
				new RingOfElements(), new RingOfAccuracy(), new RingOfMight(), new RingOfForce(),
				new RingOfFuror(), new RingOfEvasion(), new RingOfEnergy(), new RingOfMagic(),
				new RingOfHaste(), new RingOfSharpshooting(), new RingOfTenacity(), new RingOfKnowledge()
		};
		for (Ring ring : rings) {
			ring.upgrade(10);
			collect(hero, identified(ring));
		}

		collect(hero, new SaveYourLife());
		collect(hero, new FourClover());
		MasterThievesArmband armband = new MasterThievesArmband();
		armband.upgrade(5);
		collect(hero, armband);

		Dungeon.gold = 10000;
		hero.HTBoost = 10000 - hero.baseLevelHT();
		hero.updateHT(false);
		hero.HP = hero.HT;
		Dungeon.depth = 1;
		Dungeon.branch = 0;
	}

	private static Item identified(Item item) {
		return item.identify();
	}

	private static void collect(Hero hero, Item item) {
		if (item.collect(hero.belongings.backpack)) return;
		// TEST_TIME is a developer loadout; never silently discard one of its fixtures.
		hero.belongings.backpack.items.add(item);
		Collections.sort(hero.belongings.backpack.items, Item.itemComparator);
	}

	private SpsTestTimeLoadout() { }
}
