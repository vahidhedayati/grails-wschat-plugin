package grails.plugin.wschat

import grails.gorm.DetachedCriteria
import groovy.transform.ToString

import org.apache.commons.lang.builder.HashCodeBuilder

@ToString(cache=true, includeNames=true, includePackage=false)
class ChatAuthChatRole implements Serializable {

	private static final long serialVersionUID = 1

	ChatAuth chatAuth
	ChatRole chatRole

	ChatAuthChatRole(ChatAuth u, ChatRole r) {
		this()
		chatAuth = u
		chatRole = r
	}

	@Override
	boolean equals(other) {
		if (!(other instanceof ChatAuthChatRole)) {
			return false
		}

		other.chatAuth?.id == chatAuth?.id && other.chatRole?.id == chatRole?.id
	}

	@Override
	int hashCode() {
		def builder = new HashCodeBuilder()
		if (chatAuth) builder.append(chatAuth.id)
		if (chatRole) builder.append(chatRole.id)
		builder.toHashCode()
	}

	static ChatAuthChatRole get(long chatAuthId, long chatRoleId) {
		criteriaFor(chatAuthId, chatRoleId).get()
	}

	static boolean exists(long chatAuthId, long chatRoleId) {
		criteriaFor(chatAuthId, chatRoleId).count()
	}

	private static DetachedCriteria criteriaFor(long chatAuthId, long chatRoleId) {
		ChatAuthChatRole.where {
			chatAuth == ChatAuth.load(chatAuthId) &&
			chatRole == ChatRole.load(chatRoleId)
		}
	}

	static ChatAuthChatRole create(ChatAuth chatAuth, ChatRole chatRole, boolean flush = false) {
		def instance = new ChatAuthChatRole(chatAuth, chatRole)
		instance.save(flush: flush, insert: true)
		instance
	}

	static boolean remove(ChatAuth u, ChatRole r, boolean flush = false) {
		if (u == null || r == null) return false

		int rowCount = ChatAuthChatRole.where { chatAuth == u && chatRole == r }.deleteAll()

		if (flush) { ChatAuthChatRole.withSession { it.flush() } }

		rowCount
	}

	static void removeAll(ChatAuth u, boolean flush = false) {
		if (u == null) return

		ChatAuthChatRole.where { chatAuth == u }.deleteAll()

		if (flush) { ChatAuthChatRole.withSession { it.flush() } }
	}

	static void removeAll(ChatRole r, boolean flush = false) {
		if (r == null) return

		ChatAuthChatRole.where { chatRole == r }.deleteAll()

		if (flush) { ChatAuthChatRole.withSession { it.flush() } }
	}

	static constraints = {
		chatRole validator: { ChatRole r, ChatAuthChatRole ur ->
			if (ur.chatAuth == null || ur.chatAuth.id == null) return
			boolean existing = false
			ChatAuthChatRole.withNewSession {
				existing = ChatAuthChatRole.exists(ur.chatAuth.id, r.id)
			}
			if (existing) {
				return 'userRole.exists'
			}
		}
	}

	static mapping = {
		id composite: ['chatAuth', 'chatRole']
		version false
	}
}
