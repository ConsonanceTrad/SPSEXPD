/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class LaJi extends TownNpc {
	public LaJi() {
		configure(Spec.LAJI);
		spriteClass = pd.sprites.LaJiSprite.class;
	}
}
