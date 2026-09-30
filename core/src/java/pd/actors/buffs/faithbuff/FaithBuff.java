/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.faithbuff;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Permanent marker and legacy SPS faction mapping shared by the five faiths. */
public abstract class FaithBuff extends Buff {
	private static final Set<String> NATURE_NAMES = names(
			"Abi", "Acidic", "Albino", "AlbinoPiranha", "AliveFish", "ARealMan", "Apostle",
			"ArmorStatue", "BambooMob", "Bat", "BlackMeow", "BombBug", "BrownBat", "Bunny",
			"ButterflyPet", "CatSheep", "Chocobo", "CocoCat", "Crab", "CrabKing", "Dachhack",
			"Datura", "DemonFlower", "DemonGoo", "DogPet", "DreamPlayer", "DustElement",
			"ExBambooMob", "FetidRat", "Fiend", "FireElemental", "FishProtector", "Fly",
			"FlyingProtector", "ForestProtector", "FrogPet", "FruitCat", "GentleCrab",
			"GiftCoconut", "GreatCrab", "Greatmoss", "GreyRat", "HateSokoban", "HBB",
			"HermitCrab", "HeXA", "Hmdzl001", "Hybrid", "IceBall", "IceBug", "Jinkeloid",
			"Lery", "LeryFire", "LiveMoss", "MagicEye", "Otiluke", "PigPet", "Piranha",
			"RainTrainer", "Rat", "RatBoss", "RatKing", "RibbonRat", "SaidbySun", "SandMob",
			"Scorpio", "Scorpion", "Sheep", "Snake", "Spider", "SpiderBot", "SpiderQueen",
			"Spinner", "Stone", "Swarm", "TestMob", "UGoo", "UKing", "Velocirooster",
			"XixiZero", "YearPet");
	private static final Set<String> MECHANICAL_NAMES = names(
			"Apostle", "Bilboldev", "BrokenRobot", "Coconut", "Coconut2", "DM300", "DwarfBoy",
			"DwarfLich", "ElderAvatar", "Evan", "GiftBaMech", "GiftBunnyKeeper", "GiftCoconut",
			"GiftFruitWorker", "GiftMeatSeller", "GoblinPlayer", "GoldCollector", "Golem", "Haro",
			"Hybrid", "IceBall", "Juh9870", "King", "Leadercn", "LevelChecker", "LitTower",
			"Locastan", "MineSentinel", "Monk", "Musketeer", "OrbOfZotMob", "PatrolUAV",
			"Senior", "Sentinel", "Shell", "SokobanSentinel", "StarKid", "TestMob2", "Thief",
			"UDM300", "Udawos", "Warlock");
	private static final Set<String> HOLY_NAMES = names(
			"AFly", "AshWolf", "Assassin", "ATV9", "Bandit", "BanditKing", "Blacksmith",
			"Blacksmith2", "BlueCat", "BlueGirl", "BoneStar", "Brute", "DemonRabbit", "ExVagrant",
			"FireRabbit", "FlyLing", "FoxHelper", "GiftAFly", "GiftAshWolf", "GiftBegger",
			"GiftFlyLing", "GiftRen", "GiftTorch", "Gnoll", "GnollArcher", "GnollKing",
			"GnollShaman", "GnollTrickster", "GoldOrc", "GoldThief", "GraveProtector", "Guard",
			"HoneyPoooot", "Ice13", "LaJi", "Lyn", "Lynn", "MemoryOfSand", "Millilitre",
			"Monkey", "NutPainter", "OldNewStwist", "Omicronrg9", "Orc", "OtilukeNPC",
			"PlagueDoctor", "PrisonWander", "Ravenwolf", "RENnpc", "Rustyblade", "SadSaltan",
			"Shielded", "Shit", "Shopkeeper", "Shower", "Sufferer", "Tempest102", "Tengu",
			"TenguDen", "ThiefKing", "Tinkerer1", "Tinkerer2", "Tinkerer4", "Tinkerer5",
			"TrollWarrior", "UIcecorps", "UIcecorps2", "UncleS", "UTengu", "Vagrant",
			"VaultProtector", "Velocirooster", "Wandmaker");
	private static final Set<String> DEMONIC_NAMES = names(
			"Acidic", "AdultDragonViolet", "AFly", "Albino", "BlueDragon", "BlueWraith",
			"BugDragon", "ConsideredHamster", "DemonFlower", "DemonGoo", "DemonRabbit",
			"DemonSummoner", "Dragonking", "DragonRider", "DwarfLich", "Eye", "Fiend",
			"FireSuccubus", "G2159687", "Ghost", "GhostPhoto", "GoldDragon", "GoldOrc",
			"Goo", "GreenDragon", "Hybrid", "Imp", "ImpShopkeeper", "Kodora", "Kostis12345",
			"LichDancer", "LightDragon", "LitDemon", "LotusSummoner", "Lyn", "MagicEye",
			"ManySkeleton", "Mimic", "MirrorImage", "MonsterBox", "MossySkeleton", "NewPlayer",
			"NYRDS", "RedDragon", "RedWraith", "SFB", "ShadowDragon", "ShadowYog",
			"SheepSokoban", "SheepSokobanBlack", "SheepSokobanCorner", "SheepSokobanStop",
			"SheepSokobanSwitch", "Skeleton", "SkeletonHand1", "SkeletonHand2", "SkeletonKing",
			"SommonSkeleton", "SP931", "StormAndRain", "Succubus", "Sufferer", "Tank",
			"ThankList", "ThiefImp", "TimeKeeper", "TypedScroll", "UAmulet", "UGoo", "UYog",
			"VioletDragon", "Virus", "Watabou", "WhiteGhost", "Wraith", "YearPet", "Yog",
			"Zot", "ZotPhase", "Zombie");

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public boolean act() {
		spend(TICK);
		return true;
	}

