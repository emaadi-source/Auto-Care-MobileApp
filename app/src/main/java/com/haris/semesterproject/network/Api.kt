package com.haris.semesterproject.network

import com.haris.semesterproject.authentication.data.LoginResponse
import com.haris.semesterproject.customer.data.BookingResponse
import com.haris.semesterproject.customer.data.BookingResponseNew
import com.haris.semesterproject.customer.data.ChatMessage
import com.haris.semesterproject.customer.data.Service
import com.haris.semesterproject.customer.data.ServiceWhole
import com.haris.semesterproject.customer.data.Workshop
import com.haris.semesterproject.provider.data.BookingDetails
import com.haris.semesterproject.provider.data.DashboardStatsResponse
import com.haris.semesterproject.provider.data.ProfileResponse
import com.haris.semesterproject.provider.data.ServiceListResponse
import com.haris.semesterproject.provider.data.SimpleResponse
import retrofit2.Call
import retrofit2.http.*

interface Api {

    @FormUrlEncoded
    @POST("signup.php")
    fun signup(
        @Field("full_name") fullName: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phone") phone: String,
        @Field("role") role: String // "customer" or "provider"
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    // --- PROVIDER MODULE ---
    @FormUrlEncoded
    @POST("provider/add_service.php")
    fun addServiceItem(
        @Field("provider_id") providerId: Int,
        @Field("name") name: String,
        @Field("category") category: String,
        @Field("price") price: Double,
        @Field("type") type: String, // "SERVICE" or "PART"
        @Field("brand") brand: String?,
        @Field("stock") stock: Int?,
        @Field("duration") duration: String?
    ): Call<SimpleResponse>

    @FormUrlEncoded
    @POST("provider/get_incoming_bookings.php")
    fun getIncomingBookings(
        @Field("provider_id") providerId: Int,
        @Field("status") status: String // "pending" or "confirmed"
    ): Call<List<BookingDetails>>

    @FormUrlEncoded
    @POST("provider/update_booking_status.php")
    fun updateBookingStatus(
        @Field("booking_id") bookingId: Int,
        @Field("status") status: String
    ): Call<SimpleResponse>

    @FormUrlEncoded
    @POST("provider/get_provider_bookings.php")
    fun getProviderBookings(
        @Field("provider_id") providerId: Int
    ): Call<List<com.haris.semesterproject.provider.data.Booking>>

    @FormUrlEncoded
    @POST("provider/get_earnings_history.php")
    fun getEarningsHistory(
        @Field("provider_id") providerId: Int
    ): Call<com.haris.semesterproject.provider.data.EarningsResponse>

    @FormUrlEncoded
    @POST("provider/get_services.php")
    fun getProviderServices(
        @Field("provider_id") providerId: Int
    ): Call<ServiceListResponse>

    @FormUrlEncoded
    @POST("provider/get_dashboard_stats.php")
    fun getDashboardStats(
        @Field("provider_id") providerId: Int
    ): Call<DashboardStatsResponse>

    @FormUrlEncoded
    @POST("provider/get_profile.php")
    fun getWorkshopProfile(
        @Field("provider_id") providerId: Int
    ): Call<ProfileResponse>

    @FormUrlEncoded
    @POST("provider/update_profile.php")
    fun updateWorkshopProfile(
        @Field("provider_id") providerId: Int,
        @Field("workshop_name") name: String,
        @Field("owner_name") owner: String,
        @Field("description") desc: String,
        @Field("address") address: String,
        @Field("city") city: String,
        @Field("pincode") pin: String,
        @Field("contact_email") email: String,
        @Field("contact_phone") phone: String,
        @Field("image_1") image1: String?,
        @Field("image_2") image2: String?
    ): Call<SimpleResponse>

    @GET("get_workshops.php")
    fun getAllWorkshops(): Call<List<Workshop>>



    @FormUrlEncoded
    @POST("getProviderServices.php")
    fun getServices(
        @Field("provider_id") id: Int
    ): Call<List<Service>>

    @FormUrlEncoded
    @POST("get_services.php") // Points to the customer-facing PHP script
    fun getServicesForCustomer(
        @Field("provider_id") providerId: Int
    ): Call<List<ServiceWhole>>

    // --- CHAT MODULE ---
    @FormUrlEncoded
    @POST("send_message.php")
    fun sendMessage(
        @Field("sender_id") sender_id: String,
        @Field("receiver_id") receiver_id: String,
        @Field("message_content") message_content: String,
        @Field("image_path") image_path: String? = null
    ): Call<ApiResponse>

    @GET("get_messages.php")
    fun getMessages(
        @Query("sender_id") sender_id: Int,
        @Query("receiver_id") receiver_id: Int
    ): Call<List<ChatMessage>>


    @FormUrlEncoded
    @POST("delete_message.php")
    fun deleteMessage(
        @Field("message_id") message_id: String
    ): Call<ApiResponse>

    // ------------------ CREATE CALL ------------------
    @FormUrlEncoded
    @POST("calls.php")
    fun createCall(
        @Field("action") action: String = "create",
        @Field("caller_id") callerId: String,
        @Field("receiver_id") receiverId: String
    ): Call<CallResponse>

    // ------------------ UPDATE CALL STATUS ------------------
    @FormUrlEncoded
    @POST("calls.php")
    fun updateCallStatus(
        @Field("action") action: String = "update",
        @Field("caller_id") callId: String,
        @Field("status") status: String
    ): Call<CallResponse>

    // ------------------ GET CALL STATUS ------------------
    @GET("calls.php")
    fun getCallStatus(
        @Query("action") action: String = "status",
        @Query("caller_id") callId: Int
    ): Call<CallStatusResponse>

    // ------------------ GET INCOMING CALL ------------------
// MUST BE GET (your PHP uses $_GET)
    @GET("calls.php")
    fun getIncomingCall(
        @Query("action") action: String = "check_incoming",
        @Query("receiver_id") receiverId: String
    ): Call<CallResponse>

    @FormUrlEncoded
    @POST("create_bookings.php") // Ensure plural 's' matches filename
    fun createBooking(
        @Field("customer_id") customerId: Int,
        @Field("provider_id") providerId: Int,
        @Field("date") date: String,
        @Field("total_amount") totalAmount: Double,
        @Field("services") servicesJson: String,
        @Field("vehicle_model") vehicleModel: String,
        @Field("vehicle_number") vehicleNumber: String
    ): Call<BookingResponse>
    // Note: Use BookingResponse if you created the CustomerModels.kt file,
    // otherwise change back to SimpleResponse1 if you kept it inside Api.kt

    @GET("get_bookings.php")
    fun getCustomerBookings(
        @Query("customer_id") customerId: Int
    ): Call<BookingResponseNew>

    @FormUrlEncoded
    @POST("update_user.php")
    fun updateUserProfile(
        @Field("user_id") userId: Int,
        @Field("full_name") fullName: String,
        @Field("email") email: String,
        @Field("password") password: String? = null // optional
    ): Call<UpdateProfileResponse>

    data class UpdateProfileResponse(
        val success: Boolean,
        val message: String
    )


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
        val message: String,
        val message_id: String? = null,
        val timestamp: Long? = null,
        val image_path: String? = null,
        val vanish_mode: Boolean? = null
    )

    data class GetMessagesResponse(
        val success: Boolean,
        val message: String,
        val messages: List<ChatMessage>
    )

}
