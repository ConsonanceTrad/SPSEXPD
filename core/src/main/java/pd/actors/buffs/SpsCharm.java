/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

/** SPS-PD charm does not lose duration when its target takes damage. */
public class SpsCharm extends Charm {

	{
		announced = false;
	}

	@Override
	public void recover(Object src) {
		// Damage did not shorten charm in SPS-PD 0.9.8.
	}
}
