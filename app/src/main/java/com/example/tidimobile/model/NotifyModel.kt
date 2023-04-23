package com.example.tidimobile.model

class NotifyModel(
    val notification: ArrayList<SubNotifyModel>? =null
){
    class SubNotifyModel(
        val _id: String ?= null,
        val idUser: String ?= null,
        val idUserTarget: String ?= null,
        val idTarget: String ?= null,
        val typeNotify: String? =null,
        val content: String ?= null,
        val createdAt: String ?= null
    )
}

class NotifyStoreModel(
    val _id: String ?= null,
    val idUser: String ?= null,
    val content: String ?= null,
    val typeNotify: String? =null,
    val idUserTarget: String ?= null,
    val idTarget: String ?= null,
)