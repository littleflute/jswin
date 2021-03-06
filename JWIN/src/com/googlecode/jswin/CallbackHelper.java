package com.googlecode.jswin;

public abstract class CallbackHelper implements AutoCloseable {
	protected abstract int callback(int[] params);

	int numParams;

	private int lpucCallbackMem = CallProc.alloc(CallProc.CALLBACK_SIZE);
	private int lpContext = CallProc.newRef(new Callback() {
		@Override
		public int callback(int lpcParam) {
			int[] params = new int[numParams];
			for (int i = 0; i < numParams; i ++) {
				params[i] = CallProc.getMem4(lpcParam + i * 4);
			}
			return CallbackHelper.this.callback(params);
		}
	});

	protected CallbackHelper(int numParams) {
		this.numParams = numParams;
		CallProc.prepareCallback(lpucCallbackMem, lpContext);
	}

	public int getCallbackMem() {
		return lpucCallbackMem;
	}

	@Override
	public void close() {
		CallProc.delRef(lpContext);
		CallProc.free(lpucCallbackMem);
	}
}