/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.start;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.NormalMeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode.RockCode;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class XSaber extends NormalMeleeWeapon {
	public static final String AC_ADD = "ADD";
	private static final String ROCK_CODE = "rock_code";
	private RockCode rockCode;

	public XSaber() {
		super(1, 1.2f, 0.5f, 1, 6, 10, ItemSpriteSheet.RUNIC_BLADE);
		unique = true;
		reinforced = true;
		cursed = true;
	}
	@Override protected void applyLegacyUpgrade(Stats stats) { stats.min += 2; stats.max += 3; }
	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_ADD);
		return actions;
	}
	@Override public void execute(Hero hero, String action) {
		if (AC_ADD.equals(action)) { curUser = hero; GameScene.selectItem(selector); }
		else super.execute(hero, action);
	}
	public boolean learn(Hero hero, RockCode code) {
		if (hero == null || code == null || !hero.belongings.backpack.contains(code)) return false;
		Item consumed = code.detach(hero.belongings.backpack);
		if (!(consumed instanceof RockCode)) return false;
		rockCode = (RockCode)consumed;
		rockCode.identify();
		rockCode.cursed = false;
		hero.spendAndNext(2f);
		updateQuickslot();
		return true;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (rockCode != null) rockCode.onMeleeHit(this, attacker, defender, damage);
		return super.proc(attacker, defender, damage);
	}
	public RockCode rockCode() { return rockCode; }
	@Override public String info() {
		String info = super.info();
		if (rockCode != null) info += "\n\n" + Messages.get(this, "learned", rockCode.name());
		return info;
	}
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); if (rockCode != null) bundle.put(ROCK_CODE, rockCode); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); Object code = bundle.get(ROCK_CODE); rockCode = code instanceof RockCode ? (RockCode)code : null; }
	private final WndBag.ItemSelector selector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(XSaber.class, "prompt"); }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) { return item instanceof RockCode; }
		@Override public void onSelect(Item item) { if (item instanceof RockCode) learn(curUser, (RockCode)item); }
	};
}
