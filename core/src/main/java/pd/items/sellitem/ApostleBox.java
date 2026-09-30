/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.sellitem;

import pd.Assets;
import pd.actors.buffs.Arcane;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** Apostle's single-use chaos box with the original four equiprobable outcomes. */
public class ApostleBox extends SellItem {

	public static final String AC_APPLY = "APPLY";

	{
		image = ItemSpriteSheet.APOSTLE_BOX;
		defaultAction = AC_APPLY;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_APPLY);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_APPLY.equals(action)) {
			super.execute(hero, action);
			return;
		}

		int result = Random.Int(4);
		activate(hero, result);
		if (hero.sprite != null) {
			hero.sprite.centerEmitter().start(Speck.factory(Speck.MASK), 0.05f, 10);
			hero.sprite.operate(hero.pos);
		}
		Sample.INSTANCE.play(Assets.Sounds.EVOKE);
		GLog.i(Messages.get(this, messageKey(result)));
	}

	void activate(Hero hero, int result) {
		detach(hero.belongings.backpack);
		applyEffect(hero, result);
		hero.spend(1f);
		hero.busy();
	}

	void applyEffect(Hero hero, int result) {
		switch (result) {
			case 0:
				Buff.affect(hero, AttackUp.class, 50f).level(50);
				break;
			case 1:
				Buff.affect(hero, BerryRegeneration.class).level(50);
				break;
			case 2:
				Buff.affect(hero, DefenceUp.class, 50f).level(50);
				break;
			case 3:
				Buff.affect(hero, Invisibility.class, 50f);
				Buff.affect(hero, Arcane.class, 10f);
				break;
			default:
				throw new IllegalArgumentException("Unknown ApostleBox result: " + result);
		}
	}

	private static String messageKey(int result) {
		switch (result) {
			case 0: return "red";
			case 1: return "green";
			case 2: return "blue";
			default: return "violet";
		}
	}

	@Override public int value() { return 120 * quantity; }
}
