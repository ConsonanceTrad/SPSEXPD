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

package pd.items.consum.potions.brews;

import pd.messages.InlineText;

/**
 * SPSEXPD: 7 种「效果待实现」的魔药（docs/alchemy-rework.md §7.3）。
 * 目前统一沿用 {@link PlaceholderBrew} 的 +10 护盾占位效果。
 */
public final class SpsPlaceholderElixirs {

	private SpsPlaceholderElixirs() {}

	/** 力量药剂 → 巨力魔药。 */
	public static class TitanBrew extends PlaceholderBrew {
		static {
			InlineText.of(TitanBrew.class)
				.t("name", "巨力魔药")
				.t("desc", "（占位效果：投掷后为落点角色提供 10 点护盾）");
		}
	}

	/** 治疗药剂 → 生机魔药。 */
	public static class VitalBrew extends PlaceholderBrew {
		static {
			InlineText.of(VitalBrew.class)
				.t("name", "生机魔药")
				.t("desc", "（占位效果：投掷后为落点角色提供 10 点护盾）");
		}
	}

	/** 灵视药剂 → 洞察魔药。 */
	public static class InsightBrew extends PlaceholderBrew {
		static {
			InlineText.of(InsightBrew.class)
				.t("name", "洞察魔药")
				.t("desc", "（占位效果：投掷后为落点角色提供 10 点护盾）");
		}
	}

	/** 强酸药剂 → 蚀骨魔药。 */
	public static class CorrodeBrew extends PlaceholderBrew {
		static {
			InlineText.of(CorrodeBrew.class)
				.t("name", "蚀骨魔药")
				.t("desc", "（占位效果：投掷后为落点角色提供 10 点护盾）");
		}
	}

	/** 极速药剂 → 疾风魔药。 */
	public static class SwiftBrew extends PlaceholderBrew {
		static {
			InlineText.of(SwiftBrew.class)
				.t("name", "疾风魔药")
				.t("desc", "（占位效果：投掷后为落点角色提供 10 点护盾）");
		}
	}

	/** 隐形药剂 → 隐匿魔药。 */
	public static class VeilBrew extends PlaceholderBrew {
		static {
			InlineText.of(VeilBrew.class)
				.t("name", "隐匿魔药")
				.t("desc", "（占位效果：投掷后为落点角色提供 10 点护盾）");
		}
	}

	/** 经验药剂 → 升华魔药。 */
	public static class AscendBrew extends PlaceholderBrew {
		static {
			InlineText.of(AscendBrew.class)
				.t("name", "升华魔药")
				.t("desc", "（占位效果：投掷后为落点角色提供 10 点护盾）");
		}
	}
}
