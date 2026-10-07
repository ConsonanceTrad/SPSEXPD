/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.hero;

import pd.Challenges;
import pd.Dungeon;
import pd.items.Elevator;
import pd.items.Item;
import pd.items.Palantir;
import pd.items.PowerHand;
import pd.items.SaveYourLife;
import pd.items.SkillBook;
import pd.items.SoulCollect;
import pd.items.TomeOfMastery;
import pd.items.equipment.artifacts.MasterThievesArmband;
import pd.items.equipment.bags.BambooBasket;
import pd.items.equipment.bags.MagicalHolster;
import pd.items.equipment.bags.BambooBasket;
import pd.items.equipment.bags.PotionBandolier;
import pd.items.equipment.bags.BambooBasket;
import pd.items.equipment.bags.ScrollHolder;
import pd.items.equipment.bags.BambooBasket;
import pd.items.equipment.bags.ShoppingCart;
import pd.items.consum.eggs.AflyEgg;
import pd.items.consum.eggs.EasterEgg;
import pd.items.consum.eggs.GoldDragonEgg;
import pd.items.consum.eggs.randomone.RandomMonthEgg;
import pd.items.consum.food.Honey;
import pd.items.consum.food.completefood.Hamburger;
import pd.items.consum.food.completefood.MoonCake;
import pd.items.misc.FourClover;
import pd.items.nornstone.BlueNornStone;
import pd.items.nornstone.GreenNornStone;
import pd.items.nornstone.OrangeNornStone;
import pd.items.nornstone.PurpleNornStone;
import pd.items.nornstone.YellowNornStone;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.consum.potions.PotionOfMindVision;
import pd.items.quest.AdventureJournal;
import pd.items.quest.ChallengeJournal;
import pd.items.equipment.rings.Ring;
import pd.items.equipment.rings.RingOfAccuracy;
import pd.items.equipment.rings.RingOfElements;
import pd.items.equipment.rings.RingOfEnergy;
import pd.items.equipment.rings.RingOfEvasion;
import pd.items.equipment.rings.RingOfForce;
import pd.items.equipment.rings.RingOfFuror;
import pd.items.equipment.rings.RingOfHaste;
import pd.items.equipment.rings.RingOfMight;
import pd.items.equipment.rings.RingOfSharpshooting;
import pd.items.equipment.rings.RingOfTenacity;
import pd.items.equipment.rings.fusion.RingOfKnowledge;
import pd.items.equipment.rings.fusion.RingOfMagic;
import pd.items.consum.scrolls.ScrollOfDummy;
import pd.items.consum.scrolls.ScrollOfIdentify;
import pd.items.consum.scrolls.ScrollOfMagicMapping;
import pd.items.consum.scrolls.ScrollOfPsionicBlast;
import pd.items.consum.potions.elixirs.WishPotion;
import pd.items.equipment.weapon.melee.special.TestWeapon;
import pd.items.consum.food.*;
import pd.plants.*;

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
		//SPSEXPD: 竹背篓——专门存放投掷果实与大型果实
		collect(hero, new BambooBasket());
		collect(hero, new Palantir());
		collect(hero, new SoulCollect());
		collect(hero, new PowerHand());
		collect(hero, new TomeOfMastery());
		collect(hero, identified(new TestWeapon()));
		collect(hero, new EasterEgg());
		collect(hero, new AflyEgg());
		collect(hero, new GoldDragonEgg());


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
		collect(hero, new PotionOfHealing().quantity(10));
		collect(hero, new ScrollOfPsionicBlast().quantity(10));
		collect(hero, new Hamburger().quantity(10));
		collect(hero, new RandomMonthEgg().quantity(10));
		collect(hero, new Honey().quantity(10));

		//SPSEXPD: 本次新增/改造的种子产物，供测试三分支与炼药（20 种种子、19 种蔬菜、19 种二次加工产物）
		//集露草之种与种子荚之种已在上方按 10 粒发放，此处不重复
		Plant.Seed[] testSeeds = {
				new Firebloom.Seed(), new Icecap.Seed(), new Sorrowmoss.Seed(), new Blindweed.Seed(),
				new Sungrass.Seed(), new Earthroot.Seed(), new Fadeleaf.Seed(), new Rotberry.Seed(),
				new BlandfruitBush.Seed(), new Dreamfoil.Seed(), new Stormvine.Seed(), new NutPlant.Seed(),
				new Starflower.Seed(), new ReNepenth.Seed(), new StarEater.Seed(), new Freshberry.Seed(),
				new SiOtwoFlower.Seed(), new Swiftthistle.Seed()
		};
		for (Plant.Seed seed : testSeeds) collect(hero, seed.quantity(5));

		//SPSEXPD: 不再发放大小果实/蔬菜/二次产物（用户裁决 2026-11），改为 20 瓶许愿魔药便于测试许愿
		collect(hero, new WishPotion().quantity(20));

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
		//SPSEXPD: 初始血量恢复正常（不再把 TEST_TIME 的最大生命拉到 10000，也不覆盖开局生命）
		//SPS: 不再把开局挪到 1 层。Dungeon.init() 已把 depth 设为 0（0 层 = 学者+商店安全层），
		//这里若覆盖成 1 会把出生点推后一层；测试时间挑战只负责发装备，不改开局位置。
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
