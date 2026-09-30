/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class FlyLing extends TownNpc {
	public FlyLing() {
		configure(Spec.FLY_LING);
		spriteClass = pd.sprites.WhiteLingSprite.class;
	}
}
