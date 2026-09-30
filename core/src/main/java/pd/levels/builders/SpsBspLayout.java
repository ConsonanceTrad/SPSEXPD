/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package pd.levels.builders;

import com.watabou.utils.Graph;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

/** Builds the 48x48 BSP room graph used by SPS-PD 0.9.8 ordinary floors. */
public final class SpsBspLayout {

	public static final int MIN_ROOM_SIZE = 8;
	public static final int MAX_ROOM_SIZE = 10;

	public static Result generate(int width, int height, int maxAttempts) {
		return generate(width, height, maxAttempts, MIN_ROOM_SIZE, MAX_ROOM_SIZE);
	}

	public static Result generate(int width, int height, int maxAttempts,
			int minRoomSize, int maxRoomSize) {
		if (width < 16 || height < 16 || maxAttempts < 1) {
			throw new IllegalArgumentException("invalid SPS BSP layout bounds");
		}
		if (minRoomSize < 5 || maxRoomSize < minRoomSize) {
			throw new IllegalArgumentException("invalid SPS BSP room sizes");
		}
		for (int attempt = 1; attempt <= maxAttempts; attempt++) {
			Result result = generateOnce(width, height, minRoomSize, maxRoomSize);
			if (result != null) {
				result.attempts = attempt;
				return result;
			}
		}
		return null;
	}

	/** Builds the dedicated four-leg pursuit loop used by legacy depth 41. */
	public static Result generateThiefCatch(int width, int height, int maxAttempts) {
		if (width < 16 || height < 16 || maxAttempts < 1) {
			throw new IllegalArgumentException("invalid SPS thief-pursuit layout bounds");
		}
		for (int attempt = 1; attempt <= maxAttempts; attempt++) {
			Result result = generateThiefCatchOnce(width, height);
			if (result != null) {
				result.attempts = attempt;
				return result;
			}
		}
		return null;
	}

	private static Result generateThiefCatchOnce(int width, int height) {
		ArrayList<Room> rooms = createRooms(width, height, MIN_ROOM_SIZE, MAX_ROOM_SIZE);
		if (rooms == null) return null;

		Room entrance = null;
		for (int retry = 0; retry <= 20; retry++) {
			Room candidate = Random.element(rooms);
			if (candidate.width() >= 4 && candidate.height() >= 4
					&& candidate.top > 0 && candidate.top < 12) {
				entrance = candidate;
				break;
			}
		}
		if (entrance == null) return null;
		entrance.type = Type.ENTRANCE;

		Room current = null;
		Room last = entrance;
		Room kingRoom = null;
		for (int leg = 0; leg <= 4; leg++) {
			if (leg < 4) {
				current = null;
				for (int retry = 0; retry <= 20; retry++) {
					Room candidate = Random.element(rooms);
					Graph.buildDistanceMap(rooms, candidate);
					if (candidate.type == Type.NULL && last.distance() == 2
							&& candidate.intersect(entrance).isEmpty()) {
						current = candidate;
						break;
					}
				}
				if (current == null) return null;
				current.type = Type.STANDARD;
			} else {
				current = entrance;
			}

			Graph.buildDistanceMap(rooms, current);
			List<Room> path = Graph.buildPath(rooms, last, current);
			if (path == null) return null;
			Graph.setPrice(path, last.distance());
			path = Graph.buildPath(rooms, last, current);
			if (path == null) return null;
			connectPath(last, path, null);

			if (leg == 4) {
				ArrayList<Room> candidates = new ArrayList<>();
				for (Room room : last.neighbours) {
					if (room.type == Type.NULL && room.connected.isEmpty()
							&& !room.neighbours.contains(entrance)) candidates.add(room);
				}
				if (candidates.isEmpty()) return null;
				kingRoom = Random.element(candidates);
				kingRoom.connect(last);
				kingRoom.type = Type.STANDARD;
			}
			last = current;
		}

		LinkedHashSet<Room> connected = new LinkedHashSet<>();
		for (Room room : rooms) {
			if (room.type == Type.NULL && !room.connected.isEmpty()) room.type = Type.TUNNEL;
			if (room.type != Type.NULL) connected.add(room);
		}
		for (Room neighbour : entrance.neighbours) {
			if (neighbour.bottom == entrance.top && neighbour.type != Type.NULL) return null;
		}
		return new Result(rooms, connected, entrance, entrance, connected.size(), kingRoom);
	}

