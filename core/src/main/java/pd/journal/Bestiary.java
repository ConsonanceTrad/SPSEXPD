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

package pd.journal;

import pd.Badges;
import pd.actors.hero.abilities.cleric.PowerOfMany;
import pd.actors.hero.abilities.huntress.SpiritHawk;
import pd.actors.hero.abilities.rogue.ShadowClone;
import pd.actors.hero.abilities.rogue.SmokeBomb;
import pd.actors.mobs.Acidic;
import pd.actors.mobs.Albino;
import pd.actors.mobs.ArmoredBrute;
import pd.actors.mobs.ArmoredStatue;
import pd.actors.mobs.Bandit;
import pd.actors.mobs.Bat;
import pd.actors.mobs.Bee;
import pd.actors.mobs.Brute;
import pd.actors.mobs.CausticSlime;
import pd.actors.mobs.Crab;
import pd.actors.mobs.CrystalGuardian;
import pd.actors.mobs.CrystalMimic;
import pd.actors.mobs.CrystalSpire;
import pd.actors.mobs.CrystalWisp;
import pd.actors.mobs.DM100;
import pd.actors.mobs.DM200;
import pd.actors.mobs.DM201;
import pd.actors.mobs.DM300;
import pd.actors.mobs.DemonSpawner;
import pd.actors.mobs.DwarfKing;
import pd.actors.mobs.EbonyMimic;
import pd.actors.mobs.Elemental;
import pd.actors.mobs.Eye;
import pd.actors.mobs.FetidRat;
import pd.actors.mobs.Ghoul;
import pd.actors.mobs.Gnoll;
import pd.actors.mobs.GnollExile;
import pd.actors.mobs.GnollGeomancer;
import pd.actors.mobs.GnollGuard;
import pd.actors.mobs.GnollSapper;
import pd.actors.mobs.GnollTrickster;
import pd.actors.mobs.GoldenMimic;
import pd.actors.mobs.Golem;
import pd.actors.mobs.Goo;
import pd.actors.mobs.GreatCrab;
import pd.actors.mobs.Guard;
import pd.actors.mobs.HermitCrab;
import pd.actors.mobs.Mimic;
import pd.actors.mobs.Monk;
import pd.actors.mobs.Necromancer;
import pd.actors.mobs.PhantomPiranha;
import pd.actors.mobs.Piranha;
import pd.actors.mobs.Pylon;
import pd.actors.mobs.Rat;
import pd.actors.mobs.RipperDemon;
import pd.actors.mobs.RotHeart;
import pd.actors.mobs.RotLasher;
import pd.actors.mobs.Scorpio;
import pd.actors.mobs.Senior;
import pd.actors.mobs.Shaman;
import pd.actors.mobs.Skeleton;
import pd.actors.mobs.Slime;
import pd.actors.mobs.Snake;
import pd.actors.mobs.SpectralNecromancer;
import pd.actors.mobs.Spinner;
import pd.actors.mobs.Statue;
import pd.actors.mobs.Succubus;
import pd.actors.mobs.Swarm;
import pd.actors.mobs.Tengu;
import pd.actors.mobs.Thief;
import pd.actors.mobs.TormentedSpirit;
import pd.actors.mobs.Warlock;
import pd.actors.mobs.Wraith;
import pd.actors.mobs.YogDzewa;
import pd.actors.mobs.YogFist;
import pd.actors.mobs.npcs.Blacksmith;
import pd.actors.mobs.npcs.Ghost;
import pd.actors.mobs.npcs.Imp;
import pd.actors.mobs.npcs.MirrorImage;
import pd.actors.mobs.npcs.PrismaticImage;
import pd.actors.mobs.npcs.RatKing;
import pd.actors.mobs.npcs.Sheep;
import pd.actors.mobs.npcs.Shopkeeper;
import pd.actors.mobs.npcs.VaultLaser;
import pd.actors.mobs.npcs.VaultSentry;
import pd.actors.mobs.npcs.Wandmaker;
import pd.actors.mobs.quest.vault.VaultBossElemental;
import pd.actors.mobs.quest.vault.VaultDM100;
import pd.actors.mobs.quest.vault.VaultDM200;
import pd.actors.mobs.quest.vault.VaultElemental;
import pd.actors.mobs.quest.vault.VaultGhoul;
import pd.actors.mobs.quest.vault.VaultGolem;
import pd.actors.mobs.quest.vault.VaultShaman;
import pd.actors.mobs.quest.vault.VaultSkeleton;
import pd.items.artifacts.DriedRose;
import pd.items.quest.CorpseDust;
import pd.items.wands.WandOfLivingEarth;
import pd.items.wands.WandOfRegrowth;
import pd.items.wands.WandOfWarding;
import pd.levels.rooms.special.SentryRoom;
import pd.levels.traps.AlarmTrap;
import pd.levels.traps.BlazingTrap;
import pd.levels.traps.BurningTrap;
import pd.levels.traps.ChillingTrap;
import pd.levels.traps.ConfusionTrap;
import pd.levels.traps.CorrosionTrap;
import pd.levels.traps.CursingTrap;
import pd.levels.traps.DisarmingTrap;
import pd.levels.traps.DisintegrationTrap;
import pd.levels.traps.DistortionTrap;
import pd.levels.traps.ExplosiveTrap;
import pd.levels.traps.FlashingTrap;
import pd.levels.traps.FlockTrap;
import pd.levels.traps.FrostTrap;
import pd.levels.traps.GatewayTrap;
import pd.levels.traps.GeyserTrap;
import pd.levels.traps.GnollRockfallTrap;
import pd.levels.traps.GrimTrap;
import pd.levels.traps.GrippingTrap;
import pd.levels.traps.GuardianTrap;
import pd.levels.traps.OozeTrap;
import pd.levels.traps.PitfallTrap;
import pd.levels.traps.PoisonDartTrap;
import pd.levels.traps.RockfallTrap;
import pd.levels.traps.ShockingTrap;
import pd.levels.traps.StormTrap;
import pd.levels.traps.SummoningTrap;
import pd.levels.traps.TeleportationTrap;
import pd.levels.traps.TenguDartTrap;
import pd.levels.traps.ToxicTrap;
import pd.levels.traps.WarpingTrap;
import pd.levels.traps.WeakeningTrap;
import pd.levels.traps.WornDartTrap;
import pd.messages.Messages;
import pd.plants.BlandfruitBush;
import pd.plants.Blindweed;
import pd.plants.Earthroot;
import pd.plants.Fadeleaf;
import pd.plants.Firebloom;
import pd.plants.Icecap;
import pd.plants.Mageroyal;
import pd.plants.Rotberry;
import pd.plants.Sorrowmoss;
import pd.plants.Starflower;
import pd.plants.Stormvine;
import pd.plants.Sungrass;
import pd.plants.Swiftthistle;
import render.utils.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

