/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class HateSokoban extends TownNpc {
	public HateSokoban() {
		configure(Spec.HATE_SOKOBAN);
		spriteClass = pd.sprites.HateSokobanSprite.class;
	}
}
