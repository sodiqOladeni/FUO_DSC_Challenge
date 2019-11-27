package com.eemanapp.fuoexaet.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eemanapp.fuoexaet.data.SharedPref
import com.eemanapp.fuoexaet.data.local.RequestDao
import com.eemanapp.fuoexaet.model.Request
import com.eemanapp.fuoexaet.model.UiData
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.Methods
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import javax.inject.Inject

class HomeDashboardViewModel @Inject constructor(
    private val requestDao: RequestDao, private val pref: SharedPref
) : ViewModel(), EventListener<QuerySnapshot> {

    private val db = FirebaseFirestore.getInstance()
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user
    val requests = requestDao.getRequests()
    private val newUiData = UiData()
    private var mRequestQuery: Query? = null
    private val snapshots = ArrayList<DocumentSnapshot>()
    var registration: ListenerRegistration? = null

    init {
        getUser()
    }

    private fun getUser() {
        val user = getUserFromPref()
        _user.value = user
        composeQuery(user)
    }

    private fun getUserFromPref(): User {
        return pref.getUser()
    }

    fun updateRequest(request: Request):MutableLiveData<UiData> {
        val uiData = MutableLiveData<UiData>()
        val ref = db.collection(Constants.ALL_REQUESTS).document(request.requestUniqueId)
        ref.update("requestStatus", request.requestStatus,
            "declineOrApproveTime", request.declineOrApproveTime,
            "approveCoordinator", request.approveCoordinator,
            "gateDepartureTime", request.gateDepartureTime,
            "gateArrivalTime", request.gateArrivalTime)

            .addOnSuccessListener {
                newUiData.status = true
                newUiData.message = "Request successfully ${request.requestStatus}"
                uiData.value = newUiData
            }

            .addOnFailureListener {
                newUiData.status = false
                newUiData.message = "Error updating request"
                uiData.value = newUiData
            }
        return uiData
    }

    private fun startListening() {
        if (mRequestQuery != null && registration == null) {
            registration = mRequestQuery?.addSnapshotListener(this)
        }
    }

    private fun setQuery(query: Query) {
        // Stop listening
        stopListening()
        // Clear existing data
        snapshots.clear()
        // Listen to new query
        this.mRequestQuery = query
        startListening()
    }

    private fun stopListening() {
        registration?.remove()
        registration = null
        snapshots.clear()
    }

    override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            return
        }

        for (change in querySnapshot?.documentChanges!!) {
            val snapshot = change.document
            when (change.type) {
                DocumentChange.Type.ADDED -> {
                    uiScope.launch {
                        saveRequestInDb(snapshot.toObject(Request::class.java))
                    }
                }
                DocumentChange.Type.MODIFIED -> {
                    uiScope.launch {
                        updateRequestChangesInDb(snapshot.toObject(Request::class.java))
                    }
                }
                DocumentChange.Type.REMOVED -> {
                    uiScope.launch {
                        deleteRequestChangesInDb(snapshot.toObject(Request::class.java))
                    }
                }
            }
        }
    }

    private suspend fun saveRequestInDb(request: Request): Long {
        return withContext(Dispatchers.IO) {
            requestDao.setRequest(request)
        }
    }

    private suspend fun updateRequestChangesInDb(request: Request) {
        return withContext(Dispatchers.IO) {
            requestDao.updateRequest(request)
        }
    }

    private suspend fun deleteRequestChangesInDb(request: Request) {
        return withContext(Dispatchers.IO) {
            requestDao.deleteRequest(request)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        stopListening()
    }

    private fun composeQuery(user: User) {
        when (Methods.userWhoCodeToName(user.userWho)) {
            //STUDENT
            Constants.STUDENT -> {
                val q = db.collection(Constants.ALL_REQUESTS)
                    .whereEqualTo("user.uniqueId", user.uniqueId)
                setQuery(q)
            }
            //SECURITY
            Constants.SECURITY -> {
                val q = db.collection(Constants.ALL_REQUESTS)
                setQuery(q)
            }
            //COORDINATOR
            Constants.COORDINATOR -> {
                val q = db.collection(Constants.ALL_REQUESTS)
                setQuery(q)
            }
        }
    }
}
