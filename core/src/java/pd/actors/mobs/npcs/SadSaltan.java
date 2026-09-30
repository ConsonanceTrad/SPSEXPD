/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class SadSaltan extends TownNpc {
	public SadSaltan() {
		configure(Spec.SAD_SALTAN);
		spriteClass = pd.sprites.SadSaltanSprite.class;
	}
}
