/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class RENnpc extends TownNpc {
	public RENnpc() {
		configure(Spec.RENNPC);
		spriteClass = pd.sprites.RENSprite.class;
	}
}
