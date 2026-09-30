/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class AliveFish extends TownNpc {
	public AliveFish() {
		configure(Spec.ALIVE_FISH);
		spriteClass = pd.sprites.PiranhaSprite.class;
	}
}
