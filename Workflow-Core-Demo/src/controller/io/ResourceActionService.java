package controller.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import controller.Core;
import controller.SimpleHandler;
import controller.library.Library;
import model.account.Account;
import model.resource.task.FileResource;

/**
 * Handles requests for actions that may rely on the transfer
 * of resources, as well as the initiation and monitoring of
 * any such transfers related to the requested action.
 * 
 * @author Test
 *
 */
public class ResourceActionService extends Thread{

	private List<FileResource> resources;
	private FileResource resource;
	private Action action;
	private Account account;
	private boolean cancel = false;
	
	private SimpleHandler successHandler, failureHandler;
	private ArrayList<ResourceActionService> running, failures;
	
	public enum Action{
		GET, SEND 
	}

//===================== Static Job Functions =============================

	public static ResourceActionService loadResources(List<FileResource> resources, SimpleHandler success, SimpleHandler failed){
		ResourceActionService newService = new ResourceActionService(resources, Action.GET, success, failed);
		newService.start();
		return newService;
	}
	
	public static ResourceActionService loadResource(FileResource resource, SimpleHandler success, SimpleHandler failed){
		if(resource.getParentLibrary().getType() == Library.Type.LOCAL){
			File file = new File(resource.getAbsolutePath());
			if(file.exists() && file.isFile()){
				success.handle();
				return null;
			}else{
				failed.handle();
				return null;
			}
		}else{
			ResourceActionService newService = new ResourceActionService(resource, Action.GET, success, failed);
			newService.start();
			return newService;
		}
	}
	
	public static ResourceActionService saveResources(List<FileResource> resources, SimpleHandler success, SimpleHandler failed){
		ResourceActionService newService = new ResourceActionService(resources, Action.SEND, success, failed);
		newService.start();
		return newService;
	}
	
	public static ResourceActionService saveResource(FileResource resource, SimpleHandler success,  SimpleHandler failed){
		if(resource.getParentLibrary().getType() == Library.Type.LOCAL){
			success.handle();
			return null;
		}else{
			ResourceActionService newService = new ResourceActionService(resource, Action.SEND, success, failed);
			newService.start();
			return newService;
		}
	}
	
//===================== Instance Functions ===============================
	
	public ResourceActionService(FileResource r, Action act){
		this(r, act, null, null);
	}
	
	public ResourceActionService(FileResource r, Action act, SimpleHandler success, SimpleHandler failed){
		resource = r;
		account = r.getParentLibrary().getAccount();
		action = act;
		successHandler = success;
		failureHandler = failed;
	}
	
	public ResourceActionService(List<FileResource> r, Action act){
		this(r, act, null, null);
	}
	
	public ResourceActionService(List<FileResource> r, Action act, SimpleHandler success, SimpleHandler failed){
		resources = r;
		action = act;
		successHandler = success;
		failureHandler = failed;
	}

	public void setOnSucceeded(SimpleHandler handler){
		successHandler = handler;
	}
	
	public void setOnFailed(SimpleHandler handler){
		failureHandler = handler;
	}
	
	public void succeeded(){
		if(successHandler != null){
			successHandler.handle();
		}
	}
	
	public void failed(){
		if(failureHandler != null){
			failureHandler.handle();
		}
	}
	
	public void cancel(){
		//propagate the request to all running threads
		if(running != null){
			for(ResourceActionService service : running){
				service.cancel();
			}
		}
		//if we have a list of failed threads, execute the failure handler, else success handler
		if(failures != null && !failures.isEmpty()){
			failed();
		}
		//since we can't force a call to the dropbox or google APIs to halt, we have no choice but to simply stop the thread.
		this.stop();
	}
	
	public void run(){
		boolean result = false;
		FileOpsStatus status;
		
		if(resources != null){ //Given a list of resources to download or upload
		
			running = new ArrayList<ResourceActionService>();
			failures = new ArrayList<ResourceActionService>();
			
			int max = Integer.parseInt(Core.getCore().getProperty(action == ResourceActionService.Action.GET ? Core.PROP_MAX_SIMULTANEOUS_DOWNLOADS : Core.PROP_MAX_SIMULTANEOUS_UPLOADS));
			
			synchronized(running){
				//spawn new tasks for each resource waiting to be transferred
				while(resources.size() > 0 && !cancel){
					final ResourceActionService nextService = new ResourceActionService(resources.remove(0), action);
					nextService.setOnSucceeded(new SimpleHandler(){
						public void handle() {
							synchronized(running){ 
								running.remove(nextService);
								running.notifyAll();
							} 
						}
					});
					nextService.setOnFailed(new SimpleHandler(){
						public void handle() {
							running.remove(nextService);
							failures.add(nextService);
							running.notifyAll();
						} 
					});
					running.add(nextService);
					nextService.start();
					
					while(running.size() >= max && !cancel){
						//wait for space to open up for new tasks
						try {
							running.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				//wait for all tasks to finish
				while(running.size() > 0 && !cancel){
					try {
						running.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
			
			result = failures.isEmpty();
			
		}else{ //given a single resource to download or upload
			
			switch(action){
				case GET:
					resource.setStatus(FileResource.Status.DOWNLOADING);
					if(!Core.getResourceCache().containsFileRecord(resource)){
						Core.getResourceCache().addFile(resource);
					}
					status = account.getFileOps().loadFile(account, resource.getAbsolutePath(), Core.getResourceCache().getPath(resource));
					if(status.getCode() == FileOpsStatus.Code.SUCCESS){
						//update the last saved version
						File file = new File(Core.getResourceCache().getAbsolutePath(resource));
						resource.setLastSavedTime(file.lastModified());
						resource.setStatus(FileResource.Status.NORMAL);
						result = true;
						break;
					}else if(status.getCode() == FileOpsStatus.Code.FILE_NOT_FOUND){
						resource.setStatus(FileResource.Status.MISSING, status.getMessage());
						break;
					}else{
						resource.setStatus(FileResource.Status.ERROR, status.getMessage());
						break;
					}
				case SEND:
					status = account.getFileOps().saveFile(account, Core.getResourceCache().getAbsolutePath(resource), resource.getAbsolutePath());
					if(status.getCode() == FileOpsStatus.Code.SUCCESS){
						//update the last saved version
						File file = new File(Core.getResourceCache().getAbsolutePath(resource));
						resource.setLastSavedTime(file.lastModified());
						resource.setStatus(FileResource.Status.NORMAL);
						result = true;
						break;
					}else{
						resource.setStatus(FileResource.Status.ERROR, status.getMessage());
						break;
					}
				default:
					break;
			}
			
		}
		
		if(!cancel){
			if(result){
				succeeded();
			}else{
				failed();
			}
		}
		
	}
	
}