	public static boolean nature(Char ch) {
		return named(ch, NATURE_NAMES) || has(ch, Char.Property.BEAST, Char.Property.PLANT, Char.Property.FISHER,
				Char.Property.ELEMENT, Char.Property.FIERY, Char.Property.ICY,
				Char.Property.ACIDIC, Char.Property.ELECTRIC);
	}

	public static boolean mechanical(Char ch) {
		return named(ch, MECHANICAL_NAMES) || has(ch, Char.Property.MECH, Char.Property.ALIEN, Char.Property.DWARF,
				Char.Property.GOBLIN, Char.Property.INORGANIC);
	}

	public static boolean holy(Char ch) {
		return named(ch, HOLY_NAMES) || has(ch, Char.Property.HUMAN, Char.Property.ORC, Char.Property.ELF, Char.Property.TROLL);
	}

	public static boolean demonic(Char ch) {
		return named(ch, DEMONIC_NAMES) || has(ch, Char.Property.DRAGON, Char.Property.DEMONIC,
				Char.Property.UNKNOW, Char.Property.UNDEAD);
	}

	public static boolean powerful(Char ch) {
		return has(ch, Char.Property.BOSS, Char.Property.MINIBOSS);
	}

	public static float outgoingMultiplier(Hero hero, Char enemy) {
		if ((hero.buff(MechFaith.class) != null && nature(enemy))
				|| (hero.buff(LifeFaith.class) != null && mechanical(enemy))
				|| (hero.buff(DemonFaith.class) != null && holy(enemy))
				|| (hero.buff(HumanFaith.class) != null && demonic(enemy))
				|| (hero.buff(BalanceFaith.class) != null && powerful(enemy))) return 1.5f;
		return 1f;
	}

	public static float incomingMultiplier(Hero hero, Char enemy) {
		if ((hero.buff(LifeFaith.class) != null && nature(enemy))
				|| (hero.buff(MechFaith.class) != null && mechanical(enemy))
				|| (hero.buff(HumanFaith.class) != null && holy(enemy))
				|| (hero.buff(DemonFaith.class) != null && demonic(enemy))
				|| (hero.buff(BalanceFaith.class) != null && powerful(enemy))) return .75f;
		return 1f;
	}

	private static boolean has(Char ch, Char.Property... properties) {
		if (ch == null) return false;
		for (Char.Property property : properties) if (Char.hasProp(ch, property)) return true;
		return false;
	}

	//按类名匹配名单（上面的 *_NAMES 即怪物类名常量：改名需同步名单，否则判定静默失效）
	private static boolean named(Char ch, Set<String> names) {
		return ch != null && names.contains(ch.getClass().getSimpleName());
	}

	private static Set<String> names(String... names) {
		return new HashSet<>(Arrays.asList(names));
	}
}
