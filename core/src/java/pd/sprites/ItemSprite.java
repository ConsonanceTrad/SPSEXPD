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

package pd.sprites;

import pd.atlas.IconEntry;
import pd.atlas.items.GroundGroundingItemsDict;
import pd.atlas.IconEntry;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.atlas.IconEntry;
import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.levels.Terrain;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.tiles.DungeonTilemap;
import render.gltextures.SmartTexture;
import render.gltextures.TextureCache;
import render.glwrap.Matrix;
import render.glwrap.Vertexbuffer;
import render.noosa.Camera;
import render.noosa.Game;
import render.noosa.Image;
import render.noosa.MovieClip;
import render.noosa.NoosaScript;
import render.noosa.audio.Sample;
import render.noosa.particles.Emitter;
import render.utils.geom.PointF;
import render.utils.math.Random;

import java.nio.Buffer;

public class ItemSprite extends MovieClip {

	public static final int SIZE	= 16;
	
	private static final float DROP_INTERVAL = 0.4f;
	
	public Heap heap;
	
	private Glowing glowing;
	//FIXME: a lot of this emitter functionality isn't very well implemented.
	//right now I want to ship 0.3.0, but should refactor in the future.
	protected Emitter emitter;
	private float phase;
	private boolean glowUp;
	
	private float dropInterval;

	//the amount the sprite is raised from flat when viewed in a raised perspective
	//SPSEXPD: 默认 0（不抬）；实际值由 Item.spriteRaise() 按图标帧高决定小件是否抬 SMALL_ITEM_LIFT
	protected float perspectiveRaise    =  0f;

	//SPSEXPD: 小图标的地面抬升值：避免底边贴住格子底边显得太靠下（上游 Shattered/STSEXPD 是全局抬 5px）。
	//3/16 格 = 3px：16px 的格子里 10px 高的露珠上下各留 3px，视觉居中（原 5px 几乎贴格顶，实测太浮）。
	public static final float SMALL_ITEM_LIFT       =  3 / 16f; //3 像素

	//SPSEXPD: 帧高不超过这个值的图标一律按小件抬升 SMALL_ITEM_LIFT（露珠 10px 即属此列）。
	public static final int SMALL_ITEM_MAX_HEIGHT   =  10;

	//the width and height of the shadow are a percentage of sprite size
	//offset is the number of pixels the shadow is moved down or up (handy for some animations)
	protected boolean renderShadow  = false;
	protected float shadowWidth     = 1f;
	protected float shadowHeight    = 0.25f;
	protected float shadowOffset    = 0.5f;
	
	public ItemSprite() {
		this( SpecificPlaceHolderDict.SOMETHING_0, null );
	}
	
	public ItemSprite( Heap heap ){
		super();
		view( heap );
	}
	
	public ItemSprite( Item item ) {
		super();
		view( item );
	}
	
	public ItemSprite( IconEntry image ){
		this( image, null );
	}
	
	public ItemSprite( IconEntry image, Glowing glowing ) {
		super();
		
		view(image, glowing);
	}
	
	public void link() {
		link(heap);
	}
	
	public void link( Heap heap ) {
		this.heap = heap;
		view(heap);
		//SPSEXPD: 阴影由堆类型决定（藏宝地是地面装饰、不投立体阴影）
		updateShadow();
		visible = heap.seen;
		place(heap.pos);
	}

	/** SPSEXPD: 藏宝地（E_DUST）贴地，不画椭圆阴影；其余堆保持原样。 */
	protected void updateShadow() {
		renderShadow = heap == null || heap.type != Heap.Type.E_DUST;
	}
	
	@Override
	public void revive() {
		super.revive();
		
		speed.set( 0 );
		acc.set( 0 );
		dropInterval = 0;
		
		heap = null;
		if (emitter != null) {
			emitter.killAndErase();
			emitter = null;
		}
	}

	@Override
	public void copy(Image other) {
		super.copy(other);

		if (other instanceof ItemSprite && ((ItemSprite) other).glowing != null){
			glow(((ItemSprite) other).glowing);
		}

	}

	public void visible(boolean value){
		this.visible = value;
		if (emitter != null && !visible){
			emitter.killAndErase();
			emitter = null;
		}
	}
	
	public PointF worldToCamera( int cell ) {
		final int csize = DungeonTilemap.SIZE;
		
		return new PointF(
				PixelScene.align(Camera.main, ((cell % Dungeon.level.width()) + 0.5f) * csize - width() * 0.5f),
				PixelScene.align(Camera.main, ((cell / Dungeon.level.width()) + 1.0f) * csize - height() - csize * perspectiveRaise)
		);
	}
	
