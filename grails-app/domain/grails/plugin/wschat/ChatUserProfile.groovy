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
		firstName nullable: true,size: 0..50
        middleName nullable: true,size: 0..50
        lastName nullable: true,size: 0..50
        email nullable: true,email: true, notEqual: "bill@microsoft.com"
        age nullable: true, range: 0..150
        gender nullable: true,inList: ["Male", "Female"]
		martialStatus nullable: true,inList: ["Single", "Married"]
        birthDate nullable: true,max: new Date()
        wage nullable: true, min: 0F, scale: 2
		children nullable: true,min:0, max:10
        homePage nullable: true,url: true
    }

	
}
