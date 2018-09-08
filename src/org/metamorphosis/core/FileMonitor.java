package org.metamorphosis.core;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

public class FileMonitor {

	private List<FileListener> listeners = new ArrayList<FileListener>();
	private File directory;

	public FileMonitor(File directory) {
	    this.directory = directory;
	}
	
	@SuppressWarnings("unchecked")
	public void watch() {
		new Thread(new Runnable() {
		  public void run() {
			try {
				WatchService watcher = FileSystems.getDefault().newWatchService();
				Path dir = Paths.get(directory.getAbsolutePath());
				dir.register(watcher, ENTRY_CREATE,ENTRY_DELETE);
				WatchKey key;
				while(true) {
					try {
						key = watcher.take();
					} catch (InterruptedException ex) {
						return;
					}
					for(WatchEvent<?> event : key.pollEvents()) {
						WatchEvent.Kind<?> kind = event.kind();
						WatchEvent<Path> ev = (WatchEvent<Path>) event;
						String file = ev.context().toString();
						if(kind == OVERFLOW) {
							continue;
						} else if(kind == ENTRY_CREATE) {
						  for(FileListener listener : listeners) listener.onFileCreated(file);
						}
						else if(kind == ENTRY_DELETE) {
						  for(FileListener listener : listeners) listener.onFileDeleted(file);
						}
					}
					if(!key.reset()) break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		  }
		}).start();
	}
	
	public FileMonitor addListener(FileListener listener) {
		listeners.add(listener);
		return this;
	}
	
	public void removeListener(FileListener listener) {
		listeners.remove(listener);
	}
}