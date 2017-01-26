package controller;

import controller.ICoreExtension;

public class CoreExtension implements ICoreExtension{

	@Override
	public String getStorageRoot() {
		return ".";
	}
	
}

