/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Lery extends TownNpc {
	public Lery() {
		configure(Spec.LERY);
		spriteClass = pd.sprites.LerySprite.class;
	}
}
