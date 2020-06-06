/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
//this class has to extend the @AndroidViewModel Class which takes an Application "Context" as a parameter
//since we need to Access the Database then we need an Instance from SleepDatabaseDao in the constructor
//since we need to access resources such as Strings and styles so the constructor we pass an instance form the Application class
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {
    //  1- create the Job
    var viewModelJob = Job()
    //3- Define a uiScope for the coroutines
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //4- Define a variable, tonight, to hold the current night, and make it MutableLiveData
    private val tonight = MutableLiveData<SleepNight?>()

    //5- Define a variable, nights. Then getAllNights() from the database and assign to the nights variable
    private val nights = database.getAllNights()

    private val _navigationToSleepQuality=MutableLiveData<SleepNight?>()
    val navigationToSleepQuality:LiveData<SleepNight?> get() = _navigationToSleepQuality



    var nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }

    var startButtonVisible=Transformations.map(tonight){
        null==it
    }
    var stopButtonVisible=Transformations.map(tonight){
        null!=it
    }
    var clearButtonVisible=Transformations.map(nights){
        it?.isNotEmpty()
    }

    //6- To initialize the tonight variable, create an init block and call initializeTonight()
    init {
        initializeTonight()
    }

    /* 7- Implement initializeTonight(). In the uiScope, launch a coroutine.
     Inside, get the value for tonight from the database by calling getTonightFromDatabase(),
     which you will define in the next step, and assign it to tonight.value:
     */
    private fun initializeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    /* 8- Implement getTonightFromDatabase(). Define
    is as a private suspend function that returns a nullable SleepNight,
    if there is no current started sleepNight

     */

    private suspend fun getTonightFromDatabase(): SleepNight? {
        //9- inside the function body, return the result
        // from a coroutine that runs in the Dispatchers.IO context:
        return withContext(Dispatchers.IO) {
            var night: SleepNight? = database.getTonight()
            if (night?.startTimeMilli != night?.endTimeMilli) {
                night = null
            }
            night
        }
    }

    //10-handle the start Button Click
    fun onStartTracking() {
        // 11- run UI Scope
        uiScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            //13- assign tonight to the latest night in the database
            tonight.value = getTonightFromDatabase()
        }
    }

    //12- implement the insert function on IO scope and insert the current night
    private suspend fun insert(newNight: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(newNight)
        }
    }

    // 13- handle Stop Button
    fun onStopTracking() {
        uiScope.launch {
            //14- get the tonight and update it
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
            _navigationToSleepQuality.value=oldNight
        }
    }

    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }

    //15- handle the clear button
    fun onClear() {
        uiScope.launch {
            clear()
            tonight.value = null
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // 2- clear the Job in onCleared
        viewModelJob.cancel()

    }

    fun doneNavigating(){
        _navigationToSleepQuality.value=null
    }

}

