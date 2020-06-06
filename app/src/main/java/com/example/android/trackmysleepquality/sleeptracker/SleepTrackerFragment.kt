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

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)
        //get a reference to the application context.
        val application = requireNotNull(this.activity).application
        //get a reference to the DAO of the database
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        //Create an instance of the viewModelFactory and pass in dataSource as well as application.
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)
        //Get a reference to the SleepTrackerViewModel To the ViewModelProvider,
        // specify to use the viewModelFactory and get an instance of SleepTrackerViewModel::class.java
        val viewModel = ViewModelProvider(this, viewModelFactory)
                .get(SleepTrackerViewModel::class.java)

        val adapter:SleepNightAdapter= SleepNightAdapter()
        binding.sleepList.adapter=adapter
        // the following observer to scroll the list to first position
        // when an item added to the list
        adapter.registerAdapterDataObserver(object :RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                Log.i("TAG", itemCount.toString())
                binding.sleepList.scrollToPosition(0)
            }
        })

        viewModel.nights.observe(viewLifecycleOwner, Observer {
            it?.let {
              adapter.submitList(it)
                binding.sleepList.scrollToPosition(0)
            }
        })


        /*then create the data TAG in the "fragment_sleep_tracker.xml"
         *and create  the "sleepTrackerViewModel" variable
         */


        //Assign the sleepTrackerViewModel binding variable to the sleepTrackerViewModel

        binding.sleepTrackerViewModel = viewModel
        binding.setLifecycleOwner(this)

        viewModel.navigationToSleepQuality.observe(viewLifecycleOwner, Observer {night ->
            night?.let {
                this.findNavController()
                        .navigate(
                        SleepTrackerFragmentDirections
                                .actionSleepTrackerFragmentToSleepQualityFragment(night.nightId)
                        )
                viewModel.doneNavigating()
            }
        })
        return binding.root
    }
}
