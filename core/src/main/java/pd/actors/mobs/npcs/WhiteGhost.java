/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class WhiteGhost extends TownNpc {
	public WhiteGhost() {
		configure(Spec.WHITE_GHOST);
		spriteClass = pd.sprites.WhiteGhostSprite.class;
	}
}
