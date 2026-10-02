/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.journal;

import pd.actors.mobs.Assassin;
import pd.actors.mobs.BambooMob;
import pd.actors.mobs.BombBug;
import pd.actors.mobs.BrokenRobot;
import pd.actors.mobs.BrownBat;
import pd.actors.mobs.DemonFlower;
import pd.actors.mobs.DemonGoo;
import pd.actors.mobs.DemonRabbit;
import pd.actors.mobs.DragonRider;
import pd.actors.mobs.DustElement;
import pd.actors.mobs.Elemental;
import pd.actors.mobs.ExBambooMob;
import pd.actors.mobs.ExVagrant;
import pd.actors.mobs.FireElemental;
import pd.actors.mobs.FireRabbit;
import pd.actors.mobs.FireSuccubus;
import pd.actors.mobs.GnollShaman;
import pd.actors.mobs.GoldCollector;
import pd.actors.mobs.Greatmoss;
import pd.actors.mobs.IceBug;
import pd.actors.mobs.LevelChecker;
import pd.actors.mobs.LiveMoss;
import pd.actors.mobs.ManySkeleton;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Musketeer;
import pd.actors.mobs.PatrolUAV;
import pd.actors.mobs.RatBoss;
import pd.actors.mobs.SandMob;
import pd.actors.mobs.Shielded;
import pd.actors.mobs.Shit;
import pd.actors.mobs.SpiderBot;
import pd.actors.mobs.SpsCityMobs;
import pd.actors.mobs.SpsExitMobs;
import pd.actors.mobs.SpsHallsMobs;
import pd.actors.mobs.Sufferer;
import pd.actors.mobs.ThiefImp;
import pd.actors.mobs.TimeKeeper;
import pd.actors.mobs.TrollWarrior;
import pd.actors.mobs.Vagrant;
import pd.actors.mobs.YogDzewa;
import pd.actors.mobs.Zombie;
import pd.messages.Messages;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import pd.messages.InlineText;

/** The seven always-visible creature groups from SPS-PD 0.9.8's NewMobCatalog. */
public enum SpsMobCatalog {

	SEWER("Rat Albino BrownBat DustElement LiveMoss Swarm Crab Shit PatrolUAV Vagrant ExVagrant Goo SewerHeart PlagueDoctor"),
	PRISON("Thief Bandit Gnoll Guard Zombie GoldCollector BambooMob ExBambooMob Assassin TrollWarrior FireRabbit Tengu PrisonWander Tank"),
	CAVE("Bat Skeleton GnollShaman Brute Shielded SandMob TimeKeeper Spinner IceBug BombBug TimeKeeper BrokenRobot Hybrid DM300 SpiderQueen"),
	CITY("FireElemental Warlock Monk Senior SpiderBot Golem DragonRider Musketeer DwarfLich ManySkeleton LichDancer ElderAvatar King"),
	HALL("Succubus Eye DemonGoo DemonFlower Sufferer ThiefImp DemonRabbit Scorpio Acidic FireSuccubus Yog"),
	EX("GnollArcher MossySkeleton AlbinoPiranha GoldThief BlueWraith Orc GoldOrc Fiend Wraith Greatmoss Piranha Mimic TestMob"),
	ETC("BlueDragon BugDragon Bunny ButterflyPet Chocobo CocoCat Datura DogPet Fly GentleCrab GoldDragon GreenDragon Haro Kodora LeryFire LightDragon Monkey PigPet RedDragon RibbonRat Scorpion ShadowDragon Snake Spider Stone Velocirooster VioletDragon YearPet FoxHelper DwarfBoy FrogPet StarKid LitDemon Abi");
	//SPSEXPD: inline Chinese text (generated from messages/journal/zh)
	static {
		InlineText.of(SpsMobCatalog.class)
			.t("sewer.title", "下水道")
			.t("prison.title", "监狱")
			.t("cave.title", "洞穴")
			.t("city.title", "矮人都市")
			.t("hall.title", "恶魔大厅")
			.t("ex.title", "额外敌人")
			.t("etc.title", "伙伴");
	}




	private final LinkedHashSet<Class<? extends Mob>> mobs = new LinkedHashSet<>();

	SpsMobCatalog(String classNames) {
		for (String name : classNames.split(" ")) mobs.add(resolve(name));
	}

	public Collection<Class<? extends Mob>> mobs() {
		return Collections.unmodifiableCollection(mobs);
	}

	public int totalMobs() {
		return mobs.size();
	}

	public int totalSeen() {
		return mobs.size();
	}

	public String title() {
		return Messages.get(this, name() + ".title");
	}

	public static boolean contains(Class<?> type) {
		for (SpsMobCatalog catalog : values()) if (catalog.mobs().contains(type)) return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends Mob> resolve(String name) {
		switch (name) {
			case "BrownBat": return BrownBat.class;
			case "DustElement": return DustElement.class;
			case "LiveMoss": return LiveMoss.class;
			case "Shit": return Shit.class;
			case "PatrolUAV": return PatrolUAV.class;
			case "Vagrant": return Vagrant.class;
			case "ExVagrant": return ExVagrant.class;
			case "RatBoss": return RatBoss.class;
			case "Zombie": return Zombie.class;
			case "GoldCollector": return GoldCollector.class;
			case "BambooMob": return BambooMob.class;
			case "ExBambooMob": return ExBambooMob.class;
			case "Assassin": return Assassin.class;
			case "TrollWarrior": return TrollWarrior.class;
			case "FireRabbit": return FireRabbit.class;
			case "GnollShaman": return GnollShaman.class;
			case "SandMob": return SandMob.class;
			case "TimeKeeper": return TimeKeeper.class;
			case "IceBug": return IceBug.class;
			case "BombBug": return BombBug.class;
			case "Shielded": return Shielded.class;
			case "BrokenRobot": return BrokenRobot.class;
			case "FireElemental": return FireElemental.class;
			case "SpiderBot": return SpiderBot.class;
			case "DragonRider": return DragonRider.class;
			case "Musketeer": return Musketeer.class;
			case "ManySkeleton": return ManySkeleton.class;
			case "Greatmoss": return Greatmoss.class;
			case "DemonGoo": return DemonGoo.class;
			case "DemonFlower": return DemonFlower.class;
			case "Sufferer": return Sufferer.class;
			case "ThiefImp": return ThiefImp.class;
			case "DemonRabbit": return DemonRabbit.class;
			case "FireSuccubus": return FireSuccubus.class;
			case "Yog": return YogDzewa.class;
			default:
				// 目录即语义：以 Mob 所在包为锚点，只写相对子包，不出现任何根包名。
				// 必须在方法内内联计算——枚举常量的构造早于静态字段初始化。
				String mobsPackage = Mob.class.getPackage().getName() + ".";
				for (String subPackage : new String[]{"pets.", ""}) {
					try {
						Class<?> type = Class.forName(mobsPackage + subPackage + name);
						if (Mob.class.isAssignableFrom(type)) return (Class<? extends Mob>)type;
					} catch (ClassNotFoundException ignored) {
						// Continue through the finite legacy mob package list.
					}
				}
				throw new ExceptionInInitializerError("Missing SPS catalog mob: " + name);
		}
	}
}
