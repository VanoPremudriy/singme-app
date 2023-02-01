package com.example.singmeapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.adapters.ChooseMemberAdapter
import com.example.singmeapp.databinding.FragmentChooseMemberBinding
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.User
import com.example.singmeapp.viewmodels.ChooseMemberViewModel


class ChooseMemberFragment : Fragment() {

    lateinit var binding: FragmentChooseMemberBinding
    lateinit var chooseMemberViewModel: ChooseMemberViewModel
    lateinit var chooseMemberAdapter: ChooseMemberAdapter
    lateinit var band: Band
    var chosenUser: User? = null
    var rolesArray =  ArrayList<String>()
    lateinit var fragmentActivity: AppCompatActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        setHasOptionsMenu(true)
        val provider = ViewModelProvider(this)
        chooseMemberViewModel = provider[ChooseMemberViewModel::class.java]
        chooseMemberViewModel.getChooseMembers()
        band = arguments?.getSerializable("band") as Band
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragmentActivity.title = getString(R.string.choose_new_member)
        binding = FragmentChooseMemberBinding.inflate(layoutInflater)

        chooseMemberAdapter = ChooseMemberAdapter(this)
        binding.rcChooseMember.layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)

        chooseMemberViewModel.listChooseMembers.observe(viewLifecycleOwner){
            chooseMemberAdapter.userArrayList = it as ArrayList<User> /* = java.util.ArrayList<com.example.singmeapp.items.User> */
            binding.rcChooseMember.adapter = chooseMemberAdapter
        }

        binding.bAddChosenMemberRole.setOnClickListener {
            binding.llRoles.addView(EditText(context))
        }

        binding.bAddChosenMember.setOnClickListener {
            binding.llRoles.children.forEach { view ->
                run {
                    if (view::class.java == EditText::class.java) {
                        if ((view as EditText).text.toString() != "")
                            rolesArray.add((view as EditText).text.toString())
                    }
                }
            }
            val bundle = Bundle()
            bundle.putSerializable("band", band)
            chooseMemberViewModel.addNewMember(chosenUser!!.uuid, rolesArray, band)
            findNavController().navigate(R.id.bandFragment, bundle)
        }

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            val count: Int? = activity?.supportFragmentManager?.backStackEntryCount
            if (count == 0) {
                activity?.onBackPressed()
            } else {
                findNavController().popBackStack()
            }
        }
        return true
    }

    companion object {

        @JvmStatic
        fun newInstance() = ChooseMemberFragment()
    }
}