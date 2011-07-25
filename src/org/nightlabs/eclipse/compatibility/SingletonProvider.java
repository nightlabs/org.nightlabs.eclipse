package org.nightlabs.eclipse.compatibility;

public class SingletonProvider<C> {
	private C instance;
	
	public SingletonProvider(Class<C> _class) {
		try {
			instance =  _class.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public SingletonProvider(C instance) {
		this.instance = instance;
	}
	
	public C getInstance() {
		return instance;
	}
}
