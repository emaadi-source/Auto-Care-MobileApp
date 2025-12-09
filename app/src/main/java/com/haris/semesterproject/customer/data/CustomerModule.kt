package com.haris.semesterproject.customer.data

class CustomerModule {
    data class SimpleResponse1(
        val success: Boolean,
        val message: String
    )


    data class CallResponse(
        val success: Boolean,
        val message: String,
        val call: CallData?
    )

    data class CallData(
        val id: Int,
        val caller_id: Int,
        val receiver_id: Int,
        val status: String
    )

    data class CallStatusResponse(
        val success: Boolean,
        val message: String,
        val call: CallData?
    )


    data class ApiResponse(
        val success: Boolean,
        val message: String
    )

    data class GetMessagesResponse(
        val success: Boolean,
        val message: String,
        val messages: List<ChatMessage>
    )
}