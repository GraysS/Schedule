package info.schedule.domain


data class Account(val name : String,
                    val surname: String,
                    val patronymic: String,
                    val username: String) {


    override fun toString(): String {
        val accounts: String?
        accounts =
            if(surname.isNotEmpty() &&
            name.isNotEmpty() &&
            patronymic.isNotEmpty())
            "$surname ${name.substring(0,1)}.${patronymic.substring(0,1)}."
        else
            username
        return accounts
    }
}

data class Group(val name: String) {

    override fun toString(): String {
        return name
    }
}

data class University(val universityName: String) {

    override fun toString(): String {
        return universityName
    }
}