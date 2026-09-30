/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class StormAndRain extends TownNpc {
	public StormAndRain() {
		configure(Spec.STORM_AND_RAIN);
		spriteClass = pd.sprites.StormAndRainSprite.class;
	}
}
