/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class ConsideredHamster extends TownNpc {
	public ConsideredHamster() {
		configure(Spec.CONSIDERED_HAMSTER);
		spriteClass = pd.sprites.MimicSprite.class;
	}
}
