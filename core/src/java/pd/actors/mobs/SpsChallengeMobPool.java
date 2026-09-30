/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import render.utils.math.Random;
import render.utils.serialize.Reflection;

public final class SpsChallengeMobPool {

	private SpsChallengeMobPool() { }

	public static Mob forest() {
		return weighted(new Class[]{GnollArcher.class, ForestProtector.class, Brute.class},
				new float[]{1f, 0.1f, 0.1f});
	}

	public static Mob prison() {
		return weighted(new Class[]{MossySkeleton.class, GraveProtector.class,
				ManySkeleton.class}, new float[]{1f, 0.1f, 0.1f});
	}

	public static Mob cave() {
		return weighted(new Class[]{AlbinoPiranha.class, FishProtector.class, Crab.class},
				new float[]{1f, 0.1f, 0.1f});
	}

	public static Mob city() {
		return weighted(new Class[]{GoldThief.class, VaultProtector.class, Succubus.class},
				new float[]{1f, 0.1f, 0.1f});
	}

	@SuppressWarnings("unchecked")
	private static Mob weighted(Class<? extends Mob>[] classes, float[] weights) {
		float total = 0;
		for (float weight : weights) total += weight;
		float roll = Random.Float(total);
		for (int i = 0; i < classes.length; i++) {
			roll -= weights[i];
			if (roll < 0) return Reflection.newInstance(classes[i]);
		}
		return Reflection.newInstance(classes[classes.length - 1]);
	}

	private static final Class<? extends Mob>[] ICE_SPAWNS = new Class[]{
			Rat.class, BrownBat.class, DustElement.class,
			LiveMoss.class, Swarm.class, Crab.class, PatrolUAV.class,
			Thief.class, Gnoll.class, BambooMob.class, Guard.class,
			Assassin.class, TrollWarrior.class,
			Zombie.class, Bat.class, Skeleton.class, Brute.class,
			TimeKeeper.class, GnollShaman.class, Spinner.class,
			BrokenRobot.class, SandMob.class, IceBug.class,
			FireElemental.class, Warlock.class, Monk.class, DragonRider.class,
			Golem.class, SpiderBot.class, Musketeer.class,
			DwarfLich.class, Succubus.class, Eye.class, DemonGoo.class,
			Scorpio.class, ThiefImp.class, DemonFlower.class,
			Sufferer.class, BlueWraith.class, GnollArcher.class,
			ForestProtector.class, MossySkeleton.class, GraveProtector.class,
			FishProtector.class, GoldThief.class, VaultProtector.class, Orc.class,
			FlyingProtector.class, MonsterBox.class, Fiend.class
	};

	public static Class<? extends Mob>[] iceBallSpawnTypes() {
		return ICE_SPAWNS.clone();
	}

	public static Mob randomIceBallSpawn() {
		return Reflection.newInstance(Random.element(ICE_SPAWNS));
	}
}
