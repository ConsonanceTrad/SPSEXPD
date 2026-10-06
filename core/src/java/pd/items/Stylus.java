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
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.effects.Enchanting;
import pd.effects.particles.PurpleParticle;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.bags.Bag;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;
import render.noosa.audio.Sample;

import java.util.ArrayList;
import pd.messages.InlineText;

public class Stylus extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Stylus.class)
			.t("name", "奥术刻笔")
			.t("ac_inscribe", "蚀刻")
			.t("prompt", "选择一件护甲")
			.t("identify", "你需要先鉴定那件护甲有无诅咒。")
			.t("cursed", "奥术刻笔的力量无法作用在被诅咒的护甲上。")
			.t("inscribed", "你用刻笔雕刻了盔甲。")
			.t("desc", "这支奥术刻笔由一种暗黑色的坚硬石料制成。你能用它在护甲上刻下魔法刻印，不过你无法自主选择，刻笔会替你做出决定。");
	}



	
	private static final float TIME_TO_INSCRIBE = 2;
	
	private static final String AC_INSCRIBE = "INSCRIBE";
	
	{
		image = ConsumUsefulUsefulDict.STYLUS;
		
		stackable = true;

		defaultAction = AC_INSCRIBE;

		bones = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_INSCRIBE );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals(AC_INSCRIBE)) {

			curUser = hero;
			GameScene.selectItem( itemSelector );
			
		}
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	private void inscribe( Armor armor ) {

		if (!armor.cursedKnown){
			GLog.w( Messages.get(this, "identify"));
			return;
		} else if (armor.cursed || armor.hasCurseGlyph()){
			GLog.w( Messages.get(this, "cursed"));
			return;
		}
		
		detach(curUser.belongings.backpack);
		Catalog.countUse(getClass());

		GLog.w( Messages.get(this, "inscribed"));

		armor.inscribe();
		
		curUser.sprite.operate(curUser.pos);
		curUser.sprite.centerEmitter().start(PurpleParticle.BURST, 0.05f, 10);
		Enchanting.show(curUser, armor);
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
		
		curUser.spend(TIME_TO_INSCRIBE);
		curUser.busy();
	}
	
	private void inscribeSeal( BrokenSeal seal ) {

		//SPSEXPD: 纹章没有护甲那样的诅咒判定，直接刻上随机刻印
		detach(curUser.belongings.backpack);
		Catalog.countUse(getClass());

		GLog.w( Messages.get(this, "inscribed") );

		seal.inscribe( Armor.Glyph.random( seal.getGlyph() != null ? seal.getGlyph().getClass() : null ) );

		curUser.sprite.operate(curUser.pos);
		curUser.sprite.centerEmitter().start(PurpleParticle.BURST, 0.05f, 10);
		Enchanting.show(curUser, seal);
		Sample.INSTANCE.play(Assets.Sounds.BURNING);

		curUser.spend(TIME_TO_INSCRIBE);
		curUser.busy();
	}

	@Override
	public int value() {
		return 30 * quantity;
	}

	private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(Stylus.class, "prompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			//SPSEXPD: 拆卸下来的破损纹章同样可以刻印
			return item instanceof Armor || item instanceof BrokenSeal;
		}

		@Override
		public void onSelect( Item item ) {
			if (item instanceof Armor) {
				Stylus.this.inscribe( (Armor)item );
			} else if (item instanceof BrokenSeal) {
				Stylus.this.inscribeSeal( (BrokenSeal)item );
			}
		}
	};
}
