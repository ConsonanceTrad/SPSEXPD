/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Dachhack extends TownNpc {
	public Dachhack() {
		configure(Spec.DACHHACK);
		spriteClass = pd.sprites.DachhackSprite.class;
	}
}
