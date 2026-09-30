/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class NutPainter extends TownNpc {
	public NutPainter() {
		configure(Spec.NUT_PAINTER);
		spriteClass = pd.sprites.PainterSprite.class;
	}
}
