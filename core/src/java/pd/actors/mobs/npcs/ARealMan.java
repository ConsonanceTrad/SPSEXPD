/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class ARealMan extends TownNpc {
	public ARealMan() {
		configure(Spec.A_REAL_MAN);
		spriteClass = pd.sprites.ARealManSprite.class;
	}
}
