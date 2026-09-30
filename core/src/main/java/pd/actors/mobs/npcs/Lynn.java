/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Lynn extends TownNpc {
	public Lynn() {
		configure(Spec.LYNN);
		spriteClass = pd.sprites.LynnSprite.class;
	}
}
