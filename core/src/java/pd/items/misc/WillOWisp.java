/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.Dungeon;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Item;
import pd.messages.InlineText;
import pd.scenes.GameScene;

/**
 * SPSEXPD: 踩踏高草的收获——磷火。
 * 掷出后点燃落点格子，自身在点火的瞬间烧尽（不落地）。
 */
public class WillOWisp extends Item {
	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(WillOWisp.class)
			.t("name", "磷火")
			.t("desc", "一小簇游荡在草叶之间的冷焰。掷出去，它会在落点腾起火焰，随后烧尽。");
	}

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = true;
		defaultAction = AC_THROW;
	}

	@Override
	protected void onThrow( int cell ) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(cell)) {
			super.onThrow( cell );
			return;
		}
		//SPSEXPD: 点燃落点格子；磷火自身不落地（掷出即烧尽）
		GameScene.add( Blob.seed( cell, 5, Fire.class ) );
		if (Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[cell]) {
			CellEmitter.get( cell ).burst( Speck.factory( Speck.INFERNO ), 6 );
		}
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 6 * quantity; }
}
