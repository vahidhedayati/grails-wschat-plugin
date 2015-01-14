package grails.plugin.wschat

class ChatUserPics {

	Date dateCreated
	Date lastUpdated
	ChatUser chatuser
	byte[] photo
	static mapping = {
		photo type: "binary" // or "blob"?
	}
	static constraints = {
		photo(maxSize: 2048000, blank:false, minsize: 1) // 250kb
	}


}
