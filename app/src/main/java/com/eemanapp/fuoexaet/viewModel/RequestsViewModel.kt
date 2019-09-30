package com.eemanapp.fuoexaet.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.eemanapp.fuoexaet.data.SharedPref
import com.eemanapp.fuoexaet.data.local.RequestDao
import com.eemanapp.fuoexaet.model.Request
import com.eemanapp.fuoexaet.model.UiData
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class RequestsViewModel @Inject constructor(
    private val pref: SharedPref,
    private val requestDao: RequestDao
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    val requests = requestDao.getRequests()
    private val _uiData = MutableLiveData<UiData>()
    val uiData: LiveData<UiData>
        get() = _uiData
    private val newUiData = UiData()

    // _________________________________
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    init {
        getUser()
    }

    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }

    fun setIndex(index: Int) {
        _index.value = index
    }


    fun submitRequest(request: Request) {
        val ref = db.collection(Constants.ALL_REQUESTS).document()
        request.requestUniqueId = ref.id
        ref.set(request)
            .addOnSuccessListener {
                newUiData.status = true
                newUiData.message = "Request successfully submitted"
                _uiData.value = newUiData
            }
            .addOnFailureListener {
                newUiData.status = false
                newUiData.message = it.message
                _uiData.value = newUiData
            }
    }

    fun updateRequest(request: Request) {
        val ref = db.collection(Constants.ALL_REQUESTS).document(request.requestUniqueId)
        ref.update(
            "requestStatus", request.requestStatus,
            "declineOrApproveTime", request.declineOrApproveTime,
            "approveCoordinator", request.approveCoordinator,
            "gateDepartureTime", request.gateDepartureTime,
            "gateArrivalTime", request.gateArrivalTime
        )
            .addOnSuccessListener {
                newUiData.status = true
                newUiData.message = "Request successfully updated to ${request.requestStatus}"
                _uiData.value = newUiData
            }
            .addOnFailureListener {
                newUiData.status = false
                newUiData.message = "Error updating request"
                _uiData.value = newUiData
            }
    }


    fun getRequestForUser(userId: String): LiveData<List<Request>> {
        return requestDao.getRequestWithUserId(userId)
    }

    fun getRequestById(id: String): LiveData<Request> {
        return requestDao.getRequestWithId(id)
    }

    private fun getUser() {
        _user.value = getUserFromPref()
    }

    private fun getUserFromPref(): User {
        return pref.getUser()
    }
}