	public void place( int p ) {
		if (Dungeon.level != null) {
			point(worldToCamera(p));
			shadowOffset = 0.5f;
		}
	}
	
	public void drop() {

		if (heap.isEmpty()) {
			return;
		} else if (heap.size() == 1){
			// normally this would happen for any heap, however this is not applied to heaps greater than 1 in size
			// in order to preserve an amusing visual bug/feature that used to trigger for heaps with size > 1
			// where as long as the player continually taps, the heap sails up into the air.
			place(heap.pos);
		}
			
		dropInterval = DROP_INTERVAL;
		
		speed.set( 0, -100 );
		acc.set(0, -speed.y / DROP_INTERVAL * 2);
		
		if (heap != null && heap.seen && heap.peek() instanceof Gold) {
			CellEmitter.center( heap.pos ).burst( Speck.factory( Speck.COIN ), 5 );
			Sample.INSTANCE.play( Assets.Sounds.GOLD, 1, 1, Random.Float( 0.9f, 1.1f ) );
		}
	}
	
	public void drop( int from ) {

		if (heap.pos == from) {
			drop();
		} else {
			
			float px = x;
			float py = y;
			drop();
			
			place(from);
	
			speed.offset((px - x) / DROP_INTERVAL, (py - y) / DROP_INTERVAL);
		}
	}

	public ItemSprite view( Item item ){
		//SPSEXPD: 地面金币堆按数量换图标（小额改用单枚金币，见 Gold.pileImage）
		view( heap != null && item instanceof Gold ? Gold.pileImage( item.quantity() ) : item.image(),
				item.glowing() );
		//SPSEXPD: 抬升由物品自己决定（默认 0 = 贴格底）。必须在 frame() 之后赋值，否则会被重置。
		perspectiveRaise = item.spriteRaise();
		Emitter emitter = item.emitter();
		if (emitter != null && parent != null) {
			emitter.pos( this );
			parent.add( emitter );
			this.emitter = emitter;
		}
		return this;
	}
	
	public ItemSprite view( Heap heap ){
		if (heap.size() <= 0 || heap.items == null){
			return view( SpecificPlaceHolderDict.SOMETHING_0, null );
		}

		switch (heap.type) {
			case HEAP: case FOR_SALE: case FOR_LIFE:
				view( heap.peek() ); break;
			case CHEST: case MIMIC:
				view( GroundGroundingItemsDict.CHEST_0, null ); break;
			case LOCKED_CHEST:
			case G_MIMIC:
				view( GroundGroundingItemsDict.LOCKED_CHEST_0, null ); break;
			case CRYSTAL_CHEST:
				view( GroundGroundingItemsDict.CRYSTAL_CHEST_0, null ); break;
			case TOMB:
				view( SpecificPlaceHolderDict.SOMETHING_0, null ); break;
			case SKELETON:
				view( SpecificPlaceHolderDict.SOMETHING_0, null ); break;
			case REMAINS:
				view( SpecificPlaceHolderDict.SOMETHING_0, null ); break;
			case E_DUST:
				view( GroundGroundingItemsDict.TREASURE_SPOT, null );
				//SPSEXPD: 藏宝地是 16x10 的贴地装饰——上抬 1px（原先贴格底偏下，居中 3px 又偏高）
				//（frame() 会把 perspectiveRaise 归零，所以必须在 view 之后赋值）
				perspectiveRaise = 1 / 16f;
				break;
			case M_WEB:
				view( SpecificPlaceHolderDict.SOMETHING_0, null ); break;
			default:
				view( SpecificPlaceHolderDict.SOMETHING_0, null );
		}

		alpha( heap.hidden ? 0.1f : 1f);
		//SPSEXPD: 堆类型变化后同步阴影（藏宝地不投影）
		updateShadow();

		return this;
	}
	
	public ItemSprite view( IconEntry image, Glowing glowing ) {
		if (this.emitter != null) this.emitter.killAndErase();
		emitter = null;
		frame( image );
		glow( glowing );
		return this;
	}

	public void frame( IconEntry entry ){
		if (entry == null) entry = SpecificPlaceHolderDict.SOMETHING_0;

		SmartTexture tex = TextureCache.get( entry.atlas );
		this.texture = tex;
		frame( tex.uvRectBySize( entry.x(0), entry.y(0), entry.w(0), entry.h(0) ) );

		//SPSEXPD: 抬升值不再按图标尺寸判定，统一在这里重置为 0（精灵对象池 heaps.recycle 复用时必须重置）；
		//需要抬升的物品由 Item.spriteRaise() 决定，在 view(Item) 里应用。
		perspectiveRaise = 0f;
	}
	
