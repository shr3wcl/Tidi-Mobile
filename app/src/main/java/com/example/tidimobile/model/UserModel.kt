package com.example.tidimobile.model


data class UserLoginModel(
    val username: String? = null,
    val password: String? = null
)

data class UserLoginResponseModel(
    val message: String? = null,
    val user: UserLoginObject? = null,
    val token: TokenModel? = null
) {
    data class UserLoginObject(
        val _id: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val username: String? = null,
        val email: String? = null,
        val gender: String? = null,
        val admin: Boolean? = null,
        val avatar: String? = null,
        val birthday: String? = null,
        val bio: String? = null,
        val createdAt: String? = null
    )

}

data class UserRegisterModel(
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null,
    val email: String? = null,
    val gender: String? = null,
    val password: String? = null,
)

data class ResponseMessage(
    val message: String? = null
)

data class UserChangedModel(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val gender: String? = null,
    val birthday: String? = null,
    val bio: String? = null,
)