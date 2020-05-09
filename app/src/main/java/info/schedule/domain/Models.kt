package info.schedule.domain

// Account
data class Account(val name : String,
                    val surname: String,
                    val patronymic: String,
                    val username: String)


// User
data class User(val name : String,
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

data class UserRole(val name : String,
                    val surname: String,
                    val patronymic: String,
                    val username: String,
                    val role: String) {

    override fun toString(): String {
        val users: String?
        users = if(surname.isNotEmpty() &&
            name.isNotEmpty() &&
            patronymic.isNotEmpty())
            "$surname ${name.substring(0,1)}.${patronymic.substring(0,1)}. [${role}]"
        else
            username
        return users
    }
}

// Group
data class Group(val name: String) {

    override fun toString(): String {
        return name
    }
}

// University
data class University(val universityName: String) {

    override fun toString(): String {
        return universityName
    }
}

// Faculty
data class Faculty(val facultyName: String) {

    override fun toString(): String {
        return facultyName
    }
}


data class Schedule(val subjectName: String,
                    val typeLecture: String,
                    val lectureRoom: String,
                    val groupName: String,
                    val nameUser: String,
                    val surnameUser: String,
                    val patronymicUser: String,
                    val universityName: String,
                    val date: String,
                    val startLecture: String,
                    val finishLecture: String) {

}