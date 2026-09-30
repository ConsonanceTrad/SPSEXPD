/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Ice13 extends TownNpc {
	public Ice13() {
		configure(Spec.ICE13);
		spriteClass = pd.sprites.Ice13Sprite.class;
	}
}
