package com.shatteredpixel.shatteredpixeldungeon.journal;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/** The seven always-visible item groups from SPS-PD 0.9.8's NewCatalog. */
public enum SpsCatalog {

	WEAPONS("ShortSword Handaxe Scimitar BattleAxe Gsword MageBook FightGloves Nunchakus Club WarHammer Dagger Dualknive Rapier AssassinsBlade Lance Knuckles Spear Whip Glaive Halberd Triangolo Flute Wardrum Trumpet Harp WoodenStaff Mace HolyWater PrayerWheel StoneCross TrickSand MirrorDoll WindBottle HandLight CurseBox AresSword CromCruachAxe JupitersWraith LokisFlail NeptunusTrident TekkoKagi WraithBreath Spork Goei Handcannon RunicBlade ErrorW ShadowEater Brick DragonBoat FireCracker HookHam KeyWeapon Lollipop Pumpkin SJRBMusic TestWeapon Tree ToyGun MiniMoai GoblinShield SpKnuckles TenguSword HolyMace Javelin Kunai SmallChakram HugeShuriken Tamahawk DewVial Weightstone"),
	ARMOR("BaseArmor VestArmor ClothArmor WoodenArmor RubberArmor LeatherArmor CeramicsArmor CDArmor DiscArmor StoneArmor StyrofoamArmor MailArmor MultiplelayerArmor ProtectiveclothingArmor ScaleArmor BulletArmor PhantomArmor PlateArmor MachineArmor LifeArmor ErrorArmor WarriorSkill MageSkill RogueSkill HuntressSkill PerformerSkill SoldierSkill FollowerSkill AsceticSkill TestArmor WarriorArmor MageArmor RogueArmor HuntressArmor PerformerArmor SoldierArmor FollowerArmor AsceticArmor GreatRune Torch"),
	WANDS("WandOfMagicMissile WandOfDisintegration WandOfLightning WandOfTCloud WandOfFirebolt WandOfMeteorite WandOfFreeze WandOfFlow WandOfAcid WandOfSwamp WandOfLight WandOfCharm WandOfFlock WandOfBlood WandOfError WandOfTest WoodenBowN StoneBowN MetalBowN AlloyBowN PVCBowN WoodenBowS StoneBowS MetalBowS AlloyBowS PVCBowS WoodenBowR StoneBowR MetalBowR AlloyBowR PVCBowR Sling GunA GunB GunC GunD GunE RangePan"),
	SPECIALS("MissileShield AttackShield DemoScroll SavageHelmet RewardPaper SeriousPunch PotionOfMage CannonOfMage GnollMark ElfBow DemonBlade LinkSword UndeadBook HorseTotem NeedPaper EleKatana Boomerang ManyKnive TaurcenBow RangeBag PPC ShootGun Shovel BShovel CopyBall DanceLion LeaderFlag PPC2 GunOfSoldier AttackShoes MKbox MechPocket HealBag NmHealBag FaithSign DiamondPickaxe DiceTower BigBattery HolyMace GrassBook Garbage CatSharkArmor WandOfShatteredFireblast WandOf13 WandOfBlackMeow TestCloak NouthSouth BottleFlower DevUpPlan CrossPhoto SheepFur Apk931 Simple360 ApostleBox Tissue HunterLens Mirror2 SellMushroom UncleDumbbell BrokenHammer SellPermit HummingTool FishCracker FunnyFood HaroEgg CrystalVial CursePhone XiXiBox AFlySock MoneyBook HoneyArrow BottleFire LynnDoll RainShield FishBone TempestBoomerang FishPetFood GhostGirlRose RustybladeCat DwarfHammer"),
	ARTIFACTS("AlchemistsToolkit CapeOfThorns ChaliceOfBlood CloakOfShadows DriedRose EtherealChains HornOfPlenty GlassTotem MasterThievesArmband SandalsOfNature TalismanOfForesight TimekeepersHourglass UnstableSpellbook AlienBag EyeOfSkadi RobotDMT Pylon TimeOclock FlyChains NoomlinCrown RingOfAccuracy RingOfEnergy RingOfElements RingOfEvasion RingOfForce RingOfFuror RingOfHaste RingOfMight RingOfSharpshooting RingOfTenacity RingOfMagic RingOfKnowledge"),
	FOODS("Honey Nut WaterItem OverpricedRation NormalRation Pasty BattleFlower DreamLeaf HealGrass NutVegetable Blackberry Blueberry Cloudberry Moonberry FullMoonberry Blandfruit Strawberry Durian Cherry Meat MysteryMeat FireMeat IceMeat EarthMeat ShockMeat LightMeat DarkMeat BugMeat AflyFood Chickennugget Chocolate Crystalnucleus Foamedbeverage FoodFans Frenchfries Fruitsalad Gel GoldenNut Hamburger Herbmeat HoneyGel Honeymeat Honeyrice HoneyWater Icecream Kebab Meatroll NutCake PerfectFood PetFood Porksoup Ricefood Vegetablekebab Vegetableroll Vegetablesoup ZongZi FruitCandy NutCookie MixPizza RiceGruel Sishimi Mediummeat"),
	PILLS("plants/BlandfruitBush$Seed plants/Blindweed$Seed plants/Dewcatcher$Seed plants/Dreamfoil$Seed plants/Earthroot$Seed plants/Fadeleaf$Seed plants/Firebloom$Seed plants/Icecap$Seed plants/NutPlant$Seed plants/ReNepenth$Seed plants/Rotberry$Seed plants/Seedpod$Seed plants/SiOtwoFlower$Seed plants/Sorrowmoss$Seed plants/StarEater$Seed plants/Starflower$Seed plants/Stormvine$Seed plants/Sungrass$Seed Powerpill Magicpill Shootpill Smashpill Musicpill Hardpill BlueMilk DeathCap Earthstar GoldenJelly GreenSpore JackOLantern PixieParasol RealgarWine Greaterpill Timepill Timepill2 BlindFruit CharmFruit FireFruit GlassFruit HealFruit IceFruit MagicHand NutFruit RocketMissile RootFruit ShockFruit SmokeFruit ToxicFruit");

