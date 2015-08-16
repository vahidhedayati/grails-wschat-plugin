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
 * 
 *     ChatAI.findOrSaveWhere(input:'contact number', output: '0800 123456' )
 *     ChatAI.findOrSaveWhere(input:'opening hours', output: '9 - 5' )
 * 
 *   This is an example of the bot working away with example above:
 *    
 *   Guest8F550FFA6B4A1D05682A2B313D17F63E: what is your contact number 
 *   Guest8F550FFA6B4A1D05682A2B313D17F63E_assistant: 0800 123456
 *   
 *   Guest8F550FFA6B4A1D05682A2B313D17F63E: what is your opening hours fool 
 *   Guest8F550FFA6B4A1D05682A2B313D17F63E_assistant: 9 - 5 
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

