package de.tu_darmstadt.informatik.tk.scopviz.ui.mapView;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.jxmapviewer.util.ProjectProperties;
import org.jxmapviewer.viewer.AbstractTileFactory;
import org.jxmapviewer.viewer.Tile;
import org.jxmapviewer.viewer.TileCache;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.util.GeoUtil;

/**
 * Custom tile factory for handling connection problems
 * 
 * @author Dominik Renkel
 *
 */
public class CustomTileFactory extends AbstractTileFactory {

	/**
	 * Note that the name and version are actually set by Gradle so there is no
	 * need to bump a version manually when new release is made.
	 */
	private static final String DEFAULT_USER_AGENT = ProjectProperties.INSTANCE.getName() + "/"
			+ ProjectProperties.INSTANCE.getVersion();

	/**
	 * userAgent needed to request data from OpenStreetMap servers
	 */
	private String userAgent = DEFAULT_USER_AGENT;

	/**
	 * standard thread count, that are used to load tiles
	 */
	private int threadPoolSize = 4;

	/**
	 * Handling Threads
	 */
	private ExecutorService service;

	/**
	 * saves loaded tiles
	 */
	private Map<String, Tile> tileMap = new HashMap<String, Tile>();

	/**
	 * Actual Loaded Tile chache
	 */
	private TileCache cache = new TileCache();

	/**
	 * constructor with super reference
	 * 
	 * @param info
	 */
	public CustomTileFactory(TileFactoryInfo info) {
		super(info);
	}

	/**
	 * Returns the tile that is located at the given tilePoint for this zoom.
	 * For example, if getMapSize() returns 10x20 for this zoom, and the
	 * tilePoint is (3,5), then the appropriate tile will be located and
	 * returned.
	 */
	@Override
	public CustomTile getTile(int x, int y, int zoom) {
		return getTile(x, y, zoom, true);
	}

	/**
	 * Get a tile like above, but load image when not already in TileCache
	 * 
	 * @param tpx
	 * @param tpy
	 * @param zoom
	 * @param eagerLoad
	 *            load all tiles with same priority
	 * @return
	 */
	private CustomTile getTile(int tpx, int tpy, int zoom, boolean eagerLoad) {
		// wrap the tiles horizontally --> mod the X with the max width
		// and use that
		int tileX = tpx;// tilePoint.getX();
		int numTilesWide = (int) getMapSize(zoom).getWidth();
		if (tileX < 0) {
			tileX = numTilesWide - (Math.abs(tileX) % numTilesWide);
		}

		tileX = tileX % numTilesWide;
		int tileY = tpy;
		String url = getInfo().getTileUrl(tileX, tileY, zoom);

		Tile.Priority pri = Tile.Priority.High;
		if (!eagerLoad) {
			pri = Tile.Priority.Low;
		}
		CustomTile tile;

		if (!tileMap.containsKey(url)) {
			if (!GeoUtil.isValidTile(tileX, tileY, zoom, getInfo())) {
				tile = new CustomTile(tileX, tileY, zoom);
			} else {
				tile = new CustomTile(tileX, tileY, zoom, url, pri, this);
				startLoading(tile);
			}
			tileMap.put(url, tile);
		} else {
			tile = (CustomTile) tileMap.get(url);
			// if its in the map but is low and isn't loaded yet
			// but we are in high mode
			if (tile.getPriority() == Tile.Priority.Low && eagerLoad && !tile.isLoaded()) {

				// tile.promote();
				promote(tile);
			}
		}

		return tile;
	}

	/** ==== threaded tile loading stuff === */
	/**
	 * Thread pool for loading the tiles
	 */
	private BlockingQueue<Tile> tileQueue = new PriorityBlockingQueue<Tile>(5, new Comparator<Tile>() {
		@Override
		public int compare(Tile o1, Tile o2) {
			if (o1.getPriority() == Tile.Priority.Low && o2.getPriority() == Tile.Priority.High) {
				return 1;
			}
			if (o1.getPriority() == Tile.Priority.High && o2.getPriority() == Tile.Priority.Low) {
				return -1;
			}
			return 0;

		}
	});

	/**
	 * @return the tile cache
	 */
	@Override
	public TileCache getTileCache() {
		return cache;
	}

	/**
	 * @param cache
	 *            the tile cache
	 */
	@Override
	public void setTileCache(TileCache cache) {
		this.cache = cache;
	}

