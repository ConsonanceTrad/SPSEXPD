package pd.plants;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Dewdrop;
import pd.items.RedDewdrop;
import pd.items.VioletDewdrop;
import pd.items.YellowDewdrop;
import pd.items.medicine.GreenSpore;
import pd.sprites.ItemSpriteSheet;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Dewcatcher extends Plant {
	{ image = 12; seedClass = Seed.class; }
	@Override public void activate(Char ch) {
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = pos + offset;
			if (cell >= 0 && cell < Dungeon.level.length() && Dungeon.level.passable[cell]) {
				Dewdrop dew;
				if (Random.Int(10) == 1) dew = new VioletDewdrop();
				else if (Random.Int(5) == 1) dew = new RedDewdrop();
				else if (Random.Int(3) == 1) dew = new YellowDewdrop();
				else dew = new Dewdrop();
				Dungeon.level.drop(dew, cell).sprite.drop(pos);
			}
		}
	}
	public static class Seed extends Plant.Seed {
		{ image = ItemSpriteSheet.SPS_SEED_DEWCATCHER; plantClass = Dewcatcher.class; explantClass = ExDewcatcher.class; }
	}
	public static class ExDewcatcher extends SpsFruitBush {
		{ image = 12; harvestCount = 3; harvestClass = GreenSpore.class; }
	}
}
