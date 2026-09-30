/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.artifacts.fusion;

import pd.items.artifacts.Artifact;
import pd.sprites.ItemSpriteSheet;

/** SPS-PD 0.9.8's powerless Noomlin keepsake. */
public class NoomlinCrown extends Artifact {

	{
		image = ItemSpriteSheet.NOOMLIN_CROWN;
		levelCap = 1;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Crown();
	}

	public class Crown extends ArtifactBuff {
	}

	@Override
	public int value() {
		return 100 * quantity();
	}
}
