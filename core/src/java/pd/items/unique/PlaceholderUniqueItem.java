/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.messages.InlineText;
import pd.messages.Messages;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 特殊物品的通用「占位」实现。
 *
 * <p>清单里尚未实装具体效果的专属物品，都用本类 + 一个 {@link UniqueItemId} 编号登记：
 * 名称取自编号的中文名（内联）+ 英文模板，描述统一为占位说明，图标统一用通用占位图。
 * 将来实装功能时，把它替换为独立类并沿用同一个 {@code UniqueItemId} 即可。</p>
 */
public class PlaceholderUniqueItem extends Item implements UniqueItem {

	//SPSEXPD: inline Chinese text —— 逐个占位物品的名字 + 统一的占位描述
	static {
		InlineText.of(PlaceholderUniqueItem.class)
			.t("placeholder_desc", "占位物品：具体效果尚未实装。");
		for (UniqueItemId id : UniqueItemId.values()) {
			if (id.zhName != null) {
				InlineText.of(PlaceholderUniqueItem.class).t(id.key() + ".name", id.zhName);
			}
		}
	}

	private static final String ID = "unique_id";

	private UniqueItemId id;

	public PlaceholderUniqueItem() {
		this(UniqueItemId.WARRIOR_SEAL_T1_2_BLOOD);
	}

	public PlaceholderUniqueItem( UniqueItemId id ) {
		this.id = id;
		//SPSEXPD: 暂用通用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public UniqueItemId uniqueId() {
		return id;
	}

	@Override
	public String name() {
		if (id == null) return super.name();
		return Messages.get(PlaceholderUniqueItem.class, id.key() + ".name");
	}

	@Override
	public String desc() {
		return Messages.get(PlaceholderUniqueItem.class, "placeholder_desc");
	}

	@Override public boolean isUpgradable() { return false; }

	@Override public boolean isIdentified() { return true; }

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ID, id == null ? "" : id.name() );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		String s = bundle.getString( ID );
		id = (s == null || s.isEmpty()) ? null : UniqueItemId.valueOf( s );
	}
}
