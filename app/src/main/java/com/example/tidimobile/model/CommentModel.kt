package com.example.tidimobile.model

data class CommentModel(
    val _id: String? = null,
    val idBlog: String? = null,
    val idUser: UserCmtModel? = null,
    val content: String? = null,
    val favorites: Int? = null,
    val createdAt: String? = null
) {
    data class UserCmtModel(
        val _id: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val avatar: String? =null
    )
}

data class CommentNewModel(
    val content: String? = null
)