package pd.items.weapon.melee.relic;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Slow;
import pd.actors.hero.Hero;
import pd.items.weapon.enchantments.NeptuneShock;
import pd.levels.Terrain;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;

public class NeptunusTrident extends RelicMeleeWeapon {

	public static final String AC_FLOOD = "FLOOD";

	public NeptunusTrident() {
		super(1f, 1f, 2);
		image = ItemSpriteSheet.NEPTUNUS_TRIDENT;
		enchant(new NeptuneShock());
	}

	@Override
	protected String relicAction() {
		return AC_FLOOD;
	}

	@Override
	protected void useRelicPower(Hero hero) {
		for (int cell = Dungeon.level.width(); cell < Dungeon.level.length() - Dungeon.level.width(); cell++) {
			if (Dungeon.level.distance(hero.pos, cell) >= 2 || !floodable(cell)) continue;
			Dungeon.level.set(cell, Terrain.WATER);
			Char ch = Actor.findChar(cell);
			if (ch != null && ch != hero) Buff.affect(ch, Slow.class, Math.max(1, level() / 10f));
			GameScene.updateMap(cell);
		}
		Dungeon.observe();
	}

	private boolean floodable(int cell) {
		int terrain = Dungeon.level.map[cell];
		if (terrain == Terrain.ENTRANCE || terrain == Terrain.EXIT) return false;
		return Dungeon.level.water[cell]
				|| terrain == Terrain.EMPTY || terrain == Terrain.GRASS
				|| terrain == Terrain.HIGH_GRASS || terrain == Terrain.FURROWED_GRASS
				|| terrain == Terrain.EMBERS || terrain == Terrain.EMPTY_DECO
				|| terrain == Terrain.SIGN || terrain == Terrain.STATUE;
	}

}
