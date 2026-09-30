package pd.items.food.vegetable;

import pd.actors.buffs.ArcaneArmor;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.PhysicalEmpower;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

public class BattleFlower extends Vegetable {
	{ image = ItemSpriteSheet.STAR_FLOWER; }
	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Bless.class, 30f);
		Buff.affect(hero, ArcaneArmor.class).set(3 + hero.lvl / 4, 30);
		Buff.affect(hero, PhysicalEmpower.class).set(3 + hero.lvl / 3, 5);
	}
}
