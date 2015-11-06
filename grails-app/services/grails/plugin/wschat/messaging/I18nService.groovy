package grails.plugin.wschat.messaging

import org.springframework.context.MessageSource
import org.springframework.web.servlet.i18n.SessionLocaleResolver

class I18nService {

	boolean transactional = false

	SessionLocaleResolver localeResolver
	MessageSource messageSource

	/**
	 *
	 * @param msgKey
	 * @param defaultMessage default message to use if none is defined in the message source
	 * @param objs objects for use in the message
	 * @return
	 */
	def msg(String msgKey, String defaultMessage = null, List objs = null) {
		def msg = messageSource.getMessage(msgKey,objs?.toArray(),defaultMessage,localeResolver.defaultLocale)
		///def msg = messageSource.getMessage(msgKey, objs?.toArray() ?: [], defaultMessage, null)
		if (msg == null || msg == defaultMessage) {
			log.warn("No i18n messages specified for msgKey: ${msgKey} ${defaultMessage}")
			msg = defaultMessage
		}

		return msg
	}

	/**
	 * Method to look like g.message
	 * @param args
	 * @return
	 */
	def message(Map args) {
		return msg(args.code, args.default, args.attrs)
	}
}