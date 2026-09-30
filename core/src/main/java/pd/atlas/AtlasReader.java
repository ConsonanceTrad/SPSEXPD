package pd.atlas;

import render.gltextures.SmartTexture;
import render.noosa.Image;
import render.utils.RectF;

/**
 * 图集条目的统一读取器：全项目通过字典取图的唯一入口。
 *
 * 职责：把 {@link IconEntry}（图集路径 + 帧矩形）转成可渲染的 {@link Image}。
 * 纹理本身由渲染层 {@code TextureCache} 按图集路径缓存，本类不重复缓存。
 *
 * 不负责：图标语义、布局与尺寸策略、动画时序（调用方自行决定用哪一帧）。
 */
public final class AtlasReader {

	private AtlasReader() { }

	/** 第 0 帧（单帧图标） */
	public static Image image(IconEntry entry) {
		return image(entry, 0);
	}

	/** 指定帧 */
	public static Image image(IconEntry entry, int frame) {
		Image img = new Image(entry.atlas);
		img.frame(uv(img.texture, entry, frame));
		return img;
	}

	/** 第 0 帧，并缩放到目标尺寸 */
	public static Image image(IconEntry entry, float targetW, float targetH) {
		Image img = image(entry, 0);
		img.scale.set(targetW / img.width(), targetH / img.height());
		return img;
	}

	/** 变体族 / 动画：按帧顺序取全部帧 */
	public static Image[] images(IconEntry entry) {
		Image[] result = new Image[entry.frames()];
		for (int i = 0; i < result.length; i++) {
			result[i] = image(entry, i);
		}
		return result;
	}

	private static RectF uv(SmartTexture texture, IconEntry entry, int frame) {
		return texture.uvRectBySize(entry.x(frame), entry.y(frame), entry.w(frame), entry.h(frame));
	}
}
