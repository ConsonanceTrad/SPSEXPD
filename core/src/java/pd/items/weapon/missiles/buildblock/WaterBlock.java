package pd.items.weapon.missiles.buildblock;

import pd.atlas.items.ConsumThrowsDict;
import pd.levels.Terrain;
public class WaterBlock extends LegacyBuildBlock {
	public WaterBlock() { super(Terrain.WATER, ConsumThrowsDict.WATER_BLOCK_PLACER); }
}
