/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Coconut2 extends TownNpc {
	public Coconut2() {
		configure(Spec.COCONUT2);
		spriteClass = pd.sprites.CoconutSprite.class;
	}
}
