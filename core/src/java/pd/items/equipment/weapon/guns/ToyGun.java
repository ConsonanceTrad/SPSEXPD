package pd.items.equipment.weapon.guns;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponUniqueWeaponDict;

public class ToyGun extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ToyGun.class)
			.t("name", "玩具枪")
			.t("desc", "一把庆祝2019年新年与春节的简易玩具枪。")
			.t("ac_shoot", "射击")
			.t("ac_reload", "填弹");
	}



	{
		image = EquipmentEquipWeaponUniqueWeaponDict.PAINTBALL_GUN;
		reinforced = true;
	}
	public ToyGun() {
		super(GunArchetype.CUSTOM);
		fullCharge = 10;   //SPSEXPD: 玩具枪弹匣 10
		shotsPerFire = 1;
		perfectSTR = 10;
		effectiveRange = 4;
		noiseRadius = 3;
		baseReloadTime = 3f; //SPSEXPD: 玩具枪装填固定 3 回合
	}
	@Override public int min(int lvl) { return 1 + lvl; }
	@Override public int max(int lvl) { return 10 + 3 * lvl; }
	@Override protected boolean canShoot(Hero hero) { return isEquipped(hero); }
	@Override protected boolean canReload(Hero hero) { return isEquipped(hero); }
	@Override protected boolean causesVertigo() { return false; }
	@Override protected boolean addsEnergyDamage() { return false; }
	//SPSEXPD: 玩具枪装填固定 3 回合（不受特工 0.5 回合特例影响）
	@Override protected float reloadTime(Hero hero, boolean automatic) { return 3f; }
}
