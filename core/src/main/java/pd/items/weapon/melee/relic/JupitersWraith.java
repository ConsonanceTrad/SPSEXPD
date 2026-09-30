package pd.items.weapon.melee.relic;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.weapon.enchantments.JupitersHorror;
import pd.sprites.ItemSpriteSheet;
import watabou.noosa.Camera;
import watabou.utils.Random;

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
