/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Challenge map concept adapted from Special Surprise Pixel Dungeon.
 * Distributed under the GNU General Public License v3 or later.
 */

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ChallengeGuardian;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.ChallengeJournal;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.LineBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CavesPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CityPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.HallsPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.PrisonPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.SewerPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.EntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.ExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ConfusionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.OozeTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap;

import java.util.ArrayList;

public class ChallengeLevel extends RegularLevel {

	private int goalCell;

	public ChallengeLevel() {
		int theme = theme();
		switch (theme) {
			case 0: color1 = 0x48763c; color2 = 0x59994a; break;
			case 1: color1 = 0x6a723d; color2 = 0x88924c; break;
			case 2: color1 = 0x534f3e; color2 = 0xb9d661; break;
			case 3: color1 = 0x4b6636; color2 = 0xf2f2f2; break;
			default: color1 = 0x801500; color2 = 0xa68521; break;
		}
	}

	private int challenge() {
		return Math.max(0, ChallengeJournal.challengeForBranch(Dungeon.branch));
	}

	private int theme() {
		int challenge = challenge();
		if (challenge <= 4) return challenge;
		if (challenge == 5) return 1;
		if (challenge == 6) return 3;
		return 4;
	}

	@Override
	protected boolean build() {
		feeling = Feeling.NONE;
		if (!super.build()) return false;

		int entrance = entrance();
		goalCell = exit();
		transitions.clear();
		transitions.add(new LevelTransition(this, entrance,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		map[entrance] = Terrain.ENTRANCE;
		map[goalCell] = Terrain.PEDESTAL;

		for (int i = 0; i < length(); i++) {
			Trap trap = traps.get(i);
			if (trap != null) {
				trap.visible = true;
				map[i] = Terrain.TRAP;
			}
		}
		return true;
	}

	@Override
	protected ArrayList<Room> initRooms() {
		ArrayList<Room> rooms = new ArrayList<>();
		rooms.add(roomEntrance = new EntranceRoom());
		for (int i = 0; i < 4; i++) rooms.add(new EmptyRoom());
		rooms.add(roomExit = new ExitRoom());
		return rooms;
	}

	@Override
	protected Builder builder() {
		return new LineBuilder()
				.setPathVariance(0.35f)
				.setPathLength(1f, new float[]{1})
				.setTunnelLength(new float[]{1, 2, 1}, new float[]{1});
	}

	@Override
	protected Painter painter() {
		int challenge = challenge();
		int traps = challenge == 5 ? 0 : challenge == 7 ? 6 : 2;
		switch (theme()) {
			case 0:
				return new SewerPainter().setWater(0.35f, 4).setGrass(0.12f, 3)
						.setTraps(traps, new Class[]{OozeTrap.class, WornDartTrap.class}, new float[]{1, 1});
			case 1:
				return new PrisonPainter().setWater(0.15f, 4).setGrass(0.08f, 3)
						.setTraps(traps, new Class[]{PoisonDartTrap.class, BurningTrap.class}, new float[]{1, 1});
			case 2:
				return new CavesPainter().setWater(0.18f, 4).setGrass(0.20f, 3)
						.setTraps(traps, new Class[]{GrippingTrap.class, ExplosiveTrap.class}, new float[]{1, 1});
			case 3:
				return new CityPainter().setWater(0.12f, 4).setGrass(0.08f, 3)
						.setTraps(traps, new Class[]{ShockingTrap.class, TeleportationTrap.class}, new float[]{1, 1});
			default:
				return new HallsPainter().setWater(0.20f, 4).setGrass(0.05f, 3)
						.setTraps(traps, new Class[]{ChillingTrap.class, ConfusionTrap.class, GatewayTrap.class}, new float[]{2, 1, 1});
		}
	}

	@Override
	protected void createMobs() {
		int challenge = challenge();
		ChallengeGuardian guardian = new ChallengeGuardian().configure(challenge, true);
		guardian.pos = goalCell;
		mobs.add(guardian);

		int echoes = challenge == 5 ? 4 : challenge == 6 ? 0 : challenge == 7 ? 1 : 2;
		for (int i = 0; i < echoes; i++) {
			ChallengeGuardian echo = new ChallengeGuardian().configure(challenge, false);
			int pos;
			int tries = 30;
			do {
				pos = randomRespawnCell(echo);
			} while (--tries > 0 && (pos < 0 || findMob(pos) != null || pos == goalCell));
			if (pos >= 0 && findMob(pos) == null && pos != goalCell) {
				echo.pos = pos;
				mobs.add(echo);
			}
		}
	}

	@Override
	protected void createItems() {
		// Rewards are dropped only by the guardian, so this branch cannot inflate random loot.
	}

	@Override
	public Mob createMob() {
		return new ChallengeGuardian().configure(challenge(), false);
	}

	@Override
	public Actor addRespawner() {
		return null;
	}

	@Override
	public String tilesTex() {
		switch (theme()) {
			case 0: return Assets.Environment.SPS_TILES_SEWERS_LEGACY;
			case 1: return Assets.Environment.SPS_TILES_PRISON_LEGACY;
			case 2: return Assets.Environment.SPS_TILES_CAVES_LEGACY;
			case 3: return Assets.Environment.SPS_TILES_CITY_LEGACY;
			default: return Assets.Environment.SPS_TILES_HALLS_LEGACY;
		}
	}

	@Override
	public String waterTex() {
		switch (theme()) {
			case 0: return Assets.Environment.SPS_WATER_SEWERS;
			case 1: return Assets.Environment.SPS_WATER_PRISON;
			case 2: return Assets.Environment.SPS_WATER_CAVES;
			case 3: return Assets.Environment.SPS_WATER_CITY;
			default: return Assets.Environment.SPS_WATER_HALLS;
		}
	}
}
