/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Jinkeloid extends TownNpc {
	public Jinkeloid() {
		configure(Spec.JINKELOID);
		spriteClass = pd.sprites.JinkeloidSprite.class;
	}
}
