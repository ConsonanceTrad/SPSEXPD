package pd.items.equipment.weapon.melee.relic;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.equipment.weapon.enchantments.JupitersHorror;
import render.noosa.Camera;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

public class JupitersWraith extends RelicMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(JupitersWraith.class)
			.t("name", "落岩圆刃")
			.t("desc", "这件刃缘锋利的圆形武器，只要熟练使用便能在命中后返回手中。\n借由黄色魔法石的能量，它可以威慑目标，并在充能完毕后震击大范围内的敌人。")
			.t("ac_explode", "落岩震击")
			.t("stats_desc", "");
	}




	public static final String AC_EXPLODE = "EXPLODE";

	public JupitersWraith() {
		super(1f, 1f, 4);
		image = EquipmentEquipWeaponBasicWeaponDict.DETERRENT_ROCK_BLADE;
		enchant(new JupitersHorror());
	}

	@Override
	protected String relicAction() {
		return AC_EXPLODE;
	}

	@Override
	protected void useRelicPower(Hero hero) {
		if (Camera.main != null) Camera.main.shake(3, 0.7f);
		for (int cell = Dungeon.level.width(); cell < Dungeon.level.length() - Dungeon.level.width(); cell++) {
			if (Dungeon.level.distance(hero.pos, cell) >= 4) continue;
			if (hero.sprite != null && Dungeon.level.heroFOV[cell] && Dungeon.level.passable[cell]) {
				CellEmitter.center(cell).start(Speck.factory(Speck.ROCK), 0.07f, 10);
			}
			Char ch = Actor.findChar(cell);
			if (ch != null && ch != hero) {
				int damage = Random.NormalIntRange(min(), max()) - Math.max(ch.drRoll(), 0);
				if (damage > 0) {
					ch.damage(damage, this);
					if (ch.isAlive() && Random.Int(3) == 1) Buff.prolong(ch, Paralysis.class, 1f);
				}
			}
		}
	}

}
