package pd.items.equipment.armor.fusion;

import pd.actors.Char;
import pd.items.equipment.armor.MailArmor;
import pd.messages.InlineText;

public class CatSharkArmor extends MailArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CatSharkArmor.class)
			.t("name", "猫鲨护甲")
			.t("desc", "轻便的三阶混合护甲。最大格挡比锁甲低1点，作为交换，穿戴者的行动速度提高6%。");
	}




	@Override
	public int DRMax(int lvl) {
		return Math.max(0, super.DRMax(lvl) - 1);
	}

	@Override
	public float speedFactor(Char owner, float speed) {
		return super.speedFactor(owner, speed) * 1.06f;
	}
}
