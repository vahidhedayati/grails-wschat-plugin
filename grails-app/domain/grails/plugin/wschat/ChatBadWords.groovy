package grails.plugin.wschat

/*
 * @author Vahid Hedayati 8th August 2015
 * Chat Artificial Intelligence Class
 * 
 * This works in Conjunction with LIVE CUSTOMER CHAT feature
 *  
 * When a user clicks live chat and has verified or is verified 
 * If they type in a sentence word matching below input the output provided will be sent back as a response
 * This is for you to populate with your company useful information
 * Customer may ask whats your contact number for which bot will repond with a response if that phrase is found in this table
 * The process to look up input output would suite something like (if you registered below in your bootstrap):
 * ChatBadWords.findOrSaveWhere(input:'poo', output: '/kickuser' )
 * //minutes months hours years days
 * ChatBadWords.findOrSaveWhere(input:'pants', output: '/banuser', duration: 1  ,period: 'minutes')
 * ChatBadWords.findOrSaveWhere(input:'bastard', output: '/banuser', duration: 1  ,period: 'months')
 *  
 */
class ChatBadWords {

	Date dateCreated
	Date lastUpdated
	Integer duration
	String period
	String input
	String output
	
	static constraints = {
		input blank: false, unique: true
		duration(nullable:true)
		period(nullable:true)
	}
	
	static mapping = {
		input type: 'text'
		output type: 'text'
	}
	
	String toString() {
		output
	}	
	
}

