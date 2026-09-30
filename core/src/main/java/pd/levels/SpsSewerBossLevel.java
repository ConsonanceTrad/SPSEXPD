/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.actors.mobs.Mob;
import pd.actors.mobs.PlagueDoctor;
import pd.actors.mobs.SewerHeart;
import pd.actors.mobs.SpsGoo;
import pd.items.Generator;
import pd.items.Heap;
import pd.tiles.custom.SpsLegacyLevelVisual;
import watabou.utils.Random;
import watabou.utils.Bundle;

/** SPS-PD's fixed sewer boss arena. Boss behavior is migrated separately. */
public class SpsSewerBossLevel extends SpsFixedBossLevel {
	static final int GOO = 0;
	static final int SEWER_HEART = 1;
	static final int PLAGUE_DOCTOR = 2;
	private static final String BOSS_VARIANT = "boss_variant";
	private int bossVariant = -1;
	{
		color1 = 0x6a723d;
		color2 = 0x88924c;
	}

	@Override protected int[] legacyMap() { return SpsBossLayouts.SEWERS; }
	@Override protected Mob createLegacyBoss() {
		selectBoss();
		switch (bossVariant) {
			case SEWER_HEART: return new SewerHeart();
			case PLAGUE_DOCTOR: return new PlagueDoctor();
			default: return new SpsGoo();
		}
	}
	@Override protected Class<? extends Mob> legacyBossClass() {
		selectBoss();
		switch (bossVariant) {
			case SEWER_HEART: return SewerHeart.class;
			case PLAGUE_DOCTOR: return PlagueDoctor.class;
			default: return SpsGoo.class;
		}
	}
	@Override protected boolean isLegacyBossActor(Mob mob) {
		selectBoss();
		if (bossVariant == SEWER_HEART) return mob instanceof SewerHeart;
		if (bossVariant == PLAGUE_DOCTOR) return mob instanceof PlagueDoctor;
		return mob instanceof SpsGoo || mob instanceof SpsGoo.PoisonGoo;
	}

	private void selectBoss() {
		if (bossVariant >= 0) return;
		if (Random.Int(3) == 1) bossVariant = GOO;
		else if (Random.Int(2) == 1) bossVariant = SEWER_HEART;
		else bossVariant = PLAGUE_DOCTOR;
	}

	@Override protected void storeBossSelection(Bundle bundle) {
		selectBoss();
		bundle.put(BOSS_VARIANT, bossVariant);
	}

	@Override protected void restoreBossSelection(Bundle bundle) {
		if (bundle.contains(BOSS_VARIANT)) {
			bossVariant = bundle.getInt(BOSS_VARIANT);
			if (bossVariant < GOO || bossVariant > PLAGUE_DOCTOR) bossVariant = GOO;
			return;
		}
		for (Mob mob : mobs) {
			if (mob instanceof SewerHeart) bossVariant = SEWER_HEART;
			else if (mob instanceof PlagueDoctor) bossVariant = PLAGUE_DOCTOR;
			else if (mob instanceof SpsGoo) bossVariant = GOO;
			if (bossVariant >= 0) return;
		}
		bossVariant = GOO;
	}

	int bossVariantForTesting() { selectBoss(); return bossVariant; }

	@Override
	protected boolean build() {
		if (!super.build()) return false;
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.10f) map[cell] = Terrain.HIGH_GRASS;
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.10f) map[cell] = Terrain.OLD_HIGH_GRASS;
		}
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_SEWERS_LEGACY, width(), height(), map));
		return true;
	}

	@Override
	protected void createItems() {
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.GROUND_A && heaps.get(cell) == null) {
				drop(Generator.random(), cell).type = Heap.Type.CHEST;
			}
		}
	}

	@Override public String tilesTex() { return Assets.Environment.TILES_SEWERS; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_SEWERS; }
}