	private static Result generateOnce(int width, int height, int minRoomSize, int maxRoomSize) {
		ArrayList<Room> rooms = createRooms(width, height, minRoomSize, maxRoomSize);
		if (rooms == null) return null;

		ArrayList<Room> endpointCandidates = new ArrayList<>();
		for (Room room : rooms) {
			if (room.width() >= 5 && room.height() >= 5) endpointCandidates.add(room);
		}
		if (endpointCandidates.size() < 2) return null;

		Room entrance = null;
		Room exit = null;
		int minDistance = (int)Math.sqrt(rooms.size());
		for (int retry = 0; retry <= 15; retry++) {
			Room candidateEntrance = Random.element(endpointCandidates);
			Room candidateExit = Random.element(endpointCandidates);
			if (candidateEntrance == candidateExit) continue;
			Graph.buildDistanceMap(rooms, candidateExit);
			if (candidateEntrance.distance() >= minDistance) {
				entrance = candidateEntrance;
				exit = candidateExit;
				break;
			}
		}
		if (entrance == null || exit == null) return null;

		LinkedHashSet<Room> connected = new LinkedHashSet<>();
		connected.add(entrance);
		Graph.buildDistanceMap(rooms, exit);
		List<Room> path = Graph.buildPath(rooms, entrance, exit);
		if (path == null) return null;
		connectPath(entrance, path, connected);

		Graph.setPrice(path, entrance.distance());
		Graph.buildDistanceMap(rooms, exit);
		path = Graph.buildPath(rooms, entrance, exit);
		if (path == null) return null;
		connectPath(entrance, path, connected);

		int target = (int)(rooms.size() * Random.Float(0.5f, 0.7f));
		int remainingTries = rooms.size() * rooms.size();
		while (connected.size() < target && remainingTries-- > 0) {
			Room from = Random.element(connected);
			if (from.neighbours.isEmpty()) continue;
			Room to = Random.element(from.neighbours);
			if (connected.add(to)) from.connect(to);
		}
		if (connected.size() < target) return null;

		return new Result(rooms, connected, entrance, exit, target, null);
	}

	private static ArrayList<Room> createRooms(int width, int height,
			int minRoomSize, int maxRoomSize) {
		ArrayList<Room> rooms = new ArrayList<>();
		split(new Rect(0, 0, width - 1, height - 1), rooms, minRoomSize, maxRoomSize);
		if (rooms.size() < 20) return null;
		for (int i = 0; i < rooms.size() - 1; i++) {
			for (int j = i + 1; j < rooms.size(); j++) rooms.get(i).addNeighbour(rooms.get(j));
		}
		return rooms;
	}

	private static void connectPath(Room entrance, List<Room> path,
			LinkedHashSet<Room> connected) {
		Room previous = entrance;
		for (Room next : path) {
			previous.connect(next);
			if (connected != null) connected.add(next);
			previous = next;
		}
	}

