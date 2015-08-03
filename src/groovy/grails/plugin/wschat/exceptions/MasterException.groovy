package grails.plugin.wschat.exceptions

class MasterException extends Exception {
	@Override
	public Throwable fillInStackTrace() {
		// do nothing
		return this
	}
}