	public synchronized void glow( Glowing glowing ){
		this.glowing = glowing;
		if (glowing == null) resetColor();
	}

	@Override
	public void kill() {
		super.kill();
		if (emitter != null) {
			emitter.on = false;
			emitter.autoKill = true;
		}
		emitter = null;
	}

	private float[] shadowMatrix = new float[16];

	@Override
	protected void updateMatrix() {
		super.updateMatrix();
		Matrix.copy(matrix, shadowMatrix);
		Matrix.translate(shadowMatrix,
				(width() * (1f - shadowWidth)) / 2f,
				(height() * (1f - shadowHeight)) + shadowOffset);
		Matrix.scale(shadowMatrix, shadowWidth, shadowHeight);
	}

	@Override
	public void draw() {
		if (texture == null || (!dirty && buffer == null))
			return;

		if (renderShadow) {
			if (dirty) {
				((Buffer)verticesBuffer).position(0);
				verticesBuffer.put(vertices);
				if (buffer == null)
					buffer = new Vertexbuffer(verticesBuffer);
				else
					buffer.updateVertices(verticesBuffer);
				dirty = false;
			}

			NoosaScript script = script();

			texture.bind();

			script.camera(camera());

			updateMatrix();

			script.uModel.valueM4(shadowMatrix);
			script.lighting(
					0, 0, 0, am * .6f,
					0, 0, 0, aa * .6f);

			script.drawQuad(buffer);
		}

		super.draw();

	}

	@Override
	public synchronized void update() {
		super.update();

		visible = (heap == null || heap.seen);

		if (emitter != null){
			emitter.visible = visible;
		}

		if (dropInterval > 0){
			shadowOffset -= speed.y * Game.elapsed * 0.8f;

			if ((dropInterval -= Game.elapsed) <= 0){

				speed.set(0);
				acc.set(0);
				shadowOffset = 0.25f;
				place(heap.pos);

				if (visible) {

					if (Dungeon.level.water[heap.pos]) {
						GameScene.ripple(heap.pos);
					}

					if (Dungeon.level.water[heap.pos]) {
						Sample.INSTANCE.play( Assets.Sounds.WATER, 0.8f, Random.Float( 1f, 1.45f ) );
					} else if (Dungeon.level.map[heap.pos] == Terrain.EMPTY_SP) {
						Sample.INSTANCE.play( Assets.Sounds.STURDY, 0.8f, Random.Float( 1.16f, 1.25f ) );
					} else if (Dungeon.level.map[heap.pos] == Terrain.GRASS
							|| Dungeon.level.map[heap.pos] == Terrain.EMBERS
							|| Dungeon.level.map[heap.pos] == Terrain.FURROWED_GRASS){
						Sample.INSTANCE.play( Assets.Sounds.GRASS, 0.8f, Random.Float( 1.16f, 1.25f ) );
					} else if (Dungeon.level.map[heap.pos] == Terrain.HIGH_GRASS) {
						Sample.INSTANCE.play( Assets.Sounds.STEP, 0.8f, Random.Float( 1.16f, 1.25f ) );
					} else {
						Sample.INSTANCE.play( Assets.Sounds.STEP, 0.8f, Random.Float( 1.16f, 1.25f ));
					}
				}
			}
		}

		if (visible && glowing != null) {
			if (glowUp && (phase += Game.elapsed) > glowing.period) {
				
				glowUp = false;
				phase = glowing.period;
				
			} else if (!glowUp && (phase -= Game.elapsed) < 0) {
				
				glowUp = true;
				phase = 0;
				
			}
			
			float value = phase / glowing.period * 0.6f;
			
			rm = gm = bm = 1 - value;
			ra = glowing.red * value;
			ga = glowing.green * value;
			ba = glowing.blue * value;
		}
	}

	public static int pick( IconEntry entry, int x, int y ) {
		SmartTexture tx = TextureCache.get( entry.atlas );
		return tx.getPixel( entry.x(0) + x, entry.y(0) + y );
	}
	
	public static class Glowing {
		
		public int color;
		public float red;
		public float green;
		public float blue;
		public float period;
		
		public Glowing( int color ) {
			this( color, 1f );
		}
		
		public Glowing( int color, float period ) {

			this.color = color;

			red = (color >> 16) / 255f;
			green = ((color >> 8) & 0xFF) / 255f;
			blue = (color & 0xFF) / 255f;
			
			this.period = period;
		}
	}
}
