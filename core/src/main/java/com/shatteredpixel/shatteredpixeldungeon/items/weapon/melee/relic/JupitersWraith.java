package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.relic;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.JupitersHorror;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Camera;
import com.watabou.utils.Random;

public class JupitersWraith extends RelicMeleeWeapon {

	public static final String AC_EXPLODE = "EXPLODE";

	public JupitersWraith() {
		super(1f, 1f, 4);
		image = ItemSpriteSheet.JUPITERS_WRAITH;
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
