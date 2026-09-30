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

package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.items.trinkets.RatSkull;
import watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MobSpawner extends Actor {
	{
		actPriority = BUFF_PRIO; //as if it were a buff.
	}

	@Override
	protected boolean act() {
		if ((Dungeon.dewDraw || Dungeon.dewWater) && !Dungeon.level.cleared) {
			spend(Dungeon.level.respawnCooldown());
			return true;
		}

		if (Dungeon.level.mobCount() < Dungeon.level.mobLimit()) {

			if (Dungeon.level.spawnMob(12)){
				spend(Dungeon.level.respawnCooldown());
			} else {
				//try again in 1 turn
				spend(TICK);
			}

		} else {
			spend(Dungeon.level.respawnCooldown());
		}

		return true;
	}

	public void resetCooldown(){
		spend(-cooldown());
		spend(Dungeon.level.respawnCooldown());
	}

	public static ArrayList<Class<? extends Mob>> getMobRotation(int depth ){
		ArrayList<Class<? extends Mob>> mobs = standardMobRotation( depth );
		addRareMobs(depth, mobs);
		swapSpsMobAlts(mobs);
		Random.shuffle(mobs);
		return mobs;
	}

	//returns a rotation of standard mobs, unshuffled.
	static ArrayList<Class<? extends Mob>> standardMobRotation( int depth ){
		switch(depth){

			// Sewers
			case 1: default:
				//3x rat, 1x snake
				return new ArrayList<>(Arrays.asList(
						Rat.class, Rat.class, Rat.class,
						Snake.class));
			case 2:
				// Legacy weights: rat 1, brown bat 1, dust elemental 0.7, rat boss 0.02.
				return weightedRotation(
						new Class[]{Rat.class, BrownBat.class,
								DustElement.class, RatBoss.class},
						new int[]{50, 50, 35, 1});
			case 3:
				// Legacy weights: 1, 1, 1, 0.8, 0.6, 0.4, 0.7.
				return weightedRotation(
						new Class[]{Rat.class, BrownBat.class, Shit.class,
								DustElement.class, LiveMoss.class,
								Swarm.class, PatrolUAV.class},
						new int[]{10, 10, 10, 8, 6, 4, 7});
			case 4:
				return weightedRotation(
						new Class[]{Shit.class, DustElement.class,
								LiveMoss.class, Swarm.class, Crab.class,
								PatrolUAV.class, Vagrant.class},
						new int[]{1, 1, 1, 1, 1, 1, 1});
			case 5:
				return new ArrayList<>(Arrays.asList(Gnoll.class,
						Swarm.class,
						Crab.class, Crab.class,
						Slime.class, Slime.class));

			// Prison
			case 6:
				//3x skeleton, 1x thief, 1x swarm
				return new ArrayList<>(Arrays.asList(Skeleton.class, Skeleton.class, Skeleton.class,
						Thief.class,
						Swarm.class));
			case 7:
				return weightedRotation(
						new Class[]{Thief.class, Gnoll.class, GhostPhoto.class,
								PatrolUAV.class, Vagrant.class},
						new int[]{10, 10, 5, 2, 2});
			case 8:
				return weightedRotation(
						new Class[]{Thief.class, Gnoll.class, Guard.class,
								Assassin.class, TrollWarrior.class,
								GhostPhoto.class, FireRabbit.class,
								BambooMob.class, GoldCollector.class},
						new int[]{10, 10, 5, 4, 3, 10, 5, 10, 5});
			case 9:
				if (Dungeon.sporkAvailable) return weightedRotation(
						new Class[]{Assassin.class, TrollWarrior.class,
								BanditKing.class, GoldCollector.class,
								FireRabbit.class}, new int[]{10, 10, 1, 10, 10});
				return weightedRotation(
						new Class[]{Thief.class, Gnoll.class, Guard.class,
								Assassin.class, TrollWarrior.class,
								Zombie.class, FireRabbit.class,
								BambooMob.class, GoldCollector.class},
						new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1});
			case 10:
				return new ArrayList<>(Arrays.asList(Skeleton.class, Thief.class,
						DM100.class, DM100.class, Guard.class, Guard.class,
						Necromancer.class, Necromancer.class));

			// Caves
			case 11:
				//3x bat, 1x brute, 1x shaman
				return new ArrayList<>(Arrays.asList(
						Bat.class, Bat.class, Bat.class,
						Brute.class,
						Shaman.random()));
			case 12:
				return weightedRotation(
						new Class[]{Bat.class, Skeleton.class, GnollShaman.class,
								Spinner.class, SandMob.class, IceBug.class},
						new int[]{10, 9, 5, 3, 9, 10});
			case 13:
				return weightedRotation(
						new Class[]{Bat.class, Skeleton.class, Brute.class, GnollShaman.class,
								Spinner.class, BrokenRobot.class, SandMob.class,
								IceBug.class, TimeKeeper.class},
						new int[]{10, 10, 3, 7, 6, 7, 4, 10, 3});
			case 14:
				return weightedRotation(
						new Class[]{Bat.class, Skeleton.class, Brute.class, GnollShaman.class,
								Spinner.class, BrokenRobot.class, SandMob.class,
								IceBug.class, TimeKeeper.class},
						new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1});
			case 15:
				return new ArrayList<>(Arrays.asList(Bat.class, Brute.class,
						Shaman.random(), Shaman.random(), Spinner.class, Spinner.class,
						DM200.class, DM200.class));

			// City
			case 16:
				//3x ghoul, 1x elemental, 1x warlock
				return new ArrayList<>(Arrays.asList(
						Ghoul.class, Ghoul.class, Ghoul.class,
						Elemental.random(),
						Warlock.class));
			case 17:
				return weightedRotation(
						new Class[]{FireElemental.class, Monk.class, Golem.class,
								SpiderBot.class, Musketeer.class},
						new int[]{10, 8, 4, 4, 2});
			case 18:
				return weightedRotation(
						new Class[]{FireElemental.class, Warlock.class, Monk.class,
								DragonRider.class, Golem.class, SpiderBot.class,
								Musketeer.class, DwarfLich.class,
								ManySkeleton.class, LevelChecker.class},
						new int[]{10, 10, 10, 8, 8, 8, 6, 2, 1, 6});
			case 19:
				return weightedRotation(
						new Class[]{FireElemental.class, Warlock.class, Monk.class,
								DragonRider.class, Golem.class, SpiderBot.class,
								Musketeer.class, DwarfLich.class,
								ManySkeleton.class, LevelChecker.class},
						new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
			case 20:
				return new ArrayList<>(Arrays.asList(
						Elemental.random(), Warlock.class, Warlock.class,
						Monk.class, Monk.class, Golem.class, Golem.class, Golem.class));

			// Halls
			case 21:
				//2x succubus, 1x evil eye
				return new ArrayList<>(Arrays.asList(
						Succubus.class, Succubus.class,
						Eye.class));
			case 22:
				return weightedRotation(
						new Class[]{Succubus.class, Eye.class, DemonGoo.class,
								ThiefImp.class, DemonFlower.class, Sufferer.class},
						new int[]{2, 1, 2, 1, 1, 1});
			case 23:
				return weightedRotation(
						new Class[]{Succubus.class, Eye.class, DemonGoo.class,
								Scorpio.class, ThiefImp.class, DemonFlower.class,
								Sufferer.class, DemonRabbit.class},
						new int[]{2, 2, 2, 1, 1, 1, 1, 1});
			case 24:
				return weightedRotation(
						new Class[]{Succubus.class, Eye.class, DemonGoo.class,
								Scorpio.class, ThiefImp.class, DemonFlower.class,
								Sufferer.class, DemonRabbit.class},
						new int[]{1, 1, 1, 1, 1, 1, 1, 1});
			case 25: case 26:
				//1x succubus, 2x evil eye, 3x scorpio
				return new ArrayList<>(Arrays.asList(
						Succubus.class,
						Eye.class, Eye.class,
						Scorpio.class, Scorpio.class, Scorpio.class));
		}

	}

	private static ArrayList<Class<? extends Mob>> weightedRotation(
			Class<? extends Mob>[] classes, int[] weights) {
		ArrayList<Class<? extends Mob>> result = new ArrayList<>();
		for (int i = 0; i < classes.length; i++) {
			for (int count = 0; count < weights[i]; count++) result.add(classes[i]);
		}
		return result;
	}

	//has a chance to add a rarely spawned mobs to the rotation
	public static void addRareMobs( int depth, ArrayList<Class<?extends Mob>> rotation ){

		switch (depth){

			// Sewers
			default:
				return;
			case 4:
				if (Random.Float() < 0.025f) rotation.add(Thief.class);
				return;

			// Prison
			case 9:
				if (Random.Float() < 0.025f) rotation.add(Bat.class);
				return;

			// Caves
			case 14:
				if (Random.Float() < 0.025f) rotation.add(Ghoul.class);
				return;

			// City
			case 19:
				if (Random.Float() < 0.025f) rotation.add(Succubus.class);
				return;
		}
	}

	//switches out regular mobs for their alt versions when appropriate
	private static void swapMobAlts(ArrayList<Class<?extends Mob>> rotation) {
		float altChance = 1 / 50f * RatSkull.exoticChanceMultiplier();
		for (int i = 0; i < rotation.size(); i++) {
			if (Random.Float() < altChance) {
				Class<? extends Mob> cl = rotation.get(i);
				Class<? extends Mob> alt = RARE_ALTS.get(cl);
				if (alt != null) {
					rotation.set(i, alt);
				}
			}
		}
	}

	// SPS-PD applied these mutations at 1/8, independently of Shattered's rare-alt rate.
	static void swapSpsMobAlts(ArrayList<Class<? extends Mob>> rotation) {
		for (int i = 0; i < rotation.size(); i++) {
			Class<? extends Mob> alt = SPS_ALTS.get(rotation.get(i));
			if (alt != null && Random.Int(8) == 0) rotation.set(i, alt);
		}
	}

	private static final HashMap<Class<? extends Mob>, Class<? extends Mob>> SPS_ALTS = new HashMap<>();
	static {
		SPS_ALTS.put(Rat.class, Albino.class);
		SPS_ALTS.put(Vagrant.class, ExVagrant.class);
		SPS_ALTS.put(Thief.class, Bandit.class);
		SPS_ALTS.put(BambooMob.class, ExBambooMob.class);
		SPS_ALTS.put(Brute.class, Shielded.class);
		SPS_ALTS.put(IceBug.class, BombBug.class);
		SPS_ALTS.put(Monk.class, Senior.class);
		SPS_ALTS.put(Scorpio.class, Acidic.class);
		SPS_ALTS.put(Succubus.class, FireSuccubus.class);
	}

	public static final HashMap<Class<?extends Mob>, Class<?extends Mob>> RARE_ALTS = new HashMap<>();
	static {
		RARE_ALTS.put(Rat.class,            Albino.class);
		RARE_ALTS.put(Gnoll.class,          GnollExile.class);
		RARE_ALTS.put(Crab.class,           HermitCrab.class);
		RARE_ALTS.put(Slime.class,          CausticSlime.class);

		RARE_ALTS.put(Thief.class,          Bandit.class);
		RARE_ALTS.put(Necromancer.class,    SpectralNecromancer.class);

		RARE_ALTS.put(Brute.class,          ArmoredBrute.class);
		RARE_ALTS.put(DM200.class,          DM201.class);

		RARE_ALTS.put(Monk.class,           Senior.class);
		//swapping to chaos elemental actually happens in Elemental.random
		RARE_ALTS.put(Elemental.class,      Elemental.ChaosElemental.class);

		RARE_ALTS.put(Scorpio.class,        Acidic.class);
	}
}
