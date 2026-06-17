package me.park.user.adapter.`in`.web.request

data class ChangePointRequest(
    val amount: Long,
    val reason: String,
    val referenceType: String? = null,
    val referenceId: String? = null,
)