//contains all the game's various entities, mostly enemies, NPCS, and allies, but also traps and plants
public enum Bestiary {

	REGIONAL,
	BOSSES,
	UNIVERSAL,
	RARE,
	QUEST,
	NEUTRAL,
	ALLY,
	TRAP,
	PLANT;

	//tracks whether an entity has been encountered
	private final LinkedHashMap<Class<?>, Boolean> seen = new LinkedHashMap<>();
	//tracks enemy kills, trap activations, plant tramples, or just sets to 1 for seen on allies
	private final LinkedHashMap<Class<?>, Integer> encounterCount = new LinkedHashMap<>();

	//should only be used when initializing
	private void addEntities(Class<?>... classes ){
		for (Class<?> cls : classes){
			seen.put(cls, false);
			encounterCount.put(cls, 0);
		}
	}

	public Collection<Class<?>> entities(){
		return seen.keySet();
	}

	public String title(){
		return Messages.get(this, name() + ".title");
	}

	public int totalEntities(){
		return seen.size();
	}

	public int totalSeen(){
		int seenTotal = 0;
		for (boolean entitySeen : seen.values()){
			if (entitySeen) seenTotal++;
		}
		return seenTotal;
	}

	static {

		REGIONAL.addEntities(Rat.class, Snake.class, Gnoll.class, Swarm.class, Crab.class, Slime.class,
				Skeleton.class, Thief.class, DM100.class, Guard.class, Necromancer.class,
				Bat.class, Brute.class, Shaman.RedShaman.class, Shaman.BlueShaman.class, Shaman.PurpleShaman.class, Spinner.class, DM200.class,
				Ghoul.class, Elemental.FireElemental.class, Elemental.FrostElemental.class, Elemental.ShockElemental.class, Warlock.class, Monk.class, Golem.class,
				RipperDemon.class, DemonSpawner.class, Succubus.class, Eye.class, Scorpio.class);

		BOSSES.addEntities(Goo.class,
				Tengu.class,
				Pylon.class, DM300.class,
				DwarfKing.class,
				YogDzewa.Larva.class, YogFist.BurningFist.class, YogFist.SoiledFist.class, YogFist.RottingFist.class, YogFist.RustedFist.class,YogFist.BrightFist.class, YogFist.DarkFist.class, YogDzewa.class);

		UNIVERSAL.addEntities(Wraith.class, Piranha.class, Mimic.class, GoldenMimic.class, EbonyMimic.class, Statue.class, GuardianTrap.Guardian.class, SentryRoom.Sentry.class);

		RARE.addEntities(Albino.class, GnollExile.class, HermitCrab.class, CausticSlime.class,
				Bandit.class, SpectralNecromancer.class,
				ArmoredBrute.class, DM201.class,
				Elemental.ChaosElemental.class, Senior.class,
				Acidic.class,
				TormentedSpirit.class, PhantomPiranha.class, CrystalMimic.class, ArmoredStatue.class);

		QUEST.addEntities(FetidRat.class, GnollTrickster.class, GreatCrab.class,
				Elemental.NewbornFireElemental.class, RotLasher.class, RotHeart.class,
				CrystalWisp.class, CrystalGuardian.class, CrystalSpire.class, GnollGuard.class, GnollSapper.class, GnollGeomancer.class,
				VaultSkeleton.class, VaultDM100.class, VaultShaman.class, VaultDM200.class, VaultSentry.class, VaultLaser.class, VaultBossElemental.class);

		NEUTRAL.addEntities(Ghost.class, RatKing.class, Shopkeeper.class, Wandmaker.class, Blacksmith.class, Imp.class, Sheep.class, Bee.class);

		ALLY.addEntities(MirrorImage.class, PrismaticImage.class,
				DriedRose.GhostHero.class,
				WandOfWarding.Ward.class, WandOfWarding.Ward.WardSentry.class, WandOfLivingEarth.EarthGuardian.class,
				ShadowClone.ShadowAlly.class, SmokeBomb.NinjaLog.class, SpiritHawk.HawkAlly.class, PowerOfMany.LightAlly.class);

		TRAP.addEntities(WornDartTrap.class, PoisonDartTrap.class, DisintegrationTrap.class, GatewayTrap.class,
				ChillingTrap.class, BurningTrap.class, ShockingTrap.class, AlarmTrap.class, GrippingTrap.class, TeleportationTrap.class, OozeTrap.class,
				FrostTrap.class, BlazingTrap.class, StormTrap.class, GuardianTrap.class, FlashingTrap.class, WarpingTrap.class,
				ConfusionTrap.class, ToxicTrap.class, CorrosionTrap.class,
				FlockTrap.class, SummoningTrap.class, WeakeningTrap.class, CursingTrap.class,
				GeyserTrap.class, ExplosiveTrap.class, RockfallTrap.class, PitfallTrap.class,
				DistortionTrap.class, DisarmingTrap.class, GrimTrap.class);

		PLANT.addEntities(Rotberry.class, Sungrass.class, Fadeleaf.class, Icecap.class,
				Firebloom.class, Sorrowmoss.class, Swiftthistle.class, Blindweed.class,
				Stormvine.class, Earthroot.class, Mageroyal.class, Starflower.class,
				BlandfruitBush.class,
				WandOfRegrowth.Dewcatcher.class, WandOfRegrowth.Seedpod.class, WandOfRegrowth.Lotus.class);

	}

