package com.eemanapp.fuoexaet.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eemanapp.fuoexaet.data.local.RequestDao
import com.eemanapp.fuoexaet.data.local.UserDao
import com.eemanapp.fuoexaet.model.Request
import com.eemanapp.fuoexaet.model.UiData
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Constants
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import javax.inject.Inject

class HomeDashboardViewModel @Inject constructor(
    private val userDao: UserDao,
    private val requestDao: RequestDao
) : ViewModel(), EventListener<QuerySnapshot> {

    private val db = FirebaseFirestore.getInstance()
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user
    private val _uiData = MutableLiveData<UiData>()
    val uiData: LiveData<UiData>
        get() = _uiData
    val requests = requestDao.getRequests()
    private val newUiData = UiData()
    private var mRequestQuery: Query? = null
    private val snapshots = ArrayList<DocumentSnapshot>()
    var registration: ListenerRegistration? = null

    init {
        getUser()
        startListening()
        mRequestQuery =
            db.collection(Constants.ALL_REQUESTS).orderBy("requestTime", Query.Direction.ASCENDING)
        setQuery(mRequestQuery!!)
    }

    private fun getUser() {
        uiScope.launch {
            _user.value = getUserFromDb()
        }
    }

    private suspend fun getUserFromDb(): User {
        return withContext(Dispatchers.IO) {
            userDao.getUser()
        }
    }

    fun updateRequest(request: Request) {
        val ref = db.collection(Constants.ALL_REQUESTS).document(request.requestUniqueId!!)
        ref.update(
            "requestStatus", request.requestStatus,
            "approveCoordinator", request.approveCoordinator
        )
            .addOnSuccessListener {
                newUiData.status = true
                newUiData.message = "Request successfully ${request.requestStatus}"
                _uiData.value = newUiData
            }
            .addOnFailureListener {
                newUiData.status = false
                newUiData.message = "Error updating request"
                _uiData.value = newUiData
            }
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
                    Log.v(
                        "HomeDashboardViewModel",
                        "Data Fetched =>" + snapshot.toObject(Request::class.java).toString()
                    )
                }
                DocumentChange.Type.MODIFIED -> {
                    uiScope.launch {
                        updateRequestChangesInDb(snapshot.toObject(Request::class.java))
                    }
                    Log.v(
                        "HomeDashboardViewModel",
                        "Data Updated =>" + snapshot.toObject(Request::class.java).toString()
                    )
                }
                DocumentChange.Type.REMOVED -> {
                    Log.v(
                        "HomeDashboardViewModel",
                        "Data Removed =>" + snapshot.toObject(Request::class.java).toString()
                    )
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        stopListening()
    }
}