	/**
	 * Subclasses may override this method to provide their own executor
	 * services. This method will be called each time a tile needs to be loaded.
	 * Implementations should cache the ExecutorService when possible.
	 * 
	 * @return ExecutorService to load tiles with
	 */
	@Override
	protected synchronized ExecutorService getService() {
		if (service == null) {
			// System.out.println("creating an executor service with a
			// threadpool of size " + threadPoolSize);
			service = Executors.newFixedThreadPool(threadPoolSize, new ThreadFactory() {
				private int count = 0;

				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r, "tile-pool-" + count++);
					t.setPriority(Thread.MIN_PRIORITY);
					t.setDaemon(true);
					return t;
				}
			});
		}
		return service;
	}

	@Override
	public void dispose() {
		if (service != null) {
			service.shutdown();
			service = null;
		}
	}

	/**
	 * Set the number of threads to use for loading the tiles. This controls the
	 * number of threads used by the ExecutorService returned from getService().
	 * Note, this method should be called before loading the first tile. Calls
	 * after the first tile are loaded will have no effect by default.
	 * 
	 * @param size
	 *            the thread pool size
	 */
	@Override
	public void setThreadPoolSize(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException(
					"size invalid: " + size + ". The size of the threadpool must be greater than 0.");
		}
		threadPoolSize = size;
	}

	@Override
	protected synchronized void startLoading(Tile tile) {
		if (tile.isLoading()) {
			// System.out.println("already loading. bailing");
			return;
		}
		tile.setLoading(true);
		try {
			tileQueue.put(tile);
			getService().submit(createTileRunner(tile));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Increase the priority of this tile so it will be loaded sooner.
	 * 
	 * @param tile
	 *            the tile
	 */
	public synchronized void promote(CustomTile tile) {
		if (tileQueue.contains(tile)) {
			try {
				tileQueue.remove(tile);
				tile.setPriority(Tile.Priority.High);
				tileQueue.put(tile);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	protected Runnable createTileRunner(Tile tile) {
		return new CustomTileRunner();
	}

	/**
	 * An inner class which actually loads the tiles. Used by the thread queue.
	 * Subclasses can override this if necessary.
	 */
	private class CustomTileRunner implements Runnable {
		/**
		 * Gets the full URI of a tile.
		 * 
		 * @param tile
		 *            the tile
		 * @throws URISyntaxException
		 *             if the URI is invalid
		 * @return a URI for the tile
		 */
		protected URI getURI(Tile tile) throws URISyntaxException {
			if (tile.getURL() == null) {
				return null;
			}
			return new URI(tile.getURL());
		}

		/**
		 * implementation of the Runnable interface.
		 */
		@Override
		public void run() {
			/*
			 * 3 strikes and you're out. Attempt to load the url. If it fails,
			 * decrement the number of tries left and try again. Log failures.
			 * If I run out of try s just get out. This way, if there is some
			 * kind of serious failure, I can get out and let other tiles try to
			 * load.
			 */
			final CustomTile tile = (CustomTile) tileQueue.remove();

			int trys = 3;
			while (!tile.isLoaded() && trys >= 0) {
				try {
					BufferedImage img;
					URI uri = getURI(tile);
					img = cache.get(uri);

					if (img == null) {
						// put image in chache when loaded
						byte[] bimg = cacheInputStream(uri.toURL());
						img = ImageIO.read(new ByteArrayInputStream(bimg));
						cache.put(uri, bimg, img);
						img = cache.get(uri);
					}
					if (img != null) {
						// set image to tile when loaded
						final BufferedImage i = img;
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								tile.image = new SoftReference<BufferedImage>(i);
								tile.setLoaded(true);
								tile.setLoading(false);
								fireTileLoadedEvent(tile);
							}
						});
					} else {
						// something has not been loaded correctly
						trys--;
					}

				} catch (OutOfMemoryError memErr) {
					// Cache out of memory order more
					cache.needMoreMemory();

				} catch (Throwable e) {
					// tile could not be loaded

					if (trys == 0) {
						if (!tileQueue.contains(tile)) {
							tileQueue.add(tile);
						}
					} else {
						trys--;
					}
				}
			}
		}

		// fetch data from OpenStreetMap server
		private byte[] cacheInputStream(URL url) throws IOException {
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-Agent", userAgent);
			InputStream ins = connection.getInputStream();

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] buf = new byte[256];
			while (true) {
				int n = ins.read(buf);
				if (n == -1)
					break;
				bout.write(buf, 0, n);
			}
			ins.close();

			byte[] data = bout.toByteArray();
			bout.close();
			return data;
		}
	}

}
