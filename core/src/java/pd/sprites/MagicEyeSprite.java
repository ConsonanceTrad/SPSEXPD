/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;

/** The legacy magic-eye sheet has the same frame layout as Shattered's eye. */
public class MagicEyeSprite extends EyeSprite {
	public MagicEyeSprite() {
		super();
		texture(Assets.Sprites.SPS_MAGIC_EYE);
	}
}
