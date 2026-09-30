/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class SP931 extends TownNpc {
	public SP931() {
		configure(Spec.SP931);
		spriteClass = pd.sprites.SP931Sprite.class;
	}
}
