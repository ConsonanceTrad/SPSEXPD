/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.items.equipment.weapon.guns.GunWeapon;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 装填状态——装填期间挂在英雄身上，buff 图标上会显示剩余回合数，让装填过程更显眼。
 * 装填时间走完（本 buff 结束）的瞬间由它负责把枪的弹匣补满；装填期间弹匣为空，无法提前射击。
 */
public class Reloading extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Reloading.class)
			.t("name", "装填中")
			.t("desc", "正在装填武器弹药。\n\n剩余效果时长：%s回合。");
	}

	/** SPSEXPD: 正在装填的枪（对象引用不进存档，读档后按当前装备的枪重新绑定）。 */
	private GunWeapon gun;

	{ type = buffType.NEUTRAL; announced = false; }

	/** SPSEXPD: 绑定本次装填的目标枪械。 */
	public void bind(GunWeapon gun) { this.gun = gun; }

	/** SPSEXPD: 装填结束时补满弹匣（detach 时调用；单测可直接调用）。 */
	public void finish() {
		if (gun != null) gun.finishReload();
	}

	@Override
	public void detach() {
		//SPSEXPD: 装填时间走完的瞬间补满弹匣
		finish();
		super.detach();
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (Dungeon.hero != null && Dungeon.hero.belongings != null
				&& Dungeon.hero.belongings.weapon() instanceof GunWeapon) {
			gun = (GunWeapon) Dungeon.hero.belongings.weapon();
		}
	}

	@Override public int icon() { return BuffIndicator.RECHARGING; }

	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
