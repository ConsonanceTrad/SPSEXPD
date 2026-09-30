/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.hero;

import pd.Challenges;
import pd.Dungeon;
import pd.items.Elevator;
import pd.items.Item;
import pd.items.Palantir;
import pd.items.PocketBall;
import pd.items.PowerHand;
import pd.items.SaveYourLife;
import pd.items.SkillBook;
import pd.items.SoulCollect;
import pd.items.TomeOfMastery;
import pd.items.artifacts.MasterThievesArmband;
import pd.items.bags.MagicalHolster;
import pd.items.bags.PotionBandolier;
import pd.items.bags.ScrollHolder;
import pd.items.bags.ShoppingCart;
import pd.items.eggs.AflyEgg;
import pd.items.eggs.EasterEgg;
import pd.items.eggs.GoldDragonEgg;
import pd.items.eggs.randomone.RandomMonthEgg;
import pd.items.food.Honey;
import pd.items.food.completefood.Hamburger;
import pd.items.food.completefood.MoonCake;
import pd.items.misc.FourClover;
import pd.items.nornstone.BlueNornStone;
import pd.items.nornstone.GreenNornStone;
import pd.items.nornstone.OrangeNornStone;
import pd.items.nornstone.PurpleNornStone;
import pd.items.nornstone.YellowNornStone;
import pd.items.potions.PotionOfMending;
import pd.items.potions.PotionOfMindVision;
import pd.items.quest.AdventureJournal;
import pd.items.quest.ChallengeJournal;
import pd.items.rings.Ring;
import pd.items.rings.RingOfAccuracy;
import pd.items.rings.RingOfElements;
import pd.items.rings.RingOfEnergy;
import pd.items.rings.RingOfEvasion;
import pd.items.rings.RingOfForce;
import pd.items.rings.RingOfFuror;
import pd.items.rings.RingOfHaste;
import pd.items.rings.RingOfMight;
import pd.items.rings.RingOfSharpshooting;
import pd.items.rings.RingOfTenacity;
import pd.items.rings.fusion.RingOfKnowledge;
import pd.items.rings.fusion.RingOfMagic;
import pd.items.scrolls.ScrollOfDummy;
import pd.items.scrolls.ScrollOfIdentify;
import pd.items.scrolls.ScrollOfMagicMapping;
import pd.items.scrolls.ScrollOfPsionicBlast;
import pd.items.weapon.melee.special.TestWeapon;
import pd.plants.Dewcatcher;
import pd.plants.Seedpod;

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

		Dungeon.gold = 20000;
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