	private static final String ROOT = "com.shatteredpixel.shatteredpixeldungeon.";
	private static final String[] ITEM_PACKAGES = {
			"items.weapon.melee.normalweapon.", "items.weapon.melee.fusion.",
			"items.weapon.melee.block.", "items.weapon.melee.relic.",
			"items.weapon.melee.special.", "items.weapon.melee.start.",
			"items.weapon.melee.", "items.weapon.missiles.arrows.", "items.weapon.missiles.fusion.",
			"items.weapon.missiles.meleethrow.", "items.weapon.missiles.throwing.",
			"items.weapon.missiles.", "items.weapon.ranges.", "items.weapon.guns.",
			"items.armor.normalarmor.", "items.armor.specialarmor.", "items.armor.fusion.", "items.armor.",
			"items.artifacts.fusion.", "items.artifacts.", "items.eggs.", "items.food.completefood.",
			"items.food.fruit.", "items.food.meatfood.", "items.food.staplefood.", "items.food.fusion.",
			"items.food.vegetable.", "items.food.", "items.medicine.", "items.misc.",
			"items.rings.fusion.", "items.rings.", "items.sellitem.", "items.skills.", "items.summon.",
			"items.wands.fusion.", "items.wands.", "items."
	};

	private final String classNames;
	private final LinkedHashSet<Class<? extends Item>> items = new LinkedHashSet<>();

	SpsCatalog(String classNames) {
		this.classNames = classNames;
	}

	static {
		for (SpsCatalog catalog : values()) {
			for (String name : catalog.classNames.split(" ")) {
				catalog.items.add(resolve(name));
			}
		}
	}

	public Collection<Class<? extends Item>> items() {
		return Collections.unmodifiableCollection(items);
	}

	public int totalItems() {
		return items.size();
	}

	public int totalSeen() {
		return items.size();
	}

	public String title() {
		return Messages.get(this, name() + ".title");
	}

	public static boolean contains(Class<?> type) {
		for (SpsCatalog catalog : values()) {
			if (catalog.items.contains(type)) return true;
		}
		return false;
	}

	public static final Set<SpsCatalog> EQUIPMENT = Collections.unmodifiableSet(
			new LinkedHashSet<>(Arrays.asList(WEAPONS, ARMOR, WANDS, SPECIALS, ARTIFACTS)));
	public static final Set<SpsCatalog> CONSUMABLES = Collections.unmodifiableSet(
			new LinkedHashSet<>(Arrays.asList(FOODS, PILLS)));

	@SuppressWarnings("unchecked")
	private static Class<? extends Item> resolve(String oldName) {
		if (oldName.startsWith("plants/")) {
			return checkedClass(ROOT + oldName.replace('/', '.'));
		}
		String currentName = oldName;
		if (oldName.equals("Wardrum")) currentName = "WarDrum";
		if (oldName.equals("Magicpill")) currentName = "MagicPill";
		if (oldName.equals("Timepill")) currentName = "TimePill";
		for (String itemPackage : ITEM_PACKAGES) {
			try {
				Class<?> type = Class.forName(ROOT + itemPackage + currentName);
				if (Item.class.isAssignableFrom(type)) return (Class<? extends Item>)type;
			} catch (ClassNotFoundException ignored) {
				// Continue through the finite legacy package list.
			}
		}
		throw new ExceptionInInitializerError("Missing SPS catalog item: " + oldName);
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends Item> checkedClass(String name) {
		try {
			Class<?> type = Class.forName(name);
			if (Item.class.isAssignableFrom(type)) return (Class<? extends Item>)type;
			throw new ExceptionInInitializerError(name + " is not an item");
		} catch (ClassNotFoundException error) {
			throw new ExceptionInInitializerError("Missing SPS catalog item: " + name);
		}
	}
}
