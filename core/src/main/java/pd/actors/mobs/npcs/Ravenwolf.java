/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Ravenwolf extends TownNpc {
	public Ravenwolf() {
		configure(Spec.RAVENWOLF);
		spriteClass = pd.sprites.RavenwolfSprite.class;
	}
}
