package grails.plugin.wschat


class ChatUser {

	public static final String CHAT_ADMIN				= 'admin'
	public static final String CHAT_USER				= 'user'
	public static final String CHAT_LIVE_USER			= 'liveChat'
	public static final String CHAT_LIVE_USER_ADMIN		= 'monitorLiveChat'
	public static final String DEFAULT_PERMISSION		=  CHAT_USER

	public static final String SPRINGSECURITY_USER 		= 'ROLE_USER'
	public static final String SPRINGSECURITY_ADMIN		= 'ROLE_ADMIN'

	public static final PERMISSIONS=[CHAT_ADMIN,CHAT_USER,CHAT_LIVE_USER,CHAT_LIVE_USER_ADMIN]

	Date dateCreated
	Date lastUpdated
	String username
	ChatLog log
	ChatLog offlog
	ChatPermissions permissions
	static belongsTo = [profile:ChatUserProfile]
	static hasMany = [photos: ChatUserPics, friends: ChatFriendList, blocked:ChatBlockList ]

	static mapping = {
		permissions (lazy: false, inList:PERMISSIONS)

		//friends cascade: 'lock'
	}

	static constraints = {
		username blank: false, unique: true
		friends nullable:true
		blocked nullable:true
		photos nullable:true
		profile nullable:true
		log nullable: true
		offlog nullable: true
	}

	String getPermissionName() {
		return permissions?.name
	}

}
