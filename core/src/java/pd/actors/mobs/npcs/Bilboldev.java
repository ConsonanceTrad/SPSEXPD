/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Bilboldev extends TownNpc {
	public Bilboldev() {
		configure(Spec.BILBOLDEV);
		spriteClass = pd.sprites.BilboldevSprite.class;
	}
}
