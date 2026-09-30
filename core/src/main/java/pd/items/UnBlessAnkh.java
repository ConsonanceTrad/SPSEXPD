/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.Assets;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class UnBlessAnkh extends Item {
	public static final String AC_BLESS = "BLESS";
	{
		image = ItemSpriteSheet.SPS_UNBLESS_ANKH;
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		Waterskin waterskin = hero.belongings.getItem(Waterskin.class);
		if (waterskin != null && waterskin.isFullBless()) actions.add(AC_BLESS);
		return actions;
	}
	@Override public void execute(Hero hero, String action) {
		if (!AC_BLESS.equals(action)) { super.execute(hero, action); return; }
		bless(hero);
	}
	public boolean bless(Hero hero) {
		if (hero == null || hero.belongings == null) return false;
		Waterskin waterskin = hero.belongings.getItem(Waterskin.class);
		if (waterskin == null || !waterskin.isFullBless() || !hero.belongings.contains(this)) return false;
		detach(hero.belongings.backpack);
		Item ankh = new Ankh();
		if (!ankh.collect(hero.belongings.backpack) && Dungeon.level != null) {
			Dungeon.level.drop(ankh, hero.pos).sprite.drop();
		}
		waterskin.upbook(100);
		GLog.p(Messages.get(this, "bless"));
		Sample.INSTANCE.play(Assets.Sounds.DRINK);
		if (hero.sprite != null) {
			CellEmitter.get(hero.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
			hero.sprite.operate(hero.pos);
		}
		hero.spendAndNext(1f);
		return true;
	}
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 25 * quantity; }
}
