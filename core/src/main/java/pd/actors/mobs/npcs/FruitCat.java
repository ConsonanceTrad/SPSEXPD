/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class FruitCat extends TownNpc {
	public FruitCat() {
		configure(Spec.FRUIT_CAT);
		spriteClass = pd.sprites.FruitCatSprite.class;
	}
}
