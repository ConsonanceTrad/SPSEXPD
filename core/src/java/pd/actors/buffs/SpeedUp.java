/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

/** The legacy SPS all-action speed boost. */
public class SpeedUp extends FlavourBuff {

	public static final float DURATION = 10f;
	public static final float SPEED_FACTOR = 1.5f;

	{
		type = buffType.POSITIVE;
	}
}
