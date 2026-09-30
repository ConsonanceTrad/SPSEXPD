/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class TypedScroll extends TownNpc {
	public TypedScroll() {
		configure(Spec.TYPED_SCROLL);
		spriteClass = pd.sprites.TypedScrollSprite.class;
	}
}
