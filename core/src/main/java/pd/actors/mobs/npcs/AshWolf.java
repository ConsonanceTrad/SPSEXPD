/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class AshWolf extends TownNpc {
	public AshWolf() {
		configure(Spec.ASH_WOLF);
		spriteClass = pd.sprites.AshWolfSprite.class;
	}
}