	//some mobs and traps have different internal classes in some cases, so need to convert here
	private static final HashMap<Class<?>, Class<?>> classConversions = new HashMap<>();
	static {
		classConversions.put(CorpseDust.DustWraith.class,       Wraith.class);

		classConversions.put(Necromancer.NecroSkeleton.class,   Skeleton.class);

		classConversions.put(TenguDartTrap.class,               PoisonDartTrap.class);
		classConversions.put(GnollRockfallTrap.class,           RockfallTrap.class);

		classConversions.put(VaultGhoul.class,                  Ghoul.class);
		classConversions.put(VaultElemental.Fire.class,         Elemental.FireElemental.class);
		classConversions.put(VaultElemental.Frost.class,        Elemental.FrostElemental.class);
		classConversions.put(VaultElemental.Shock.class,        Elemental.ShockElemental.class);
		classConversions.put(VaultGolem.class,                  Golem.class);

		classConversions.put(DwarfKing.DKGhoul.class,           Ghoul.class);
		classConversions.put(DwarfKing.DKWarlock.class,         Warlock.class);
		classConversions.put(DwarfKing.DKMonk.class,            Monk.class);
		classConversions.put(DwarfKing.DKGolem.class,           Golem.class);

		classConversions.put(YogDzewa.YogRipper.class,          RipperDemon.class);
		classConversions.put(YogDzewa.YogEye.class,             Eye.class);
		classConversions.put(YogDzewa.YogScorpio.class,         Scorpio.class);
	}

