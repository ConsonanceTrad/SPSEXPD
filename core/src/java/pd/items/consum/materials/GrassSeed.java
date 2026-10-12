/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.materials;

import pd.Dungeon;
import pd.atlas.items.ConsumGoodsMaterialsMaterialsExtraDict;
import pd.items.Item;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.messages.InlineText;
import pd.scenes.GameScene;
import com.badlogic.gdx.Gdx;
import render.noosa.audio.Sample;
import pd.Assets;

/**
 * SPSEXPD: 踩踏高草的收获——草种。
 *
 * <p>掷出后在落点长出一株高草（掷出即消耗，不落地；落点不适合长草时按普通物品落下）。
 * 由草种种出的高草会记在 {@link Level#spsGrassSeedGrass} 上：它被踩开时仍会掉枯枝/鲜草等零碎，
 * 但**不会再掉草种**（免得自己繁殖自己）。</p>
 */
public class GrassSeed extends Item {
	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(GrassSeed.class)
			.t("name", "草种")
			.t("desc", "一小撮高草的种子。掷出去，它会在落点长成一株齐腰高的草丛，踩开照样能翻出枯枝、鲜草、磷火之类的零碎。\n\n草种自己长出的高草不会再结出草种。");
	}

	{
		image = ConsumGoodsMaterialsMaterialsExtraDict.GRASS_SEED;
		stackable = true;
		defaultAction = AC_THROW;
	}

	@Override
	protected void onThrow( int cell ) {
		Level level = Dungeon.level;
		if (level == null || !level.insideMap(cell) || !growable(level.map[cell])) {
			//SPSEXPD: 长不出草的地方就按普通物品落地，不浪费
			super.onThrow( cell );
			return;
		}

		//SPSEXPD: 掷出即落地生根——落点直接长出一株高草
		Level.set(cell, Terrain.HIGH_GRASS, level);
		level.spsGrassSeedGrass.add(cell);
		GameScene.updateMap(cell);

		if (level.heroFOV != null && level.heroFOV[cell] && Gdx.audio != null) {
			Sample.INSTANCE.play( Assets.Sounds.PLANT );
		}
	}

	/** SPSEXPD: 能长出高草的地形——与 {@code GroundItems.plant} 的判定同源。 */
	private static boolean growable( int terrain ) {
		return terrain == Terrain.EMPTY || terrain == Terrain.EMPTY_DECO || terrain == Terrain.GRASS
				|| terrain == Terrain.HIGH_GRASS || terrain == Terrain.FURROWED_GRASS
				|| terrain == Terrain.EMBERS;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 4 * quantity; }
}
