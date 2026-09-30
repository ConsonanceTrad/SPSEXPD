/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Watabou extends TownNpc {
	public Watabou() {
		configure(Spec.WATABOU);
		spriteClass = pd.sprites.WatabouSprite.class;
	}
}
