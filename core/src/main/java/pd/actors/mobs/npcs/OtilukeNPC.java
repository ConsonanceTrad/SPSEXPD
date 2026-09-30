/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class OtilukeNPC extends TownNpc {
	public OtilukeNPC() {
		configure(Spec.OTILUKE_NPC);
		spriteClass = pd.sprites.OtilukeNPCSprite.class;
	}
}
