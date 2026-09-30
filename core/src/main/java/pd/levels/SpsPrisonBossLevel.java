/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.actors.mobs.Mob;
import pd.actors.mobs.PrisonWander;
import pd.actors.mobs.SpsTengu;
import pd.actors.mobs.Tank;
import pd.tiles.custom.SpsLegacyLevelVisual;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

/** SPS-PD's fixed prison arena and its original three-way random boss selection. */
public class SpsPrisonBossLevel extends SpsFixedBossLevel {
	static final int TENGU = 0;
	static final int PRISON_WANDER = 1;
	static final int TANK = 2;
	private static final String BOSS_VARIANT = "boss_variant";

	private int bossVariant = -1;

	{
		color1 = 0x6a723d;
		color2 = 0x88924c;
	}

	@Override
	protected boolean build() {
		selectBoss();
		if (!super.build()) return false;
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_PRISON_LEGACY, width(), height(), map));
		return true;
	}

	@Override protected int[] legacyMap() { return SpsBossLayouts.PRISON; }

	@Override
	protected Mob createLegacyBoss() {
		selectBoss();
		switch (bossVariant) {
			case PRISON_WANDER: return new PrisonWander();
			case TANK: return new Tank();
			default: return new SpsTengu();
		}
	}

	@Override
	protected Class<? extends Mob> legacyBossClass() {
		selectBoss();
		switch (bossVariant) {
			case PRISON_WANDER: return PrisonWander.class;
			case TANK: return Tank.class;
			default: return SpsTengu.class;
		}
	}

	private void selectBoss() {
		if (bossVariant >= 0) return;
		if (Random.Int(3) == 1) bossVariant = TENGU;
		else if (Random.Int(2) == 1) bossVariant = PRISON_WANDER;
		else bossVariant = TANK;
	}

	@Override
	protected void storeBossSelection(Bundle bundle) {
		selectBoss();
		bundle.put(BOSS_VARIANT, bossVariant);
	}

	@Override
	protected void restoreBossSelection(Bundle bundle) {
		if (bundle.contains(BOSS_VARIANT)) {
			bossVariant = bundle.getInt(BOSS_VARIANT);
			if (bossVariant < TENGU || bossVariant > TANK) bossVariant = TENGU;
			return;
		}
		for (Mob mob : mobs) {
			if (mob instanceof PrisonWander) bossVariant = PRISON_WANDER;
			else if (mob instanceof Tank) bossVariant = TANK;
			else if (mob instanceof SpsTengu) bossVariant = TENGU;
			if (bossVariant >= 0) return;
		}
		// Saves created before variants existed always used SPS Tengu.
		bossVariant = TENGU;
	}

	int bossVariantForTesting() {
		selectBoss();
		return bossVariant;
	}

	@Override public String tilesTex() { return Assets.Environment.TILES_PRISON; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_PRISON; }
}
