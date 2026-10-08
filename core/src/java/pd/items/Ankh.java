/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.items;

import pd.atlas.items.ConsumUsefulUsefulDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.misc.Ankhshield;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: 由「重生十字架」改为「安卡」——不再有祝福概念，
 * 死亡时一律在原地复活（恢复 10% 生命 + 2 回合无敌），物品不会遗落。
 * 仍可把安卡按在神圣护盾上为其充能。
 */
public class Ankh extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Ankh.class)
			.t("name", "安卡")
			.t("ac_charge", "充能")
			.t("desc", "这枚象征不朽的古老饰物能使人起死回生：死亡时会直接在原地复活，恢复 10% 生命并获得 2 回合的无敌，随身物品也不会遗落。它不需要任何形式的祝福。也可以把它按在神圣护盾上，为护盾补充充能。")
			.t("discover_hint", "你可在商店中购买该物品。")
			.t("charge_done", "你把这枚安卡按在神圣护盾上，护盾恢复了 %1$d 点充能。")
			.t("charge_full", "神圣护盾的充能已经满了。")
			.t("no_shield", "你没有持有神圣护盾。");
	}

	//SPSEXPD: 安卡可以为神圣护盾充能（护盾不再逐回合自然回复）
	public static final String AC_CHARGE = "CHARGE";
	public static final int SHIELD_CHARGE = 50;

	{
		image = ConsumUsefulUsefulDict.ANKH;

		//You tell the ankh no, don't revive me, and then it comes back to revive you again in another run.
		//I'm not sure if that's enthusiasm or passive-aggression.
		bones = true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions(hero);
		Ankhshield shield = shield(hero);
		if (shield != null && shield.charge() < Ankhshield.FULL_CHARGE) actions.add( AC_CHARGE );
		return actions;
	}

	/** 英雄身上的神圣护盾，没有则为 null。 */
	public static Ankhshield shield(Hero hero) {
		return hero == null ? null : hero.belongings.getItem(Ankhshield.class);
	}

	@Override
	public String defaultAction() {
		//SPSEXPD: 持有神圣护盾时，快捷行为改为充能
		if (shield(Dungeon.hero) != null) return AC_CHARGE;
		return super.defaultAction();
	}

	@Override
	public void execute( final Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_CHARGE )) {

			chargeShield( hero );
		}
	}

	//SPSEXPD: 吞噬一枚安卡为神圣护盾充能
	private void chargeShield( Hero hero ) {
		Ankhshield shield = shield(hero);
		if (shield == null) {
			GLog.w( Messages.get(this, "no_shield") );
			return;
		}
		int missing = Ankhshield.FULL_CHARGE - shield.charge();
		if (missing <= 0) {
			GLog.i( Messages.get(this, "charge_full") );
			return;
		}

		int gained = Math.min(SHIELD_CHARGE, missing);
		shield.charge( shield.charge() + gained );
		GLog.p( Messages.get(this, "charge_done", gained) );

		Sample.INSTANCE.play( Assets.Sounds.DRINK );
		if (hero.sprite != null) {
			CellEmitter.get(hero.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
			hero.sprite.operate( hero.pos );
		}
		hero.spend( 1f );
		hero.busy();

		//这枚安卡被护盾吞噬
		detachAll( hero.belongings.backpack );
	}

	@Override
	public int value() {
		return 50 * quantity;
	}
}
