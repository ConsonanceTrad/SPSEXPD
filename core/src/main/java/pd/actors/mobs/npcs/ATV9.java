/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class ATV9 extends TownNpc {
	public ATV9() {
		configure(Spec.ATV9);
		spriteClass = pd.sprites.ATV9Sprite.class;
	}
}
