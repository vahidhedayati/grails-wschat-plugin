package grails.plugin.wschat

class ChatUserProfile {
	
	Date dateCreated
	Date lastUpdated
	ChatUser chatuser
	String firstName
    String middleName
    String lastName
    Integer age
	Date birthDate
	String gender
	Float wage
	String email
	String homePage
	String martialStatus
	Integer children
	
	static constraints = {
		firstName size: 1..50
        middleName nullable: true,size: 0..50
        lastName size: 1..50
        email nullable: true,email: true, notEqual: "bill@microsoft.com"
        age nullable: true, range: 0..150
        gender inList: ["Male", "Female"]
		martialStatus inList: ["Single", "Married"]
        birthDate max: new Date()
        wage nullable: true, min: 0F, scale: 2
		children nullable: true,min:0, max:10
        homePage nullable: true,url: true
    }

	
}
