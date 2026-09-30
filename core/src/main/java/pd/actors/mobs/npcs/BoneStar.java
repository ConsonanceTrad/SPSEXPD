/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class BoneStar extends TownNpc {
	public BoneStar() {
		configure(Spec.BONE_STAR);
		spriteClass = pd.sprites.BoneStarSprite.class;
	}
}
