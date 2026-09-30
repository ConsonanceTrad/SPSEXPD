/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class RainTrainer extends TownNpc {
	public RainTrainer() {
		configure(Spec.RAIN_TRAINER);
		spriteClass = pd.sprites.RainSprite.class;
	}
}