	public static boolean isSeen(Class<?> cls){
		for (Bestiary cat : values()) {
			if (cat.seen.containsKey(cls)) {
				return cat.seen.get(cls);
			}
		}
		return false;
	}

	public static void setSeen(Class<?> cls){
		if (classConversions.containsKey(cls)){
			cls = classConversions.get(cls);
		}
		for (Bestiary cat : values()) {
			if (cat.seen.containsKey(cls) && !cat.seen.get(cls)) {
				cat.seen.put(cls, true);
				Journal.saveNeeded = true;
			}
		}
		Badges.validateCatalogBadges();
	}

	public static int encounterCount(Class<?> cls) {
		for (Bestiary cat : values()) {
			if (cat.encounterCount.containsKey(cls)) {
				return cat.encounterCount.get(cls);
			}
		}
		return 0;
	}

	//used primarily when bosses are killed and need to clean up their minions
	public static boolean skipCountingEncounters = false;

	public static void countEncounter(Class<?> cls){
		countEncounters(cls, 1);
	}

	public static void countEncounters(Class<?> cls, int encounters){
		if (skipCountingEncounters){
			return;
		}
		if (classConversions.containsKey(cls)){
			cls = classConversions.get(cls);
		}
		for (Bestiary cat : values()) {
			if (cat.encounterCount.containsKey(cls) && cat.encounterCount.get(cls) != Integer.MAX_VALUE){
				cat.encounterCount.put(cls, cat.encounterCount.get(cls)+encounters);
				if (cat.encounterCount.get(cls) < -1_000_000_000){ //to catch cases of overflow
					cat.encounterCount.put(cls, Integer.MAX_VALUE);
				}
				Journal.saveNeeded = true;
			}
		}
	}

	private static final String BESTIARY_CLASSES    = "bestiary_classes";
	private static final String BESTIARY_SEEN       = "bestiary_seen";
	private static final String BESTIARY_ENCOUNTERS = "bestiary_encounters";

	public static void store( Bundle bundle ){

		ArrayList<Class<?>> classes = new ArrayList<>();
		ArrayList<Boolean> seen = new ArrayList<>();
		ArrayList<Integer> encounters = new ArrayList<>();

		for (Bestiary cat : values()) {
			for (Class<?> entity : cat.entities()) {
				if (cat.seen.get(entity) || cat.encounterCount.get(entity) > 0){
					classes.add(entity);
					seen.add(cat.seen.get(entity));
					encounters.add(cat.encounterCount.get(entity));
				}
			}
		}

		Class<?>[] storeCls = new Class[classes.size()];
		boolean[] storeSeen = new boolean[seen.size()];
		int[] storeEncounters = new int[encounters.size()];

		for (int i = 0; i < storeCls.length; i++){
			storeCls[i] = classes.get(i);
			storeSeen[i] = seen.get(i);
			storeEncounters[i] = encounters.get(i);
		}

		bundle.put( BESTIARY_CLASSES, storeCls );
		bundle.put( BESTIARY_SEEN, storeSeen );
		bundle.put( BESTIARY_ENCOUNTERS, storeEncounters );

	}

	public static void restore( Bundle bundle ){

		if (bundle.contains(BESTIARY_CLASSES)
				&& bundle.contains(BESTIARY_SEEN)
				&& bundle.contains(BESTIARY_ENCOUNTERS)){
			Class<?>[] classes = bundle.getClassArray(BESTIARY_CLASSES);
			boolean[] seen = bundle.getBooleanArray(BESTIARY_SEEN);
			int[] encounters = bundle.getIntArray(BESTIARY_ENCOUNTERS);

			for (int i = 0; i < classes.length; i++){
				for (Bestiary cat : values()){
					if (cat.seen.containsKey(classes[i])){
						cat.seen.put(classes[i], seen[i]);
						cat.encounterCount.put(classes[i], encounters[i]);
					}
				}
			}
		}

	}

}
