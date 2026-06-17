package me.park.user.application.command

data class UsePointCommand(
    val userId: Long,
    val amount: Long,
    val reason: String,
    val referenceType: String? = null,
    val referenceId: String? = null,
)
