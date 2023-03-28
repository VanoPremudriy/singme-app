package com.example.singmeapp.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentChangeUserDataBinding
import com.example.singmeapp.viewmodels.ChangeUserDataViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ChangeUserDataFragment : Fragment() {

    lateinit var binding: FragmentChangeUserDataBinding
    lateinit var changeUserDataViewModel: ChangeUserDataViewModel
    lateinit var fragmentActivity: AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        fragmentActivity = activity as AppCompatActivity
        val provider = ViewModelProvider(this)
        changeUserDataViewModel = provider[ChangeUserDataViewModel::class.java]
        changeUserDataViewModel.getData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.title = getString(R.string.change_user_data)
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = FragmentChangeUserDataBinding.inflate(layoutInflater)
        changeUserDataViewModel.currentUser.observe(viewLifecycleOwner){

            binding.etRealNameInChangeData.setText(it.realName)
            binding.etLastNameInChangeData.setText(it.lastName)
            if (it.sex == "male") binding.radioChooseSex.check(R.id.rbMale)
            else binding.radioChooseSex.check(R.id.rbFemale)
        }

        changeUserDataViewModel.mutDate.observe(viewLifecycleOwner){
            binding.datePickerInChangeData.init(it.year, it.month.value, it.dayOfMonth, null)
        }

        changeUserDataViewModel.realNameValid.observe(viewLifecycleOwner){
            binding.tvRealNameValid.text = it.toString()
        }

        changeUserDataViewModel.lastNameValid.observe(viewLifecycleOwner){
            binding.tvLastNameValid.text = it.toString()
        }

        binding.bSaveChangedData.setOnClickListener{
            changeUserDataViewModel.clearValid()
            saveData()
            findNavController().navigate(R.id.profileFragment)
        }

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            back()
        }
        return true
    }
    fun back(){
        val count: Int? = activity?.supportFragmentManager?.backStackEntryCount

        if (count == 0) {
            fragmentActivity.supportActionBar?.show()
            activity?.onBackPressed()

        } else {
            fragmentActivity.supportActionBar?.show()
            findNavController().popBackStack()
        }
    }
    companion object {

        @JvmStatic
        fun newInstance() = ChangeUserDataFragment()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveData(){
        if (textValidation()) {
            val year = binding.datePickerInChangeData.year
            val month = binding.datePickerInChangeData.month
            val day = binding.datePickerInChangeData.dayOfMonth
            val uLocalDateTime =
                LocalDate.parse("$day/$month/$year", DateTimeFormatter.ofPattern("d/M/yyyy"))
                    .toString()

            changeUserDataViewModel.setData(
                binding.etRealNameInChangeData.text.toString(),
                binding.etLastNameInChangeData.text.toString(),
                uLocalDateTime,
                binding.radioChooseSex.checkedRadioButtonId,

                )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun textValidation():Boolean{
        val uRealName = binding.etRealNameInChangeData.text.toString()
        val uLastName = binding.etLastNameInChangeData.text.toString()

        changeUserDataViewModel.setValidValues(arrayListOf(uRealName, uLastName))

        val result =  changeUserDataViewModel.valid()
        return result

    }
}