/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Apostle extends TownNpc {
	public Apostle() {
		configure(Spec.APOSTLE);
		spriteClass = pd.sprites.ApostleSprite.class;
	}
}
