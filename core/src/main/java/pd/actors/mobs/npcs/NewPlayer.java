/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class NewPlayer extends TownNpc {
	public NewPlayer() {
		configure(Spec.NEW_PLAYER);
		spriteClass = pd.sprites.NewPlayerSprite.class;
	}
}
