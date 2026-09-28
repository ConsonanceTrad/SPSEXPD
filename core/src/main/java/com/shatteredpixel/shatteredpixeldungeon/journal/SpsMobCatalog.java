/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.journal;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Assassin;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.BambooMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.BombBug;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.BrokenRobot;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.BrownBat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DemonFlower;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DemonGoo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DemonRabbit;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DragonRider;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DustElement;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ExBambooMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ExVagrant;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FireSuccubus;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FireRabbit;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FireElemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GoldCollector;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Greatmoss;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollShaman;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TrollWarrior;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.IceBug;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.LevelChecker;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.LiveMoss;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ManySkeleton;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Musketeer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.PatrolUAV;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RatBoss;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SandMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shielded;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shit;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SpsCityMobs;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SpsExitMobs;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SpsHallsMobs;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SpiderBot;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Sufferer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ThiefImp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TimeKeeper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Vagrant;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Zombie;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

/** The seven always-visible creature groups from SPS-PD 0.9.8's NewMobCatalog. */
public enum SpsMobCatalog {

	SEWER("Rat Albino BrownBat DustElement LiveMoss Swarm Crab Shit PatrolUAV Vagrant ExVagrant Goo SewerHeart PlagueDoctor"),
	PRISON("Thief Bandit Gnoll Guard Zombie GoldCollector BambooMob ExBambooMob Assassin TrollWarrior FireRabbit Tengu PrisonWander Tank"),
	CAVE("Bat Skeleton GnollShaman Brute Shielded SandMob TimeKeeper Spinner IceBug BombBug TimeKeeper BrokenRobot Hybrid DM300 SpiderQueen"),
	CITY("FireElemental Warlock Monk Senior SpiderBot Golem DragonRider Musketeer DwarfLich ManySkeleton LichDancer ElderAvatar King"),
	HALL("Succubus Eye DemonGoo DemonFlower Sufferer ThiefImp DemonRabbit Scorpio Acidic FireSuccubus Yog"),
	EX("GnollArcher MossySkeleton AlbinoPiranha GoldThief BlueWraith Orc GoldOrc Fiend Wraith Greatmoss Piranha Mimic TestMob"),
	ETC("BlueDragon BugDragon Bunny ButterflyPet Chocobo CocoCat Datura DogPet Fly GentleCrab GoldDragon GreenDragon Haro Kodora LeryFire LightDragon Monkey PigPet RedDragon RibbonRat Scorpion ShadowDragon Snake Spider Stone Velocirooster VioletDragon YearPet FoxHelper DwarfBoy FrogPet StarKid LitDemon Abi");

	private static final String ROOT = "com.shatteredpixel.shatteredpixeldungeon.actors.mobs.";
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
		for (SpsMobCatalog catalog : values()) if (catalog.mobs.contains(type)) return true;
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
				for (String packageName : new String[]{"pets.", ""}) {
					try {
						Class<?> type = Class.forName(ROOT + packageName + name);
						if (Mob.class.isAssignableFrom(type)) return (Class<? extends Mob>)type;
					} catch (ClassNotFoundException ignored) {
						// Continue through the finite legacy mob package list.
					}
				}
				throw new ExceptionInInitializerError("Missing SPS catalog mob: " + name);
		}
	}
}
