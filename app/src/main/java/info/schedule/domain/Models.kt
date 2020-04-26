package info.schedule.domain

data class Account(val name : String,
                    val surname: String,
                    val patronymic: String,
                    val username: String) {

    override fun toString(): String {
        return "$surname ${name.substring(0,1)}.${patronymic.substring(0,1)}."
    }
}