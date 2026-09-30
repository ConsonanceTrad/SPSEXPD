/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Millilitre extends TownNpc {
	public Millilitre() {
		configure(Spec.MILLILITRE);
		spriteClass = pd.sprites.MillilitreSprite.class;
	}
}
