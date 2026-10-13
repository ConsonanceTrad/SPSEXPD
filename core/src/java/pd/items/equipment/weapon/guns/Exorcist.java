package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.messages.InlineText;

/** SPSEXPD: 冲锋枪·驱魔人——弹匣最大、完美力量更低的冲锋枪。 */
public class Exorcist extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Exorcist.class)
			.t("name", "驱魔人")
			.t("desc", "弹匣做得极长的冲锋枪，据说最初的持有者用它把整座教堂的阴影打了出去。");
	}

	{ image = EquipmentEquipWeaponBasicWeaponDict.MACHINE_GUN; } //SPSEXPD: 占位图标，待美术替换
	public Exorcist() {
		super(GunArchetype.SMG);
		fullCharge = 25;     //SPSEXPD: 个体特色——弹匣 25
		perfectSTR = 15;     //SPSEXPD: 个体特色——完美力量 15
	}
}
