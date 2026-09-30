/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Tinkerer4 extends TownNpc {
	public Tinkerer4() {
		configure(Spec.MAYOR);
		spriteClass = pd.sprites.NoodlemireSprite.class;
	}
}
