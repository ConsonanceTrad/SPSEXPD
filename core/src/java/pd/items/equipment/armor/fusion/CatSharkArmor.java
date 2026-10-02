package pd.items.equipment.armor.fusion;

import pd.actors.Char;
import pd.items.equipment.armor.MailArmor;

public class CatSharkArmor extends MailArmor {

	@Override
	public int DRMax(int lvl) {
		return Math.max(0, super.DRMax(lvl) - 1);
	}

	@Override
	public float speedFactor(Char owner, float speed) {
		return super.speedFactor(owner, speed) * 1.06f;
	}
}
