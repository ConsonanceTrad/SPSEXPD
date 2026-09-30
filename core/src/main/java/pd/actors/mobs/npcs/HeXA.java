/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class HeXA extends TownNpc {
	public HeXA() {
		configure(Spec.HEXA);
		spriteClass = pd.sprites.HeXASprite.class;
	}
}
