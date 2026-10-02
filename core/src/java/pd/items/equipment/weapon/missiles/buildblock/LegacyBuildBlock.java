/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.buildblock;

import pd.atlas.IconEntry;
import pd.Dungeon;
import pd.actors.Actor;
import pd.levels.Level;
import pd.scenes.GameScene;
abstract class LegacyBuildBlock extends BuildBlock {
	private final int terrain;
	LegacyBuildBlock(int terrain,IconEntry image){this.terrain=terrain;this.image=image;}
	@Override protected void onThrow(int cell){if(Dungeon.level!=null&&Dungeon.level.insideMap(cell)&&Actor.findChar(cell)==null){Level.set(cell,terrain,Dungeon.level);GameScene.updateMap(cell);Dungeon.observe();}else super.onThrow(cell);}
}
