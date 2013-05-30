package net.engio.daoism.dao.query;


public class Options {
	
	/**
	 * Get an instance of the default access configuration
	 * @return
	 */
	public static AccessPlan Default(){
		return new AccessPlan();
	}
	
	/**
	 * Get a new instance of AccessPlan with default values
	 * @return
	 */
	public static AccessPlan Refresh(){
		return Default().Refresh();
	}
	
	public static AccessPlan Lock(LockType lockMode){
		return Default().Lock(lockMode);
	}


    public static class AccessPlan{
		
		private LockType lockMode = LockType.Default;
		
		private boolean refresh = false;


		public LockType getLockMode() {
			return lockMode;
		}

		
		public AccessPlan Lock(LockType lockMode) {
			this.lockMode = lockMode;
			return this;
		}

		
		public boolean isRefresh() {
			return refresh;
		}

		
		public AccessPlan Refresh() {
			this.refresh = true;
			return this;
		}


	}

}
