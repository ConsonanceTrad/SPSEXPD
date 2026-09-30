/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Rustyblade extends TownNpc {
	public Rustyblade() {
		configure(Spec.RUSTYBLADE);
		spriteClass = pd.sprites.RustybladeSprite.class;
	}
}
