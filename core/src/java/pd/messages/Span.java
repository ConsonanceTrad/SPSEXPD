/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 富文本片段：一段文字 + 它自己的颜色。
 * 供 RenderedTextBlock.spans() 之类的多色文本渲染使用。
 */

package pd.messages;

public final class Span {

	/** 默认颜色：由控件自身的硬着色（或 _ / ** 高亮）决定 */
	public static final int DEFAULT = -1;

	public final String text;
	public final int color;

	public Span(String text, int color) {
		this.text = text == null ? "" : text;
		this.color = color;
	}

	public static Span of(String text) {
		return new Span(text, DEFAULT);
	}

	public static Span of(String text, int color) {
		return new Span(text, color);
	}
}