	private static void split(Rect rect, ArrayList<Room> rooms,
			int minRoomSize, int maxRoomSize) {
		int w = rect.width();
		int h = rect.height();
		if (w > maxRoomSize && h < minRoomSize) {
			int x = Random.Int(rect.left + 4, rect.right - 3);
			split(new Rect(rect.left, rect.top, x, rect.bottom), rooms, minRoomSize, maxRoomSize);
			split(new Rect(x, rect.top, rect.right, rect.bottom), rooms, minRoomSize, maxRoomSize);
		} else if (h > maxRoomSize && w < minRoomSize) {
			int y = Random.Int(rect.top + 4, rect.bottom - 3);
			split(new Rect(rect.left, rect.top, rect.right, y), rooms, minRoomSize, maxRoomSize);
			split(new Rect(rect.left, y, rect.right, rect.bottom), rooms, minRoomSize, maxRoomSize);
		} else if ((Random.Float() <= (float)(minRoomSize * minRoomSize) / rect.square()
				&& w <= maxRoomSize && h <= maxRoomSize && w > 5 && h > 5)
				|| w < minRoomSize || h < minRoomSize) {
			rooms.add(new Room(rect));
		} else if (Random.Float() < (float)(w - 2) / (w + h - 4)) {
			int x = Random.Int(rect.left + 4, rect.right - 3);
			split(new Rect(rect.left, rect.top, x, rect.bottom), rooms, minRoomSize, maxRoomSize);
			split(new Rect(x, rect.top, rect.right, rect.bottom), rooms, minRoomSize, maxRoomSize);
		} else {
			int y = Random.Int(rect.top + 4, rect.bottom - 3);
			split(new Rect(rect.left, rect.top, rect.right, y), rooms, minRoomSize, maxRoomSize);
			split(new Rect(rect.left, y, rect.right, rect.bottom), rooms, minRoomSize, maxRoomSize);
		}
	}

	public static final class Result {
		public final ArrayList<Room> rooms;
		public final LinkedHashSet<Room> connected;
		public final Room entrance;
		public final Room exit;
		public final Room bossRoom;
		public final int targetConnected;
		public int attempts;

		private Result(ArrayList<Room> rooms, LinkedHashSet<Room> connected,
				Room entrance, Room exit, int targetConnected, Room bossRoom) {
			this.rooms = rooms;
			this.connected = connected;
			this.entrance = entrance;
			this.exit = exit;
			this.bossRoom = bossRoom;
			this.targetConnected = targetConnected;
		}
	}

	public static final class Room extends Rect implements Graph.Node {
		public final LinkedHashSet<Room> neighbours = new LinkedHashSet<>();
		public final LinkedHashMap<Room, Door> connected = new LinkedHashMap<>();
		public Type type = Type.NULL;
		private int distance;
		private int price = 1;

		private Room(Rect rect) {
			super(rect);
		}

		private void addNeighbour(Room other) {
			Rect overlap = intersect(other);
			if ((overlap.width() == 0 && overlap.height() >= 3)
					|| (overlap.height() == 0 && overlap.width() >= 3)) {
				neighbours.add(other);
				other.neighbours.add(this);
			}
		}

		public void connect(Room other) {
			if (!connected.containsKey(other)) {
				connected.put(other, null);
				other.connected.put(this, null);
			}
		}

		public int randomCell(int width, int margin) {
			int minX = left + 1 + margin;
			int maxX = right - 1 - margin;
			int minY = top + 1 + margin;
			int maxY = bottom - 1 - margin;
			if (minX > maxX || minY > maxY) return -1;
			return Random.IntRange(minX, maxX) + Random.IntRange(minY, maxY) * width;
		}

		@Override
		public int distance() {
			return distance;
		}

		@Override
		public void distance(int value) {
			distance = value;
		}

		@Override
		public int price() {
			return price;
		}

		@Override
		public void price(int value) {
			price = value;
		}

		@Override
		public Collection<Room> edges() {
			return neighbours;
		}
	}

	public static final class Door {
		public enum Type { EMPTY, TUNNEL, REGULAR, UNLOCKED, HIDDEN, BARRICADE, LOCKED, ONEWAY }

		public final int x;
		public final int y;
		public Type type = Type.EMPTY;

		public Door(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void set(Type type) {
			if (type.compareTo(this.type) > 0) this.type = type;
		}
	}

	public enum Type {
		NULL, STANDARD, ENTRANCE, EXIT, TUNNEL, PASSAGE, SHOP,
		BLACKSMITH,
		JUNGLE, MATERIAL, LIBRARY, COOKING, VAULT, TRAPS, STORAGE,
		BARRICADED, TENTROOM, MAGIC_WELL, GARDEN, CRYPT, STATUE,
		POOL, RUIN_ROOM, PRISON_PIT, MEMORY, HIDE_SHOP, WISH_POOL, GLASSROOM,
		TENGU_BOX, POWER_ROOM, WISDOM_ROOM
	}

	private SpsBspLayout() {
	}
}
