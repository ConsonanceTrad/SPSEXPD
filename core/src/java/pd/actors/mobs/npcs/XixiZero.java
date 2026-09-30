/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class XixiZero extends TownNpc {
	public XixiZero() {
		configure(Spec.XIXI_ZERO);
		spriteClass = pd.sprites.XixiZeroSprite.class;
	}
}
