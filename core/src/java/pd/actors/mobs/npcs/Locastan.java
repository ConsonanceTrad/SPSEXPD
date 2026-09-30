/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Locastan extends TownNpc {
	public Locastan() {
		configure(Spec.LOCASTAN);
		spriteClass = pd.sprites.LocastanSprite.class;
	}
}
