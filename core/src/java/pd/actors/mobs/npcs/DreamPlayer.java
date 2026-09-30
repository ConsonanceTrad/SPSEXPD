/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class DreamPlayer extends TownNpc {
	public DreamPlayer() {
		configure(Spec.DREAM_PLAYER);
		spriteClass = pd.sprites.DreamPlayerSprite.class;
	}
}
