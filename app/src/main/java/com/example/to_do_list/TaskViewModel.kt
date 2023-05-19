package com.example.to_do_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskViewModel: ViewModel() {
    var desc = MutableLiveData<String>()
}