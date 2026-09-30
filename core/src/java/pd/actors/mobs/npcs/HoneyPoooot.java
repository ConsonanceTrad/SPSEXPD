/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class HoneyPoooot extends TownNpc {
	public HoneyPoooot() {
		configure(Spec.HONEY_POOOOT);
		spriteClass = pd.sprites.HoneyPooootSprite.class;
	}
}
