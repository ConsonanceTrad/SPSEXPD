/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class GoblinPlayer extends TownNpc {
	public GoblinPlayer() {
		configure(Spec.GOBLIN_PLAYER);
		spriteClass = pd.sprites.GoblinPlayerSprite.class;
	}
}
