package co.auth

data class User(
    var name: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    var image: String = "",
    val matricula: String = "",
    val tipoVehiculo: String = ""

)