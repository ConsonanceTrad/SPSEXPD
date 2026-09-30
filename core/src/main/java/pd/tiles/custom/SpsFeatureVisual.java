/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package pd.tiles.custom;

import pd.Assets;
import pd.tiles.CustomTilemap;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;

/** Renders individual terrain cells directly from the original SPS-PD tile sheet. */
public class SpsFeatureVisual extends CustomTilemap {

	public static final int SIGN = 29;
	public static final int FLOWER_POT = 32;
	public static final int IRON_MAKER = 33;
	public static final int TENT = 38;
	public static final int DEW_BLESS = 39;

	private static final String FEATURE = "feature";

	private int feature;

	{
		texture = Assets.Environment.SPS_FEATURES;
	}

	public SpsFeatureVisual() {
	}

	public SpsFeatureVisual(int feature) {
		this.feature = feature;
	}

	public int feature() {
		return feature;
	}

	public void destroy() {
		if (vis != null) vis.killAndErase();
	}

	@Override
	public Tilemap create() {
		Tilemap result = super.create();
		result.map(new int[]{feature}, 1);
		return result;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(FEATURE, feature);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		feature = bundle.getInt(FEATURE);
	}
}
