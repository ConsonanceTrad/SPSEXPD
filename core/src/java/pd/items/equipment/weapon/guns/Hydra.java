package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.messages.InlineText;

/** SPSEXPD: 手枪·九头蛇——弹匣小但单发更重的重型手枪。 */
public class Hydra extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Hydra.class)
			.t("name", "九头蛇")
			.t("desc", "多管交织的重型手枪，弹匣虽小，每一发都像被蛇头撕咬。");
	}

	{ image = EquipmentEquipWeaponGunDict.HYDRA; }
	public Hydra() {
		super(GunArchetype.PISTOL);
		fullCharge = 6;      //SPSEXPD: 个体特色——弹匣 6
		perfectSTR = 13;     //SPSEXPD: 个体特色——完美力量 13
	}
	@Override public int min(int lvl) { return 5 + lvl; }
	@Override public int max(int lvl) { return 9 + 2 * lvl; }
}
