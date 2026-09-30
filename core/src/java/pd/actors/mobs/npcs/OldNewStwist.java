/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class OldNewStwist extends TownNpc {
	public OldNewStwist() {
		configure(Spec.OLD_NEW_STWIST);
		spriteClass = pd.sprites.OldNewStwistSprite.class;
	}
}
