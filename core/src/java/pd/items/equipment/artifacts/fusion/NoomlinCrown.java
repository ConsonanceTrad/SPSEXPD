/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.artifacts.fusion;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.items.equipment.artifacts.Artifact;

/** SPS-PD 0.9.8's powerless Noomlin keepsake. */
public class NoomlinCrown extends Artifact {

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
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
