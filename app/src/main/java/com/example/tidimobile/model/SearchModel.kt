package com.example.tidimobile.model

data class SearchKeyModel(
    val key: String? = null
)

data class UserSearchModel(
    val users: ArrayList<UserSearchDetail>? = null
) {
    data class UserSearchDetail(
        val firstName: String? = null,
        val lastName: String? = null,
        val _id: String? = null,
        val avatar: String? = null
    )
}