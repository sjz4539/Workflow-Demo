package model.resource.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import model.resource.task.FileResource;
import controller.Core;
import controller.io.FileOpsStatus;
import controller.io.LocalFileOps;
import controller.library.Library;

/**
 * Organizes and monitors local copies of remotely-stored resources.
 * 
 * @author Test
 *
 */
public class ResourceCache {

	private HashMap<Library, String> libMap;
	private HashMap<Library, HashMap<FileResource, String>> fileMap;
	private String root;
	private boolean initComplete = false;
	
	public ResourceCache(String r){
		libMap = new HashMap<Library, String>();
		fileMap = new HashMap<Library, HashMap<FileResource, String>>();
		root = r;
		init();
	}
	
	public boolean init(){
		if(!initComplete){
			
			File file = new File(root);
			
			if( (!file.exists() || !file.isDirectory()) && file.mkdirs()){
				initComplete = true;
			}else if(file.isDirectory()){
				if(recursiveDeleteAll() && file.mkdir()){
					initComplete = true;
				}else{
					initComplete = false;
				}
			}else{
				initComplete = false;
			}
			
		}
		
		return initComplete;
	}
	
	//called by library to secure a storage location before downloading a remotely-stored resource
	public String addFile(FileResource r){
		if(init()){
			if(r.getParentLibrary().getType() == Library.Type.REMOTE && !libMap.containsKey(r.getParentLibrary())){
				libMap.put(r.getParentLibrary(), getLibraryKey(r.getParentLibrary()));
				File file = new File(root + File.separator + libMap.get(r.getParentLibrary()));
				if(!file.exists() && !file.mkdir()){
						return null;
				}
			}
			if(!fileMap.containsKey(r.getParentLibrary())){
				fileMap.put(r.getParentLibrary(), new HashMap<FileResource, String>());
			}
			fileMap.get(r.getParentLibrary()).put(r, generatePath(r));
			return fileMap.get(r.getParentLibrary()).get(r);
		}
		return null;
	}
	
	public boolean containsFileRecord(FileResource r){
		return fileMap.containsKey(r.getParentLibrary()) && fileMap.get(r.getParentLibrary()).containsKey(r);
	}
	
	public boolean containsFileData(FileResource r){
		return containsFileRecord(r) && getFile(r) != null;
	}
	
	public String getPath(FileResource r){
		if(containsFileRecord(r)){
			return fileMap.get(r.getParentLibrary()).get(r);
		}else{
			return null;
		}
	}
	
	public String getAbsolutePath(FileResource r){
		if(containsFileRecord(r)){
			return new File(getPath(r)).getAbsolutePath();
		}else{
			return "";
		}
	}
	
	public File getFile(FileResource r){
		File res = new File(fileMap.get(r.getParentLibrary()).get(r));
		if(res.exists() && res.isFile()){
			return res;
		}else{
			return null;
		}
	}
	
	public ArrayList<FileResource> getContents(){
		ArrayList<FileResource> resources = new ArrayList<FileResource>();
		
		for(Library l : fileMap.keySet()){
			for(FileResource r : fileMap.get(l).keySet()){
				resources.add(r);
			}
		}
		
		return resources;
	}
	
	public boolean removeFile(FileResource r){
		if(LocalFileOps.deleteFile(getPath(r)).getCode() == FileOpsStatus.Code.SUCCESS){
			//Core.getProcessMonitor().remove(r); //if we deleted this resource, it's obviously not open.
			fileMap.get(r.getParentLibrary()).remove(r);
			return true;
		}else{
			return false;
		}
	}
	
	public void clear(){
		fileMap.clear();
	}
	
	public boolean deleteAll(){
		
		boolean result = true;
		
		for(Library l : fileMap.keySet()){
			if(l.getType() == Library.Type.REMOTE){
				
				boolean libResult = true;
				
				ArrayList<FileResource> keys = new ArrayList<FileResource>();
				keys.addAll(fileMap.get(l).keySet());
				
				//try to remove each file in the cache one by one
				for(FileResource r : keys){
					if(!removeFile(r)){
						libResult = false;
					}
				}
				
				if(libResult){ //if all cached files for this library were removed, remove the library folder.
					LocalFileOps.deleteFile(root + File.separator + libMap.get(l));
					libMap.remove(l);
				}
				
				result = libResult;
			}
		}
		
		if(result){
			fileMap.clear();
		}
		
		return result;
	}
	
	private boolean recursiveDeleteAll(){
		return recursiveDelete(new File(root));
	}
	
	private boolean recursiveDelete(File target){
		if(target.isFile()){
			return target.delete();
		}else{
			for(File f : target.listFiles()){
				if(!recursiveDelete(f)){
					return false;
				}
			}
			return target.delete();
		}
	}
	
	public boolean checkUnsavedResources(){
		
		for(Library l : fileMap.keySet()){
			if(l.getType() == Library.Type.REMOTE){
				for(FileResource r : fileMap.get(l).keySet()){
					if(r.needsSave()){
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
//============================================================
	
	private String generatePath(FileResource r){
		String path = "";
		
		if(r.getParentLibrary().getType() == Library.Type.LOCAL){
			return r.getAbsolutePath();
		}else{
			path = root + File.separator + getLibraryKey(r.getParentLibrary()) + File.separator + r.getPath();
		}
		
		if(!fileExists(path)){
			return path;
		}else{
			int i = 0;
			int splitPos = r.getPath().indexOf(".");
			String name = r.getPath();
			String extension = "";
			
			if(splitPos != -1){
				extension = name.substring(splitPos);
				name = name.substring(0, splitPos);
			}
			
			while(fileExists(name + "_" + i + extension)){
				i++;
			}
			
			return root + File.separator + getLibraryKey(r.getParentLibrary()) + File.separator + name + "_" + i + extension;
		}
	}
	
	private String getLibraryKey(Library l){
		if(libMap.containsKey(l)){
			return libMap.get(l);
		}else if(!libMap.containsValue(l.getName())){
			return l.getName();
		}else{
			int i = 0;
			while(libMap.containsValue(l.getName() + "_" + i)){
				i++;
			}
			return l.getName() + "_" + i;
		}
	}
	
	private boolean fileExists(String p){
		return new File(p).exists();
	}
	
	public static String genCacheFolder(){
		
		String ret = null;
		
		while(ret == null || new File(ret).isDirectory()){
			ret = Core.getCoreExtension().getStorageRoot() + File.separator + "Cache_" + UUID.randomUUID();
		}
		
		return ret;
		
	}
	
}
