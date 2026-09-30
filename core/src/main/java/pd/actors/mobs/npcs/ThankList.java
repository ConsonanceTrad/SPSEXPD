/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class ThankList extends TownNpc {
	public ThankList() {
		configure(Spec.THANK_LIST);
		spriteClass = pd.sprites.ThankListSprite.class;
	}
}
