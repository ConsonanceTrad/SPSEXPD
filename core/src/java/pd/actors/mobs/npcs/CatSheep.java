/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class CatSheep extends TownNpc {
	public CatSheep() {
		configure(Spec.CAT_SHEEP);
		spriteClass = pd.sprites.CatSheepSprite.class;
	}
}
