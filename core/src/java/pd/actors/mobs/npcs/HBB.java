/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class HBB extends TownNpc {
	public HBB() {
		configure(Spec.HBB);
		spriteClass = pd.sprites.HBBSprite.class;
	}
}
