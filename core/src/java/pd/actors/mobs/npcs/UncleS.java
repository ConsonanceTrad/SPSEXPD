/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class UncleS extends TownNpc {
	public UncleS() {
		configure(Spec.UNCLE_S);
		spriteClass = pd.sprites.UncleSSprite.class;
	}
}
